# Log Reporting

There ate two styles of log reporting needed, Site Admin UI, and Tenant/User available logs.

## Tenant/User Access

These are things like, user audit history, user login attempts.

These API's are specific for certain UI elements. When integrated in ECM, this information comes from Logstash/Kibanna.
However, when Logstash and Kibanna are unavailable, the interfaces can be overridden to pull this information from other
locations.

The stand alone SM installation without any log analysis tool will be able to display the logs found on the machine,
however will not be able to track and store user audit history, unless a log collection service is available.

//TODO: Api's are required based on the UI design and requirements

## Site Admin UI

// TODO: Define the Needs of Site Admin UI