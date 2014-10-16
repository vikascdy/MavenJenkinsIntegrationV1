package com.edifecs.servicemanager.metric.api;

import com.edifecs.servicemanager.metric.api.exception.MetricException;

/**
 * The Enum SupportedMetrics. <br>
 * <b>Counter</b> : A counter is just a gauge for an AtomicLong instance. You
 * can increment or decrement its value. For example, we may want a more
 * efficient way of measuring the pending job in a queue. <br>
 * <b>Meter</b> : A meter measures the rate of events over time (e.g., "requests
 * per second"). In addition to the mean rate, meters also track 1-, 5-, and
 * 15-minute moving averages. <br>
 * <b>Timer</b> : A timer measures both the rate that a particular piece of code
 * is called and the distribution of its duration. <br>
 * <b>Gauge</b> : A gauge is an instantaneous measurement of a value. For
 * example, we may want to measure the number of pending jobs in a queue.
 * 
 */
public enum SupportedMetrics {

	/** The counter. */
	COUNTER("Counter"), /** The meter. */
	METER("Meter"), /** The timer. */
	TIMER("Timer"), /** The gauge. */
	GAUGE("Gauge");

	/** The name. */
	private String name;

	/**
	 * Instantiates a new supported metrics.
	 * 
	 * @param name
	 *            the name
	 */
	private SupportedMetrics(String name) {
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
	 * @return the supported metrics
	 * @throws MetricException
	 *             the metric exception
	 */
	public static SupportedMetrics fromString(String arg)
			throws MetricException {
		SupportedMetrics metricType = null;
		for (SupportedMetrics m : SupportedMetrics.values())
			if (m.name.equalsIgnoreCase(arg)) {
				metricType = m;
				break;
			}
		if (metricType == null)
			throw new MetricException(String.format(
					"'%s' metric type is not supported.", arg));
		return metricType;
	}
}
