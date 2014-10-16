package com.edifecs.esm.test.BVT;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
import com.edifecs.esm.test.web.TenantRolePage;
import com.edifecs.esm.test.web.TenantSettingsPage;
import com.edifecs.esm.test.web.TenantUserGroupPage;
import com.edifecs.esm.test.web.XBoardHeader;
import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.GetDataFromPropertyFile;

public class TestSettingsPage extends DriverBase{

	
	private WebDriver webDriver;
	private LoginPage loginPage;
	private SiteConfigurationMenu siteConfigurationMenu;
	private SiteDoormatMenu siteDoormatMenu;
	private SiteOverviewPage siteOverviewPage;
	private CreateTenantPage createTenantPage;
	private SiteTenantsPage siteTenantsPage;
	
	/*Changes By Priyanka.Sharma Starts*/
	 private String suffix;
	 /*Changes By Priyanka.Sharma Ends*/ 

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
	TenantSettingsPage tenantSettingsPage;
	//
	// parameters
	//
	String loginDomain = "_System";
	String loginOrganization = "edfx";
	String loginUsername = "admin";
	String loginPassword = "admin";
	
	String tenant;
	String description = "BVT Description";
	String domain = "BVT";
	String organization = "BVT Organization";
	String landingPage = "/esm/#!/Site/ManageTenants";
	String changePasswdAtFirstLogin = "true";
	String pAge = "121";
	String pHistory = "4";
	String pLockoutDuration = "16";
	String pMaxFailure = "6";
	String pResetFailureLockout = "6";
	String expressionName = "Tenant-Security";
	String testPassword = "edfxedfx";
	String passwordDescription = "'written by test' Password matching expression. Match all alphanumeric character and predefined wild characters. "
			+ "Password must consists of at least 8 characters and not more than 15 characters";
	
	@BeforeClass
	private void InitializeFramework() {
		webDriver = GetDriver();
		
		tenant = GetDataFromPropertyFile.readDataInPropertiesFile("tenant");
		// Is required when test may be run locally
        // also it echo performance of login on local machine
		///*
		Long startTime = System.currentTimeMillis();
        webDriver.get("http://localhost:8080");
		Long endTime = System.currentTimeMillis();
		
		Long totalTime = endTime - startTime;
		System.out.println("totalTime"+totalTime);
		//*/
		loginPage = new LoginPage(webDriver);
		tenantSettingsPage = new TenantSettingsPage(webDriver);
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
	public void Login(){
		try{
		loginPage.login(loginDomain, loginOrganization, loginUsername, loginPassword);
	}catch(Exception e){
		e.printStackTrace();
	}
}
	@Test(dependsOnMethods = "Login" )
	public void TestLandingPage() throws InterruptedException{
	
		siteConfigurationMenu.clickManageTenant();
		siteTenantsPage.clickTenant(tenant);
		tenantConfigurationMenu.ClickSettings();
		tenantSettingsPage.setLandingPage(landingPage);
	}
	@Test(dependsOnMethods = "TestLandingPage")
	public void TestPasswordPolicy(){
		
		tenantSettingsPage.setPasswordPolicy(changePasswdAtFirstLogin, pAge, pHistory, pLockoutDuration, pMaxFailure, pResetFailureLockout, expressionName, testPassword, passwordDescription);
	}
}
