package com.edifecs.servicemanager.metric.api.impl.test;

import org.junit.Assert;
import org.junit.Test;

import com.edifecs.servicemanager.metric.api.SupportedMetricOperations;
import com.edifecs.servicemanager.metric.api.SupportedMetrics;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA. User: sandeep.kath Date: 2/25/14 Time: 5:09 PM To
 * change this template use File | Settings | File Templates.
 */
public class MeterTest extends MetricTestBase {

	@Test
	public void markMeterTest() throws Exception {
		try {
			String metricName = "test.meter";
			String metricReportingFile = metricName + ".csv";
			metricAPIInstance
					.registerMetric(SupportedMetrics.METER, metricName); // optional
			metricAPIInstance.performOperation(metricName,
					SupportedMetricOperations.MARK);
			metricAPIInstance.performOperation(metricName,
					SupportedMetricOperations.MARK);
			metricAPIInstance.performOperation(metricName,
					SupportedMetricOperations.MARK);
			Thread.sleep(2000);
			List<String[]> metricContent = readCSVReportingFile(metricReportingFile);
			String[] row = metricContent.get(metricContent.size() - 1);
			Assert.assertEquals("Count", "3", row[1]);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void reportingMeterValues() {
		try {
			String metricName = "test.meter_reporting";
			String metricReportingFile = metricName + ".csv";
			metricAPIInstance
					.registerMetric(SupportedMetrics.METER, metricName);

			for (int i = 0; i < 5; i++) { // simulating 5 random calls
				int randomCallTime = (int) (Math.random() * 100);
				metricAPIInstance.performOperation(metricName,
						SupportedMetricOperations.MARK);
				Thread.sleep(randomCallTime);
			}

			Thread.sleep(1000); // Wait for reporter to write into CSV for
								// testing

			List<String[]> metricContent = readCSVReportingFile(metricReportingFile);
			String[] row = metricContent.get(metricContent.size() - 1);
			logger.debug(
					"Mean Rate {} ;1 Minute Rate {} ;5 Minute Rate {} ;15 Minute Rate ;Rate Unit  ",
					row[2], row[3], row[4], row[5], row[6]);
			assertNotNull("Mean Rate", row[2]);
			assertNotNull("1 Minute Rate", row[3]);
			assertNotNull("5 Minute Rate", row[4]);
			assertNotNull("15 Minute Rate", row[5]);
			assertNotNull("Rate Unit", row[6]);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}
