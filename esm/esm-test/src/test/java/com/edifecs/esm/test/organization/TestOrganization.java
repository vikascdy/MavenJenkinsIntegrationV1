package com.edifecs.esm.test.organization;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.esm.test.populatedata.CommanSMPopulateForTest;
import com.edifecs.esm.test.web.CreateChildOrganizationsPage;
import com.edifecs.esm.test.web.CreateOrganizationRolesPage;
import com.edifecs.esm.test.web.CreateOrganizationUserPage;
import com.edifecs.esm.test.web.CreateTenantOrganizationPage;
import com.edifecs.esm.test.web.CreateTenantPage;
import com.edifecs.esm.test.web.CreateTenantRolePage;
import com.edifecs.esm.test.web.CreateTenantUserGroupPage;
import com.edifecs.esm.test.web.LoginPage;
import com.edifecs.esm.test.web.OrganizationAssignRolesPage;
import com.edifecs.esm.test.web.OrganizationConfigurationMenu;
import com.edifecs.esm.test.web.OrganizationOverviewPage;
import com.edifecs.esm.test.web.OrganizationSubOrganizationPage;
import com.edifecs.esm.test.web.OrganizationUsersPage;
import com.edifecs.esm.test.web.SiteConfigurationMenu;
import com.edifecs.esm.test.web.SiteDoormatMenu;
import com.edifecs.esm.test.web.SiteOverviewPage;
import com.edifecs.esm.test.web.SiteTenantsPage;
import com.edifecs.esm.test.web.TenantConfigurationMenu;
import com.edifecs.esm.test.web.TenantOrganizationPage;
import com.edifecs.esm.test.web.TenantRolePage;
import com.edifecs.esm.test.web.TenantSettingsPage;
import com.edifecs.esm.test.web.TenantUserGroupPage;
import com.edifecs.esm.test.web.XBoardHeader;
import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.GetDataFromPropertyFile;

public class TestOrganization extends DriverBase{

	private WebDriver webDriver;
	private LoginPage loginPage;
	private SiteConfigurationMenu siteConfigurationMenu;
	private SiteDoormatMenu siteDoormatMenu;
	private SiteOverviewPage siteOverviewPage;
	private CreateTenantPage createTenantPage;
	private SiteTenantsPage siteTenantsPage;
	
	private String suffix;

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
    CreateChildOrganizationsPage createChildOrganizationsPage ;
    OrganizationOverviewPage organizationOverviewPage;
    OrganizationAssignRolesPage organizationAssignRolesPage;
    CreateOrganizationRolesPage createOrganizationRolesPage;
    // parameters
	//
	
	public static String tenant;
	String description = "BVT Description";
	String domain = "BVT";
	String organization ;
	String role = "BVT Role";
	String group = "BVT Group";
	String landingPage;
	
	int tenantNo;
	int organizationNo = 1;
	int roleNo = 1;
	int groupNo;
	int userNo;
	
	String subOrganization = "new Child Organization";
	String usernameOfUser = "test_user";
	String passwordOfUser = "useruser";
	String firstNameOfUser = "user";
	String middleNameOfUser = "m";
	String lastNameOfUser = "user";
	String titleUser = "Mr";
	String emailUser;
	
	@BeforeClass
	private void InitializeFramework() {
		CommanSMPopulateForTest c = new CommanSMPopulateForTest();
		c.createOnlyTestTemplateItems();
		
		webDriver = GetDriver();
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		
		tenant = GetDataFromPropertyFile.readDataInPropertiesFile("tenant");
		organization = c.organizationName();
		
		suffix = UUID.randomUUID().toString().substring(24);
		usernameOfUser = usernameOfUser + suffix;
		emailUser = usernameOfUser + "@edifecs.com";

        // Do I have a valid context?

		organizationAssignRolesPage = new OrganizationAssignRolesPage(webDriver);
		createOrganizationRolesPage = new CreateOrganizationRolesPage(webDriver);
		organizationSubOrganizationPage = new OrganizationSubOrganizationPage(webDriver);
	    createChildOrganizationsPage = new CreateChildOrganizationsPage(webDriver);
	    organizationOverviewPage = new OrganizationOverviewPage(webDriver);
	    loginPage = new LoginPage(webDriver);
		siteDoormatMenu = new SiteDoormatMenu(webDriver);
		siteConfigurationMenu = new SiteConfigurationMenu(webDriver);
		createTenantPage = new CreateTenantPage(webDriver);
		siteTenantsPage = new SiteTenantsPage(webDriver);
		tenantConfigurationMenu = new TenantConfigurationMenu(webDriver);
		tenantOrganizationPage = new TenantOrganizationPage(webDriver);
		tenantRolePage = new TenantRolePage(webDriver);
		createTenantOrganizationPage = new CreateTenantOrganizationPage(webDriver);
		createTenantRolePage = new CreateTenantRolePage(webDriver);
		tenantUserGroupPage = new TenantUserGroupPage(webDriver);
		createTenantUserGroupPage = new CreateTenantUserGroupPage(webDriver);
		organizationConfigurationMenu = new OrganizationConfigurationMenu(webDriver);
		organizationUsersPage = new OrganizationUsersPage(webDriver);
		createOrganizationUserPage = new CreateOrganizationUserPage(webDriver);
		xBoardHeader = new XBoardHeader(webDriver);
		tenantSettingsPage=new TenantSettingsPage(webDriver);
	}

