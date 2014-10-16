package com.edifecs.esm.test.web;



import com.edifecs.test.common.ActionDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

/**
 * Created by c-moharama on 6/17/2014.
 */
public class SiteOverviewPage extends ActionDriver {

    private WebDriver webDriver;

    public SiteOverviewPage(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = webDriver;
    }

    By siteOverviewLinkLocator = By.xpath("//*[@id='treeview-1020-record-siteOverview']/td/div/span");
    By siteOverviewNameInputLocator = By.id("siteOverview-canonicalName-inputEl");
    By siteOverviewAddDescriptionInputLocator = By.id("siteOverview-description-inputEl");
    By siteOverviewSaveButtonLocator = By.id("siteOverview-updateSite-btnIconEl");
    By siteOverviewSuccessMessageBoxLocator = By.id("messagebox-1001");
    By siteOverviewOkButtonLocator = By.id("button-1005-btnIconEl");

    public void updateSiteOverview(String name, String description) {
       
       WebDriverWait wait = new WebDriverWait(webDriver, 20);
       WebElement element = wait.until(ExpectedConditions.elementToBeClickable(siteOverviewNameInputLocator));
       safeClick(siteOverviewLinkLocator);
        
        safeSendKey(siteOverviewNameInputLocator, name);
        safeSendKey(siteOverviewAddDescriptionInputLocator, description);
        safeClick(siteOverviewSaveButtonLocator);
        
        WebElement webElement = webDriver.findElement(siteOverviewSuccessMessageBoxLocator);
        if (webElement != null) {
            safeClick(siteOverviewOkButtonLocator);

        } else {
            // Using the TestNG API for logging
            Reporter.log("Element: " + siteOverviewSuccessMessageBoxLocator + ", is not available on page - "
                    + webDriver.getCurrentUrl());
        }
        Reporter.log("after update Site Overview");
    }
}
