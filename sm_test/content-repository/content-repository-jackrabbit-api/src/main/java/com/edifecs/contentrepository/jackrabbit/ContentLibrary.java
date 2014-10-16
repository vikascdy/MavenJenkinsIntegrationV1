package com.edifecs.contentrepository.jackrabbit;

import com.edifecs.contentrepository.api.IContentLibrary;
import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.contentrepository.api.model.Item;
import com.edifecs.contentrepository.api.model.ItemType;
import com.edifecs.epp.security.ISecurityManager;
import com.edifecs.epp.security.data.Tenant;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.*;

/**
 * Created by abhising on 29-07-2014.
 */
public class ContentLibrary extends ContentRepository implements IContentLibrary {

    protected static final String ITEM_NODE_TYPE = "edifecs:item";
    protected static final String ITEM_ROOT_MIXIN = "edifecs:itemRoot";
    protected static final String ITEM_ID = "jcr:uuid";
    protected static final String ITEM_NAME_PROPERTY = "item:name";
    protected static final String ITEM_ASSC_PROPERTY = "item:associations";
    protected static final String ITEM_TYPE_PROPERTY = "item:itemType";
    protected static final String ITEM_DESC_PROPERTY = "item:description";
    protected static final String ITEM_PROPERTIES_CHILD = "edifecs:itemProperties";
    protected static final String ITEM_CAT_CHILD = "edifecs:itemCategory";
    protected static final String PATH_TO_SYSTEM_XBOARD = SEPARATOR + TENANT_SYSTEM_FOLDER + SEPARATOR + TENANT_SYSTEM_XBOARD_FOLDER;

    public ContentLibrary(ISecurityManager securityManager) throws ContentRepositoryException {
        super(securityManager);
    }

    @Override
    public Item addItem(String path, Item item) throws ContentRepositoryException {
        if (null != item.getId()) {
            throw new ContentRepositoryException("Item Already Exists");
        }

        Session session = null;
        Node rootContent;
        boolean adminOp = false;
        try {
            if (item.getItemType().equals(ItemType.DATASET.toString()) ||
                    item.getItemType().equals(ItemType.WIDGET_TYPE.toString()) ||
                    item.getItemType().equals(ItemType.DATASOURCE.toString()) ||
                    item.getItemType().equals(ItemType.CATEGORY.toString()) ||
                    item.getItemType().equals(ItemType.SUB_CATEGORY.toString())
                    ) {
                // add these items to /Tenant/System/xBoard/
                adminOp = true;
                Tenant tenant = getTenant();
                session = getRootSession(tenant.getId());

                Node xboard = session.getNode(PATH_TO_SYSTEM_XBOARD);
                if (!xboard.hasNode(item.getItemType())) {
                    rootContent = xboard.addNode(item.getItemType());
                    rootContent.addMixin(ITEM_ROOT_MIXIN);
                } else {
                    rootContent = xboard.getNode(item.getItemType());
                }

            } else {
                //create app node, if not exists
                session = getSession();
                Node app;
                try {
                    app = nodeByUserPath(path);
                } catch (ContentRepositoryException e) {
                    // node not found
                    createFolder(path);
                    app = nodeByUserPath(path);
                }
                // add item to folder named [itemType]
                if (!app.hasNode(item.getItemType())) {
                    rootContent = app.addNode(item.getItemType());
                    rootContent.addMixin(ITEM_ROOT_MIXIN);
                } else {
                    rootContent = app.getNode(item.getItemType());
                }
            }

            Node itemNode = rootContent.addNode(item.getName(), ITEM_NODE_TYPE);
            item.setId(itemNode.getIdentifier());
            itemToJcrNode(itemNode, item);
            session.save();
            getLogger().debug("Item added Successfully. " + itemNode.getPath());
            item = jcrNodeToItem(itemNode, session);
            return item;
        } catch (ItemExistsException e) {
            getLogger().error("error add item", e);
            throw new ContentRepositoryException("Unable to create Item : Item already Exists");
        } catch (RepositoryException e) {
            getLogger().error("error add item", e);
            throw new ContentRepositoryException("Unable to create Item.");
        } finally {
            if (adminOp && null != session && session.isLive())
                session.logout();
        }
    }

    @Override
    public Item getItemByID(String id) throws ContentRepositoryException {
        try {
            return jcrNodeToItem(getItemNodeByUUID(id));
        } catch (RepositoryException e) {
            getLogger().error("error get item by id", e);
            throw new ContentRepositoryException("Unable to get Item for ID : " + id);
        }
    }

