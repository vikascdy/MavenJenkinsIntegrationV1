# Active Directory Realm
 
Active Directory Realm is the security realm implementation with Active Directory as security provider. It will do the authentication and authorization agaist active directory.

## Implementation
 
SecurityActiveDirectoryRealm inherits SecurityLdapRealm class which also extends from Apache Shiro JndiLdapRealm. 
Like its parent SecurityLdapRealm it supports UsernamePasswordToken to be submitted for authentication.
 
It exposes following APIs as well:
 
    AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) 
    AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    AuthorizationInfo doGetAuthorizationInfo(AuthenticationInfo authenInfo)

## Configuration

To build up SecurityActiveDirectoryRealm the following properties need to be configured:
 
    | Name                         | Description                                                          | Required/Optional  | Default Value     |
    ------------------------------------------------------------------------------------------------------------------------------------------------
    | URL                          | URL of active directory server with format of "ldap://HOSTNAME:PORT" | R                  |                   |
    | System User                  | system user                                                          | R                  |                   |
    | System Password              | system password                                                      | R                  |                   |
    | Domain Name                  | domain name                                                          | R                  |                   |
    | User Search Base             | user search base                                                     | R                  |                   |
    | User Filter                  | user search filter                                                   | R                  |                   |
    | Group Search Base            | group search base                                                    | R                  |                   |
    | Group Filter                 | group search filter                                                  | R                  |                   |
    | First Name Attribute         | first name attribute in active directory                             | O                  | givenName         |
    | Last Name Attribute          | last name attribute in active directory                              | O                  | sn                |
    | Mail Attribute               | email attribute in active directory                                  | O                  | mail              |
    | Distinguished Name Attribute | distinguished name attribute in active directory                     | O                  | distinguishedName |
    | Member Of Attribute          | member of attribute in active directory                              | O                  | memberOf          |


## Sample Configuration


    | URL                          | ladp://corp.edifecs.com:389                 |
    ------------------------------------------------------------------------------
    | System User                  | ENG.PlatformUser1                           |
    | System Password              | Hc2%jbS6                                    |
    | Domain Name                  | EDFX                                        |
    | User Search Base             | OU=Edifecs,DC=corp,DC=edifecs,DC=com        |
    | User Filter                  | (&(objectClass=user)(sAMAccountName=\{0\})) |
    | Group Search Base            | OU=Edifecs,DC=corp,DC=edifecs,DC=com        |
    | Group Filter                 | (&(objectClass=group)(member=\{0\}))        |
    | First Name Attribute         | givenName                                   |
    | Last Name Attribute          | sn                                          |
    | Mail Attribute               | mail                                        |
    | Distinguished Name Attribute | distinguishedName                           |
    | Member Of Attribute          | memberOf                                    |

