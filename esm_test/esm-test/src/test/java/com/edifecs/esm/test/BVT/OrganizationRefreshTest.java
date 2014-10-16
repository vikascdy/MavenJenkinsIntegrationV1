package com.edifecs.esm.test.BVT;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.esm.test.common.RefreshPage;
import com.edifecs.esm.test.web.ClickDoormat;
import com.edifecs.esm.test.web.ClickWorkspaceButton;
import com.edifecs.esm.test.web.CreateOrganizationRolesPage;
import com.edifecs.esm.test.web.CreateOrganizationUserPage;
import com.edifecs.esm.test.web.LoginPage;
import com.edifecs.esm.test.web.OrganizationAssignRolesPage;
import com.edifecs.esm.test.web.OrganizationConfigurationMenu;
import com.edifecs.esm.test.web.OrganizationOverviewPage;
import com.edifecs.esm.test.web.OrganizationUsersPage;
import com.edifecs.esm.test.web.SiteOverviewPage;
import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.HtmlReporter;
import com.edifecs.test.common.WaitForAction;

/**
 * Created by laksagga on 6/13/2014.
 * 
 * The aim of this test is to check asserts,verify page on refresh This test
 * class will follow the natural path of: Log In go to Organization Organization
 * Overview, refresh and validate go to Organization Role, refresh and validate
 * go to Organization Group, refresh and validate Logging out
 */
public class OrganizationRefreshTest extends DriverBase {

	WebDriver webDriver;
	private LoginPage loginPage;
	private SiteOverviewPage siteOverviewPage;
	private OrganizationConfigurationMenu organizationConfigurationMenu;
	private OrganizationOverviewPage organizationOverviewPage;
	private ClickDoormat clickDoormat;
	private ClickWorkspaceButton clickWorkspaceButton;
	private OrganizationAssignRolesPage organizationAssignRolesPage;
	private CreateOrganizationRolesPage createOrganizationRolesPage;
	private OrganizationUsersPage organizationUsersPage;
	private CreateOrganizationUserPage createOrganizationUserPage;

	@BeforeClass
	private void InitializeFramework() {
		
		webDriver = GetDriver();
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		
		loginPage = new LoginPage(webDriver);
		siteOverviewPage = new SiteOverviewPage(webDriver);
		organizationConfigurationMenu = new OrganizationConfigurationMenu(
				webDriver);
		organizationOverviewPage = new OrganizationOverviewPage(webDriver);
		organizationAssignRolesPage = new OrganizationAssignRolesPage(webDriver);
		organizationUsersPage = new OrganizationUsersPage(webDriver);
		createOrganizationUserPage = new CreateOrganizationUserPage(webDriver);
		createOrganizationRolesPage = new CreateOrganizationRolesPage(webDriver);
		clickDoormat = new ClickDoormat(webDriver);
		clickWorkspaceButton = new ClickWorkspaceButton(webDriver);
	}

	@AfterClass
	public void closeBrowser() {
		webDriver.close();
	}

	@Test
	public void Login() {
		HtmlReporter.log("" , true);	    
		WaitForAction.Sleep(MID_SLEEP);
		loginPage.login("_System", "edfx", "admin", "admin");
		HtmlReporter.log("" , true);	    
		
	}
	
	@Test(dependsOnMethods = { "Login" })
	public void refreshOrganizationOverview() {

		HtmlReporter.log("before refreshOrganizationOverview" , true);	    
		WaitForAction.Sleep(MID_SLEEP);
        clickDoormat.clickManageMyOrganization();
		organizationConfigurationMenu.ClickOrganizationOverview();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains(
				"Organization Overview"));
		HtmlReporter.log("after refreshOrganizationOverview" , true);	    
	}

	@Test(dependsOnMethods = { "refreshOrganizationOverview" })
	public void refreshSubOrganization() {

		HtmlReporter.log("before refreshSubOrganization" , true);	    
		WaitForAction.Sleep(SHORT_SLEEP);
        organizationConfigurationMenu.ClickManageSubOrganizations();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains(
				"Sub-Organizations"));
		HtmlReporter.log("after refreshSubOrganization" , true);	    
	}
	// functionality removed for now
	/*@Test(dependsOnMethods = { "refreshSubOrganization" })
	public void RefreshOrganizationRoles() {

		WaitForAction.Sleep(VERY_SHORT_SLEEP);
        organizationConfigurationMenu.ClickAssignRoles();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains(
				"Organization Roles"));
	}
*/
	@Test(dependsOnMethods = { "refreshSubOrganization" })
	public void RefreshOrganizationUser() {

		HtmlReporter.log("begin RefreshOrganizationUser" , true);	    
		WaitForAction.Sleep(SHORT_SLEEP);
        organizationConfigurationMenu.ClickManageUsers();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains(
				"Organization Users"));
		HtmlReporter.log("after RefreshOrganizationUser" , true);	    
		
	}
}
