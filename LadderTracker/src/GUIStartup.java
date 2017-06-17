import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
	private JComboBox<String> comboBox, comboBoxMode;
	private JTextField textField;
	private JTextPane textLeague, textCharacter, textDisplayAlso, textStart, textEnd, textStartLeague, textEndLeague, textMode, textLine;
	private JButton buttonStart;
	private String[] comboBoxContent, leagueID;
	private String selectedLeagueID, characterName, prefCharacterName, prefComboBoxSelectedItem;
	private ComboBoxListener comboBoxListener = new ComboBoxListener();
	private ComboBoxModeListener comboBoxModeListener = new ComboBoxModeListener();	
	private TextFieldListener textFieldListener = new TextFieldListener();
	private TextFieldKeyListener textFieldKeyListener = new TextFieldKeyListener();
	private ButtonStartListener buttonStartListener = new ButtonStartListener();
	private CheckboxListener checkboxListener = new CheckboxListener();
	private GUILadderTracker windowLadderTracker;
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	private JCheckBox checkboxExpBehind, checkboxExpAhead, checkboxExpPerHour, checkboxDeathsAhead;
	private Boolean displayExpBehind, displayExpAhead, displayExpPerHour, displayDeathsAhead;
	private LeagueCollector leagueCollector;
	private boolean modeCSV;

	/**
	 * Constructor of the GUIStartup object.
	 */
	public GUIStartup(){
//		try {
//			prefs.clear();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		prefCharacterName = prefs.get("CharacterName", "");
		prefComboBoxSelectedItem = prefs.get("LeagueNameListIndex", "0");
		characterName = prefCharacterName;
		
		initialize();
	}
	/**
	 * Initializes the startup GUI.
	 */
	private void initialize(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// start up window
		windowStartUp = new JFrame();	
		windowStartUp.setBounds(100, 100, 300, 271);
		windowStartUp.setLocation(dim.width/2-windowStartUp.getSize().width/2, dim.height/2-windowStartUp.getSize().height/2);
		windowStartUp.setResizable(false);
		windowStartUp.setTitle("Ladder Tracker v2.1");
		windowStartUp.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		windowStartUp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowStartUp.getContentPane().setLayout(null);
		
		// text league
		textLeague = new JTextPane();
		textLeague.setEditable(false);
		textLeague.setBackground(UIManager.getColor("CheckBox.background"));
		textLeague.setText("League");
		textLeague.setBounds(10, 11, 56, 20);
		windowStartUp.getContentPane().add(textLeague);
		
		// combo box
		comboBox = new JComboBox<String>();
		comboBox.setBounds(74, 11, 210, 20);
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
				comboBox.setModel(new DefaultComboBoxModel<String>(comboBoxContent));
				
				if(Integer.parseInt(prefs.get("LeagueNameListIndex", "0")) < leagueNameList.size()){
					selectedLeagueID = leagueID[Integer.parseInt(prefComboBoxSelectedItem)];
					comboBox.setSelectedItem(comboBoxContent[Integer.parseInt(prefComboBoxSelectedItem)]);
				}
				else{
					selectedLeagueID = leagueID[0];
					comboBox.setSelectedItem(comboBoxContent[0]);
				}				
				comboBox.addItemListener(comboBoxListener);
				break;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			counter++;
			if(counter == 10){
				counter = 0;
				GUIError windowsError = new GUIError();
				windowsError.show();
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		windowStartUp.getContentPane().add(comboBox);
		
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
		textField.setText(characterName);
		textField.setColumns(10);
		textField.getDocument().addDocumentListener(textFieldListener);
		windowStartUp.getContentPane().add(textField);

		// button start
		buttonStart = new JButton("Start");
		buttonStart.setBounds(10, 209, 274, 23);
		buttonStart.addActionListener(buttonStartListener);
		windowStartUp.getContentPane().add(buttonStart);
		
		if(selectedLeagueID.equals("null")){
			buttonStart.setEnabled(false);
			buttonStart.setText("Unable to track right now!");
		}
		else{
			buttonStart.setEnabled(true);
			buttonStart.setText("Start");
		}
		
		// text DisplayAlso
		textDisplayAlso = new JTextPane();
		textDisplayAlso.setText("Display also:");
		textDisplayAlso.setEditable(false);
		textDisplayAlso.setBackground(SystemColor.menu);
		textDisplayAlso.setBounds(10, 103, 66, 20);
		windowStartUp.getContentPane().add(textDisplayAlso);
		
		// checkbox DeathsAhead
		checkboxDeathsAhead = new JCheckBox("DeahtsAhead");
		checkboxDeathsAhead.setBounds(74, 102, 97, 23);
		displayDeathsAhead = Boolean.valueOf(prefs.get("checkboxDeathsAhead", "true"));
		checkboxDeathsAhead.setSelected(displayDeathsAhead);
		checkboxDeathsAhead.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxDeathsAhead);
		
		// checkbox ExpBehind
		checkboxExpBehind = new JCheckBox("ExpBehind");
		checkboxExpBehind.setBounds(187, 102, 97, 23);
		displayExpBehind = Boolean.valueOf(prefs.get("checkboxExpBehind", "true"));
		checkboxExpBehind.setSelected(displayExpBehind);
		checkboxExpBehind.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpBehind);
		
		// checkbox ExpAhead
		checkboxExpAhead = new JCheckBox("ExpAhead");
		checkboxExpAhead.setBounds(187, 128, 97, 23);
		displayExpAhead = Boolean.valueOf(prefs.get("checkboxExpAhead", "true"));
		checkboxExpAhead.setSelected(displayExpAhead);
		checkboxExpAhead.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpAhead);
		
		// checkbox ExpPerHour
		checkboxExpPerHour = new JCheckBox("Exp/h + Progress");
		checkboxExpPerHour.setBounds(74, 128, 109, 23);
		displayExpPerHour = Boolean.valueOf(prefs.get("checkboxExpPerHour", "true"));
		checkboxExpPerHour.setSelected(displayExpPerHour);
		checkboxExpPerHour.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpPerHour);
		
		// text Start
		textStart = new JTextPane();
		textStart.setText("Start  :");
		textStart.setEditable(false);
		textStart.setBackground(SystemColor.menu);
		textStart.setBounds(76, 32, 40, 20);
		windowStartUp.getContentPane().add(textStart);
		
		// text End
		textEnd = new JTextPane();
		textEnd.setText("End    :");
		textEnd.setEditable(false);
		textEnd.setBackground(SystemColor.menu);
		textEnd.setBounds(76, 48, 40, 20);
		windowStartUp.getContentPane().add(textEnd);
		
		// text of the start date
		textStartLeague = new JTextPane();
		textStartLeague.setEditable(false);
		textStartLeague.setBackground(SystemColor.menu);
		textStartLeague.setBounds(120, 32, 164, 20);
		windowStartUp.getContentPane().add(textStartLeague);
		
		// text of the end date
		textEndLeague = new JTextPane();
		textEndLeague.setEditable(false);
		textEndLeague.setBackground(SystemColor.menu);
		textEndLeague.setBounds(120, 48, 164, 20);
		windowStartUp.getContentPane().add(textEndLeague);
		
		textMode = new JTextPane();
		textMode.setText("Mode:");
		textMode.setEditable(false);
		textMode.setBackground(SystemColor.menu);
		textMode.setBounds(10, 178, 47, 20);
		windowStartUp.getContentPane().add(textMode);
		
		textLine = new JTextPane();
		textLine.setText("______________________________________________________________");
		textLine.setEditable(false);
		textLine.setBackground(SystemColor.menu);
		textLine.setBounds(10, 147, 274, 20);
		windowStartUp.getContentPane().add(textLine);
		
		comboBoxMode = new JComboBox<String>();
		comboBoxMode.setModel(new DefaultComboBoxModel<String>(new String[] {"CSV", "API"}));
		comboBoxMode.setBounds(74, 178, 210, 20);
		windowStartUp.getContentPane().add(comboBoxMode);
		comboBoxMode.addItemListener(comboBoxModeListener);
		
		if(Integer.parseInt(prefs.get("LadderTrackerMode", "0")) == 0){
			modeCSV = true;
			prefs.put("LadderTrackerMode", Integer.toString(0));
			comboBoxMode.setSelectedItem("CSV");
		}
		else{
			modeCSV = false;
			prefs.put("LadderTrackerMode", Integer.toString(1));
			comboBoxMode.setSelectedItem("API");
		}
		
		// update the start and end text
		updateDates(Integer.parseInt(prefComboBoxSelectedItem));
		
	}
	/**
	 * Starts the ladder tracker process.
	 */
	private void ladderTrackerStart(){
		windowLadderTracker = new GUILadderTracker(comboBoxContent[Integer.parseInt(prefs.get("LeagueNameListIndex", "0"))], modeCSV);
		windowLadderTracker.setDisplayData(displayDeathsAhead, displayExpAhead, displayExpBehind, displayExpPerHour);
		windowLadderTracker.setCharacter(characterName);
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
	 * Updates the start/end date & time.
	 * 
	 * @param index - Index of the selected league/race.
	 */
	private void updateDates(int index){
		String start = leagueCollector.getLeagueStart().get(index);
		String end = leagueCollector.getLeagueEnd().get(index);
		textStartLeague.setText(start);
		textEndLeague.setText(end);
	}
	/**
	 * Item listener for the combo box.
	 * 
	 * @author Joschn
	 */
	private class ComboBoxListener implements ItemListener{
		public void itemStateChanged(ItemEvent e) {
			for(int i = 0; i < comboBoxContent.length; i++){
				if(e.getSource() == comboBox){
					if(comboBox.getSelectedItem().equals(comboBoxContent[i])){
						selectedLeagueID = leagueID[i];
						prefs.put("LeagueNameListIndex", Integer.toString(i));
						updateDates(i);
						
						if(selectedLeagueID.equals("null")){
							buttonStart.setEnabled(false);
							buttonStart.setText("Unable to track right now!");
						}
						else{
							buttonStart.setEnabled(true);
							buttonStart.setText("Start");
						}
						break;
					}
				}
			}
		}	
	}
	private class ComboBoxModeListener implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			if(e.getSource() == comboBoxMode){
				if(comboBoxMode.getSelectedItem().equals("CSV")){
					modeCSV = true;
					prefs.put("LadderTrackerMode", Integer.toString(0));
				}
				else{
					modeCSV = false;
					prefs.put("LadderTrackerMode", Integer.toString(1));
				}
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
			characterName = textField.getText();
			prefs.put("CharacterName", characterName);
		}
	};
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
				displayDeathsAhead = true;
			}
			else{
				displayDeathsAhead = false;
			}
			if(checkboxExpAhead.isSelected()){
				displayExpAhead = true;
			}
			else{
				displayExpAhead = false;
			}
			if(checkboxExpBehind.isSelected()){
				displayExpBehind = true;
			}
			else{
				displayExpBehind = false;
			}
			if(checkboxExpPerHour.isSelected()){
				displayExpPerHour = true;
			}
			else{
				displayExpPerHour = false;
			}
			
			prefs.put("checkboxDeathsAhead", String.valueOf(displayDeathsAhead));
			prefs.put("checkboxExpAhead", String.valueOf(displayExpAhead));
			prefs.put("checkboxExpBehind", String.valueOf(displayExpBehind));
			prefs.put("checkboxExpPerHour", String.valueOf(displayExpPerHour));
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
}