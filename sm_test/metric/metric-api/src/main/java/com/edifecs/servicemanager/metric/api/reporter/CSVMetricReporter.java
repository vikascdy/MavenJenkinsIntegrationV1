package com.edifecs.servicemanager.metric.api.reporter;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.edifecs.servicemanager.metric.api.exception.MetricException;

/**
 * The CSVMetricReporter reports all metric values to the respective csv files,
 * in the user defined csv directory.
 */
public class CSVMetricReporter extends IMetricReporter {

	/** The csv dir. */
	private File csvDir;

	/**
	 * Gets the csv dir.
	 * 
	 * @return the csv dir
	 */
	public File getCsvDir() {
		return csvDir;
	}

	/**
	 * Sets the csv dir.
	 * 
	 * @param csvDir
	 *            the new csv dir
	 */
	public void setCsvDir(File csvDir) {
		this.csvDir = csvDir;
	}

	/**
	 * Instantiates a new CSV metric reporter.
	 * 
	 * @param period
	 *            the period
	 * @param unit
	 *            the unit
	 * @param csvDir
	 *            the csv dir where csv files should be created.
	 */
	public CSVMetricReporter(long period, TimeUnit unit, File csvDir) {
		super(period, unit);
		this.csvDir = csvDir;
	}

	/**
	 * Instantiates a new CSV metric reporter.
	 * 
	 * @param period
	 *            the period
	 * @param unit
	 *            the unit
	 * @param csvDir
	 *            the csv dir path where csv files should be created.
	 */
	public CSVMetricReporter(long period, TimeUnit unit, String csvDirPath)
			throws MetricException {
		super(period, unit);
		if (csvDirPath == null)
			throw new MetricException(
					String.format("Null Path for CSV Directory"));

		File csvDir = new File(csvDirPath);
        csvDir.mkdirs();
		if (csvDir == null || !csvDir.isDirectory())
			throw new MetricException(
					String.format(
							"Invalid Path for CSV Directory : '%s', path should point to a valid directory",
                            csvDir.getAbsolutePath()));
		this.csvDir = csvDir;
	}
}
