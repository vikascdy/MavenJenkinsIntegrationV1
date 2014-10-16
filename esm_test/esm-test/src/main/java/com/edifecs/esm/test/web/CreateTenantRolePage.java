package com.edifecs.esm.test.web;

import com.edifecs.test.common.ActionDriver;

import com.edifecs.test.common.HtmlReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;

import java.util.List;


public class CreateTenantRolePage extends ActionDriver {

	 private WebDriver webDriver;

	    public CreateTenantRolePage(WebDriver webDriver) {
	        super(webDriver);
            this.webDriver = webDriver;
	    }
	    By backToTenantRoleListLinkLocator = By.id("tenantRoles-link");
	    public void clickBackToTenantRoleList(){
	    	
	    	safeClick(backToTenantRoleListLinkLocator);
	    	Reporter.log("after clickBackToTenantRoleList");
	    }
	    By createRoleNameInputLocator = By.id("createTenantRole-canonicalName-inputEl");
	    By createRoleDescriptionInputLocator = By.id("createTenantRole-description-inputEl");
	    By createRoleButtonLocator = By.id("createTenantRole-createRole-btnIconEl");
	    By successMessageBoxOkButtonLocator = By.linkText("OK");
        By roleListLocator = By.xpath("//td[starts-with(@class, 'x-grid-cell x-grid-td x-grid-cell-headerId-gridcolumn')]");

	   
	    public void CreateRole(String name, String description) {

	    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(createRoleNameInputLocator));
	        
	    	safeSendKey(createRoleNameInputLocator, name);
	    	safeSendKey(createRoleDescriptionInputLocator, description);
	    	safeClick(createRoleButtonLocator);
            HtmlReporter.log("after create Role", true);
	        safeClick(successMessageBoxOkButtonLocator);
 	        
	    }
	    public boolean isCreateTenantRoleSuccessful(String role){

            List<WebElement> tableList = webDriver.findElements(roleListLocator);

            for(int index = 0; index < tableList.size(); index++){
                if(tableList.get(index).getText().equals(role)){
                    return true;
                }
            }

            HtmlReporter.log("Could not find role '" + role + "'in list of roles.", true);
	        return false;

	    }
}
