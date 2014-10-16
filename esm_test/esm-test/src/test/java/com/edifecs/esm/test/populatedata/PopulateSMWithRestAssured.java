package com.edifecs.esm.test.populatedata;

import com.edifecs.test.common.DriverBase;
import com.edifecs.test.common.HtmlReporter;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;

import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * created by laksagga on 15/07/2014
 * By default test of the class has been commented as they are individual test only
 * However all these tests are running
 * They could be used in case individuallybtest need to be run
 * we are only using expansive tests at present 
 * */
public class PopulateSMWithRestAssured extends DriverBase {

    JsonPath jp;
    Response loginResponse;
    Response createTenantResponse;
    Response createTenantOrganizationResponse;
    Response createTenantRoleResponse;
    Response createTenantGroupResponse;
    Response createUserResponse;
    SessionFilter sessionFilter = new SessionFilter();
    // Session filter is used to record the session Id returned from server and
    // also apply this Id for in subsequent request

    WebDriver driver;
    String baseURL;
    String loginData;
    String tenantData;
    String organizationData;
    String roleData;
    String groupData;
    String userData;

    int tenantNo;
    int tenantId;
    int organizationId;
    int organizationNo;
    int roleNo;
    int groupNo;
    int userNo;

    int countOfTenantsToBeCreated = 1;
    int countOfOrganizationsToBeCreated = 100000;
    int countOfGroupsToBeCreated = 0;
    int countOfRolesToBeCreated = 0;
    int countOfUsersToBeCreated = 0;

    int startCountTenant = 10;
    int startCountOrganization = 200000;
    int startCountRole = 2000;
    int startCountGroup = 2000;
    int startCountUser = 2000;
    static String sessionIdReturned;
    String suffix = UUID.randomUUID().toString().substring(24);
    Long startTime;
    
    @BeforeClass
    public void InitTest() {
        baseURL = GetBaseUrl() + "/rest/service/esm-service/";
    }
    
    @BeforeTest
    public void beforeTest(){
    	startTime = System.currentTimeMillis();
    }

