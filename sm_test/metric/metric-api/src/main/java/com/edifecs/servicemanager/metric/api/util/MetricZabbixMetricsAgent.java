package com.edifecs.servicemanager.metric.api.util;

import com.codahale.metrics.*;
import com.edifecs.servicemanager.metric.api.exception.ZabbixApiException;
import com.edifecs.servicemanager.metric.api.exception.ZabbixItemAlreadyExistsException;
import com.janramm.metrics_zabbix.metric_provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by abhising on 09-06-2014.
 */
public class MetricZabbixMetricsAgent implements MetricRegistryListener {
    private final static Logger logger = LoggerFactory.getLogger(MetricZabbixMetricsAgent.class);
    private final MetricZabbixAgent agent;
    private final MetricRegistry metrics;
    private final ZabbixApiItem zabbixApiItem;
    private final String hostId;


    public MetricZabbixMetricsAgent(MetricRegistry metrics, MetricZabbixAgent agent, ZabbixApi api, String hostId) {
        this.metrics = metrics;
        this.agent = agent;
        this.zabbixApiItem = new ZabbixApiItem(api);
        this.hostId = hostId;
    }

    public String makeKey(String name, String key) {
        return name + "" + key;
    }

    /**
     * Register this agent as metrics listener in order to get all added metrics.
     * This method should be called, before any metric is added otherwise the metrics won't get reported to zabbix
     */
    public void start() {
        this.metrics.addListener(this);
    }

    /**
     * Removes this agent as metrics listener
     */
    public void stop() {
        this.metrics.removeListener(this);
    }

    @Override
    public void onGaugeAdded(final String name, final Gauge<?> gauge) {
        this.agent.addProvider(name, new GaugeMetricProvider(gauge));
        try {
            zabbixApiItem.addItem(name, "Gauge", makeKey(name, "COUNT"), hostId, "", 30);
        } catch (ZabbixItemAlreadyExistsException e) {
            logger.debug("zabbix item already exists : {}", name, e);
        } catch
                (ZabbixApiException e) {
            logger.error("Unable to add zabbix item key : {}", name, e);
        }
    }

    @Override
    public void onCounterAdded(final String name, final Counter counter) {
        this.agent.addProvider(name, new CounterMetricProvider(counter));
        try {
            zabbixApiItem.addItem(name, "Counter", makeKey(name, "COUNT"), hostId, "", 30);
        } catch (ZabbixItemAlreadyExistsException e) {
            logger.debug("zabbix item already exists : {}", name, e);
        } catch (ZabbixApiException e) {
            logger.error("Unable to add zabbix item key : {}", name, e);
        }
    }

    @Override
    public void onHistogramAdded(final String name, final Histogram histogram) {
        this.agent.addProvider(name, new HistogramMetricProvider(histogram));
        try {
            zabbixApiItem.addItem(name, "MEAN", makeKey(name, "MEAN"), hostId, "", 30);
        } catch (ZabbixItemAlreadyExistsException e) {
            logger.debug("zabbix item already exists : {}", name, e);
        } catch (ZabbixApiException e) {
            logger.error("Unable to add zabbix item key : {}", name, e);
        }
    }

    @Override
    public void onMeterAdded(final String name, final Meter meter) {
        this.agent.addProvider(name, new MeterMetricProvider(meter));
        try {
            zabbixApiItem.addItem(name, "Throughput", makeKey(name, "MEAN_RATE"), hostId, "/sec", 60);
        } catch (ZabbixItemAlreadyExistsException e) {
            logger.debug("zabbix item already exists : {}", name, e);
        } catch (ZabbixApiException e) {
            logger.error("Unable to add zabbix item key : {}", name, e);
        }
    }

    @Override
    public void onTimerAdded(final String name, final Timer timer) {
        this.agent.addProvider(name, new TimerMetricProvider(timer));
        try {
            zabbixApiItem.addItem(name, "Latency", makeKey(name, "MEAN"), hostId, "ms", 50);
        } catch (ZabbixItemAlreadyExistsException e) {
            logger.debug("zabbix item already exists : {}", name, e);
        } catch (ZabbixApiException e) {
            logger.error("Unable to add zabbix item key : {}", name, e);
        }
    }

    @Override
    public void onCounterRemoved(String name) {
        //do nothing because zabbixj does not support to remove a metric
    }

    @Override
    public void onHistogramRemoved(String name) {
        //do nothing because zabbixj does not support to remove a metric

    }

    @Override
    public void onMeterRemoved(String name) {
        //do nothing because zabbixj does not support to remove a metric
    }

    @Override
    public void onGaugeRemoved(final String name) {
        //do nothing because zabbixj does not support to remove a metric
    }

    @Override
    public void onTimerRemoved(String name) {
        //do nothing because zabbixj does not support to remove a metric
    }
}
