package com.edifecs.esm.test.web;


import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;

import com.edifecs.test.common.HtmlReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

/**
 * Created by martholl on 5/14/2014.
 */
public class LoginPage extends ActionDriver {

    private WebDriver webDriver;
    private int SHORT_SLEEP = 5000;

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = webDriver;
        //check if we are login page or the right page
        if (!"Edifecs® Security Manager".contains(webDriver.getTitle())) {
            throw new IllegalStateException("This is not the login page");
        }

        // Look for Domain label to ensure this is the login page and not a random ESM page
        if(!webDriver.findElement(domainLabelLocator).isDisplayed()) {
            throw new IllegalStateException("This is not the login page");
        }
    }


    // site html elements will be represented as WebElements and will be define only once
    // Please refrain from using xPath as it is not supported by all the browsers and is  frequently failing test cases 
    By domainLocator = By.id("domain-inputEl");
    By organizationLocator = By.id("organization-inputEl");
    By usernameLocator = By.id("username-inputEl");
    By passwordLocator = By.id("password-inputEl");
    By loginButtonLocator = By.id("login-button");
    By siteLinkLocator = By.linkText("Manage Sites");
    By DoormatButtonLocator = By.id("doormatId-btnIconEl");
    By domainLabelLocator = By.id("domain-labelEl");
    By loginErrorMessageLabelLocator = By.id("login-message");
    public void login(String tenant, String organization, String username, String password ) {

        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        wait.until(ExpectedConditions.presenceOfElementLocated(domainLocator));
        safeSendKey(domainLocator, tenant);
        safeSendKey(organizationLocator, organization);
        safeSendKey(usernameLocator, username);
        safeSendKey(passwordLocator, password);
        safeClick(loginButtonLocator);
        WaitForAction.Sleep(MID_SLEEP);
        safeClick(DoormatButtonLocator);
        safeClick(siteLinkLocator);
        HtmlReporter.log("after Login");
    }

    public boolean isLoginSuccessful(boolean successHint){

        WebDriverWait longWait = new WebDriverWait(webDriver, 20);
        WebDriverWait shortWait = new WebDriverWait(webDriver, 1);
        WaitForAction.Sleep(SHORT_SLEEP);

        if(successHint) {
            if (IsDoormatPresent(longWait)) {
                return true;
            } else {
                return !IsErrorLabelPresent(shortWait);
            }
        }
        else {
            if (IsErrorLabelPresent(longWait)) {
                return false;
            } else {
                return IsDoormatPresent(shortWait);
            }
        }

    }

    private boolean IsDoormatPresent(WebDriverWait wait){

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(DoormatButtonLocator));
            return true;
        }
        catch(org.openqa.selenium.TimeoutException err){
            return false;
        }
    }
    private boolean IsErrorLabelPresent(WebDriverWait wait){

        WebElement loginErrorMessageLabel;
        try {
            loginErrorMessageLabel = wait.until(ExpectedConditions.presenceOfElementLocated(loginErrorMessageLabelLocator));
            HtmlReporter.log("Found Login Error Message: " + loginErrorMessageLabel.getText());
            return true;
        }
        catch(org.openqa.selenium.TimeoutException err){
            return false;
        }
    }

    public boolean isLogOutSuccessful(){
        return webDriver.findElement(domainLabelLocator).isDisplayed();
    }

}
