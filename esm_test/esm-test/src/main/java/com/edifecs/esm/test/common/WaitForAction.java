package com.edifecs.esm.test.common;

	import org.testng.Reporter;

	/**
	 * Created by martholl on 5/14/2014.
	 */
	public class WaitForAction {

		static long millis;
		public void WaitForAction(){}
		
		public static void Sleep() {
	        try {
	            Thread.sleep(millis);
	        } catch (Exception e) {
	            Reporter.log("WaitForAction.Sleep() Failed");
	            Reporter.log("Exception: " + e.toString());
	        }
	    }
	     public static void Sleep(long mill) {
	        try {
	            Thread.sleep(mill);
	        } catch (Exception e) {
	            Reporter.log("WaitForAction.Sleep() Failed");
	            Reporter.log("Exception: " + e.toString());
	        }
	    }
	     public static void ChangeSleep(long mill) {
	        try {
	            millis = mill;
	        } catch (Exception e) {
	            Reporter.log("WaitForAction.Sleep() Failed");
	            Reporter.log("Exception: " + e.toString());
	        }
	    }
	}


