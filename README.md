#Sonar Setup for App Platform#

Document describing how to setup the quality profiles in SonarQube server and the changes in the configuration of jenkins jobs ESM, SM, x-Board to enable Sonar.

## Quality profiles setup in Sonar ##
We setup CheckStyle and PMD as Java quality profiles and a default JavaScript quality profile by installing JavaScript plugin. 
One can install a plugin via update center using authentication. 
Default profile is "Sonar way" which comes with language plugins so that sonar runner can analyze the project.

### CheckStyle and PMD profile ###
1. First login sonar using the authentication. Then in `Settings` section go to `Update Center` and install `Checkstyle` , `Java` , `JavaScript` and `PMD` plugins from `Availabele Plugins` .
2. After Installing the plugins, restart the Sonar.
3. Go to `Quality Profiles` tab. This displays default profiles named 'Sonar way' based on language plugins.
4. Under Java Profiles, create a new profile by clicking `+Create` in the right corner.
5. Use a suitable name 'Checkstyle and PMD' and upload the configuration files (.xml files) for both Checkstyle and PMD configuration.
6. Each profile conatains the rules which have severity levels of either info, minor, major,critical or blocker.



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
     * We can explicitly specify the project properties (can be mandatory or optional) for sonar runner analyzer.

