#Sonar Setup for App Platform#

Document describing how to setup the quality profiles in SonarQube server and the changes in the configuration of jenkins jobs ESM, SM, x-Board to enable Sonar.



## Configuring Jenkins for Sonar ##
1. Install sonar plugin and database-mysql plugin from `Manage Jenkins` -> `Manage Plugins` -> `Available` .

2. Now configure the jenkins system from `Manage Jenkins` -> `Configure System` .

3. In Sonar Runner installations, install sonar runner with latest version 2.4 from maven central installer . 

4. In Sonar section, configure the system:
   * Name of sonar server for the system.
   * Server URL is the machine IP or hostname with port number on which sonar server is installed.
   * Sonar account login and password, if needed.
   * Database URL is the url for using MySQL database (in this case MySQL is used, other options are embedded, postgresql, oracle database).
   * Database login and password for accessing the MySQL database.
   * Database driver is "com.mysql.jdbc.driver" .
   * Version of sonar-maven plugin is optional (this is used in case of maven projects with sonar as post-build step).

5. In Global Database section, configure the system:
   * Select the databse from drop-down list.
   * Set Host Name same as the bind address in the /etc/mysql/my.cnf file (here IP of machine and default port for database is 3306).
   * Configure the database name with access rights to Username with Password.
   * Test the connection to see everything working good.

## Configuring Jenkins Job for Sonar Analysis ##

1. In Build section of job configuration, add a new build step 'Invoke Standalone Sonar Analysis' using `Add build Step`.
2. Now Configure this build step:
   * Specify the JDK used by Jenkins job.
   * Now for sonar runner configuration there are two ways to tell Jenkins:
     * Specify the path to project properties file that contains the configuration of sonar runner for sonar analysis.
     * We can specify the project properties either mandatory or optional for sonar runner analyzer.

