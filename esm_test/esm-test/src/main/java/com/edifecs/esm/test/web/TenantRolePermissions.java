package com.edifecs.esm.test.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.edifecs.test.common.ActionDriver;

public class TenantRolePermissions extends ActionDriver{

	private WebDriver webDriver;
	
	public TenantRolePermissions(WebDriver webDriver){
		super(webDriver);
		this.webDriver = webDriver;
		
	} 
	
		//CheckBox for the Group
		By permissionSecurityAdminGroupCreateLocator = By.name("platform:security:administrative:group:create");
		By permissionSecurityAdminGroupDeleteLocator = By.name("platform:security:administrative:group:delete");
		By permissionSecurityAdminGroupEditLocator = By.name("platform:security:administrative:group:edit");
		By permissionSecurityAdminGroupListLocator = By.name("platform:security:administrative:group:list");
		By permissionSecurityAdminGroupViewLocator = By.name("platform:security:administrative:group:view");
		
		public void securityAdminAllGroupPermission(){
			safeClick(permissionSecurityAdminGroupCreateLocator);
			safeClick(permissionSecurityAdminGroupDeleteLocator);
			safeClick(permissionSecurityAdminGroupEditLocator);
			safeClick(permissionSecurityAdminGroupListLocator);
			safeClick(permissionSecurityAdminGroupViewLocator);		
		}
		public void createGroup(){
			safeClick(permissionSecurityAdminGroupCreateLocator);
		}
		public void deleteGroup(){
			safeClick(permissionSecurityAdminGroupDeleteLocator);
		}
		public void editGroup(){
			safeClick(permissionSecurityAdminGroupEditLocator);
		}
		public void viewGroup(){
			safeClick(permissionSecurityAdminGroupViewLocator);
		}
		public void listGroup(){
			safeClick(permissionSecurityAdminGroupListLocator);
		}
		
		//Checkbox for the Organization	
		By permissionSecurityAdminOrgCreateLocator = By.name("platform:security:administrative:organization:create");
		By permissionSecurityAdminOrgDeleteLocator = By.name("platform:security:administrative:organization:delete");
		By permissionSecurityAdminOrgEditLocator = By.name("platform:security:administrative:organization:edit");
		By permissionSecurityAdminOrgListLocator = By.name("platform:security:administrative:organization:list");
		By permissionSecurityAdminOrgViewLocator = By.name("platform:security:administrative:organization:view");
		
		public void securityAdminAllOrgPermission(){
			safeClick(permissionSecurityAdminOrgCreateLocator);
			safeClick(permissionSecurityAdminOrgDeleteLocator);
			safeClick(permissionSecurityAdminOrgEditLocator);
			//safeClick(permissionSecurityAdminOrgListLocator);
			safeClick(permissionSecurityAdminOrgViewLocator);	
		}
		public void createOrg(){
			safeClick(permissionSecurityAdminOrgCreateLocator);
		}
		public void deleteOrg(){
			safeClick(permissionSecurityAdminOrgDeleteLocator);
		}
		public void editOrg(){
			safeClick(permissionSecurityAdminOrgEditLocator);
		}
		public void viewOrg(){
			safeClick(permissionSecurityAdminOrgViewLocator);
		}
		/*public void listOrg(){
			safeClick(permissionSecurityAdminOrgListLocator);
		}
		*/
		
		//Checkbox for the Permission
		By permissionSecurityAdminPermissionCreateLocator = By.name("platform:security:administrative:permission:create");
		By permissionSecurityAdminPermissionDeleteLocator = By.id("platform:security:administrative:permission:delete");
		By permissionSecurityAdminPermissionEditLocator = By.id("platform:security:administrative:permission:edit");
		By permissionSecurityAdminPermissionListLocator = By.id("platform:security:administrative:permission:list");
		By permissionSecurityAdminPermissionViewLocator = By.id("platform:security:administrative:permission:view");
		
