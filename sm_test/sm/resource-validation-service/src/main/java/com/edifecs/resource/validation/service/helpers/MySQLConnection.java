package com.edifecs.resource.validation.service.helpers;

import java.sql.DriverManager;
import java.sql.Connection;

public final class MySQLConnection {
	
	private MySQLConnection() {
	}
	
	public static boolean validate(String hostname, int port, String dbname,
			String username, String password) throws DBConnectionException {
		boolean connected = false;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			String url = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname;
			Connection connection = (Connection) DriverManager.getConnection(url, username, password);

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
		return validate(con.getServer(), con.getPort(), con.getDatabase(),
				con.getUser(), con.getPassword());
	}
}
