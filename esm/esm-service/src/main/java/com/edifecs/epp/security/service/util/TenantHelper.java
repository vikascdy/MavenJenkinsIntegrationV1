package com.edifecs.epp.security.service.util;

import java.util.List;

public class TenantHelper {

	private String name;
	
	private String description;
	
	private List<UserJsonHelper> organization;
	
	private List<Roles> roles;
	
	private List<Groups> groups;

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
	 * @return the listOfOrganization
	 */
	public List<UserJsonHelper> getListOfOrganization() {
		return organization;
	}


	/**
	 * @param listOfOrganization the listOfOrganization to set
	 */
	public void setListOfOrganization(List<UserJsonHelper> listOfOrganization) {
		this.organization = listOfOrganization;
	}


	/**
	 * @return the listOfRoles
	 */
	public List<Roles> getListOfRoles() {
		return roles;
	}


	/**
	 * @param listOfRoles the listOfRoles to set
	 */
	public void setListOfRoles(List<Roles> listOfRoles) {
		this.roles = listOfRoles;
	}


	/**
	 * @return the listOfGroups
	 */
	public List<Groups> getListOfGroups() {
		return groups;
	}


	/**
	 * @param listOfGroups the listOfGroups to set
	 */
	public void setListOfGroups(List<Groups> listOfGroups) {
		this.groups = listOfGroups;
	}


	/**
	 * Role Handler in Tenant
	 * @author mayaguru
	 *
	 */
	public class Roles {
		private String name;
		
		private String description;
		
		private List<String> permissions;

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return (name + "-" + description + "-" + "("+permissions+")");
		}
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
		 * @return the permissions
		 */
		public List<String> getPermissions() {
			return permissions;
		}

		/**
		 * @param permissions the permissions to set
		 */
		public void setPermissions(List<String> permissions) {
			this.permissions = permissions;
		}
	}
	
	
	/**
	 * Group Handler in Tenant
	 * @author mayaguru
	 *
	 */
	public class Groups {
		
		private String name;
		
		private String description;
		
		private List<String> roles;
		
		private List<String> users;
		
		private List<String> organizations;

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
		 * @return the roles
		 */
		public List<String> getRoles() {
			return roles;
		}

		/**
		 * @param roles the roles to set
		 */
		public void setRoles(List<String> roles) {
			this.roles = roles;
		}

		/**
		 * @return the users
		 */
		public List<String> getUsers() {
			return users;
		}

		/**
		 * @param users the users to set
		 */
		public void setUsers(List<String> users) {
			this.users = users;
		}

		/**
		 * @return the organizations
		 */
		public List<String> getOrganizations() {
			return organizations;
		}

		/**
		 * @param organizations the organizations to set
		 */
		public void setOrganizations(List<String> organizations) {
			this.organizations = organizations;
		}
	}

}