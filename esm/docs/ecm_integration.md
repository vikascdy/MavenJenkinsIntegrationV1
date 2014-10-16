# ESM PaaS/IaaS Integration

## Dependent Physical Components

  * MySQL Cartridge
  * Content Repository

The MySQL and ContentRepository cartridges are configured and installed before ESM is installed. ECM manages this startup
process.

## Lifecycle Events

### Deploy

  * SQL DB Creation
  
Creates the DB Scheme and tables. This is a SQL Script and a sh file that executes the table creation. This is the only
lifecycle event called that does not need to be written using the Java API.

Shell Script Example:

    mysql -h "server-name" -u "root" -p "XXXXXXXX" "database-name" < "create-db.sql"

### Provision

  * HTML Content

    Deploys the HTML content of ESM to the tenant's HTML store within the ContentRepository.

  * Default Role and Permission Creation

    Takes the ESM roles and permissions and creates them for the tenants as they are created.

Both of these lifecycle events are shared, and all that a dev needs to do is specify them within the Manifest file.
Using out dev environment, this can be abstracted out from the devs and done automatically as part of the package
process.

### Run

  Start the SM container configured to run the service.

Need to specify the runtime properties needed, as well as the Classname of the service to run.

### UnProvision

  * Removes the HTML Content from the tenants content repository
   
  * Removes the configured Roles and Permissions

Both of these lifecycle events are shared, and all that a dev needs to do is specify them within the Manifest file.
Using out dev environment, this can be abstracted out from the devs and done automatically as part of the package
process.

### UnDeploy

  * Deletes and removes the database scheme of the ESM DB from mysql. 

Shell Script Example:

    mysql -h "server-name" -u "root" -p "XXXXXXXX" "database-name" < "drop-db.sql"

## Package and Deployment to EAS and Docker Registry

The `sm-eas-deploy` tool is used to publish the created component to EAS and the DockerRegistry.

This is a SBT or Maven plugin.
 
[sm-eas-deploy tool](https://gitlab.corp.edifecs.com/platform/sm/blob/develop/sm/tools/sm-eas-deploy/README.md)

The development environment helps build out the ZIP distribution:

[Application ZIP Distribution](https://gitlab.corp.edifecs.com/platform/sm/blob/develop/sm/docs/Packaging.md)

## Manifest File

    ---
    manifestVersion: 1.0.0

    name: esm
    version: 1.0.0.0
    displayName: Edifecs Security Manager
    displayVersion: 1.5.0.0
    description: This is the Identity Management component for use in all Edifecs applications.

    physicalComponents:
      - name: esm-deploy
        displayName: ESM Deployment
        description: Installation and configuration of ESM's database.
        lifeCycleStage: deploy
        scheduling:
          classname: scripts/mysql_esm_db_install.sh
          scheduler: script
        properties:
          // TODO: SQL Config Properties required
        dependencies:
          - name: mysql
            versionSpec: 5.6.21
            optional: false

      - name: esm-provision-html
        displayName: ESM Provision Html
        description: Installation of the ESM Html content
        lifeCycleStage: provision
        scheduling:
          classname: com.edifecs.epp.web.lifecycle.ProvisionHtmlLifecycle
          scheduler: service
        dependencies:
          - name: content-repository
            versionSpec: 1.6.0.0
            optional: false

      - name: esm-provision-roles
        displayName: ESM Provision Roles
        description: Installation and configuration of default roles and permissions
        lifeCycleStage: provision
        scheduling:
          classname: com.edifecs.epp.security.lifecycle.ProvisionRoleLifecycle
          scheduler: service
        dependencies:
          - name: mysql
            versionSpec: 5.6.21
            optional: false

      - name: esm-unprovision-roles
        displayName: ESM Role Deployment
        description: UnInstallation of ESM default roles and permissions
        lifeCycleStage: unprovision
        scheduling:
          classname: com.edifecs.epp.security.lifecycle.UnProvisionRoleLifecycle
          scheduler: service
        dependencies:
          - name: mysql
            versionSpec: 5.6.21
            optional: false

      - name: esm-unprovision-html
        displayName: ESM HTML Deployment
        description: UnInstallation of ESM Html content
        lifeCycleStage: unprovision
        scheduling:
          classname: com.edifecs.epp.web.lifecycle.UnProvisionHtmlLifecycle
          scheduler: service
        dependencies:
          - name: content-repository
            versionSpec: 1.6.0.0
            optional: false

      - name: esm-undeploy
        displayName: ESM Uninstall
        description: UnInstallation of ESM's database.
        lifeCycleStage: undeploy
        scheduling:
          classname: scripts/mysql_esm_db_uninstall.sh
          scheduler: script
        properties:
          // TODO: SQL Config Properties required
        dependencies:
          - name: mysql
            versionSpec: 5.6.21
            optional: false

      - name: esm-service
        displayName: ESM Service
        description: This is the main Security Service for all of ESM
        scheduling:
          classname: com.edifecs.epp.security.service.SecurityService
          scheduler: service
        dependencies:
          - name: mysql
            versionSpec: 5.6.21
            optional: false

        properties:
          - name: account.lockout.threshold
            description: >
              Specifies how many unsuccessful logon attempts should happen before an account is locked.
              The value 0 means that the account is never going to be locked, i.e. locking out
              mechanism is disabled.
            scope: Service
            constraint:
              dataType: int
              defaultValue: 10
              validationMessage: Account lockout threshold must be a number between 1 and 100

          - name: account.lockout.counter.reset.interval
            description: >
              Specifies the interval in minutes that needs to pass since the last logon attempt
              (successful or unsuccessful) to automatically reset the number of unsuccessful logon
              attempts to 0.
            scope: Service
            constraint:
              dataType: int
              defaultValue: 10
              validationMessage: Account lockout threshold must be a number between 1 and 100

          - name: account.lockout.duration
            description: >
              Specifies how long the account is going to be locked out after a certain amount of
              unsuccessful logon attempts. The entered value specifies the interval in minutes. A
              special value of 0 means that the account is locked out until the Site /
              CommunityGateway Admin explicitly unlocks the account.
            scope: Service
            constraint:
              dataType: int
              defaultValue: 10
              validationMessage: Account lockout threshold must be a number between 1 and 100

        profileAttributes:
          - name: account.lockout.counter.reset.interval
            description: >
              Specifies the interval in minutes that needs to pass since the last logon attempt
              (successful or unsuccessful) to automatically reset the number of unsuccessful logon
              attempts to 0.
            scope: Service
            constraint:
              dataType: int
              defaultValue: 10
              validationMessage: Account lockout threshold must be a number between 1 and 100

    ...

