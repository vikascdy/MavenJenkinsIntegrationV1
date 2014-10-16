package com.edifecs.esm.test.configureLDAP;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;


public class ReleaseUnusedPort 
{
	static int MIN_PORT_ADDRESS=0; 
	static int MAX_PORT_ADDRESS=111000;
	
	public static boolean available(int port) 
	{
	    if (port < MIN_PORT_ADDRESS || port > MAX_PORT_ADDRESS) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) 
	    {
	    	return false;
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	  //  return false;
	}
	public static int unusedport(int port)
	{
		Boolean verify = false;
		while(verify==false)
		{
			
		verify=available(port);
		port=port+1;
		
		}
		return port;
		
	}

}
