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
	private BufferedReader in=null;
	private String inMessage="";
	private boolean isMessage=false;
	private boolean isIOError=false;
	private int port;
	private boolean isConnected=false;
	

	RobotSocket(int inport) throws IOException
	{
		
		
		port=inport;
		
	}
	
	public  synchronized String getMessage()
	{
		if (isMessage)
		{
			System.out.println("***************************************  getMessage():"+inMessage);
			 return inMessage;
		}
		else
		{
			return "";

		} 
			
	}

	public synchronized void setMessageReceived()
	{
		System.out.println("setMessageReceived()");
		isMessage=false;
	}
	
	public  synchronized void sendMessage(String outmessage)
	{
		System.out.println("sendMessage():" + outmessage);
		try
		{
			if (isConnected)
			{
				out.println(outmessage);
				System.out.println("sendMessage() message sent: " + outmessage);
			}
			else
			  System.out.println("sendMessage():No connection");
				
		}
		catch (Exception e) 
    	{
    		e.printStackTrace(System.out);
    		setIOError();
    	}
		
		
	}

	public  synchronized String getMessageBlocking() throws InterruptedException 
	{
		while (!isMessage)
			wait();
		return inMessage;
		
	}
	
	
		
	private synchronized void setInMessage(String inmessage) {
		inMessage = inmessage;
		isMessage=true;
		notify();
	}
	
	private synchronized void setIOError(){
		isIOError=true;
	}
	
	private synchronized void resetIOError(){
		isIOError=false;
	}
	
	private synchronized boolean getIOError(){
		return isIOError;
	}
	
	private synchronized void setConnected(boolean bConnected){
		isConnected=bConnected;
	}
	
	
	
	
	
	

	public  void  run() 
	{     
		String localInMessage="";
		
		while(true)
		{
			try {
				ss = new ServerSocket(port);
    			ss.getInetAddress();
				System.out.println("Wait for Connection: " + InetAddress.getLocalHost().getHostAddress());
    			socket = ss.accept();
    			
    			is = socket.getInputStream();
    			os = socket.getOutputStream();
    			System.out.println("Conected!!!");
    			
    			out = new PrintWriter(os, true);
				in = new BufferedReader(new InputStreamReader(is));
				setConnected(true);
    			
				}
    			catch (IOException e) 
            	{
    				e.printStackTrace(System.out);
    				setIOError();
            	}
			
			while (!getIOError())
    		{
    			
				try{
					System.out.println("Wait for Message...");
	        		localInMessage  = in.readLine();
	            	
	            	
	            	if (localInMessage==null)
	            	{
	            		System.out.println("Null received");
	            		setIOError();
	            		break;
	            	}else
	            	{
	            		System.out.println("Received: " + localInMessage);
	            		setInMessage(localInMessage);
	            	}
	            	
					
				}catch (IOException e) 
            	{
    				e.printStackTrace(System.out);
    				setIOError();
            	}
        	            	
            	Delay.msDelay(100);
    		}
			
			if(getIOError())
			{
				try {
					setConnected(false);
					socket.close();
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				resetIOError();
			}
        		
			
			
			
			
		}
    		
    		
	}
}
