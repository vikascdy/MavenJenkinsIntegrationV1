package com.edifecs.esm.test.DeployAccountDataSets;

import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.edifecs.esm.test.configureLDAP.DirContextSourceAnonAuthTest;
import com.edifecs.esm.test.configureLDAP.LDAPMain;
import com.edifecs.esm.test.populatedata.ESMAPI;
import com.edifecs.test.common.HtmlReporter;

public class CreateOrganizationStructure 
{
	String ReadRandomUsers =  System.getProperty("user.dir") + "/integration-tests/esm-test/src/test/resources/RandomUsers.xls";
	ESMAPI api = new ESMAPI();
	String DomainName;
	
	public void CreateStructure(int tenantId, int organizationId, String TenantName, String TenantDomain, String OrganizationName, String OrganizationalStructure, String UsersIN)
	{
		String StructureAddress = System.getProperty("user.dir") + "/integration-tests/esm-test/src/test/resources/"+OrganizationalStructure+".xls";
		this.DomainName = TenantDomain;
		
		try
		{
			int sub1organizationID = 0;
			int sub2organizationID = 0;
			int sub3organizationID = 0;
			
			FileInputStream fileInputStream = new FileInputStream(StructureAddress);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet worksheet = workbook.getSheet("organization");
			
			for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++)
			{	
			
			HSSFRow row1 = worksheet.getRow(i);
			HSSFCell cell = row1.getCell(0);
			String a1Val = cell.getStringCellValue();

			//Print Organization
			System.out.println("Organization"  + a1Val);	
			// Create Child to Parent Organization
			sub1organizationID = api.createSubOrganization(tenantId, organizationId, TenantName, TenantDomain, a1Val);
			SetTypeOfOrganizationDB(UsersIN, sub1organizationID);
			
		
			HSSFSheet worksheet2 = workbook.getSheet("suborganization");
			
			for(int j = 1; j<worksheet2.getPhysicalNumberOfRows(); j++)
			{	
			
			HSSFRow row2 = worksheet2.getRow(j);
			HSSFCell parent = row2.getCell(1);
			String a1Val2 = "";
			
			if(parent.toString().equals(a1Val.toString()) && sub1organizationID != 0)
			{
				HSSFCell cell2 = row2.getCell(0);
				a1Val2 = cell2.getStringCellValue();
				
				//Sub-Organization
				System.out.println("	Sub-Organization" + a1Val2);
				sub2organizationID = api.createSubOrganization(tenantId, sub1organizationID, TenantName, TenantDomain, a1Val2);
				SetTypeOfOrganizationDB(UsersIN, sub2organizationID);
				
			}
			
			HSSFSheet worksheet3 = workbook.getSheet("subsuborganization");
			
			for(int k = 1; k<worksheet3.getPhysicalNumberOfRows(); k++)
			{	
			
			HSSFRow row3 = worksheet3.getRow(k);
			HSSFCell subparent = row3.getCell(1);
			
			if(subparent.toString().equals(a1Val2.toString()) && sub2organizationID != 0)
			{
				HSSFCell cell2 = row3.getCell(0);
				String a1Val3 = cell2.getStringCellValue();
				
				//Sub-Sub-Organization
				System.out.println("		sub3organization" + a1Val3);
				System.out.println("tenantID  " + tenantId + "   sub2organizationID  " + sub2organizationID + "    TenantName  " + TenantName + "    TenantDomain  " + TenantDomain + "    sub3organization  " + a1Val3);
				sub3organizationID = api.createSubOrganization(tenantId, sub2organizationID, TenantName, TenantDomain, a1Val3);
				SetTypeOfOrganizationDB(UsersIN, sub3organizationID);
				
			}
			}
			
		    }
		}
			if(sub1organizationID==0 || sub2organizationID==0)
			{
				System.out.println("Structure1 is not created successfully");
			}
			else
			{
				System.out.println("OrganizationStructure with Structure1 created successfully");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
	}
	
	public void SetTypeOfOrganizationDB(String UsersIN, int orgID)
	{
		try
		{
		switch(UsersIN)
		{
		case "LDAP" : 	DirContextSourceAnonAuthTest ldap = new DirContextSourceAnonAuthTest();
						String username = ldap.anonAuth(orgID);
						ConfigureandcreateLDAPUser(username, DomainName, orgID);
						
						break;
		case "AD" : 	HtmlReporter.log("AD will be configured soon", true);
						break;
		case "ESM DB" : HtmlReporter.log("Users In Type is ESM DB" , true);
						ConfigureandcreateESMUser(orgID);
						break;
		default : 		HtmlReporter.log("Invalid Users-In Type" , true);
						break;
		}
	}
	catch(Exception e)
	{
		
	}
	}
	
	private void ConfigureandcreateESMUser(int orgID) 
	{
		String username = LDAPMain.ReadUsernameForESMUser();
		
		try
		{
			FileInputStream fileInputStream = new FileInputStream(ReadRandomUsers);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet worksheet = workbook.getSheet("Random Users");
			
			for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++)
			{	
			
			HSSFRow row1 = worksheet.getRow(i);
			HSSFCell cell = row1.getCell(0);
			String a1Val = cell.getStringCellValue();

			if(a1Val.equals(username))
			{
				String Password = row1.getCell(1).getStringCellValue();	
				String Title = row1.getCell(2).getStringCellValue();	
				String Firstname = row1.getCell(3).getStringCellValue();	
				String Lastname = row1.getCell(4).getStringCellValue();	
				String Email = row1.getCell(5).getStringCellValue();	
				
				System.out.println("Username  " + a1Val + "  Password  " + Password + "  Title  " + Title + "  Firstname  " + Firstname + "  Lastname  " + Lastname + "  Email  " + Email);

			
			    api.AddESMUser(username, Password, Firstname, Lastname, Title, Email, orgID);
                break;
			}
	  
			}
		}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public void ConfigureandcreateLDAPUser(String username, String domain, int OrgID)
	{
		try
		{
			FileInputStream fileInputStream = new FileInputStream(ReadRandomUsers);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet worksheet = workbook.getSheet("Random Users");
			
			for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++)
			{	
			
			HSSFRow row1 = worksheet.getRow(i);
			HSSFCell cell = row1.getCell(0);
			String a1Val = cell.getStringCellValue();

			if(a1Val.equals(username))
			{
				String Title = row1.getCell(2).getStringCellValue();	
				String Firstname = row1.getCell(3).getStringCellValue();	
				String Lastname = row1.getCell(4).getStringCellValue();	
				String Email = row1.getCell(5).getStringCellValue();	
				
				System.out.println("Username  " + a1Val + "  Title  " + Title + "  Firstname  " + Firstname + "  Lastname  " + Lastname + "  Email  " + Email);
			
			    api.AddLDAPUser(a1Val, Firstname, Lastname, Title, Email, domain, OrgID);
                break;
			}
	  
			}
		}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
}
