package com.edifecs.esm.test.web;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

public class TenantOrganizationRealm extends ActionDriver{

	WebDriver webDriver;
	public TenantOrganizationRealm(WebDriver webDriver){
		super(webDriver);
		this.webDriver = webDriver;
	}
	
	By realmAuthenticationTypeLocator = By.id("realm-authenticationType-inputEl");
	By authTypeClassLocator = By.className("x-boundlist-item");
	
	public void selectUserType(String option) {

        safeSelectAndClick(realmAuthenticationTypeLocator);

        List<WebElement> elements = webDriver.findElements(authTypeClassLocator);
        for(WebElement webelement : elements){
            if(webelement.getText().equals(option)){
                webelement.click();
            }
        }
        Reporter.log("after select user type");
    }
	
	By addNewPropertyNameInputLocator = By.id("realmConfigProperties-propertyName-inputEl");
	By addButtonLocator = By.id("addNewProp-btnIconEl");	
	public void clickAddPropertyButton(){
    
		safeClick(addButtonLocator);
		WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(addNewPropertyNameInputLocator));
        Reporter.log("after click add property button ");
	}
	public void selectPropertyFromList(String Property){
    
		By propertyLocator = By.id("realmPropertyName-"+Property);
		safeClick(propertyLocator);
		Reporter.log("after select Property From List");
	}
	By deleteButtonLocator = By.id("deleteProp-btnIconEl");
	public void clickDeletePropertyButton(String Property){
    
		selectPropertyFromList(Property);
		safeClick(deleteButtonLocator);
		Reporter.log("after click Delete Property Button");
	}
	
	By testAndSaveButtonLocator = By.id("testConnection-btnIconEl");
	public void clickTestAndSaveRealmButton(){
    
		safeClick(testAndSaveButtonLocator);
	}
	
	public void clickBuiltInUserDatabase(){
    	selectUserType("Built-in User Database");
    }
    
    By defaultAuthPropertyURL = By.id("realmPropertyValue-URL");
    By defaultAuthPropertyGroupFilter = By.id("realmPropertyValue-Group Filter");
    By defaultAuthPropertyUserFilter = By.id("realmPropertyValue-User Filter");
    By defaultAuthPropertyUserSearchBase = By.id("realmPropertyValue-User Search Base");    
    By defaultAuthPropertyGroupSearchBase = By.id("realmPropertyValue-Group Search Base");    
    
    By updateAuthPropertyURL = By.id("realmPropertyValueEdit-URL-inputEl");
    By updateAuthPropertyGroupFilter = By.id("realmPropertyValueEdit-Group Filter-inputEl");
    By updateAuthPropertyUserFilter = By.id("realmPropertyValueEdit-User Filter-inputEl");
    By updateAuthPropertyUserSearchBase = By.id("realmPropertyValueEdit-User Search Base-inputEl");    
    By updateAuthPropertyGroupSearchBase = By.id("realmPropertyValueEdit-Group Search Base-inputEl");    
    public void LDAPServer(String URL,String groupFilter,String userFilter,String userSearchBase,String groupSearchBase){
    	selectUserType("LDAP Server");
    	
    	safeClick(defaultAuthPropertyURL);
    	safeSendKey(updateAuthPropertyURL, URL);
    	
    	safeClick(defaultAuthPropertyGroupFilter);
    	safeSendKey(updateAuthPropertyGroupFilter, groupFilter);
    	
    	safeClick(defaultAuthPropertyGroupSearchBase);
    	safeSendKey(updateAuthPropertyGroupSearchBase, groupSearchBase);

    	safeClick(defaultAuthPropertyUserFilter);
    	safeSendKey(updateAuthPropertyUserFilter, userFilter);
    	
    	safeClick(defaultAuthPropertyUserSearchBase);
    	safeSendKey(updateAuthPropertyUserSearchBase, userSearchBase);
    	
    }
    
    By defaultAuthPropertyADSURL = By.id("realmPropertyValue-URL");
    By defaultAuthPropertyADSUser = By.id("realmPropertyValue-System User");
    By defaultAuthPropertyADSPassword = By.id("realmPropertyValue-System Password");
    By defaultAuthPropertyADSUserFilter = By.id("realmPropertyValue-User Filter");
    By defaultAuthPropertyADSUserSearchBase = By.id("realmPropertyValue-User Search Base");
    
    By updateAuthPropertyADSURL = By.id("realmPropertyValueEdit-URL-inputEl");
    By updateAuthPropertyADSUser = By.id("realmPropertyValueEdit-System User-inputEl");
    By updateAuthPropertyADSPassword = By.id("realmPropertyValueEdit-System Password-inputEl");
    By updateAuthPropertyADSUserFilter = By.id("realmPropertyValueEdit-User Filter-inputEl");
    By updateAuthPropertyADSUserSearchBase = By.id("realmPropertyValueEdit-User Search Base-inputEl");
    public void ActiveDirectoryServer(String URL,String user,String password,String userFilter,String userSearchBase){
    	selectUserType("Active Directory Server");
    
    	safeClick(defaultAuthPropertyADSURL);
    	safeSendKey(updateAuthPropertyADSURL, URL);
    	
    	safeClick(defaultAuthPropertyADSUser);
    	safeSendKey(updateAuthPropertyADSUser, user);

    	// the password field is not working for now and need to be checked
    	//safeClick(defaultAuthPropertyADSPassword);
    	//safeSendKey(updateAuthPropertyADSPassword, password);

    	safeClick(defaultAuthPropertyADSUserFilter);
    	safeSendKey(updateAuthPropertyADSUserFilter, userFilter);

    	safeClick(defaultAuthPropertyADSUserSearchBase);
    	safeSendKey(updateAuthPropertyADSUserSearchBase, userSearchBase);
    	
    }
}
