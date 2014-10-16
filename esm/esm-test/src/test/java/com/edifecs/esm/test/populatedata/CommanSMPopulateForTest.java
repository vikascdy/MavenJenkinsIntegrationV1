package com.edifecs.esm.test.populatedata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.testng.Reporter;
import org.testng.annotations.Test;

import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.GetDataFromPropertyFile;
import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.steadystate.css.parser.selectors.SuffixAttributeConditionImpl;

import static com.jayway.restassured.RestAssured.given;

public class CommanSMPopulateForTest extends DriverBase {

	//String baseURL = GetBaseUrl();
	String baseURL = "http://smbox:8080";

	JsonPath jp;
	Response loginResponse;
	Response createTenantResponse;
	Response createTenantOrganizationResponse;
	Response createTenantRoleResponse;
	Response createTenantGroupResponse;
	Response createUserResponse;
	Response createSubOrganizationResponse1;
	Response createSubOrganizationResponse2;
	Response addRoleToOrganizationResponse;
	SessionFilter sessionFilter = new SessionFilter();

	// Session filter is used to record the session Id returned from server and
	// also apply this Id for in subsequent request

	String loginData;
	String tenantData;
	String organizationData;
	String roleData;
	String groupData;
	String userData;
	String createSubOrganizationData1;
	String createSubOrganizationData2;
	String addRoleToOrganizationData;

	int tenantId;
	int organizationId;
	int tenantNo;
	int organizationNo;
	int roleNo;
	int groupNo;
	int userNo;

	int countOfTenantsToBeCreated = 2;
	int countOfOrganizationsToBeCreated = 2;
	int countOfGroupsToBeCreated = 2;
	int countOfRolesToBeCreated = 2;
	int countOfUsersToBeCreated = 2;

	int startCountTenant = 3000;
	int startCountOrganization = 2000;
	int startCountRole = 2000;
	int startCountGroup = 2000;
	int startCountUser = 2000;

	String suf = UUID.randomUUID().toString().substring(23);
	String suffix = suf.substring(1);
	String nameTenant = "Tenant" + suffix;
	String countTenant = "23";
	String descriptionTenant = "edifecs";
	String domainTenant = nameTenant + "@edifecs.com";

	public String tenantName() {
		return nameTenant;
	}

	public String tenantDomain() {
		return domainTenant;
	}

	String nameRole = "role";
	String countRole = "34";
	String descriptionRole = "role description";

	public String roleName() {
		return nameRole;
	}

	String nameGroup = "Group";
	String countGroup = "4";
	String descriptionGroup = "this is 1st group";

	public String groupName() {
		return nameGroup;
	}

	String nameOrganization = "organization";
	String descriptionOrganization = "This is Organization";
	String countOrganization = "4";
	String nameSubOrganization = "sub_organization";
	String descriptionSubOrganization = "This is Sub-Organization";

	public String organizationName() {
		return nameOrganization;
	}

	public String subOrganizationName() {
		return nameSubOrganization;
	}

	String firstNameOfUser = "user";
	String middleNameOfUser = "m";
	String lastNameOfUser = "user";
	String countUser = "dfg";
	String titleUser = "Mr";
	String emailUser = "user@user.com";
	String usernameOfUser = "user" + suffix;
	String passwordOfUser = "useruser";

	public String username() {
		return usernameOfUser;
	}

	public String password() {
		return passwordOfUser;
	}

	static String sessionIdReturned;
	Map<String, String> map = new HashMap<String, String>();

	public void login() {
		
		// Rest call for login and get a session Id
		loginData = "{\"domain\":\"_System\",\"organization\":\"edfx\",\"username\":\"admin\",\"password\":\"admin\"}";
		loginResponse = given()
				.contentType("application/x-www-form-urlencoded")
				.formParam("data", loginData).filter(sessionFilter).when()
				.post(GetBaseUrl() + "/rest/" + "login");

		loginResponse.prettyPrint();
		GetDataFromPropertyFile.saveDataInPropertiesFile("tenant", nameTenant);
		emailUser = suffix + emailUser;
		//GetDataFromPropertyFile

		map = loginResponse.getCookies();
		sessionIdReturned = map.get("eim_session");
		Reporter.log("login output is : " + sessionIdReturned);
		
	}

