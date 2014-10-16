
package com.edifecs.esm.test.BVT;

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
public class EndToEndUserTest extends DriverBase{

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
    CreateChildOrganizationsPage createChildOrganizationsPage ;
    OrganizationOverviewPage organizationOverviewPage;
    OrganizationAssignRolesPage organizationAssignRolesPage;
    CreateOrganizationRolesPage createOrganizationRolesPage;
    EsmManagementPage esmManagementPage;
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

    @BeforeClass
    private void InitializeFramework() {
        webDriver = GetDriver();
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        landingPage = GetBaseUrl() +  "/esm/#!/Site/Overview";

        suffix = UUID.randomUUID().toString().substring(24);
        tenant = "bvt tenant" + suffix;
        domain = tenant + "@edifecs.com";
        usernameOfUser = usernameOfUser + suffix;
        emailUser = usernameOfUser + "@edifecs.com";

        // Do I have a valid context?

        organizationAssignRolesPage = new OrganizationAssignRolesPage(webDriver);
        createOrganizationRolesPage = new CreateOrganizationRolesPage(webDriver);
        organizationSubOrganizationPage = new OrganizationSubOrganizationPage(webDriver);
        createChildOrganizationsPage = new CreateChildOrganizationsPage(webDriver);
        organizationOverviewPage = new OrganizationOverviewPage(webDriver);
    }

    private void LoadPageElements()
    {
        if(pageLoaded == false) {
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
            createTenantOrganizationPage = new CreateTenantOrganizationPage(webDriver);
            createTenantRolePage = new CreateTenantRolePage(webDriver);
            tenantUserGroupPage = new TenantUserGroupPage(webDriver);
            createTenantUserGroupPage = new CreateTenantUserGroupPage(webDriver);
            organizationConfigurationMenu = new OrganizationConfigurationMenu(webDriver);
            organizationUsersPage = new OrganizationUsersPage(webDriver);
            createOrganizationUserPage = new CreateOrganizationUserPage(webDriver);
            xBoardHeader = new XBoardHeader(webDriver);
            tenantSettingsPage = new TenantSettingsPage(webDriver);
            esmManagementPage = new EsmManagementPage(webDriver);
            pageLoaded = true;
        }

    }

   // @Test
    public void LoginAdminInvalidPassword(){
        
    	LoadPageElements();
        HtmlReporter.log("begin Login with invalid password", true);
        loginPage.login("_System", "edfx", "admin", "BadPassword");

        Assert.assertTrue(false == loginPage.isLoginSuccessful(false), "loginPage.isLoginSuccessful()");
        HtmlReporter.log("End Login with invalid password", true);

    }

    //@Test(dependsOnMethods = { "LoginAdminInvalidPassword" })
    @Test
    public void LoginAdmin(){
    	
    	LoadPageElements();
        HtmlReporter.log("begin Login", true);
        loginPage.login("_System", "edfx", "admin", "admin");
        Assert.assertTrue(loginPage.isLoginSuccessful(true), "loginPage.isLoginSuccessful()");
        HtmlReporter.log("End Login", true);

    }

    @Test(dependsOnMethods = { "LoginAdmin" })
    public void CreateTenant() {
        HtmlReporter.log("Begin CreateTenant",true);
        siteConfigurationMenu.clickManageTenant();
        siteTenantsPage.clickNewTenantLink();
        Assert.assertTrue(createTenantPage.createNewTenant(tenant, description, domain),  "createTenantPage.createNewTenant()");
        createTenantPage.clickBacktoTenantListLink();
        Assert.assertTrue(siteTenantsPage.isTenantInList(tenant), "siteTenantsPage.isTenantInList()");

        // TODO:  This may fail if there are more than 20 tenants created.  To fix this we need to move to the next page.
        siteTenantsPage.selectTenant(tenant);
        Assert.assertEquals(siteTenantsPage.findSelectedTenantName(), tenant);
        Assert.assertEquals(siteTenantsPage.findSelectedTenantDescription(), description);
        Assert.assertEquals(siteTenantsPage.findSelectedTenantDomain(), domain);

        HtmlReporter.log("after CreateTenant", true);
    }

    @Test(dependsOnMethods = { "CreateTenant" })
    public void CreateDuplicateTenant() {
        HtmlReporter.log("Begin CreateDuplicateTenant",true);
        siteTenantsPage.clickNewTenantLink();
        Assert.assertFalse(createTenantPage.createNewTenant(tenant, description, domain),  "createTenantPage.createNewTenant()");
        createTenantPage.clickBacktoTenantListLink();
        HtmlReporter.log("after CreateDuplicateTenant", true);
    }

    @Test(dependsOnMethods = { "CreateDuplicateTenant" })
    public void AttemptToDeleteSystemTenant() {
        HtmlReporter.log("Begin AttemptToDeleteSystemTenant",true);
        Assert.assertFalse(siteTenantsPage.clickDeleteTenantLink("_System"), "siteTenantsPage.clickDeleteTenantLink");
        HtmlReporter.log("after AttemptToDeleteSystemTenant", true);
    }


