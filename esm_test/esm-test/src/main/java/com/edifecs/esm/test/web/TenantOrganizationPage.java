package com.edifecs.esm.test.web;


import java.util.List;

import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.HtmlReporter;
import com.edifecs.test.common.WaitForAction;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.uncommons.reportng.HTMLReporter;

/**
 * Created by martholl on 5/15/2014.
 */
public class TenantOrganizationPage extends ActionDriver{

    WebDriver webDriver;

    public TenantOrganizationPage(WebDriver webDriver) {
        super(webDriver);
    	this.webDriver = webDriver;
    }

    By tenantOrganizationNewOrganizationLinkLocator = By.id("newtenantOrganization-link");
    By tenantOrganizationDeleteOrganizationLinkLocator = By.id("deletetenantOrganization-link");
    By tenantOrganizationClass = By.className("x-grid-row-checker");
   
    public void ClickNewOrganizationLink() {

    	safeClick(tenantOrganizationNewOrganizationLinkLocator);
    	Reporter.log("after Click New Organization Link");
    }
    
    By organizationLastPage = By.xpath("//span[@class='x-btn-icon-el x-tbar-page-last ']");
    By organizationClassLocator = By.className("select-grid-row");
    public void selectOrganizationFromList(int organizationNo) {
    	if (organizationNo > paginationCount){
    		safeClick(organizationLastPage);
    		if((organizationNo/paginationCount) != 0)
    			organizationNo = organizationNo%paginationCount;
    		else
    			organizationNo = paginationCount;
    	}
    	--organizationNo;
    	
    	WaitForAction.Sleep(sleepForPageLoad);
    	
    	String organization = Integer.toString(organizationNo);
    	List<WebElement> elementsList = webDriver.findElements(organizationClassLocator);
            for (WebElement element: elementsList) {
                Reporter.log("elements accessed are :"+ element.getText());
                Reporter.log(" & datarecordindex is : "+ element.getAttribute("data-recordindex"));
            	if (element.getAttribute("data-recordindex").equals(organization)) {
            		element.click();
            		Reporter.log("inside loop if");
            		break;
                }
            }
    }
    
    public void SelectOrganization(String organization) {
    	
    	 // Unable to use a locator as I'm accessing by dynamic link text
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        
        try
    	{
    	webDriver.findElement(By.linkText(organization)).isEnabled();
    	}
    	catch(Exception e)
       	{
    		webDriver.findElement(By.xpath("//span[@class='x-btn-icon-e1 x-tbar-page-last']")).click();
        }
        
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.linkText(organization))));
       
        if (element != null) 
        {
            element.click();
        }
        Reporter.log("after Select Organization");
    }
    
    By organizationYesButtonLocator = By.linkText("Yes");
    public void DeleteOrganization(int organizationNo){    	
    	
    	selectOrganizationFromList(organizationNo);
    	safeClick(tenantOrganizationDeleteOrganizationLinkLocator);
    	safeClick(organizationYesButtonLocator);
    	Reporter.log("after Delete Organization");
    }

    public String findSelectedOrganizationName(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//label[starts-with(@class, 'x-component detailPaneHeading')]"));

        if(tableList.size() == 0){
            return null;
        }
        return tableList.get(0).getText();
    }
    public String findSelectedOrganizationDescription(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//div[starts-with(@class, 'x-form-display-field')]"));

        if(tableList.size() == 0){
            return null;
        }
        return tableList.get(0).getText();
    }

    public void clickOrganization(String name){
        WebElement elem = webDriver.findElement(By.xpath("//a[text() = '"+name+"']/parent::div"));
        elem.click();
    }
    
}
