package com.edifecs.esm.test.web;

import com.edifecs.test.common.WaitForAction;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

import java.util.List;

/**
 * Created by martholl on 5/21/2014.
 */
public class TenantConfigurationMenu extends ActionDriver {

    private WebDriver webDriver;

    public TenantConfigurationMenu(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = webDriver;
    }

    By overviewLocator = By.xpath("//tr[contains(@id,'overview')]");
    By manageOrganizationsLocator = By.xpath("//tr[contains(@id,'manageOrganizations')]");
    By manageRolesLocator = By.xpath("//tr[contains(@id,'manageRoles')]");
    By manageGroupsLocator = By.xpath("//tr[contains(@id,'manageGroups')]");
    By settingsConfigLocator = By.xpath("//tr[contains(@id,'settingsConfig')]");
    By appsLocator = By.xpath("//tr[contains(@id,'apps')]");
    
    
    By PasswordAge = By.id("managePasswordPolicy-passwdAge-inputEl");    
    By tenantOverviewNameInputLocator = By.id("tenantOverview-canonicalName-inputEl");
    By tenantOrganizationNewOrganizationLinkLocator = By.id("newtenantOrganization-link");
    By tenantNewUserGroupLinkLocator = By.id("newtenantUserGroup-link");
    By tenantNewRoleLinkLocator = By.id("newtenantRole-link");
    

    public void ClickOverview() {
        
    	safeClick(overviewLocator);
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement webelement = wait.until(ExpectedConditions.visibilityOfElementLocated(tenantOverviewNameInputLocator));
        
		Reporter.log("after Click Overview ");
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant Overview"));        
    }

    public void ClickApps() {
        
    	safeClick(appsLocator);
        Reporter.log("after Click Apps ");
		Assert.assertTrue(webDriver.getPageSource().contains("Apps"));        
    }
    public void ClickManageOrganizations() {
    	
    	Reporter.log("enter ManageOrganizations");    	
    	
    	safeClick(manageOrganizationsLocator);
    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement webelement = wait.until(ExpectedConditions.elementToBeClickable(tenantOrganizationNewOrganizationLinkLocator));
        
		Reporter.log("after Click Manage Organization ");
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant Organization"));
    }

    public void ClickManageRoles() {
    	Reporter.log("enter ManageRoles");
    	
    	safeClick(manageRolesLocator);
    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement webelement = wait.until(ExpectedConditions.visibilityOfElementLocated(tenantNewRoleLinkLocator));
        
		Reporter.log("after Click Manage Roles ");
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant Roles"));
    }

    public void ClickManageGroups() {
        
    	safeClick(manageGroupsLocator);
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement webelement = wait.until(ExpectedConditions.elementToBeClickable(tenantNewUserGroupLinkLocator));
        
		Reporter.log("after Click Manage Groups ");
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant User Group"));
    }

    public void ClickSettings() {
        
    	safeClick(settingsConfigLocator);
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement webelement = wait.until(ExpectedConditions.elementToBeClickable(PasswordAge));
        
		Reporter.log("after Click Settings ");
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant Logo"));
    }
}
