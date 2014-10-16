package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

public class CreatePropertyPage extends ActionDriver{

	WebDriver webDriver;
	public CreatePropertyPage(WebDriver webDriver){
		super(webDriver);
		this.webDriver = webDriver;
	}
	
	By addNewPropertyNameInputLocator = By.id("realmConfigProperties-propertyName-inputEl");
	By addNewPropertyValueInputLocator = By.id("realmConfigProperties-propertyValue-inputEl");
	By addNewPropertyDescriptionInputLocator = By.id("realmConfigProperties-description-inputEl");
	By addNewPropertyAddButtonLocator = By.id("realmConfigProperties-add-btnIconEl");
	By addNewPropertyCancelButtonLocator = By.id("realmConfigProperties-cancel-btnIconEl");
	public void createNewProperty(String name,String value,String description){
		Reporter.log("Before Add Property");
		safeSendKey(addNewPropertyNameInputLocator, name);
		safeSendKey(addNewPropertyValueInputLocator, value);
		safeSendKey(addNewPropertyDescriptionInputLocator, description);
		
		safeClick(addNewPropertyAddButtonLocator);
		Reporter.log("After Add Property");		
	}
	public void cancelNewPropertyDialog(){
		safeClick(addNewPropertyCancelButtonLocator);
		Reporter.log("after cancelNewPropertyDialog");
	}
}
