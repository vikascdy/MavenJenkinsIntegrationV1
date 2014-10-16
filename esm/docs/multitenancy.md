# Multitenancy and Multientity

Within our Security framework, there is a difference between Multi Tenancy and Multi Entity.

## What is Multitenancy and what is Multientity?

### Multitenancy

"Multitenancy refers to a principle in software architecture where a single instance of the software runs on a server, serving multiple client-organizations (tenants). Multitenancy contrasts with multi-instance 
architectures where separate software instances (or hardware systems) operate on behalf of different client organizations. With a multitenant architecture, a software application is designed to virtually partition its 
data and configuration, and each client organization works with a customized virtual application." - [source] (http://en.wikipedia.org/wiki/Multitenancy)

For us a tenant is a customer who is purchasing one or more of our applications.

### Multientity

With a single tenant, you have multiple sub-organizations, partner organizations, and user groups that all have seperate views and configurations within a single tenant.

The support for Multientity allows allows for the applications to filter and partition data within a single enviornment.

## How is Multitenancy Used?

The Security Service provides the ability as a site admin to manage the tenants in the system, it is up to the applications to use this information.

In some cases, due to company requirements, HIPPA like regulations, or performance in an application may need to use the tenant information to control how a service is executed.

 * Load balancing the tenant information can be used to partition and force certain tenant requests to certain machines.
 * Physically partition data store in databases to limit visibility and for company requirements
 * Auto scale per tenat based on agreed SLA on performance
 * Service Data Caching and patitioning shared data by Tenants

## Example Tenant Org Structure
Organizations can exist in multiple tenants, since a tenant is a physical seperation, If an organization has been given a login to two different tenancies, it requires the user to have two differen logins, one for each 
tenant.

In the below example, is a user belongs to Hospital A, they will have two seperate logins. One for Tenant 1, another for Tenant 2.

 * Tenant - Tenant 1
    - Partner Organization - Hospital A
 * Tenant - Tenant 2
    - Partner Organization - Hospital A
    - Partner Organization - Hospital B
    - Partner Organization - Hospital C

 If a product is made that is able to aggigate the data and views from across the two tenants, and the organization pays for this service, then they would have their own tenancy and data would be duplicated from the 
 individual tenants to this aggrigate view. This should be done based on the requirements around data security and provacy for the individual products.

And as with the above scenario, Hospital A would have its own login and be its own tenancy.

 * Tenant - Hospital A