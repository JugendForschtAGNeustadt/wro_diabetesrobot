package pack;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MaximumFilter;

public class SoundSensor {
		NXTSoundSensor ir;
		SampleProvider sp;
		float [] sample;
		MaximumFilter sf;

	    SoundSensor(Port port)
	    {
	    	ir = new NXTSoundSensor(port);
	    	//sp = ir.getDBMode();
	    	sp = ir.getDBAMode();
	    	
	    	sf = new MaximumFilter(sp,50);
	    	sample = new float[sp.sampleSize()];
	    	
	    	
	    }
	    
	    public int getDB()
	    {	
	        sf.fetchSample(sample, 0);
	        
	        return (int)(sample[0]*100);
	    }
	
}
