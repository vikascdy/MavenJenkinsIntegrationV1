package com.edifecs.esm.test.web;

/**
 * Created by c-moharama on 6/17/2014.
 */


import junit.framework.Assert;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

public class SiteConfigurationMenu extends ActionDriver {

    private WebDriver webDriver;

    public SiteConfigurationMenu(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = webDriver;
//        if (!"http://smbox:8080/esm/#!/Site/Overview".contains(webDriver.getCurrentUrl())) {
//            throw new IllegalStateException("This is not the site overview page");
//        }
    }


    By siteOverviewLinkLocator = By.xpath("//tr[contains(@id,'overview')]/td/div/span");
    By manageTenantLinkLocator = By.xpath("//tr[contains(@id,'manageTenants')]/td/div/span");
    By siteTenantsNewTenantLinkLocator = By.id("newtenant-link");

    public void clickSiteOverview() {
        
    	safeClick(siteOverviewLinkLocator);
        Reporter.log("after click Site Overview ");
	 }

    public void clickManageTenant() {
        
    	WaitForAction.Sleep(sleepForPageLoad);
    	safeClick(manageTenantLinkLocator);
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(siteTenantsNewTenantLinkLocator));
        Reporter.log("after click Manage Tenant ");

    }
}


