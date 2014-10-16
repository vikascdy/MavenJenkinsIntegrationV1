package com.edifecs.esm.test.web;

import java.util.List;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

public class SubOrganizationPage extends ActionDriver{

	 WebDriver webDriver;

	    public SubOrganizationPage(WebDriver webDriver) {
	        super(webDriver);
	    	this.webDriver = webDriver;
	    }
	    By ChildOrganizationNewLinkLocator = By.id("assignOrganizationRoles-link");
	    By ChildOrganizationClassLocator = By.className("x-grid-row-checker");
	    By ChildOrganizationDeleteLinkLocator = By.id("removeOrganizationRoles-link");
	    By ChildOrganizationYesButtonLocator = By.linkText("Yes");
	    
	    By ChildOrganizationRoleSuccessMessageBoxLocator = By.id("messagebox-1001");  
	    By ChildOrganizationRoleOkButtonLocator = By.id("button-1005-btnIconEl");
	    
	    public void ClickAddSubOrganization() {

	    	safeClick(ChildOrganizationNewLinkLocator);
	        Reporter.log("after Click Add Sub Organization ");
		
	        Assert.assertTrue(webDriver.getPageSource().contains(""));
	    }
	    public void SelectRole(int option) {
	    	int count = 0;
	    	 List<WebElement> elementsList = webDriver.findElements(ChildOrganizationClassLocator);
	            for (WebElement element: elementsList) {
	                Reporter.log("count"+count);
	            	if (count == option) {
	            		element.click();
	            		Reporter.log("inside loop if");
	                }
	            	count++;
	            }
	    }
	    public void DeleteSubOrganization(int option) {
	    	
	    	SelectRole(option);
	    	safeClick(ChildOrganizationDeleteLinkLocator); 
	    	safeClick(ChildOrganizationYesButtonLocator);
	    	Reporter.log("after Delete Sub Organization");
    }
}
