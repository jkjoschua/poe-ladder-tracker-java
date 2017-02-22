import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * The Runner object is used to start the program.
 * 
 * @author Joschn
 */
public class Runner{
	private GUIStartup guiStartup;
	
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
		guiStartup = new GUIStartup();
		guiStartup.show();
	}
}