package package1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.utility.Delay;



public class RobotSocket extends Thread {
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private ServerSocket ss;
	public PrintWriter out=null;
	 public String inMessage="";
	 public boolean isMessage=false;
	 public String outMessage="";
	

	RobotSocket(int port)
	{
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public  synchronized String getMessage() throws InterruptedException
	{
		while (!isMessage)
			wait();
		isMessage = false;
		return inMessage;
		
	}
	
	public  synchronized String getMessageNonBlocking() throws InterruptedException
	{
		if (isMessage)
		{
			isMessage = false;
			 return inMessage;
			
		}
		else 
			return null;
		
	}
	
	private synchronized void setIsMessage() {
		isMessage=true;
		notify();
	}
	
	
	public  synchronized void sendMessage(String outmessage)
	{
		out.println(outmessage);
	}
	
	
	
	

	public  void  run() 
	{     
  //  		SocketConnector connector = new SocketConnector();
    		
    		try {
    		
    			
    			ss.getInetAddress();
				System.out.println("Wait for Connection: " + InetAddress.getLocalHost().getHostAddress());
    			socket = ss.accept();
    			
    			is = socket.getInputStream();
    			os = socket.getOutputStream();
    			System.out.println("Conected!!!");
    			
    			out = new PrintWriter(os, true);
    			BufferedReader in = new BufferedReader(new InputStreamReader(is));
    			
	    		
	    		
	    		while (true)
	    		{
	    			
	        	
	        	
	        		System.out.println("Wait for Message...");
	        		inMessage  = in.readLine();
	            	
	            	
	            	if (inMessage==null)
	            	{
	            		
	            		System.out.println("Null received");
	            		socket.close();
	        			ss.close();
	            		break;
	            	}else {
	            	
	            		setIsMessage();
	            		
	            	}
	            	
	            	/*if (RobotSocket.outMessage!="")
	            	{
	            		out.println(RobotSocket.outMessage);
	            		RobotSocket.outMessage="";
	            	}*/
	            		
	            	
	            		            	
	            	
	            	
	            	
	            	
	            	Delay.msDelay(100);
	    		}
	    		
	    		
	    	} 
        	catch (IOException e) 
        	{
        		//e.printStackTrace(System.out);
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
    		
    		//System.out.println("End of RobotSocket Thread!");
    		
	}
}