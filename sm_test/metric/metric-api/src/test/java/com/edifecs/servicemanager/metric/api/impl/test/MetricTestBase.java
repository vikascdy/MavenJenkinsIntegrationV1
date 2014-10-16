package com.edifecs.servicemanager.metric.api.impl.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.edifecs.servicemanager.metric.api.IMetric;
import com.edifecs.servicemanager.metric.api.impl.CodahaleMetricImpl;
import com.edifecs.servicemanager.metric.api.reporter.CSVMetricReporter;
import com.edifecs.servicemanager.metric.api.reporter.IMetricReporter;

/**
 * Created with IntelliJ IDEA. User: sandeep.kath Date: 2/25/14 Time: 5:51 PM To
 * change this template use File | Settings | File Templates.
 */
public class MetricTestBase {
	protected static IMetric metricAPIInstance = new CodahaleMetricImpl();
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected static File dataDirectory;

	@BeforeClass
	public static void setUp() throws Exception {
		dataDirectory = new File(FileUtils.getTempDirectoryPath());
		dataDirectory.deleteOnExit();
		IMetricReporter reporter = new CSVMetricReporter(1, TimeUnit.SECONDS,
				dataDirectory);
		metricAPIInstance.configureReporter(reporter);
	}

	protected String fileContents(String filename) throws IOException {
		final StringBuilder builder = new StringBuilder();
		final File file = new File(dataDirectory, filename);
		if (file.exists()) {
			final FileInputStream input = new FileInputStream(file);
			try {
				final InputStreamReader reader = new InputStreamReader(input);
				final BufferedReader bufferedReader = new BufferedReader(reader);
				final CharBuffer buf = CharBuffer.allocate(1024);
				while (bufferedReader.read(buf) != -1) {
					buf.flip();
					builder.append(buf);
					buf.clear();
				}
			} finally {
				input.close();
			}
		}
		return builder.toString();
	}

	protected List<String[]> readCSVReportingFile(String metricReportingFile)
			throws IOException {
		logger.debug(fileContents(metricReportingFile));
		File reportingFile = new File(dataDirectory, metricReportingFile);
		CSVReader reader = new CSVReader(new FileReader(reportingFile));
		List<String[]> metricContent = reader.readAll();
		return metricContent;
	}

	protected String csv(String... lines) {
		final StringBuilder builder = new StringBuilder();
		for (String line : lines) {
			builder.append(line).append(String.format("%n"));
		}
		return builder.toString();
	}

	@After
	public void cleanUp() {

	}
}
