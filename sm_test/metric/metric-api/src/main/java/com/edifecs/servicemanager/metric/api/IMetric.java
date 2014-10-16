package com.edifecs.servicemanager.metric.api;

import com.edifecs.servicemanager.metric.api.exception.MetricException;
import com.edifecs.servicemanager.metric.api.reporter.IMetricReporter;

/**
 * The Interface IMetric, provides the ability to register metrics and perform
 * operations on registered metrics.
 * 
 * @author abhising
 */
public interface IMetric {

	/**
	 * **************************** METRICS *********************************.
	 * 
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @throws MetricException
	 *             the metric exception
	 */
	/**
	 * Register Metric.
	 * 
	 * @param type
	 *            the type of metric. Please see
	 *            {@link com.edifecs.servicemanager.metric.api.SupportedMetrics}
	 * @param name
	 *            of the meter, in order to distinguish names, it is recommended
	 *            to append names to the class name, e.g :
	 *            'com.edifecs.service.requestsPerSecond'.
	 * @throws MetricException
	 *             the metric exception
	 */

	void registerMetric(SupportedMetrics type, String name)
			throws MetricException;

	/**
	 * Perform operation on metric.
	 * 
	 * @param metric
	 *            the metric name
	 * @param operation
	 *            the operation to be performed. Please see
	 *            {@link com.edifecs.servicemanager.metric.api.SupportedMetricOperations}
	 * @throws MetricException
	 *             the metric exception
	 */
	void performOperation(String metric, SupportedMetricOperations operation)
			throws MetricException;

	/**
	 * Utility method for creating metric names, appends dotted names to the
	 * class name.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param names
	 *            dot separated names
	 * @return the string
	 */
	String generateMetricName(Class<?> clazz, String... names);

	/**
	 * **************************** REPORTERS *********************************.
	 * 
	 * @param reporter
	 *            the reporter.
	 *            {@link com.edifecs.servicemanager.metric.api.reporter.IMetricReporter}
	 *            All reporters are added to the existing and active metric
	 *            registry.
	 * @throws MetricException
	 *             the metric exception
	 */

	void configureReporter(IMetricReporter reporter) throws MetricException;

	/**
	 * Get Throughput.
	 * 
	 * @param mName
	 *            Meter Metric Name
	 * @return the Throughput Snapshot object
	 * @throws MetricException
	 *             the metric exception
	 */
	Throughput getThroughput(final String mName) throws MetricException;

	/**
	 * Get Latency.
	 * 
	 * @param mName
	 *            Meter Metric Name
	 * @return the Latency Snapshot object
	 * @throws MetricException
	 *             the metric exception
	 */
	Latency getLatency(final String mName) throws MetricException;

	/**
	 * Shuts down all the metrics being collected.
	 */
	void shutdown() throws Exception;
}
