package com.edifecs.esm.test.Sandbox;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.esm.test.web.*;
import com.edifecs.test.common.DriverBase;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by martholl on 6/3/2014.
 * 
 * This test class will follow the natural path of: Log In Create a Tenant
 * Create an Organization Create a Rule Create a Group Create a User Logging out
 */
public class SandboxTest extends DriverBase{

	private WebDriver webDriver;
	private LoginPage loginPage;
    XBoardHeader xBoardHeader;

	//
	// parameters
	//

	
	@BeforeClass
	private void InitializeFramework() {
		webDriver = GetDriver();
		webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		
		loginPage = new LoginPage(webDriver);
		xBoardHeader = new XBoardHeader(webDriver);
	}

	@AfterClass
	public void closeBrowser() {
		webDriver.close();
	}

    @Test
	public void FailedLogin(){
        System.out.println("Failed Login forces Login Test Should Fail");

        // doing login because I need a screen to capture on failure

            loginPage.login("tenant", "org", "user", "pass");
            // TODO: handle exception
            Assert.assertTrue(false, "Failed Login Force test to Fail");

    }

	@Test
    public void SuccessfulLogin(){
        System.out.println("Login Test Should Pass");
        Assert.assertTrue(true, " SuccessfulLogin Test Should Pass");

    }

	@Test
	public void Logout() {
        Assert.assertTrue(true, "Logout Test Should Pass");
    }
}