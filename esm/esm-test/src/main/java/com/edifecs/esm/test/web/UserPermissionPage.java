package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.edifecs.test.common.ActionDriver;

public class UserPermissionPage extends ActionDriver{

	WebDriver webDriver;
	
	public UserPermissionPage(WebDriver webDriver){
		super(webDriver);
		this.webDriver = webDriver;
	}
	
	By clickRefreshButtonLocator = By.linkText("Refresh");
	public void clickRefreshButton(){
		safeClick(clickRefreshButtonLocator);
	}
}
