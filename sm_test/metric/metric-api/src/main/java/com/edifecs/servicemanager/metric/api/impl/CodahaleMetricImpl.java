package com.edifecs.servicemanager.metric.api.impl;

import com.codahale.metrics.*;
import com.edifecs.servicemanager.metric.api.*;
import com.edifecs.servicemanager.metric.api.exception.MetricException;
import com.edifecs.servicemanager.metric.api.reporter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;

public class CodahaleMetricImpl implements IMetric {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private MetricRegistry metricRegistry;

    private Closeable metricReporter;

    public CodahaleMetricImpl() {
        this.metricRegistry = new MetricRegistry();
    }

    @Override
    public void configureReporter(IMetricReporter reporter)
            throws MetricException {
        if (reporter == null) {
            throw new MetricException("Reporter cannot be null");
        }
        if (reporter instanceof ConsoleMetricReporter) {
            ConsoleMetricReporter cMReporter = (ConsoleMetricReporter) reporter;

            ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(
                    metricRegistry).build();
            /* Uncomment for reporting to console
            consoleReporter.start(cMReporter.getPeriod(), cMReporter.getUnit());
            */
            metricReporter = consoleReporter;

        } else if (reporter instanceof CSVMetricReporter) {
            CSVMetricReporter csvMetricReporter = (CSVMetricReporter) reporter;
            File csvDir = csvMetricReporter.getCsvDir();
            if (csvDir == null || !csvDir.isDirectory())
                throw new MetricException(
                        String.format("Invalid or Null Path for CSV Directory, expected path to a directory"));

            CsvReporter csvReporter = CsvReporter.forRegistry(metricRegistry)
                    .build(csvDir);
            csvReporter.start(csvMetricReporter.getPeriod(),
                    csvMetricReporter.getUnit());
            metricReporter = csvReporter;
        } else if (reporter instanceof ZabbixMetricReporter) {
            ZabbixMetricReporter zabbixMetricReporter = (ZabbixMetricReporter) reporter;
            zabbixMetricReporter.setup(metricRegistry);
            zabbixMetricReporter.start();
            metricReporter = zabbixMetricReporter;
        } else if (reporter instanceof ZabbixDockerMetricReporter) {
            ZabbixDockerMetricReporter zabbixDockerMetricReporter = (ZabbixDockerMetricReporter) reporter;
            zabbixDockerMetricReporter.setup(metricRegistry);
            zabbixDockerMetricReporter.start();
            metricReporter = zabbixDockerMetricReporter;
        } else if (reporter instanceof JmxMetricReporter) {
            JmxMetricReporter jmxMetricReporter = (JmxMetricReporter) reporter;
            JmxReporter jmx = JmxReporter.forRegistry(metricRegistry).build();
            jmx.start();
            metricReporter = jmx;
        }
    }

    @Override
    public String generateMetricName(Class<?> clazz, String... names) {
        return MetricRegistry.name(clazz, names);
    }

    @Override
    public void registerMetric(SupportedMetrics type, String name)
            throws MetricException {
        if (name == null || type == null)
            throw new IllegalArgumentException(
                    String.format("metric type or name cannot be null."));

        switch (type) {
            case COUNTER:
                registerCounter(name);
                break;
            case METER:
                registerMeter(name);
                break;
            case TIMER:
                registerTimer(name);
                break;
            case GAUGE:
                registerGauge(name);
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "'%s' metric type is not supported.", type));
        }
    }

    @Override
    public void performOperation(String metric,
                                 SupportedMetricOperations operation) throws MetricException {
        if (metric == null || operation == null)
            throw new IllegalArgumentException(
                    String.format("metric name or operation cannot be null."));

        switch (operation) {
            case MARK:
                mark(metric);
                break;
            case INC:
                incCounter(metric);
                break;
            case DEC:
                decCounter(metric);
                break;
            case TIMER_START:
                startTimer(metric);
                break;
            case TIMER_STOP:
                stopTimer(metric);
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        "'%s' metric operation is not supported.", operation));
        }
    }

    private void registerGauge(String name) throws MetricException {
        throw new UnsupportedOperationException();

    }

    private Counter registerCounter(String name) throws MetricException {
        return metricRegistry.counter(name);
    }

    private Meter registerMeter(String name) throws MetricException {
        return metricRegistry.meter(name);
    }

    private Timer registerTimer(String name) throws MetricException {
        return metricRegistry.timer(name);

    }

    private void incCounter(final String mName) throws MetricException {
        Counter counter = registerCounter(mName);
        counter.inc();
    }

    private void decCounter(final String mName) throws MetricException {
        Counter counter = registerCounter(mName);
        counter.dec();
    }

    private void mark(final String mName) throws MetricException {
        Meter meter = registerMeter(mName);
        meter.mark();
    }

    private void startTimer(final String tName) throws MetricException {
        Timer timer = registerTimer(tName);
        timer.time();
    }

    private void stopTimer(final String tName) throws MetricException {
        Timer.Context ctx = registerTimer(tName).time();
        ctx.stop();
    }

    public Throughput getThroughput(final String mName) throws MetricException {
        Throughput throughput = new Throughput();
        Meter meter = registerMeter(mName);
        throughput.setCount(meter.getCount());
        throughput.setMeanRate(meter.getMeanRate());
        throughput.setOneMinuteRate(meter.getOneMinuteRate());
        throughput.setFiveMinuteRate(meter.getFiveMinuteRate());
        throughput.setFifteenMinuteRate(meter.getFifteenMinuteRate());

        return throughput;
    }

    public Latency getLatency(final String mName) throws MetricException {
        Latency latency = new Latency();
        Timer timer = registerTimer(mName);
        latency.setCount(timer.getCount());
        Snapshot snapshot = timer.getSnapshot();
        latency.setMax(snapshot.getMax());
        latency.setMean(snapshot.getMean());
        latency.setMin(snapshot.getMin());
        latency.setStandardDeviation(snapshot.getStdDev());
        latency.setPercentile50(snapshot.getMedian());
        latency.setPercentile75(snapshot.get75thPercentile());
        latency.setPercentile95(snapshot.get95thPercentile());
        latency.setPercentile98(snapshot.get98thPercentile());
        latency.setPercentile99(snapshot.get99thPercentile());
        latency.setPercentile999(snapshot.get999thPercentile());
        latency.setMeanRate(timer.getMeanRate());
        latency.setOneMinuteRate(timer.getOneMinuteRate());
        latency.setFiveMinuteRate(timer.getFiveMinuteRate());
        latency.setFifteenMinuteRate(timer.getFifteenMinuteRate());
        logger.debug(mName, latency);

        return latency;
    }

    @Override
    public void shutdown() throws Exception {
        for (String metric : metricRegistry.getNames()) {
            metricRegistry.remove(metric);
        }

        metricReporter.close();
    }
}
