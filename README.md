MavenJenkinsIntegrationV1
=========================

this is used for maven projects to deploy on a server like tomcat, glassfish via jenkins continuous server


#Sonar Setup for App Platform#

Document describing how to setup the quality profiles in SonarQube server and the changes in the configuration of jenkins jobs ESM, SM, x-Board to enable Sonar.



## Configuring Jenkins for Sonar ##
1. Install sonar plugin and database-mysql plugin from Manage Jenkins -> Manage Plugins -> Available .
2. Now configure the jenkins system from Manage Jenkins -> Configure System .
3. In Sonar Runner installations, install sonar runner with latest version 2.4 from maven central installer . 
4. In Sonar section, configure the system:
   * Name of sonar server for the system.
5. In Global Database section, configure the system:
   Select the databse from drop-down list.
   Host Name is the bind address in the /etc/mysql/my.cnf file (here IP of machine and default port for database is 3306).
   Configure the database name with access rights to Username with Password.
   Test the connection to see everything working good.
