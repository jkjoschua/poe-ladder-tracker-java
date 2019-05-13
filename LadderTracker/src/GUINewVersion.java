import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.awt.event.ActionEvent;

public class GUINewVersion{
	private JButton buttonContinue, buttonOpenInBrowser;
	private JFrame windowNewVersion;
	private JTextPane textNewVersion;
	private URL githubURL;
	private String githubVersionLink = "https://raw.githubusercontent.com/jkjoschua/poe-ladder-tracker-java/master/VERSION";
	private String githubReleasesLink = "https://github.com/jkjoschua/poe-ladder-tracker-java/releases";
	private String version = "2.7", latestVersion;
	private volatile boolean wait = true;

	public GUINewVersion() throws Exception{
		githubURL = new URL(githubVersionLink);
		getLatestVersion();
		initialize();
	}
	private void getLatestVersion() throws Exception{
		HttpURLConnection connection = (HttpURLConnection) githubURL.openConnection();
		connection.setConnectTimeout(60000);
		
		InputStream inputStream = connection.getInputStream();
		
		InputStreamReader inputStreamReader = null;
	    BufferedReader bufferedReader = null;
		String line;
		
		inputStreamReader = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(inputStreamReader);
		
	    int lineNumber = 0;
	    while((line = bufferedReader.readLine()) != null){
			if(lineNumber >= 0){
				latestVersion = line;
			}
			lineNumber ++;
	    }
	    
	    inputStreamReader.close();
		bufferedReader.close();
		inputStream.close();
	}
	private void initialize(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		windowNewVersion = new JFrame();	
		windowNewVersion.setBounds(100, 100, 300, 108);
		windowNewVersion.setLocation(dim.width/2-windowNewVersion.getSize().width/2, dim.height/2-windowNewVersion.getSize().height/2);
		windowNewVersion.setResizable(false);
		windowNewVersion.setTitle("Ladder Tracker v" + version);
		windowNewVersion.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		windowNewVersion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowNewVersion.getContentPane().setLayout(null);
		
		buttonContinue = new JButton("Continue");
		buttonContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wait = false;
			}
		});
		buttonContinue.setBounds(155, 45, 118, 23);
		windowNewVersion.getContentPane().add(buttonContinue);
		
		textNewVersion = new JTextPane();
		textNewVersion.setText("There is a new version available on Github!");
		textNewVersion.setEditable(false);
		textNewVersion.setBackground(SystemColor.menu);
		textNewVersion.setBounds(39, 11, 212, 23);
		windowNewVersion.getContentPane().add(textNewVersion);
		
		buttonOpenInBrowser = new JButton("Open in browser");
		buttonOpenInBrowser.setBounds(20, 45, 118, 23);
		buttonOpenInBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openBrowser();
				System.exit(0);
			}
		});
		windowNewVersion.getContentPane().add(buttonOpenInBrowser);
	}
	private void openBrowser(){
	    try {
	        Desktop.getDesktop().browse(new URL(githubReleasesLink).toURI());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void check(){
		if((Character.getNumericValue((version.charAt(0))) < Character.getNumericValue((latestVersion.charAt(0)))) || (Character.getNumericValue((version.charAt(2))) < Character.getNumericValue((latestVersion.charAt(2))))){
			windowNewVersion.setVisible(true);
			while(wait){}
			windowNewVersion.dispose();
		}
	}
}