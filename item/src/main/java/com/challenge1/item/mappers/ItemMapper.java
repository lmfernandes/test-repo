package com.challenge1.item.mappers;

import com.challenge1.item.entities.Item;
import com.challenge1.item.models.ItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemMapper {

    public static ItemModel itemEntityToItemModel(Item item) {
        ItemModel itemModel = new ItemModel();
        if(!Objects.isNull(item)) {
            itemModel.setId(Long.toString(item.getId()));
            itemModel.setGroupId(Long.toString(item.getGroupId()));
            itemModel.setDescription(item.getDescription());
            itemModel.setName(item.getName());
        }
        return itemModel;
    }

    public static Item itemModelToItemEntity(ItemModel itemModel) {
        Item item = new Item();
        if(!Objects.isNull(itemModel)) {
            item.setGroupId(Long.parseLong(itemModel.getGroupId()));
            item.setDescription(itemModel.getDescription());
            item.setName(itemModel.getName());
        }
        return item;
    }

    public static List<ItemModel> itemEntityListToItemModelList(List<Item> items) {
        List<ItemModel> itemModelList = new ArrayList<>();
        for (Item item : items) {
            itemModelList.add(itemEntityToItemModel(item));
        }
        return itemModelList;
    }

    public static List<Item> itemModelListToItemEntityList(List<ItemModel> itemModelList) {
        List<Item> items = new ArrayList<>();
        for (ItemModel itemModel : itemModelList) {
            items.add(itemModelToItemEntity(itemModel));
        }
        return items;
    }
}
