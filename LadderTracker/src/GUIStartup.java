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
	private JComboBox<String> comboBox;
	private JTextField textField;
	private JTextPane textLeague, textCharacter, textDisplayAlso;
	private JButton buttonStart;
	private String[] comboBoxContent, leagueID;
	private String selectedLeagueID, characterName, prefCharacterName, prefComboBoxSelectedItem;
	private ComboBoxListener comboBoxListener = new ComboBoxListener();	
	private TextFieldListener textFieldListener = new TextFieldListener();
	private TextFieldKeyListener textFieldKeyListener = new TextFieldKeyListener();
	private ButtonStartListener buttonStartListener = new ButtonStartListener();
	private CheckboxListener checkboxListener = new CheckboxListener();
	private GUILadderTracker windowLadderTracker;
	private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	private JCheckBox checkboxExpBehind, checkboxExpAhead, checkboxExpPerHour, checkboxDeathsAhead;
	private Boolean displayExpBehind, displayExpAhead, displayExpPerHour, displayDeathsAhead;

	/**
	 * Constructor of the GUIStartup object.
	 */
	public GUIStartup(){
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
		windowStartUp.setBounds(100, 100, 300, 188);
		windowStartUp.setLocation(dim.width/2-windowStartUp.getSize().width/2, dim.height/2-windowStartUp.getSize().height/2);
		windowStartUp.setResizable(false);
		windowStartUp.setTitle("Ladder Tracker v2.0");
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
				LeagueCollector leagueCollector = new LeagueCollector();
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
			if(counter == 20){
				GUIError windowsError = new GUIError();
				windowsError.show();
			}
		}
		windowStartUp.getContentPane().add(comboBox);
		
		// text character
		textCharacter = new JTextPane();
		textCharacter.setEditable(false);
		textCharacter.setText("Character");
		textCharacter.setBackground(SystemColor.menu);
		textCharacter.setBounds(10, 42, 56, 20);
		windowStartUp.getContentPane().add(textCharacter);
		
		// text field for the character name
		textField = new JTextField();
		textField.addKeyListener(textFieldKeyListener);
		textField.setBounds(74, 42, 210, 20);
		textField.setText(characterName);
		textField.setColumns(10);
		textField.getDocument().addDocumentListener(textFieldListener);
		windowStartUp.getContentPane().add(textField);

		// button start
		buttonStart = new JButton("Start");
		buttonStart.setBounds(10, 130, 274, 23);
		buttonStart.addActionListener(buttonStartListener);
		windowStartUp.getContentPane().add(buttonStart);
		
		// text DisplayAlso
		textDisplayAlso = new JTextPane();
		textDisplayAlso.setText("Display also:");
		textDisplayAlso.setEditable(false);
		textDisplayAlso.setBackground(SystemColor.menu);
		textDisplayAlso.setBounds(10, 75, 66, 20);
		windowStartUp.getContentPane().add(textDisplayAlso);
		
		// checkbox DeathsAhead
		checkboxDeathsAhead = new JCheckBox("DeahtsAhead");
		checkboxDeathsAhead.setBounds(74, 74, 97, 23);
		displayDeathsAhead = Boolean.valueOf(prefs.get("checkboxDeathsAhead", "true"));
		checkboxDeathsAhead.setSelected(displayDeathsAhead);
		checkboxDeathsAhead.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxDeathsAhead);
		
		// checkbox ExpBehind
		checkboxExpBehind = new JCheckBox("ExpBehind");
		checkboxExpBehind.setBounds(187, 74, 97, 23);
		displayExpBehind = Boolean.valueOf(prefs.get("checkboxExpBehind", "true"));
		checkboxExpBehind.setSelected(displayExpBehind);
		checkboxExpBehind.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpBehind);
		
		// checkbox ExpAhead
		checkboxExpAhead = new JCheckBox("ExpAhead");
		checkboxExpAhead.setBounds(187, 100, 97, 23);
		displayExpAhead = Boolean.valueOf(prefs.get("checkboxExpAhead", "true"));
		checkboxExpAhead.setSelected(displayExpAhead);
		checkboxExpAhead.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpAhead);
		
		// checkbox ExpPerHour
		checkboxExpPerHour = new JCheckBox("Exp/h + Progress");
		checkboxExpPerHour.setBounds(74, 100, 109, 23);
		displayExpPerHour = Boolean.valueOf(prefs.get("checkboxExpPerHour", "true"));
		checkboxExpPerHour.setSelected(displayExpPerHour);
		checkboxExpPerHour.addActionListener(checkboxListener);
		windowStartUp.getContentPane().add(checkboxExpPerHour);
	}
	/**
	 * Starts the ladder tracker process.
	 */
	private void ladderTrackerStart(){
		windowLadderTracker = new GUILadderTracker();
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
						break;
					}
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