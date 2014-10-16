package com.edifecs.esm.test.web;

import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.HtmlReporter;
import com.edifecs.test.common.WaitForAction;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import javax.swing.text.html.HTML;
import java.util.List;


/**
 * Created by martholl on 5/15/2014.
 */
public class CreateTenantPage extends ActionDriver {

    private WebDriver webDriver;
    String success = "";
    public CreateTenantPage(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = webDriver;
    }

    By createTenantNameInputLocator = By.id("createTenant-canonicalName-inputEl");
    By successMessageBoxOkButtonLocator = By.id("button-1005-btnIconEl");
    By statusMessageBoxLocator = By.xpath("//span[starts-with(@id, 'messagebox-')]");
    By statusMessageTextLocator = By.id("messagebox-1001-displayfield-inputEl");
    By createTenantButtonLocator = By.id("createTenant-createTenant-btnIconEl");
    By createTenantDomainInputLocator = By.id("createTenant-domain-inputEl");
    By createTenantDescriptionInputLocator = By.id("createTenant-description-inputEl");
    By backtoTenantListLinkLocator = By.id("manageTenants-list");

    public void clickBacktoTenantListLink(){
    	
    	safeClick(backtoTenantListLinkLocator);
    	Reporter.log("after clickBacktoTenantListLink");
    }

    public boolean createNewTenant(String name, String description, String domain) {
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(createTenantNameInputLocator));
        
        safeSendKey(createTenantNameInputLocator, name);
        safeSendKey(createTenantDescriptionInputLocator, description);
        safeSendKey(createTenantDomainInputLocator, domain);
        safeSelectAndClick(createTenantButtonLocator);

        boolean success = false;

        List<WebElement> tableList = webDriver.findElements(statusMessageBoxLocator);
        for(int index = 0; index < tableList.size(); index++){
            if(tableList.get(index).getText().equals("Success")){
                success = true;
            }
        }

        if(false == success){
            WebElement messageElement = webDriver.findElement(statusMessageTextLocator);
            HtmlReporter.log("Failed to create tenant.  Error Message: " + messageElement.getText(), true);

        }

        safeClick(successMessageBoxOkButtonLocator);
        HtmlReporter.log("after createNewTenant", true);
        return success;
		
    }


}
