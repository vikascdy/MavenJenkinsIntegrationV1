package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;


public class TenantOverviewPage extends ActionDriver{

	 private WebDriver webDriver;

	     public TenantOverviewPage(WebDriver webDriver) {
	        super(webDriver);
	    	 this.webDriver = webDriver;
	    }
	     By tenantOverviewLinkLocator = By.xpath("//*[@id='treeview-1313-record-overview']/td/div/span");
	     By BackToSiteLinkLocator = By.id("siteOverview-link");
	     public void ClickBackToSitePage(){
	    	safeClick(BackToSiteLinkLocator); 
	    	Reporter.log("after Click Back To Site Page");
	     }
	     
	     By tenantOverviewNameInputLocator = By.id("tenantOverview-canonicalName-inputEl");
	     By tenantOverviewDescriptionInputLocator = By.id("tenantOverview-description-inputEl");
	     By tenantOverviewDomainInputLocator = By.id("tenantOverview-domain-inputEl");
	     By tenantOverviewSaveButtonLocator = By.id("tenantOverview-saveTenant-btnIconEl");
	     
	     By tenantOverviewSuccessMessageBoxLocator = By.id("messagebox-1001");  
	     By tenantOverviewOkButtonLocator = By.id("button-1005-btnIconEl");
	     
	    public void UpdateOverview(String name, String description, String domain) {

	    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(tenantOverviewNameInputLocator));
	        
	        safeSendKey(tenantOverviewNameInputLocator, name);
	        safeSendKey(tenantOverviewDescriptionInputLocator, description);
	        safeSendKey(tenantOverviewDomainInputLocator, domain);
	        safeClick(tenantOverviewSaveButtonLocator);
	        
	        WebElement webElement = webDriver.findElement(tenantOverviewSuccessMessageBoxLocator);
	        if (webElement != null) {
	            safeClick(tenantOverviewOkButtonLocator);

	        } else {
	            // Using the TestNG API for logging
	            Reporter.log("Element: " + tenantOverviewSuccessMessageBoxLocator + ", is not available on page - "
	                    + webDriver.getCurrentUrl());
	        }
	        Reporter.log("after Update Overview");
	    }
}
