package pack;

import pack.HaendeMotors;
import pack.HaendeMotors.HaendeStatus;

import java.io.IOException;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;


public class Hauptprogramm {
	
	static RemoteRequestEV3 EV3Haende;
	


	static ControlSensors sensors;
	static HaendeMotors haendeThread;
	static Fahren fahren;
	
	public static void main(String[] args) throws IOException {

	 fahren = new Fahren();
     sensors = new ControlSensors();
     sensors.setDaemon(true);
     sensors.start();
 	 EV3Haende = new RemoteRequestEV3("192.168.188.210");
 	 haendeThread = new HaendeMotors(EV3Haende);
 	 haendeThread.setDaemon(true);
 	 haendeThread.start();
	
     Behavior b1 = new Antwort();
     Behavior b2 = new Traurig();
     Behavior b3 = new Froehlich();
     Behavior[] behaviorList =
     {
       b1, b2, b3
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
	    return true;  // this behavior always wants control.
	  }

	  public void suppress()
	  {
	    _suppressed = true;// standard practice for suppress methods
	  }

	  public void action()
	  {
	    _suppressed = false;
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

class Traurig implements Behavior {

	 private boolean _suppressed = false;

	  public boolean takeControl()
	  {
        if(Hauptprogramm.sensors.KindPosition == "links" && Hauptprogramm.sensors.KindGeklatscht)
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
	    if(Hauptprogramm.sensors.KindPosition == "rechts" && Hauptprogramm.sensors.KindGeklatscht)
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
        
	    Hauptprogramm.haendeThread.StartMove(HaendeStatus.FREUDE);
	    Hauptprogramm.fahren.rotate(360);
	    Hauptprogramm.haendeThread.StopMove();
	    }

	
	
	
}
