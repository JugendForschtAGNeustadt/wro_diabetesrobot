package package1;
import lejos.utility.Delay;
import package1.RobotSocket;

public class app_test {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Start of the program!!!");
		RobotSocket rs = new RobotSocket(8888);
		rs.setDaemon(true);
		rs.start();
		
		
		Delay.msDelay(1000);
		String InMessage="";
		
		/*while(true)
		{
			
			InMessage=rs.getMessage();
			System.out.println("Message received: " + InMessage);
			
			
				if(InMessage.contains("ja")||InMessage.contains("nein")) {
					Delay.msDelay(5000);
					System.out.println("Send: ja");
					rs.sendMessage("ja");
					
			}
			
		}*/
		
		while(true)
		{
			
			InMessage=rs.getMessageNonBlocking();
			if (InMessage!=null)
			{
				System.out.println("Message received: " + InMessage);
				
				
				if(InMessage.contains("ja")||InMessage.contains("nein")) {
					Delay.msDelay(5000);
					System.out.println("Send: ja");
					rs.sendMessage("ja");
			}
				
			}
			Delay.msDelay(100);
			
			
		}
		
	     
	}

}