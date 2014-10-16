# Hello Example Using Maven

## Overview:

The hello example is aimed at guiding any developer on how to develop an Application using the Service Manager
Framework. The tutorial demonstrates this by creating a sample application named, Hello Example.

## Prerequisites:

The developer should have a basic understanding of Service Manager Framework. 

The developer should also have some basic understanding of maven, as it used to add Service Manager Components as
dependencies to the project.

## Project Structure:

  The project structure follows the recommended project structure for any application development at 
  
  * [Project Structure](../../docs/howto/ApplicationDevelopmentStructure.md)

## Project Set Up:

### hello-example

  1. Create a new maven project named hello-example under apps, with the simple archetype. 


    <properties>
        <service.manager.version>1.6.0.0-SNAPSHOT</service.manager.version>
    </properties>
    
    <parent>
        <groupId>com.edifecs.servicemanager</groupId>
        <artifactId>apps</artifactId>
        <version>1.6.0.0-SNAPSHOT</version>
    </parent>
    
    <groupId>com.edifecs</groupId>
    <artifactId>hello-example</artifactId>
    <version>1.0.0.0-SNAPSHOT</version>
    
    <packaging>pom</packaging>
    
    <name>Hello World Example</name>
    <description>pom to build the base of service-manager based hello example components</description>


  2. Add the following to the pom.xml
    a. Plugins
    There are several Maven plugins that should be adopted as a part of the auto build process:
    
    
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.1</version>
                    <configuration>
                        <source>1.7</source>
                        <target>1.7</target>
                    </configuration>
                </plugin>
        
                <plugin>
                    <artifactId>maven-jar-plugin</artifactId>
                    <version>2.4</version>
                    <configuration>
                        <useDefaultManifestFile>true</useDefaultManifestFile>
                        <archive>
                            <addMavenDescriptor>false</addMavenDescriptor>
                            <manifestEntries>
                                <Specification-Title>${project.name}</Specification-Title>
                                <Implementation-Title>${project.name}</Implementation-Title>
                                <Specification-Version>${project.version}</Specification-Version>
                                <Implementation-Version>${project.version}.${buildNumber}</Implementation-Version>
                                <Specification-Vendor>Edifecs, Inc.</Specification-Vendor>
                                <Specification-Vendor-URL>http://www.edifecs.com</Specification-Vendor-URL>
                                <Implementation-Vendor>Edifecs, Inc.</Implementation-Vendor>
                                <Implementation-URL>http://www.edifecs.com</Implementation-URL>
                            </manifestEntries>
                        </archive>
                    </configuration>
                </plugin>
            </plugins>
        </build>


