package com.edifecs.esm.test.web;


import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.WaitForAction;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

/**
 * Created by martholl on 5/15/2014.
 */
public class OrganizationOverviewPage extends ActionDriver{
    WebDriver webDriver;

    public OrganizationOverviewPage(WebDriver webDriver) {
        super(webDriver);
    	this.webDriver = webDriver;
    }

    By BackToTenantsLinkLocator = By.id("tenantOverview-link");
    
    By OrganizationOverviewNameInputLocator = By.id("organizationOverview-canonicalName-inputEl");
    By OrganizationOverviewDescriptionInputLocator = By.id("organizationOverview-description-inputEl");
    By OrganizationOverviewSaveButtonLocator = By.id("organizationOverview-saveOrganization-btnIconEl");
    
    public void ClickBackToTenantPage() {
        safeClick(BackToTenantsLinkLocator);
        Reporter.log("after ClickBackToTenantPage");
    }
    
    By organizationOKLocator = By.linkText("OK");
    public void UpdateOrganizationOverview(String name, String description){
    	
    	safeSendKey(OrganizationOverviewNameInputLocator, name);
    	safeSendKey(OrganizationOverviewDescriptionInputLocator, description);
    	safeClick(OrganizationOverviewSaveButtonLocator);
    	safeClick(organizationOKLocator);
    	Reporter.log("after UpdateOrganizationOverview");
    }

    public void UpdateAllOrganizationOverview(String name, String description){
    	
    	safeSendKey(OrganizationOverviewNameInputLocator, name);
    	safeSendKey(OrganizationOverviewDescriptionInputLocator, description);
    	Reporter.log("after UpdateAllOrganizationOverview");
    }
    	
    public void clickOrganizationOverviewSaveButton(){
    	
    	safeClick(OrganizationOverviewSaveButtonLocator);
    	Reporter.log("after clickOrganizationOverviewSaveButton");
    }
}
