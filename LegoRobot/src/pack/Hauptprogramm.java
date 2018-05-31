package pack;

import pack.HaendeMotors;
import pack.HaendeMotors.HaendeStatus;

import java.io.IOException;

import lejos.hardware.Audio;
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
    static String appmessage=null;
    static boolean FrageAntwort;
    static RobotStatus JetzigerStatus=RobotStatus.FRAGE;
   
	
	public static void main(String[] args) throws IOException {

	 fahren = new Fahren();
     sensors = new ControlSensors();
     sensors.setDaemon(true);
     sensors.start();
 	 EV3Haende = new RemoteRequestEV3("192.168.188.210");
 	 haendeThread = new HaendeMotors(EV3Haende);
 	 haendeThread.setDaemon(true);
 	 haendeThread.start();
 	 ComControl = new RobotSocket(8888);
 	 //ComAugen = new RobotSocket(8889);
 	 
 	 ComControl.setDaemon(true);
 	 ComControl.start();
 	 
 	 //ComAugen.setDaemon(true);
 	 //ComAugen.start();
 	 
 	 
 	 

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

}


class Antwort implements Behavior {
	 private boolean _suppressed = false;

	  public boolean takeControl()
	  {
		  if (Hauptprogramm.appmessage!=null)
			  if(Hauptprogramm.JetzigerStatus==RobotStatus.FRAGE &&
					  (Hauptprogramm.appmessage.contains("ja")
					 ||Hauptprogramm.appmessage.contains("nein"))
				)
			  {
				  if(Hauptprogramm.appmessage.contains("ja"))
				  {
					  Hauptprogramm.FrageAntwort = true;
				  }
				  else
				  {
					  Hauptprogramm.FrageAntwort = false;  
				  }
				  Hauptprogramm.ComControl.setMessageReceived();
				  return true;
			  }
			  else
			  {
				  return false;
			  }
		  else
			  return false;
		  
	  }

	  public void suppress()
	  {
	    _suppressed = true;// standard practice for suppress methods
	  }

	  public void action()
	  {
	    _suppressed = false;
	    Hauptprogramm.JetzigerStatus=RobotStatus.ANTWORT;
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
	      Thread.yield(); //don't exit till suppressed
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
		Hauptprogramm.JetzigerStatus=RobotStatus.FRAGE;
		Hauptprogramm.appmessage=null;
	    
	    while (!_suppressed)
	    {
	    	Hauptprogramm.appmessage = Hauptprogramm.ComControl.getMessage();
	    }
	    
	      
	    }



}	




class Traurig implements Behavior {

	 private boolean _suppressed = false;

	  public boolean takeControl()
	  {
        if(Hauptprogramm.JetzigerStatus==RobotStatus.ANTWORT &&
           (Hauptprogramm.sensors.KindPosition == "links" 
        		&& Hauptprogramm.sensors.KindGeklatscht && Hauptprogramm.FrageAntwort 
         ||Hauptprogramm.sensors.KindPosition == "rechts" 
        		&& Hauptprogramm.sensors.KindGeklatscht && !Hauptprogramm.FrageAntwort))
        {
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
	    if(Hauptprogramm.FrageAntwort)
	    {
	    	 Hauptprogramm.ComControl.sendMessage("NEIN"); 	
	    }
	    else
	    {
	    	 Hauptprogramm.ComControl.sendMessage("JA");
	    }
	   
	    
	    Hauptprogramm.JetzigerStatus=RobotStatus.TRAURIG;
	    
	   Hauptprogramm.haendeThread.StartMove(HaendeStatus.TRAUER);
	    
	   Hauptprogramm.fahren.backward(400);
	   Delay.msDelay(17000);
	   Hauptprogramm.fahren.forward(400);
	   
	   Hauptprogramm.haendeThread.StopMove();

	    }

	
	
	
}

class Froehlich implements Behavior {

	 private boolean _suppressed = false;

	  public boolean takeControl()
	  {
	    if(Hauptprogramm.JetzigerStatus==RobotStatus.ANTWORT &&
	      (Hauptprogramm.sensors.KindPosition == "links" 
	      		&& Hauptprogramm.sensors.KindGeklatscht && !Hauptprogramm.FrageAntwort 
	     ||Hauptprogramm.sensors.KindPosition == "rechts" 
	     		&& Hauptprogramm.sensors.KindGeklatscht && Hauptprogramm.FrageAntwort))
	    {
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
	    if(!Hauptprogramm.FrageAntwort)
	    {
	    	 Hauptprogramm.ComControl.sendMessage("NEIN"); 	
	    }
	    else
	    {
	    	 Hauptprogramm.ComControl.sendMessage("JA");
	    }
	    
	    Hauptprogramm.JetzigerStatus=RobotStatus.FROHELIG;
		   Hauptprogramm.fahren.backward(200);
		   Delay.msDelay(3000);
	    Hauptprogramm.haendeThread.StartMove(HaendeStatus.FREUDE);
	    Hauptprogramm.fahren.rotate(360);
	    Hauptprogramm.haendeThread.StopMove();
		   Hauptprogramm.fahren.forward(200);
	    }

	
	
	
}
