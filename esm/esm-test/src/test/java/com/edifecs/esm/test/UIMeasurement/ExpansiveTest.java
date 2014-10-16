package com.edifecs.esm.test.UIMeasurement;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.edifecs.esm.test.web.*;
import com.edifecs.test.common.HtmlReporter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.DriverBase;

/**
 * Created by martholl on 6/3/2014.
 * 
 * This test class will follow the natural path of: Log In Create a Tenant
 * Create an Organization Create a Rule Create a Group Create a User Logging out
 */
public class ExpansiveTest extends DriverBase {

	private WebDriver webDriver;
	private LoginPage loginPage;
	private SiteConfigurationMenu siteConfigurationMenu;
	private SiteDoormatMenu siteDoormatMenu;
	private SiteOverviewPage siteOverviewPage;
	private CreateTenantPage createTenantPage;
	private SiteTenantsPage siteTenantsPage;
	private ClickDoormat clickDoormat;
	private boolean pageLoaded;

	private String suffix;

	ClickWorkspaceButton clickWorkspaceButton;
	TenantConfigurationMenu tenantConfigurationMenu;
	TenantOrganizationPage tenantOrganizationPage;
	CreateTenantOrganizationPage createTenantOrganizationPage;
	TenantRolePage tenantRolePage;
	CreateTenantRolePage createTenantRolePage;
	TenantUserGroupPage tenantUserGroupPage;
	CreateTenantUserGroupPage createTenantUserGroupPage;
	OrganizationUsersPage organizationUsersPage;
	CreateOrganizationUserPage createOrganizationUserPage;
	XBoardHeader xBoardHeader;
	TenantSettingsPage tenantSettingsPage;
	OrganizationConfigurationMenu organizationConfigurationMenu;
	OrganizationSubOrganizationPage organizationSubOrganizationPage;
	CreateChildOrganizationsPage createChildOrganizationsPage;
	OrganizationOverviewPage organizationOverviewPage;
	OrganizationAssignRolesPage organizationAssignRolesPage;
	CreateOrganizationRolesPage createOrganizationRolesPage;
	EsmManagementPage esmManagementPage;
	TenantOverviewPage tenantOverviewPage;
	// parameters
	//

	String tenant;
	String description = "BVT Description";
	String domain = "BVT";
	String organization = "bvt organization";
	String role = "BVT Role";
	String group = "BVT Group";
	String landingPage;

	int tenantNo;
	int organizationNo = 1;
	int roleNo = 1;
	int groupNo;
	int userNo;

	String subOrganization = "Child Organization";
	String usernameOfUser = "user";
	String passwordOfUser = "useruser";
	String firstNameOfUser = "user";
	String middleNameOfUser = "m";
	String lastNameOfUser = "user";
	String titleUser = "Mr";
	String emailUser;
	int count1, count2, count3;

	@BeforeClass
	private void InitializeFramework() {
		webDriver = GetDriver();
		webDriver.manage().window().maximize();
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		landingPage = GetBaseUrl() + "/esm/#!/Site/Overview";

		suffix = UUID.randomUUID().toString().substring(24);
		tenant = "bvt tenant" + suffix;
		domain = tenant + "@edifecs.com";
		usernameOfUser = usernameOfUser + suffix;
		emailUser = usernameOfUser + "@edifecs.com";

		// Do I have a valid context?
		tenantOverviewPage = new TenantOverviewPage(webDriver);
		organizationAssignRolesPage = new OrganizationAssignRolesPage(webDriver);
		createOrganizationRolesPage = new CreateOrganizationRolesPage(webDriver);
		organizationSubOrganizationPage = new OrganizationSubOrganizationPage(
				webDriver);
		createChildOrganizationsPage = new CreateChildOrganizationsPage(
				webDriver);
		organizationOverviewPage = new OrganizationOverviewPage(webDriver);
	}

	private void LoadPageElements() {
		if (pageLoaded == false) {
			loginPage = new LoginPage(webDriver);
			clickWorkspaceButton = new ClickWorkspaceButton(webDriver);
			siteDoormatMenu = new SiteDoormatMenu(webDriver);
			clickDoormat = new ClickDoormat(webDriver);
			siteConfigurationMenu = new SiteConfigurationMenu(webDriver);
			createTenantPage = new CreateTenantPage(webDriver);
			siteTenantsPage = new SiteTenantsPage(webDriver);
			tenantConfigurationMenu = new TenantConfigurationMenu(webDriver);
			tenantOrganizationPage = new TenantOrganizationPage(webDriver);
			tenantRolePage = new TenantRolePage(webDriver);
			tenantOverviewPage = new TenantOverviewPage(webDriver);
			createTenantOrganizationPage = new CreateTenantOrganizationPage(
					webDriver);
			createTenantRolePage = new CreateTenantRolePage(webDriver);
			tenantUserGroupPage = new TenantUserGroupPage(webDriver);
			createTenantUserGroupPage = new CreateTenantUserGroupPage(webDriver);
			organizationConfigurationMenu = new OrganizationConfigurationMenu(
					webDriver);
			organizationUsersPage = new OrganizationUsersPage(webDriver);
			createOrganizationUserPage = new CreateOrganizationUserPage(
					webDriver);
			xBoardHeader = new XBoardHeader(webDriver);
			tenantSettingsPage = new TenantSettingsPage(webDriver);
			esmManagementPage = new EsmManagementPage(webDriver);
			pageLoaded = true;
		}

	}

