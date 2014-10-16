package com.edifecs.servicemanager.metric.api.impl.test;

import com.edifecs.servicemanager.metric.api.IMetric;
import com.edifecs.servicemanager.metric.api.SupportedMetricOperations;
import com.edifecs.servicemanager.metric.api.SupportedMetrics;
import com.edifecs.servicemanager.metric.api.Throughput;
import com.edifecs.servicemanager.metric.api.impl.CodahaleMetricImpl;
import com.edifecs.servicemanager.metric.api.reporter.JmxMetricReporter;
import com.edifecs.servicemanager.metric.api.reporter.ZabbixMetricReporter;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: sandeep.kath Date: 2/27/14 Time: 2:44 PM To
 * change this template use File | Settings | File Templates.
 */
public class ThroughputWithZabbixTest {
    protected static IMetric metricAPIInstance = new CodahaleMetricImpl();
    protected Logger logger = LoggerFactory.getLogger(getClass());


    @BeforeClass
    public static void setUp() throws Exception {
        ZabbixMetricReporter zReporter = new ZabbixMetricReporter("zabbix01", 10051, ZabbixApiTest.hostGroupName, "admin", "zabbix");
        JmxMetricReporter jmx = new JmxMetricReporter();
        metricAPIInstance.configureReporter(jmx);
        //metricAPIInstance.configureReporter(zReporter);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        metricAPIInstance.shutdown();
    }

    
    @Test
    public void throughputTest() {
        try {
            String metricName = "throughput";
            metricAPIInstance
                    .registerMetric(SupportedMetrics.METER, metricName);

            for (int i = 0; i < 50; i++) {
                int randomCallTime = (int) (Math.random() * 200);
                Thread.sleep(randomCallTime);
                metricAPIInstance.performOperation(metricName,
                        SupportedMetricOperations.MARK);

                Throughput throughput = metricAPIInstance
                        .getThroughput(metricName);
                logger.debug(throughput.toString());
            }

            Thread.sleep(35000);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
