package com.challenge1.item;

import com.challenge1.item.controllers.ItemsController;
import com.challenge1.item.models.ItemModel;
import com.challenge1.item.services.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private ItemsController itemsController;
    @Autowired
    private ItemService itemService;

    @BeforeEach
    public void cleanUpEach(){
        itemService.deleteAll();
    }

    @Test
    public void getItemsEmptyTest() {
        CompletableFuture<ResponseEntity> result = itemsController.getItems();
        while(!result.isDone()) {}
        try {
            ResponseEntity resp = result.get();
            assertEquals(resp.getStatusCode(),HttpStatus.OK);
            List<ItemModel> itemModels = (List<ItemModel>) resp.getBody();
            assertEquals(itemModels.isEmpty(), true);
        }
        catch (Exception ex) {
            assertEquals(true, false);
        }
    }

    @Test
    public void getItemsTest() {
        List<ItemModel> itemModels = new ArrayList<>();
        ItemModel itemModel = new ItemModel();
        itemModel.setId("1");
        itemModel.setName("Test1");
        itemModel.setDescription("Description1");
        itemModel.setGroupId("1");
        itemModels.add(itemModel);
        itemModel = new ItemModel();
        itemModel.setId("1");
        itemModel.setName("Test2");
        itemModel.setDescription("Description2");
        itemModel.setGroupId("1");
        itemModels.add(itemModel);
        ResponseEntity resp = itemsController.createItems(itemModels);
        assertEquals(resp.getStatusCode(),HttpStatus.CREATED);
        itemModels = (List<ItemModel>) resp.getBody();
        assertEquals(itemModels.size() == 2, true);

        CompletableFuture<ResponseEntity> result = itemsController.getItems();
        while(!result.isDone()) {}
        try {
            resp = result.get();
            assertEquals(resp.getStatusCode(),HttpStatus.OK);
            List<ItemModel> itemModels2 = (List<ItemModel>) resp.getBody();
            assertEquals(itemModels2.size() == 2, true);
        }
        catch (Exception ex) {
            assertEquals(true, false);
        }
    }

    @Test
    public void getItemNotFoundTest() {
        ResponseEntity resp = itemsController.getItem("100",null,null);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    public void getItemTest() {
        ItemModel itemModel = new ItemModel();
        itemModel.setId("1");
        itemModel.setName("Test1");
        itemModel.setDescription("Description1");
        itemModel.setGroupId("1");
        ResponseEntity resp = itemsController.createItem(itemModel);
        assertEquals(resp.getStatusCode(),HttpStatus.CREATED);
        resp = itemsController.getItem(null,"Test1",null);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        itemModel = (ItemModel) resp.getBody();
        assertEquals("Test1",itemModel.getName());
    }

    @Test
    public void deleteItemNoKey() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity resp = itemsController.deleteItem(request, "1");
        assertEquals(resp.getStatusCode(),HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void deleteItemNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("apiKey", "1234");
        ResponseEntity resp = itemsController.deleteItem(request, "1");
        assertEquals(resp.getStatusCode(),HttpStatus.NOT_FOUND);
    }
}
