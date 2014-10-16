# Metric Reporting Interface

Within the UI, there are some metrics that we wish to make presentable in the UI to both end users, and to system admins.

So there are two API's designed to do this.

## Tenant Metric Reporting

Tenants are interested in several types of metrics.

  * System Usage
    * User login rate
    * Transactions per day
    * etc..
  * SLA Information
    * Average Processing time aligned with their SLA

Many of this information will pull from zabbix and other end points. These metrics need to have specialized security
around the queries to protect this data. It also needs to be multi tenant so that each tenant can view only their own
metrics.

// TODO: Design needed for exposing this data to end users.

## Site Admin UI

### ECM based integration

For the majority of views, it is just embedding Zabbix to display the built in dashboard capabilities provided in
Zabbix.

However for custom reports, the API is designed to query Zabbix to return these results 

// TODO: API Interface needed when Site Admin UI is designed.

### SM only implementation

We will not be able to show much, if anything for metrics, unless its configured with Zabbix. Without Zabbix, metric
reporting and visualization is offloaded to another tool. For example, CSV, or JMX style reporting.

With certain endpoints we can in future releases work on our custom reports to display this content from other metric
collection utilities.

[Metric Reporting Configuration Options](../../metric/README.md)