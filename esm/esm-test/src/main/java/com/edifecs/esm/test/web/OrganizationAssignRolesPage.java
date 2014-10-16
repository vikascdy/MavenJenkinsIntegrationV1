package com.edifecs.esm.test.web;

import java.util.List;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;


public class OrganizationAssignRolesPage extends ActionDriver{

	    WebDriver webDriver;

	    public OrganizationAssignRolesPage(WebDriver webDriver) {
	        super(webDriver);
	    	this.webDriver = webDriver;
	    }
	    By OrganizationRolesNewLinkLocator = By.id("assignOrganizationRoles-link");
	    By OrganizationRolesClassLocator = By.className("x-grid-row-checker");
	    By OrganizationRolesDeleteLinkLocator = By.id("removeOrganizationRoles-link");
	    By OrganizationRolesDeleteAckLocator = By.linkText("Yes");
	    
	    public void ClickAddRole() {

	    	safeClick(OrganizationRolesNewLinkLocator);
	        Reporter.log("after ClickAddRole ");
		
	    }
	    public void SelectRole(int option) {
	    	int count = 0;
	    	 List<WebElement> elementsList = webDriver.findElements(OrganizationRolesClassLocator);
	            for (WebElement element: elementsList) {
	            	Reporter.log("count"+count);
	            	if (count == option) {
	            		element.click();
	            		Reporter.log("inside loop if");
	                }
	            	count++;
	            }
	    }
	    public void DeleteRole(int option) {
	    	
	    	SelectRole(option);
	    	safeClick(OrganizationRolesDeleteLinkLocator); 
	    	safeClick(OrganizationRolesDeleteAckLocator);
	    	Reporter.log("after delete role");
       }
	}

