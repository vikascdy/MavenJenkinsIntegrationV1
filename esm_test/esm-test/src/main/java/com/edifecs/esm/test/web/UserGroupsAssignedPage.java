package com.edifecs.esm.test.web;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

public class UserGroupsAssignedPage extends ActionDriver{

	WebDriver webDriver;
	public UserGroupsAssignedPage(WebDriver webDriver){
		
		super(webDriver);
		this.webDriver = webDriver;
	}
	
	By roleListClassLocator = By.className("select-grid-row-userGroupsAssignment");
    public void selectGroupFromList(int GroupNo) {
    	--GroupNo;
    	String group = Integer.toString(GroupNo);
    	 List<WebElement> elementsList = webDriver.findElements(roleListClassLocator);
            for (WebElement element: elementsList) {
                Reporter.log("elements accessed are :"+ element.getText());
                Reporter.log(" & datarecordindex is : "+ element.getAttribute("data-recordindex"));
            	if (element.getAttribute("data-recordindex").equals(group)){
            		element.click();
            		Reporter.log("inside loop if");
            		break;
                }
            }
    }
	
	By groupClassLocator = By.className("x-grid-row-checker");
    public void selectGroupFromListOfDialogBox(int roleNo) {
    	 List<WebElement> elementsList = webDriver.findElements(groupClassLocator);
         int count = 1;
    	 for (WebElement element: elementsList) {
    		 Reporter.log("elements accessed are :"+ element.getText());
    		 Reporter.log(" & datarecordindex is : "+ element.getAttribute("data-recordindex"));
            	if (count == roleNo) {
            		element.click();
            		Reporter.log("inside loop if");
            		break;
                }
            	count++;
            }
    }
    
    By addGroupLocator = By.id("addGroupToUser-toolEl");
	By addButtonLocator = By.id("addRoleOrGroupOrUser-btnIconEl");
	By closeButtonLocator = By.linkText("Close");
	By OKButtonLocator = By.linkText("OK");
	public void addGroup(int groupNo){
		
		safeClick(addGroupLocator);
		selectGroupFromListOfDialogBox(groupNo);
		safeClick(addButtonLocator);
		safeClick(OKButtonLocator);
		Reporter.log("after add group");
	}
	
	By removeGroupLocator = By.id("removeGroupFromUserr-toolEl");
	By YesButtonLocator = By.linkText("Yes");
	public void deleteGroup(int groupNo){
		
		selectGroupFromList(groupNo);
		safeClick(removeGroupLocator);
		safeClick(YesButtonLocator);
		Reporter.log("after delete group");
	}
	public void closeAddGroupBox(int groupOption){
		
		safeClick(closeButtonLocator);
	}
}

