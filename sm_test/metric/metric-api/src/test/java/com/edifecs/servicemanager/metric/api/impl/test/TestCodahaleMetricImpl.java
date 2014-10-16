package com.edifecs.servicemanager.metric.api.impl.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.edifecs.servicemanager.metric.api.IMetric;
import com.edifecs.servicemanager.metric.api.SupportedMetricOperations;
import com.edifecs.servicemanager.metric.api.SupportedMetrics;
import com.edifecs.servicemanager.metric.api.exception.MetricException;
import com.edifecs.servicemanager.metric.api.impl.CodahaleMetricImpl;
import com.edifecs.servicemanager.metric.api.reporter.ConsoleMetricReporter;
import com.edifecs.servicemanager.metric.api.reporter.IMetricReporter;

public class TestCodahaleMetricImpl {

	private static IMetric metricApi = new CodahaleMetricImpl();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		IMetricReporter reporter = new ConsoleMetricReporter(1,
				TimeUnit.SECONDS);
		metricApi.configureReporter(reporter);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
        metricApi.shutdown();
	}

	@Test
	public void testRegisterMeterMetric() {
		try {
			metricApi.registerMetric(SupportedMetrics.METER, metricApi
					.generateMetricName(getClass(), "testRegisterMetric"));
		} catch (MetricException e) {
			fail(e.getMessage());
		}
        assertTrue(true);
	}

	@Test
	public void testMeterMetric() {
		try {
			// metricService.registerMeter("requestsPerSecond");
			callMeteredMethod();

			try {
				Thread.sleep(1000);
				callMeteredMethod();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
        assertTrue(true);
	}

	private void callMeteredMethod() throws Exception {
		// registers and marks the meter,
		metricApi.performOperation("requestsPerSecond",
				SupportedMetricOperations.MARK);
        assertTrue(true);
	}

	@Test
	public void testTimerMetric() throws Exception {
		String timerName = "TestTimer";
		// optional, Timer Start will register metric also
		metricApi.registerMetric(SupportedMetrics.TIMER, timerName);
		metricApi.performOperation(timerName,
				SupportedMetricOperations.TIMER_START);

		try {
			// block
			Thread.sleep(1000);
		} finally {
			metricApi.performOperation(timerName,
					SupportedMetricOperations.TIMER_STOP);
		}
        assertTrue(true);
	}
}
