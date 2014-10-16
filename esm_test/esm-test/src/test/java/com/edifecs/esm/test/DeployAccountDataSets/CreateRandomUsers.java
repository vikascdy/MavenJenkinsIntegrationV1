package com.edifecs.esm.test.DeployAccountDataSets;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.edifecs.test.common.HtmlReporter;

public class CreateRandomUsers 
{
	
	String CreateExcelforUsers =  System.getProperty("user.dir") + "/integration-tests/esm-test/src/test/resources/RandomUsers.xls";
	static int randomuserscreated;
	static List<String> uname = new ArrayList<String>();
	static List<String> psswd = new ArrayList<String>();
	static List<String> lname = new ArrayList<String>();
	static List<String> fname = new ArrayList<String>();
	static List<String> title = new ArrayList<String>();
	static List<String> mailid = new ArrayList<String>();

	
public List<String> getusername()
{
	return uname;	
}
public List<String> getpassword()
{
	return psswd;	
}
public List<String> getfirstname()
{
	return fname;	
}
public List<String> getlastname()
{
	return lname;	
}
public List<String> gettitle()
{
	return title;	
}
public List<String> getemail()
{
	return mailid;	
}

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  public static void createrandomusers() {
	  try
	{
	  JSONObject json = readJsonFromUrl("http://api.randomuser.me/?results="+randomuserscreated);
   // System.out.println(json.toString());
 
    // Convert Json to xml
    String xml = XML.toString(json);
    xml = "<result>" + xml + "</result>";
 //   System.out.println(xml);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xml)));
    Element rootElement = document.getDocumentElement();
    getString("user", rootElement);
    }
    catch(Exception e)
    {
    	
    }
    
  }
  protected static void getString(String tagName, Element element) {
      NodeList list = element.getElementsByTagName(tagName);
      
      for (int temp = 0; temp < list.getLength(); temp++) 
      {
    	  
  		Node nNode = list.item(temp);
  		
  	//	System.out.println("\nCurrent Element :" + nNode.getNodeName());
   
  		if (nNode.getNodeType() == Node.ELEMENT_NODE) 
  		{
   
  			Element eElement = (Element) nNode;
  			NodeList zip = eElement.getElementsByTagName("zip");
  			NodeList street = eElement.getElementsByTagName("street");
  			NodeList state = eElement.getElementsByTagName("state");
  			NodeList city = eElement.getElementsByTagName("city");
  			
  			
  			NodeList nametitle = eElement.getElementsByTagName("title");
  			NodeList firstname = eElement.getElementsByTagName("first");
  			NodeList lastname = eElement.getElementsByTagName("last");
  			
  			title.add(nametitle.item(0).getChildNodes().item(0).getNodeValue());
  		//	System.out.println("nametitle: "+ nametitle.item(0).getChildNodes().item(0).getNodeValue());
  			
  			fname.add(firstname.item(0).getChildNodes().item(0).getNodeValue());
  		//	System.out.println("firstname: "+ firstname.item(0).getChildNodes().item(0).getNodeValue());
  			
  			lname.add(lastname.item(0).getChildNodes().item(0).getNodeValue());
  		//	System.out.println("lastname: "+ lastname.item(0).getChildNodes().item(0).getNodeValue());
  			
  			
  			/*System.out.println("zip: "+ zip.item(0).getChildNodes().item(0).getNodeValue());
  			System.out.println("street: "+ street.item(0).getChildNodes().item(0).getNodeValue());
  			System.out.println("state: "+ state.item(0).getChildNodes().item(0).getNodeValue());
  			System.out.println("city: "+ city.item(0).getChildNodes().item(0).getNodeValue());
  			System.out.println("phone : " + eElement.getElementsByTagName("phone").item(0).getTextContent());*/
  			
  			uname.add(eElement.getElementsByTagName("username").item(0).getTextContent());
  			//System.out.println("username : " + eElement.getElementsByTagName("username").item(0).getTextContent());
  			
  			mailid.add(eElement.getElementsByTagName("email").item(0).getTextContent());
  			//System.out.println("email : " + eElement.getElementsByTagName("email").item(0).getTextContent());
  			
  			psswd.add(eElement.getElementsByTagName("password").item(0).getTextContent());
  			//System.out.println("password : " + eElement.getElementsByTagName("password").item(0).getTextContent());
  			
  			//System.out.println("cell : " + eElement.getElementsByTagName("cell").item(0).getTextContent());
   
  		}
  	}
  }
      public void createExcelForUsers(int numberofusersneeded)
      {
    	  try
    	  {
    	  CreateRandomUsers reader = new CreateRandomUsers();
    	  
    	  CreateRandomUsers.randomuserscreated = numberofusersneeded;
    	  createrandomusers();
    	  
    	  FileOutputStream fileOut = new FileOutputStream(CreateExcelforUsers);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("Random Users");
			
			HSSFRow row1 = worksheet.createRow((short) 0);

			HSSFCell cellA1 = row1.createCell(0);
			cellA1.setCellValue("UserName");
			HSSFCell cellA2 = row1.createCell(1);
			cellA2.setCellValue("Password");
			HSSFCell cellA3 = row1.createCell(2);
			cellA3.setCellValue("Title");
			HSSFCell cellA4 = row1.createCell(3);
			cellA4.setCellValue("FirstName");
			HSSFCell cellA5 = row1.createCell(4);
			cellA5.setCellValue("LastName");
			HSSFCell cellA6 = row1.createCell(5);
			cellA6.setCellValue("Email");
		
			for(int i=0; i<reader.getusername().size(); i++)
		  	{
			HSSFRow row = worksheet.createRow((short) i+1);
			HSSFCell addusername = row.createCell(0);
			addusername.setCellValue(reader.getusername().get(i));	
			
			HSSFCell password = row.createCell(1);
			password.setCellValue(reader.getpassword().get(i) + "ab123");	
			
			HSSFCell title = row.createCell(2);
			title.setCellValue(reader.gettitle().get(i));	
			
			HSSFCell fname = row.createCell(3);
			fname.setCellValue(reader.getfirstname().get(i));	
			
			HSSFCell lname = row.createCell(4);
			lname.setCellValue(reader.getlastname().get(i));	
			
			HSSFCell email = row.createCell(5);
			email.setCellValue(reader.getemail().get(i));	
		  	}
					
	
			
	
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
			HtmlReporter.log("Excel has been created successfuly", true);
    	  }
    	  catch(Exception e)
    	  {
    		  e.printStackTrace();
    	  }
      }
  
}