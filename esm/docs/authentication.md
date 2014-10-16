# Authentication and Authorization

## Authentication Tokens
To authenticate a user we have multiple types of Authentication Tokens that allow the use of the basic username and password to certificate based authentication. In the future we will also be adding in concepts such as 
two factor authentication.

 * Username and password
 * Secure Certificate
 * Two Factor Authentication (future)

## Security Realms
A Security Realm is an adapter that allows the Security Service to communicate with a Authentication and Authorization provider. The Definition from Shiro is below.

"A Realm is a component that can access application-specific security data such as users, roles, and permissions. The Realm translates this application-specific data into a format that Shiro understands so Shiro can in
turn provide a single easy-to-understand Subject programming API no matter how many data sources exist or how application-specific your data might be.

Realms usually have a 1-to-1 correlation with a data source such as a relational database, LDAP directory, file system, or other similar resource. As such, implementations of the Realm interface use data source-specific 
APIs to discover authorization data (roles, permissions, etc), such as JDBC, File IO, Hibernate or JPA, or any other Data Access API." - [source] (http://shiro.apache.org/realm.html)

There are several Realms provided by the Security Service today.

 * DataStoreRealm - Used internally to communicate with the SecurityDB.
 * SecurityServiceRealm - Used by remote applications and the message API to remotely authenticate with the SecurityService.
 * [SecurityActiveDirectoryRealm](authentication/activeDirectoryRealm.md) - Configurable Realm to add ActiveDirectory support.
 * [SecurityLdapRealm](authentication/securityLdapRealm.md) - Configurable Realm to add LDAP support.

## How to create your own custom Realm
Future Implementations of third party authentication providers:

 * CAS
 * SAML
 * OAuth
To build a new Security Realm for the Security Module, you need to build the Shiro Realm and specify the metadata around the configuraiton options for that realm, to provide to the UI for configurations.

Resources on how to implement a custom realm can ge found here:
 
 * [http://shiro.apache.org/realm.html](http://shiro.apache.org/realm.html)
 
 * [http://shiro.apache.org/static/current/apidocs/org/apache/shiro/realm/Realm.html](http://shiro.apache.org/static/current/apidocs/org/apache/shiro/realm/Realm.html)

There are already many prebuilt realms that can be used as is, or can be extended or customized to modify behaviour.