package com.edifecs.esm.test.web;

import com.edifecs.test.common.ActionDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by martholl on 9/4/2014.
 */
public class EsmManagementPage extends ActionDriver {

    WebDriver webDriver;
    String value;
    public EsmManagementPage(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = webDriver;
    }

    By SiteOverviewLinkLocator = By.linkText("Site Overview");

    public void clickSiteOverview() {

        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(SiteOverviewLinkLocator));
        safeClick(SiteOverviewLinkLocator);
    }

}
