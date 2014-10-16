package com.edifecs.servicemanager.metric.api.reporter;

import java.util.concurrent.TimeUnit;

/**
 * The Class IMetricReporter allows to configure reporters for the IMetric Api.
 */
public abstract class IMetricReporter {

	/** The period. */
	private long period;

	/** The unit. */
	private TimeUnit unit;

	/**
	 * Gets the period.
	 * 
	 * @return the period
	 */
	public long getPeriod() {
		return period;
	}

	/**
	 * Sets the period.
	 * 
	 * @param period
	 *            the new period
	 */
	public void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * Gets the unit.
	 * 
	 * @return the unit
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * Sets the unit.
	 * 
	 * @param unit
	 *            the new unit
	 */
	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}

	/**
	 * Instantiates a new i metric reporter.
	 * 
	 * @param period
	 *            the period
	 * @param unit
	 *            the unit
	 */
	public IMetricReporter(long period, TimeUnit unit) {
		this.period = period;
		this.unit = unit;
	}

	public IMetricReporter() {
	}

}
