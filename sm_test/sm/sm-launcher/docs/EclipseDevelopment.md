# Eclipse Development

The easiest way to launch SM from an IDE is to include the sm-launcher dependency into one of your projects and configure the run configurations below

Maven `sm-launcher` Dependency Information:

    <dependency>
      <groupId>com.edifecs.epp</groupId>
      <artifactId>sm-launcher</artifactId>
      <version>1.6.0.0-SNAPSHOT</version>
    </dependency>



## Running a Node

Go to:

    Run -> Run Configurations
    Create  new Java Application run configuration
 
Set the following Properties on these pages


  * Name: `Node Start`
  * Project: `sm-launcher`
  * Main class: `com.edifecs.agent.launcher.NodeStarter`
  * Program arguments:


    -nodeName=<node-name>


  * VMArguments (Only Required in Linux):


    -Djava.net.preferIPv4Stack=true
    
  * Working Directory (Other):


    <service-manager-dir>/bin


## Running an Agent

Go to:
  
    Run -> Run Configurations
    Create new Java Application run configuration
 
Set the following Properties on these pages

  * Name: `Agent Start`
  * Project: `sm-launcher`
  * Main class: `com.edifecs.agent.launcher.AgentStarter`
  * Program arguments:
    * *Optional* - Launch Only Agent without Node: `-launchNodes=false`
    * *Optional* - To disable colored console output: `-nocolor`
    
  * VMArguments:


    -Djava.net.preferIPv4Stack=true
    
  * Working Directory (Other):


    <service-manager-dir>/bin



 