		public void securityAdminAllPermissionPermission(){
		//	safeClick(permissionSecurityAdminPermissionCreateLocator);
		//	safeClick(permissionSecurityAdminPermissionDeleteLocator);
		//	safeClick(permissionSecurityAdminPermissionEditLocator);
		//	safeClick(permissionSecurityAdminPermissionListLocator);
			safeClick(permissionSecurityAdminPermissionViewLocator);
		}
		public void createPermission(){
			safeClick(permissionSecurityAdminPermissionCreateLocator);
		}
		public void deletePermission(){
			safeClick(permissionSecurityAdminPermissionDeleteLocator);
		}
		public void editPermission(){
			safeClick(permissionSecurityAdminPermissionEditLocator);
		}
		public void viewPermission(){
			safeClick(permissionSecurityAdminPermissionViewLocator);
		}
		public void listPermission(){
			safeClick(permissionSecurityAdminPermissionListLocator);
		}
		
		/*//Checkbox for the Permissions
		By permissionSecurityAdminPermissionsEditLocator = By.id("permission-28");	
		
		public void securityAdminPermissions(){
			safeClick(permissionSecurityAdminPermissionsEditLocator);
		}*/
		
		//Checkbox for the Role
		By permissionSecurityAdminRoleCreateLocator = By.name("platform:security:administrative:role:create");
		By permissionSecurityAdminRoleDeleteLocator = By.name("platform:security:administrative:role:delete");
		By permissionSecurityAdminRoleEditLocator = By.name("platform:security:administrative:role:edit");
		By permissionSecurityAdminRoleListLocator = By.name("platform:security:administrative:role:list");
		By permissionSecurityAdminRoleViewLocator = By.name("platform:security:administrative:role:view");
		
		public void securityAdminAllRolePermission(){
			safeClick(permissionSecurityAdminRoleCreateLocator);
			safeClick(permissionSecurityAdminRoleDeleteLocator);
			safeClick(permissionSecurityAdminRoleEditLocator);
			//safeClick(permissionSecurityAdminRoleListLocator);
			safeClick(permissionSecurityAdminRoleViewLocator);
		}
		public void createRole(){
			safeClick(permissionSecurityAdminRoleCreateLocator);
		}
		public void deleteRole(){
			safeClick(permissionSecurityAdminRoleDeleteLocator);
		}
		public void editRole(){
			safeClick(permissionSecurityAdminRoleEditLocator);
		}
		public void viewRole(){
			safeClick(permissionSecurityAdminRoleViewLocator);
		}
		public void listRole(){
			safeClick(permissionSecurityAdminRoleListLocator);
		}
		
		By permissionSecurityAdminSiteCreateLocator = By.name("platform:security:administrative:site:create");
		By permissionSecurityAdminSiteDeleteLocator = By.name("platform:security:administrative:site:delete");
		By permissionSecurityAdminSiteEditLocator = By.name("platform:security:administrative:site:edit");
		By permissionSecurityAdminSiteViewLocator = By.name("platform:security:administrative:site:view");
		
		public void securityAdminAllSitePermission(){
			safeClick(permissionSecurityAdminSiteCreateLocator);
			safeClick(permissionSecurityAdminSiteDeleteLocator);
			safeClick(permissionSecurityAdminSiteEditLocator);
			safeClick(permissionSecurityAdminSiteViewLocator);
		}
		public void createSite(){
			safeClick(permissionSecurityAdminSiteCreateLocator);
		}
		public void deleteSite(){
			safeClick(permissionSecurityAdminSiteDeleteLocator);
		}
		public void editSite(){
			safeClick(permissionSecurityAdminSiteEditLocator);
		}
		public void viewSite(){
			safeClick(permissionSecurityAdminSiteViewLocator);
		}
		
		//Checkbox for the Tenant using xpath
		// might be required for future reference 
		//By permissionSecurityAdminTenantCreateLocator = By.xpath("//div[text()='tenant']/parent::div/parent::td/following-sibling::td//span[contains(.,'create')]/preceding-sibling::input");
		//By permissionSecurityAdminTenantDeleteLocator = By.xpath("//div[text()='tenant']/parent::div/parent::td/following-sibling::td//span[contains(.,'delete')]/preceding-sibling::input");
		//By permissionSecurityAdminTenantEditLocator = By.xpath("//div[text()='tenant']/parent::div/parent::td/following-sibling::td//span[contains(.,'edit')]/preceding-sibling::input");
		//By permissionSecurityAdminTenantListLocator = By.xpath("//div[text()='tenant']/parent::div/parent::td/following-sibling::td//span[contains(.,'list')]/preceding-sibling::input");
		//By permissionSecurityAdminTenantViewLocator =By.xpath("//div[text()='tenant']/parent::div/parent::td/following-sibling::td//span[contains(.,'view')]/preceding-sibling::input");
		
