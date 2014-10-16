package com.edifecs.servicemanager.metric.api.impl.test;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.edifecs.servicemanager.metric.api.SupportedMetricOperations;
import com.edifecs.servicemanager.metric.api.SupportedMetrics;
import com.edifecs.servicemanager.metric.api.exception.MetricException;

/**
 * Created with IntelliJ IDEA. User: sandeep.kath Date: 2/25/14 Time: 5:09 PM To
 * change this template use File | Settings | File Templates.
 */
public class TimerTest extends MetricTestBase {

	@Test
	public void clockTest() {
		try {
			String metricName = "test.timer";
			String metricReportingFile = metricName + ".csv";
			metricAPIInstance
					.registerMetric(SupportedMetrics.TIMER, metricName);
			metricAPIInstance.performOperation(metricName,
					SupportedMetricOperations.TIMER_START);
			Thread.sleep(500); // Doing some operation...
			metricAPIInstance.performOperation(metricName,
					SupportedMetricOperations.TIMER_STOP);
			Thread.sleep(2000); // Wait for reporter to write into CSV for
								// testing
			List<String[]> metricContent = readCSVReportingFile(metricReportingFile);
			String[] row = metricContent.get(metricContent.size() - 1);
			Assert.assertEquals("1", row[1]);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void reportingTimerValues() {
		try {
			String metricName = "test.timer_report_values6";
			String metricReportingFile = metricName + ".csv";
			metricAPIInstance
					.registerMetric(SupportedMetrics.TIMER, metricName);

			for (int i = 0; i < 5; i++) {
				someServiceMethod(metricName);
			}

			Thread.sleep(1000); // Wait for reporter to write into CSV for
								// testing

			List<String[]> metricContent = readCSVReportingFile(metricReportingFile);
			String[] row = metricContent.get(metricContent.size() - 1);
			assertNotNull("Max", row[2]);
			assertNotNull("Mean", row[3]);
			assertNotNull("Min", row[4]);
			assertNotNull("StdDev", row[5]);
			assertNotNull("Median", row[6]);
			assertNotNull("95thPercentile", row[8]);
			assertNotNull("98thPercentile", row[9]);
			assertNotNull("99thPercentile", row[10]);
			assertNotNull("999thPercentile", row[11]);
			assertNotNull("Mean Rate", row[12]);
			assertNotNull("1 Minute Rate", row[13]);
			assertNotNull("5 Minute Rate", row[14]);
			assertNotNull("15 Minute Rate", row[15]);
			assertNotNull("Rate Unit", row[16]);
			assertNotNull("Duration Unit", row[17]);
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
