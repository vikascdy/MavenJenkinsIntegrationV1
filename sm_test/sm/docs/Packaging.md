# Application Package format

    <Application Name>/
        |-- MANIFEST.yaml
        |-- resources/
        |      |-- <some-image-used-for-config>
        |      +-- <some-js-used-for-config>
        |-- services/
        |   +-- <Service Name>/
        |       |-- conf/
        |       |   |-- nav.json
        |       |   |-- security.json
        |       |   +-- featured-items.json
        |       +- lib/
        |-- html/
        |   +-- <Web App Name>/
        |       +-- <some-web-resources>
        +-- artifacts/
            +-- <Artifacts>
        
  * [MANIFEST.yaml file](manifest/README.md) - Configuration for the application
  
  * resources - folder is used to store EAS related resources such as images to use for the App Icon.
    
    // TODO: Rename resources folder

  * services - contains SM specific services and service configuration files and jar libs
  
  * html - contains all static HTML content that is served by the web servers
  
  * artifacts - modifiable complex configuration files and resources that are deployed to the Content Repository 