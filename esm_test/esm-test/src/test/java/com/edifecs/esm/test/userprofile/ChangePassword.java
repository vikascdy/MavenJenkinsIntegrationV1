package com.edifecs.esm.test.userprofile;


import com.edifecs.esm.test.web.AccountSettingsPage;
import com.edifecs.test.common.DriverBase;
import com.edifecs.esm.test.web.LoginPage;
import com.edifecs.esm.test.web.XBoardHeader;
import com.edifecs.test.common.WaitForAction;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Created by martholl on 5/15/2014.
 */
public class ChangePassword extends DriverBase {

    private WebDriver webDriver;
    private LoginPage loginPage;
    private XBoardHeader xBoardHeader;
    private AccountSettingsPage accoutSettingsPage;

    @BeforeClass
    private void InitializeFramework() {
        webDriver = GetDriver();
        webDriver.get("http://smbox:8080");
        WaitForAction.Sleep(3000);
        webDriver.manage().window().maximize();

        loginPage = new LoginPage(webDriver);
        webDriver.manage().window().maximize();
        xBoardHeader = new XBoardHeader(webDriver);
        accoutSettingsPage = new AccountSettingsPage(webDriver);
    }

    @Test
    public void Login_SystemAdmin() {
    	try{
        loginPage.login("_System", "edfx", "admin", "admin");
        Assert.assertTrue(webDriver.getPageSource().contains("Site Overview"));

    }catch (Exception e) {
    	e.printStackTrace();
	}
}

    @Test(dependsOnMethods = {"Login_SystemAdmin"})
    public void ChangePassword() {
        xBoardHeader.ClickUserAccounts();
        accoutSettingsPage.ChangePassword("newpassword");


    }
}
