package pack;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;



public class HaendeMotors {
	static HaendeMotorsThread motorsThread;
	static RemoteRequestEV3 remoteEV3;
	static boolean bIsMoving=false;
	
	public enum HaendeStatus {
		FREUDE, TRAUER, WARTEN, STOP
	}
	
	static HaendeStatus Status = HaendeStatus.STOP;
	
	public static void Init(RemoteRequestEV3 EV3){
		remoteEV3 = EV3;
		motorsThread = new HaendeMotorsThread();
		motorsThread.setDaemon(true);
		motorsThread.start();
		bIsMoving=false;
	}
	
public static void StartFreude(){
		Status=HaendeStatus.FREUDE;
	}
public static void StartTrauer(){
	Status=HaendeStatus.TRAUER;
}

public static void StartWarten(){
	Status=HaendeStatus.WARTEN;
}
	
public static void Stop(){
	Status = HaendeStatus.STOP;
	}

public static boolean IsMoving(){
	return bIsMoving;
}
	
}

class HaendeMotorsThread extends Thread {
	private RegulatedMotor linkesArm;
	private RegulatedMotor rechtesArm;
	private RegulatedMotor linkeHand;
	private RegulatedMotor rechteHand;
	
	private enum FREUDEStatus {
		START, OBEN, UNTEN, STOP
	}
	private FREUDEStatus FreudeStatus;
	
	
	HaendeMotorsThread(){
		FreudeStatus=FREUDEStatus.STOP;
		
		ArmeInit();
		
	}
	
	private void ArmeInit(){
		linkesArm = HaendeMotors.remoteEV3.createRegulatedMotor("B", 'L');
		rechtesArm = HaendeMotors.remoteEV3.createRegulatedMotor("C", 'L');
		linkeHand = HaendeMotors.remoteEV3.createRegulatedMotor("A", 'M');
		rechteHand = HaendeMotors.remoteEV3.createRegulatedMotor("D", 'M');
		
		linkesArm.resetTachoCount();
		rechtesArm.resetTachoCount();
		linkeHand.resetTachoCount();
		rechteHand.resetTachoCount();
	}
	
	private void SetMaxSpeed(){
		linkesArm.setSpeed((int)linkesArm.getMaxSpeed());
		linkesArm.setAcceleration(360);
		rechtesArm.setSpeed((int)rechtesArm.getMaxSpeed());
		rechtesArm.setAcceleration(360);
		linkeHand.setSpeed((int)linkeHand.getMaxSpeed());
		linkeHand.setAcceleration(180);
		rechteHand.setSpeed((int)rechteHand.getMaxSpeed());
		rechteHand.setAcceleration(180);
	}
	
	private void ArmeNachOben(){
		SetMaxSpeed();
		linkesArm.synchronizeWith(new RegulatedMotor[] { rechtesArm, linkeHand,  rechteHand});
		linkesArm.startSynchronization();
		
		linkesArm.rotate(-720,true);
		rechtesArm.rotate(-720,true);
		linkeHand.rotate(90,true);
		rechteHand.rotate(90,true);
		
		linkesArm.endSynchronization();
		
		
	}
	
	private void ArmeNachUnten(){
		SetMaxSpeed();
		linkesArm.synchronizeWith(new RegulatedMotor[] { rechtesArm, linkeHand,  rechteHand});
		linkesArm.startSynchronization();
		linkesArm.rotateTo(0,true);
		rechtesArm.rotateTo(0,true);
		linkeHand.rotateTo(0,true);
		rechteHand.rotateTo(0,true);
		linkesArm.endSynchronization();
	}
	
	private boolean Arme_IsMoving()
	{
	    return 	linkesArm.isMoving() || rechtesArm.isMoving() || linkeHand.isMoving() || rechteHand.isMoving();
	}
	
	
	
	public void run(){
		
		 while (true)
	        {
			 HaendeMotors.bIsMoving=Arme_IsMoving();
			 
			switch (FreudeStatus){
				 case STOP:
					 if (HaendeMotors.Status == HaendeMotors.HaendeStatus.FREUDE)
						 FreudeStatus=FREUDEStatus.START;
					 break;
				 case START: 
					 ArmeNachOben();
					 FreudeStatus=FREUDEStatus.OBEN;
				   break;
				 
				 case OBEN:
					 if (!Arme_IsMoving())
					 {
						 ArmeNachUnten();
						 FreudeStatus=FREUDEStatus.UNTEN; 
					 }
					break;
				 case UNTEN:
					 if (!Arme_IsMoving())
					 {
						 FreudeStatus=FREUDEStatus.STOP; 
					 }
					   break;
				 }
				
			 
			 Delay.msDelay(10);
	        }
		
	}

}
