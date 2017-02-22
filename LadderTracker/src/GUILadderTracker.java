import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import javax.swing.JLabel;
import java.awt.ComponentOrientation;
import java.awt.Dimension;

/**
 * The GUILadderTracker object handles the ladder tracker window.
 * 
 * @author Joschn
 */
public class GUILadderTracker{
	private JFrame windowLadderTracker;
	private JTextArea textRank, textClass, textExpBehind, textExpAhead, textDeathsAhead, textLastUpdate;
	private JLabel labelDragAndDrop;
	private String showRank = "", showClassRank = "", showClass = "", showExpBehind = "", showExpAhead = "", showDeathsAhead = ""; 
	private String leagueID, character, linkBase;
	private boolean ladderUpdated = false;
	private boolean ladderFirstUpdate = true;
	private int ladderUpdateInterval = 11000;
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

	/**
	 * Constructor of the GUILadderTracker object.
	 */
	public GUILadderTracker() {
		initialize();
	}
	/**
	 * Initializes the ladder tracker GUI.
	 */
	private void initialize(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		// ladder tracker window
		windowLadderTracker = new JFrame();
		windowLadderTracker.setBounds(100, 100, 150, 85);
		windowLadderTracker.setLocation(Integer.parseInt(prefs.get("LadderTrackerLocationX", Integer.toString(dim.width/2-windowLadderTracker.getSize().width/2))), Integer.parseInt(prefs.get("LadderTrackerLocationY", Integer.toString(dim.height/2-windowLadderTracker.getSize().height/2))));
		windowLadderTracker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowLadderTracker.setTitle("Ladder Tracker v1.0");
		windowLadderTracker.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		windowLadderTracker.setUndecorated(true);
		windowLadderTracker.setAlwaysOnTop(true);
		windowLadderTracker.getContentPane().setLayout(null);
        
        // drag and drop label
        labelDragAndDrop = new JLabel("");
        labelDragAndDrop.setBounds(0, 0, 150, 85);
        FrameDragListener dragAndDropListener = new FrameDragListener();
        labelDragAndDrop.addMouseListener(dragAndDropListener);
        labelDragAndDrop.addMouseMotionListener(dragAndDropListener);
        windowLadderTracker.getContentPane().add(labelDragAndDrop);
   
        // rank text
        textRank = new JTextArea();
        textRank.setEditable(false);
        textRank.setFont(new Font("Century Schoolbook", Font.PLAIN, 18));
        textRank.setBackground(UIManager.getColor("Button.background"));
        textRank.setText("");
        textRank.setBounds(0, 0, 104, 25);
        textRank.setHighlighter(null);
        windowLadderTracker.getContentPane().add(textRank);
        
        // class text
        textClass = new JTextArea();
        textClass.setText("");
        textClass.setFont(new Font("Century Schoolbook", Font.PLAIN, 18));
        textClass.setEditable(false);
        textClass.setBackground(SystemColor.menu);
        textClass.setBounds(0, 20, 140, 24);
        windowLadderTracker.getContentPane().add(textClass);
        
        // deaths ahead text
        textDeathsAhead = new JTextArea();
        textDeathsAhead.setText("");
        textDeathsAhead.setFont(new Font("Century Schoolbook", Font.PLAIN, 10));
        textDeathsAhead.setEditable(false);
        textDeathsAhead.setBackground(SystemColor.menu);
        textDeathsAhead.setBounds(0, 45, 140, 15);
        windowLadderTracker.getContentPane().add(textDeathsAhead);
        
        // exp behind text
        textExpBehind = new JTextArea();
        textExpBehind.setText("");
        textExpBehind.setFont(new Font("Century Schoolbook", Font.PLAIN, 10));
        textExpBehind.setEditable(false);
        textExpBehind.setBackground(SystemColor.menu);
        textExpBehind.setBounds(0, 57, 140, 15);
        windowLadderTracker.getContentPane().add(textExpBehind);
        
        // exp ahead text
        textExpAhead = new JTextArea();
        textExpAhead.setText("");
        textExpAhead.setFont(new Font("Century Schoolbook", Font.PLAIN, 10));
        textExpAhead.setEditable(false);
        textExpAhead.setBackground(SystemColor.menu);
        textExpAhead.setBounds(0, 69, 140, 15);
        windowLadderTracker.getContentPane().add(textExpAhead);
        
        // last update text
        textLastUpdate = new JTextArea();
        textLastUpdate.setText("");
        textLastUpdate.setFont(new Font("Century Schoolbook", Font.PLAIN, 10));
        textLastUpdate.setEditable(false);
        textLastUpdate.setBackground(SystemColor.menu);
        textLastUpdate.setBounds(100, 0, 50, 25);
        textLastUpdate.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        windowLadderTracker.getContentPane().add(textLastUpdate);
	}
	/**
	 * Shows the ladder tracker window and starts the update cycle.
	 */
	public void show(){
		windowLadderTracker.setVisible(true);

		// thread definition of the ladder tracker (never ending)
		Thread ThreadLadderTracker = new Thread(){
			public void run(){
				boolean counterON = false;
				Ladder ladder;
				
				// thread definition of the updating window
				Thread ThreadLadderFirstUpdating = new Thread(){
					private int counter = 0;
					public void run(){
						while(ladderFirstUpdate){				
							try {
								textLastUpdate.setText(Integer.toString(counter));
								textRank.setText("updating");
								Thread.sleep(500);
								textRank.setText("updating.");
								Thread.sleep(500);
								counter++;
								textLastUpdate.setText(Integer.toString(counter));
								textRank.setText("updating..");
								Thread.sleep(500);
								textRank.setText("updating...");
								Thread.sleep(500);
								counter++;
								if(counter == 90){
									textDeathsAhead.setText("servers probably down");
									textExpBehind.setText("check www.pathofexile.com");
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				};
				// start thread
				ThreadLadderFirstUpdating.start();
				
				// create csv link (only once)
				while(true){	
					try {
						CSVLinkCreator csvLink = new CSVLinkCreator(leagueID);
						csvLink.create();
						linkBase = csvLink.getCSVFileLink();
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// initialize ladder (only once)
				while(true){
					try {
						ladder = new Ladder(linkBase, character);
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// infinite loop
				while(true){
					// ladder update 
					while(true){			
						try {
							ladder.update();
							
							// wait until the first ladder update is done
							if(ladderFirstUpdate){
								ladderFirstUpdate = false;
							}
							ThreadLadderFirstUpdating.join();

							showRank = Integer.toString(ladder.getRank());
							showClassRank = Integer.toString(ladder.getClassRank());
							showClass = ladder.getClassName();
							showDeathsAhead = Integer.toString(ladder.getDeathsAhead());
							showExpBehind = stringAddDots(Long.toString(ladder.getExpBehind()));
							showExpAhead = stringAddDots(Long.toString(ladder.getExpAhead()));
							
							if(showExpAhead.length() == showExpBehind.length()){
								
							}
							else if(showExpBehind.length() > showExpAhead.length()){
								for(int i = showExpAhead.length(); i < showExpBehind.length(); i++){
									showExpAhead = "  " + showExpAhead;
								}
							}
							else{
								for(int i = showExpBehind.length(); i < showExpAhead.length(); i++){
									showExpBehind = "  " + showExpBehind;
								}	
							}
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}	
					// ladder tracker window update
					while(true){
						try {
							if(ladder.isCharacterFound()){
								textRank.setText("Rank " + showRank);
								textClass.setText(showClassRank + ". " + showClass);
								textDeathsAhead.setText("DeathsAhead: " + showDeathsAhead);
								textExpBehind.setText("ExpBehind: " + showExpBehind);
								textExpAhead.setText("ExpAhead : " + showExpAhead);
							}
							else{
								textRank.setText("Character");
								textClass.setText("was not found!");
								textDeathsAhead.setText("Requirements");
								if(ladder.getRequiredLevel() == null){
									textExpBehind.setText("Level: empty league");
									textExpAhead.setText("Exp: empty league");		
								}
								else{
									textExpBehind.setText("Level: " + ladder.getRequiredLevel());
									textExpAhead.setText("Exp: " + stringAddDots(ladder.getRequiredExp()));			
								}
							}
							if(ladderUpdated){
								ladderUpdated = false;
							}
							else{
								ladderUpdated = true;
							}

							// thread definition of timer
							Thread ThreadCounter = new Thread(){
								private int counter;
								private boolean currentUpdateStatus = ladderUpdated;
								public void run(){
									counter = 0;
									while(true){
										if(ladderUpdated != currentUpdateStatus){
											counter = 0;
											currentUpdateStatus = ladderUpdated;
										}
										textLastUpdate.setText(Integer.toString(counter));
										counter++;
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							};
							
							if(!counterON){
								ThreadCounter.start();
								counterON = true;
							}
							sleep(ladderUpdateInterval);
							break;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}				
				}
			}
		};
		ThreadLadderTracker.start();
	}
	/**
	 * Adds dots to a string.
	 * 
	 * @param s - String to edit.
	 * @return String with dots.
	 */
	private String stringAddDots(String s){
		int len = s.length();
		String temp = "";
		int i;
		for(i = 0; (len-((i+1)*3)) > 0; i++){
			temp = s.substring(len-((i+1)*3), len-(i*3)) + temp;
			temp = "." + temp;
		}
		temp = s.subSequence(0, len-(((i-1)+1)*3)) + temp;
		return temp;
	}
	/**
	 * Sets the league ID.
	 * 
	 * @param id - The league ID as a string.
	 */
	public void setleagueID(String id){
		leagueID = id;
	}
	/**
	 * Sets the character name.
	 * 
	 * @param characterName - The character name as a String.
	 */
	public void setCharacter(String characterName){
		character = characterName;
	}
	/**
	 * The mouse adapter for the drag and drop label.
	 * 
	 * @author Joschn
	 */
	public class FrameDragListener extends MouseAdapter{
        private Point mouseDownCompCoords = null;

        public FrameDragListener(){
        }
        public void mouseReleased(MouseEvent e){
            mouseDownCompCoords = null;
        }
        public void mousePressed(MouseEvent e){
        	if(!SwingUtilities.isRightMouseButton(e)){
        		mouseDownCompCoords = e.getPoint();
        	}
        }
        public void mouseDragged(MouseEvent e){
            Point currCoords = e.getLocationOnScreen();
            windowLadderTracker.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            prefs.put("LadderTrackerLocationX", Integer.toString(currCoords.x - mouseDownCompCoords.x));
            prefs.put("LadderTrackerLocationY", Integer.toString(currCoords.y - mouseDownCompCoords.y));
        }
    }
}