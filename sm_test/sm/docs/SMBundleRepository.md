# SM Bundle Repository

Stores the application distribution packages that is used by SM and other components that are not completely deployed
though docker images.

Service Manager container itself is deployed through a docker image, but not all the services or resources are. In this case when
ECM is notified that a service needs to be deployed, it can trigger and download the application bundle that contains
that service from the Application stores Bundle Repository, onto the machine that the service is going to be deployed
on.

So the Application packages are simply ZIP files that contain resources that are required on configuration of a new app
for a tenant or installation onto a machine.

For information on the format of the application package format, please see: [Application Package Format](Packaging.md)