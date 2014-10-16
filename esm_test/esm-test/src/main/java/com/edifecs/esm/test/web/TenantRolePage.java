package com.edifecs.esm.test.web;

import java.util.*;

import com.edifecs.test.common.HtmlReporter;
import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;

public class TenantRolePage extends ActionDriver {

	WebDriver webDriver;

	public TenantRolePage(WebDriver webDriver) {
		super(webDriver);
		this.webDriver = webDriver;
	}

	By tenantNewRoleLinkLocator = By.id("newtenantRole-link");
	By tenantDeleteRoleLinkLocator = By.id("deletetenantRole-link");

	By tenantIncludeRolesLinkLocator = By.linkText("Include Roles");
	public void clickIncludeRoles() {

		safeClick(tenantIncludeRolesLinkLocator);
		Reporter.log("after click Include Roles ");

	}

	By tenantUsersWithRoleLinkLocator = By.linkText("Users With Role");
	public void clickUsersWithRole() {

		safeClick(tenantUsersWithRoleLinkLocator);		
		Reporter.log("after click Users With Role ");
	}

	By tenantRolePermissionsLinkLocator = By.linkText("Role Permissions");
	public void clickRolePermissions() {

		safeClick(tenantRolePermissionsLinkLocator);		
		Reporter.log("after click Role Permissions ");
	}

	public void ClickNewRole() {

		safeClick(tenantNewRoleLinkLocator);
		Reporter.log("after  Click New Role ");
	}

	public void SelectRole(String role) {
		// Unable to use a locator as I'm accessing by dynamic link text
		WebDriverWait wait = new WebDriverWait(webDriver, 20);
		try {
			webDriver.findElement(By.linkText(role)).isEnabled();
		} catch (Exception e) {
			webDriver
					.findElement(
							By.xpath("//span[@class='x-btn-icon-e1 x-tbar-page-last']"))
					.click();
		}

		WebElement element = wait
				.until(ExpectedConditions.elementToBeClickable(webDriver
						.findElement(By.linkText(role))));

		if (element != null) {
			element.click();
		}
		Reporter.log("after Select role");
	}

	By roleClassLocator = By.className("select-grid-row");
	By roleLastPage = By.xpath("//span[@class='x-btn-icon-el x-tbar-page-last ']");
	public void selectRoleFromList(int roleNo) {
		
		if (roleNo > paginationCount){
    		safeClick(roleLastPage);
    		if((roleNo/paginationCount) != 0)
    			roleNo = roleNo%paginationCount;
    		else
    			roleNo = paginationCount;
    	}
		--roleNo;
		
    	WaitForAction.Sleep(sleepForPageLoad);
    	String role = String.valueOf(roleNo);
		List<WebElement> elementsList = webDriver
				.findElements(roleClassLocator);
		for (WebElement element : elementsList) {
			Reporter.log("elements accessed are :" + element.getText());
			Reporter.log(" & datarecordindex is : "
					+ element.getAttribute("data-recordindex"));
			if (element.getAttribute("data-recordindex").equals(role)) {
				element.click();
				Reporter.log("inside loop if");
				break;
			}
		}
	}

	By editRoleButtonLocator = By.id("manageTenant-editRole");
	By editRoleNameLocator = By.id("updateTenantRole-canonicalName-inputEl");
	By editRoleDescriptionLocator = By
			.id("updateTenantRole-description-inputEl");
	By saveButtonLocator = By.linkText("Save");
	By OKButtonLocator = By.linkText("OK");
	public void editRole(int roleNo, String name, String description) {

		selectRoleFromList(roleNo);
		safeClick(editRoleButtonLocator);
		safeSendKey(editRoleNameLocator, name);
		safeSendKey(editRoleDescriptionLocator, description);
		safeClick(saveButtonLocator);
		WaitForAction.Sleep(sleepForPageLoad);
		safeClick(OKButtonLocator);
		Reporter.log("after edit role");
	}

	By clickYesLocator = By.linkText("Yes");
	public void DeleteRole(int roleNo) {

		selectRoleFromList(roleNo);
		safeClick(tenantDeleteRoleLinkLocator);
		safeClick(clickYesLocator);
		Reporter.log("after delete role");
	}

    public String findSelectedRoleName(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//label[starts-with(@class, 'x-component detailPaneHeading')]"));

        if(tableList.size() == 0){
            return null;
        }
        return tableList.get(0).getText();
    }
    public String findSelectedRoleDescription(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//div[starts-with(@class, 'x-form-display-field')]"));

        if(tableList.size() == 0){
            return null;
        }
        return tableList.get(0).getText();
    }

    public void clickRole(String role) {
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        List<WebElement> list = webDriver.findElements(By.xpath("//div[starts-with(@class, 'x-grid-cell-inner')]"));
        for(int index = 0; index < list.size(); index++){
            if(role.equals(list.get(index).getText())){
                list.get(index).click();
                return;
            }
        }
    }
}
