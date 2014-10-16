package com.edifecs.esm.test.common;

import org.openqa.selenium.WebDriver;

import com.edifecs.test.common.DriverBase;

public class RefreshPage extends DriverBase{

	
	public void RefreshPage(){}
	
	static public void Refresh(WebDriver driver){
    	try{
    		driver.navigate().refresh();
    		// Wait is needed as page takes time to refresh
    		WaitForAction.Sleep(4000);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
