package com.edifecs.esm.test.Permissions;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.esm.test.populatedata.CommanSMPopulateForTest;
import com.edifecs.esm.test.web.ClickDoormat;
import com.edifecs.esm.test.web.CreateOrganizationUserPage;
import com.edifecs.esm.test.web.CreatePropertyPage;
import com.edifecs.esm.test.web.CreateTenantOrganizationPage;
import com.edifecs.esm.test.web.CreateTenantPage;
import com.edifecs.esm.test.web.CreateTenantRolePage;
import com.edifecs.esm.test.web.CreateTenantUserGroupPage;
import com.edifecs.esm.test.web.LoginPage;
import com.edifecs.esm.test.web.OrganizationConfigurationMenu;
import com.edifecs.esm.test.web.OrganizationOverviewPage;
import com.edifecs.esm.test.web.UserPermissionPage;
import com.edifecs.esm.test.web.OrganizationUsersPage;
import com.edifecs.esm.test.web.SiteConfigurationMenu;
import com.edifecs.esm.test.web.SiteDoormatMenu;
import com.edifecs.esm.test.web.SiteOverviewPage;
import com.edifecs.esm.test.web.SiteTenantsPage;
import com.edifecs.esm.test.web.TenantConfigurationMenu;
import com.edifecs.esm.test.web.TenantOrganizationPage;
import com.edifecs.esm.test.web.TenantOrganizationRealm;
import com.edifecs.esm.test.web.TenantRolePage;
import com.edifecs.esm.test.web.TenantRolePermissions;
import com.edifecs.esm.test.web.TenantUserGroupPage;
import com.edifecs.esm.test.web.XBoardHeader;
import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.GetDataFromPropertyFile;
import com.edifecs.test.common.WaitForAction;

/**
 * Created by laksagga on 6/30/2014.
 * 
 * This test class will follow the  path of: Log In 
 * Go to organization Overview Page 
 * Perform CUD on Organization Property
 */
// the test is under development as feature is under development
public class TestRolePermission extends DriverBase{

	private WebDriver webDriver;
	private LoginPage loginPage;
	private SiteConfigurationMenu siteConfigurationMenu;
	private SiteDoormatMenu siteDoormatMenu;
	private SiteOverviewPage siteOverviewPage;
	private CreateTenantPage createTenantPage;
	private SiteTenantsPage siteTenantsPage;
	private TenantRolePermissions tenantRolePermissions;
	private ClickDoormat clickDoormat; 
	
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
	UserPermissionPage organizationUserPermission; 
	TenantOrganizationRealm tenantOrganizationRealm;
	OrganizationOverviewPage organizationOverviewPage; 
	//
	// parameters
	//
	String tenant;
	String organization;
	int roleNo = 1;
	
	@BeforeClass
	private void InitializeFramework() {
		
		CommanSMPopulateForTest commanSMPopulateForTest = new CommanSMPopulateForTest();
		commanSMPopulateForTest.createTestItems();
		
		webDriver = GetDriver();
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		
		tenant = GetDataFromPropertyFile.readDataInPropertiesFile("tenant");
		organization = commanSMPopulateForTest.organizationName();
		
		loginPage = new LoginPage(webDriver);
		siteDoormatMenu = new SiteDoormatMenu(webDriver);
		siteConfigurationMenu = new SiteConfigurationMenu(webDriver);
		createTenantPage = new CreateTenantPage(webDriver);
		siteTenantsPage = new SiteTenantsPage(webDriver);
		organizationOverviewPage = new OrganizationOverviewPage(webDriver);
		tenantOrganizationRealm = new TenantOrganizationRealm(webDriver);
		tenantConfigurationMenu = new TenantConfigurationMenu(webDriver);
		tenantOrganizationPage = new TenantOrganizationPage(webDriver);
		tenantRolePage = new TenantRolePage(webDriver);
		createTenantOrganizationPage = new CreateTenantOrganizationPage(webDriver);
				
		createTenantRolePage = new CreateTenantRolePage(webDriver);
		tenantUserGroupPage = new TenantUserGroupPage(webDriver);
		createTenantUserGroupPage = new CreateTenantUserGroupPage(webDriver);
		organizationConfigurationMenu = new OrganizationConfigurationMenu(
				webDriver);
		organizationUsersPage = new OrganizationUsersPage(webDriver);
		createOrganizationUserPage = new CreateOrganizationUserPage(webDriver);
		xBoardHeader = new XBoardHeader(webDriver);
		tenantRolePermissions = new TenantRolePermissions(webDriver);
		organizationUserPermission = new UserPermissionPage(webDriver);
	}

