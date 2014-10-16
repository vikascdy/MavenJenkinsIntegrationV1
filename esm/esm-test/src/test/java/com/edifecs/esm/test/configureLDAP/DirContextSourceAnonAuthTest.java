package com.edifecs.esm.test.configureLDAP;



import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;







import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.core.schema.SchemaPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.ldif.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;



import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.edifecs.esm.test.populatedata.ESMAPI;
import com.edifecs.test.common.HtmlReporter;




public class DirContextSourceAnonAuthTest 
{
  private String configPropertiesAddress =  System.getProperty("user.dir") + "/integration-tests/esm-test/src/test/resources/DeployAccountDataSets_config.properties";

  private static DirectoryService directoryService;
  private static LdapServer ldapServer;
  public static int port=10392;

  @BeforeClass
  public static void startApacheDs() throws Exception 
  {
	  
	System.out.println("startApacheDs");
	port = ReleaseUnusedPort.unusedport(port);
	System.out.println("POrt address " + port);
    String buildDirectory = System.getProperty("buildDirectory");

    File workingDirectory = new File(buildDirectory, "apacheds-work");

    workingDirectory.mkdir();
    directoryService = new DefaultDirectoryService();
    directoryService.setWorkingDirectory(workingDirectory);

    SchemaPartition schemaPartition = directoryService.getSchemaService()
        .getSchemaPartition();
   
    LdifPartition ldifPartition = new LdifPartition();
    String workingDirectoryPath = directoryService.getWorkingDirectory()
        .getPath();
    ldifPartition.setWorkingDirectory(workingDirectoryPath + "/schema");

    File schemaRepository = new File(workingDirectory, "schema");
    
    try
    {
    SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(
        workingDirectory);
    extractor.extractOrCopy(true);

    }
    catch(Exception e)
    {
    	e.printStackTrace();
    }
    
    schemaPartition.setWrappedPartition(ldifPartition);

    SchemaLoader loader = new LdifSchemaLoader(schemaRepository);
    SchemaManager schemaManager = new DefaultSchemaManager(loader);
    directoryService.setSchemaManager(schemaManager);

    schemaManager.loadAllEnabled();

    schemaPartition.setSchemaManager(schemaManager);

    List<Throwable> errors = schemaManager.getErrors();

    if (!errors.isEmpty())
      throw new Exception("Schema load failed : " + errors);

    JdbmPartition systemPartition = new JdbmPartition();
    systemPartition.setId("system");
    systemPartition.setPartitionDir(new File(directoryService
        .getWorkingDirectory(), "system"));
    systemPartition.setSuffix(ServerDNConstants.SYSTEM_DN);
    systemPartition.setSchemaManager(schemaManager);
    directoryService.setSystemPartition(systemPartition);

    directoryService.setShutdownHookEnabled(false);
    directoryService.getChangeLog().setEnabled(false);

    ldapServer = new LdapServer();
    ldapServer.setTransports(new TcpTransport(port));
    ldapServer.setDirectoryService(directoryService);

    directoryService.startup();
    ldapServer.start();
  }

  @AfterClass
  public static void stopApacheDs() throws Exception {
    ldapServer.stop();
    directoryService.shutdown();
    directoryService.getWorkingDirectory().delete();
    System.out.println("Connection is closed now");
  }

  public String anonAuth(int orgID) throws NamingException, InterruptedException {
	  try
	  {
		  String MachineIPAddress;
	  System.out.println("anonAuth");
	  Person person;
	  person = LDAPMain.createusers();
    
    FileReader reader = new FileReader(configPropertiesAddress);
	Properties properties = new Properties();
	properties.load(reader);
	MachineIPAddress = properties.getProperty("MachineIPAddress");
    
	ESMAPI api = new ESMAPI();
    api.CheckLDAPConnectionAndSaveSetting(orgID,port,MachineIPAddress,person.getName(),person.getPassword());
    Thread.sleep(5000);
    return person.getName();
	  }
	  catch(Exception e)
	  {
		  HtmlReporter.log("LDAP Connection Failure", true);
		  e.printStackTrace();
		  throw new RuntimeException();
	  }
  }

}