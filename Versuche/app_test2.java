package package1;
import lejos.utility.Delay;
import package1.RobotSocket;

public class app_test2 {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Start of the program!!!");
		
		//Initializieren von dem Kommunikationsobjekt
		RobotSocket rs = new RobotSocket(8888);
		rs.setDaemon(true);
		rs.start();
		
		
		Delay.msDelay(1000);
		String InMessage="";
		
		
		while(true)
		{
			
			//Nachricht Empfangen
			//Wenn null zurückgeliefert ist - keine Nachricht vorhanden
			InMessage=rs.getMessage(true);
			
			//Überprüfen ob neue Nachricht da war
			if (InMessage!=null)
			{
				System.out.println("Message received: " + InMessage);
				
				//Vergleichen welche Nachricht gekommen war
				if(InMessage.contains("ja")||InMessage.contains("nein")) {
					Delay.msDelay(5000);
					System.out.println("Send: ja");
					
					//Nachricht zurück Senden
					rs.sendMessage("ja",true);
			}
				
			}
			Delay.msDelay(100);
			
			
		}
		
	     
	}

}