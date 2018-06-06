package pack;

import pack.HaendeMotors;
import pack.HaendeMotors.HaendeStatus;

import java.io.IOException;

import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

enum RobotStatus {FRAGE, ANTWORT, TRAURIG, FROHELIG}


public class Hauptprogramm {
	
	static RemoteRequestEV3 EV3Haende;
	


	static ControlSensors sensors;
	static HaendeMotors haendeThread;
	static Fahren fahren;
	static RobotSocket ComControl;
	static RobotSocket ComAugen;
    static boolean FrageAntwort;
    static RobotStatus JetzigerStatus=RobotStatus.FRAGE;
   
	
	public static void main(String[] args) throws IOException {

	 fahren = new Fahren();
     sensors = new ControlSensors();
     sensors.setDaemon(true);
     sensors.start();
 	 EV3Haende = new RemoteRequestEV3(MyFindEV3IP("HAENDE2"));
 	 haendeThread = new HaendeMotors(EV3Haende);
 	 haendeThread.setDaemon(true);
 	 haendeThread.start();
 	 ComControl = new RobotSocket(8888);
 	 ComAugen = new RobotSocket(8889);
 	 
 	 ComControl.setDaemon(true);
 	 ComControl.start();
 	 
 	 ComAugen.setDaemon(true);
 	 ComAugen.start();
 	 
 	 
 	 

     Behavior b1 = new Frage();
     Behavior b2 = new Antwort();
     Behavior b3 = new Traurig();
     Behavior b4 = new Froehlich();
     Behavior[] behaviorList =
     {
       b1, b2, b3, b4
     };
     Arbitrator arbitrator = new Arbitrator(behaviorList);
     LCD.drawString("Sugarman",0,1);
     Button.LEDPattern(6);
     Button.waitForAnyPress();
     Button.LEDPattern(0);
     LCD.clear();
     arbitrator.go();
     
	}
	
	 static String MyFindEV3IP(String EV3Name){
		    BrickInfo[] bricks = BrickFinder.discover();
			for (BrickInfo brick: bricks)
			{
				System.out.println("EV3 Brick: " + brick.getIPAddress() + " " + brick.getName() + " " + brick.getType());
				if (brick.getName().contains(EV3Name))
					return brick.getIPAddress();
				
			}
			 
			 return "";
	}


}

class Frage implements Behavior {
	 private boolean _suppressed = false;

	  public boolean takeControl()
	  {
		
		
	    return true;  // this behavior always wants control.
	  }

	  public void suppress()
	  {
	    _suppressed = true;// standard practice for suppress methods
	  }

	  public void action()
	  {
		_suppressed = false;
		
		//System.out.println("***************************************  Frage.action():START");
		Hauptprogramm.JetzigerStatus=RobotStatus.FRAGE;
	      
	    }



}	


class Antwort implements Behavior {
	 private boolean _suppressed = false;

	  public boolean takeControl()
	  {
		  
		 
			  
		  String sAppMessage = Hauptprogramm.ComControl.getMessage();
			  
			  if(Hauptprogramm.JetzigerStatus==RobotStatus.FRAGE &&
					  (sAppMessage.contains("ja")
					 ||sAppMessage.contains("nein"))
				)
			  {
				  if(sAppMessage.contains("ja"))
				  {
					  Hauptprogramm.FrageAntwort = true;
					  System.out.println("FrageAntwort = true");
				  }
				  else
				  {
					  Hauptprogramm.FrageAntwort = false; 
					  System.out.println("FrageAntwort = false");
				  }
				  System.out.println("Antwort.takeControl=true");
				 
				  
				  
				  return true;
			  }
			  else
			  {
				  return false;
			  }
		  
	  }

	  public void suppress()
	  {
	    _suppressed = true;// standard practice for suppress methods
	  }

	  public void action()
	  {
	    _suppressed = false;
	    System.out.println("***************************************  Antwort.action():START");
	    Hauptprogramm.JetzigerStatus=RobotStatus.ANTWORT;
	    Hauptprogramm.ComControl.setMessageReceived();
	   
	    int zaehler=0;
	    
	    while (!_suppressed)
	    {
	    zaehler++;	    	
	    if (zaehler==20)
	    {
	    	Hauptprogramm.fahren.rotate(5);
	    	Hauptprogramm.fahren.rotate(-5);
	    	zaehler=0;
	    }
	    
	    LCD.clear();
	    LCD.drawString(Hauptprogramm.sensors.KindPosition, 0, 2);
	    if (Hauptprogramm.sensors.KindIstDa)
	    	LCD.drawString("Kind ist da", 0, 3);
	    else
	    	LCD.drawString("Kind ist nicht da", 0, 3);
	    LCD.drawString("Lautstaerke: " + Hauptprogramm.sensors.lautstaerke, 0, 4);
	    Delay.msDelay(100);	
	    }
	      Thread.yield(); 
	      
	      System.out.println("Antwort.action():ENDE");
		}
		



}	









