# Manifest

The MANIFEST.yaml file supplies information about a product/application. This includes configurations for pipelines,
jobs, services, and cartridges. Much more then just SM. The store the holds this information for retrial is called
the Application/Cartridge Directory.

The Manifest itself is used by:

  * Edifecs App Store - It becomes our product catalog and defines what and where the resources are for deployment
  * ECM Meta Scheduler - For runtime deployment and management (This includes the SM Meta Scheduler Implementation)
  * Service Container - For identification of deployed service types

SM uses it specifically for:

  * Definition of services for the SM Container, and for the Meta Scheduler
  * Flex field definitions for use in ESM profile pages for user, org, tenant, etc... from Logical Properties
  * Service and application name and version
  * Inter-dependencies between services and other components like database servers
  * Defines if a service is app specific or tenant specific

Since the Manifest contains more information than what SM requires. Not all information is mandatory. Only the elements
specific to services are required if that is all that is being used.

More information on Manifest structure:

  * [MANIFEST.yaml Readme](../../shared/packaging/manifest/README.md)

Example Manifests can be found in the examples:

  * [Examples](../../examples/README.md)
  
Information of FlexField Integration into ESM from Manifest file

  * [ESM\Manifest\FlexFields](../../flexfields/README.md)