package com.edifecs.core.configuration.helper;

public final class TypesafeConfigKeys {
    private TypesafeConfigKeys() {}

    public static final String CLUSTER_NAME = "isc.cluster.name";

    public static final String CLUSTER_JOINER_CLASS = "isc.cluster.joiner";

    public static final String ASYNC_MESSAGE_TIMEOUT = "isc.messaging.async-timeout";

    public static final String SYNC_MESSAGE_TIMEOUT = "isc.messaging.sync-timeout";

    public static final String STREAM_EXPIRE_TIMEOUT = "isc.messaging.stream.expire-timeout";

    public static final String STREAM_CHUNK_TIMEOUT = "isc.messaging.stream.chunk-timeout";

    public static final String STREAM_CHUNK_SIZE = "isc.messaging.stream.chunk-size";

    public static final String STREAM_CHUNK_CACHE_SIZE = "isc.messaging.stream.chunk-cache-size";

    public static final String AKKA_PORT = "akka.remote.netty.tcp.port";

    public static final String AKKA_HOSTNAME = "akka.remote.netty.tcp.hostname";

    public static final String DEFAULT_NODE_JVM_OPTS = "isc.node.jvm.opts";

    public static final String UI_PORT_TCP = "isc.ui.port";

    //public static final String TCP_HOSTS = "tcp.hosts";

    public static final String SHUTDOWN_TIMEOUT = "sm.shutdown.timeout";

    public static final String STARTUP_TIMEOUT = "sm.startup.timeout";

    public static final String METRIC_REPORTER_TYPE = "isc.metric.reporter.type";

    public static final String METRIC_REPORTER_PERIOD = "isc.metric.reporter.period";

    public static final String METRIC_REPORTER_ZABBIX_HOST = "isc.metric.reporter.host";

    public static final String METRIC_REPORTER_ZABBIX_USERNAME="isc.metric.reporter.username";

    public static final String METRIC_REPORTER_ZABBIX_PASSWORD="isc.metric.reporter.password";

    public static final String METRIC_REPORTER_ZABBIX_HOST_GRP = "isc.metric.reporter.group";

    public static final String METRIC_REPORTER_ZABBIX_PORT = "isc.metric.reporter.port";

    public static final String METRIC_REPORTER_DATA_DIRECTORY = "isc.metric.reporter.data-directory";
}
