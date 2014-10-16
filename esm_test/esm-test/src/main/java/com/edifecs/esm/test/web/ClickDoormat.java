package com.edifecs.esm.test.web;

import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.DriverBase;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.edifecs.test.common.WaitForAction;

public class ClickDoormat extends ActionDriver {

	WebDriver webDriver;
	String value;
	 public ClickDoormat(WebDriver webDriver) {
	     super(webDriver);   
		 this.webDriver = webDriver;
	    }
	
	  By DoormatButtonLocator = By.id("doormatId-btnIconEl");
	  By DoormatManageMySiteLinkLocator = By.linkText("Manage Sites");
	  By DoormatManageMyTenantLinkLocator = By.linkText("Manage My Tenant");
	  By DoormatManageMyOrganizationLinkLocator = By.linkText("Manage My Organization");
	  //By siteOverviewTextLocator = By.xpath("//div[@id='component-1011']");

	  By siteOverviewNameInputLocator = By.id("siteOverview-canonicalName-inputEl");
	  By tenantOverviewNameInputLocator = By.id("tenantOverview-canonicalName-inputEl");
	  By OrganizationOverviewNameInputLocator = By.id("organizationOverview-canonicalName-inputEl");

        public boolean idDormatButtonPresent() {

            WebDriverWait wait = new WebDriverWait(webDriver, 20);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(DoormatButtonLocator));
            return (true);
        }

	    public void clickManageMySite(){
	        
	    	safeClick(DoormatButtonLocator);
	        safeClick(DoormatManageMySiteLinkLocator);
	        
	    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(siteOverviewNameInputLocator));
	        Reporter.log("after clickManageMySite ");
			Assert.assertTrue(webDriver.getPageSource().contains("Site Overview"));
	    }

	    public void clickManageMyTenant(){
	    	
	    	safeClick(DoormatButtonLocator);
	    	safeClick(DoormatManageMyTenantLinkLocator);
	        
			Reporter.log("after clickManageMyTenants ");
			
	    	//WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        //WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(tenantOverviewNameInputLocator));
	       //Assert.assertTrue(webDriver.getPageSource().contains("Tenant Overview"));
	    }

	    public void clickManageMyOrganization(){
	    	
	    	safeClick(DoormatButtonLocator);
	    	safeClick(DoormatManageMyOrganizationLinkLocator);
	        
	    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(OrganizationOverviewNameInputLocator));
	        
			Reporter.log("after clickManageMyOrganization ");
			Assert.assertTrue(webDriver.getPageSource().contains("Organization Overview"));
	    }
}
