# IntelliJ UI Deployment

In development, deploying changes to the application is as simple as copying the files to their bundled location, 
assuming that the deployment is not zipped.

## BlinkSync
BlinkSync is a command line utility that, among other things, compares the contents of two folders and copies
files from the source to destination folder if they are newer than their destination folder counterparts.  

[BlinkSync](http://blinksync.sourceforge.net/)

## IntelliJ Setup
To enable BlinkSync in IntelliJ, navigate to Settings->External Tools.  Create a new external program and enter the following:

* **Name** Whatever you want to call the copy task.
* **Program** Path the the BlinkSync exe
* **Parameters** \<SourceFolder\> \<Target Folder\>

The source folder should be the webapp root folder and contain only non-compiled content.

After creating the external tool, it is helpful to map it to a shortcut.

Under Settings, navigate to Keymaps->External Tools, and right click the external tool you just created.  Enter a new
shortcut.  If it is available, click save.  Now invoking that shortcoming from withing IntelliJ IDEA will copy any updated
source files.  