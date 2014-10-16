// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

package com.edifecs.servicemanager.test.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Application {
 /*	 
  * For mysql database
  *	private static final String dbUrl = "jdbc:mysql://localhost:3306/test";	
  *
  * For SQLServer database
  * private static final String dbUrl = "jdbc:sqlserver://localhost:1433;"+
			 "databaseName=Test;user=sahil;password=Edifecs@work12";
  */
 
	
	private static final String dbUrl = "jdbc:mysql://localhost:3306/test";
	static public final String user = "root";
	static public final String password = "root";
 
    public Application() {
    }
 
    
    public static Map<String,Map<Integer,String>> execute(String command)
    {
        Statement stmt = null;
        Map<String,Map<Integer,String>> map=new HashMap<String,Map<Integer,String>>();
    	Map<Integer,String> m=new HashMap<Integer,String>();
    	
       String driverName = "com.mysql.jdbc.Driver";
        //String driverName ="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        Connection con = null;
        try {
            Class.forName(driverName);
            con = DriverManager.getConnection(dbUrl);
            stmt = con.createStatement();
            if(command.split(" ")[0].toUpperCase().equals("SELECT")){
        		ResultSet rs = stmt.executeQuery(command);
        		int column_count=rs.getMetaData().getColumnCount();
                while (rs.next()) {
                String ret="";
               	for(int i=1;i<=column_count;i++) {
                    ret=ret+"\t "+rs.getString(i);
                }
                m.put(rs.getRow(),ret);
                }
                map.put(command, m);
        	}
            else
            {
            	 int rows_affected = stmt.executeUpdate(command);
            	 m.put(rows_affected, "rows were effected by the command");
            	 map.put(command, m);
            }
            con.close();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
        	System.out.println("class not found");
            e.printStackTrace();
        } catch (SQLException e) {
        	System.out.println("SQL Exception error");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }
 
}