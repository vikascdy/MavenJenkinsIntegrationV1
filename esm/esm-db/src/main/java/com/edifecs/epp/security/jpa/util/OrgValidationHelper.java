package com.edifecs.epp.security.jpa.util;

import java.util.List;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.GroupsValidator;
import com.edifecs.epp.security.jpa.util.TenantValidationHelper.RolesValidator;



/**
 * Mapper classes for mapping with json format, specified in the json file
 *
 * @author abhising
 */
public class OrgValidationHelper {

	
	private String name;
	
	private String errorCode;
	
	private List<UsersValidator> users;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return errorCode;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the userList
	 */
	public List<UsersValidator> getUserList() {
		return users;
	}

	/**
	 * @param userList the userList to set
	 */
	public void setUserList(List<UsersValidator> userList) {
		this.users = userList;
	}

	public class UsersValidator {
		private String name;
		private String errorCode;
		private List<RolesValidator> roleValidator;
		private List<GroupsValidator> groupValidator;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the errorCode
		 */
		public String getErrorCode() {
			return errorCode;
		}
		/**
		 * @param errorCode the errorCode to set
		 */
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		/**
		 * @return the roleValidator
		 */
		public List<RolesValidator> getRoleValidator() {
			return roleValidator;
		}
		/**
		 * @param roleValidator the roleValidator to set
		 */
		public void setRoleValidator(List<RolesValidator> roleValidator) {
			this.roleValidator = roleValidator;
		}
		/**
		 * @return the groupValidator
		 */
		public List<GroupsValidator> getGroupValidator() {
			return groupValidator;
		}
		/**
		 * @param groupValidator the groupValidator to set
		 */
		public void setGroupValidator(List<GroupsValidator> groupValidator) {
			this.groupValidator = groupValidator;
		}
		
	}
}