    @Override
    public Item getItemByName(String path, String itemType, String name) throws ContentRepositoryException {
        try {
            Node item = nodeByUserPath(path + SEPARATOR + itemType + SEPARATOR + name);
            return jcrNodeToItem(item);
        } catch (ContentRepositoryException | RepositoryException e) {
            getLogger().error("error get item by name", e);
            throw new ContentRepositoryException("Item : " + name + " Not Found");
        }
    }

    @Override
    public Item updateItem(Item item) throws ContentRepositoryException {
        if (null == item.getId()) {
            throw new ContentRepositoryException("Item Id : " + item.getId() + "Cannot be Null");
        }
        try {
            Node itemNode = getItemNodeByUUID(item.getId());
            getVersionManager().checkout(itemNode.getPath());
            itemToJcrNode(itemNode, item);
            getSession().save();
            getVersionManager().checkin(itemNode.getPath());
            return item;
        } catch (ContentRepositoryException | RepositoryException e) {
            getLogger().error("error update item by id", e);
            throw new ContentRepositoryException("Item : " + item.getId() + " Not Found");
        }
    }

    @Override
    public boolean deleteItem(String id) throws ContentRepositoryException {
        // soft delete
        markDeleted(getItemNodeByUUID(id));
        return true;
    }

    private Node itemToJcrNode(Node itemNode, Item item) throws RepositoryException,
            ContentRepositoryException {
        itemNode.setProperty(ITEM_TYPE_PROPERTY, item.getItemType());
        itemNode.setProperty(ITEM_DESC_PROPERTY, item.getDescription());
        itemNode.setProperty(ITEM_NAME_PROPERTY, item.getName());

        Node props = itemNode.getNode(ITEM_PROPERTIES_CHILD);
        for (Map.Entry<String, Object> entry : item.getProperties().entrySet()) {
            // ignore jcr defaults
            if (entry.getKey().startsWith("jcr")) {
                continue;
            }
            props.setProperty(entry.getKey(), entry.getValue().toString());
        }
        // references
        Set<String> refIds = new HashSet<>();
        for (Map.Entry<String, Collection<Item>> entry : item.getAssociations().entrySet()) {
            for (Item refItem : entry.getValue()) {
                if (refItem.getId() == null) {
                    throw new ContentRepositoryException(String.format("Unsaved Association Found." +
                            "Item '%s' refers to an Item : '%s', which has not been saved. Please persist the " +
                            "Transient Items First.", item.getName(), refItem.getName()));
                }
                refIds.add(refItem.getId());
            }
        }
        itemNode.setProperty(ITEM_ASSC_PROPERTY, refIds.toArray(new String[refIds.size()]));
        return itemNode;
    }

    private Item jcrNodeToItem(Node itemNode) throws RepositoryException, ContentRepositoryException {
        return jcrNodeToItem(itemNode, getSession());
    }

    private Item jcrNodeToItem(Node itemNode, Session session) throws RepositoryException, ContentRepositoryException {
        if (!isValidItemNode(itemNode)) {
            throw new ContentRepositoryException("Invalid Request. Item with ID : " + itemNode.getIdentifier() + " " +
                    "has been deleted");
        }
        Item item = new Item();
        item.setId(getOrElse(itemNode, ITEM_ID));
        item.setDescription(getOrElse(itemNode, ITEM_DESC_PROPERTY));
        item.setName(getOrElse(itemNode, ITEM_NAME_PROPERTY));
        item.setItemType(getOrElse(itemNode, ITEM_TYPE_PROPERTY));
        if (itemNode.hasNode(ITEM_PROPERTIES_CHILD)) {
            PropertyIterator it = itemNode.getNode(ITEM_PROPERTIES_CHILD).getProperties();
            while (it.hasNext()) {
                Property p = it.nextProperty();
                if (p.isMultiple()) {
                    StringBuffer values = new StringBuffer();
                    for (Value v : p.getValues()) {
                        values.append(v.getString());
                        values.append(",");
                    }
                    item.getProperties().put(p.getName(), values.toString());
                } else {
                    item.getProperties().put(p.getName(), p.getValue().getString());
                }
            }
        }
        item.setAssociations(getAssociations(itemNode, new HashSet<String>(), session));
        item.setDateCreated(itemNode.getProperty(JCR_CREATED).getDate().getTime());
        item.setCreatedBy(itemNode.getProperty(JCR_CREATED_BY).getString());
        item.setModifiedBy(itemNode.getProperty(JCR_LAST_MODIFIED_BY).getString());
        item.setDateModified(itemNode.getProperty(JCR_LAST_MODIFIED).getDate().getTime());
        item.setVersion(itemNode.getProperty(JCR_BASE_VERSION).getString());
        return item;
    }