	public void createTenant() {
		
		// Rest call to create a tenant
		tenantData = "{\"tenant\":{\"canonicalName\":\""
				+ nameTenant
				+ "\",\"description\":\"\",\"domain\": \""
				+ domainTenant
				+ "\" ,\"environment\":\"\",\"passwordPolicy\":{},\"site\":{\"id\":1,\"canonicalName\":\"Default Site\",\"description\":\"Default Edifecs Site\",\"domain\":\"\",\"environment\":\"\"},\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"Default Site\",\"siteId\":1}}";
		createTenantResponse = given().cookie("eim_session", sessionIdReturned).contentType("application/x-www-form-urlencoded").formParam("data", tenantData).filter(sessionFilter).when().post(baseURL + "tenant.createTenant");
        jp = createTenantResponse.jsonPath();
		tenantId = jp.get("data.id");
		Reporter.log("tenant id is: " + tenantId, true);


	}

	public void createOrganization() {
		
		// rest call to create an Organization
		organizationData = "{\"tenant\":{\"id\":"
				+ tenantId
				+ ",\"canonicalName\":\""
				+ nameTenant
				+ "\",\"description\":\""
				+ descriptionTenant
				+ "\",\"domain\":\""
				+ domainTenant
				+ "\",\"environment\":\"\",\"passwordPolicy\":"
				+ "{\"id\":"
				+ tenantId
				+ ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
				+ "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
				+ "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
				+ "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
				+ "\"organization\":{\"canonicalName\":\"" + nameOrganization
				+ "\",\"description\":\"" + descriptionOrganization + "\"}}";

		createTenantOrganizationResponse = given().cookie("eim_session", sessionIdReturned)
				.contentType("application/x-www-form-urlencoded")
				.formParam("data", organizationData).filter(sessionFilter)
				.when()
				.post(baseURL + "organization.createOrganizationForTenant");

		jp = createTenantOrganizationResponse.jsonPath();
		organizationId = jp.get("data.id");
		Reporter.log("Organization Id is:" + organizationId, true);


	}

	public void createSubOrganization() {
		
		// rest call to create a Sub-Organization
		createSubOrganizationData1 = "{\"tenant\":{\"id\":"
				+ tenantId
				+ ",\"canonicalName\":\""
				+ nameTenant
				+ "\",\"description\":\""
				+ descriptionTenant
				+ "\",\"domain\":\""
				+ domainTenant
				+ "\",\"environment\":\"\",\"passwordPolicy\":"
				+ "{\"id\":"
				+ tenantId
				+ ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
				+ "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
				+ "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
				+ "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
				+ "\"organization\":{\"canonicalName\":\""
				+ nameSubOrganization + "\",\"description\":\""
				+ descriptionSubOrganization + "\"}}";

		createSubOrganizationResponse1 = given().cookie("eim_session", sessionIdReturned)
				.contentType("application/x-www-form-urlencoded")
				.formParam("data", createSubOrganizationData1)
				.filter(sessionFilter).when()
				.post(baseURL + "organization.createOrganizationForTenant");

		jp = createSubOrganizationResponse1.jsonPath();
		int subOrganizationId = jp.get("data.id");
		Reporter.log("Sub Organization Id is:" + subOrganizationId, true);

		createSubOrganizationData2 = "{\"organizationId\": \"" + organizationId
				+ "\",\"childOrganizationId\": \"" + subOrganizationId + "\"}";

		createSubOrganizationResponse2 = given().cookie("eim_session", sessionIdReturned)
				.contentType("application/x-www-form-urlencoded")
				.formParam("data", createSubOrganizationData2)
				.filter(sessionFilter).when()
				.post(baseURL + "organization.addChildOrganization");
		Reporter.log("final sub organization is created", true);


	}
	
