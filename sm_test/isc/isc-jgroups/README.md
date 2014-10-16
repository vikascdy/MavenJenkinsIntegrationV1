# The ETCD Discovery Protocol

The `ETCD` JGroups discovery protocol provides cluster discovery services to ServiceManager nodes running in separate Docker containers. It is enabled when the system property `cluster.platform` is set to `ecm`.

## How It Works

When a node starts, it creates an etcd key named `$baseDir/$clusterName/$uuid.node`, where `$basePath` is the `ETCD` protocol's base directory (`/ecm/clusters`, by default), `$clusterName` is the name of the cluster, and `$uuid` is the node's UUID. This key has a short TTL (30 seconds, by default), and a background thread repeatedly renews this key on an interval of half the TTL. The value of the key is base64-serialized [`PingData`](http://www.jgroups.org/javadoc/org/jgroups/protocols/PingData.html) representing the node's address.

When a node scans for other cluster members, it lists the contents of the etcd directory `$baseDir/$clusterName`, and reads the `PingData` from each key in the directory.

## Configuration Options

The following options can be set via system properties:

- `etcd.address`: The IP address of the etcd service. Default value: `127.0.0.1`

- `etcd.port`: The port that the etcd service at `etcd.address` uses. Default value: `4001`

- `etcd.directory`: The base directory (`$baseDir`) that cluster keys are stored under. Default value: `/ecm/clusters`

- `etcd.timeout`: The time, in milliseconds, to wait for a response from the etcd service before throwing an exception. Default value: `5000`

- `etcd.ttl`: The TTL (time to live), in seconds, of keys created by the ETCD protocol. Keys will be written to repeatedly, on an interval of half the TTL. Default value: `30`