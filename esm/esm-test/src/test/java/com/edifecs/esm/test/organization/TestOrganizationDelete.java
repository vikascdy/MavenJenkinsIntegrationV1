package com.edifecs.esm.test.organization;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.esm.test.BVT.EndToEndUserTest;
import com.edifecs.esm.test.populatedata.CommanSMPopulateForTest;
import com.edifecs.esm.test.web.ClickDoormat;
import com.edifecs.esm.test.web.ClickWorkspaceButton;
import com.edifecs.esm.test.web.CreateOrganizationRolesPage;
import com.edifecs.esm.test.web.CreateOrganizationUserPage;
import com.edifecs.esm.test.web.LoginPage;
import com.edifecs.esm.test.web.OrganizationAssignRolesPage;
import com.edifecs.esm.test.web.OrganizationConfigurationMenu;
import com.edifecs.esm.test.web.OrganizationOverviewPage;
import com.edifecs.esm.test.web.OrganizationSubOrganizationPage;
import com.edifecs.esm.test.web.OrganizationUsersPage;
import com.edifecs.esm.test.web.SiteConfigurationMenu;
import com.edifecs.esm.test.web.SiteOverviewPage;
import com.edifecs.esm.test.web.SiteTenantsPage;
import com.edifecs.esm.test.web.TenantConfigurationMenu;
import com.edifecs.esm.test.web.TenantOrganizationPage;
import com.edifecs.esm.test.web.XBoardHeader;
import com.edifecs.test.common.ActionDriver;
import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.GetDataFromPropertyFile;
import com.edifecs.test.common.ScreenCaptureListener;
import com.edifecs.test.common.WaitForAction;

/**
 * Created by laksagga on 6/24/2014.
 * 
 * This test class will follow the natural path of: Log In Delete an
 * SubOrganization, Unassign a Role, delete a User, delete an organization ,Logging out
 */
// please run these tests after running TestOrganization suite
public class TestOrganizationDelete extends DriverBase {

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
	private SiteTenantsPage siteTenantsPage;
	private TenantConfigurationMenu tenantConfigurationMenu;
	private TenantOrganizationPage tenantOrganizationPage;
	private XBoardHeader xBoardHeader;
	private SiteConfigurationMenu siteConfigurationMenu;
	private OrganizationSubOrganizationPage organizationSubOrganizationPage;

	String tenant ;
	String organization = "BVT Organization";// "BVT Organization";
	String user;
	int userNo = 1;
	int SubOrganizationNo = 2;
	
	@BeforeClass
	private void InitializeFramework() {
		
		CommanSMPopulateForTest c = new CommanSMPopulateForTest();
		c.createTestItems();
		
		webDriver = GetDriver();
		webDriver.manage().window().maximize();
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		
		tenant = GetDataFromPropertyFile.readDataInPropertiesFile("tenant");
		user = GetDataFromPropertyFile.readDataInPropertiesFile("user");
		organization = c.organizationName();
		user = c.username();
		loginPage = new LoginPage(webDriver);
		siteOverviewPage = new SiteOverviewPage(webDriver);
		organizationConfigurationMenu = new OrganizationConfigurationMenu(
				webDriver);
		organizationSubOrganizationPage = new OrganizationSubOrganizationPage(
				webDriver);
		organizationOverviewPage = new OrganizationOverviewPage(webDriver);
		organizationAssignRolesPage = new OrganizationAssignRolesPage(webDriver);
		organizationUsersPage = new OrganizationUsersPage(webDriver);
		createOrganizationUserPage = new CreateOrganizationUserPage(webDriver);
		createOrganizationRolesPage = new CreateOrganizationRolesPage(webDriver);
		clickDoormat = new ClickDoormat(webDriver);
		clickWorkspaceButton = new ClickWorkspaceButton(webDriver);
		siteTenantsPage = new SiteTenantsPage(webDriver);
		tenantConfigurationMenu = new TenantConfigurationMenu(webDriver);
		tenantOrganizationPage = new TenantOrganizationPage(webDriver);
		xBoardHeader = new XBoardHeader(webDriver);
		siteConfigurationMenu = new SiteConfigurationMenu(webDriver);
	}

	@AfterClass
	public void closeBrowser() {
		webDriver.close();
	}

	@Test
	public void Login_SystemAdmin() {
		WaitForAction.Sleep(MID_SLEEP);
		loginPage.login("_System", "edfx", "admin", "admin");
		Reporter.log("after Login_SystemAdmin" , true);
		
	}

	@Test(dependsOnMethods = { "Login_SystemAdmin" })
	public void DeleteOrganizationUser() {

		WaitForAction.Sleep(MID_SLEEP);
		siteConfigurationMenu.clickManageTenant();
		siteTenantsPage.clickTenant(tenant);
		tenantConfigurationMenu.ClickManageOrganizations();
		tenantOrganizationPage.SelectOrganization(organization);
		
		WaitForAction.Sleep(SHORT_SLEEP);
		organizationConfigurationMenu.ClickManageUsers();
		organizationUsersPage.DeleteOrganizationUser(userNo);
		Reporter.log("after DeleteOrganizationUser" , true);
		
	}

	@Test(dependsOnMethods = { "DeleteOrganizationUser" })
	public void DeleteSubOrganization() {

		WaitForAction.Sleep(SHORT_SLEEP);
		organizationConfigurationMenu.ClickManageSubOrganizations();
		organizationSubOrganizationPage.DeleteSubOrganization(SubOrganizationNo);
		Reporter.log("after DeleteSubOrganization" , true);
		
	}

	// presently removed from UI , checked and deleted later
	/*@Test(dependsOnMethods = { "DeleteOrganizationUser" })
	public void DeleteOrganizationRoles() {

		WaitForAction.Sleep(VERY_SHORT_SLEEP);
		organizationConfigurationMenu.ClickAssignRoles();
		organizationAssignRolesPage.DeleteRole(0);
		Reporter.log("after DeleteOrganizationRoles" , true);
		
	}
*/
	@Test(dependsOnMethods = { "DeleteSubOrganization" })
	public void DeleteOrganization() {

		WaitForAction.Sleep(VERY_SHORT_SLEEP);
		organizationConfigurationMenu.ClickOrganizationOverview();
		organizationOverviewPage.ClickBackToTenantPage();
		tenantOrganizationPage.DeleteOrganization(1);
		Reporter.log("after DeleteOrganization" , true);
		
	}

	@Test(dependsOnMethods = { "DeleteOrganization" })
	public void Logout() {
		
		WaitForAction.Sleep(VERY_SHORT_SLEEP);
		xBoardHeader.ClickUserButton();
		xBoardHeader.ClickLogout();
		// Need to add verification(s)
		Reporter.log("after Logout" , true);
		
	}
}
