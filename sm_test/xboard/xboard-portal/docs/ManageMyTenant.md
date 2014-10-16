#Tenant Configuration

The tenant configuration screen presents a tenant admin with the following options:

##Overview

##Manage Organizations

##Manage Roles

##Manage Groups

##Settings
The settings config area allows for the configuration of a customizable landing page, a tenant logo, and the
tenant password policy configuration.

###Tenant Landing Page
The tenant landing page configuration options specifies the page to which the tenant user should be redirected
after successful login.  The URL can be either a fully specified URL or relative the the application root.
Upon login, if the landing page is not found, the user will be silently redirected to the default landing page
for the site.

Validations:  The landing page value must begin with 'http' for absolute links or '/' for relative links.

###Tenant Logo
This section allows the administrator to change the tenant logo.  If a tenant logo currently exists,
it is displayed.  If not, a cross-hatched canvas area is displayed.  If the administrator attempts to upload
an image that is of a size that is likely to distort the xbar area (i.e. if the image differs by more than 10 
pixels from 190 x 60), the user will be warned but will be allowed to upload the image.

After a successful upload, the Current Logo image will be updated to display the new icon.  The new Icon 
will not be visible in the XBar until a page refresh or new login.



###Tenant Password Policy