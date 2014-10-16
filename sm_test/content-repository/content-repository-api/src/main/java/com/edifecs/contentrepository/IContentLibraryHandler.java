package com.edifecs.contentrepository;

import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.contentrepository.api.model.Item;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.CommandHandler;
import com.edifecs.epp.isc.annotations.RequiresPermissions;
import com.edifecs.epp.isc.annotations.SyncCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abhising on 01-08-2014.
 */

@CommandHandler
public interface IContentLibraryHandler {

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:create")
    public Item createItem(
            @Arg(name = "path", required = false, description = "App/Folder Containing Content") String path,
            @Arg(name = "name", required = true, description = "Name of the Item") String name,
            @Arg(name = "description", required = true, description = "Item Description") String description,
            @Arg(name = "type", required = true, description = "Item Type") String itemType,
            @Arg(name = "associations", required = true, description = "Item Associations") HashMap<String,
                    Collection<Item>> associations,
            @Arg(name = "properties", description = "Item Properties") HashMap<String, Object> properties
    ) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:add")
    public Item addItem(
            @Arg(name = "path", required = true, description = "App/Folder Containing Content") String path,
            @Arg(name = "item", required = true, description = "Item") Item item
    ) throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:read")
    public Item getItemById(
            @Arg(name = "id", required = true, description = "Item ID") String id)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:list")
    Collection<Item> getItemsAssociatedWith(
            @Arg(name = "associatedItemId", required = true, description = "Item ID") String itemId,
            @Arg(name = "getItemType", required = true, description = "Item Type") String itemType) throws
            ContentRepositoryException;

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:list")
    public Map<String, Collection<Item>> getAllItems(
            @Arg(name = "path", required = true, description = "App/Folder Containing Content") String path)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:list")
    public Collection<Item> getAllItemsOfType(
            @Arg(name = "path", required = true, description = "App/Folder Containing Content") String path,
            @Arg(name = "itemType", required = true, description = "Item Type") String itemType)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:read")
    public Item getItemByName(
            @Arg(name = "path", required = true, description = "App/Folder Containing Content") String path,
            @Arg(name = "itemType", required = true, description = "Item Type") String itemType,
            @Arg(name = "name", required = true, description = "Item Name") String name) throws
            Exception;

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:edit")
    public Item updateItem(
            @Arg(name = "item", required = true, description = "item to be updated") Item item)
            throws Exception;

    @SyncCommand
    @RequiresPermissions("platform:repository:content:item:delete")
    public boolean deleteItem(
            @Arg(name = "id", required = true, description = "Item ID") String id)
            throws Exception;
}