	public void createUser(){
		
		// rest call to create a user
				userData = "{\"user\":{\"canonicalName\":\""
						+ usernameOfUser
						+ "\",\"humanUser\":true,\"active\":true,\"deleted\":false,"
						+ "\"username\":\""
						+ usernameOfUser
						+ "\",\"contact\":{\"firstName\":\""
						+ firstNameOfUser
						+ "\",\"middleName\":\""
						+ middleNameOfUser
						+ "\",\"lastName\":\""
						+ lastNameOfUser
						+ "\","
						+ "\"salutation\":\"\",\"emailAddress\":\""
						+ emailUser
						+ "\"}},\"token\":{\"username\":\""
						+ usernameOfUser
						+ "\","
						+ "\"password\":[\"u\",\"s\",\"e\",\"r\",\"u\",\"s\",\"e\",\"r\"]},\"organizationId\":"
						+ organizationId + "}";

				createUserResponse = given().cookie("eim_session", sessionIdReturned)
						.contentType("application/x-www-form-urlencoded")
						.formParam("data", userData).filter(sessionFilter).when()
						.post(baseURL + "user.createUser");
				jp = createUserResponse.jsonPath();
				Reporter.log("generated User id is:" + jp.get("data.id"), true);


	}
	
	public void createRole(){
		
		// Rest call to create a role
				roleData = "{\"tenant\":{\"id\":"
						+ tenantId
						+ ",\"canonicalName\":\""
						+ nameTenant
						+ "\",\"description\":\""
						+ descriptionTenant
						+ "\",\"domain\":\""
						+ domainTenant
						+ "\",\"environment\":\"\",\"passwordPolicy\":"
						+ "{\"id\":"
						+ tenantId
						+ ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
						+ "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
						+ "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
						+ "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
						+ "\"role\":{\"canonicalName\":\"" + nameRole
						+ "\",\"description\":\"" + descriptionRole + "\"}}";

				createTenantRoleResponse = given().cookie("eim_session", sessionIdReturned)
						.contentType("application/x-www-form-urlencoded")
						.formParam("data", roleData).filter(sessionFilter).when()
						.post(baseURL + "role.createRoleForTenant");

				jp = createTenantRoleResponse.jsonPath();
				int roleId = jp.get("data.id");
				Reporter.log("Role generated id is:" + jp.get("data.id"), true);


	}
	public void assignRole(){
		
		// Not clear if the functionality be removed
				/*
				 * // rest call to assign a role to organization
				 * addRoleToOrganizationData = "{\"organizationId\": \"" +
				 * organizationId + "\",\"roles\":[{\"id\": \"" + roleId +
				 * "\",\"canonicalName\":\"" + nameRole + "\",\"description\":\"" +
				 * descriptionRole + "\"}]}"; addRoleToOrganizationResponse = given()
				 * .contentType("application/x-www-form-urlencoded") .formParam("data",
				 * addRoleToOrganizationData) .filter(sessionFilter).when()
				 * .post(baseURL + "organization.addRolesToOrganization");
				 * 
				 * Reporter.log("role assigned to an organization :" , true);
				 * addRoleToOrganizationResponse.prettyPrint();
				 */
	}
	
	public void createGroup(){
		
		// Rest call to create a group
				groupData = "{\"tenant\":{\"id\":"
						+ tenantId
						+ ",\"canonicalName\":\""
						+ nameTenant
						+ "\",\"description\":\""
						+ descriptionTenant
						+ "\",\"domain\":\""
						+ domainTenant
						+ "\",\"environment\":\"\",\"passwordPolicy\":"
						+ "{\"id\":"
						+ tenantId
						+ ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
						+ "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
						+ "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
						+ "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
						+ "\"group\":{\"canonicalName\":\"" + nameGroup
						+ "\",\"description\":\"" + descriptionGroup + "\"}}";

				createTenantGroupResponse = given().cookie("eim_session", sessionIdReturned)
						.contentType("application/x-www-form-urlencoded")
						.formParam("data", groupData).filter(sessionFilter).when()
						.post(baseURL + "group.createGroupForTenant");

				jp = createTenantGroupResponse.jsonPath();
				Reporter.log("Group generated id is:" + jp.get("data.id"), true);

	}
	
	@Test
	public void createTestItems() {

		baseURL = GetBaseUrl() + "/rest/service/esm-service/";
		
		login();
		createTenant();
		createOrganization();
		createSubOrganization();
		createUser();
		createRole();
		createGroup();
	}

	public void createOnlyTestTemplateItems() {

		baseURL = GetBaseUrl() + "/rest/service/esm-service/";
		
		login();
		createTenant();
		createOrganization();
		createRole();
		createGroup();
	}
}	
