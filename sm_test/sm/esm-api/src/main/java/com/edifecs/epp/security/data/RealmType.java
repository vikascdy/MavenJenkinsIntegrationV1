package com.edifecs.epp.security.data;
public enum RealmType {

		/**
		 * The ldap.
		 */
		LDAP("LDAP"),
		/**
		 * Active Directory.
		 */
		ACTIVEDIRECTORY("ACTIVEDIRECTORY"),
		/**
		 * The database.
		 */
		DATABASE("DATABASE");

		/**
		 * The realm.
		 */
		private String realm;

		/**
		 * Instantiates a new realm type.
		 * 
		 * @param realm
		 *            the realm
		 */
		private RealmType(String realm) {
			this.realm = realm;
		}

		/**
		 * Gets the val.
		 * 
		 * @return the val
		 */
		public String getVal() {
			return realm;
		}

		public static RealmType fromString(String realmType) {
			for (RealmType r : RealmType.values()) {
				if (r.getVal().equalsIgnoreCase(realmType))
					return r;
			}
			return null;
		}
	}