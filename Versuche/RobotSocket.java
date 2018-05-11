package pack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//import lejos.remote.nxt.NXTConnection;
//import lejos.remote.nxt.SocketConnector;

public class RobotSocket extends Thread {
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private ServerSocket ss;
	

	RobotSocket()
	{
		
		
	}

	public void run() 
	{     
  //  		SocketConnector connector = new SocketConnector();
    		
    		try {
    			ss = new ServerSocket(8888);
    			
    			ss.getInetAddress();
				System.out.println("Wait for Connection: " + InetAddress.getLocalHost().getHostAddress());
    			socket = ss.accept();
    			
    			is = socket.getInputStream();
    			os = socket.getOutputStream();
    			System.out.println("Conected!!!");
    			
    			PrintWriter out =
    			        new PrintWriter(os, true);
    			BufferedReader in = new BufferedReader(new InputStreamReader(is));
    			
			
    		/*
	    		NXTConnection conn = connector.waitForConnection(0, NXTConnection.RAW);
	    		InputStream is = conn.openInputStream();
	    		BufferedReader br = new BufferedReader(new InputStreamReader(is), 1);
*/
	    		String inmessage = "";
	    		String outmessage = "";
	    		int i=0;
	    		while (true)
	    		{
	    			i++;
	        		inmessage = "";
	        	
	        		System.out.println("Wait for Message...");
	            	inmessage  = in.readLine();
	            	System.out.println("Message received: " + inmessage);
	            	
	            	if (inmessage!=null)
	            	{
	            		outmessage ="Freude! #" + i; 
		            	out.println(outmessage);	
		            	System.out.println("Message sent: " + outmessage);
	            	}
	            	else
	            	{
	            		System.out.println("Null received");
	            		socket.close();
	        			ss.close();
	            		break;
	            	}
	            		
	            	
	            		            	
	            	if (out.checkError())
	            	{
	            		System.out.println("Out Message error");
	            		socket.close();
	        			ss.close();
	            		break;
	            	}
	            	
	            	
	            	
	    
	    		}
	    		
	    		
	    	} 
        	catch (IOException e) 
        	{
        		e.printStackTrace(System.out);
        	}
    		finally
    		{
    			try {
    				socket.close();
					ss.close();
				} catch (IOException e) {
					e.printStackTrace(System.out);
				}
    		}
    		
    		System.out.println("End of RobotSocket Thread!");
    		
	}
}
