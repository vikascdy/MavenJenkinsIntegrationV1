package com.edifecs.servicemanager.dashboard;

public class XBoardException extends Exception {

	private static final long serialVersionUID = 1L;

	public XBoardException() {
		super();
	}

	public XBoardException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public XBoardException(String message, Throwable cause) {
		super(message, cause);
	}

	public XBoardException(String message) {
		super(message);
	}

	public XBoardException(Throwable cause) {
		super(cause);
	}

}
