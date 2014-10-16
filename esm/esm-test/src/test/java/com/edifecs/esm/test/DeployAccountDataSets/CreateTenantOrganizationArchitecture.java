package com.edifecs.esm.test.DeployAccountDataSets;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.testng.annotations.Test;

import com.edifecs.esm.test.configureLDAP.DirContextSourceAnonAuthTest;
import com.edifecs.esm.test.populatedata.ESMAPI;

public class CreateTenantOrganizationArchitecture extends DirContextSourceAnonAuthTest
{
	
	String configPropertiesAddress =  System.getProperty("user.dir") + "/integration-tests/esm-test/src/test/resources/DeployAccountDataSets_config.properties";
	String DatagenerationSheetAddress = System.getProperty("user.dir") + "/integration-tests/esm-test/src/test/resources/Data Generation Worksheet.xls";
	
	String TenantDomain = PropertyFileReader("Domain");
	//String UserDomain = PropertyFileReader("UserDomain");
	String TenantOrganization = PropertyFileReader("Organization");
	String Username = PropertyFileReader("Username");
	String Password = PropertyFileReader("Password");
	int numberofrandomusersneeded = Integer.parseInt(PropertyFileReader("numberofrandomusersneeded"));
	
	
	static int TenantID;
	static int OrganizationID;
	
	
	public void CreateTenantAndOrganization(String TenantName, String OrganizationStructure, String UsersIN, String Domain)
	{
		System.out.println("CreateTenantAndOrganization");
		//Here Parent Organization Name is same as Tenant Name
		String ParentOrganization = TenantName;
		ESMAPI api = new ESMAPI();
		api.loginToESM(TenantDomain, TenantOrganization, Username, Password);
		TenantID = api.createTenant(TenantName, Domain);
		OrganizationID = api.createOrganization(TenantID, TenantName, Domain, ParentOrganization);
		
		
		CreateOrganizationStructure createstructure = new CreateOrganizationStructure();
		switch(OrganizationStructure)
		{
		case "Structure1" : createstructure.CreateStructure(TenantID, OrganizationID, TenantName, Domain, ParentOrganization, OrganizationStructure, UsersIN);
							break;
		case "Structure2" : createstructure.CreateStructure(TenantID, OrganizationID, TenantName, Domain, ParentOrganization, OrganizationStructure, UsersIN);
							break;
		case "Structure3" : createstructure.CreateStructure(TenantID, OrganizationID, TenantName, Domain, ParentOrganization, OrganizationStructure, UsersIN);
							break;
		default : System.out.println("Invalid Organizational Structure");
							break;
		
		}
		
	}
	
	public String PropertyFileReader(String PropertyName)
	{
		String PropertyValue = null;
		try
		{
	   FileReader reader = new FileReader(configPropertiesAddress);
			Properties properties = new Properties();
			properties.load(reader);
			PropertyValue = properties.getProperty(PropertyName);
			System.out.println("PropertyName-->  " + PropertyName + "  Value-->  " + PropertyValue);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return PropertyValue;
		
	}
	
	@Test
	public void ReadAndRunDataGenerationExcel()
	{
		try
		{
		//Create Random Users and store it in Excel file
        CreateRandomUsers create = new CreateRandomUsers();
        create.createExcelForUsers(numberofrandomusersneeded);
		
		FileInputStream fileInputStream = new FileInputStream(DatagenerationSheetAddress);
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		HSSFSheet worksheet = workbook.getSheet("TenantInformation");
		
		for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++)
		{	
		
		HSSFRow row = worksheet.getRow(i);
		HSSFCell cell1 = row.getCell(0);
		HSSFCell cell2 = row.getCell(1);
		HSSFCell cell3 = row.getCell(2);
		HSSFCell cell4 = row.getCell(3);
		String TenantName = cell1.getStringCellValue();
		String UsersIN = cell2.getStringCellValue();
		String OrgStructure = cell3.getStringCellValue();
		String Domain = cell4.getStringCellValue();
		
		System.out.println("TenantNAME  " + TenantName + "  USersIN  " + UsersIN + "  ORgStruture  " + OrgStructure + "  Domain  " + Domain);
		CreateTenantAndOrganization(TenantName, OrgStructure, UsersIN, Domain);
		
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
