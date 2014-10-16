package com.edifecs.contentrepository.test.unit;

import com.edifecs.contentrepository.api.exception.ContentRepositoryException;
import com.edifecs.contentrepository.api.model.Item;
import com.edifecs.contentrepository.api.model.ItemType;
import com.edifecs.epp.security.data.Tenant;
import com.edifecs.epp.security.data.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by abhising on 31-07-2014.
 */
public class ContentLibraryTest extends JackrabbitRepositoryMultiTenancyTest {

    @Test
    public void testAddGetItem() throws Exception {
        for (Map.Entry<User, Tenant> entry : users.entrySet()) {
            ((MockCententRepository) contentRepository).setTestTenant(entry
                    .getValue());
            ((MockCententRepository) contentRepository).simulateUserLogin(entry
                    .getKey());

            Item item1 = new Item();
            item1.setName("Test Widget Add");
            item1.setItemType(ItemType.WIDGET.toString());
            item1.setDescription("Testing");
            item1.getProperties().put("location", 1L);
            item1.getProperties().put("key", "value");
            item1.getProperties().put("list", Arrays.asList("1", "2", "3"));

            Item result = ((MockCententRepository) contentRepository).addItem("testApp", item1);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getId());
            Item getItem = ((MockCententRepository) contentRepository).getItemByID(result.getId().toString());
            Assert.assertNotNull(getItem);

            System.out.println("Created Item : " + result.getId());
            System.out.println("Get Item : " + getItem);
        }
    }

    @Test(expected = ContentRepositoryException.class)
    public void testAddItemFail() throws Exception {
        // same name, should fail
        for (Map.Entry<User, Tenant> entry : users.entrySet()) {
            ((MockCententRepository) contentRepository).setTestTenant(entry
                    .getValue());
            ((MockCententRepository) contentRepository).simulateUserLogin(entry
                    .getKey());

            Item item1 = new Item();
            item1.setName("Test Widget Add 1");
            item1.setItemType(ItemType.WIDGET.toString());
            item1.setDescription("Testing");
            item1.getProperties().put("location", 1L);
            item1.getProperties().put("key", "value");
            item1.getProperties().put("list", Arrays.asList("1", "2", "3"));

            ((MockCententRepository) contentRepository).addItem("testApp", item1);
            item1.setId(null);
            ((MockCententRepository) contentRepository).addItem("testApp", item1);
        }
    }

    @Test
    public void testUpdateItem() throws Exception {
        for (Map.Entry<User, Tenant> entry : users.entrySet()) {
            ((MockCententRepository) contentRepository).setTestTenant(entry
                    .getValue());
            ((MockCententRepository) contentRepository).simulateUserLogin(entry
                    .getKey());

            Item item1 = new Item();
            item1.setName("Test Widget Add 2");
            item1.setItemType(ItemType.WIDGET.toString());
            item1.setDescription("Testing");
            item1.getProperties().put("location", 1L);
            item1.getProperties().put("key", "value");
            item1.getProperties().put("list", Arrays.asList("1", "2", "3"));

            Item result = ((MockCententRepository) contentRepository).addItem("testApp1", item1);
            Item getItem = ((MockCententRepository) contentRepository).getItemByID(result.getId().toString());
            Assert.assertNotNull(getItem);

            for (Map.Entry<String, Object> entry1 : getItem.getProperties().entrySet()) {
                System.out.println(entry1.getKey() + " : " + entry1.getValue());
            }

            getItem.setName("Updated");
            ((MockCententRepository) contentRepository).updateItem(getItem);

            getItem = ((MockCententRepository) contentRepository).getItemByID(result.getId().toString());
            Assert.assertNotNull(getItem);
            Assert.assertEquals("Updated", getItem.getName());
        }
    }

    @Test(expected = ContentRepositoryException.class)
    public void testDeleteItem() throws Exception {
        for (Map.Entry<User, Tenant> entry : users.entrySet()) {
            ((MockCententRepository) contentRepository).setTestTenant(entry
                    .getValue());
            ((MockCententRepository) contentRepository).simulateUserLogin(entry
                    .getKey());

            Item item1 = new Item();
            item1.setName("Test Widget Add 3");
            item1.setItemType(ItemType.WIDGET.toString());
            item1.setDescription("Testing");
            item1.getProperties().put("location", 1L);
            item1.getProperties().put("key", "value");
            item1.getProperties().put("list", Arrays.asList("1", "2", "3"));

            Item result = ((MockCententRepository) contentRepository).addItem("testApp1", item1);
            Item getItem = ((MockCententRepository) contentRepository).getItemByID(result.getId().toString());
            Assert.assertNotNull(getItem);

            ((MockCententRepository) contentRepository).deleteItem(getItem.getId().toString());

            getItem = ((MockCententRepository) contentRepository).getItemByID(result.getId().toString());
        }
    }

    @Test
    public void getAllItems() throws Exception {
        for (Map.Entry<User, Tenant> entry : users.entrySet()) {
            ((MockCententRepository) contentRepository).setTestTenant(entry
                    .getValue());
            ((MockCententRepository) contentRepository).simulateUserLogin(entry
                    .getKey());

            Item item1 = new Item();
            item1.setName("Test Widget Add 4");
            item1.setItemType(ItemType.WIDGET.toString());
            item1.setDescription("Testing");
            item1.getProperties().put("location", 1L);
            item1.getProperties().put("key", "value");
            item1.getProperties().put("list", Arrays.asList("1", "2", "3"));

            ((MockCententRepository) contentRepository).addItem("testApp", item1);

            Item item2 = new Item();
            item2.setName("Test Widget Add 5");
            item2.setItemType(ItemType.WIDGET.toString());
            item2.setDescription("Testing");
            item2.getProperties().put("location", 1L);
            item2.getProperties().put("key", "value");
            item2.getProperties().put("list", Arrays.asList("1", "2", "3"));

            item2 = ((MockCententRepository) contentRepository).addItem("testApp", item2);

            Map<String, Collection<Item>> getItems = ((MockCententRepository) contentRepository).getAllItems("testApp1");
            Assert.assertNotNull(getItems);
            Assert.assertTrue(getItems.size() > 0);

            ((MockCententRepository) contentRepository).deleteItem(item2.getId());
            Map<String, Collection<Item>> getItems2 = ((MockCententRepository) contentRepository).getAllItems
                    ("testApp1");
            Assert.assertNotNull(getItems2);
            Assert.assertTrue(getItems2.size() > 0);

            for (Map.Entry<String, Collection<Item>> entry2 : getItems2.entrySet()) {
                System.out.println(entry2.getKey() + " :: " + entry2.getValue());
            }
        }
    }
}
