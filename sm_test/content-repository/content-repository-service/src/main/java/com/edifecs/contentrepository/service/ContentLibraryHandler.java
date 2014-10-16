package com.edifecs.contentrepository.service;

import com.edifecs.contentrepository.IContentLibraryHandler;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.contentrepository.api.model.Item;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abhising on 01-08-2014.
 */
public class ContentLibraryHandler extends AbstractCommandHandler implements IContentLibraryHandler {

    public ContentLibraryHandler() {
    }

    @Override
    public Item createItem(String path, String name, String description, String itemType, HashMap<String,
            Collection<Item>> associations, HashMap<String, Object> properties) throws Exception {
        return ContentRepositoryService.getContentLibrary().addItem(path, new Item(name, itemType, description, properties, associations));

    }

    @Override
    public Item addItem(String path, Item item) throws Exception {
        return ContentRepositoryService.getContentLibrary().addItem(path, item);
    }

    @Override
    public Item getItemById(String id) throws Exception {
        return ContentRepositoryService.getContentLibrary().getItemByID(id);
    }

    @Override
    public Map<String, Collection<Item>> getAllItems(String path) throws Exception {
        return ContentRepositoryService.getContentLibrary().getAllItems(path);
    }

    @Override
    public Collection<Item> getAllItemsOfType(String path, String itemType) throws Exception {
        return ContentRepositoryService.getContentLibrary().getAllItemsOfType(path, itemType);
    }

    @Override
    public Item getItemByName(String path, String itemType, String name) throws Exception {
        return ContentRepositoryService.getContentLibrary().getItemByName(path, itemType, name);
    }

    @Override
    public Item updateItem(Item item) throws Exception {
        return ContentRepositoryService.getContentLibrary().updateItem(item);
    }

    @Override
    public boolean deleteItem(String id) throws Exception {
        return ContentRepositoryService.getContentLibrary().deleteItem(id);
    }

    @Override
    public Collection<Item> getItemsAssociatedWith(String itemId, String itemType) throws ContentRepositoryException {
        return ContentRepositoryService.getContentLibrary().getItemsAssociatedWith(itemId, itemType);
    }
}
