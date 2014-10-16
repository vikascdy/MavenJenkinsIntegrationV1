package com.edifecs.esm.test.BVT;

import com.edifecs.esm.test.common.RefreshPage;
import com.edifecs.esm.test.web.*;
import com.edifecs.test.common.DriverBase;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by martholl on 6/3/2014.
 * 
 * A simple test to verify the Automation Grid is configured correctly.
 * Exectued by the testng-verify-grid.xml file - which verifies all browsers and parallelism.
 */
public class VerifyAutomationGrid extends DriverBase {

	private WebDriver webDriver;
	private LoginPage loginPage;

	XBoardHeader xBoardHeader;

	
	@BeforeClass
	private void InitializeFramework() {
		webDriver = GetDriver();
		

		loginPage = new LoginPage(webDriver);
		xBoardHeader = new XBoardHeader(webDriver);
	}

	@AfterClass
	public void closeBrowser() {
		webDriver.close();
	}


	@Test
	public void LoginAdmin() {
		
        // In order to validate parallel test execution - I need the test to run for at least 10 seconds.
        try {
        	loginPage.login("_System", "edfx", "admin", "admin");
        	Thread.sleep(10000);
            Assert.assertTrue(loginPage.isLoginSuccessful(true),
    				"loginPage.isLoginSuccessful()");
    	
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (Exception e) {
        	e.printStackTrace();
		}
	}

	@Test(dependsOnMethods = { "LoginAdmin" })
	public void Logout() {
		xBoardHeader.ClickUserButton();
		xBoardHeader.ClickLogout();
	}
}