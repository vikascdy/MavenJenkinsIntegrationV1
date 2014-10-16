# Content Library Module

The purpose of the content library to provide the API to persist the semi-structured data into repository.
 
Basic Unit of storing the semi-structured data is an item and item will have some predefined properties and it can custom properties depending of the usage.

Content Library has significant usage in the xBoard Project where Widget, Widget Definition, Page, App, Data Sources etc. will be persisted using content library.

## Content Library Model

### Item

Item has few defined properties and it will have properties and associations with other items.

        public class Item implements Serializable {
        
            private String id;
        
            private String name;
        
            private ItemType itemType;
        
            private String description;
        
            private Map<String, Object> properties = new HashMap<>();
        
            private Map<ItemType, Collection<Item>> associations = new HashMap<>();
        
            private String version;
        
            private String createdBy;
        
            private Date dateCreated;
        
            private String modifiedBy;
        
            private Date dateModified;
        }
        
Item mixin type is defined like this

    <edifecs = 'http://edifecs.com/1.0'>
    <item = 'http://edifecs.com/item/1.0'>
    
    [edifecs:preserveAfterDeletion] mixin
    - edifecs:deleted (BOOLEAN)
    
    [edifecs:item]
    > mix:created, mix:lastModified, mix:versionable, edifecs:preserveAfterDeletion
    orderable
    primaryitem item:id
    - item:name (STRING) mandatory
    - item:description (STRING)
    - item:itemType (STRING) mandatory
    - item:associations (STRING) multiple
    + edifecs:itemProperties = nt:unstructured autocreated version
    
    [edifecs:itemRoot] mixin
    + * (edifecs:item)

### ItemType

Different item types need to be registered and for now they are part on enumeration

    public enum ItemType {
        PAGE, WIGDET, WIGDET_TYPE, DATASET, DATASOURCE;
    }

## Content Library API

Content library provides following methods for the CRUD operations
 

    public interface IContentLibrary extends IContentRepository {
    
        Item addItem(String path, Item item) throws ContentRepositoryException;
    
        Item getItemByID(String id) throws ContentRepositoryException;
    
        Map<String, Collection<Item>> getAllItems(String path) throws ContentRepositoryException;
    
        Collection<Item> getAllItemsOfType(String path, ItemType itemType) throws ContentRepositoryException;
    
        Item getItemByName(String path, ItemType itemType, String name) throws ContentRepositoryException;
    
        Item updateItem(Item item) throws ContentRepositoryException;
    
        boolean deleteItem(String id) throws ContentRepositoryException;
    
    }
 
 Content library by default using multi-tenant repositories.
 