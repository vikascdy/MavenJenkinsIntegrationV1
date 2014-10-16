package com.edifecs.esm.test.BVT;

import java.util.concurrent.TimeUnit;

import com.edifecs.test.common.HtmlReporter;
import junit.framework.Assert;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.esm.test.common.RefreshPage;
import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.esm.test.web.ClickDoormat;
import com.edifecs.esm.test.web.CreateTenantOrganizationPage;
import com.edifecs.esm.test.web.CreateTenantPage;
import com.edifecs.esm.test.web.CreateTenantRolePage;
import com.edifecs.esm.test.web.CreateTenantUserGroupPage;
import com.edifecs.esm.test.web.LoginPage;
import com.edifecs.esm.test.web.SiteConfigurationMenu;
import com.edifecs.esm.test.web.SiteTenantsPage;
import com.edifecs.esm.test.web.TenantConfigurationMenu;
import com.edifecs.esm.test.web.TenantOrganizationPage;
import com.edifecs.esm.test.web.TenantRolePage;
import com.edifecs.esm.test.web.TenantUserGroupPage;
import com.edifecs.esm.test.web.XBoardHeader;
import com.edifecs.test.common.DriverBase;

/**
 * Created by laksagga on 6/13/2014.
 *
 * The aim of this test is to check asserts,verify page on refresh  
 * This test class will follow the natural path of:
 *     Log In
 *     go to site overview , refresh and validate
 *     go to site tenants , refresh and validate
 *     go to tenant Organization, refresh and validate
 *     go to tenant  Role, refresh and validate
 *     go to tenant Group, refresh and validate
 *     go to tenant Settings, refresh and validate
 *     Logging out
 */
public class TenantRefreshTest extends DriverBase {

	private WebDriver webDriver;
	private LoginPage loginPage;
	private SiteConfigurationMenu siteConfigurationMenu;
	private SiteTenantsPage siteTentantsPage;
	private ClickDoormat clickDoormat;
	private CreateTenantPage createTenantPage;
	private TenantConfigurationMenu tenantConfigurationMenu;
	private TenantOrganizationPage tenantOrganizationPage;
	private CreateTenantOrganizationPage createOrganizationPage;
	private XBoardHeader xBoardHeader;
	private TenantUserGroupPage tenantUserGroupPage;
	private CreateTenantUserGroupPage createTenantUserGroupPage;
	private TenantRolePage tenantRolePage;
	private CreateTenantRolePage createTenantRolePage;

	String tenant = "BVT Tenant";
	String organization = "BVT Organization";
	String user = "BVT User";
	String email = "bvtuser@yahoo.com";

	
	@BeforeClass
	private void InitializeFramework() {
		
		webDriver = GetDriver();
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		// Is required when test may be run locally
        //webDriver.get("http://localhost:8080");
		webDriver.manage().window().maximize();
		
		loginPage = new LoginPage(webDriver);
		tenantUserGroupPage = new TenantUserGroupPage(webDriver);
		createTenantUserGroupPage = new CreateTenantUserGroupPage(webDriver);
		tenantRolePage = new TenantRolePage(webDriver);
		createTenantRolePage = new CreateTenantRolePage(webDriver);
		siteConfigurationMenu = new SiteConfigurationMenu(webDriver);
		siteTentantsPage = new SiteTenantsPage(webDriver);
		clickDoormat = new ClickDoormat(webDriver);
		createTenantPage = new CreateTenantPage(webDriver);
		tenantConfigurationMenu = new TenantConfigurationMenu(webDriver);
		tenantOrganizationPage = new TenantOrganizationPage(webDriver);
		createOrganizationPage = new CreateTenantOrganizationPage(webDriver);
		tenantConfigurationMenu = new TenantConfigurationMenu(webDriver);
		xBoardHeader = new XBoardHeader(webDriver);

	}

	@Test
	public void Login() {
	 
		HtmlReporter.log("Begin Login", true);
		WaitForAction.Sleep(SHORT_SLEEP);
        loginPage.login("_System", "edfx", "admin", "admin");
        HtmlReporter.log("End Login" , true);
}

	@Test(dependsOnMethods = "Login")
	public void refreshSiteOverview() {
        
		HtmlReporter.log("Begin refreshSiteOverview" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
        siteConfigurationMenu.clickSiteOverview();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains("Site Overview"));
		HtmlReporter.log("End refreshSiteOverview" , true);
	}

	@Test(dependsOnMethods = "refreshSiteOverview")
	public void refreshManageTenant() {

		HtmlReporter.log("Begin refreshManageTenant" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
        siteConfigurationMenu.clickManageTenant();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains("Site Tenants"));
		HtmlReporter.log("End refreshManageTenant" , true);
	}

	@Test(dependsOnMethods = "refreshManageTenant")
	public void refreshTenantOverview() {

		HtmlReporter.log("Begin refreshTenantOverview" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
        clickDoormat.clickManageMyTenant();
		tenantConfigurationMenu.ClickOverview();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant Overview"));
		HtmlReporter.log("End refreshTenantOverview" , true);
	}

	//@Test(dependsOnMethods = "refreshManageTenant")
	@Test(dependsOnMethods = "refreshTenantOverview")
	public void refreshTenantOrganizations() {

		HtmlReporter.log("Begin refreshTenantOrganizations" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
		tenantConfigurationMenu.ClickManageOrganizations();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant Organizations"));
		HtmlReporter.log("End refreshTenantOrganizations" , true);
	}

	@Test(dependsOnMethods = "refreshTenantOrganizations")
	public void refreshTenantRoles() {

		HtmlReporter.log("Begin refreshTenantRoles" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
        tenantConfigurationMenu.ClickManageRoles();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant Roles"));	
		HtmlReporter.log("End refreshTenantRoles" , true);
	}

	@Test(dependsOnMethods = "refreshTenantRoles")
	public void refreshTenantGroup() {

		HtmlReporter.log("Begin refreshTenantGroup" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
        tenantConfigurationMenu.ClickManageGroups();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains("Tenant User Group"));
		HtmlReporter.log("End refreshTenantGroup" , true);
	}

	@Test(dependsOnMethods = "refreshTenantGroup")
	public void refreshAppsPage() {

		HtmlReporter.log("Begin refreshAppsPage" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
        tenantConfigurationMenu.ClickApps();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains("Apps"));
		HtmlReporter.log("End refreshAppsPage" , true);
	}
	@Test(dependsOnMethods = "refreshAppsPage")
	public void refreshTenantSettings() {

		HtmlReporter.log("Begin refreshTenantSettings" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
        tenantConfigurationMenu.ClickSettings();
		RefreshPage.Refresh(webDriver);
		Assert.assertTrue(webDriver.getPageSource().contains("Password Policy Configuration"));
		HtmlReporter.log("End refreshTenantSettings" , true);
	}
	
	@Test(dependsOnMethods = "refreshTenantSettings")
	public void Logout() {

		HtmlReporter.log("Begin Logout" , true);
		WaitForAction.Sleep(SHORT_SLEEP);
        xBoardHeader.ClickLogout();
        HtmlReporter.log("End Logout" , true);
	}
}
