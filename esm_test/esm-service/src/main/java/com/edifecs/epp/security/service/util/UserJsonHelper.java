package com.edifecs.epp.security.service.util;

import java.util.List;



/**
 * Mapper classes for mapping with json format, specified in the json file
 *
 * @author abhising
 */
public class UserJsonHelper {

	
	private String name;
	
	private String description;
	
	private List<importUsers> users;
	
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
	 * @return the userList
	 */
	public List<importUsers> getUserList() {
		return users;
	}

	/**
	 * @param userList the userList to set
	 */
	public void setUserList(List<importUsers> userList) {
		this.users = userList;
	}

	public class importUsers {
		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}
		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}
		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}
		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}
		/**
		 * @return the first_name
		 */
		public String getFirst_name() {
			return first_name;
		}
		/**
		 * @param first_name the first_name to set
		 */
		public void setFirst_name(String first_name) {
			this.first_name = first_name;
		}
		/**
		 * @return the middle_name
		 */
		public String getMiddle_name() {
			return middle_name;
		}
		/**
		 * @param middle_name the middle_name to set
		 */
		public void setMiddle_name(String middle_name) {
			this.middle_name = middle_name;
		}
		/**
		 * @return the last_name
		 */
		public String getLast_name() {
			return last_name;
		}
		/**
		 * @param last_name the last_name to set
		 */
		public void setLast_name(String last_name) {
			this.last_name = last_name;
		}
		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}
		/**
		 * @param title the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}
		/**
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}
		/**
		 * @param email the email to set
		 */
		public void setEmail(String email) {
			this.email = email;
		}
		/**
		 * @return the assigned_roles
		 */
		public List<String> getAssigned_roles() {
			return assigned_roles;
		}
		/**
		 * @param assigned_roles the assigned_roles to set
		 */
		public void setAssigned_roles(List<String> assigned_roles) {
			this.assigned_roles = assigned_roles;
		}
		/**
		 * @return the assigned_groups
		 */
		public List<String> getAssigned_groups() {
			return assigned_groups;
		}
		/**
		 * @param assigned_groups the assigned_groups to set
		 */
		public void setAssigned_groups(List<String> assigned_groups) {
			this.assigned_groups = assigned_groups;
		}
		private String username;
		private String password;
		private String first_name;
		private String middle_name;
		private String last_name;
		private String title;
		private String email;
		private List<String> assigned_roles;
		private List<String> assigned_groups;
		
		@Override
	    public String toString() {
	        return username + " - " + password + "-" + first_name + "-"+ middle_name + 
	        		"-"+ last_name +  "-"+ email +  " (" + assigned_roles + ")" +  " (" + assigned_groups + ")";
	    }
	}
}