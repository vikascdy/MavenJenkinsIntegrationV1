package com.edifecs.esm.test.web;

import com.edifecs.test.common.HtmlReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;

import javax.swing.text.html.HTML;
import java.util.List;


public class CreateTenantUserGroupPage extends ActionDriver{

	   private WebDriver webDriver;

	    public CreateTenantUserGroupPage(WebDriver webDriver) {
	        super(webDriver);
	    	this.webDriver = webDriver;
	    }

	    By backToGroupsListLinkLocator = By.id("tenantUserGroups-link");
	    public void clickBackToGroupsList(){
	    	
	    	safeClick(backToGroupsListLinkLocator);
	    	Reporter.log("after clickBackToGroupsList");
	    }
	    
	    By createGroupNameInputLocator = By.id("createTenantUserGroup-canonicalName-inputEl");
	    By createGroupDescriptionInputLocator = By.id("createTenantUserGroup-description-inputEl");
	    By createGroupButtonLocator = By.id("createTenantUserGroup-createUserGroup-btnIconEl");
	    
	    By successMessageBoxOkButtonLocator = By.id("button-1005");
        By groupListLocator = By.xpath("//td[starts-with(@class, 'x-grid-cell x-grid-td x-grid-cell-headerId-gridcolumn')]");


	    public void createNewGroup(String name, String description) {

	    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
	        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(createGroupNameInputLocator));
	        
	    	safeSendKey(createGroupNameInputLocator, name);
	    	safeSendKey(createGroupDescriptionInputLocator, description);
	    	
	    	safeClick(createGroupButtonLocator); 
	        safeClick(successMessageBoxOkButtonLocator);
            HtmlReporter.log("after new Group", true);
	    }
	    public boolean isCreateTenantGroupSuccessful(String group) {

            List<WebElement> tableList = webDriver.findElements(groupListLocator);

            for (int index = 0; index < tableList.size(); index++) {
                HtmlReporter.log(tableList.get(index).getText());
                if (tableList.get(index).getText().equals(group)) {
                    return true;
                }

            }


            HtmlReporter.log("Could not find group '" + group + "'in list of groups.", true);
            return false;
        }
}
