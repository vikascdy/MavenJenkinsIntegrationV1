package com.edifecs.esm.test.web;


import java.util.List;

import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.WaitForAction;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * Created by martholl on 5/15/2014.
 */
public class XBoardHeader extends ActionDriver{

    WebDriver webdriver;

    public XBoardHeader(WebDriver webDriver) {
        super(webDriver);
    	this.webdriver = webDriver;
    }

    By xBoardButtonLocator = By.xpath("//*[@id='userId-btnIconEl']");
    By xBoardUserAccounts =  By.linkText("Admin Admin");
    By xBoardAbout = By.linkText("About");
    By xBoardLogout = By.linkText("Logout");
    
    public void ClickUserButton(){
		safeClick(xBoardButtonLocator);
		
	}

    public void ClickUserAccounts(){
    	safeClick(xBoardButtonLocator);
		safeClick(xBoardUserAccounts);
	}

	public void ClickAbout(){
		safeClick(xBoardButtonLocator);
		safeClick(xBoardAbout);
	}

	public void ClickLogout(){
		safeClick(xBoardButtonLocator);
		safeClick(xBoardLogout);
	}
}
