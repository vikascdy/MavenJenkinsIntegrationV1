//package com.edifecs.epp.security.service.handler;
//
//import static org.junit.Assert.*;
//
//import com.edifecs.core.configuration.configuration.Scope;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import com.edifecs.epp.isc.message.exception.HandlerConfigurationException;
//import SecurityManager;
//import com.edifecs.epp.security.data.Contact;
//import com.edifecs.epp.security.data.Property;
//import com.edifecs.epp.security.data.User;
//import com.edifecs.epp.security.datastore.ISecurityDataStore;
//import com.edifecs.epp.security.exception.ItemAlreadyExistsException;
//import com.edifecs.epp.security.exception.SecurityManagerException;
//import com.edifecs.epp.security.jpa.helper.DatabaseDataStore;
//import com.edifecs.epp.security.service.handler.rest.PropertyHandler;
//import com.edifecs.epp.security.service.handler.rest.UserHandler;
//
////TODO fix it
//@Ignore
//public class PropertyHandlerTest {
//
//	private static PropertyHandler propertyHandler;
//	private static UserHandler userHandler;
//
//	@BeforeClass
//	public static void beforeClass() throws ItemAlreadyExistsException,
//			HandlerConfigurationException, SecurityManagerException {
//		ISecurityDataStore dataStore = new DatabaseDataStore();
//		SecurityManager sm = new SecurityManager(null, null);
//		propertyHandler = new PropertyHandler(dataStore, sm);
//		userHandler = new UserHandler(dataStore, sm);
//	}
//
//	@Test
//	public void testCreateProperties() throws Exception {
//		Contact contact = new Contact();
//		contact.setFirstName("Tom");
//		contact.setLastName("Harris");
//		User user = new User();
//		user.setActive(true);
//		user.setContact(contact);
//		user = userHandler.createUser(user);
//		Property property = new Property();
//		property.setName("property1");
//		property.setValue("value1");
//		property.setOwnerId(user.getId());
//		property.setOwnerScope(Scope.USER);
//		property = propertyHandler.createProperty(property);
//		assertNotNull(property.getId());
//	}
//
//	@Test
//	public void testDeleteProperties() throws Exception {
//		Contact contact = new Contact();
//		contact.setFirstName("Tom");
//		contact.setLastName("Harris");
//		User user = new User();
//		user.setActive(true);
//		user.setContact(contact);
//		user = userHandler.createUser(user);
//		Property property = new Property();
//		property.setName("property1");
//		property.setValue("value1");
//		property.setOwnerId(user.getId());
//		property.setOwnerScope(Scope.USER);
//		property = propertyHandler.createProperty(property);
//		propertyHandler.deleteProperty(property.getId());
//		assertNull(propertyHandler.getPropertyById(property.getId()));
//	}
//
//	@Test
//	public void testGetProperties() throws Exception {
//		Contact contact = new Contact();
//		contact.setFirstName("Tom");
//		contact.setLastName("Harris");
//		User user = new User();
//		user.setActive(true);
//		user.setContact(contact);
//		user = userHandler.createUser(user);
//		Property property = new Property();
//		property.setName("property1");
//		property.setValue("value1");
//		property.setOwnerId(user.getId());
//		property.setOwnerScope(Scope.USER);
//		property = propertyHandler.createProperty(property);
//
////		propertyHandler.getProperties();
//	}
//
//}
