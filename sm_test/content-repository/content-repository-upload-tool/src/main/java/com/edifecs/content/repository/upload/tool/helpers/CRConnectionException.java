package com.edifecs.content.repository.upload.tool.helpers;

public class CRConnectionException extends Exception {
	private static final long serialVersionUID = 541705066125156377L;
	
	public CRConnectionException(Exception e) {
		super(e);
	}

	public CRConnectionException(String msg) {
		super(msg);
	}
	
	public CRConnectionException(String msg, Exception e) {
		super(msg, e);
	}
}
