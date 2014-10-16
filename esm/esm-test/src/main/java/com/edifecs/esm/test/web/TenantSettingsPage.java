package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.edifecs.esm.test.common.WaitForAction;
import com.edifecs.test.common.ActionDriver;


public class TenantSettingsPage extends ActionDriver{

	  WebDriver webDriver;

	     public TenantSettingsPage(WebDriver webDriver) {
	        super(webDriver);
	    	this.webDriver = webDriver;
	    }
	     
	     By changePasswordAtFirstLoginLocator = By.id("managePasswordPolicy-changePasswdAtFirstLogin-inputEl");
	     By passwordAgeLocator = By.id("managePasswordPolicy-passwdAge-inputEl");
	     By passwordHistoryLocator = By.id("managePasswordPolicy-passwdHistory-inputEl");
	     By passwordLockDurationLocator = By.id("managePasswordPolicy-passwdLockoutDuration-inputEl");
	     By passwordMaxFailureLocator = By.id("managePasswordPolicy-passwdMaxFailure-inputEl");
	     By passwordResetFailureLocator = By.id("managePasswordPolicy-passwdResetFailureLockout-inputEl");
	     By passwordTestPasswordLocator = By.id("managePasswordPolicy-password-inputEl");
	     By passwordDescriptionLocator = By.id("managePasswordPolicy-passwdRegexDesc-inputEl");
	     By passwordRegularExpressionLocator = By.id("managePasswordPolicy-passwdRegex-inputEl");
	     By passwordSaveButtonLocator = By.id("managePasswordPolicy-savePasswordPolicy-btnIconEl");
	     By messageSiteOverviewConfirmation = By.xpath("//h1[contains(.,'Site Overview')]");
	    // By confirmationBtn = By.xpath("//span[text()='OK']");
	     By confirmationBtn = By.xpath("//span[contains(.,'OK') and @class= 'x-btn-button']");
	     By landingBtn = By.id("workspaceId-btnIconEl");
	    public void setPasswordPolicy(String changePasswdAtFirstLogin, String pAge,String pHistory,String pLockoutDuration,String pMaxFailure,
	    		String pResetFailureLockout,String expressionName ,String testPassword,String description) {

	     	if(changePasswdAtFirstLogin.equals("true"))
	     	safeClick(changePasswdAtFirstLoginLocator);
	     		
	    	safeSendKey(passwordAgeLocator, pAge);
	     	safeSendKey(passwordHistoryLocator, pHistory);
	     	safeSendKey(passwordLockDurationLocator, pLockoutDuration);
	     	safeSendKey(passwordMaxFailureLocator, pMaxFailure);
	     	safeSendKey(passwordResetFailureLocator, pResetFailureLockout);
	     	
	     	// this is commented because we may have to set customized regex of our for passwords ,
	     	// commenting keeps the default 
	     	//safeSendKey(PasswordRegularExpression, "");
	     	
	     	safeSendKey(passwordTestPasswordLocator, testPassword);
	        safeSendKey(passwordDescriptionLocator, description);
	        safeClick(passwordSaveButtonLocator);
	        Reporter.log("after set Password policy");    
	    }
	    By changePasswdAtFirstLoginLocator = By.id("managePasswordPolicy-changePasswdAtFirstLogin-inputEl");
	    public void clickChangePasswdAtFirstLogin(){
	    	safeClick(changePasswdAtFirstLoginLocator);
	    }
	    By landingPageInputLocator = By.name("landingPage");
	    By landingPageUpdateButtonLocator = By.id("tenantLogo-saveTenantLandingPage-btnIconEl");
	    public void setLandingPage(String landingPage) throws InterruptedException{
	    	
	    	safeSendKey(landingPageInputLocator, landingPage);
	    	
	    	WaitForAction.Sleep(sleepForPageLoad);
	    	safeClick(landingPageUpdateButtonLocator);
	    	Reporter.log("after set landing page");
	    }
	    public void clickConfirmation() throws InterruptedException{
	    	
	     	WaitForAction.Sleep(sleepForPageLoad);
	     	safeClick(confirmationBtn);
	    	safeClick(landingBtn);
	    	Reporter.log("after click confirmation ");
	    }
	    
	    By tenantNewLogoLinkLocator = By.name("newLogo");  //button: tenantNewLogoLinkLocator
	    By tenantLogoSaveButtonLocator = By.id("tenantLogo-saveTenantLogo-btnIconEl");
	    By tenantLogoConfirmationMsg = By.xpath("//span[contains(.,'Yes') and @class='x-btn-button']");
	    By tenantSuccessMessage = By.xpath("//span[contains(.,'OK') and @class= 'x-btn-button']");
	    public void setTenantLogo(String logoimagepath){
	    	
	    	safeUpload(tenantNewLogoLinkLocator,logoimagepath);
	     	WaitForAction.Sleep(sleepForPageLoad);
	 	   	
	    	safeClick(tenantLogoConfirmationMsg);
	    	
	     	WaitForAction.Sleep(sleepForPageLoad);
	 	   
	    	safeClick(tenantLogoSaveButtonLocator);
	     	WaitForAction.Sleep(sleepForPageLoad);
	     	
	    	safeClick(tenantSuccessMessage);
	     	BrowserRefresh();
	    }
	    public boolean isUpdateLandingPageSuccessfull(){
	        return webDriver.findElement(messageSiteOverviewConfirmation).isDisplayed();
	    }
}
