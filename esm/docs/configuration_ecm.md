# Configuration of ESM for ECM based deployments

[Install and setup ECM](https://gitlab.corp.edifecs.com/platform/cluster/blob/master/ecm/README.md)

Start Cluster

    cd <coreos-vagrant>
    ecm boot --vagrant core-01 --store app-store:9000 --registry registry:5000
    
Add Hosts

    ecm --vagrant <host-name> addHost --meta <name>=<value>,<name>=<value>,....

Provision an App for a Tenant (In this case is a cartridge)

    ecm addApp --tenant <t> --app esm
    
Lists the deployments and gives you the ID of the deployments
    
    ecm listDeployments  --tenant <t> --app <name>
    
Configure the ESM app given the deployment ID's

    ecm setConfig --id <deployment-id> <stdin>
    
    esm:
        password.max.attempts: 5
        password.reset.lockout.interval: 5
        password.reset.login:
        password.lockout.duration:
        password.history:
        password.age:
        password.regex:
        password.regex.name:
        password.regex.description:
        Security Database:
            URL: jdbc:h2:mem:securityDB
            Driver: org.h2.Driver</Value>
            Dialect: org.hibernate.dialect.H2Dialect</Value>
            Username: sa</Value>
            Password: 
            AutoCreate: true
    
Start the application

     ecm startApp --id <app-id>
