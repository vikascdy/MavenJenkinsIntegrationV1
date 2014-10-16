package com.edifecs.esm.test.web;

import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.HtmlReporter;
import com.edifecs.test.common.WaitForAction;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import java.util.List;

/**
 * Created by martholl on 5/15/2014.
 */
public class CreateTenantOrganizationPage extends ActionDriver{

    WebDriver webDriver;

    public CreateTenantOrganizationPage(WebDriver webDriver) {
        super(webDriver);
    	this.webDriver = webDriver;
    }
    
    By createTenantNameInputLocator = By.id("createOrganization-canonicalName-inputEl");
    By createTenantDescriptionInputLocator = By.id("createOrganization-description-inputEl");
    By createTenantButtonLocator = By.id("createOrganization-createOrganization-btnIconEl");    
    By successMessageBoxOkButtonLocator = By.id("button-1005");      
    By backToOrganizationsListLinkLocator = By.id("manageTenantOrganizations-link");
    By organizationListLocator = By.xpath("//a[starts-with(@id, 'tenantOrganizationDetail')]");

    
    public void clickBackToOrganizationsList(){
    	
    	safeClick(backToOrganizationsListLinkLocator);
        HtmlReporter.log("after clickBackToOrganizationsList", true);
		
    }
    public void CreateNewOrganization(String name, String description) {
    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(createTenantNameInputLocator));
        
    	safeSendKey(createTenantNameInputLocator, name);
    	safeSendKey(createTenantDescriptionInputLocator, description);
        
    	safeClick(createTenantButtonLocator);

        HtmlReporter.log("after createNewOrganization", true);
		safeClick(successMessageBoxOkButtonLocator);
    }
    public boolean isCreateTenantOrganizationSuccessful(String name){

        List<WebElement> tableList = webDriver.findElements(organizationListLocator);

        for(int index = 0; index < tableList.size(); index++){
            HtmlReporter.log(tableList.get(index).getText());
            if(tableList.get(index).getText().equals(name)){
                return true;
            }
        }

        HtmlReporter.log("Could not find organization '" + name + "'in list of organizations.", true);
        return false;


    }
}