    @AfterTest
    public void afterTest(){
    	System.out.println("test time is :" +  (System.currentTimeMillis() - startTime));
    }
    @Test
    public void PopulateDataViaRestAPI() {


        loginData = "{\"domain\":\"_System\",\"organization\":\"edfx\",\"username\":\"admin\",\"password\":\"admin\"}";
        loginResponse = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("data", loginData).filter(sessionFilter).when()
                .post(GetBaseUrl() + "/rest/" + "login");

        Map<String, String> map = new HashMap<>();
        map = loginResponse.getCookies();
        int size = map.size();
        boolean success = false;
        sessionIdReturned = map.get("eim_session");
        for (tenantNo = startCountTenant; tenantNo < startCountTenant + countOfTenantsToBeCreated; tenantNo++) {
            tenantData = "{\"tenant\":{\"canonicalName\":\"tenant" + tenantNo + suffix + "\",\"description\":\"tenant" + tenantNo + suffix + "\",\"domain\":\"tenant" + tenantNo + suffix + "\",\"environment\":\"\",\"passwordPolicy\":{},\"site\":{\"id\":1,\"canonicalName\":\"Default Site\",\"description\":\"Default Edifecs Site\",\"domain\":\"\",\"environment\":\"\"},\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"Default Site\",\"siteId\":1}}";
            createTenantResponse = given().cookie("eim_session", sessionIdReturned).contentType("application/x-www-form-urlencoded").formParam("data", tenantData).filter(sessionFilter).when().post(baseURL + "tenant.createTenant");
            jp = createTenantResponse.jsonPath();
            success = jp.getBoolean("success");
            if (!success) {
                HtmlReporter.log("success:false", true);
                HtmlReporter.log(jp.prettyPrint(),  true);
                break;
            }
            tenantId = jp.get("data.id");
            HtmlReporter.log("tenant info :" + tenantId , true);
            if (tenantNo < (startCountTenant + countOfTenantsToBeCreated)) {
                for (organizationNo = startCountOrganization; organizationNo < startCountOrganization + countOfOrganizationsToBeCreated; organizationNo++) {
                    organizationData = "{\"tenant\":{\"id\":" + tenantId + ",\"canonicalName\":\"tenant" + tenantNo + suffix + "\",\"description\":\"tenant" + tenantNo + suffix + "\",\"domain\":\"tenant" + tenantNo + suffix + "\",\"environment\":\"\",\"passwordPolicy\":"
                            + "{\"id\":" + tenantId + ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
                            + "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
                            + "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
                            + "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
                            + "\"organization\":{\"canonicalName\":\"orgaization" + tenantNo + "" + organizationNo + "\",\"description\":\"organization" + tenantNo + "" + organizationNo + "\"}}";

                    createTenantOrganizationResponse = given().cookie("eim_session", sessionIdReturned).contentType("application/x-www-form-urlencoded").formParam("data", organizationData).filter(sessionFilter).when().post(baseURL + "organization.createOrganizationForTenant");

                    jp = createTenantOrganizationResponse.jsonPath();
                    success = jp.getBoolean("success");
                    if (!success) {
                        HtmlReporter.log("success:false", true);
                        HtmlReporter.log(jp.prettyPrint(),  true);
                        break;
                    }
                    organizationId = jp.get("data.id");

                    if (organizationNo < (startCountOrganization + countOfOrganizationsToBeCreated)) {
                        for (userNo = startCountUser; userNo < startCountUser + countOfUsersToBeCreated; userNo++) {
                            userData = "{\"user\":{\"canonicalName\":\" user user" + tenantNo + "" + organizationNo + "" + userNo + suffix + "\",\"humanUser\":true,\"active\":true,\"deleted\":false,"
                                    + "\"username\":\"user" + tenantNo + "" + organizationNo + "" + userNo + suffix + "\",\"contact\":{\"firstName\":\"user" + tenantNo + "" + organizationNo + "" + userNo + suffix + "\",\"middleName\":\"\",\"lastName\":\"user" + tenantNo + "" + organizationNo + "" + userNo + suffix + "\","
                                    + "\"salutation\":\"Mr\",\"emailAddress\":\"user" + tenantNo + "" + organizationNo + "" + userNo + suffix + "@user.com\"}},\"token\":{\"username\":\"user" + tenantNo + "" + organizationNo + "" + userNo + suffix + "\","
                                    + "\"password\":[\"u\",\"s\",\"e\",\"r\",\"1\",\"u\",\"s\",\"e\",\"r\",\"1\"]},\"organizationId\":" + organizationId + "}";
                            createUserResponse = given().cookie("eim_session", sessionIdReturned).contentType("application/x-www-form-urlencoded").formParam("data", userData).filter(sessionFilter).when().post(baseURL + "user.createUser");
                            jp = createUserResponse.jsonPath();
                            success = jp.getBoolean("success");
                            if (!success) {
                                HtmlReporter.log("success:false", true);
                                HtmlReporter.log(jp.prettyPrint(),  true);
                                break;
                            }
                        }
                    }
                }

                for (roleNo = startCountRole; roleNo < startCountRole + countOfRolesToBeCreated; roleNo++) {
                    roleData = "{\"tenant\":{\"id\":" + tenantId + ",\"canonicalName\":\"tenant" + tenantId + "\",\"description\":\"tenant" + tenantId + "\",\"domain\":\"tenant" + tenantId + "\",\"environment\":\"\",\"passwordPolicy\":"
                            + "{\"id\":" + tenantId + ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
                            + "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
                            + "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
                            + "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
                            + "\"role\":{\"canonicalName\":\"role" + tenantNo + "" + roleNo + "\",\"description\":\"role" + tenantNo + "" + roleNo + "\"}}";

                    createTenantRoleResponse = given().cookie("eim_session", sessionIdReturned).contentType("application/x-www-form-urlencoded").formParam("data", roleData).filter(sessionFilter).when().post(baseURL + "role.createRoleForTenant");

                    jp = createTenantRoleResponse.jsonPath();
                    success = jp.getBoolean("success");
                    if (!success) {
                        HtmlReporter.log("success:false", true);
                        HtmlReporter.log(jp.prettyPrint(),  true);
                        break;
                    }
                }
                for (groupNo = startCountGroup; groupNo < startCountGroup + countOfGroupsToBeCreated; groupNo++) {
                    groupData = "{\"tenant\":{\"id\":" + tenantId + ",\"canonicalName\":\"tenant" + tenantNo + "\",\"description\":\"tenant" + tenantNo + "\",\"domain\":\"tenant" + tenantNo + "\",\"environment\":\"\",\"passwordPolicy\":"
                            + "{\"id\":" + tenantId + ",\"passwdHistory\":3,\"passwdAge\":120,\"passwdMaxFailure\":5,\"passwdResetFailureLockout\":5,\"changePasswdAtFirstLogin\":false,"
                            + "\"passwdLockoutDuration\":15,\"passwdRegex\":\"^([a-zA-Z0-9@*#]{8,15})$\",\"passwdRegexDesc\":\"Password matching expression. Match all alphanumeric "
                            + "character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters\"},"
                            + "\"site\":\"\",\"logo\":\"\",\"landingPage\":\"\",\"siteName\":\"NA\",\"siteId\":null},"
                            + "\"group\":{\"canonicalName\":\"group" + tenantNo + "" + groupNo + "\",\"description\":\"group" + tenantNo + "" + groupNo + "\"}}";

                    createTenantGroupResponse = given().cookie("eim_session", sessionIdReturned).contentType("application/x-www-form-urlencoded").formParam("data", groupData).filter(sessionFilter).when().post(baseURL + "group.createGroupForTenant");
                    jp = createTenantGroupResponse.jsonPath();
                    success = jp.getBoolean("success");
                    if (!success) {
                        HtmlReporter.log("success:false", true);
                        HtmlReporter.log(jp.prettyPrint(),  true);
                        break;
                    }
                }
            }
        }
    }
}
		

