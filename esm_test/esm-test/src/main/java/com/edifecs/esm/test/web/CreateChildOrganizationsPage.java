package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;

public class CreateChildOrganizationsPage extends ActionDriver{

	 private WebDriver webDriver;

	    public CreateChildOrganizationsPage(WebDriver webDriver) {
	        super(webDriver);
	    	this.webDriver = webDriver;
	    }
	    By createChildOrganizationNameInputLocator = By.id("createChildOrganization-canonicalName-inputEl"); 
	    By createChildOrganizationDescriptionInputLocator = By.id("createChildOrganization-description-inputEl"); 
	    By createChildOrganizationSaveButtonLocator = By.linkText("Save");
	    
	    By createChildOrganizationOkButtonLocator = By.linkText("OK");	     
	    public void ChildOrganization(String name, String description) {

	    	/*WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement webelement = wait.until(ExpectedConditions.elementToBeClickable(createChildOrganizationNameInputLocator));
	        */
	    	WaitForAction.Sleep(sleepForPageLoad);
	        safeSendKey(createChildOrganizationNameInputLocator, name);
	        safeSendKey(createChildOrganizationDescriptionInputLocator, description);
	        Reporter.log("Attempting to click Save button for creating Sub-Organization", true);
	        safeClick(createChildOrganizationSaveButtonLocator);
            Reporter.log("Finished clicking Save button", true);
            // would be a good place to do a named screen capture

            WaitForAction.Sleep(sleepForPageLoad);
	        safeClick(createChildOrganizationOkButtonLocator);
	    }
	}