	@Test
	public void LoginAdmin(){ 
				
		Reporter.log("begin Login",true);
		WaitForAction.Sleep(SHORT_SLEEP);
				loginPage.login("_System", "edfx", "admin", "admin");
				Assert.assertTrue(loginPage.isLoginSuccessful(true),"loginPage.isLoginSuccessful()");
				Reporter.log("End Login",true);
				
	}	


	@Test(dependsOnMethods = { "LoginAdmin" })
	public void AddSubOrganization(){

        Reporter.log("Begin AddSubOrganization()", true);
		WaitForAction.Sleep(MID_SLEEP);
		siteConfigurationMenu.clickManageTenant();
		siteTenantsPage.clickTenant(tenant);
		tenantConfigurationMenu.ClickManageOrganizations();
		tenantOrganizationPage.SelectOrganization(organization);
		
		WaitForAction.Sleep(SHORT_SLEEP);
        organizationConfigurationMenu.ClickManageSubOrganizations();
        WaitForAction.Sleep(VERY_SHORT_SLEEP);
        organizationSubOrganizationPage.clickNewSubOrganization(organizationNo);
        createChildOrganizationsPage.ChildOrganization(subOrganization, description);

        //
        // I need to get my result.Context here
        //

        Reporter.log("End AddSubOrganization()", true);
    }

    @Test (dependsOnMethods = { "AddSubOrganization" })
	public void UpdateOrganizationOverview(){
    	
    	Reporter.log("Begin UpdateOrganixationOverview" , true);
    	WaitForAction.Sleep(SHORT_SLEEP);
    	organizationConfigurationMenu.ClickOrganizationOverview();
    	organizationOverviewPage.UpdateOrganizationOverview(organization , "Mohali Center is ist IDC");
    	Reporter.log("End UpdateOrganixationOverview" , true);
   }

	// Presently removed from UI need to be verified later
    /*@Test(dependsOnMethods = {"AddSubOrganization"})
	public void AddOrganizationRoles(){

		Reporter.log("Begin AddOrganizationRoles" , true);
		WaitForAction.Sleep(MID_SLEEP);
    	organizationConfigurationMenu.ClickAssignRoles();
        organizationAssignRolesPage.ClickAddRole();
        WaitForAction.Sleep(VERY_SHORT_SLEEP);
        createOrganizationRolesPage.AddNewRole(roleNo);
        // Needs verification
        Reporter.log("End AddSubOrganizationRoles", true);
    }
*/
    @Test(dependsOnMethods = {"AddSubOrganization"})
	public void AddOrganizationUser(){

		Reporter.log("Begin AddOrganizationUser", true);
		WaitForAction.Sleep(SHORT_SLEEP);
    	organizationConfigurationMenu.ClickManageUsers();
    	organizationUsersPage.ClickNewUser();
    	createOrganizationUserPage.CreateStandardUser(usernameOfUser,passwordOfUser ,passwordOfUser , firstNameOfUser,middleNameOfUser, lastNameOfUser, titleUser, emailUser);
        // Needs verification

        Reporter.log("End AddOrganizationUser", true);
    }
    
	@Test(dependsOnMethods = { "AddOrganizationUser" })
	public void Logout() {
		Reporter.log("Enter Logout", true);		
		WaitForAction.Sleep(MID_SLEEP);
		xBoardHeader.ClickUserButton();
		xBoardHeader.ClickLogout();
		Assert.assertTrue(loginPage.isLogOutSuccessful(), "loginPage.isLogOutSuccessfull()");
        Reporter.log("Completed Logout", true);
		// Need to add verification(s)
	}
}
