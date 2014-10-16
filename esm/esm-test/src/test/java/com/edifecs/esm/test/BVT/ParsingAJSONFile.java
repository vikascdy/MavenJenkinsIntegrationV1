package com.edifecs.esm.test.BVT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParsingAJSONFile {

	String nameTenant;
	String countTenant;
	String descriptionTenant;
	String domainTenant;
	
	String nameRole;
	String countRole;
	String descriptionRole;
	
	String nameGroup;
	String countGroup;
	String descriptionGroup;
	
	String nameOrganization;
	String countOrganization;
	String descriptionOrganization;
	
	String nameUser;
	String countUser;
	String descriptionUser;
	
	@Test
	public void print(){
		
		System.out.println(new java.io.File("testng-bvt.xml").getAbsolutePath());
		System.out.println(new java.io.File("testng-bvt.xml"));
		System.out.println(ParsingAJSONFile.class.getClassLoader().getResource("").getPath());
	}
	
@Test
public void parseXML(){
	
	try{
		File file = new File("InputData.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		
		doc.getDocumentElement().normalize();
		
		System.out.println("root Element :"+doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("data");
		 
			
			Node dataNode = nList.item(0);
	
			System.out.println("Node name is:"+dataNode.getNodeName());
			Element dataElement = (Element) dataNode;
			
			NodeList test1List = dataElement.getElementsByTagName("test1");
			NodeList tenantList = dataElement.getElementsByTagName("tenant");
			NodeList test2List = dataElement.getElementsByTagName("test2");
			NodeList test3List = dataElement.getElementsByTagName("test3");
			
			Node tenantNode = tenantList.item(0);
			Element tenantElement = (Element) tenantNode;
			
			NodeList tenantName = tenantElement.getElementsByTagName("name"); 
			Node tenantNameNode =  tenantName.item(0);
			nameTenant = tenantNameNode.getTextContent();
			NodeList tenantDescription = tenantElement.getElementsByTagName("description");
			Node tenantDescriptionNode = tenantDescription.item(0);
			descriptionTenant= tenantDescriptionNode.getTextContent();
			NodeList tenantDomain = tenantElement.getElementsByTagName("domain");
			Node tenantDomainNode = tenantDomain.item(0);
			domainTenant = tenantDomainNode.getTextContent();
			NodeList tenantCount = tenantElement.getElementsByTagName("count");
			Node tenantCountNode = tenantCount.item(0);
			countTenant = tenantCountNode.getTextContent();
			
			System.out.println("values are : "+ nameTenant+" "+descriptionTenant+" "+domainTenant+" "+countTenant);
			
			NodeList tenantRole = tenantElement.getElementsByTagName("role");
			Node tenantRoleNode = tenantRole.item(0);
			Element tenantRoleElement = (Element) tenantRoleNode; 
			NodeList roleList = tenantRoleElement.getElementsByTagName(""); 
			Node roleListNode = roleList.item(0);
			Element roleElement = (Element) roleListNode;
			
			NodeList roleNameList = roleElement.getElementsByTagName("name");
			Node roleName = roleNameList.item(0);
			nameRole = roleName.getTextContent();
			
			NodeList roleDescriptionList = roleElement.getElementsByTagName("description");
			Node roleDescription = roleDescriptionList.item(0);
			descriptionRole = roleDescription.getTextContent();
			
			NodeList roleCountList = roleElement.getElementsByTagName("count");
			Node roleCount = roleCountList.item(0);
			countRole = roleCount.getTextContent();
			
			System.out.println("role info is :"+nameRole+" "+descriptionRole+" "+countRole);
			
			NodeList tenantGroup = tenantElement.getElementsByTagName("group");
			Node tenantGroupNode = tenantGroup.item(0);
			NodeList groupList = tenantGroupNode.getChildNodes();
			
			System.out.println("");
			
			Node groupName = groupList.item(0) ;
			nameGroup = groupName.getTextContent();
			Node groupDescription = groupList.item(1);
			descriptionGroup = groupDescription.getTextContent();
			Node groupCount = groupList.item(2);
			countGroup = groupCount.getTextContent();
			
			System.out.println("group info is :"+nameGroup+" "+descriptionGroup+" "+countGroup);
			
			NodeList tenantOrganization = tenantElement.getElementsByTagName("organization");
			Node tenantOrganizationNode = tenantOrganization.item(0);
			NodeList organizationList = tenantOrganizationNode.getChildNodes();
			
			Node organizationName = organizationList.item(0) ;
			nameOrganization = organizationName.getTextContent();
			Node organizationDescription = organizationList.item(1);
			descriptionOrganization = organizationDescription.getTextContent();
			Node organizationCount = organizationList.item(2);
			countOrganization = organizationCount.getTextContent();
			
			System.out.println("organization info :" +nameOrganization+" "+descriptionOrganization+" "+countOrganization);
			
			NodeList tenantUser = tenantElement.getElementsByTagName("user");
			Node tenantUserNode = tenantUser.item(0);
			NodeList userList = tenantUserNode.getChildNodes();
			
			Node userName = userList.item(0);
			nameUser = userName.getTextContent();
			Node userDescription = userList.item(1);
			descriptionUser = userDescription.getTextContent();
			Node userCount = userList.item(2);
			countUser = userCount.getTextContent();
			
			System.out.println("user info :"+ nameUser+" "+descriptionUser+" "+countUser );
			
		//	Node test1 = dataList.item(0);
			/*System.out.println("please get test 1 name : "+" name : "+ test1.getNodeName() +" "+ test1.getTextContent());
			
 			System.out.println("test3 " + element.getElementsByTagName("test3"));
			System.out.println("test4 " + element.getElementsByTagName("test4"));
			
			System.out.println("tenant name is "+element.getElementsByTagName("tenant"));
			System.out.println("organization name is "+element.getElementsByTagName("organization"));
			System.out.println("role name is "+ element.getElementsByTagName("role"));
			System.out.println("group name is "+element.getElementsByTagName("group"));
			System.out.println("user name is "+element.getElementsByTagName("user"));
			*/
		
		
	}catch(Exception e){
		System.out.println("");
		e.printStackTrace();
	}
}
	
}