    private String getOrElse(Node node, String property) {
        try {
            if (node.hasProperty(property)) {
                return node.getProperty(property).getString();
            }
        } catch (RepositoryException e) {
            getLogger().debug("Value Not Found for Property : " + property);
        }
        return null;
    }

    private Map<String, Collection<Item>> getAssociations(Node itemNode, Set<String> nodesTraversed,
                                                          Session session) throws
            RepositoryException, ContentRepositoryException {

        if (session == null) {
            session = getSession();
        }
        nodesTraversed.add(itemNode.getIdentifier());
        Map<String, Collection<Item>> associations = new HashMap<>();

        for (Value reference : itemNode.getProperty(ITEM_ASSC_PROPERTY).getValues()) {
            getLogger().debug(String.format("found reference : '%s' for node : '%s'", reference.getString(),
                    itemNode.getName()));
            Node refNode = getItemNodeByUUID(reference.getString(), session);
            try {
                if (!nodesTraversed.contains(refNode.getIdentifier()) && isValidItemNode(refNode)) {
                    String key = refNode.getProperty(ITEM_TYPE_PROPERTY).getString();
                    if (!associations.containsKey(key)) {
                        associations.put(key, Arrays.asList(jcrNodeToItem(refNode, session)));
                    } else {
                        Collection<Item> assocs = associations.get(key);
                        Set<Item> assocItems = new HashSet<Item>(assocs);
                        assocItems.add(jcrNodeToItem(refNode, session));
                        associations.put(key, assocItems);
                    }
                }
            } catch (ContentRepositoryException e) {
                // ignore this node
                getLogger().debug("Unable to parse Node : " + refNode.getName());
            }
        }
        return associations;
    }

    @Override
    public Map<String, Collection<Item>> getAllItems(String path) throws ContentRepositoryException {
        Map<String, Collection<Item>> itemsMap = new HashMap<>();
        for (ItemType type : ItemType.values()) {
            itemsMap.put(type.name(), getAllItemsOfType(path, type.toString()));
        }
        return itemsMap;
    }

    @Override
    public Collection<Item> getAllItemsOfType(String path, String itemType) throws ContentRepositoryException {
        if (itemType.equals(ItemType.DATASET.toString()) ||
                itemType.equals(ItemType.WIDGET_TYPE.toString()) ||
                itemType.equals(ItemType.DATASOURCE.toString()) ||
                itemType.equals(ItemType.CATEGORY.toString()) ||
                itemType.equals(ItemType.SUB_CATEGORY.toString())
                ) {
            return getItemDefs(itemType);

        } else {
            Node app;
            try {
                app = nodeByUserPath(path);
            } catch (ContentRepositoryException e) {
                throw new ContentRepositoryException("Invalid Path");
            }
            return getItemsAtPath(app, itemType, getSession());
        }
    }

    @Override
    public Collection<Item> getItemsAssociatedWith(String itemId, String itemType) throws ContentRepositoryException {
        Set<Item> items = new HashSet<>();

        //TODO: update query to Jcr.SQL2
        String q = String.format("SELECT * FROM %s WHERE %s ='%s' AND %s = '%s'", ITEM_NODE_TYPE,
                ITEM_TYPE_PROPERTY,
                itemType, ITEM_ASSC_PROPERTY, itemId);

        try {
            items.addAll(executeJcrQuery(null, q));
            if (itemType.equals(ItemType.DATASET.toString()) ||
                    itemType.equals(ItemType.WIDGET_TYPE.toString()) ||
                    itemType.equals(ItemType.DATASOURCE.toString()) ||
                    itemType.equals(ItemType.CATEGORY.toString()) ||
                    itemType.equals(ItemType.SUB_CATEGORY.toString())
                    ) {
                // also get these items from default Repo
                items.addAll(executeJcrQuery(getRootSession(DEFAULT_TENANT_ID), q));
            }
        } catch (ContentRepositoryException e) {
            throw new ContentRepositoryException(String.format("Unable to find Associated %ss for %s, reason : %s",
                    itemType, itemId, e.getMessage()));
        }
        return items;
    }

