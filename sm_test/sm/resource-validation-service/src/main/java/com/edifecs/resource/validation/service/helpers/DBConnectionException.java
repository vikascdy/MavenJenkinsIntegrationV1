package com.edifecs.resource.validation.service.helpers;

public class DBConnectionException extends Exception {

	private static final long serialVersionUID = 944003802435557642L;

	public DBConnectionException(String msg, Exception e) {
		super(msg, e);
	}
	
	public DBConnectionException(Exception e) {
		super(e);
	}
	
	public DBConnectionException(String msg) {
		super(msg);
	}
}
