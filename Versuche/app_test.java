package pack;
import pack.RobotSocket;;

public class app_test {

	public static void main(String[] args) {
		System.out.println("Start of the program!!!");
		
		RobotSocket sr = new RobotSocket();
		//sr.setDaemon(true);
		//sr.start();
		sr.run();
		
		System.out.println("End of the program!!!");
	     
	}

}
