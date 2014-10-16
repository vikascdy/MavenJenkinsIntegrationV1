# LDAP Realm Configuration

LDAP Realm is the security realm implementation with LDAP as security provider. It will do the authentication and authorization agaist LDAP server.

## Implementation
 
SecurityLdapRealm extends from Apache Shiro JndiLdapRealm. It supports UsernamePasswordToken to be submitted for authentication.
 
It exposes following APIs:
 
    AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) 
    AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    AuthorizationInfo doGetAuthorizationInfo(AuthenticationInfo authenInfo)

## Configuration

To build up SecurityLdapRealm the following properties need to be configured:
 
    | Name                         | Description                                                          | Required/Optional  | Default Value     |
    ------------------------------------------------------------------------------------------------------------------------------------------------
    | URL                          | URL of ldap server with format of "ldap://HOSTNAME:PORT"             | R                  |                   |
    | System User                  | system user                                                          | R (O if anonymous)|              |
    | System Password              | system password                                                      | R (O if anonymous)|                   |
    | User Search Base             | user search base                                                     | R                  |                   |
    | User Filter                  | user search filter                                                   | R                  |                   |
    | Group Search Base            | group search base                                                    | R                  |                   |
    | Group Filter                 | group search filter                                                  | R                  |                   |
    | First Name Attribute         | first name attribute in ldap                                         | O                  | givenName         |
    | Last Name Attribute          | last name attribute in ldap                                          | O                  | sn                |
    | Mail Attribute               | email attribute in ldap                                              | O                  | mail              |
    | Distinguished Name Attribute | distinguished name attribute in ldap                                 | O                  | distinguishedName |
    | User DN                      | user replaceable distinguished name eg : uid={0},ou=users,ou=system  | O                  | {0} |


### Sample Configuration


    | URL                          | ladp://corp.edifecs.com:389                 |
    -------------------------------+----------------------------------------------
    | System User                  | ENG.PlatformUser1                           |
    | System Password              | Hc2%jbS6                                    |
    | User Search Base             | OU=Edifecs,DC=corp,DC=edifecs,DC=com        |
    | User Filter                  | (&(objectClass=user)(sAMAccountName=\{0\})) |
    | Group Search Base            | OU=Edifecs,DC=corp,DC=edifecs,DC=com        |
    | Group Filter                 | (&(objectClass=group)(member=\{0\}))        |
    | First Name Attribute         | givenName                                   |
    | Last Name Attribute          | sn                                          |
    | Mail Attribute               | mail                                        |
    | Distinguished Name Attribute | distinguishedName                           |
