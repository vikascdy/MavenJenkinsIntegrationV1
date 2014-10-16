package com.edifecs.servicemanager.metric.api;

import com.edifecs.servicemanager.metric.api.exception.MetricException;

/**
 * The Enum SupportedMetricOperations. <br>
 * <b>Mark</b> : Mark the occurrence of an event <br>
 * <b>Inc</b> : Increment the counter by one. <br>
 * <b>Dec</b> : Decrement the counter by one.<br>
 * <b>Timer Start</b> : Starts timer context. <br>
 * <b>Timer Stop</b> : Stops the timer
 */
public enum SupportedMetricOperations {

	/** The mark. */
	MARK("Mark"), /** The inc. */
	INC("Inc"), /** The dec. */
	DEC("Dec"), TIMER_START("Timer Start"), TIMER_STOP("Timer Stop"), ;

	/** The name. */
	private String name;

	/**
	 * Instantiates a new supported metric operations.
	 * 
	 * @param name
	 *            the name
	 */
	private SupportedMetricOperations(String name) {
		this.name = name;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * From string.
	 * 
	 * @param arg
	 *            the arg
	 * @return the supported metric operations
	 * @throws MetricException
	 *             the metric exception
	 */
	public static SupportedMetricOperations fromString(String arg)
			throws MetricException {
		SupportedMetricOperations operation = null;
		for (SupportedMetricOperations m : SupportedMetricOperations.values())
			if (m.name.equalsIgnoreCase(arg)) {
				operation = m;
				break;
			}
		if (operation == null)
			throw new MetricException(String.format(
					"'%s' metric operation is not supported.", arg));
		return operation;
	}
}
