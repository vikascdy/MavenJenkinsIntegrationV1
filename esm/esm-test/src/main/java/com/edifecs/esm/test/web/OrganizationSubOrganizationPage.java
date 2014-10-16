package com.edifecs.esm.test.web;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.edifecs.test.common.ActionDriver;

public class OrganizationSubOrganizationPage extends ActionDriver{

	WebDriver webDriver;
	public OrganizationSubOrganizationPage(WebDriver webDriver){
		super(webDriver);
		this.webDriver = webDriver;
	}
	
	By newSubOrganizationLinkLocator = By.id("newChildOrganization-link");
	By deleteSubOrganizationLinkLocator = By.id("deleteChildOrganization-link");
	By listClassLocator = By.className("x-tree-checkbox");
	
	public void selectSubOrganizationCheckbox(int option) {
    	int count = 1;
    	 List<WebElement> elementsList = webDriver.findElements(listClassLocator);
            for (WebElement element: elementsList) {
                Reporter.log("count"+count+element.getText());
            	if (count == option) {
            		element.click();
            		Reporter.log("inside loop if");
                }
            	count++;
            }
    }
	public void clickNewSubOrganization(int subOrganization){
		
		selectSubOrganizationCheckbox(subOrganization);
		safeClick(newSubOrganizationLinkLocator);
		Reporter.log("after click new Sub Organization");
	}
	By deleteSubOrganizationYesButton = By.linkText("Yes");
	public void DeleteSubOrganization(int subOrganization){
		
		selectSubOrganizationCheckbox(subOrganization);
		safeClick(deleteSubOrganizationLinkLocator);
		safeClick(deleteSubOrganizationYesButton);
		Reporter.log("after delete sub organixation");
	}
	public void showOrganizationDetails(String organization){
		By link = By.linkText(organization);
		safeClick(link);
		Reporter.log("after show sub organization details");
	}
}
