package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.edifecs.test.common.ActionDriver;

public class UserSettingsPage extends ActionDriver{

	WebDriver webDriver;
	public UserSettingsPage(WebDriver webDriver){
		
		super(webDriver);
		this.webDriver = webDriver;
	}
	
	By newPasswordLocator = By.id("userPassword-inputEl");
	By confirmPasswordLocator = By.id("userPassword2-inputEl");
	By clickUpdateButtonLocator = By.linkText("Update");
	By OKButtonLocator = By.linkText("OK");
	public void changePassword(String newPassword, String confirmPassword){
		
		safeSendKey( newPasswordLocator, newPassword);
		safeSendKey(confirmPasswordLocator, confirmPassword);
		safeClick(clickUpdateButtonLocator);
		safeClick(OKButtonLocator);
	}
}
