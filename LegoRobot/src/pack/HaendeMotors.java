package pack;

import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;



class HaendeMotors extends Thread {
	private RemoteRequestEV3 remoteEV3;

	private RegulatedMotor linkesArm;
	private RegulatedMotor rechtesArm;
	private RegulatedMotor linkeHand;
	private RegulatedMotor rechteHand;

	public enum HaendeStatus {
		FREUDE, TRAUER, WARTEN, STOP
	}

	private HaendeStatus Status = HaendeStatus.STOP;
	

	public synchronized void StartMove(HaendeStatus parStatus){
		Status=parStatus;
	}

	
	public synchronized void StopMove(){
		Status = HaendeStatus.STOP;
	}

	private synchronized HaendeStatus getStatus()
	{
		return Status;
	}

		
	
	HaendeMotors(RemoteRequestEV3 EV3){
		remoteEV3= EV3;
		
		ArmeInit();
		SetMaxSpeed();
		
	}
	
	private void ArmeInit(){
		linkesArm = remoteEV3.createRegulatedMotor("B", 'L');
		rechtesArm = remoteEV3.createRegulatedMotor("C", 'L');
		linkeHand = remoteEV3.createRegulatedMotor("A", 'M');
		rechteHand = remoteEV3.createRegulatedMotor("D", 'M');
		
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
		
		linkesArm.synchronizeWith(new RegulatedMotor[] { rechtesArm, linkeHand,  rechteHand});
		linkesArm.startSynchronization();
		
		linkesArm.rotate(-720,true);
		rechtesArm.rotate(-720,true);
		linkeHand.rotate(90,true);
		rechteHand.rotate(90,true);
		
		linkesArm.endSynchronization();
		
		
	}
	
	private void ArmeNachUnten(){
		
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
			 
			 
			 if(getStatus() == HaendeStatus.FREUDE)
			 {
				ArmeNachOben();
				while(Arme_IsMoving());
				ArmeNachUnten();
				while(Arme_IsMoving());
			 }
			
				
			if(getStatus() == HaendeStatus.TRAUER)
			{
				rechtesArm.rotate(-700);
				rechteHand.rotate(340);
				for(int i = 0; i <= 1; i++)
				{
					rechteHand.rotate(-100);
					rechteHand.rotate(100);
				}
				rechteHand.rotate(-320);
				rechtesArm.rotate(700);
			}
			 
			 Delay.msDelay(10);
	        }
		
	}

}
