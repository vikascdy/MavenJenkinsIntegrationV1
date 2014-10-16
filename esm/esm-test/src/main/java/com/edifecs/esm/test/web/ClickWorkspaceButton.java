package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.WaitForAction;

public class ClickWorkspaceButton extends ActionDriver{

	WebDriver webDriver;
	
	 public ClickWorkspaceButton(WebDriver webDriver) {
	     
		 super(webDriver);
		 this.webDriver = webDriver;
	  }
	 	
	 	By WorkspaceButtonLocator = By.xpath("//*[@id='workspaceId-btnIconEl']");
	    By WorkspaceManageMySiteLinkLocator = By.linkText("Manage Sites");
	    By WorkspaceManageMyTenantLinkLocator = By.linkText("Manage My Tenant");
	    By WorkspaceManageMyOrganizationLinkLocator = By.linkText("Manage My Organization");
	    By siteOverviewTextLocator = By.xpath("//div[@id='component-1011']");

	    By siteOverviewNameInputLocator = By.id("siteOverview-canonicalName-inputEl");
		By tenantOverviewNameInputLocator = By.id("tenantOverview-canonicalName-inputEl");
		By OrganizationOverviewNameInputLocator = By.id("organizationOverview-canonicalName-inputEl");
		  
	    public void clickWorkspaceButton(){
	        safeClick(WorkspaceButtonLocator);
	    }

	    public void clickManageMySite(){
	    	safeClick(WorkspaceButtonLocator);
	    	safeClick(WorkspaceManageMySiteLinkLocator);
	        
	    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(siteOverviewNameInputLocator));
	        Assert.assertTrue(webDriver.getPageSource().contains("Site Overview"));
	    }

	    public void clickManageMyTenant(){
	    	safeClick(WorkspaceButtonLocator);
	    	safeClick(WorkspaceManageMyTenantLinkLocator);
	        
	    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(tenantOverviewNameInputLocator));
	        Assert.assertTrue(webDriver.getPageSource().contains("Tenant Overview"));
	    }

	    public void clickManageMyOrganization(){
	    	safeClick(WorkspaceButtonLocator);
	    	safeClick(WorkspaceManageMyOrganizationLinkLocator);
	        
	    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(OrganizationOverviewNameInputLocator));
	        Assert.assertTrue(webDriver.getPageSource().contains("Organization Overview"));
	    }

}
