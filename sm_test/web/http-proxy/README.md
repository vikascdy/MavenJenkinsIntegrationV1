# How to Set Up the JSON Proxy Servlet from Eclipse

## Preliminary

*  Make sure you have the Java EE tools and Web development tools installed in
  your Eclipse installation.
* Make sure you have an external Tomcat server configured and available in
  Eclipse.
* If you are connecting to a Service Manager instance running anywhere other
  than localhost:8080, then:
    * Open service-manager/tools/json/src/webapp/WEB-INF/web.xml
    * Change the 'targetUri' param from "http://localhost:8080/json" to the
    location and port of your Service Manager instance, followed by "/json".

## Step 1

* Right-click on the 'json' project in Eclipse (service-manager/tools/json) and
  select "Properties."
* In the properties window, select "Project Facets."
* Click on the "Convert to faceted form..." link.
* Check the "Dynamic Web Module" facet.
* A "Further configuration available..." link will appear at the bottom of the
  window; click it.
* In the dialog that appears, there will be a "Content directory" field. Replace
  the contents of this field with "src/main/webapp".
* Click OK to close this dialog, then click OK to close the properties dialog.


    NOTE: If the 'json' project is now marked with a red 'error' X, but no source
      files are marked with the red X, try setting the Java compiler level of
      the 'json' project to 1.7.

## Step 2

* Find the Ext JS UI project you want to deploy (for example, 'bpm-ui').
* Repeat the process from Step 1: right-click on this project, go to
  "Properties" -> "Project Facets," convert it into a faceted project, add the
  "Dynamic Web Module" facet, select "Further configuration available..." and
  enter "src/main/webapp" in the "Content directory" field.


    WARNING: Enabling the Web facet on an Ext JS project will also enable the
             JavaScript validator, which will attempt to validate all of the
             JavaScript source files in the project, including Ext JS's source code.
             This is incredibly slow and will sometimes crash Eclipse.
         
You can disable this by going to the project's Properties, selecting
Builders, and unchecking the JavaScript Validator. (Ignore the warning
message about "This is an advanced operation"; it's harmless)

## Step 3

* Make sure the backend app for the UI project you are deploying is installed in
  Service Manager's apps directory.
* Launch Service Manager, either through Eclipse or by running start.bat. Leave
  it running in the background.

## Step 4

* In the Servers view (open it if it's not visible), double-click on your Tomcat
  server, and make sure its HTTP port is set to something * other than*  8080 so
  that it doesn't conflict with Service Manager.
* Right-click on the Ext JS UI project that you configured in Step 2, open the
  "Run As" menu, and select "Run on Server" 
* In the dialog that appears, select your Tomcat server, then click "Next."
* The next page has two columns. Your UI project should already be in the right
  column; add the "json" module to the right column as well, then click
  "Finish."
* Navigate to your new server, and everything should work now! (You may have to
  sign in through the Security Service on the Service Manager server first.)
