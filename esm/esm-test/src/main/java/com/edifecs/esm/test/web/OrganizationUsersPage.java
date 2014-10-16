package com.edifecs.esm.test.web;

import java.util.List;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;

/**
 * Created by martholl on 5/15/2014.
 */
public class OrganizationUsersPage extends ActionDriver{

    WebDriver webDriver;

    public OrganizationUsersPage(WebDriver webDriver) {
        super(webDriver);
    	this.webDriver = webDriver;
    }

    By organizationNewUserLinkLocator = By.id("neworganizationUser-link");
    By organizationDeleteUserLinkLocator = By.id("deleteorganizationUser-link");
    By organizationEditUserLinkLocator = By.id("editorganizationUser-link");
    By organizationDeleteUserAckLocator = By.linkText("Yes");
    
    public void ClickNewUser() {

        safeClick(organizationNewUserLinkLocator);
        Reporter.log("ClickNewUser totalTime "+totalTime);
	}

    By organizationUserClassLocator = By.className("select-grid-row");
    By userLastpage = By.xpath("//span[@class='x-btn-icon-el x-tbar-page-last ']");
    public void selectUserFromList(int userNo) {
    	if (userNo > paginationCount){
    		safeClick(userLastpage);
    		if((userNo/paginationCount) != 0)
    			userNo = userNo%paginationCount;
    		else
    			userNo = paginationCount;
    	}
    	 --userNo;
     	
    	WaitForAction.Sleep(sleepForPageLoad);
    	 String user = Integer.toString(userNo);
    	 List<WebElement> elementsList = webDriver.findElements(organizationUserClassLocator);
            for (WebElement element: elementsList) {
            	Reporter.log("elements accessed are :"+ element.getText());
            	Reporter.log(" & datarecordindex is : "+ element.getAttribute("data-recordindex"));
            	if (element.getAttribute("data-recordindex").equals(user)) {
            		element.click();
            		Reporter.log("inside loop if");
            		break;
                }
            }
    }
    
    By EditUserButtonLocator = By.id("manageTenant-editUser");
    By EditUserFirstNameLocator = By.id("edit-user-firstname-inputEl");
    By EditUserMiddleNameLocator = By.id("edit-user-middlename-inputEl");
    By EditUserLastNameLocator = By.id("edit-user-lastname-inputEl");
    By EditUserTitleLocator = By.id("edit-user-salutation-inputEl");
    By EditUserEmailLocator = By.id("edit-user-emailAddress-inputEl");
    By EditUserActiveStatusLocator = By.id("edit-user-active-inputEl");
    By EditUserSuspendedStatusLocator = By.id("edit-user-suspended-inputEl");
    By UpdateButtonLocator = By.linkText("Update");
    By OKButtonLocator = By.linkText("OK");
    public void EditOrganizationUser(int userNo, String fName,String mName,String lName, String title,String EMail, String status){
    	selectUserFromList(userNo);
    	WaitForAction.Sleep(sleepForPageLoad);
    	
    	safeClick(EditUserButtonLocator);
    	safeSendKey(EditUserFirstNameLocator, fName);
    	safeSendKey(EditUserMiddleNameLocator, mName);
    	safeSendKey(EditUserLastNameLocator, lName);
    	safeSendKey(EditUserTitleLocator, title);
    	safeSendKey(EditUserEmailLocator, EMail);
    	
    	switch(status){
    	
    	case "default" : break;
    	case "active" : safeClick(EditUserActiveStatusLocator); break; 
    	default : safeClick(EditUserSuspendedStatusLocator); break;
    	}
    	
    	WaitForAction.Sleep(5000);
    	safeClick(UpdateButtonLocator);
    	WaitForAction.Sleep(5000);
    	safeClick(OKButtonLocator);
    	Reporter.log("after EditOrganizationUser");
    }
        public void DeleteOrganizationUser(int userNo){
    	
        selectUserFromList(userNo);
        WaitForAction.Sleep(sleepForPageLoad);
        safeClick(organizationDeleteUserLinkLocator);
        safeClick(organizationDeleteUserAckLocator);
        Reporter.log("after DeleteOrganizationUser");
    }  
    
    By settingsTabLocator = By.linkText("Settings");
    public void clickSettingsTab(){
    	safeClick(settingsTabLocator);
    	Reporter.log("after click Settings Tab");
    }
    By roleAssignedTabLocator = By.linkText("Roles Assigned"); 
    public void clickRoleAssignedTab(){
    	safeClick(roleAssignedTabLocator);
    	Reporter.log("after click Role Assigned Tab");
    }
    By groupsAssignedTabLocator = By.linkText("Groups Assigned");
    public void clickGroupsAssignedTab(){
    	safeClick(groupsAssignedTabLocator);
    	Reporter.log("after click Groups Assigned Tab");
    }
}