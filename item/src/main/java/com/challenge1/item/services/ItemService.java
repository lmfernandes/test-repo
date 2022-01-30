package com.challenge1.item.services;


import com.challenge1.item.configurations.AsycTaskConfiguration;
import com.challenge1.item.entities.Item;
import com.challenge1.item.mappers.ItemMapper;
import com.challenge1.item.models.ItemModel;
import com.challenge1.item.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsycTaskConfiguration.class);

    @Autowired
    ItemRepository itemRepository;

    @Cacheable(value="items", key="#id", unless="#result.id == null")
    public ItemModel getItemById(long id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.isPresent() ? ItemMapper.itemEntityToItemModel(item.get()) : new ItemModel();
    }

    public ItemModel getItemByName(String name) {
        Collection<Item> items = getItemsByName(name);
        return ItemMapper.itemEntityToItemModel(getFirst(items));
    }

    private Collection<Item> getItemsByName(String name) {
        Collection<Item> items = itemRepository.findByName(name);
        return items;
    }
    private Item getFirst(Collection<Item> items) {
        Item item = null;
        if(!CollectionUtils.isEmpty(items)) {
            item = items.stream().findFirst().orElse(null);
        }
        return item;
    }

    public ItemModel getItemByGroup(long group) {
        Collection<Item> items = getItemsByGroup(group);
        return ItemMapper.itemEntityToItemModel(getFirst(items));
    }

    private Collection<Item> getItemsByGroup(long group) {
        Collection<Item> items = itemRepository.findByGroup(group);
        return items;
    }

    public ItemModel save(ItemModel itemModel) {
        Item item =ItemMapper.itemModelToItemEntity(itemModel);
        item = itemRepository.save(item);
        return ItemMapper.itemEntityToItemModel(item);
    }

    @CachePut(value="items", key = "#itemModel.id")
    public ItemModel update(ItemModel itemModel) {
        long id = Long.parseLong(itemModel.getId());
        Item item =ItemMapper.itemModelToItemEntity(itemModel);
        item.setId(id);
        item = itemRepository.save(item);
        return ItemMapper.itemEntityToItemModel(item);
    }

    public List<ItemModel> saveAll(List<Item> itemsList) {
        Iterable<Item> items = itemRepository.saveAll(itemsList);
        itemsList = StreamSupport
                .stream(items.spliterator(), false)
                .collect(Collectors.toList());
        return ItemMapper.itemEntityListToItemModelList(itemsList);
    }

    /*This should also be cached but since we are using the default cache in this exercise we will not cache this one since the
      cache value would differ from the items cache, for this a cache implementation instead of the default would recommended*/
    @Async
    public CompletableFuture<List<ItemModel>> findAll() {
        Iterable<Item> items = itemRepository.findAll();
        List<Item> itemsList = StreamSupport
                .stream(items.spliterator(), false)
                .collect(Collectors.toList());
        List<ItemModel> itemModels = ItemMapper.itemEntityListToItemModelList(itemsList);
        return CompletableFuture.completedFuture(itemModels);
    }

    @CacheEvict(value="items", key = "#id")
    public boolean delete(long id) {
        boolean result = true;
        try {
            itemRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException ex) {
            result = false;
        }
        return result;
    }

    @CacheEvict(value = "items", allEntries = true)
    public void deleteAll() {
        itemRepository.deleteAll();
    }
}