    @Test(dependsOnMethods = { "AttemptToDeleteSystemTenant" })
    public void CreateRole() {
        HtmlReporter.log("Begin CreateRole ", true);
        siteConfigurationMenu.clickManageTenant();
        siteTenantsPage.clickTenant(tenant);

        tenantConfigurationMenu.ClickManageRoles();
        tenantRolePage.ClickNewRole();
        createTenantRolePage.CreateRole(role, description);
        createTenantRolePage.clickBackToTenantRoleList();
        Assert.assertTrue(createTenantRolePage.isCreateTenantRoleSuccessful(role), "createTenantRolePage.isCreateTenantRoleSuccessful()");

        tenantRolePage.clickRole(role);
        Assert.assertEquals(tenantRolePage.findSelectedRoleName(), role);
        Assert.assertEquals(tenantRolePage.findSelectedRoleDescription(), description);

        HtmlReporter.log("End CreateRole ", true);

    }

    @Test(dependsOnMethods = { "CreateRole" })
    public void CreateGroup() {

        HtmlReporter.log("Begin CreateGroup ", true);

        tenantConfigurationMenu.ClickManageGroups();
        tenantUserGroupPage.ClickNewUserGroup();
        createTenantUserGroupPage.createNewGroup(group, description);
        createTenantUserGroupPage.clickBackToGroupsList();
        Assert.assertTrue(createTenantUserGroupPage.isCreateTenantGroupSuccessful(group), "createTenantUserGroupPage.isCreateTenantGroupSuccessful()");

        tenantUserGroupPage.clickGroup(group);
        Assert.assertEquals(tenantUserGroupPage.findSelectedGroupName(), group);
        Assert.assertEquals(tenantUserGroupPage.findSelectedGroupDescription(), description);

        HtmlReporter.log("End CreateGroup ", true);

    }

    //@Test(dependsOnMethods = { "CreateGroup" })
 // This test if not working, please fix this or remove this , who so ever has created it 
    @Test(enabled = false)
    public void settings_TenantLogo(){
        LoadPageElements();

        try{
            WaitForAction.Sleep(SHORT_SLEEP);
            tenantConfigurationMenu.ClickSettings();
            File file = new File("images/download.jpg");
            HtmlReporter.log(file.getAbsolutePath(), true);
            tenantSettingsPage.setTenantLogo(file.getAbsolutePath());
            HtmlReporter.log("checkpoint", true);
        }
        catch(Exception e){
            HtmlReporter.log("Exception is"+e);
            //throw new RuntimeException("Tenant Loga Fails");
        }
        HtmlReporter.log("after Settings_TenantLogo", true);
    }

    //@Test(dependsOnMethods = { "settings_TenantLogo" })
    // This test if not working, please fix this or remove this , who so ever has created it 
    @Test (enabled = false)
    public void settings_LandingPage(){
        LoadPageElements();
        try{
            WaitForAction.Sleep(SHORT_SLEEP);
            tenantConfigurationMenu.ClickSettings();
            tenantSettingsPage.setLandingPage(landingPage);
            tenantSettingsPage.clickConfirmation();
            Assert.assertTrue(tenantSettingsPage.isUpdateLandingPageSuccessfull(), "tenantSettingsPage.isUpdateLandingPageSuccessfull()");
            HtmlReporter.log("landing page check point", true);
            // Need to add verification(s)
        }
        catch(Exception e){
            System.out.println(e);
        }
        HtmlReporter.log("after settings_LandingPage", true);
    }

    @Test(dependsOnMethods = { "CreateGroup" })
    public void CreateOrganization() {

        HtmlReporter.log("Begin CreateOrganization ", true);

        tenantConfigurationMenu.ClickManageOrganizations();
        tenantOrganizationPage.ClickNewOrganizationLink();
        createTenantOrganizationPage.CreateNewOrganization(organization,
                description);

        createTenantOrganizationPage.clickBackToOrganizationsList();
        Assert.assertTrue(createTenantOrganizationPage.isCreateTenantOrganizationSuccessful(organization), "createTenantPage.isCreateTenantOrganizationSuccessful()");

        tenantOrganizationPage.clickOrganization(organization);
        Assert.assertEquals(tenantOrganizationPage.findSelectedOrganizationName(), organization);
        Assert.assertEquals(tenantOrganizationPage.findSelectedOrganizationDescription(), description);

        HtmlReporter.log("End create organization", true);
    }
    
    @Test(dependsOnMethods = { "CreateOrganization" })
    public void Logout() {
        HtmlReporter.log("Enter Logout", true);
        WaitForAction.Sleep(MID_SLEEP);
        xBoardHeader.ClickUserButton();
        xBoardHeader.ClickLogout();
        Assert.assertTrue(loginPage.isLogOutSuccessful(), "loginPage.isLogOutSuccessful()");
        HtmlReporter.log("Completed Logout", true);
    }
}