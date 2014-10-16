package com.edifecs.servicemanager.metric.api.reporter;

import java.util.concurrent.TimeUnit;

/**
 * The ConsoleMetricReporter, reports all metric values to the console.
 */
public class ConsoleMetricReporter extends IMetricReporter {

	/**
	 * Instantiates a new console metric reporter.
	 * 
	 * @param period
	 *            the period
	 * @param unit
	 *            the unit
	 */
	public ConsoleMetricReporter(long period, TimeUnit unit) {
		super(period, unit);
	}
}
