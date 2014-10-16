package com.edifecs.esm.test.web;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

public class UserRolesAssignedPage extends ActionDriver{

		WebDriver webDriver;
		public UserRolesAssignedPage(WebDriver webDriver){
			super(webDriver);
			this.webDriver = webDriver;
		}
		
		
		
		By roleListClassLocator = By.className("select-grid-row-userRolesAssignment");
	    public void selectRoleFromList(int roleNo) {
	    	--roleNo;
	    	String role = Integer.toString(roleNo);
	    	 List<WebElement> elementsList = webDriver.findElements(roleListClassLocator);
	            for (WebElement element: elementsList) {
	                Reporter.log("elements accessed are :"+ element.getText());
	                Reporter.log(" & datarecordindex is : "+ element.getAttribute("data-recordindex"));
	            	if (element.getAttribute("data-recordindex").equals(role)) {
	            		element.click();
	            		Reporter.log("inside loop if");
	            		break;
	                }
	            }
	    }
		
		By roleClassLocator = By.className("x-grid-row-checker");
	    public void selectRoleFromListOfDialogBox(int roleNo) {
	    	 List<WebElement> elementsList = webDriver.findElements(roleClassLocator);
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
	    
	    By addRoleLocator = By.id("assignRoleToUser-toolEl");
		By addButtonLocator = By.id("addRoleOrGroupOrUser-btnIconEl");
		By closeButtonLocator = By.linkText("Close");
		By OKButtonLocator = By.linkText("OK");
		public void addRole(int roleNo){
			
			safeClick(addRoleLocator);
			selectRoleFromListOfDialogBox(roleNo);
			safeClick(addButtonLocator);
			safeClick(OKButtonLocator);
		}
		By removeUserLocator = By.id("removeRoleFromUser-toolEl");
		By YesButtonLocator = By.linkText("Yes");
		public void deleteRole(int roleNo){
			
			selectRoleFromList(roleNo);
			safeClick(removeUserLocator);
			safeClick(YesButtonLocator);
		}
		public void closeAddRoleBox(int roleOption){
			safeClick(closeButtonLocator);
		}
	}


