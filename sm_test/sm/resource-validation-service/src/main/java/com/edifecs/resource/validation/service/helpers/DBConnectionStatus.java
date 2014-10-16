package com.edifecs.resource.validation.service.helpers;

public class DBConnectionStatus {
	private boolean connected;
	private String exception;
	
	public DBConnectionStatus() {
		connected = false;
		exception = null;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public String getException() {
		return exception;
	}
	
	public void setException(String exception) {
		this.exception = exception;
	}
	
	@Override
	public String toString() {
		return String.format("connected = " + isConnected() + " exception = " +  getException());
	}

}