class Traurig implements Behavior {

	
	  public boolean takeControl()
	  {
        if(Hauptprogramm.JetzigerStatus==RobotStatus.ANTWORT &&
           (Hauptprogramm.sensors.KindPosition == "links" 
        		&& Hauptprogramm.sensors.KindGeklatscht && Hauptprogramm.FrageAntwort 
         ||Hauptprogramm.sensors.KindPosition == "rechts" 
        		&& Hauptprogramm.sensors.KindGeklatscht && !Hauptprogramm.FrageAntwort)
           
           || Hauptprogramm.JetzigerStatus==RobotStatus.TRAURIG)
        {
        	
        	Hauptprogramm.JetzigerStatus=RobotStatus.TRAURIG;
			System.out.println("Traurig.takeControl=true");
        	return true;
        }
        else
        {
        	return false;
        }
	  }

	  public void suppress()
	  {
	    
	  }

	  public void action()
	  {
		
		System.out.println("***************************************  Traurig.action(): START");
		Hauptprogramm.JetzigerStatus=RobotStatus.TRAURIG;
		System.out.println("JetzigerStatus=RobotStatus.TRAURIG");
	    
	    
	    if(Hauptprogramm.FrageAntwort)
	    {
	    	 Hauptprogramm.ComControl.sendMessage("NEIN"); 	
	    }
	    else
	    {
	    	 Hauptprogramm.ComControl.sendMessage("JA");
	    }
	    Hauptprogramm.ComAugen.sendMessage("traurig");
	    
	 
	    
	   Hauptprogramm.haendeThread.StartMove(HaendeStatus.TRAUER);
	    
	   Hauptprogramm.fahren.backward(400);
	   Delay.msDelay(17000);
	   Hauptprogramm.fahren.forward(400);
	   
	   Hauptprogramm.haendeThread.StopMove();

	   System.out.println("Traurig.action(): ENDE");
	   
	   Hauptprogramm.JetzigerStatus=RobotStatus.FRAGE;

	    }

	
	
	
}

class Froehlich implements Behavior {

	

	  public boolean takeControl()
	  {
	    if(Hauptprogramm.JetzigerStatus==RobotStatus.ANTWORT &&
	      (Hauptprogramm.sensors.KindPosition == "links" 
	      		&& Hauptprogramm.sensors.KindGeklatscht && !Hauptprogramm.FrageAntwort 
	     ||Hauptprogramm.sensors.KindPosition == "rechts" 
	     		&& Hauptprogramm.sensors.KindGeklatscht && Hauptprogramm.FrageAntwort)
	      
	    		|| Hauptprogramm.JetzigerStatus==RobotStatus.FROHELIG)
	    {
			System.out.println("Froehlich.takeControl=true");
			Hauptprogramm.JetzigerStatus=RobotStatus.FROHELIG;
	    	return true;
	    }
	    else
	    {
	    	return false;
	    }
	  }

	  public void suppress()
	  {
	    
	  }

	  public void action()
	  {
		
		System.out.println("***************************************  Froehlich.action(): START");
		Hauptprogramm.JetzigerStatus=RobotStatus.FROHELIG;
		System.out.println("JetzigerStatus=RobotStatus.FROHELIG");

	   
	    
	    if(!Hauptprogramm.FrageAntwort)
	    {
	    	 Hauptprogramm.ComControl.sendMessage("NEIN"); 	
	    }
	    else
	    {
	    	 Hauptprogramm.ComControl.sendMessage("JA");
	    }
	    Hauptprogramm.ComAugen.sendMessage("freude");
	    
		   Hauptprogramm.fahren.backward(200);
		   Delay.msDelay(1500);
	    Hauptprogramm.haendeThread.StartMove(HaendeStatus.FREUDE);
	    Hauptprogramm.fahren.rotate(360);
	    Hauptprogramm.haendeThread.StopMove();
		   Hauptprogramm.fahren.forward(200);
		   System.out.println("Froehlich.action(): ENDE");
		   
		   Hauptprogramm.JetzigerStatus=RobotStatus.FRAGE;
	    }

	
	
	
}
