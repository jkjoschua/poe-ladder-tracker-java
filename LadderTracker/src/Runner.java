import java.net.ServerSocket;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * The Runner object is used to start the program.
 * 
 * @author Joschn
 */
public class Runner{
	private GUIStartup guiStartup;
	private ServerSocket ss;
	
	/**
	 * Constructor for the Runner object.
	 */
	public Runner(){}
	/**
	 * The main method.
	 * 
	 * @param args - Contains the supplied command-line arguments.
	 */
	public static void main(String [] args){
		try {
		    for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
		        if("Windows".equals(info.getName())){
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		}
		Runner RUNNER = new Runner();
		RUNNER.start();
	}
	/**
	 * Starts the program.
	 */
	private void start(){
		singleInstanceCheck();
		guiStartup = new GUIStartup();
		guiStartup.show();
	}
	/**
	 * Check if there is already one instance of the program running.
	 */
	private void singleInstanceCheck(){
		try {
			ss = new ServerSocket(61236);
		} catch (Exception e) {
			System.exit(-1);
		}
	}
	/**
	 * Resets the ServerSocket.
	 */
	protected void finalize(){
		try{
			ss.close();
		} catch (Exception e){
		    System.exit(-1);
		}
	}
}