package com.edifecs.esm.test.web;

import com.edifecs.esm.test.common.*;
import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.HtmlReporter;
import com.edifecs.test.common.WaitForAction;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import java.util.List;

/**
 * Created by martholl on 5/15/2014.
 */
public class SiteTenantsPage extends ActionDriver {

    WebDriver webDriver;

    public SiteTenantsPage(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = webDriver;
    }

    By siteTenantsNewTenantLinkLocator = By.id("newtenant-link");
    By siteTenantsDeleteTenantLinkLocator = By.id("deletetenant-link");
    By tenantlastpage = By.xpath("//span[@class='x-btn-icon-el x-tbar-page-last ']");
    By clickYesLocator = By.linkText("Yes");
    By messageBoxTitleLocator = By.id("messagebox-1001_header_hd-textEl");
    By messageBoxTextLocator = By.id("messagebox-1001-displayfield-inputEl");
    By successMessageBoxOkButtonLocator = By.id("button-1005-btnIconEl");
    By tenantListLocator = By.xpath("//a[starts-with(@id, 'tenantDetail')]");

    public void clickNewTenantLink() {
    	
    	safeClick(siteTenantsNewTenantLinkLocator);
        Reporter.log("after New Tenant Link ");
		
    }
    By SiteTenantClass = By.className("select-grid-row");
    public boolean clickDeleteTenantLink(String tenant){
    	WebDriverWait wait = new WebDriverWait(webDriver, 20);
		if(webDriver.findElement(By.linkText(tenant)).isEnabled()) {		

			WebElement elem = webDriver.findElement(By.xpath("//a[text() = '"+tenant+"']/parent::div"));
			elem.click();


		}
        else{
	    	webDriver.findElement(tenantlastpage).click();
			WaitForAction.Sleep(sleepForPageLoad);
			WebElement elem = webDriver.findElement(By.xpath("//a[text() = '"+tenant+"']/parent::div"));
			elem.click();

	    }
        safeClick(siteTenantsDeleteTenantLinkLocator);

        WebElement messageBoxTitle = webDriver.findElement(messageBoxTitleLocator);
        WebElement messageBoxMessage = webDriver.findElement(messageBoxTextLocator);

        boolean success = false;

        if(messageBoxTitle.getText().equals("Delete Tenant?")){
            HtmlReporter.log("Click yes to confirm deleting tenant.  " + messageBoxTitle.getText() + "  " + messageBoxMessage.getText());
            safeClick(clickYesLocator);
            success = true;
        }
        else{
            HtmlReporter.log("Error message when Deleting Tenant.  " + messageBoxTitle.getText() + "  " + messageBoxMessage.getText());
            safeClick(successMessageBoxOkButtonLocator);
            success = false;

        }

        WaitForAction.Sleep(sleepForPageLoad);
        HtmlReporter.log("after delete tenant using String" , true);
        return success;
    }

	public void clickDeleteTenantLink(int tenantNo) {
    	
    	selectTenantFromList(tenantNo);
        //By selectRecord = By.xpath("//tr[contains(@data-recordindex,'"+option+"')]");
        //WebElement element = webDriver.findElement(selectRecord);
        //System.out.println("element is :"+element.getText()+ "data record id is :"+element.getAttribute("data-recordindex"));
        safeClick(siteTenantsDeleteTenantLinkLocator);
        safeClick(clickYesLocator);
        Reporter.log("after click Delete Tenant");
    }


    public void selectTenant(String tenant){
        WebElement elem = webDriver.findElement(By.xpath("//a[text() = '"+tenant+"']/parent::div"));
        elem.click();
        WaitForAction.Sleep(sleepForPageLoad);

    }
    public String findSelectedTenantName(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//label[starts-with(@class, 'x-component detailPaneHeading')]"));


        if(tableList.size() == 0){
            HtmlReporter.log("Could not find any elements in Xpath: //label[starts-with(@class, 'x-component detailPaneHeading')]");
            return null;
        }
        String tenantName = tableList.get(0).getText();
        HtmlReporter.log("Found Tenant in right column: " + tenantName);
        return tenantName;
    }
    public String findSelectedTenantDescription(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//div[starts-with(@class, 'x-form-display-field')]"));

        if(tableList.size() == 0){
            return null;
        }
        return tableList.get(0).getText();
    }

    public String findSelectedTenantDomain(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//div[starts-with(@class, 'x-component x-component-default')]"));

        if(tableList.size() < 3){
            return null;
        }
        return tableList.get(2).getText();
    }

    public void clickTenant(String tenant) {
        // Unable to use a locator as I'm accessing by dynamic link text
       
    	WebDriverWait wait = new WebDriverWait(webDriver, 20);    	
		try
    	{
    	webDriver.findElement(By.linkText(tenant)).isEnabled();
    	}
    	catch(Exception e)
       	{
    		webDriver.findElement(tenantlastpage).click();
        }
    	WebElement element = wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.linkText(tenant))));
    	if (element != null) 
    	{
            element.click();
        }
    	Reporter.log("after click tenant");
    }
    
     public void selectTenantFromList(int tenantNo) {
    	if (tenantNo > paginationCount){
    		safeClick(tenantlastpage);
    		if((tenantNo/paginationCount) != 0)
    			tenantNo = tenantNo%paginationCount;
    		else
    			tenantNo = paginationCount;
    	}
    	--tenantNo;
    	
    	WaitForAction.Sleep(sleepForPageLoad);
    	String tenant = String.valueOf(tenantNo);
    	 List<WebElement> elementsList = webDriver.findElements(SiteTenantClass);
            for (WebElement element: elementsList) {
                Reporter.log("elements accessed are :"+ element.getText());
                Reporter.log(" & datarecordindex is : "+ element.getAttribute("data-recordindex"));
            	if (element.getAttribute("data-recordindex").equals(tenant)) {
            		element.click();
            		Reporter.log("inside loop if");
            		break;
                }
            }
    }
    
    By tenantDetailsLinkLocator = By.linkText("Details");
    By tenantNotificationsLinkLocator = By.linkText("Notifications");
    public void clickDetailsTab(){
    	safeClick(tenantDetailsLinkLocator);
    	Reporter.log("after click Details Tab");
    }
    public void clickNotificationsTab(){
    	safeClick(tenantNotificationsLinkLocator);
    	Reporter.log("after click Notifications Tab");
    }

    public boolean isTenantInList(String tenant){

        List<WebElement> tableList = webDriver.findElements(tenantListLocator);

        for(int index = 0; index < tableList.size(); index++){
            if(tableList.get(index).getText().equals(tenant)){
                return true;
            }
        }

        HtmlReporter.log("Could not find tenant '" + tenant + "'in list of tenants.", true);
        return false;

    }
}