	// @Test
	public void LoginAdminInvalidPassword() {

		LoadPageElements();
		HtmlReporter.log("begin Login with invalid password", true);
		loginPage.login("_System", "edfx", "admin", "BadPassword");

		Assert.assertTrue(false == loginPage.isLoginSuccessful(false),
				"loginPage.isLoginSuccessful()");
		HtmlReporter.log("End Login with invalid password", true);

	}

	// @Test(dependsOnMethods = { "LoginAdminInvalidPassword" })
	@Test
	public void LoginAdmin() {

		LoadPageElements();
		HtmlReporter.log("begin Login", true);
		loginPage.login("_System", "edfx", "admin", "admin");
		Assert.assertTrue(loginPage.isLoginSuccessful(true),
				"loginPage.isLoginSuccessful()");
		HtmlReporter.log("End Login", true);

	}

	@Test(dependsOnMethods = { "LoginAdmin" })
	public void CreateTenant() {
		HtmlReporter.log("Begin CreateTenant", true);

		// Creation of tenant
		for (count1 = 200; count1 < 201; count1++) {
			siteConfigurationMenu.clickManageTenant();
			siteTenantsPage.clickNewTenantLink();
			Assert.assertTrue(createTenantPage.createNewTenant(tenant + count1,
					description, count1 + domain),
					"createTenantPage.createNewTenant()");
			WaitForAction.Sleep(VERY_SHORT_SLEEP);
			System.out.println("checkpoint1");
			createTenantPage.clickBacktoTenantListLink();
			siteTenantsPage.clickTenant(tenant + count1);

			// this will create roles
			tenantConfigurationMenu.ClickManageRoles();
			tenantRolePage.ClickNewRole();
			for (count2 = 200; count2 < 201; count2++)
				createTenantRolePage.CreateRole(role + count1 + count2,
						description);
			WaitForAction.Sleep(VERY_SHORT_SLEEP);
			System.out.println("checkpoint2");
			createTenantRolePage.clickBackToTenantRoleList();

			// this will create groups
			tenantConfigurationMenu.ClickManageGroups();
			tenantUserGroupPage.ClickNewUserGroup();
			for (count2 = 200; count2 < 201; count2++)
				createTenantUserGroupPage.createNewGroup(group + count1
						+ count2, description);
			WaitForAction.Sleep(VERY_SHORT_SLEEP);
			System.out.println("checkpoint3");
			createTenantUserGroupPage.clickBackToGroupsList();

			// this will create organizations
			tenantConfigurationMenu.ClickManageOrganizations();
			tenantOrganizationPage.ClickNewOrganizationLink();
			for (count2 = 200; count2 < 201; count2++) {
				createTenantOrganizationPage.CreateNewOrganization(organization
						+ count1 + count2, description);
				WaitForAction.Sleep(VERY_SHORT_SLEEP);
				System.out.println("checkpoint4");
				createTenantOrganizationPage.clickBackToOrganizationsList();
				tenantOrganizationPage.SelectOrganization(organization + count1
						+ count2);

				// this will create users of organization user
				organizationConfigurationMenu.ClickManageUsers();
				organizationUsersPage.ClickNewUser();
				for (count3 = 200; count3 < 201; count3++)
					createOrganizationUserPage.CreateStandardUser(
							usernameOfUser + count3, passwordOfUser,
							passwordOfUser, firstNameOfUser + count3,
							middleNameOfUser, lastNameOfUser, titleUser, count1
									+ count2 + count3 + emailUser);

			}// organization
			organizationConfigurationMenu.ClickOrganizationOverview();
			organizationOverviewPage.ClickBackToTenantPage();
			tenantConfigurationMenu.ClickOverview();
			tenantOverviewPage.ClickBackToSitePage();
		}// tenant
	}// mwthod

	/*
	 * Assert.assertTrue(siteTenantsPage.isTenantInList(tenant),
	 * "siteTenantsPage.isTenantInList()");
	 * 
	 * // TODO: This may fail if there are more than 20 tenants created. To //
	 * fix this we need to move to the next page.
	 * Assert.assertEquals(siteTenantsPage.findSelectedTenantName(), tenant);
	 * Assert.assertEquals( siteTenantsPage.findSelectedTenantDescription(),
	 * description);
	 * Assert.assertEquals(siteTenantsPage.findSelectedTenantDomain(), domain);
	 * 
	 * HtmlReporter.log("after CreateTenant", true);
	 */
	@Test(dependsOnMethods = { "CreateTenant" })
	public void Logout() {
		HtmlReporter.log("Enter Logout", true);
		WaitForAction.Sleep(MID_SLEEP);
		xBoardHeader.ClickUserButton();
		xBoardHeader.ClickLogout();
		Assert.assertTrue(loginPage.isLogOutSuccessful(),
				"loginPage.isLogOutSuccessful()");
		HtmlReporter.log("Completed Logout", true);
	}
}