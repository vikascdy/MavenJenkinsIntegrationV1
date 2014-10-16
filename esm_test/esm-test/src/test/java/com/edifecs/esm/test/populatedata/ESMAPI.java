package com.edifecs.esm.test.populatedata;

import static com.jayway.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.Reporter;

import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;



public class ESMAPI
{
	static String sessionid;
	final String CookieName = "eim_session";
	JsonPath jp;
	SessionFilter sessionFilter = new SessionFilter();
	String baseURL = "http://smbox1:8080";
	Response loginResponse;
	static Map<String, String> cookies = new HashMap<String, String>();
	
	
	
	
public void loginToESM(String domain,String organization, String username, String password) 
{
		String loginData;
		System.out.println("LogintoESM entered");
		System.out.println(domain + organization + username + password);
		// Rest call for login and get a session Id
		loginData = "{\"username\":\""+username+"\",\"password\":\""+password+"\",\"domain\":\""+domain+"\",\"organization\":\""+organization+"\",\"remember\":\"false\"}";
		System.out.println( "Login Json  " + loginData );
		loginResponse = given()
				.contentType("application/x-www-form-urlencoded")
				.formParam("data", loginData).filter(sessionFilter).when()
				.post(baseURL + "/rest/" + "login");
		
		jp = loginResponse.jsonPath();
		Boolean check = jp.get("success");
		if(check == false)
		{
			loginResponse.prettyPrint();
			Assert.assertTrue(false, "API Call Failed");
		}
		
		for (Map.Entry<String, String> entry : loginResponse.getCookies().entrySet()) 
		{
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			if(entry.getKey().toString().equals(CookieName))
		  {cookies.put(entry.getKey(), entry.getValue());
		  sessionid=entry.getValue();
		  }
		}
		System.out.println("User Login Successful");
		Reporter.log("User Login Successful");

}

public int createTenant(String TenantName, String TenantDomain)
{
	System.out.println("Entered in createTenant" + TenantName + "  " + TenantDomain +" " + baseURL);
	Response createTenantResponse;
		String tenantData;
		int tenantId;
System.out.println("sessionid  " + sessionid);
		// Rest call to create a tenant
				tenantData = "{\"tenant\":{\"canonicalName\":\""
						+ TenantName
						+ "\",\"description\":\"Description\",\"domain\":\""
						+ TenantDomain
						+ "\",\"environment\":\"\",\"passwordPolicy\":{},\"site\":{\"id\":1,\"canonicalName\":\"Default Site\",\"description\":\"Default Edifecs Site\",\"domain\":\"\",\"environment\":\"\"},\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"Default Site\",\"siteId\":1}}";
				createTenantResponse = given().cookies("eim_session", sessionid).contentType("application/x-www-form-urlencoded").formParam("data", tenantData).filter(sessionFilter).when().post(baseURL + "/rest/service/esm-service/" + "tenant.createTenant");
		        jp = createTenantResponse.jsonPath();
		     
		        Boolean check = jp.get("success");
				if(check == false)
				{
					createTenantResponse.prettyPrint();
					Assert.assertTrue(false, "API Call Failed");
				}
		        
				tenantId = jp.get("data.id");
				Reporter.log("tenant id is: " + tenantId, true);
				System.out.println("Tenant ID is " + tenantId);
				
				System.out.println("Tenant Successfully created");
				Reporter.log("Tenant Successfully created");

				
				return tenantId;
}

public int createOrganization(int tenantId, String TenantName, String TenantDomain, String OrganizationName) 
{
	Response createTenantOrganizationResponse;
	String organizationData;
	int organizationId;
	
	// rest call to create an Organization
	organizationData = "{\"tenant\":{\"id\":"
			+ tenantId
			+ ",\"canonicalName\":\""
			+ TenantName
			+ "\",\"description\":\"\",\"domain\":\""
			+ TenantDomain
			+ "\",\"environment\":\"\",\"passwordPolicy\":"
			+ "{\"id\":"
			+ tenantId
			+ ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
			+ "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
			+ "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
			+ "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
			+ "\"organization\":{\"canonicalName\":\"" + OrganizationName
			+ "\",\"description\":\"\"}}";

			createTenantOrganizationResponse = given().cookie("eim_session", sessionid)
			.contentType("application/x-www-form-urlencoded")
			.formParam("data", organizationData).filter(sessionFilter)
			.when()
			.post(baseURL + "/rest/service/esm-service/" + "organization.createOrganizationForTenant");

	jp = createTenantOrganizationResponse.jsonPath();
	
	 Boolean check = jp.get("success");
		if(check == false)
		{
			createTenantOrganizationResponse.prettyPrint();
			Assert.assertTrue(false, "API Call Failed");
		}
	
	
	organizationId = jp.get("data.id");
	Reporter.log("Organization Id is:" + organizationId, true);
	System.out.println("Organization Successfully created");
	return organizationId;

}

public int createSubOrganization(int tenantId, int organizationId, String TenantName, String TenantDomain, String SubOrganizationName) 
{
	
	String createSubOrganizationData1;
	String createSubOrganizationData2;
	Response createSubOrganizationResponse1;
	Response createSubOrganizationResponse2;
	// rest call to create a Sub-Organization
	createSubOrganizationData1 = "{\"tenant\":{\"id\":"
			+ tenantId
			+ ",\"canonicalName\":\""
			+ TenantName
			+ "\",\"description\":\"\",\"domain\":\""
			+ TenantDomain
			+ "\",\"environment\":\"\",\"passwordPolicy\":"
			+ "{\"id\":"
			+ tenantId
			+ ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
			+ "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
			+ "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
			+ "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
			+ "\"organization\":{\"canonicalName\":\""
			+ SubOrganizationName + "\",\"description\":\"\"}}";

	createSubOrganizationResponse1 = given().cookie("eim_session", sessionid)
			.contentType("application/x-www-form-urlencoded")
			.formParam("data", createSubOrganizationData1)
			.filter(sessionFilter).when()
			.post(baseURL + "/rest/service/esm-service/organization.createOrganizationForTenant");

	//createSubOrganizationResponse1.prettyPrint();
	jp = createSubOrganizationResponse1.jsonPath();
	
	Boolean check = jp.get("success");
	if(check == false)
	{
		createSubOrganizationResponse1.prettyPrint();
		Assert.assertTrue(false, "API Call Failed");
	}
	
	
	int subOrganizationId = jp.get("data.id");
	Reporter.log("Sub Organization Id is:" + subOrganizationId, true);

	createSubOrganizationData2 = "{\"organizationId\": \"" + organizationId
			+ "\",\"childOrganizationId\": \"" + subOrganizationId + "\"}";

	createSubOrganizationResponse2 = given().cookie("eim_session", sessionid)
			.contentType("application/x-www-form-urlencoded")
			.formParam("data", createSubOrganizationData2)
			.filter(sessionFilter).when()
			.post(baseURL + "/rest/service/esm-service/organization.addChildOrganization");
	Reporter.log("final sub organization is created", true);
	//createSubOrganizationResponse2.prettyPrint();
	
	jp = createSubOrganizationResponse2.jsonPath();
	
	Boolean check2 = jp.get("success");
	if(check2 == false)
	{
		createSubOrganizationResponse2.prettyPrint();
		Assert.assertTrue(false, "API Call Failed");
	}
	
	
	return subOrganizationId;

}


public void CheckLDAPConnectionAndSaveSetting(int OrgID, int port, String MachineIPAddress, String LDAPusername, String LDAPpassword) 
{
	String VerifyLDAPConnection;
	String SaveLDAPConnectionsettings;
	Response createLDAPVerificationResponse;
	Response SaveLDAPConnectionResponse;

	// rest call to create a Sub-Organization
	VerifyLDAPConnection =  "{\"realm\":{\"name\":\"LDAP\",\"realmType\":\"LDAP\",\"enabled\":true,\"properties\":[{\"propertyId\":0,\"name\":\"URL\",\"value\":\""
			+ "ldap://"+ MachineIPAddress + ":" + port +
			"\",\"description\":\"URL the LDAP url to connect to. (e.g. ldap://ldapDirectoryHostname:port)\",\"required\":true},{\"propertyId\":0,\"name\":\"Group Filter\",\"value\":\"(&(objectClass=*)(member={0}))\",\"description\":\"filterExpr the filter expression to use for the search. The expression may contain variables of the form '{i}' where i is a nonnegative integer.\",\"required\":true},{\"propertyId\":0,\"name\":\"User Filter\",\"value\":\"(&(objectClass=*)(uid={0}))\",\"description\":\"filterExpr the filter expression to use for the search. The expression may contain variables of the form '{i}' where i is a nonnegative integer.\",\"required\":true},{\"propertyId\":0,\"name\":\"User Search Base\",\"value\":\"ou=users,ou=system\",\"description\":\"name the name of the context or object to search\",\"required\":true},{\"propertyId\":0,\"name\":\"Group Search Base\",\"value\":\"ou=groups,ou=system\",\"description\":\"name the name of the context or object to search\",\"required\":true},{\"propertyId\":0,\"name\":\"System User\",\"value\":\"uid=admin,ou=system\",\"description\":\"systemUsername the system username that will be used when creating an LDAP connection used for authorization queries.\",\"required\":false},{\"propertyId\":0,\"name\":\"System Password\",\"value\":\"secret\",\"description\":\"systemPassword the password of the systemUsername that will be used when creating an LDAP connection used for authorization queries.\",\"required\":false},{\"propertyId\":0,\"name\":\"User DN\",\"value\":\"uid={0},ou=users,ou=system\",\"description\":\"User DN formats are unique to the LDAP directory's schema, and each environment differs - you will need to specify the format corresponding to your directory. You do this by specifying the full User DN as normal, but but you use a {0}} placeholder token in the string representing the location where the user's submitted principal (usually a username or uid) will be substituted at runtime. \n\nFor example, if your directory uses an LDAP uid attribute to represent usernames, the User DN for the jsmith user may look like this:\n\n uid=jsmith,ou=users,dc=mycompany,dc=comin which case you would set this property with the following template value: \n\n uid={0},ou=users,dc=mycompany,dc=com\",\"required\":false},{\"propertyId\":0,\"name\":\"User Name\",\"value\":\"uid\",\"description\":\"\",\"required\":false},{\"name\":\"username\",\"value\":\""
		+ LDAPusername
		+ "\"},{\"name\":\"password\",\"value\":\""
		+LDAPpassword+
		"\"}]}}";
	
	createLDAPVerificationResponse = given().cookie("eim_session", sessionid)
			.contentType("application/x-www-form-urlencoded")
			.formParam("data", VerifyLDAPConnection)
			.filter(sessionFilter).when()
			.post(baseURL + "/rest/service/esm-service/testLdapConnection");

	createLDAPVerificationResponse.prettyPrint();
	jp = createLDAPVerificationResponse.jsonPath();
	
	Boolean check = jp.get("success");
	if(check == false)
	{
		createLDAPVerificationResponse.prettyPrint();
		Assert.assertTrue(false, "API Call Failed");
	}
	
	
	SaveLDAPConnectionsettings = "{\"realm\":{\"name\":\"LDAP\",\"realmType\":\"LDAP\",\"enabled\":true,\"properties\":[{\"name\":\"URL\",\"value\":\"ldap://"
			+ MachineIPAddress
			+ ":"
			+ port
			+ "\",\"description\":\"URL the LDAP url to connect to. (e.g. ldap://ldapDirectoryHostname:port)\",\"required\":true},{\"name\":\"Group Filter\",\"value\":\"(&(objectClass=*)(member={0}))\",\"description\":\"filterExpr the filter expression to use for the search. The expression may contain variables of the form '{i}' where i is a nonnegative integer.\",\"required\":true},{\"name\":\"User Filter\",\"value\":\"(&(objectClass=*)(uid={0}))\",\"description\":\"filterExpr the filter expression to use for the search. The expression may contain variables of the form '{i}' where i is a nonnegative integer.\",\"required\":true},{\"name\":\"User Search Base\",\"value\":\"ou=users,ou=system\",\"description\":\"name the name of the context or object to search\",\"required\":true},{\"name\":\"Group Search Base\",\"value\":\"ou=groups,ou=system\",\"description\":\"name the name of the context or object to search\",\"required\":true},{\"name\":\"System User\",\"value\":\"uid=admin,ou=system\",\"description\":\"systemUsername the system username that will be used when creating an LDAP connection used for authorization queries.\",\"required\":false},{\"name\":\"System Password\",\"value\":\"secret\",\"description\":\"systemPassword the password of the systemUsername that will be used when creating an LDAP connection used for authorization queries.\",\"required\":false},{\"name\":\"User DN\",\"value\":\"uid={0},ou=users,ou=system\",\"description\":\"User DN formats are unique to the LDAP directory's schema, and each environment differs - you will need to specify the format corresponding to your directory. You do this by specifying the full User DN as normal, but but you use a {0}} placeholder token in the string representing the location where the user's submitted principal (usually a username or uid) will be substituted at runtime. \n\nFor example, if your directory uses an LDAP uid attribute to represent usernames, the User DN for the jsmith user may look like this:\n\n uid=jsmith,ou=users,dc=mycompany,dc=comin which case you would set this property with the following template value: \n\n uid={0},ou=users,dc=mycompany,dc=com\",\"required\":false},{\"name\":\"UserName\",\"value\":\"uid\",\"description\":\"\",\"required\":false}]},\"organizationId\":"
			+ OrgID
			+ "}";
	
	SaveLDAPConnectionResponse = given().cookie("eim_session", sessionid)
			.contentType("application/x-www-form-urlencoded")
			.formParam("data", SaveLDAPConnectionsettings)
			.filter(sessionFilter).when()
			.post(baseURL + "/rest/service/esm-service/organization.addRealmToOrganization");
	SaveLDAPConnectionResponse.prettyPrint();
	
			jp = SaveLDAPConnectionResponse.jsonPath();
			Boolean check2 = jp.get("success");
			if(check2 == false)
			{
				SaveLDAPConnectionResponse.prettyPrint();
				Assert.assertTrue(false, "API Call Failed");
			}
			
			System.out.println("LDAP Connection saved successfuly");
}

public void AddLDAPUser(String username, String firstname, String lastname, String title, String email, String domain, int OrgID)
{
	String userData;
	Response createUserResponse;
	// rest call to create a user
	userData = "{\"user\":{\"canonicalName\":\""
			+ title + " " + firstname + " " + lastname
			+ "\",\"humanUser\":false,\"active\":true,\"deleted\":false,"
			+ "\"username\":\""
			+ username
			+ "\",\"contact\":{\"firstName\":\""
			+ firstname
			+ "\",\"middleName\":\""
			+ "\",\"lastName\":\""
			+ lastname
			+ "\","
			+ "\"salutation\":\""
			+ title
			+ "\",\"emailAddress\":\""
			+ email
			+ "\"}},\"token\":{\"username\":\""
			+ username
			+ "\",\"domain\":\""
			+ domain
			+ "\"},\"organizationId\":"
			+ OrgID + "}";

	System.out.println(userData);
	
	createUserResponse = given().cookie("eim_session", sessionid)
			.contentType("application/x-www-form-urlencoded")
			.formParam("data", userData).filter(sessionFilter).when()
			.post(baseURL + "/rest/service/esm-service/user.createLdapUser");
	createUserResponse.prettyPrint();
	jp = createUserResponse.jsonPath();
	Boolean check = jp.get("success");
	if(check == false)
	{
		createUserResponse.prettyPrint();
		Assert.assertTrue(false, "API Call Failed");
	}
	Reporter.log("generated User id is:" + jp.get("data.id"), true);
}

public void AddESMUser(String username, String password, String firstname, String lastname, String title, String email, int OrgID)
{
	
	Response createESMUserResponse;
	
	String userData;
	char[] passwordArray = password.toCharArray();
	StringBuilder pass = new StringBuilder();
	for(int i=0; i<passwordArray.length; i++)
	{

	if(i == 0)
	{
		pass.append(passwordArray[i] + "\"" + ",");
	}
		
	if(i != passwordArray.length-1)
	{
		pass.append("\"" + passwordArray[i] + "\"" + ",");
	}
	else
	{
		pass.append("\"" + passwordArray[i]);
	}
	}
	
	// rest call to create a user
	userData = "{\"user\":{\"canonicalName\":\""
			+ title + " " + firstname + " " + lastname
			+ "\",\"humanUser\":true,\"active\":true,\"deleted\":false,"
			+ "\"username\":\""
			+ username
			+ "\",\"contact\":{\"firstName\":\""
			+ firstname
			+ "\",\"middleName\":\""
			+ "\",\"lastName\":\""
			+ lastname
			+ "\","
			+ "\"salutation\":\""
			+ title
			+ "\",\"emailAddress\":\""
			+ email
			+ "\"}},\"token\":{\"username\":\""
			+ username
			+ "\","
			+ "\"password\":[\""
			+ pass
			+ "\"]},\"organizationId\":"
			+ OrgID + "}";
	
	System.out.println(userData);

	createESMUserResponse = given().cookie("eim_session", sessionid)
			.contentType("application/x-www-form-urlencoded")
			.formParam("data", userData).filter(sessionFilter).when()
			.post(baseURL + "/rest/service/esm-service/user.createUser");
	createESMUserResponse.prettyPrint();
	jp = createESMUserResponse.jsonPath();
	Boolean check = jp.get("success");
	if(check == false)
	{
		createESMUserResponse.prettyPrint();
		Assert.assertTrue(false, "API Call Failed");
	}
	
	Reporter.log("generated User id is:" + jp.get("data.id"), true);
}




}
