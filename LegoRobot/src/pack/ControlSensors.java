package pack;

import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public class ControlSensors extends Thread{
	private int MaxEntf = 255;
	public String KindPosition = "kein";
	public boolean KindIstDa = false;
	public boolean KindGeklatscht=false;
	public int lautstaerke = 0;
	
	   private IRSensor LeftSensor;
	   private IRSensor MiddleSensor;
	   private IRSensor RightSensor;
	   
	   private SoundSensor soundSensor;
	ControlSensors()
    {
		LeftSensor = new IRSensor(SensorPort.S3);
		MiddleSensor = new IRSensor(SensorPort.S2);
        RightSensor = new IRSensor(SensorPort.S1);
        
        soundSensor = new SoundSensor(SensorPort.S4);
    }
    
    public void run()
    {
        while (true)
        {
        	
        	int distanceLeft = LeftSensor.getDistance();
			int distanceMiddle = MiddleSensor.getDistance();
	        int distanceRight = RightSensor.getDistance();
	        
	        int Lautstaerke = soundSensor.getDB(); 
	        
	        if(distanceLeft < MaxEntf || distanceMiddle < MaxEntf || distanceRight < MaxEntf)
	        {
	        if(distanceLeft < distanceMiddle)
	        {
	        	if(distanceLeft < distanceRight)
	        	{
	        		KindPosition = "links";
	            	KindIstDa = true;
	        	}
	        	else
	        	{
	        		KindPosition = "rechts";
	            	KindIstDa = true;
	        	}
	        }
	        else
	        {
	        	if(distanceMiddle < distanceRight)
	        	{ 
	        		KindPosition = "mitte";
	            	KindIstDa = true;
	        	}
	        	else
	        	{
	        		KindPosition = "rechts";
	            	KindIstDa = true;
	        	}
	        }
			
	        }
	        else
	        {
	        	KindPosition = "kein";
	        	KindIstDa = false;
	        }
	        
	        lautstaerke = Lautstaerke;
	        
	        if(Lautstaerke >=20){
	        	KindGeklatscht=true;
	        }else{
	        	KindGeklatscht=false;
	        }
	        
	        Delay.msDelay(20);
			}

            
        }
        
}


