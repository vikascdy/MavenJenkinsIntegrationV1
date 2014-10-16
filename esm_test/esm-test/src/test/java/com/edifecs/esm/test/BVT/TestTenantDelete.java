package com.edifecs.esm.test.BVT;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.esm.test.common.RefreshPage;
import com.edifecs.esm.test.populatedata.CommanSMPopulateForTest;
import com.edifecs.esm.test.web.ClickDoormat;
import com.edifecs.esm.test.web.CreateOrganizationUserPage;
import com.edifecs.esm.test.web.CreateTenantOrganizationPage;
import com.edifecs.esm.test.web.CreateTenantPage;
import com.edifecs.esm.test.web.CreateTenantRolePage;
import com.edifecs.esm.test.web.CreateTenantUserGroupPage;
import com.edifecs.esm.test.web.LoginPage;
import com.edifecs.esm.test.web.OrganizationConfigurationMenu;
import com.edifecs.esm.test.web.OrganizationUsersPage;
import com.edifecs.esm.test.web.SiteConfigurationMenu;
import com.edifecs.esm.test.web.SiteDoormatMenu;
import com.edifecs.esm.test.web.SiteOverviewPage;
import com.edifecs.esm.test.web.SiteTenantsPage;
import com.edifecs.esm.test.web.TenantConfigurationMenu;
import com.edifecs.esm.test.web.TenantOrganizationPage;
import com.edifecs.esm.test.web.TenantOverviewPage;
import com.edifecs.esm.test.web.TenantRolePage;
import com.edifecs.esm.test.web.TenantUserGroupPage;
import com.edifecs.esm.test.web.XBoardHeader;
import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.GetDataFromPropertyFile;
import com.edifecs.test.common.WaitForAction;

/**
 * Created by laksagga on 6/24/2014.
 * 
 * This test class will follow the natural path of: Log In Delete a Group Delete
 * a Role Delete a Tenant Logging out
 */

// Tests have been commented as they are under development and may be removed
public class TestTenantDelete extends DriverBase {
	private WebDriver webDriver;
	private LoginPage loginPage;
	private SiteConfigurationMenu siteConfigurationMenu;
	private SiteDoormatMenu siteDoormatMenu;
	private SiteOverviewPage siteOverviewPage;
	private CreateTenantPage createTenantPage;
	private SiteTenantsPage siteTenantsPage;

	TenantConfigurationMenu tenantConfigurationMenu;
	TenantOrganizationPage tenantOrganizationPage;
	CreateTenantOrganizationPage createTenantOrganizationPage;
	TenantRolePage tenantRolePage;
	CreateTenantRolePage createTenantRolePage;
	TenantUserGroupPage tenantUserGroupPage;
	CreateTenantUserGroupPage createTenantUserGroupPage;
	OrganizationConfigurationMenu organizationConfigurationMenu;
	OrganizationUsersPage organizationUsersPage;
	CreateOrganizationUserPage createOrganizationUserPage;
	XBoardHeader xBoardHeader;
	ClickDoormat clickDoormat;
	TenantOverviewPage tenantOverviewPage;
	//
	// parameters
	//
	String tenant;
	String organization;
	String group;
	String role;

	int tenantNo = 4;
	int organizationNo = 1;
	int groupNo = 1;
	int roleNo = 1;

	@BeforeClass
	private void InitializeFramework() {

		CommanSMPopulateForTest commanSMPopulateForTest = new CommanSMPopulateForTest();
		commanSMPopulateForTest.createOnlyTestTemplateItems();
		
		tenant = GetDataFromPropertyFile.readDataInPropertiesFile("tenant");
		organization = commanSMPopulateForTest.organizationName();
		role = commanSMPopulateForTest.roleName();
		group = commanSMPopulateForTest.groupName();

		webDriver = GetDriver();
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

		tenantOverviewPage = new TenantOverviewPage(webDriver);
		clickDoormat = new ClickDoormat(webDriver);
		loginPage = new LoginPage(webDriver);
		siteDoormatMenu = new SiteDoormatMenu(webDriver);
		siteConfigurationMenu = new SiteConfigurationMenu(webDriver);
		createTenantPage = new CreateTenantPage(webDriver);
		siteTenantsPage = new SiteTenantsPage(webDriver);
		tenantConfigurationMenu = new TenantConfigurationMenu(webDriver);
		tenantOrganizationPage = new TenantOrganizationPage(webDriver);
		tenantRolePage = new TenantRolePage(webDriver);
		createTenantOrganizationPage = new CreateTenantOrganizationPage(
				webDriver);
		createTenantRolePage = new CreateTenantRolePage(webDriver);
		tenantUserGroupPage = new TenantUserGroupPage(webDriver);
		createTenantUserGroupPage = new CreateTenantUserGroupPage(webDriver);
		organizationConfigurationMenu = new OrganizationConfigurationMenu(
				webDriver);
		organizationUsersPage = new OrganizationUsersPage(webDriver);
		createOrganizationUserPage = new CreateOrganizationUserPage(webDriver);
		xBoardHeader = new XBoardHeader(webDriver);
	}

	@AfterClass
	public void closeBrowser() {
		webDriver.close();
	}

	@Test
	public void Login_SystemAdmin() {
		
		WaitForAction.Sleep(MID_SLEEP);
		loginPage.login("_System", "edfx", "admin", "admin");
	
	}

	@Test(dependsOnMethods = { "Login_SystemAdmin" })
	public void DeleteOrganization() {
        
        WaitForAction.Sleep(SHORT_SLEEP);
        clickDoormat.clickManageMySite();
		siteConfigurationMenu.clickManageTenant();
		siteTenantsPage.clickTenant(tenant);
		
		tenantConfigurationMenu.ClickManageOrganizations();
		tenantOrganizationPage.DeleteOrganization(organizationNo);
	}

	@Test(dependsOnMethods = { "DeleteOrganization" })
	public void DeletetRole() {

		WaitForAction.Sleep(SHORT_SLEEP);
		tenantConfigurationMenu.ClickManageRoles();
		tenantRolePage.DeleteRole(roleNo);
	}

	@Test(dependsOnMethods = { "DeletetRole" })
	public void DeleteGroup() {

		WaitForAction.Sleep(SHORT_SLEEP);
		tenantConfigurationMenu.ClickManageGroups();
		tenantUserGroupPage.ClickDeleteUserGroup(groupNo);
	}

	@Test(dependsOnMethods = { "DeleteGroup" })
	public void DeleteTenant() {

		WaitForAction.Sleep(SHORT_SLEEP);
		clickDoormat.clickManageMySite();
		WaitForAction.Sleep(MID_SLEEP);
		siteConfigurationMenu.clickManageTenant();
		siteTenantsPage.clickDeleteTenantLink(tenant);
		
	}

	@Test (dependsOnMethods = {"DeleteTenant"})
	public void Logout() {
		
		WaitForAction.Sleep(SHORT_SLEEP);
		xBoardHeader.ClickUserButton();
		xBoardHeader.ClickLogout();
		// Need to add verification(s)
	}
}
