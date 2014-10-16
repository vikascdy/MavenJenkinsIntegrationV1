package com.edifecs.epp.security.jpa.util;

import java.util.List;

import com.edifecs.epp.security.jpa.util.OrgValidationHelper.UsersValidator;

public class TenantValidationHelper {

	private String name;
	
	private String description;
	
	private List<OrgValidationHelper> organization;
	
	private List<RolesValidator> rolesValidators;
	
	private List<GroupsValidator> groupsValidators;



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
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the organization
	 */
	public List<OrgValidationHelper> getOrganization() {
		return organization;
	}


	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(List<OrgValidationHelper> organization) {
		this.organization = organization;
	}


	/**
	 * @return the roles
	 */
	public List<RolesValidator> getRoles() {
		return rolesValidators;
	}


	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<RolesValidator> roles) {
		this.rolesValidators = roles;
	}


	/**
	 * @return the groups
	 */
	public List<GroupsValidator> getGroups() {
		return groupsValidators;
	}


	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<GroupsValidator> groups) {
		this.groupsValidators = groups;
	}


	/**
	 * RolesValidator in Tenant
	 * @author mayaguru
	 *
	 */
	public class RolesValidator {
		
		private String name;
		
		private String errorCodes;
		

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
			return errorCodes;
		}

		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.errorCodes = description;
		}
	}
	
	
	/**
	 * Group Handler in Tenant
	 * @author mayaguru
	 *
	 */
	public class GroupsValidator {
		
		private String name;
		
		private String errorCodes;
		
		private List<RolesValidator> rolesValidator;
		
		private List<UsersValidator> usersValidator;
		
		private List<OrgValidationHelper> orgValidator;

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
		 * @return the errorCodes
		 */
		public String getErrorCodes() {
			return errorCodes;
		}

		/**
		 * @param errorCodes the errorCodes to set
		 */
		public void setErrorCodes(String errorCodes) {
			this.errorCodes = errorCodes;
		}

		/**
		 * @return the rolesValidator
		 */
		public List<RolesValidator> getRolesValidator() {
			return rolesValidator;
		}

		/**
		 * @param rolesValidator the rolesValidator to set
		 */
		public void setRolesValidator(List<RolesValidator> rolesValidator) {
			this.rolesValidator = rolesValidator;
		}

		/**
		 * @return the usersValidator
		 */
		public List<UsersValidator> getUsersValidator() {
			return usersValidator;
		}

		/**
		 * @param usersValidator the usersValidator to set
		 */
		public void setUsersValidator(List<UsersValidator> usersValidator) {
			this.usersValidator = usersValidator;
		}

		/**
		 * @return the orgValidator
		 */
		public List<OrgValidationHelper> getOrgValidator() {
			return orgValidator;
		}

		/**
		 * @param orgValidator the orgValidator to set
		 */
		public void setOrgValidator(List<OrgValidationHelper> orgValidator) {
			this.orgValidator = orgValidator;
		}

	}
}