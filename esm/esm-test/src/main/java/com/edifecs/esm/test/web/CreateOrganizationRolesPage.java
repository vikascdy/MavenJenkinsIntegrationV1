package com.edifecs.esm.test.web;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;


public class CreateOrganizationRolesPage extends ActionDriver{

	private WebDriver webDriver;

	public CreateOrganizationRolesPage(WebDriver webDriver) {
		super(webDriver);
		this.webDriver = webDriver;
	}
	By organizationRoleAddButtonLocator = By.linkText("Add Role");
	By organizationRoleCloseButtonLocator = By.linkText("Close");
	By organizationRoleSuccessMessageBoxLocator = By.id("messagebox-1001");  
    By organizationRoleOkAddButtonLocator = By.id("addRoleOrGroupOrUser-btnIconEl");
    By organizationRoleOkButtonLocator = By.linkText("OK");
    By organizationRoleClassLocator = By.className("x-grid-row-checker");
    
	public void AddNewRole(int option) {

		WaitForAction.Sleep(sleepForPageLoad);
        safeClick(organizationRoleAddButtonLocator);
		int count = 1;
		WaitForAction.Sleep(sleepForPageLoad);
        List<WebElement> elementsList = webDriver.findElements(organizationRoleClassLocator);
		for (WebElement element : elementsList) {
			Reporter.log("Count: " + count, true);
			Reporter.log("Element Text:  "+element.getText(), true);
			if (count == option) {
				element.click();
			}
			count++;
		}

		WaitForAction.Sleep(sleepForPageLoad);
        safeClick(organizationRoleOkAddButtonLocator);
        safeClick(organizationRoleOkButtonLocator);
		
	}
	public void ClickClose() {
		
		WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(organizationRoleClassLocator));
        safeClick(organizationRoleCloseButtonLocator);
	}
}