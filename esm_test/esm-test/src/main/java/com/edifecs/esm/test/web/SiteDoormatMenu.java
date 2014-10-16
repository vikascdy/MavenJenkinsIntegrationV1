package com.edifecs.esm.test.web;


import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * Created by c-moharama on 6/16/2014.
 */
public class SiteDoormatMenu extends ActionDriver{

    WebDriver webDriver;
    String value;
    public SiteDoormatMenu(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = webDriver;
    }

    By doormatButtonLocator = By.id("doormatId-btnIconEl");
    By doormatManageMySiteLinkLocator = By.linkText("Manage Sites");
    By doormatManageMyTenantLinkLocator = By.linkText("Manage My Tenant");
    By doormatManageMyOrganizationLinkLocator = By.linkText("Manage My Organization");
    By siteOverviewTextLocator = By.xpath("//div[@id='component-1011']");

    public void clickManageMySite(){
        safeClick(doormatButtonLocator);
    	safeClick(doormatManageMySiteLinkLocator);    	
    	WaitForAction.Sleep(LONG_SLEEP);
        Assert.assertTrue(webDriver.findElement(By.id("siteOverview-canonicalName-inputEl")).isDisplayed());
    	Reporter.log("after click Manage My Site");
    }


    public void clickManageMyTenant(){
    	safeClick(doormatButtonLocator);
    	safeClick(doormatManageMyTenantLinkLocator);
    	WaitForAction.Sleep(MID_SLEEP);
        Assert.assertTrue(webDriver.findElement(By.id("tenantOverview-canonicalName-inputEl")).isDisplayed());
    	Reporter.log("after click Manage My Tenant");
    }

    public void clickManageMyOrganization(){
    	safeClick(doormatButtonLocator);
    	safeClick(doormatManageMyOrganizationLinkLocator);
    	WaitForAction.Sleep(MID_SLEEP);
        Assert.assertTrue(webDriver.findElement(By.id("organizationOverview-canonicalName-inputEl")).isDisplayed());
    	Reporter.log("after click Manage My Organization");
    }
}
