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

import lejos.utility.Delay;



public class RobotSocket extends Thread {
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private ServerSocket ss;
	private PrintWriter out=null;
	private String inMessage="";
	private boolean isMessage=false;
	private String outMessage="";
	

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
		return inMessage;
		
	}
	
	public  synchronized String getMessage(boolean immediateReturn)
	{
		if (immediateReturn)
				return getMessageNonBlocking();
		else
		{
			try {
				return getMessage();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		}
			
	}
	
	public  synchronized void sendMessage(String outmessage)
	{
		out.println(outmessage);
	}
	
	public  synchronized void sendMessage(String outmessage, boolean noError)
	{
		if (noError)
			outMessage=outmessage;
		else
			sendMessage(outmessage);
	}
	
	
	
	private  synchronized String getMessageNonBlocking()
	{
		if (isMessage)
		{
			 return inMessage;
		}
		else 
			return null;
		
	}
	
	public synchronized void setMessageReceived()
	{
		isMessage=false;
	}
	
		
	private synchronized void setInMessage(String inmessage) {
		inMessage = inmessage;
		isMessage=true;
		notify();
	}
	
	
	
	
	private synchronized String getOutMessage() {
		String copyOutMessage = outMessage;
		outMessage="";
		return copyOutMessage;
	}
	
	
	
	
	
	

	public  void  run() 
	{     
		String localOutMessage="";
		String localInMessage="";
    		
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
	        		localInMessage  = in.readLine();
	            	
	            	
	            	if (localInMessage==null)
	            	{
	            		
	            		System.out.println("Null received");
	            		socket.close();
	        			ss.close();
	            		break;
	            	}else {
	            	
	            		setInMessage(localInMessage);
	            		
	            	}
	            	
	            	localOutMessage=getOutMessage();
	            	if (localOutMessage!="")
	            	{
	            		out.println(localOutMessage);
	            		
	            	}
	            		
	            	
	            		            	
	            	
	            	
	            	
	            	
	            	Delay.msDelay(100);
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
