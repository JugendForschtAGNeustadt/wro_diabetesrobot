package pack;

import pack.HaendeMotors;
import java.io.IOException;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class Haende_bewegung {

	static RemoteRequestEV3 EV3Haende;
	
	static RegulatedMotor linkesArm;
	static RegulatedMotor rechtesArm;
	static RegulatedMotor linkeHand;
	static RegulatedMotor rechteHand;
	
	public static void main(String[] args) throws IOException {
		
		EV3Haende = new RemoteRequestEV3("192.168.188.210");
		HaendeMotors.Init(EV3Haende);
		
		//Version mit dem Thread
		HaendeMotors.StartFreude();
		Delay.msDelay(20000);
		HaendeMotors.Stop();
		Delay.msDelay(5000);
		
		
		//ArmeInit();
		//ArmeNachOben();
		
		
	}
	
	public static void ArmeInit(){
		linkesArm = EV3Haende.createRegulatedMotor("B", 'L');
		rechtesArm = EV3Haende.createRegulatedMotor("C", 'L');
		linkeHand = EV3Haende.createRegulatedMotor("A", 'M');
		rechteHand = EV3Haende.createRegulatedMotor("D", 'M');
		
		linkesArm.resetTachoCount();
		rechtesArm.resetTachoCount();
		linkeHand.resetTachoCount();
		rechteHand.resetTachoCount();
	}
	
	private static boolean Arme_IsMoving()
	{
	    return 	linkesArm.isMoving() || rechtesArm.isMoving() || linkeHand.isMoving() || rechteHand.isMoving();
	}
	
	private static void SetMaxSpeed(){
		linkesArm.setSpeed((int)linkesArm.getMaxSpeed());
		linkesArm.setAcceleration(360);
		rechtesArm.setSpeed((int)rechtesArm.getMaxSpeed());
		rechtesArm.setAcceleration(360);
		linkeHand.setSpeed((int)linkeHand.getMaxSpeed());
		linkeHand.setAcceleration(180);
		rechteHand.setSpeed((int)rechteHand.getMaxSpeed());
		rechteHand.setAcceleration(180);
	}
	
	public static void ArmeNachOben(){
		
		SetMaxSpeed();
		
		
		linkesArm.synchronizeWith(new RegulatedMotor[] { rechtesArm, linkeHand,  rechteHand});
		linkesArm.startSynchronization();
		linkesArm.rotate(-720,true);
		rechtesArm.rotate(-720,true);
		linkeHand.rotate(90,true);
		rechteHand.rotate(90,true);
		linkesArm.endSynchronization();
		
		while (Arme_IsMoving());
		
		linkesArm.synchronizeWith(new RegulatedMotor[] { rechtesArm, linkeHand,  rechteHand});
		linkesArm.startSynchronization();
		linkesArm.rotateTo(0,true);
		rechtesArm.rotateTo(0,true);
		linkeHand.rotateTo(0,true);
		rechteHand.rotateTo(0,true);
		linkesArm.endSynchronization();
		
		while (Arme_IsMoving());
		
		
		
	}

}