[maven-compiler-plugin](http://maven.apache.org/plugins/maven-compiler-plugin/) – The Compiler Plugin is used to compile
the sources of your project.
  
[maven-jar-plugin](http://maven.apache.org/plugins/maven-jar-plugin/) – This plugin provides the capability to build and
sign jars.
  
  3. Add following modules to the pom.xml 
  
  This makes hello-example project a parent project.

    <modules>
        <module>hello-example-api</module>
        <module>hello-example-service</module>
        <module>hello-example-ui</module>
        <module>hello-example-dist</module>
    </modules>
  
### hello-example-api

  1. Create a new maven project named hello-example-api under hello-example, with the simple archetype.
  2. Develop a POJO class named HelloMessage
    - create a package named com.edifecs.helloexample.api
    - create a POJO class HelloMessage
    

    package com.edifecs.helloexample.api;

    import java.io.Serializable;

    public class HelloMessage implements Serializable {

        private static final long serialVersionUID = -1L;

        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


### hello-example-service

  1. Create a new maven project named hello-example-service under hello-example, with the simple archetype.
  2. Add the following to the pom.xml
   - Dependencies
    

    <dependency>
        <groupId>com.edifecs.hello-example</groupId>
        <artifactId>hello-example-api</artifactId>
        <version>1.0.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>com.edifecs.epp</groupId>
        <artifactId>service-api</artifactId>
        <version>${service.manager.version}</version>
    </dependency>

  2. MANIFEST.yaml file
  
  
    ---
    manifestVersion: 1.0.0
    
    name: hello-example
    version: 1.5.0.0
    displayName: Hello Example Service
    displayVersion: 1.5.0.0
    description: This is a simple hello world example.
    
    physicalComponents:
      - name: hello-example-service
        version: 1.0.0.0
        displayName: Hello Example Service
        description: This is a simple hello world example service
        type: docker
        scheduling:
          imageUri: SERVICE
          classname: com.edifecs.helloexample.service.HelloExampleService
          scheduler: service
    
        properties:
          - name: name
            type: String
            description: Name of the person say to hello too
            required: true
            defaultValue: world
            scope: Service
    ...
 
 
  3. security.json file
	
     The security.json file defines permissions and roles for this hello-example application. It is created under \src\dist\conf\.
  

    {
        "permissions": [
            {
                "permission": "helloexample:command:service:display:greeting",
                "name": "helloExamplePermission1"
            }
        ],
        "roles": [
            {
                "name": "Hello Example Role",
                "description" : "Hello Example Role Description",
                "permissions": ["helloExamplePermission1"]
            }
        ]
    } 

  4. Develop and register Hello Example Service
    - Create a package com.edifecs.helloexample.service.
    - Create a HelloExampleService class
    
    This is our service, which starts Hello Example Application, when the start method is invoked by SM. The service is
    configured using annotations, which describe the service. This is split into two classes, an interface and an
    implementation
      
     
Interface:


    package com.edifecs.helloexample.service;
    
    import com.edifecs.helloexample.handler.IHelloExampleHandler;
    import com.edifecs.servicemanager.annotations.Handler;
    import com.edifecs.servicemanager.annotations.Property;
    import com.edifecs.servicemanager.annotations.Property.PropertyType;
    import com.edifecs.servicemanager.annotations.Service;
    
    @Service(
        name = "hello-exampleeservice", 
        version = "1.0", 
        description = "Hello Example Service", 
        properties = {@Property(name = "name", propertyType = PropertyType.STRING, description = "" , defaultValue = "world", required = true)}
    )
    public interface IHelloExampleService {
    
        @Handler
        IHelloExampleHandler getHelloExampleCommandHandler();
    }
    
Implementation:

    package com.edifecs.helloexample.service;
    
    import com.edifecs.helloexample.handler.HelloExampleHandler;
    import com.edifecs.helloexample.handler.IHelloExampleHandler;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    import com.edifecs.servicemanager.api.AbstractService;
    
    public class HelloExampleService extends AbstractService implements IHelloExampleService {
    	
    	private Logger logger = LoggerFactory.getLogger(HelloExampleService.class);
    
    	@Override
    	public void start() throws Exception {
    		logger.info("hello example service started.");
    	}
    
    	@Override
    	public void stop() throws Exception {
    		//TODO
    		logger.info("hello example service stopped.");
    	}
    
        @Override
        public IHelloExampleHandler getHelloExampleCommandHandler() {
            String greetee = getProperties().getProperty("name");
            return new HelloExampleHandler(greetee);
        }
    }

 
  5. Create a Interface called IHelloExampleHandler
  
Here we register it as a Service Manager command handler and the method of greeting() as a command with the permission.
  
	
    package com.edifecs.helloexample.handler;
    
    import com.edifecs.helloexample.api.HelloMessage;
    import com.edifecs.message.annotations.Command;
    import com.edifecs.message.annotations.CommandHandler;
    import com.edifecs.message.annotations.JGroups;
    import com.edifecs.message.annotations.RequiresPermissions;
    
    @JGroups(enabled = true)
    @CommandHandler(namespace = "hello-example", description = "")
    public interface IHelloExampleHandler {
    
        @Command
        @RequiresPermissions("helloexample:command:service:display:greeting")
        public HelloMessage greeting();
    }

  
  6. Create a class named, HelloExampleCommandHandler, which implements interface IHelloExampleHandler: 
  
  This is our main program that invokes the Hello Example Service and handles all communication among the registered hello example applications. This is your external communication.


    package com.edifecs.helloexample.handler;
    
    import com.edifecs.epp.isc.async.MessageFuture;
    import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
    import com.edifecs.helloexample.api.HelloMessage;
    
    public class HelloExampleHandler extends AbstractCommandHandler implements IHelloExampleHandler {
    	
    	private String greetee = "";
    	
    	public HelloExampleHandler(String greetee) {
    		super();
    		this.greetee = greetee;
    	}
    	
    	@Override
    	public HelloMessage greeting() {
    		HelloMessage helloMessage = new HelloMessage();
    		helloMessage.setMessage("Hello " + this.greetee + "!");
    		return helloMessage;
    	}
    
        @Override
        public MessageFuture<HelloMessage> greetingFromTheFuture() {
            return MessageFuture.of(greeting());
        }
    }



  7. Metadata file (&lt;applicationname&gt;.META)

 
### hello-example-ui

This project is made for the UI of hello example application which just to display hello message. It is built on ExtJS javascript framework.

  1. Create a new maven project named hello-example-ui under hello-example, with the simple archetype.
  
  2. For pom.xml, set packaging as war.
  
    
    
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <parent>
            <groupId>com.edifecs</groupId>
            <artifactId>hello-example</artifactId>
            <version>1.0.0.0-SNAPSHOT</version>
        </parent>
        
        <groupId>com.edifecs.hello-example</groupId>
        <artifactId>hello-example-ui</artifactId>
        <name>Hello World Example UI</name>
        
        <packaging>war</packaging>
        
    </project> 

 
  3. nav.json file
  
Create nav.json file under \src\dist\conf\ folder. It is all about the landing page of hello example application within the service manager UI.
  
    {
        "service": "Hello Example",
        "id":      "hello-example",
        "name":    "Hello Example",
        "iconUrl": "/hello-example-ui/resources/images/helloexample.png",
        "columnOne": [{
            namespace: "app",
            id:        	"1-manageapp",
            text:      	"Hello Example Application",
            linkUrl:   	"/hello-example-ui",
            permission: "platform:security:administrative:user:view"
        }]
    }
  
  4. Create project structure under \src\main\webapp
  
include app (controller, model, store, view sub folders), resources (css and images sub folders), WEB-INF, META-INF folders and index.html and app.js files.
  
  5. app.js file


	Ext.Loader.setConfig({enabled:true});

	Ext.application({
		requires: ['Ext.container.Viewport'],
		name : 'HelloExample',
	
		appFolder: 'app',
	
		controllers: ['HelloController'],
	
		launch : function() {
			var me = this;

	    	Ext.create('Ext.container.Viewport', {
	        	id : 'helloexample-root-viewport',
	       		layout : 'fit',
	        	border : false,
	        	items : {
	        		xtype : 'helloview'
	        	} 
	    	});
		}	
	});

### hello-example-dist    

  1. Create a new maven project named hello-example-dist under hello-example, with the simple archetype.
  2. Add following to pom.xml:
   - Add dependencies:


    <dependency>
        <groupId>com.edifecs.hello-example</groupId>
        <artifactId>hello-example-api</artifactId>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <groupId>com.edifecs.hello-example</groupId>
        <artifactId>hello-example-service</artifactId>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <groupId>com.edifecs.hello-example</groupId>
        <artifactId>hello-example-ui</artifactId>
        <version>${project.version}</version>
        <type>war</type>
    </dependency>
 
 
   - Plugins: 
   
   
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <executions>
                    <execution>
                        <id>create-dist</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
    
                        <configuration>
                            <finalName>${app.dist.name}</finalName>
                            <appendAssemblyId>true</appendAssemblyId>
                            <descriptors>
                                <descriptor>src/main/assembly/dist.xml</descriptor>
                            </descriptors>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
    
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>unpack</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <artifactItems>
                        <artifactItem>
                            <groupId>com.edifecs.epp</groupId>
                            <artifactId>sm-dist</artifactId>
                            <version>${service.manager.version}</version>
                            <type>tar.gz</type>
                            <overWrite>true</overWrite>
                            <outputDirectory>${installer.resources.dir}</outputDirectory>
                        </artifactItem>
                    </artifactItems>
                </configuration>
            </plugin>
    
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-antrun-plugin</artifactId>
                <executions>
                    <execution>
                        <id>create-staging-area</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>run</goal>
                        </goals>
                        <configuration>
                            <tasks>
                                <copy todir="${staging.dir}/ServiceManager">
                                    <fileset dir="${installer.resources.dir}/ServiceManager" />
                                </copy>
                            </tasks>
                        </configuration>
                    </execution>
                    <execution>
                        <phase>package</phase>
                        <configuration>
                            <tasks>
                                <unzip src="${installer.name}.jar" dest="target/temp" />
                            </tasks>
                        </configuration>
                        <goals>
                            <goal>run</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

  
  This is what each build plugin does:

  3. assembly.xml file

  This is an example dist.xml file that creates a properly formatted application zip file. It is created under \src\main\assembly\ folder.
  
    <assembly
        xmlns="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.1"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.1 http://maven.apache.org/xsd/assembly-1.1.1.xsd">
    
        <formats>
            <format>zip</format>
            <format>dir</format>
        </formats>
    
        <baseDirectory>/</baseDirectory>
    
        <dependencySets>
            <dependencySet>
                <useProjectArtifact>false</useProjectArtifact>
                <outputDirectory>lib</outputDirectory>
                <outputFileNameMapping>${artifact.artifactId}-1.0.0.0.${artifact.extension}</outputFileNameMapping>
                <includes>
                    <include>com.edifecs.hello-example:hello-example-service:*:*:*</include>
                    <include>com.edifecs.hello-example:hello-example-api:*:*:*</include>
                </includes>
            </dependencySet>
            <dependencySet>
                <useProjectArtifact>false</useProjectArtifact>
                <outputDirectory>war</outputDirectory>
                <outputFileNameMapping>${artifact.artifactId}.${artifact.extension}</outputFileNameMapping>
                <includes>
                    <include>com.edifecs.hello-example:*:war:*:*</include>
                </includes>
            </dependencySet>
            <dependencySet>
                <useProjectArtifact>false</useProjectArtifact>
                <outputDirectory>conf</outputDirectory>
                <includes>
                    <include>com.edifecs.hello-example:*:security.json:*:*</include>
                    <include>com.edifecs.hello-example:*:nav.json:*:*</include>
                </includes>
            </dependencySet>
        </dependencySets>
    
        <fileSets>
            <fileSet>
                <directory>src/main/artifacts</directory>
                <outputDirectory>artifacts/hello-example</outputDirectory>
                <includes>
                    <include>**/**</include>
                </includes>
            </fileSet>
            
            <fileSet>
                <directory>${project.basedir}/src/main/resources/</directory>
                <outputDirectory>artifacts/hello-example/resources</outputDirectory>
                <includes>
                    <include>**/**</include>
                </includes>
            </fileSet>
    
            <fileSet>
                <directory>${project.parent.basedir}/hello-example-service/src/dist/conf/</directory>
                <outputDirectory>conf</outputDirectory>
                <includes>
                    <include>*.json</include>
                </includes>
            </fileSet>
            
            <fileSet>
                <directory>${project.parent.basedir}/hello-example-ui/src/dist/conf/</directory>
                <outputDirectory>conf</outputDirectory>
                <includes>
                    <include>*.json</include>
                </includes>
            </fileSet>
            
            <fileSet>
                <directory>${project.parent.basedir}/hello-example-service/target</directory>
                <outputDirectory>/</outputDirectory>
                <includes>
                    <include>*.yaml</include>
                </includes>
            </fileSet>
        </fileSets>
    </assembly>


## Configuring and Running the Hello Example Application:
  
The Hello Example Application can be run by using the Configuration.xml

configuration.xml is the default configuration which loads up the Agent starts; we can manually configure serves to
be run on specific nodes.

    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <DeploymentConfiguration xsi:noNamespaceSchemaLocation="Configuration.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <Cluster>
            <Description></Description>
            <EnvironmentType>Production</EnvironmentType>
            <Name>New Hello Example</Name>
            <Product>
                <Name>Hello Example</Name>
                <Version>1.0</Version>
            </Product>
            <Server>
                <Description></Description>
                <HostName>LUKLIII-LT01</HostName>
                <IPAddress>10.20.40.138</IPAddress>
                <Name>LUKLIII-LT01</Name>
                <Node>
                    <CommunicationPort>9100</CommunicationPort>
                    <Description></Description>
                    <JVMOptions/>
                    <Name>hello-example1</Name>
                    <Role>hello-example</Role>
                    <Service>
                        <Description></Description>
                        <LogLevel>WARNING</LogLevel>
                        <Name>Hello Example Service</Name>
                        <Property>
                            <Name>name</Name>
                            <Value>Service Manager</Value>
                        </Property>
                        <ServiceType>hello-example-service</ServiceType>
                        <Version>1.0</Version>
                    </Service>
                </Node>
            </Server>
        </Cluster>
        <CreatedDate>2014-02-11</CreatedDate>
        <Description></Description>
        <Name>New Hello Example</Name>
        <Version>1.0</Version>
    </DeploymentConfiguration>

Both the methods require an Active Service Manager Agent. For UI, the service-manager-ui project needs to be deployed on a web server.
