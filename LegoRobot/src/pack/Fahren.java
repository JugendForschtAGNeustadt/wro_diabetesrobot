package pack;
import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.Pose;
import lejos.utility.Matrix;
import lejos.robotics.navigation.MovePilot;


public class Fahren {


  
  
  ArcRotateMoveController  pilot;
  PoseProvider poseProvider;
  double radius = 300;  
  double angle = 360;
  double distance = 1000;
  int time = 4000;
  int backward = 300;
  int forward = 300;
 


  
  
private void setDefaults() {
  pilot.setLinearSpeed(pilot.getMaxLinearSpeed()/2);
  pilot.setLinearAcceleration(pilot.getMaxLinearSpeed()/1);
  pilot.setAngularSpeed(pilot.getMaxAngularSpeed()/2);
  pilot.setAngularAcceleration(pilot.getMaxAngularSpeed()/1);
}
  
private void endMove() {
 
  poseProvider.setPose(new Pose(0,0,0)); 


}

public Fahren() {
    
          Chassis chassis;
          Wheel wheel1 = WheeledChassis.modelHolonomicWheel(Motor.A, 59).polarPosition(0, 165).gearRatio(1);
          Wheel wheel2 = WheeledChassis.modelHolonomicWheel(Motor.B, 59).polarPosition(90, 165).gearRatio(1);
          Wheel wheel3 = WheeledChassis.modelHolonomicWheel(Motor.C, 59).polarPosition(180, 165).gearRatio(1);
          Wheel wheel4 = WheeledChassis.modelHolonomicWheel(Motor.D, 59).polarPosition(270, 165).gearRatio(1);
          chassis = new WheeledChassis(new Wheel[]{wheel1, wheel2, wheel3, wheel4}, WheeledChassis.TYPE_HOLONOMIC);
          pilot = new MovePilot(chassis);
          poseProvider = chassis.getPoseProvider();
          radius = Math.max(radius, pilot.getMinRadius());
          poseProvider = new OdometryPoseProvider(pilot);
          setDefaults();
          endMove();
    }

protected void backward(int backward) 
{
	  pilot.setLinearSpeed(pilot.getMaxLinearSpeed()/4);
	  pilot.setAngularSpeed(pilot.getMaxAngularSpeed()/4);

	//pilot.backward();
	pilot.travel(-backward,true);
	
}

protected void forward(int forward)
{
	
	//pilot.forward();
	pilot.travel(forward,true);
	
	
	  pilot.setLinearSpeed(pilot.getMaxLinearSpeed()/2);
	  pilot.setAngularSpeed(pilot.getMaxAngularSpeed()/2);
}

public void rotate(int grad)
{
	pilot.rotate(grad);
	
}






}