		By permissionSecurityAdminTenantCreateLocator = By.name("platform:security:administrative:tenant:create");
		By permissionSecurityAdminTenantDeleteLocator = By.name("platform:security:administrative:tenant:delete");
		By permissionSecurityAdminTenantEditLocator = By.name("platform:security:administrative:tenant:edit");
		By permissionSecurityAdminTenantListLocator = By.name("platform:security:administrative:tenant:list");
		By permissionSecurityAdminTenantViewLocator = By.name("platform:security:administrative:tenant:view");
		
		public void securityAdminAllTenantPermission(){
			safeClick(permissionSecurityAdminTenantCreateLocator);
			safeClick(permissionSecurityAdminTenantDeleteLocator);
			safeClick(permissionSecurityAdminTenantEditLocator);
			//safeClick(permissionSecurityAdminTenantListLocator);
			safeClick(permissionSecurityAdminTenantViewLocator);
		}
		public void createTenant(){
			safeClick(permissionSecurityAdminTenantCreateLocator);
		}
		public void deleteTenant(){
			safeClick(permissionSecurityAdminTenantDeleteLocator);
		}
		public void editTenant(){
			safeClick(permissionSecurityAdminTenantEditLocator);
		}
		public void viewTenant(){
			safeClick(permissionSecurityAdminTenantViewLocator);
		}
		public void listTenant(){
			safeClick(permissionSecurityAdminTenantListLocator);
		}
		
		//Checkbox for User
		By permissionSecurityAdminUserCreateLocator = By.name("platform:security:administrative:user:create");
		By permissionSecurityAdminUserDeleteLocator = By.name("platform:security:administrative:user:delete");
		By permissionSecurityAdminUserEditLocator = By.name("platform:security:administrative:user:edit");
		By permissionSecurityAdminUserImportLocator = By.name("platform:security:administrative:user:import");
		By permissionSecurityAdminUserViewLocator = By.name("platform:security:administrative:user:view");
		
		public void securityAdminAllUserPermission(){
			safeClick(permissionSecurityAdminUserCreateLocator);
			safeClick(permissionSecurityAdminUserDeleteLocator);
			safeClick(permissionSecurityAdminUserEditLocator);
			//safeClick(permissionSecurityAdminUserListLocator);
			safeClick(permissionSecurityAdminUserImportLocator);
			safeClick(permissionSecurityAdminUserViewLocator);
		}
		public void createUser(){
			safeClick(permissionSecurityAdminUserCreateLocator);
		}
		public void deleteUser(){
			safeClick(permissionSecurityAdminUserDeleteLocator);
		}
		public void editUser(){
			safeClick(permissionSecurityAdminUserEditLocator);
		}
		public void viewUser(){
			safeClick(permissionSecurityAdminUserViewLocator);
		}
		public void listUser(){
		//	safeClick(permissionSecurityAdminUserListLocator);
		}
		public void importUser(){
			safeClick(permissionSecurityAdminUserImportLocator);
		}
		By permissionsTabLinkLocator = By.linkText("Role Permissions");
		By platformTabLinkLocator = By.linkText("platform");
		By saveButtonLocator = By.linkText("Save");
		By OKButtonLocator = By.linkText("OK");
		
		public void clickPermissionTabs(){
			safeClick(permissionsTabLinkLocator);
			safeClick(platformTabLinkLocator);
		}
		public void clickSaveButton(){
			safeClick(saveButtonLocator);
			safeClick(OKButtonLocator);
			Reporter.log("after click save button ");
		}
		By refreshButtonLocator = By.linkText("Refresh");
		public void clickRefresh(){
			safeClick(refreshButtonLocator);
		}
}
