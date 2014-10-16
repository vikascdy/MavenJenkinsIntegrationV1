package com.edifecs.servicemanager.metric.api.impl.test;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.edifecs.servicemanager.metric.api.SupportedMetricOperations;
import com.edifecs.servicemanager.metric.api.SupportedMetrics;

/**
 * Created with IntelliJ IDEA. User: sandeep.kath Date: 2/25/14 Time: 1:50 PM To
 * change this template use File | Settings | File Templates.
 */
public class CounterTest extends MetricTestBase {

	@Test
	public void incrementByOne() {
		try {
			String metricName = "test_counter_inc";
			String metricReportingFile = metricName + ".csv";
			metricAPIInstance.registerMetric(SupportedMetrics.COUNTER,
					metricName); // optional step
			metricAPIInstance.performOperation(metricName,
					SupportedMetricOperations.INC);
			Thread.sleep(2000);
			List<String[]> metricContent = readCSVReportingFile(metricReportingFile);
			String[] row = metricContent.get(metricContent.size() - 1); // get
																		// last
																		// row
																		// of
																		// CSV
																		// file
			Assert.assertEquals("1", row[1]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void decrementByOne() throws Exception {
		try {
			String metricName = "test_counter_dec";
			String metricReportingFile = metricName + ".csv";
			metricAPIInstance.registerMetric(SupportedMetrics.COUNTER,
					metricName);
			metricAPIInstance.performOperation(metricName,
					SupportedMetricOperations.DEC);
			Thread.sleep(2000);
			List<String[]> metricContent = readCSVReportingFile(metricReportingFile);
			String[] row = metricContent.get(metricContent.size() - 1); // get
																		// last
																		// row
																		// of
																		// CSV
			Assert.assertEquals("-1", row[1]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
