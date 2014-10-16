package com.edifecs.servicemanager.metric.api.impl.test;

import com.edifecs.servicemanager.metric.api.IMetric;
import com.edifecs.servicemanager.metric.api.Latency;
import com.edifecs.servicemanager.metric.api.SupportedMetricOperations;
import com.edifecs.servicemanager.metric.api.SupportedMetrics;
import com.edifecs.servicemanager.metric.api.exception.MetricException;
import com.edifecs.servicemanager.metric.api.impl.CodahaleMetricImpl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: sandeep.kath Date: 2/28/14 Time: 4:02 PM To
 * change this template use File | Settings | File Templates.
 */
public class LatencyTest {
	protected static IMetric metricAPIInstance = new CodahaleMetricImpl();
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public static void setUp() throws Exception {
	}

	@Test
	public void latencyTest() {
		try {
			String metricName = "test.latency";
			metricAPIInstance
					.registerMetric(SupportedMetrics.TIMER, metricName);

			for (int i = 0; i < 5; i++) { // simulating 5 random calls
				someServiceMethod(metricName);
				Latency latency = metricAPIInstance.getLatency(metricName);
				logger.debug(latency.toString());
			}

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	private void someServiceMethod(String metricName) throws MetricException,
			InterruptedException {
		metricAPIInstance.performOperation(metricName,
				SupportedMetricOperations.TIMER_START);
		int operationTime = (int) (Math.random() * 500);
		Thread.sleep(operationTime); // Doing some operation...
		metricAPIInstance.performOperation(metricName,
				SupportedMetricOperations.TIMER_STOP);
	}

}
