package pack;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MaximumFilter;
import lejos.robotics.filter.MinimumFilter;


public class IRSensor {
	EV3IRSensor ir;
	SampleProvider sp;
	float [] sample;
	MinimumFilter sf;

    IRSensor(Port port)
    {
    	ir = new EV3IRSensor(port);
    	sp = ir.getDistanceMode();
    	sf = new MinimumFilter (new MaximumFilter(sp,3),5);
    	sample = new float[sp.sampleSize()];
    	
    	
    }
    
    public int getDistance()
    {	
        sf.fetchSample(sample, 0);
        return (int)sample[0];
    }
}
