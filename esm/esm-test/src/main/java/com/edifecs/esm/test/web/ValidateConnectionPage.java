package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

public class ValidateConnectionPage extends ActionDriver{

	WebDriver webDriver;
	public ValidateConnectionPage(WebDriver webDriver){
		
		super(webDriver);
		this.webDriver = webDriver;
	}

	By connectionUsernameInputLocator = By.id("testConnection-username-inputEl");
	By connectionPasswordInputLocator = By.id("testConnection-password-inputEl");
	By connectionSaveButtonLocator = By.id("testConnection-saveRealmChanges-btnIconEl");
	By connectionCloseButtonLocator = By.id("testConnection-closeButton-btnIconEl");
	public void validateConnection(String username, String password){
    
		safeSendKey(connectionUsernameInputLocator, username);
		safeSendKey(connectionPasswordInputLocator, password);
		
		safeClick(connectionSaveButtonLocator);
		Reporter.log("after validate connnection");
	}
	public void closeTestAndSaveDialog(){
		
		safeClick(connectionCloseButtonLocator);
	}
	    
}
