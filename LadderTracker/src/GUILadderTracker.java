import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
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
	private JTextArea textRank, textClass, textData1, textData2, textData3, textData4, textLastUpdate;
	private JLabel labelDragAndDrop;
	private String showRank = "", showClassRank = "", showClass = "", showExpBehind = "", showExpAhead = "", showDeathsAhead = "", showExpPerHour = ""; 
	private String leagueID, character, linkBase;
	private boolean ladderUpdated = false, ladderFirstUpdate = true;
	private boolean displayDeathsAhead, displayExpAhead, displayExpBehind, displayExpPerHour;
	private int ladderUpdateInterval = 301000, windowLadderTrackerHeight = 85;
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	private String prefTimestamp;
	private boolean ladderRestExpHour = false;
	private boolean ladderModeCSV = true;
	private LadderCSV ladderCSV;
	private LadderAPI ladderAPI;
	private String leagName;

	/**
	 * Constructor of the GUILadderTracker object.
	 */
	public GUILadderTracker(String initialLeagName, boolean initialMode) {
		ladderModeCSV = initialMode;
		leagName = initialLeagName;
		leagName = leagName.replace(" ", "%20");
		initialize();
	}
	/**
	 * Initializes the ladder tracker GUI.
	 */
	private void initialize(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		// ladder tracker window
		windowLadderTracker = new JFrame();
		windowLadderTracker.setBounds(100, 100, 150, windowLadderTrackerHeight);
		windowLadderTracker.setLocation(Integer.parseInt(prefs.get("LadderTrackerLocationX", Integer.toString(dim.width/2-windowLadderTracker.getSize().width/2))), Integer.parseInt(prefs.get("LadderTrackerLocationY", Integer.toString(dim.height/2-windowLadderTracker.getSize().height/2))));
		windowLadderTracker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowLadderTracker.setTitle("Ladder Tracker v2.1");
		windowLadderTracker.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		windowLadderTracker.setUndecorated(true);
		windowLadderTracker.setAlwaysOnTop(true);
		windowLadderTracker.getContentPane().setLayout(null);
        
        // drag and drop label
        labelDragAndDrop = new JLabel("");
        labelDragAndDrop.setBounds(0, 0, 150, windowLadderTrackerHeight);
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
        
        // data1 text
        textData1 = new JTextArea();
        textData1.setText("");
        textData1.setFont(new Font("Century Schoolbook", Font.PLAIN, 10));
        textData1.setEditable(false);
        textData1.setBackground(SystemColor.menu);
        textData1.setBounds(0, 45, 140, 15);
        windowLadderTracker.getContentPane().add(textData1);
        
        // data2 text
        textData2 = new JTextArea();
        textData2.setText("");
        textData2.setFont(new Font("Century Schoolbook", Font.PLAIN, 10));
        textData2.setEditable(false);
        textData2.setBackground(SystemColor.menu);
        textData2.setBounds(0, 57, 140, 15);
        windowLadderTracker.getContentPane().add(textData2);
        
        // data3 text
        textData3 = new JTextArea();
        textData3.setText("");
        textData3.setFont(new Font("Century Schoolbook", Font.PLAIN, 10));
        textData3.setEditable(false);
        textData3.setBackground(SystemColor.menu);
        textData3.setBounds(0, 69, 140, 15);
        windowLadderTracker.getContentPane().add(textData3);
        
        // data4 text
        textData4 = new JTextArea();
        textData4.setText("");
        textData4.setFont(new Font("Century Schoolbook", Font.PLAIN, 10));
        textData4.setEditable(false);
        textData4.setBackground(SystemColor.menu);
        textData4.setBounds(0, 81, 140, 15);
        windowLadderTracker.getContentPane().add(textData4);
        
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
				//Ladder ladder;
				
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
								if(counter == 500){
									textData1.setText("servers probably down");
									textData2.setText("check www.pathofexile.com");
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				};
				// start thread
				ThreadLadderFirstUpdating.start();
				
				if(ladderModeCSV){
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
							ladderCSV = new LadderCSV(linkBase, character);
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else{
					ladderAPI = new LadderAPI(character, leagName);
				}
				
				Thread ThreadLadderResetExpHour = new Thread(){
					public void run(){
						while(true){
							if(ladderRestExpHour){
								if(!showExpPerHour.equals("")){
									
									if(ladderModeCSV){
										ladderRestExpHour = false;
										ladderCSV.resetExpHour();
										
										// depending on the configuration
										if(displayDeathsAhead){						
											if(displayExpBehind){				
												if(displayExpAhead){		
													if(displayExpPerHour){
														textData4.setText("Exp/h : " + "reset" + " " + "(" + ladderCSV.getProgress() + "%)");
													}
												}
												else{
													textData3.setText("Exp/h : " + "reset" + " " + "(" + ladderCSV.getProgress() + "%)");
												}
											}
											else if(displayExpAhead){
												if(displayExpPerHour){
													textData3.setText("Exp/h : " + "reset" + " " + "(" + ladderCSV.getProgress() + "%)");
												}
											}
											else{
												textData2.setText("Exp/h : " + "reset" + " " + "(" + ladderCSV.getProgress() + "%)");
											}
										}
										else if(displayExpBehind){
											if(displayExpAhead){
												if(displayExpPerHour){
													textData3.setText("Exp/h : " + "reset" + " " + "(" + ladderCSV.getProgress() + "%)");
												}
											}
											else{
												textData2.setText("Exp/h : " + "reset" + " " + "(" + ladderCSV.getProgress() + "%)");
											}
										}
										else if(displayExpAhead){
											if(displayExpPerHour){
												textData2.setText("Exp/h : " + "reset" + " " + "(" + ladderCSV.getProgress() + "%)");
											}
										}
										else{
											textData1.setText("Exp/h : " + "reset" + " " + "(" + ladderCSV.getProgress() + "%)");
										}
									}
									else{
										ladderRestExpHour = false;
										ladderAPI.resetExpHour();
										
										// depending on the configuration
										if(displayDeathsAhead){						
											if(displayExpBehind){				
												if(displayExpAhead){		
													if(displayExpPerHour){
														textData4.setText("Exp/h : " + "reset" + " " + "(" + ladderAPI.getProgress() + "%)");
													}
												}
												else{
													textData3.setText("Exp/h : " + "reset" + " " + "(" + ladderAPI.getProgress() + "%)");
												}
											}
											else if(displayExpAhead){
												if(displayExpPerHour){
													textData3.setText("Exp/h : " + "reset" + " " + "(" + ladderAPI.getProgress() + "%)");
												}
											}
											else{
												textData2.setText("Exp/h : " + "reset" + " " + "(" + ladderAPI.getProgress() + "%)");
											}
										}
										else if(displayExpBehind){
											if(displayExpAhead){
												if(displayExpPerHour){
													textData3.setText("Exp/h : " + "reset" + " " + "(" + ladderAPI.getProgress() + "%)");
												}
											}
											else{
												textData2.setText("Exp/h : " + "reset" + " " + "(" + ladderAPI.getProgress() + "%)");
											}
										}
										else if(displayExpAhead){
											if(displayExpPerHour){
												textData2.setText("Exp/h : " + "reset" + " " + "(" + ladderAPI.getProgress() + "%)");
											}
										}
										else{
											textData1.setText("Exp/h : " + "reset" + " " + "(" + ladderAPI.getProgress() + "%)");
										}
										
									}
								}
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e){
								e.printStackTrace();
							}
						}
					}
				};
				
				ThreadLadderResetExpHour.start();
				
				// infinite loop
				while(true){
					// check timestamp
					if(ladderModeCSV){
						prefTimestamp = prefs.get("LastUpdate", "0");
						
						if(Integer.parseInt(prefTimestamp) == 0){
							textClass.setText("First start!");
							textData1.setText("Takes around 320 secounds!");
							prefs.put("LastUpdate", Integer.toString((int) new Date().getTime()));
						}
						prefTimestamp = prefs.get("LastUpdate", "0");
						
						int currentTime = (int) new Date().getTime();
						@SuppressWarnings("unused")
						int diff = currentTime-Integer.parseInt(prefTimestamp);
						while(currentTime-Integer.parseInt(prefTimestamp) < 300000){
							currentTime = (int) new Date().getTime();
							diff = currentTime-Integer.parseInt(prefTimestamp);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						prefs.put("LastUpdate", Integer.toString((int) new Date().getTime()));
					}
					
					// ladder update 
					while(true){			
						try {
							if(ladderModeCSV){
								ladderCSV.update();
								
								// wait until the first ladder update is done
								if(ladderFirstUpdate){
									ladderFirstUpdate = false;
								}
								ThreadLadderFirstUpdating.join();
	
								showRank = Integer.toString(ladderCSV.getRank());
								showClassRank = Integer.toString(ladderCSV.getClassRank());
								showClass = ladderCSV.getClassName();
								showDeathsAhead = Integer.toString(ladderCSV.getDeathsAhead());
								showExpBehind = stringAddDots(Long.toString(ladderCSV.getExpBehind()));
								showExpAhead = stringAddDots(Long.toString(ladderCSV.getExpAhead()));
								showExpPerHour = ladderCSV.getExpPerHour();
							}
							else{
								ladderAPI.update();
								
								// wait until the first ladder update is done
								if(ladderFirstUpdate){
									ladderFirstUpdate = false;
								}
								ThreadLadderFirstUpdating.join();
	
								showRank = Integer.toString(ladderAPI.getRank());
								showClassRank = Integer.toString(ladderAPI.getClassRank());
								showClass = ladderAPI.getClassName();
								showDeathsAhead = Integer.toString(ladderAPI.getDeathsAhead());
								showExpBehind = stringAddDots(Long.toString(ladderAPI.getExpBehind()));
								showExpAhead = stringAddDots(Long.toString(ladderAPI.getExpAhead()));
								showExpPerHour = ladderAPI.getExpPerHour();
								
							}
							
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
							
							int dotsExpAhead = 0, dotsExpBehind = 0;
							for(int i = 0; i < showExpAhead.length()-2; i++){
								if (showExpAhead.substring(i+1, i+2).equals(".")){
									dotsExpAhead = dotsExpAhead+1;
								}		
							}
							for(int i = 0; i < showExpBehind.length()-2; i++){
								if (showExpBehind.substring(i+1, i+2).equals(".")){
									dotsExpBehind = dotsExpBehind+1;
								}
							}	
							if(dotsExpAhead == dotsExpBehind){
								// nothing
							}				
							else if(dotsExpBehind > dotsExpAhead){
								for(int d = 0; d < dotsExpBehind-dotsExpAhead; d++){
									showExpBehind = " " + showExpBehind;
								}
							}
							else{
								for(int d = 0; d < dotsExpAhead-dotsExpBehind; d++){
									showExpAhead = " " + showExpAhead;
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
							if(ladderModeCSV){
								if(ladderCSV.isCharacterFound()){
									resetHeight();
									// always
									textRank.setText("Rank " + showRank);
									textClass.setText(showClassRank + ". " + showClass);
									
									// depending on the configuration
									if(displayDeathsAhead){
										textData1.setText("DeathsAhead: " + showDeathsAhead);
										
										if(displayExpBehind){
											textData2.setText("ExpBehind: " + showExpBehind);
											
											if(displayExpAhead){
												textData3.setText("ExpAhead:  " + showExpAhead);
												
												if(displayExpPerHour){
													textData4.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderCSV.getProgress() + "%)");
												}
											}
											else{
												textData3.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderCSV.getProgress() + "%)");
											}
										}
										else if(displayExpAhead){
											textData2.setText("ExpAhead: " + showExpAhead);
											
											if(displayExpPerHour){
												textData3.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderCSV.getProgress() + "%)");
											}
										}
										else{
											textData2.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderCSV.getProgress() + "%)");
										}
									}
									else if(displayExpBehind){
										textData1.setText("ExpBehind: " + showExpBehind);
										
										if(displayExpAhead){
											textData2.setText("ExpAhead:  " + showExpAhead);
											
											if(displayExpPerHour){
												textData3.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderCSV.getProgress() + "%)");
											}
										}
										else{
											textData2.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderCSV.getProgress() + "%)");
										}
									}
									else if(displayExpAhead){
										textData1.setText("ExpAhead: " + showExpAhead);
										
										if(displayExpPerHour){
											textData2.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderCSV.getProgress() + "%)");
										}
									}
									else{
										textData1.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderCSV.getProgress() + "%)");
									}
								}
								else{
									resetHeight(85);
									textRank.setText("Character");
									textClass.setText("was not found!");
									textData1.setText("Requirements");
									if(ladderCSV.getRequiredLevel() == null){
										textData2.setText("Level: empty league");
										textData3.setText("Exp: empty league");		
									}
									else{
										textData2.setText("Level: " + ladderCSV.getRequiredLevel());
										textData3.setText("Exp: " + stringAddDots(ladderCSV.getRequiredExp()));			
									}
								}
							}
							else{
								if(ladderAPI.isCharacterFound()){
									resetHeight();
									// always
									textRank.setText("Rank " + showRank);
									textClass.setText(showClassRank + ". " + showClass);
									
									// depending on the configuration
									if(displayDeathsAhead){
										textData1.setText("DeathsAhead: " + showDeathsAhead);
										
										if(displayExpBehind){
											textData2.setText("ExpBehind: " + showExpBehind);
											
											if(displayExpAhead){
												textData3.setText("ExpAhead:  " + showExpAhead);
												
												if(displayExpPerHour){
													textData4.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderAPI.getProgress() + "%)");
												}
											}
											else{
												textData3.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderAPI.getProgress() + "%)");
											}
										}
										else if(displayExpAhead){
											textData2.setText("ExpAhead: " + showExpAhead);
											
											if(displayExpPerHour){
												textData3.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderAPI.getProgress() + "%)");
											}
										}
										else{
											textData2.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderAPI.getProgress() + "%)");
										}
									}
									else if(displayExpBehind){
										textData1.setText("ExpBehind: " + showExpBehind);
										
										if(displayExpAhead){
											textData2.setText("ExpAhead:  " + showExpAhead);
											
											if(displayExpPerHour){
												textData3.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderAPI.getProgress() + "%)");
											}
										}
										else{
											textData2.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderAPI.getProgress() + "%)");
										}
									}
									else if(displayExpAhead){
										textData1.setText("ExpAhead: " + showExpAhead);
										
										if(displayExpPerHour){
											textData2.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderAPI.getProgress() + "%)");
										}
									}
									else{
										textData1.setText("Exp/h : " + showExpPerHour + " " + "(" + ladderAPI.getProgress() + "%)");
									}
								}
								else{
									resetHeight(85);
									textRank.setText("Character");
									textClass.setText("was not found!");
									textData1.setText("Requirements");
									if(ladderAPI.getRequiredLevel() == null){
										textData2.setText("Level: empty league");
										textData3.setText("Exp: empty league");		
									}
									else{
										textData2.setText("Level: " + ladderAPI.getRequiredLevel());
										textData3.setText("Exp: " + stringAddDots(ladderAPI.getRequiredExp()));			
									}
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
							
							// resetHeight();
							if(ladderModeCSV){
								sleep(ladderUpdateInterval);
							}
							else{
								sleep(3000);
							}
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
	 * Sets the additional which should be shown.
	 * 
	 * @param deathsAhead - DeahtsAhead will be shown for true.
	 * @param expAhead - ExpAhead will be shown for true.
	 * @param expBehind - ExpBehind will be shown for true.
	 * @param expPerHour - ExpPerHour will be shown for ture.
	 */
	public void setDisplayData(boolean deathsAhead, boolean expAhead, boolean expBehind, boolean expPerHour){
		displayDeathsAhead = deathsAhead;
		displayExpAhead = expAhead;
		displayExpBehind = expBehind;
		displayExpPerHour = expPerHour;
	}
	/**
	 * Resets the height of the ladder tracker window.
	 */
	private void resetHeight(){
		int multiplicator = 0;
		
		if(displayDeathsAhead){
			multiplicator++;
		}
		if(displayExpAhead){
			multiplicator++;
		}
		if(displayExpBehind){
			multiplicator++;
		}
		if(displayExpPerHour){
			multiplicator++;
		}
		windowLadderTrackerHeight = 45+12*multiplicator+4;
		windowLadderTracker.setSize(150, windowLadderTrackerHeight);
		labelDragAndDrop.setBounds(0, 0, 150, windowLadderTrackerHeight);
	}
	/**
	 * Resets the height of the ladder tracker window.
	 */
	private void resetHeight(int height){
		windowLadderTrackerHeight = height;
		windowLadderTracker.setSize(150, windowLadderTrackerHeight);
		labelDragAndDrop.setBounds(0, 0, 150, windowLadderTrackerHeight);
	}
	/**
	 * The mouse adapter for the drag and drop label.
	 * 
	 * @author Joschn
	 */
	public class FrameDragListener extends MouseAdapter{
        private Point mouseDownCompCoords = null;
        private boolean rightMouseButtonPressed = false;
        private int timestampLow = 0, timestampHigh = 0;

        public FrameDragListener(){
        }
        public void mouseReleased(MouseEvent e){
        	if(rightMouseButtonPressed){
		        mouseDownCompCoords = null;
		        rightMouseButtonPressed = false;
        	}
        }
        public void mousePressed(MouseEvent e){
        	if(!SwingUtilities.isRightMouseButton(e)){
        		mouseDownCompCoords = e.getPoint();
        		rightMouseButtonPressed = true;
        	}
        	if(SwingUtilities.isRightMouseButton(e)){
        		if(timestampLow == 0){
        			timestampLow = (int) new Date().getTime();
        		}
        		else if(timestampHigh == 0){
        			timestampHigh = (int) new Date().getTime();
        		}
        		else{
        			timestampLow = timestampHigh;
        			timestampHigh = (int) new Date().getTime();
        		}
        		
				if(timestampHigh - timestampLow < 350){
					ladderRestExpHour = true;
				}
        	}
        }
        public void mouseDragged(MouseEvent e){
        	if(rightMouseButtonPressed){
		        Point currCoords = e.getLocationOnScreen();
		        windowLadderTracker.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
		        prefs.put("LadderTrackerLocationX", Integer.toString(currCoords.x - mouseDownCompCoords.x));
		        prefs.put("LadderTrackerLocationY", Integer.toString(currCoords.y - mouseDownCompCoords.y));
        	}
        }
    }
}