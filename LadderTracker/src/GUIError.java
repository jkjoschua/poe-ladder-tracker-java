import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import java.awt.SystemColor;

/**
 * The GUIError object is used to show an error message if the Path of Exile API is not responding.
 * 
 * @author Joschn
 */
public class GUIError{
	private JFrame windowError;
	private JButton buttonRetry;
	private volatile boolean buttonPressed = false;
	private ButtonRetryListener buttonRetryListener = new ButtonRetryListener();
	private String errorMessage = "Error! Path of Exile's API is not responding! Servers are probably down! Check www.pathofexile.com";
	private String version = "2.7";
	
	/**
	 * Constructor for the GUIError object.
	 */
	public GUIError(){
		initialize();
	}
	/**
	 * Initializes the GUI.
	 */
	private void initialize(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		// error window
		windowError = new JFrame();	
		windowError.setBounds(100, 100, 300, 145);
		windowError.setLocation(dim.width/2-windowError.getSize().width/2, dim.height/2-windowError.getSize().height/2);
		windowError.setResizable(false);
		windowError.setTitle("Ladder Tracker v" + version);
		windowError.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		windowError.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowError.getContentPane().setLayout(null);
		
		// button retry
		buttonRetry = new JButton("Retry");
		buttonRetry.setBounds(10, 80, 274, 23);
		buttonRetry.addActionListener(buttonRetryListener);
		windowError.getContentPane().add(buttonRetry);
		
		// error text 
		JTextPane textError = new JTextPane();
		textError.setText(errorMessage);
		textError.setEditable(false);
		textError.setBackground(SystemColor.menu);
		textError.setBounds(10, 21, 274, 39);
		windowError.getContentPane().add(textError);
	}
	/**
	 * Shows the error GUI and waits for the retry button to be pressed.
	 */
	public void show(){
		windowError.setVisible(true);
		while(!buttonPressed){}
		windowError.dispose();
	}
	/**
	 * The definition of the action listener for the retry button.
	 * 
	 * @author Joschn
	 */
	private class ButtonRetryListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			buttonPressed = true;
		}
	}
}