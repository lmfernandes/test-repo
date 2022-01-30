package com.challenge1.item.controllers;


import com.challenge1.item.configurations.AsycTaskConfiguration;
import com.challenge1.item.entities.Item;
import com.challenge1.item.mappers.ItemMapper;
import com.challenge1.item.models.ItemModel;
import com.challenge1.item.services.ItemService;
import com.challenge1.item.services.messaging.Producer;
import com.challenge1.item.validations.annotations.Identifier;
import com.challenge1.item.validations.annotations.Text;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
@RequestMapping(value = "v1/items")
public class ItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsycTaskConfiguration.class);

    //this is for testing purposes for all that is holy don't put this important information here.
    private static final String API_HEADER = "apiKey";
    private static final String API_KEY = "1234";

    @Autowired
    private ItemService itemService;
    @Autowired
    private Producer producer;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity> getItems() {
        return itemService.findAll().<ResponseEntity>thenApply(ResponseEntity::ok)
        .exceptionally(handleGetItemsFailure);
    }

    /*Since this a test api i will not mention that this api should never user the name and group fields since those are generic unlike,
     the id and only return one result, there should be separate apis for names and groups /items/group/{group}*/
    @RequestMapping(value = "/item", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getItem(@RequestParam(required = false) @Identifier String id,
                          @RequestParam(required = false) @Text String name,
                          @RequestParam(required = false) @Identifier String group) {
        if(Strings.isNullOrEmpty(id) && Strings.isNullOrEmpty(name) && Strings.isNullOrEmpty(group)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ItemModel item = getItemFromMultipleParams(id, name, group);
        if(Objects.isNull(item.getId())) {
            producer.sendMessage(String.format("Cannot retrieve item with params id=$s,name=$s,group=$s ", id, name, group));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        producer.sendMessage(String.format("Retrieved item with id=$s", id));
        return new ResponseEntity<>(item, HttpStatus.OK);
    }


    private ItemModel getItemFromMultipleParams(String id, String name, String group) {
        ItemModel item = new ItemModel();
        if(!Strings.isNullOrEmpty(id)) {
            item = itemService.getItemById(Long.parseLong(id));
        }
        if(Objects.isNull(item.getId()) && !Strings.isNullOrEmpty(name)) {
            item = itemService.getItemByName(name);
        }
        if(Objects.isNull(item.getId()) && !Strings.isNullOrEmpty(group)) {
            item = itemService.getItemByGroup(Long.parseLong(id));
        }
        return item;
    }

    @RequestMapping(value = "/item", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createItem(@RequestBody @Valid ItemModel item) {
        try {
            ItemModel created = itemService.save(item);
            producer.sendMessage(String.format("Added item id=$s with values %s,%s,%s", created.getId(),
                    created.getName(), created.getDescription(), created.getGroupId()), "items");
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception ex) {
            producer.sendMessage(String.format("Failed to add item with values %s,%s,%s", item.getName(),
                    item.getDescription(), item.getGroupId()), "items");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createItems(@RequestBody @Valid List<ItemModel> items) {
        try {
            List<ItemModel> itemModels =itemService.saveAll(ItemMapper.itemModelListToItemEntityList(items));
            producer.sendMessage(String.format("Added %s items ", items.size()), "items");
            return new ResponseEntity<>(itemModels, HttpStatus.CREATED);
        }
        catch(Exception ex) {
            producer.sendMessage(String.format("Failed to add %s items", items.size()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateItem(HttpServletRequest request,
                                     @PathVariable String id,
                                     @RequestBody ItemModel itemModel) {
        if(!validateHeaders(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long identifier = Long.parseLong(id);
        try {
            ItemModel retrievedItem = itemService.getItemById(identifier);
            boolean update = !Objects.isNull(retrievedItem.getId());
            if(update) {
                itemModel.setId(id);
                retrievedItem = itemService.update(itemModel);
            }
            else {
                retrievedItem = itemService.save(itemModel);
            }
            if(update) {
                producer.sendMessage(String.format("Updated item id=$s with values %s,%s,%s", retrievedItem.getId(),
                        retrievedItem.getName(), retrievedItem.getDescription(), retrievedItem.getGroupId()), "items");
                return new ResponseEntity<>(retrievedItem, HttpStatus.OK);
            }
            else {
                producer.sendMessage(String.format("Added item id=$s with values %s,%s,%s", retrievedItem.getId(),
                        retrievedItem.getName(), retrievedItem.getDescription(), retrievedItem.getGroupId()), "items");
                return new ResponseEntity<>(retrievedItem, HttpStatus.CREATED);
            }
        }
        catch(Exception ex) {
            producer.sendMessage(String.format("Failed to update item id=$s", identifier));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteItem(HttpServletRequest request,
                                     @PathVariable String id) {
        if(!validateHeaders(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long identifier = Long.parseLong(id);
        try {
            if(itemService.delete(identifier)) {
                producer.sendMessage(String.format("Deleted item id=$s", identifier));
                return new ResponseEntity<>(HttpStatus.OK);
            }
            producer.sendMessage(String.format("Item with id=$s does not exist", identifier));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(Exception ex) {
            producer.sendMessage(String.format("Failed to delete item id=$s", identifier));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static Function<Throwable, ResponseEntity<? extends List<Item>>> handleGetItemsFailure = throwable -> {
        LOGGER.error("Failed to read records: {}", throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };

    private boolean validateHeaders(HttpServletRequest request) {
        if(!Objects.isNull(request)) {
            String header = request.getHeader(API_HEADER);
            if(API_KEY.equals(header)) {
                return true;
            }
        }
        return false;
    }
}
