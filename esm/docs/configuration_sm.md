# Configuration of ESM for local installs

Unzip the esm-dist distribution file:

    <dependency>
      <groupId>com.edifecs.epp</groupId>
      <artifactId>esm-dist</artifactId>
      <version>1.7.0.0-SNAPSHOT</version>
      <classifier>dist</classifier>
      <type>zip</type>
    </dependency>

Into the ServiceManager/apps/ folder on all machines in the cluster.

Modify the Configuration.xml file to add the ESM service to the file.

    <DeploymentConfiguration xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:noNamespaceSchemaLocation="Configuration.xsd">
    	<Cluster>
    		<Server>
    			<Node>
                    ...
                    <!-- esm-service -->
                    <Service>
                        <Name>esm-service</Name>
                        <ServiceType>esm-service</ServiceType>
                        <Description>esm-service</Description>
                        <LogLevel>WARNING</LogLevel>
                        <Version>1.0</Version>
                        <Property>
                            <Name>password.max.attempts</Name>
                            <Value>5</Value>
                        </Property>
                        <Property>
                            <Name>password.reset.lockout.interval</Name>
                            <Value>5</Value>
                        </Property>
                        <Property>
                            <Name>password.reset.login</Name>
                            <Value>false</Value>
                        </Property>
                        <Property>
                            <Name>password.lockout.duration</Name>
                            <Value>15</Value>
                        </Property>
                        <Property>
                            <Name>password.history</Name>
                            <Value>3</Value>
                        </Property>
                        <Property>
                            <Name>password.age</Name>
                            <Value>120</Value>
                        </Property>
                        <Property>
                            <Name>password.regex</Name>
                            <Value>^([a-zA-Z0-9@*#]{8,15})$</Value>
                        </Property>
                        <Property>
                            <Name>password.regex.name</Name>
                            <Value>Default Password Regex</Value>
                        </Property>
                        <Property>
                            <Name>password.regex.description</Name>
                            <Value>Password matching expression. Match all alphanumeric character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters</Value>
                        </Property>
                        <Resource>
                            <Name>Persisted H2 DB</Name>
                            <TypeName>Security Database</TypeName>
                        </Resource>
                    </Service>
                </Node>
            </Server>
            <Resource>
                <Description></Description>
                <Name>In Memory H2 DB</Name>
                <Property>
                    <Name>URL</Name>
                    <Value>jdbc:h2:mem:securityDB</Value>
                </Property>
                <Property>
                    <Name>Driver</Name>
                    <Value>org.h2.Driver</Value>
                </Property>
                <Property>
                    <Name>Dialect</Name>
                    <Value>org.hibernate.dialect.H2Dialect</Value>
                </Property>
                <Property>
                    <Name>Username</Name>
                    <Value>sa</Value>
                </Property>
                <Property>
                    <Name>Password</Name>
                    <Value></Value>
                </Property>
                <Property>
                    <Name>AutoCreate</Name>
                    <Value>true</Value>
                </Property>
                <Type>JDBC Database</Type>
            </Resource>
    	</Cluster>
    </DeploymentConfiguration>