	@AfterClass
	public void closeBrowser() {
		webDriver.close();
	}
	
	// the given are under development so might appear haphazard  
	@Test
	public void Login(){
		
		WaitForAction.Sleep(MID_SLEEP);
		loginPage.login("_System", "edfx", "admin","admin");
}
	
	@Test(dependsOnMethods = { "Login" })
	public void goToRolePage(){
		
		WaitForAction.Sleep(SHORT_SLEEP);
		siteConfigurationMenu.clickManageTenant();
		siteTenantsPage.clickTenant(tenant);
		
		tenantConfigurationMenu.ClickManageRoles();
		tenantRolePage.selectRoleFromList(roleNo);
		
		checkCreate();
	}
	
	public void getPermission(){
		
		tenantRolePermissions.clickPermissionTabs();
		
		tenantRolePermissions.securityAdminAllGroupPermission();
		tenantRolePermissions.securityAdminAllOrgPermission();
		tenantRolePermissions.securityAdminAllPermissionPermission();
		tenantRolePermissions.securityAdminAllRolePermission();
		tenantRolePermissions.securityAdminAllTenantPermission();
		tenantRolePermissions.securityAdminAllUserPermission();
		tenantRolePermissions.clickSaveButton();
	}
	public void checkCreate(){
		
		tenantRolePermissions.clickPermissionTabs();
		
		tenantRolePermissions.securityAdminAllGroupPermission();//createTenant();
		tenantRolePermissions.securityAdminAllOrgPermission();
		tenantRolePermissions.securityAdminAllTenantPermission();
		
		tenantRolePermissions.clickSaveButton();
	}
	
	// the below test refer to properties that are to user on Organization overview page 
	//@Test(dependsOnMethods = { "Login" })
	public void updateOrganizationOverviewPage(){
		siteConfigurationMenu.clickManageTenant();
		siteTenantsPage.clickTenant("tenant");
		tenantConfigurationMenu.ClickManageOrganizations();
		tenantOrganizationPage.SelectOrganization("just2");
		organizationOverviewPage.UpdateAllOrganizationOverview("just2", "just2 is my friend");
		tenantOrganizationRealm.ActiveDirectoryServer("edifecs@edifecs.com", "edifecs", "edifecsedifecs", "(&(objectClass=*)(uid={edifecs}))", "ou1=usr,ou1=system");
		tenantOrganizationRealm.clickTestAndSaveRealmButton();
		organizationOverviewPage.clickOrganizationOverviewSaveButton();
	}
	
	//@Test(dependsOnMethods = { "Login" })
	public void deleteOrganizationProperty(){
		
		tenantOrganizationRealm.ActiveDirectoryServer("edifecs@edifecs.com", "edifecs", "edifecsedifecs", "(&(objectClass=*)(uid={edifecs}))", "ou1=usr,ou1=system");
		tenantOrganizationRealm.clickDeletePropertyButton("hello world");
		organizationOverviewPage.clickOrganizationOverviewSaveButton();
	}
	
	//@Test(dependsOnMethods = { "Login" })
	public void addOrganizationProperty(){
		tenantOrganizationRealm.ActiveDirectoryServer("edifecs@edifecs.com", "edifecs", "edifecsedifecs", "(&(objectClass=*)(uid={edifecs}))", "ou1=usr,ou1=system");
		
		System.out.println("before tenantOrganizationRealm");
		tenantOrganizationRealm.clickAddPropertyButton();
		CreatePropertyPage createPropertyPage = new CreatePropertyPage(webDriver);
		
		System.out.println("before createNewProperty");
		
		createPropertyPage.createNewProperty("Edifecs", "Edifecs", "EdifecsEdifecs");
		organizationOverviewPage.clickOrganizationOverviewSaveButton();
		
	}
}
