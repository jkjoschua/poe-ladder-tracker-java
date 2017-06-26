import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JCheckBox;

/**
 * The GUIStartup object contains the first GUI that is shown if no error occurs.
 * 
 * @author Joschn
 */
public class GUIStartup{	
	private JFrame windowStartUp;
	private GUILadderTracker windowLadderTracker;
	private JComboBox<String> comboBoxLeague, comboBoxMode;
	private JTextField textField, textFieldTextColorR, textFieldTextColorG, textFieldTextColorB, textFieldBackgroundColorR, textFieldBackgroundColorG, textFieldBackgroundColorB;
	private JTextPane textLeague, textCharacter, textDisplayAlso, textStart, textEnd, textStartLeague, textEndLeague, textMode, textLine, textBackgroundColor, textTextColor, textLine2, textR, textG, textB;
	private JCheckBox checkboxExpBehind, checkboxExpAhead, checkboxExpPerHour, checkboxDeathsAhead;
	private JButton buttonStart, buttonColorReset;
	private ComboBoxListener comboBoxListener = new ComboBoxListener();
	private TextFieldListener textFieldListener = new TextFieldListener();
	private TextFieldKeyListener textFieldKeyListener = new TextFieldKeyListener();
	private ButtonStartListener buttonStartListener = new ButtonStartListener();
	private CheckboxListener checkboxListener = new CheckboxListener();
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	private LeagueCollector leagueCollector;
	private String[] comboBoxContent, leagueID;
	private String selectedLeagueID, prefCharacterName, prefTextColorR, prefTextColorG, prefTextColorB, prefBackgroundColorR, prefBackgroundColorG, prefBackgroundColorB, version = "v2.2";
	private Boolean prefDisplayExpBehind, prefDisplayExpAhead, prefDisplayExpPerHour, prefDisplayDeathsAhead;
	private int prefComboBoxLeagueSelectedItem, prefComboBoxModeSelectedItem;

	/**
	 * Constructor of the GUIStartup object.
	 */
	public GUIStartup(){
		//clearPreferences();
		loadPreferences();
		initialize();
	}
	/**
	 * Initializes the startup GUI.
	 */
	private void initialize(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// start up frame
		windowStartUp = new JFrame();	
		windowStartUp.setBounds(100, 100, 300, 357);
		windowStartUp.setLocation(dim.width/2-windowStartUp.getSize().width/2, dim.height/2-windowStartUp.getSize().height/2);
		windowStartUp.setResizable(false);
		windowStartUp.setTitle("Ladder Tracker " + version);
		windowStartUp.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		windowStartUp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowStartUp.getContentPane().setLayout(null);
		
		// button start
		buttonStart = new JButton("Start");
		buttonStart.setBounds(10, 299, 274, 23);
		buttonStart.addActionListener(buttonStartListener);
		windowStartUp.getContentPane().add(buttonStart);
		
		// text league
		textLeague = new JTextPane();
		textLeague.setEditable(false);
		textLeague.setBackground(UIManager.getColor("CheckBox.background"));
		textLeague.setText("League");
		textLeague.setBounds(10, 11, 56, 20);
		windowStartUp.getContentPane().add(textLeague);
		
		// combo box league
		comboBoxLeague = new JComboBox<String>();
		comboBoxLeague.setBounds(74, 11, 210, 20);
		configureComboBoxLeague();
		windowStartUp.getContentPane().add(comboBoxLeague);
		
		// text character
		textCharacter = new JTextPane();
		textCharacter.setEditable(false);
		textCharacter.setText("Character");
		textCharacter.setBackground(SystemColor.menu);
		textCharacter.setBounds(10, 70, 56, 20);
		windowStartUp.getContentPane().add(textCharacter);
		
		// text field for the character name
		textField = new JTextField();
		textField.addKeyListener(textFieldKeyListener);
		textField.setBounds(74, 70, 210, 20);
		textField.setText(prefCharacterName);
		textField.setColumns(10);
		textField.getDocument().addDocumentListener(textFieldListener);
		windowStartUp.getContentPane().add(textField);
		
		// text display also
		textDisplayAlso = new JTextPane();
		textDisplayAlso.setText("Display also:");
		textDisplayAlso.setEditable(false);
		textDisplayAlso.setBackground(SystemColor.menu);
		textDisplayAlso.setBounds(10, 103, 66, 20);
		windowStartUp.getContentPane().add(textDisplayAlso);
		
		// check box deaths ahead
		checkboxDeathsAhead = new JCheckBox("DeahtsAhead");
		checkboxDeathsAhead.setBounds(74, 102, 97, 23);
		checkboxDeathsAhead.setSelected(prefDisplayDeathsAhead);
		checkboxDeathsAhead.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxDeathsAhead);
		
