package com.edifecs.resource.validation.service.helpers;

import java.sql.Connection;
import java.sql.DriverManager;

public final class MSSQLConnection {

	private MSSQLConnection() {
	}
	
	public static boolean validate(String serverType, String hostname,
			int port, String dbname, String username, String password)
			throws DBConnectionException {
		boolean connected = false;

		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");

			if (null == serverType) {
				serverType = "sqlserver";
			} else if (!ServerType.contain(serverType)) {
				throw new Exception("Server Type must be either of: " + ServerType.values());
			}

			String url = "jdbc:jtds:" + serverType + "://" + hostname + ":"
					+ port + ";databaseName=" + dbname;

			Connection connection = DriverManager.getConnection(url, username, password);

			if (null != connection) {
				connected = true;
			}

			connection.close();
		} catch (Exception e) {
			throw new DBConnectionException(e);
		}

		return connected;
	}

	public static boolean validate(DBConnection con)
			throws DBConnectionException {
		return validate(con.getServerType(), con.getServer(), con.getPort(),
				con.getDatabase(), con.getUser(), con.getPassword());
	}

	public enum ServerType {
		SQLSERVER("sqlserver"), SYBASE("sybase");

		private String text;

		private ServerType(final String newText) {
			text = newText;
		}

		/**
		 * Gets the string value of the ServerType Enum.
		 * 
		 * @return String representation of the ServerType Enum
		 */
		public final String getText() {
			return text;
		}

		/**
		 * @param serverType
		 *            Check whether the serverType is valid
		 * 
		 * @return true if serverType is valid else false
		 */
		public static boolean contain(String serverType) {
			for (ServerType type : ServerType.values()) {
				if (type.getText().equals(serverType)) {
					return true;
				}
			}

			return false;
		}
	}
}
