package com.edifecs.contentrepository.api;

import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.contentrepository.api.model.Item;

import java.util.Collection;
import java.util.Map;

/**
 * Created by abhising on 29-07-2014.
 */
public interface IContentLibrary extends IContentRepository {

    Item addItem(String path, Item item) throws ContentRepositoryException;

    Item getItemByID(String id) throws ContentRepositoryException;

    Map<String, Collection<Item>> getAllItems(String path) throws ContentRepositoryException;

    Collection<Item> getAllItemsOfType(String path, String itemType) throws ContentRepositoryException;

    Collection<Item> getItemsAssociatedWith(String itemId, String itemType) throws ContentRepositoryException;

    Item getItemByName(String path, String itemType, String name) throws ContentRepositoryException;

    Item updateItem(Item item) throws ContentRepositoryException;

    boolean deleteItem(String id) throws ContentRepositoryException;

}