		// check box exp behind
		checkboxExpBehind = new JCheckBox("ExpBehind");
		checkboxExpBehind.setBounds(187, 102, 97, 23);
		checkboxExpBehind.setSelected(prefDisplayExpBehind);
		checkboxExpBehind.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpBehind);
		
		// check box exp ahead
		checkboxExpAhead = new JCheckBox("ExpAhead");
		checkboxExpAhead.setBounds(187, 128, 97, 23);
		checkboxExpAhead.setSelected(prefDisplayExpAhead);
		checkboxExpAhead.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpAhead);
		
		// check box exp per hour
		checkboxExpPerHour = new JCheckBox("Exp/h + Progress");
		checkboxExpPerHour.setBounds(74, 128, 109, 23);
		checkboxExpPerHour.setSelected(prefDisplayExpPerHour);
		checkboxExpPerHour.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpPerHour);
		
		// text start
		textStart = new JTextPane();
		textStart.setText("Start  :");
		textStart.setEditable(false);
		textStart.setBackground(SystemColor.menu);
		textStart.setBounds(76, 32, 40, 20);
		windowStartUp.getContentPane().add(textStart);
		
		// text end
		textEnd = new JTextPane();
		textEnd.setText("End    :");
		textEnd.setEditable(false);
		textEnd.setBackground(SystemColor.menu);
		textEnd.setBounds(76, 48, 40, 20);
		windowStartUp.getContentPane().add(textEnd);
		
		// text start date
		textStartLeague = new JTextPane();
		textStartLeague.setEditable(false);
		textStartLeague.setBackground(SystemColor.menu);
		textStartLeague.setBounds(120, 32, 164, 20);
		windowStartUp.getContentPane().add(textStartLeague);
		
		// text end date
		textEndLeague = new JTextPane();
		textEndLeague.setEditable(false);
		textEndLeague.setBackground(SystemColor.menu);
		textEndLeague.setBounds(120, 48, 164, 20);
		windowStartUp.getContentPane().add(textEndLeague);
		
		// update start and end text
		updateDates();
		
		// text mode
		textMode = new JTextPane();
		textMode.setText("Mode:");
		textMode.setEditable(false);
		textMode.setBackground(SystemColor.menu);
		textMode.setBounds(10, 178, 47, 20);
		windowStartUp.getContentPane().add(textMode);
		
		// text line
		textLine = new JTextPane();
		textLine.setText("______________________________________________________________");
		textLine.setEditable(false);
		textLine.setBackground(SystemColor.menu);
		textLine.setBounds(10, 147, 274, 20);
		windowStartUp.getContentPane().add(textLine);
		
		// text line
		textLine2 = new JTextPane();
		textLine2.setText("______________________________________________________________");
		textLine2.setEditable(false);
		textLine2.setBackground(SystemColor.menu);
		textLine2.setBounds(10, 199, 274, 20);
		windowStartUp.getContentPane().add(textLine2);
		
		// combo box mode
		comboBoxMode = new JComboBox<String>();
		comboBoxMode.setBounds(74, 178, 210, 20);
		configureComboBoxMode();
		windowStartUp.getContentPane().add(comboBoxMode);	

		textTextColor = new JTextPane();
		textTextColor.setText("Text-Color");
		textTextColor.setEditable(false);
		textTextColor.setBackground(SystemColor.menu);
		textTextColor.setBounds(10, 240, 56, 20);
		windowStartUp.getContentPane().add(textTextColor);
		
		textFieldTextColorR = new JTextField();
		textFieldTextColorR.setText(prefTextColorR);
		textFieldTextColorR.setColumns(10);
		textFieldTextColorR.setBounds(121, 240, 40, 20);
		textFieldTextColorR.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	textFieldTextColorR.selectAll();
		            }
		        });
		    }
		});
		windowStartUp.getContentPane().add(textFieldTextColorR);
		
		textBackgroundColor = new JTextPane();
		textBackgroundColor.setText("Background-Color");
		textBackgroundColor.setEditable(false);
		textBackgroundColor.setBackground(SystemColor.menu);
		textBackgroundColor.setBounds(10, 268, 97, 20);
		windowStartUp.getContentPane().add(textBackgroundColor);
		
		textFieldTextColorG = new JTextField();
		textFieldTextColorG.setText(prefTextColorG);
		textFieldTextColorG.setColumns(10);
		textFieldTextColorG.setBounds(184, 240, 40, 20);
		textFieldTextColorG.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	textFieldTextColorG.selectAll();
		            }
		        });
		    }
		});
		windowStartUp.getContentPane().add(textFieldTextColorG);
		
		textFieldTextColorB = new JTextField();
		textFieldTextColorB.setText(prefTextColorB);
		textFieldTextColorB.setColumns(10);
		textFieldTextColorB.setBounds(244, 240, 40, 20);
		textFieldTextColorB.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	textFieldTextColorB.selectAll();
		            }
		        });
		    }
		});
		windowStartUp.getContentPane().add(textFieldTextColorB);
		
		textR = new JTextPane();
		textR.setText("R");
		textR.setEditable(false);
		textR.setBackground(SystemColor.menu);
		textR.setBounds(135, 220, 13, 20);
		windowStartUp.getContentPane().add(textR);
		
		textG = new JTextPane();
		textG.setText("G");
		textG.setEditable(false);
		textG.setBackground(SystemColor.menu);
		textG.setBounds(197, 220, 13, 20);
		windowStartUp.getContentPane().add(textG);
		
		textB = new JTextPane();
		textB.setText("B");
		textB.setEditable(false);
		textB.setBackground(SystemColor.menu);
		textB.setBounds(257, 220, 13, 20);
		windowStartUp.getContentPane().add(textB);
		
		textFieldBackgroundColorB = new JTextField();
		textFieldBackgroundColorB.setText(prefBackgroundColorB);
		textFieldBackgroundColorB.setColumns(10);
		textFieldBackgroundColorB.setBounds(244, 269, 40, 20);
		textFieldBackgroundColorB.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	textFieldBackgroundColorB.selectAll();
		            }
		        });
		    }
		});
		windowStartUp.getContentPane().add(textFieldBackgroundColorB);
		
		textFieldBackgroundColorG = new JTextField();
		textFieldBackgroundColorG.setText(prefBackgroundColorG);
		textFieldBackgroundColorG.setColumns(10);
		textFieldBackgroundColorG.setBounds(184, 269, 40, 20);
		textFieldBackgroundColorG.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	textFieldBackgroundColorG.selectAll();
		            }
		        });
		    }
		});
		windowStartUp.getContentPane().add(textFieldBackgroundColorG);
		
		textFieldBackgroundColorR = new JTextField();
		textFieldBackgroundColorR.setText(prefBackgroundColorR);
		textFieldBackgroundColorR.setColumns(10);
		textFieldBackgroundColorR.setBounds(121, 269, 40, 20);
		textFieldBackgroundColorR.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		            	textFieldBackgroundColorR.selectAll();
		            }
		        });
		    }
		});
		windowStartUp.getContentPane().add(textFieldBackgroundColorR);
		
		buttonColorReset = new JButton("Reset");
		buttonColorReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textFieldBackgroundColorR.setText("33");
				textFieldBackgroundColorG.setText("33");
				textFieldBackgroundColorB.setText("37");
				
				textFieldTextColorR.setText("231");
				textFieldTextColorG.setText("180");
				textFieldTextColorB.setText("119");
			}
		});
		buttonColorReset.setBounds(10, 220, 96, 20);
		windowStartUp.getContentPane().add(buttonColorReset);
	}
	@SuppressWarnings("unused")
	private void clearPreferences(){
		try {
			prefs.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Color textColor(){
		try{
			int R = Integer.parseInt(textFieldTextColorR.getText()), G = Integer.parseInt(textFieldTextColorG.getText()), B = Integer.parseInt(textFieldTextColorB.getText());
			
			if(R < 0 || R > 255){
				return new Color(231, 180, 119);
			}
			else if(B < 0 || B > 255){
				return new Color(231, 180, 119);
			}
			else if(B < 0 || B > 255){
				return new Color(231, 180, 119);
			}
			else{
				prefs.put("TextColorR", textFieldTextColorR.getText());
				prefs.put("TextColorG", textFieldTextColorG.getText());
				prefs.put("TextColorB", textFieldTextColorB.getText());
				return new Color(R, G, B);
			}
		}
		catch(Exception e) {
			return new Color(231, 180, 119);
		}
	}
	private Color backgroundColor(){
		try{
			int R = Integer.parseInt(textFieldBackgroundColorR.getText()), G = Integer.parseInt(textFieldBackgroundColorG.getText()), B = Integer.parseInt(textFieldBackgroundColorB.getText());
			
			if(R < 0 || R > 255){
				return new Color(33, 33, 37);
			}
			else if(B < 0 || B > 255){
				return new Color(33, 33, 37);
			}
			else if(B < 0 || B > 255){
				return new Color(33, 33, 37);
			}
			else{
				prefs.put("BackgroundColorR", textFieldBackgroundColorR.getText());
				prefs.put("BackgroundColorG", textFieldBackgroundColorG.getText());
				prefs.put("BackgroundColorB", textFieldBackgroundColorB.getText());
				return new Color(R, G, B);
			}
		}
		catch(Exception e) {
			return new Color(33, 33, 37);
		}
	}
	private void loadPreferences(){
		prefComboBoxLeagueSelectedItem = Integer.parseInt(prefs.get("LeagueNameListIndex", "0"));
		prefComboBoxModeSelectedItem = Integer.parseInt(prefs.get("LadderTrackerMode", "1"));
		prefCharacterName = prefs.get("CharacterName", "");
		prefDisplayDeathsAhead = Boolean.valueOf(prefs.get("checkboxDeathsAhead", "true"));
		prefDisplayExpBehind = Boolean.valueOf(prefs.get("checkboxExpBehind", "true"));
		prefDisplayExpAhead = Boolean.valueOf(prefs.get("checkboxExpAhead", "true"));
		prefDisplayExpPerHour = Boolean.valueOf(prefs.get("checkboxExpPerHour", "true"));
		prefBackgroundColorR = prefs.get("BackgroundColorR", "33");
		prefBackgroundColorG = prefs.get("BackgroundColorG", "33");
		prefBackgroundColorB = prefs.get("BackgroundColorB", "37");
		prefTextColorR = prefs.get("TextColorR", "231");
		prefTextColorG = prefs.get("TextColorG", "180");
		prefTextColorB = prefs.get("TextColorB", "119");
	}
	private void updateStartButton(){
		if(selectedLeagueID.equals("null")){
			buttonStart.setEnabled(false);
			buttonStart.setText("Unable to track right now!");
		}
		else{
			buttonStart.setEnabled(true);
			buttonStart.setText("Start");
		}
	}
	private void configureComboBoxLeague(){
		int counter = 0;
		while(true){
			try {
				leagueCollector = new LeagueCollector();
				List<String> leagueNameList = leagueCollector.getLeagueNameList();
				List<String> leagueThreadID = leagueCollector.getLeagueThreadID();
				
				comboBoxContent = new String[leagueNameList.size()];
				leagueID = new String[leagueThreadID.size()];

				for(int i = 0; i < leagueNameList.size(); i++){
					comboBoxContent[i] = leagueNameList.get(i);
					leagueID[i] = leagueThreadID.get(i);
				}
				
				comboBoxLeague.setModel(new DefaultComboBoxModel<String>(comboBoxContent));
				
				if(prefComboBoxLeagueSelectedItem < leagueNameList.size()){
					selectedLeagueID = leagueID[prefComboBoxLeagueSelectedItem];
					comboBoxLeague.setSelectedItem(comboBoxContent[prefComboBoxLeagueSelectedItem]);
				}
				else{
					System.out.println("da");
					selectedLeagueID = leagueID[0];
					comboBoxLeague.setSelectedItem(comboBoxContent[0]);
				}
				updateStartButton();
				
				
				comboBoxLeague.addItemListener(comboBoxListener);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error! - Unable to collect all leagues and races! (" + counter+1 + " tries)");
			}
			
			counter++;
			if(counter == 10){
				counter = 0;
				GUIError windowsError = new GUIError();
				windowsError.show();
			}
			else{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void configureComboBoxMode(){
		comboBoxMode.setModel(new DefaultComboBoxModel<String>(new String[] {"CSV", "API"}));
		
		if(prefComboBoxModeSelectedItem == 0){
			prefs.put("LadderTrackerMode", Integer.toString(0));
			comboBoxMode.setSelectedItem("CSV");
		}
		else{
			prefs.put("LadderTrackerMode", Integer.toString(1));
			comboBoxMode.setSelectedItem("API");
		}
		
		comboBoxMode.addItemListener(comboBoxListener);
	}
	/**
	 * Starts the ladder tracker process.
	 */
	private void ladderTrackerStart(){
		boolean modeCSV;
		
		if(prefComboBoxModeSelectedItem == 0){
			modeCSV = true;
		}
		else{
			modeCSV = false;
		}
		
		windowLadderTracker = new GUILadderTracker(comboBoxContent[prefComboBoxLeagueSelectedItem], modeCSV, textColor(), backgroundColor());
		windowLadderTracker.setDisplayData(prefDisplayDeathsAhead, prefDisplayExpAhead, prefDisplayExpBehind, prefDisplayExpPerHour); 
		windowLadderTracker.setCharacter(prefCharacterName);
		windowLadderTracker.setleagueID(selectedLeagueID);
		windowStartUp.dispose();
		windowLadderTracker.show();
	}
	/**
	 * Shows the startup GUI.
	 */
	public void show(){
		windowStartUp.setVisible(true);
	}
	/**
	 * Hides the startup GUI.
	 */
	public void hide(){
		windowStartUp.setVisible(false);
	}
	/**
	 * Updates the start/end date & time.
	 * 
	 * @param index - Index of the selected league/race.
	 */
	private void updateDates(){
		String start = leagueCollector.getLeagueStart().get(prefComboBoxLeagueSelectedItem);
		String end = leagueCollector.getLeagueEnd().get(prefComboBoxLeagueSelectedItem);
		textStartLeague.setText(start);
		textEndLeague.setText(end);
	}
	private class ComboBoxListener implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			if(e.getSource() == comboBoxLeague){
				for(int i = 0; i < comboBoxContent.length; i++){
					if(e.getSource() == comboBoxLeague){
						if(comboBoxLeague.getSelectedItem().equals(comboBoxContent[i])){
							selectedLeagueID = leagueID[i];
							prefs.put("LeagueNameListIndex", Integer.toString(i));
							
							loadPreferences();
							updateDates();
							updateStartButton();
							break;
						}
					}
				}
			}
			else if(e.getSource() == comboBoxMode){
				if(comboBoxMode.getSelectedItem().equals("CSV")){
					prefs.put("LadderTrackerMode", Integer.toString(0));
				}
				else{
					prefs.put("LadderTrackerMode", Integer.toString(1));
				}
				
				loadPreferences();
			}
		}	
	}
	/**
	 * Document listener for the text field.
	 * 
	 * @author Joschn
	 */
	private class TextFieldListener implements DocumentListener{
		public void changedUpdate(DocumentEvent e){
			update();
		}
		public void removeUpdate(DocumentEvent e){
			update();
		}
		public void insertUpdate(DocumentEvent e){
			update();
		}
		public void update(){
			prefCharacterName = textField.getText();
			prefs.put("CharacterName", prefCharacterName);
			loadPreferences();
		}
	};
	/**
	 * Key listener for the text field.
	 * 
	 * @author Joschn
	 */
	private class TextFieldKeyListener implements KeyListener{
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				ladderTrackerStart();
		      }
		}
		public void keyTyped(KeyEvent e) {
		}
		public void keyReleased(KeyEvent e) {
		}
	}
	/**
	 * Action listener for the start button.
	 * 
	 * @author Joschn
	 */
	private class ButtonStartListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			ladderTrackerStart();
		}
	};
	/**
	 * Action listener for the checkboxes.
	 * 
	 * @author Joschn
	 */
	private class CheckboxListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(checkboxDeathsAhead.isSelected()){
				prefDisplayDeathsAhead = true;
			}
			else{
				prefDisplayDeathsAhead = false;
			}
			if(checkboxExpAhead.isSelected()){
				prefDisplayExpAhead = true;
			}
			else{
				prefDisplayExpAhead = false;
			}
			if(checkboxExpBehind.isSelected()){
				prefDisplayExpBehind = true;
			}
			else{
				prefDisplayExpBehind = false;
			}
			if(checkboxExpPerHour.isSelected()){
				prefDisplayExpPerHour = true;
			}
			else{
				prefDisplayExpPerHour = false;
			}
			
			prefs.put("checkboxDeathsAhead", String.valueOf(prefDisplayDeathsAhead));
			prefs.put("checkboxExpAhead", String.valueOf(prefDisplayExpAhead));
			prefs.put("checkboxExpBehind", String.valueOf(prefDisplayExpBehind));
			prefs.put("checkboxExpPerHour", String.valueOf(prefDisplayExpPerHour));
		
			loadPreferences();
		}
	}
}