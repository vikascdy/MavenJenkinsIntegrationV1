package com.edifecs.esm.test.web;

import java.util.List;

import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.WaitForAction;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

public class CreateOrganizationUserPage extends ActionDriver {

    private WebDriver webDriver;

    public CreateOrganizationUserPage(WebDriver webdriver) {
        super(webdriver);
        this.webDriver = webdriver;
    }

    By backToOrganizationUserListLinkLocator = By.id("organizationUsers-link");
    public void clickBackToOrganizationUserList(){
    	safeClick(backToOrganizationUserListLinkLocator);
    	
    }
    By typeOfUserLocator = By.id("createOrganizationUsers-typeOfUser-inputEl");
    By userNameLocator = By.id("createOrganizationUsers-username-inputEl");
    By passwordLocator = By.id("userPassword-inputEl");
    By password2Locator = By.id("userPassword2-inputEl");
    By firstNameLocator = By.id("createOrganizationUsers-firstName-inputEl");
    By middleNameLocator = By.id("createOrganizationUsers-middleName-inputEl");
    By lastNameLocator = By.id("createOrganizationUsers-lastName-inputEl");
    By titleLocator = By.id("createOrganizationUsers-salutation-inputEl");
    By emailLocator = By.id("createOrganizationUsers-emailAddress-inputEl");
    By saveButtonLocator = By.id("createOrganizationUsers-createUser");

    By domainLocator = By.id("createOrganizationUsers-domain-inputEl");
    By certificateLocator = By.id("createOrganizationUsers-certificate-inputEl");

    // This needs to be updated, but for now it seems okay
    By createUserSuccessMessageBoxLocator = By.id("messagebox-1001");  
    By messageBoxOKLocator = By.linkText("OK");

    public void SelectUserType(String option) {

        safeSelectAndClick(typeOfUserLocator);

        List<WebElement> elements = webDriver.findElements(By.className("x-boundlist-item"));
        for(WebElement webelement : elements){
            if(webelement.getText().equals(option)){
                webelement.click();
            }
        }
    }

    public void CreateStandardUser(String username,String password,String confirmPassword,String fName,
                                   String mName,String lName,String title,String email ){

        SelectUserType("User");
        safeSendKey(userNameLocator, username);
        safeSendKey(passwordLocator, password);
        safeSendKey(password2Locator, confirmPassword);
        safeSendKey(firstNameLocator, fName);
        safeSendKey(middleNameLocator, mName);
        safeSendKey(lastNameLocator, lName);
        safeSendKey(titleLocator, title);
        safeSendKey(emailLocator, email);
        safeSelectAndClick(saveButtonLocator);
        WaitForAction.Sleep(2000);
        WebElement webElement = webDriver.findElement(createUserSuccessMessageBoxLocator);
        if (webElement != null) {
            safeClick(messageBoxOKLocator);

        } else {
            // Using the TestNG API for logging
            Reporter.log("Element: " + createUserSuccessMessageBoxLocator + ", is not available on page - "
                    + webDriver.getCurrentUrl());
        }
     }

    public void UserWithCertificates(String domain,String certificate,
                                     String fName,String mName,String lName,String title,String email){
        // This has not been tested - but it should work
        SelectUserType("System User with Certificate");

        safeSendKey(domainLocator, domain);
        safeSendKey(certificateLocator, certificate);
        safeSendKey(firstNameLocator, fName);
        safeSendKey(middleNameLocator, mName);
        safeSendKey(lastNameLocator, lName);
        safeSendKey(titleLocator, title);
        safeSendKey(emailLocator, email);
        safeSelectAndClick(saveButtonLocator);
        
        WebElement webElement = webDriver.findElement(createUserSuccessMessageBoxLocator);
        if (webElement != null) {
            safeClick(messageBoxOKLocator);

        } else {
            // Using the TestNG API for logging
            Reporter.log("Element: " + createUserSuccessMessageBoxLocator + ", is not available on page - "
                    + webDriver.getCurrentUrl());
        }
    }

    public void LDAPUser(String Domain, String Username){

        // Not yet implemented as I do not have UI for LDAP User Creation
    }

}
