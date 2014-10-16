package com.edifecs.resource.validation.service.helpers;

import java.io.Serializable;

public class DBConnection implements Serializable {

	private static final long serialVersionUID = -3185110005670287278L;

	private String serverType;
	private String server;

	private String database;
	private String user;
	private String password;
	private int port;

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public final String toString() {
		return "Server Type: " + getServerType() + ", Server: " + getServer() + ", Port: " + getPort()
				+ ", Database: " + getDatabase() + ", User: " + getUser() + ", Password: " + getPassword();
	}
}