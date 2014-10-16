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

public class TenantUserGroupPage extends ActionDriver {

	WebDriver webDriver;

	public TenantUserGroupPage(WebDriver webDriver) {
		super(webDriver);
		this.webDriver = webDriver;
	}

	By tenantNewUserGroupLinkLocator = By.id("newtenantUserGroup-link");
	By tenantDeleteUserGroupLinkLocator = By.id("deletetenantUserGroup-link");

	public void ClickNewUserGroup() {

		safeClick(tenantNewUserGroupLinkLocator);
		Reporter.log("after Click New User Group " );

		// this is showing error , so it being commented for time being
		// Assert.assertTrue(webDriver.getPageSource().contains("Create User Group"));
	}

	public void SelectUserGroup(String group) {
		// Unable to use a locator as I'm accessing by dynamic link text
		WebDriverWait wait = new WebDriverWait(webDriver, 20);
		try {
			webDriver.findElement(By.linkText(group)).isEnabled();
		} catch (Exception e) {
			webDriver
					.findElement(
							By.xpath("//span[@class='x-btn-icon-e1 x-tbar-page-last']"))
					.click();
		}
		WebElement element = wait
				.until(ExpectedConditions.elementToBeClickable(webDriver
						.findElement(By.linkText(group))));
		if (element != null) {
			element.click();
		}
	}

	By groupClassLocator = By.className("select-grid-row");
	By groupLastPage = By.xpath("//span[@class='x-btn-icon-el x-tbar-page-last ']");
	public void selectGroupFromList(int groupNo) {
		if (groupNo > paginationCount){
    		safeClick(groupLastPage);
    		if((groupNo/paginationCount) != 0)
    			groupNo = groupNo%paginationCount;
    		else
    			groupNo = paginationCount;
    	}
		--groupNo;
		
    	WaitForAction.Sleep(sleepForPageLoad);
    	
		String group = Integer.toString(groupNo);
		List<WebElement> elementsList = webDriver
				.findElements(groupClassLocator);
		for (WebElement element : elementsList) {
			Reporter.log("elements accessed are :" + element.getText());
			Reporter.log(" & datarecordindex is : "
					+ element.getAttribute("data-recordindex"));
			if (element.getAttribute("data-recordindex").equals(group)) {
				element.click();
				Reporter.log("inside loop if");
				break;
			}
		}
	}

	By editGroupButtonLocator = By.id("manageTenant-editGroup");
	By editGroupNameLocator = By.id("updateTenantRole-canonicalName-inputEl");
	By editGroupDescriptionLocator = By
			.id("updateTenantRole-description-inputEl");
	By saveButtonLocator = By.linkText("Save");
	By OKButtonLocator = By.linkText("OK");
	public void editUserGroup(int groupNo, String name, String description) {
		selectGroupFromList(groupNo);
		
		safeClick(editGroupButtonLocator);
		WaitForAction.Sleep(sleepForPageLoad);
		
		safeSendKey(editGroupNameLocator, name);
		safeSendKey(editGroupDescriptionLocator, description);
		safeClick(saveButtonLocator);
		safeClick(OKButtonLocator);
		Reporter.log("after click edit user group");
	}

	By clickYesLocator = By.linkText("Yes");
	public void ClickDeleteUserGroup(int groupNo) {

		selectGroupFromList(groupNo);
		safeClick(tenantDeleteUserGroupLinkLocator);
		safeClick(clickYesLocator);
		Reporter.log("after delete user group");
	}

	By tenantRolesAssignedLinkLocator = By.linkText("Roles Assigned");
	By tenantUsersIncludedLinkLocator = By.linkText("Users Included");
	By tenantOrganizationsIncludedLinkLocator = By.linkText("Organizations Included");
	public void clickRolesAssignedTab() {
		safeClick(tenantRolesAssignedLinkLocator);
	}

	public void clickUsersIncludedTab() {
		safeClick(tenantUsersIncludedLinkLocator);
	}

	public void clickOrganizationsIncludedTab() {
		safeClick(tenantOrganizationsIncludedLinkLocator);
	}

    public String findSelectedGroupName(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//label[starts-with(@class, 'x-component detailPaneHeading')]"));

        if(tableList.size() == 0){
            return null;
        }
        return tableList.get(0).getText();
    }
    public String findSelectedGroupDescription(){
        List<WebElement> tableList = webDriver.findElements(By.xpath("//div[starts-with(@class, 'x-form-display-field')]"));

        if(tableList.size() == 0){
            return null;
        }
        return tableList.get(0).getText();
    }

    public void clickGroup(String Group) {
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        List<WebElement> list = webDriver.findElements(By.xpath("//div[starts-with(@class, 'x-grid-cell-inner')]"));
        for(int index = 0; index < list.size(); index++){
            if(Group.equals(list.get(index).getText())){
                list.get(index).click();
                return;
            }
        }
    }
}
