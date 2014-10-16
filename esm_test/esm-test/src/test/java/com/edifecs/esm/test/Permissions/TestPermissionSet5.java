package com.edifecs.esm.test.Permissions;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.esm.test.populatedata.CommanSMPopulateForTest;
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
import com.edifecs.esm.test.web.TenantRolePermissions;
import com.edifecs.esm.test.web.TenantUserGroupPage;
import com.edifecs.esm.test.web.UserRolesAssignedPage;
import com.edifecs.esm.test.web.XBoardHeader;
import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.GetDataFromPropertyFile;
import com.edifecs.test.common.WaitForAction;
/*
 * Assign permission to the user of 
 * site -> edit,view
 * tenant -> view ,edit 
 * user logins and checks its new assigned permissions  
 */
public class TestPermissionSet5 extends DriverBase{

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
    OrganizationConfigurationMenu organizationConfigurationMenu;
    OrganizationUsersPage organizationUsersPage;
    CreateOrganizationUserPage createOrganizationUserPage;
    XBoardHeader xBoardHeader;
    TenantRolePermissions tenantRolePermissions;
    CommanSMPopulateForTest commanSMPopulateForTest;
    UserRolesAssignedPage userAssignRolePage;
    TenantOverviewPage tenantOverviewPage;
    //
    // parameters
    //

    String tenant;
    String domain;
    String organization = "BVT Organization";
    String role;
    String group = "BVT Group";
    String username;
    String password;
    String description = "this entity is created by Automation test case";

    int roleNo = 1;
    int userNo = 1;
    @BeforeClass
    private void InitializeFramework() {

        commanSMPopulateForTest = new CommanSMPopulateForTest();
        commanSMPopulateForTest.createTestItems();

        webDriver = GetDriver();
        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);

        tenant = GetDataFromPropertyFile.readDataInPropertiesFile("tenant");
        domain = commanSMPopulateForTest.tenantDomain();
        organization = commanSMPopulateForTest.organizationName();
        username = commanSMPopulateForTest.username();
        password = commanSMPopulateForTest.password();
        role = commanSMPopulateForTest.roleName();
        // Is required when test may be run locally
        // also it echo performance of login on local machine

        tenantOverviewPage = new TenantOverviewPage(webDriver);
        userAssignRolePage = new UserRolesAssignedPage(webDriver);
        loginPage = new LoginPage(webDriver);
        tenantRolePermissions = new TenantRolePermissions(webDriver);
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
    public void LoginAdmin() {

        WaitForAction.Sleep(MID_SLEEP);
        loginPage.login("_System", "edfx", "admin", "admin");
        Assert.assertTrue(loginPage.isLoginSuccessful(true),"loginPage.isLoginSuccessful()");
        Reporter.log("after LoginAdmin" , true);
    }
    @Test(dependsOnMethods = "LoginAdmin")
    public void givePermission(){

        siteConfigurationMenu.clickManageTenant();
        siteTenantsPage.clickTenant(tenant);
        tenantConfigurationMenu.ClickManageRoles();
        tenantRolePage.selectRoleFromList(roleNo);
        tenantRolePage.clickRolePermissions();

        givePermissionSet5();

        tenantConfigurationMenu.ClickManageOrganizations();
        tenantOrganizationPage.SelectOrganization(organization);
        organizationConfigurationMenu.ClickManageUsers();
        organizationUsersPage.selectUserFromList(userNo);
        organizationUsersPage.clickRoleAssignedTab();
        userAssignRolePage.addRole(1);

        xBoardHeader.ClickLogout();
        Reporter.log("after givePermission" , true);
    }

    @Test(dependsOnMethods = "givePermission")
    public void validatePermissionSet5(){
        loginPage.login(domain, organization, username, password);

        WaitForAction.Sleep(MID_SLEEP);
        siteConfigurationMenu.clickManageTenant();
        siteTenantsPage.clickTenant(tenant);

        tenantConfigurationMenu.ClickOverview();
        tenantOverviewPage.UpdateOverview(tenant, description + "this description is being changed by automatiion test case", domain);
        Reporter.log("after loginAgain" , true);
    }

    public void givePermissionSet5(){

        tenantRolePermissions.clickPermissionTabs();

        tenantRolePermissions.editSite();
        tenantRolePermissions.viewSite();
        tenantRolePermissions.viewTenant();
        tenantRolePermissions.editTenant();

        tenantRolePermissions.clickSaveButton();
        Reporter.log("after givePermissionSet5" , true);
    }
}
