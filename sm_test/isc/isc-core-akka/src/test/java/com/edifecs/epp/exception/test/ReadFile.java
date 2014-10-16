package com.edifecs.epp.exception.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.*;

import org.junit.Test;

import com.edifecs.epp.exception.EppException;


public class ReadFile {

   private List<String> properties = new ArrayList<>();
   

   public void ReadConfigFile(String filePath) throws FileNotExistException {
    	 
	   File file = new java.io.File(filePath);
	   if (!file.exists()){
	       throw new FileNotExistException(new String[] {String.valueOf("configuration.xml"), filePath});
	   } else {
		   System.out.println("File exist ..");
	   }
   }

   @Test
   public void getErrorMessage(){
	   ReadFile objReadFile = new ReadFile();

       String filepath = "/exception-messages/en.properties";
          try {
             objReadFile.ReadConfigFile(filepath);
          }
          catch (EppException e) {
        	  assertTrue(e.getLocalizedMessage() != null);
        	  assertEquals(e.getLocalizedMessage(), "file name configuration.xml is not available at /exception-messages/en.properties location.");
          }
   }
   
    static public void main(String[] args) {
    	new ReadFile().getErrorMessage();
   }
}