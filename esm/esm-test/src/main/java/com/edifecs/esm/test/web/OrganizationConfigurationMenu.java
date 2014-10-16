package com.edifecs.esm.test.web;

import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.WaitForAction;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by martholl on 5/21/2014.
 */
public class OrganizationConfigurationMenu extends ActionDriver{

    private WebDriver webdriver;

    public OrganizationConfigurationMenu(WebDriver webDriver) {
        super(webDriver);
    	this.webdriver = webDriver;
    }

    By overviewLocator = By.xpath("//tr[contains(@id,'overview')]");
    By SubOrganizationsLocator = By.xpath("//tr[contains(@id,'manageSubOrganizations')]");
    By AssignRolesLocator = By.xpath("//tr[contains(@id,'assignRoles')]");
    By manageUserLocator = By.xpath("//tr[contains(@id,'manageUsers')]");
    
     public void ClickOrganizationOverview() {
    
    	safeClick(overviewLocator);
    	Reporter.log("after ClickOrganizationOverview ");
		
    }
    public void ClickManageSubOrganizations() {

    	safeClick(SubOrganizationsLocator);
    	Reporter.log("after ClickManageSubOrganizations ");
		
    }
    public void ClickAssignRoles() {

    	safeClick(AssignRolesLocator);
    	Reporter.log("after ClickAssignRoles  ");
		
    }
    public void ClickManageUsers() {
    	
    	safeClick(manageUserLocator);
    	Reporter.log("after ClickManageUsers ");
	}

}