    private Collection<Item> executeJcrQuery(Session session, String queryExpr) throws ContentRepositoryException {
        Set<Item> items = new HashSet<>();
        if (session == null) {
            session = getSession();
        }

        try {
            Workspace workspace = session.getWorkspace();
            QueryManager qm = workspace.getQueryManager();

            Query query = qm.createQuery(queryExpr, Query.SQL);
            QueryResult result = query.execute();
            NodeIterator it = result.getNodes();
            while (it.hasNext()) {
                Node node = it.nextNode();
                if (!isValidItemNode(node)) {
                    continue;
                }
                items.add(jcrNodeToItem(node, session));
            }
        } catch (RepositoryException e) {
            throw new ContentRepositoryException(e);
        }
        return items;
    }

    private Collection<Item> getItemDefs(String itemType) throws ContentRepositoryException {
        Set<Item> items = new HashSet<>();
        Session session = getSession();
        try {
            Node xboard = session.getNode(PATH_TO_SYSTEM_XBOARD);
            items.addAll(getItemsAtPath(xboard, itemType, session));
            if (getTenant().getId() != DEFAULT_TENANT_ID) {
                items.addAll(getItemDefsFromDefaultTenantRepository(itemType));
            }
        } catch (RepositoryException e) {
            getLogger().error("error get all items", e);
            throw new ContentRepositoryException("Unable to get Items : reason : " + e.getMessage());
        }
        return items;
    }

    private Collection<? extends Item> getItemDefsFromDefaultTenantRepository(String itemType) throws
            ContentRepositoryException {
        Session session = getRootSession(DEFAULT_TENANT_ID);
        try {
            Node xboard = session.getNode(PATH_TO_SYSTEM_XBOARD);
            return getItemsAtPath(xboard, itemType, session);
        } catch (RepositoryException e) {
            getLogger().error("error getting items from default repository", e);
            throw new ContentRepositoryException("Unable to get Items : reason : " + e.getMessage());
        } finally {
            if (null != session && session.isLive()) {
                session.logout();
            }
        }
    }

    private Collection<Item> getItemsAtPath(Node path, String itemType, Session session) throws ContentRepositoryException {
        try {
            Set<Item> items = new HashSet<>();
            if (path.hasNode(itemType)) {
                Node type = path.getNode(itemType);
                NodeIterator it = type.getNodes();
                while (it.hasNext()) {
                    Node n = it.nextNode();
                    if (isValidItemNode(n)) {
                        items.add(jcrNodeToItem(n, session));
                    }
                }
            }
            return items;
        } catch (RepositoryException e) {
            getLogger().error("error get all items", e);
            throw new ContentRepositoryException("Unable to get Items : reason : " + e.getMessage());
        }
    }

    private Node getItemNodeByUUID(String id) throws ContentRepositoryException {
        return getItemNodeByUUID(id, getSession());
    }

    private Node getItemNodeByUUID(String id, Session session) throws ContentRepositoryException {
        Node item;
        try {
            if (session == null) {
                session = getSession();
            }
            item = session.getNodeByIdentifier(id);
            if (!isValidItemNode(item)) {
                throw new ContentRepositoryException("Invalid id : " + id + "Item Not Found. Item may have been " +
                        "deleted");
            }
            return item;
        } catch (RepositoryException e) {
            // check for item def in defualt tenant repo : /tenant/system/xboard/...
            item = getNodeFromDefaultTenantRepository(id);
            if (null != item) {
                return item;
            }
            getLogger().error("error get item by uuid from util", e);
            throw new ContentRepositoryException(e);
        }
    }

    private Node getNodeFromDefaultTenantRepository(String id) throws ContentRepositoryException {
        Node item = null;
        Session session = null;
        try {
            session = getRootSession(DEFAULT_TENANT_ID);
            item = session.getNodeByIdentifier(id);
            if (!isValidItemNode(item)) {
                return null;
            }
        } catch (RepositoryException e) {
            // node not found here too.
            getLogger().warn("Node with ID : " + id + " Not Even Found on Default Repository.");
        }
        return item;
    }

    private boolean isValidItemNode(Node item) throws RepositoryException, ContentRepositoryException {
        return item.isNodeType(ITEM_NODE_TYPE) && !isDeleted(item);
    }
}
