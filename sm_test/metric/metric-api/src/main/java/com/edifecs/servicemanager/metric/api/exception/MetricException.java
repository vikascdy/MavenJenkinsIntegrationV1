package com.edifecs.servicemanager.metric.api.exception;

public class MetricException extends Exception {

	private static final long serialVersionUID = 1L;

	public MetricException() {
		super();
	}

	public MetricException(String message) {
		super(message);
	}

	public MetricException(Throwable cause) {
		super(cause);
	}

	public MetricException(String message, Throwable cause) {
		super(message, cause);
	}
}
