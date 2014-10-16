# Caching
To reduce the number of calls made to the Security Service, and too the related DB and thirdparty providers, we need the support for some form of caching.

Managing cache in a distributed system is very different then on a single JVM or service. for example, if a user is deleted, and access is removed from the system, how does this effect their user session on the machines 
that have the user Cached?

How caching is done can also be done at multiple levels, both within the Security Service, and within a remote Service.

## Security Service Caching
Caching at the Security Service is crucial. To prevent constant pinging the Database and the third party providers. Shiro is what we use to do this and there are multiple ways to handle caching, using just the internal 
caching algorithms or hooked into distributed caching solutions.

Out approach uses sharding, so the session caching is just done at the service level and does not require distributes cache scenarios.

## Remote Service Caching
To limit the number of calls to the Security Module from all other locations. Like the Security Service, Shiro is used to manage the client side caching of the user session information. However, this can be controlled to 
have a very short caching time, for example 5 seconds. This solves a situation where a remote service is making hundreds or thousands of calls per second, and not requiring the constant check, however still allow for the 
remote killing of a user session being done centrally.