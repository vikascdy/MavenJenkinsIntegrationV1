package com.edifecs.esm.test.web;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

public class TenantIncludeRolesPage extends ActionDriver{
	
	WebDriver webDriver;
	public TenantIncludeRolesPage(WebDriver webDriver){
		super(webDriver);
		this.webDriver = webDriver;
	}
	
	By addRoleLocator = By.id("addRoleToRole-toolEl");
	public void clickPlusButton(){
		safeClick(addRoleLocator);
		
	}
	
	By removeRoleLocator = By.id("removeRoleFromRole-toolEl");
	public void clickMinusButton(String role){
		By roleToRemove = By.linkText(role);
		safeClick(roleToRemove);
		safeClick(removeRoleLocator);
		Reporter.log("after ");
	}
	
	By roleClassLocator = By.className("select-grid-row");
    public void selectRoleFromList(String option) {
    	 List<WebElement> elementsList = webDriver.findElements(roleClassLocator);
            for (WebElement element: elementsList) {
                Reporter.log("elements accessed are :"+ element.getText());
                Reporter.log(" & datarecordindex is : "+ element.getAttribute("data-recordindex"));
            	if (element.getAttribute("data-recordindex").equals(option)) {
            		element.click();
            		Reporter.log("inside loop if");
            		break;
                }
            }
    }
	
	
	By addButtonLocator = By.id("addRoleOrGroupOrUser-btnIconEl");
	By closeButtonLocator = By.linkText("Close");
	public void addRole(String roleOption){
		
		selectRoleFromList(roleOption);
		safeClick(addButtonLocator);
		Reporter.log("after add Role");
	}
	public void closeAddRoleBox(int roleOption){
		
		safeClick(closeButtonLocator);
		Reporter.log("after close Add Role Box");
	}
}
