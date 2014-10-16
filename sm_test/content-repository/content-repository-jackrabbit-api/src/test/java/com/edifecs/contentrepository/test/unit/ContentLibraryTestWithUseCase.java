package com.edifecs.contentrepository.test.unit;

import com.edifecs.contentrepository.api.model.Item;
import com.edifecs.contentrepository.api.model.ItemType;
import com.edifecs.core.configuration.helper.SystemVariables;
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
public class ContentLibraryTestWithUseCase extends JackrabbitRepositoryMultiTenancyTest {

    protected static final String APP = "xBoard";

    @Test
    public void testApp() throws Exception {
        Item chartType = null;
        Item cat = null;

        for (Map.Entry<User, Tenant> entry : users.entrySet()) {
            ((MockCententRepository) contentRepository).setTestTenant(entry
                    .getValue());
            ((MockCententRepository) contentRepository).simulateUserLogin(entry
                    .getKey());

            if (entry.getValue().getCanonicalName().equals(SystemVariables.DEFAULT_TENANT_NAME)) {
//                simulate creation of pre def widgetTypes
                Item subCat = new Item();
                subCat.setName("Extjs Charts");
                subCat.setDescription("Test Sub Cat");
                subCat.setItemType(ItemType.CATEGORY.toString());
                subCat = ((MockCententRepository) contentRepository).addItem(null, subCat);

                cat = new Item();
                cat.setName("Charts");
                cat.setDescription("Test Cat");
                cat.setItemType(ItemType.CATEGORY.toString());
                cat.getAssociations().put(ItemType.SUB_CATEGORY.toString(), Arrays.asList(subCat));
                cat = ((MockCententRepository) contentRepository).addItem(null, cat);

                chartType = new WidgetType();
                chartType.setName("Pie Chart");
                chartType.setItemType(ItemType.WIDGET_TYPE.toString());
                chartType.getAssociations().put(ItemType.CATEGORY.toString(), Arrays.asList(cat));
                chartType = ((MockCententRepository) contentRepository).addItem(APP, chartType);
                System.out.println("Added Widget Type : Chart : " + chartType +
                        " For User : " + entry.getKey().getId());

                Collection<Item> assocs = ((MockCententRepository) contentRepository).getItemsAssociatedWith(cat
                                .getId(),
                        ItemType.WIDGET_TYPE.toString());
                Assert.assertFalse(assocs.isEmpty());
                System.out.println("Found # Associations 1 : " + assocs.size());
                for (Item i : assocs) {
                    System.out.println("Assoc. : " + i.getName());
                }
                continue;
            }

            if (null != chartType) {
                Collection<Item> assocs = ((MockCententRepository) contentRepository).getItemsAssociatedWith(cat
                                .getId(),
                        ItemType.WIDGET_TYPE.toString());
                Assert.assertFalse(assocs.isEmpty());
                System.out.println("Found # Associations 2 : " + assocs.size());
                for (Item i : assocs) {
                    System.out.println("Assoc. : " + i.getName());
                }

                Assert.assertTrue(((MockCententRepository) contentRepository).getAllItemsOfType(APP,
                        ItemType.WIDGET_TYPE.toString()).size() > 0);

                Item imgType = new WidgetType();
                imgType.setName("HTML");
                imgType.setItemType(ItemType.WIDGET_TYPE.toString());
                imgType = ((MockCententRepository) contentRepository).addItem(APP, imgType);
                System.out.println("Added Widget Type : HTML : " + imgType +
                        " For User : " + entry.getKey().getId());

                // create widgets
                Item chart = new Widget();
                chart.setName("My Chart");
                chart.setItemType(ItemType.WIDGET.toString());
                chart.setDescription("Testing Pie Chart");
                chart.getProperties().put("location", "10,20");
                chart.getProperties().put("config", "{\"data\" : \"json\"");
                chart.getAssociations().put(ItemType.WIDGET_TYPE.toString(), Arrays.asList(chartType));
                chart = ((MockCententRepository) contentRepository).addItem(APP, chart);
                Assert.assertNotNull(chart);
                Assert.assertNotNull(chart.getId());
                System.out.println("Created Widget : " + chart);

                Item html = new Widget();
                html.setName("HTML Widget");
                html.setItemType(ItemType.WIDGET.toString());
                html.setDescription("Testing HTML");
                html.getProperties().put("location", "20,40");
                html.getProperties().put("config", "{<h1>Hello World</h1>");
                html.getAssociations().put(ItemType.WIDGET_TYPE.toString(), Arrays.asList(imgType));
                html = ((MockCententRepository) contentRepository).addItem(APP, html);
                Assert.assertNotNull(html);
                Assert.assertNotNull(html.getId());
                System.out.println("Created Widget : " + html);

                // create page
                Item page = new Page();
                page.setName("Index Page");
                page.setItemType(ItemType.PAGE.toString());
                page.setDescription("Testing Page");
                page.getProperties().put("key1", "value1");
                page.getProperties().put("key2", "value2");
                page.getProperties().put("key3", "value3");

                page.getAssociations().put(ItemType.WIDGET.toString(), Arrays.asList(chart, html));
                page = ((MockCententRepository) contentRepository).addItem(APP, page);
                Assert.assertNotNull(page);
                Assert.assertNotNull(page.getId());
                System.out.println("Created Page : " + page);

                Item getItem = ((MockCententRepository) contentRepository).getItemByID(page.getId());
                Assert.assertNotNull(getItem);
                System.out.println("Get Page : " + getItem);
                Assert.assertNotNull(getItem.getAssociations());
                Assert.assertFalse(getItem.getAssociations().isEmpty());
            }
            break;
        }
    }

    static class Page extends Item {
    }

    static class Widget extends Item {

    }

    static class WidgetType extends Item {

    }
}
