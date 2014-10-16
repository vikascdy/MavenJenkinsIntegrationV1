package com.edifecs.esm.test.configureLDAP;
 
//import com.edw.bean.Person;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Hashtable;

import com.edifecs.test.common.HtmlReporter;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;








import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import sun.misc.BASE64Encoder;

/**
 *  com.edw.ldap.main.LDAPMain
 *
 *  @author edw
 */
public class LDAPMain {
 
		static String ReadRandomUsers =  System.getProperty("user.dir") + "/integration-tests/esm-test/src/test/resources/RandomUsers.xls";
		static int randomusername = 1;
        private Hashtable<String, String> env = new Hashtable<String, String>();
 
    public void LDAPMain123() {
        try { int port;
        	//port = ReleaseUnusedPort.unusedport(port);
        	port = DirContextSourceAnonAuthTest.port;
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://localhost:"+port+"");
            System.out.println("ldap://localhost:"+port+"");
            env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
            env.put(Context.SECURITY_CREDENTIALS, "secret");
            
       /*  // Specify timeout to be 10 seconds
            env.put("com.sun.jndi.ldap.connect.timeout", "10000");*/
        } catch (Exception e) {
            HtmlReporter.log(e.toString());
            e.printStackTrace();
        }
 
    }
 
    private boolean insert(Person person) {
        try {
        	System.out.println("Entered Insert Person");
        	LDAPMain123();
            DirContext dctx = new InitialDirContext(env);
            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute("uid", person.getName()));
            matchAttrs.put(new BasicAttribute("cn", person.getName()));
           // matchAttrs.put(new BasicAttribute("street", person.getAddress()));
            matchAttrs.put(new BasicAttribute("sn", person.getName()));
            matchAttrs.put(new BasicAttribute("userpassword", encryptLdapPassword("SHA", person.getPassword())));
            matchAttrs.put(new BasicAttribute("objectclass", "top"));
            matchAttrs.put(new BasicAttribute("objectclass", "person"));
            matchAttrs.put(new BasicAttribute("objectclass", "organizationalPerson"));
            matchAttrs.put(new BasicAttribute("objectclass", "inetorgperson"));
            String name = "uid=" + person.getName() + ",ou=users,ou=system";
            InitialDirContext iniDirContext = (InitialDirContext) dctx;
            iniDirContext.bind(name, dctx, matchAttrs);
            
            System.out.println("success inserting "+person.getName());
            HtmlReporter.log("success inserting "+person.getName());
            return true;
        } catch (NameAlreadyBoundException e) 
        {
            HtmlReporter.log(e.toString());
            HtmlReporter.log("User " +person.getName()+ " already exists so creating new user");
            createusers();
            return true;
        }
        catch(NamingException e)
        {
        	e.printStackTrace();
        	return false;
        }
    }
 
    private boolean edit(Person person) {
        try {
 
            DirContext ctx = new InitialDirContext(env);
            ModificationItem[] mods = new ModificationItem[2];
            Attribute mod0 = new BasicAttribute("street", person.getAddress());
            Attribute mod1 = new BasicAttribute("userpassword", encryptLdapPassword("SHA", person.getPassword()));
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
            mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod1);
 
            ctx.modifyAttributes("uid=" + person.getName() + ",ou=users,ou=system", mods);
 
            HtmlReporter.log("success editing "+person.getName());
            return true;
        } catch (Exception e) {
            HtmlReporter.log(e.toString());
            e.printStackTrace();
            return false;
        }
    }
 
    private boolean delete(Person person) {
        try {
 
            DirContext ctx = new InitialDirContext(env);
            ctx.destroySubcontext("uid=" + person.getName() + ",ou=users,ou=system");
 
            HtmlReporter.log("success deleting "+person.getName());
            return true;
        } catch (Exception e) {
            HtmlReporter.log(e.toString());
            e.printStackTrace();
            return false;
        }
    }
     
    private boolean search(Person person) {
        try {
        	System.out.println("Entered");
 
            DirContext ctx = new InitialDirContext(env);
        	
            String base = "ou=users,ou=system";
 
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
 
            String filter = "(&(objectclass=person)(uid="+person.getName()+"))";
 
            NamingEnumeration<SearchResult> results = ctx.search(base, filter, sc);
 System.out.println(results + "sas");
 
            while (results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();
 
                Attribute attr = attrs.get("uid");
                if(attr != null) {
                    HtmlReporter.log("record found " + attr.get());
                }
            }
            ctx.close();
                         
            return true;
        } catch (Exception e) {
            HtmlReporter.log(e.toString());
            e.printStackTrace();
            return false;
        }
    }
 
    private String encryptLdapPassword(String algorithm, String _password) {
        String sEncrypted = _password;
        if ((_password != null) && (_password.length() > 0)) {
            boolean bMD5 = algorithm.equalsIgnoreCase("MD5");
            boolean bSHA = algorithm.equalsIgnoreCase("SHA")
                    || algorithm.equalsIgnoreCase("SHA1")
                    || algorithm.equalsIgnoreCase("SHA-1");
            if (bSHA || bMD5) {
                String sAlgorithm = "MD5";
                if (bSHA) {
                    sAlgorithm = "SHA";
                }
                try {
                    MessageDigest md = MessageDigest.getInstance(sAlgorithm);
                    md.update(_password.getBytes("UTF-8"));
                    sEncrypted = "{" + sAlgorithm + "}" + (new BASE64Encoder()).encode(md.digest());
                } catch (Exception e) {
                    sEncrypted = null;
                    HtmlReporter.log(e.toString());
                    e.printStackTrace();
                }
            }
        }
        return sEncrypted;
    }
 
   public static Person createusers()
    {
	   try
	   {
    	System.out.println("Entered createusers");
        LDAPMain main = new LDAPMain();
 
        FileInputStream fileInputStream = new FileInputStream(ReadRandomUsers);
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		HSSFSheet worksheet = workbook.getSheet("Random Users");
        
		HSSFRow row1 = worksheet.getRow(randomusername);
		
		randomusername = randomusername + 1;
		
		HSSFCell cell = row1.getCell(0);
		String username = cell.getStringCellValue();
		
		HSSFCell cell1 = row1.getCell(1);
		String password = cell1.getStringCellValue();
        
        Person person = new Person();
      //  person.setAddress("kebayoran");
        person.setName(username);
        person.setPassword(password);
 
        // insert
        main.insert(person);
         return person;
        // edit
     //   main.edit(person);
         
        // select
        //main.search(person);
         
        // delete
    //   main.delete(person);
    }
	   catch(IOException e)
	   {
		   e.printStackTrace();
		   throw new RuntimeException();
	   }
	   catch(NullPointerException e)
	   {
		   HtmlReporter.log("Random users created are finished", true);
		   throw new RuntimeException("Please create new users and re run script");
	   }
    }
   
   public static String ReadUsernameForESMUser()
   {
	   try
	   {
	   FileInputStream fileInputStream = new FileInputStream(ReadRandomUsers);
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		HSSFSheet worksheet = workbook.getSheet("Random Users");
       
		HSSFRow row1 = worksheet.getRow(randomusername);
		
		randomusername = randomusername + 1;
		
		HSSFCell cell = row1.getCell(0);
		String username = cell.getStringCellValue();
		
		return username;

	   }
	   catch(IOException e)
	   {
		   e.printStackTrace();
		   throw new RuntimeException();
	   }
	   catch(NullPointerException e)
	   {
		   HtmlReporter.log("Random users created are finished", true);
		   throw new RuntimeException("Please create new users and re run script");
	   }
   }
}