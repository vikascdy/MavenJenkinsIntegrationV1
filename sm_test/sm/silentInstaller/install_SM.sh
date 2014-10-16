#!/bin/bash
set -e
set -o pipefail

#Download the SM 1.5 Snapshot
echo "--------------------------------------------------------------"
echo "Downloading Service Manager 1.5 Installer"
echo "--------------------------------------------------------------"

wget https://enbuild/view/SS%20Projects/job/SS.ServiceManager/lastSuccessfulBuild/artifact/distribution/installer/target/installer-1.6.0.0-SNAPSHOT-package.jar '--no-check-certificate'

#Install the Service Manager
echo "----------------------------------------------------------------"
echo "Installing the Service Manager"
echo "----------------------------------------------------------------"
java -jar installer-1.6.0.0-SNAPSHOT-package.jar autoInstall.xml

# Start the service manager
echo "----------------------------------------------------------------"
echo "Starting the service manager"
echo "----------------------------------------------------------------"

sh -x /opt/edifecs/ServiceManager/bin/startAsync.sh

echo "----------------------------------------------------------------"
echo "Done and wait for 5-10 seconds to start the SM agent and node " 
echo "----------------------------------------------------------------"
