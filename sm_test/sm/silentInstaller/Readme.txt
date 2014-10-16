Installation Steps
----------------------------------------------------------------------------------------------------------------------------------------
1. Install JDK 1.7 on Linux Machine. Edifecs Service Manager does not support OpenJDK for now.

2. Connect to Edifecs Network or VPN to download the Service Manager installer from the ENBUILD server.

3. Copy the "autoInstall.xml" and "install_SM.sh" into the linux box using SSH client or WinSCP

4. Make install_SM.sh runnable 
   chmod +x install_SM.sh

5. Run the script   
   sudo ./install_SM.sh
   
   