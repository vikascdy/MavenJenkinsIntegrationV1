package com.edifecs.servicemanager.metric.api.impl.test;

import com.edifecs.servicemanager.metric.api.IMetric;
import com.edifecs.servicemanager.metric.api.SupportedMetricOperations;
import com.edifecs.servicemanager.metric.api.SupportedMetrics;
import com.edifecs.servicemanager.metric.api.Throughput;
import com.edifecs.servicemanager.metric.api.impl.CodahaleMetricImpl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: sandeep.kath Date: 2/27/14 Time: 2:44 PM To
 * change this template use File | Settings | File Templates.
 */
public class ThroughputTest extends MetricTestBase {
	protected static IMetric metricAPIInstance = new CodahaleMetricImpl();
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public static void setUp() throws Exception {
	}

	@Test
	public void throughputTest() {
		try {
			String metricName = "test.throughput";
			metricAPIInstance
					.registerMetric(SupportedMetrics.METER, metricName);

			for (int i = 0; i < 5; i++) { // simulating 5 random calls
				int randomCallTime = (int) (Math.random() * 200);
				Thread.sleep(randomCallTime);
				metricAPIInstance.performOperation(metricName,
						SupportedMetricOperations.MARK);

				Throughput throughput = metricAPIInstance
						.getThroughput(metricName);
				logger.debug(throughput.toString());
			}

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
