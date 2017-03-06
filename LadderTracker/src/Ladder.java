import java.io.PrintWriter;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The ladder object stores the whole ladder information and depending on the submitted character name
 * the specific character data is calculated.
 * 
 * @author Joschn
 */
public class Ladder{
	Queue<CSVFile> fullCSVSet = new ConcurrentLinkedQueue<CSVFile>();
	private CSVFile CSV1, CSV2, CSV3, CSV4, CSV5, CSV6, CSV7;
	private String csvLink, character, characterSpecificClass, reqLevel, reqExp;
	private String[] onlineStatus = new String[14000];
	private String[] characterRank = new String[14000];
	private String[] accountName = new String[14000];
	private String[] characterName = new String[14000];
	private String[] characterClass = new String[14000];
	private String[] characterLevel = new String[14000];
	private String[] characterExperience = new String[14000];
	private String[] characterDead = new String[14000];
	private long characterSpecificExperienceBehind, characterSpecificExperienceAhead;
	private boolean characterFound = false;		
	private int characterSpecificRank, characterSpecificClassRank, characterSpecificDeathsAhead;
	private int httpWaitingTime = 10000;
	private int interThreadStartTime = 2500;
	
	/**
	 * Constructor of the Ladder object.
	 * 
	 * @param initialCSVLinkBase - The base of the CSV link.
	 * @param initialCharacterName - The character name of interest.
	 * @throws Exception If there is an error.
	 */
	public Ladder(String initialCSVLinkBase, String initialCharacterName) throws Exception{
		csvLink = initialCSVLinkBase;
		character = initialCharacterName;	
		CSV1 = new CSVFile(new URL(csvLink + "1"), 1);
		CSV2 = new CSVFile(new URL(csvLink + "2"), 2);
		CSV3 = new CSVFile(new URL(csvLink + "3"), 3);
		CSV4 = new CSVFile(new URL(csvLink + "4"), 4);
		CSV5 = new CSVFile(new URL(csvLink + "5"), 5);
		CSV6 = new CSVFile(new URL(csvLink + "6"), 6);
		CSV7 = new CSVFile(new URL(csvLink + "7"), 7);
	}
	/**
	 * Download the CSV Files.
	 */
	private void downloadCSVs(){
		// thread definition for CSV file 1
		Thread T1 = new Thread(new Runnable(){
			public void run(){
				boolean finished = false;
				while(!finished){
					try {
						CSV1.update();
						fullCSVSet.add(CSV1);
						finished = true;
					} catch (Exception e) {
						System.out.println("Error downloading CSV1!");
						e.printStackTrace();
					}
					if(!finished){
						try {
							Thread.sleep(httpWaitingTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		// thread definition for CSV file 2
		Thread T2 = new Thread(new Runnable(){
			public void run(){
				boolean finished = false;
				while(!finished){
					try {
						CSV2.update();
						fullCSVSet.add(CSV2);
						finished = true;
					} catch (Exception e) {
						System.out.println("Error downloading CSV2!");
						e.printStackTrace();
					}
					if(!finished){
						try {
							Thread.sleep(httpWaitingTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		// thread definition for CSV file 3
		Thread T3 = new Thread(new Runnable(){
			public void run(){
				boolean finished = false;
				while(!finished){
					try {
						CSV3.update();
						fullCSVSet.add(CSV3);
						finished = true;
					} catch (Exception e) {
						System.out.println("Error downloading CSV3!");
						e.printStackTrace();
					}
					if(!finished){
						try {
							Thread.sleep(httpWaitingTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		// thread definitionfor CSV file 4
		Thread T4 = new Thread(new Runnable(){
			public void run(){
				boolean finished = false;
				while(!finished){
					try {
						CSV4.update();
						fullCSVSet.add(CSV4);
						finished = true;
					} catch (Exception e) {
						System.out.println("Error downloading CSV4!");
						e.printStackTrace();
					}
					if(!finished){
						try {
							Thread.sleep(httpWaitingTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		// thread definition for CSV file 5
		Thread T5 = new Thread(new Runnable(){
			public void run(){
				boolean finished = false;
				while(!finished){
					try {
						CSV5.update();
						fullCSVSet.add(CSV5);
						finished = true;
					} catch (Exception e) {
						System.out.println("Error downloading CSV5!");
						e.printStackTrace();
					}
					if(!finished){
						try {
							Thread.sleep(httpWaitingTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		// thread definitionfor CSV file 6
		Thread T6 = new Thread(new Runnable(){
			public void run(){
				boolean finished = false;
				while(!finished){
					try {
						CSV6.update();
						fullCSVSet.add(CSV6);
						finished = true;
					} catch (Exception e) {
						System.out.println("Error downloading CSV6!");
						e.printStackTrace();
					}
					if(!finished){
						try {
							Thread.sleep(httpWaitingTime);
						} catch (InterruptedException e){
							e.printStackTrace();
						}
					}
				}
			}
		});
		// thread definition for CSV file 7
		Thread T7 = new Thread(new Runnable(){
			public void run(){
				boolean finished = false;
				while(!finished){
					try {
						CSV7.update();
						fullCSVSet.add(CSV7);
						finished = true;
					} catch (Exception e) {
						System.out.println("Error downloading CSV7!");
						e.printStackTrace();
					}
					if(!finished){
						try {
							Thread.sleep(httpWaitingTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		// starting the threads
		try {
			T1.start();
			Thread.sleep(interThreadStartTime);
			T2.start();
			Thread.sleep(interThreadStartTime);
			T3.start();
			Thread.sleep(interThreadStartTime);
			T4.start();
			Thread.sleep(interThreadStartTime);
			T5.start();
			Thread.sleep(interThreadStartTime);
			T6.start();
			Thread.sleep(interThreadStartTime);
			T7.start();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// wait until all threads are done
		try {
			T1.join();
			T2.join();
			T3.join();
			T4.join();
			T5.join();
			T6.join();
			T7.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Fills the ladder with the CSV data.
	 */
	private void fill(){
		CSVFile currenCSV;
		for(int i = 1; i <= 7; i++){
			currenCSV = fullCSVSet.poll();
			addOnlineStatus(currenCSV.getListOnlineStatus(), currenCSV.getIndex());
			addCharacterRank(currenCSV.getListRank(), currenCSV.getIndex());
			addAccountName(currenCSV.getListAccount(), currenCSV.getIndex());
			addCharacterName(currenCSV.getListCharacter(), currenCSV.getIndex());
			addCharacterClass(currenCSV.getListClass(), currenCSV.getIndex());
			addCharacterlevel(currenCSV.getListLevel(), currenCSV.getIndex());
			addCharacterExperience(currenCSV.getListExperience(), currenCSV.getIndex());
			addCharacterDead(currenCSV.getListDead(), currenCSV.getIndex());
		}
	}
	/**
	 * Prints the character values in the console (for debugging).
	 */
	@SuppressWarnings("unused")
	private void print(){
		if(characterFound){		
			System.out.println("----------");
			System.out.println("Character: " + character + " is in the ladder!");
			System.out.println("----------");
			System.out.println("Rank : " + getRank());
			System.out.println("Class : " + getClassName());
			System.out.println("Class Rank : " + getClassRank());
			System.out.println("DeathsAhead : " + getDeathsAhead());
			System.out.println("ExpBehind : " + getExpBehind());
			System.out.println("ExpAhead : " + getExpAhead());
		}
		else{
			System.out.println("----------");
			System.out.println("Character: " + character + " is not in the ladder!");
			System.out.println("----------");
		}
	}
	/**
	 * Calculates the specific character data.
	 */
	private void calculateCharacerSpecificData(){
		boolean reqFound = false;
		characterFound = false;
		int ladderPos;
		for(int i = 0; i < 14000; i++){
			if(character.equals(characterName[i])){
				ladderPos = i;
				characterFound = true;
				
				if(characterDead[i].equals("Dead")){
					for(int j = i+1; j < 14000; j++){
						if(character.equals(characterName[j])){
							ladderPos = j;
							break;
						}
					}
				}
				characterSpecificRank = ladderPos+1;
				characterSpecificClass = characterClass[ladderPos];
				characterSpecificClassRank = 1;
				if(ladderPos == 0){
					characterSpecificExperienceBehind = 0;
				}
				else{
					characterSpecificExperienceBehind = Long.parseLong(characterExperience[ladderPos-1], 10)-Long.parseLong(characterExperience[ladderPos], 10);
				}
				if(ladderPos == 13999 || characterExperience[ladderPos+1] == null){
					characterSpecificExperienceAhead = 0;
				}
				else{
					characterSpecificExperienceAhead = Long.parseLong(characterExperience[ladderPos], 10)-Long.parseLong(characterExperience[ladderPos+1], 10);
				}
				characterSpecificDeathsAhead = 0;
				
				for(int j = 0; j < ladderPos; j++){
					if(characterDead[j].equals("Dead")){
						characterSpecificDeathsAhead++;
					}

					if(characterSpecificClass.equals("Marauder")){					
						if(characterClass[j].equals("Marauder") || characterClass[j].equals("Juggernaut") || characterClass[j].equals("Berserker") || characterClass[j].equals("Chieftain")){
						characterSpecificClassRank++;
						}
					}
					else if(characterSpecificClass.equals("Duelist")){
						if(characterClass[j].equals("Duelist") || characterClass[j].equals("Slayer") || characterClass[j].equals("Gladiator") || characterClass[j].equals("Champion")){
							characterSpecificClassRank++;
							}
					}
					else if(characterSpecificClass.equals("Ranger")){
						if(characterClass[j].equals("Ranger") || characterClass[j].equals("Deadeye") || characterClass[j].equals("Raider") || characterClass[j].equals("Pathfinder")){
							characterSpecificClassRank++;
							}
					}
					else if(characterSpecificClass.equals("Shadow")){
						if(characterClass[j].equals("Shadow") || characterClass[j].equals("Assassin") || characterClass[j].equals("Saboteur") || characterClass[j].equals("Trickster")){
							characterSpecificClassRank++;
							}
					}
					else if(characterSpecificClass.equals("Witch")){
						if(characterClass[j].equals("Witch") || characterClass[j].equals("Necromancer") || characterClass[j].equals("Occultist") || characterClass[j].equals("Elementalist")){
							characterSpecificClassRank++;
							}
					}
					else if(characterSpecificClass.equals("Templar")){
						if(characterClass[j].equals("Templar") || characterClass[j].equals("Inquisitor") || characterClass[j].equals("Hierophant") || characterClass[j].equals("Guardian")){
							characterSpecificClassRank++;
							}
					}
					else if(characterSpecificClass.equals("Scion")){
						if(characterClass[j].equals("Scion") || characterClass[j].equals("Ascendant")){
							characterSpecificClassRank++;
							}
					}
					else{
						if(characterClass[j].equals(characterSpecificClass)){
							characterSpecificClassRank++;
						}
					}
				}
				break;
			}
			if(!reqFound && characterName[i] == null){
				if(i > 1){
					reqLevel = characterLevel[i-1];
					reqExp = characterExperience[i-1];
				}
				else{
					reqLevel = null;
					reqExp = null;				
				}
				reqFound = true;
			}
		}
		if(!reqFound){
			reqLevel = characterLevel[13999];
			reqExp = characterExperience[13999];
		}
	}
	/**
	 * Returns the character specific rank.
	 * 
	 * @return The character rank as an integer.
	 */
	public int getRank(){
		return characterSpecificRank;
	}
	/**
	 * Returns the character class name.
	 * 
	 * @return The character class name as a string.
	 */
	public String getClassName(){
		return characterSpecificClass;
	}
	/**
	 * Returns the character class rank.
	 * 
	 * @return The character class rank as an integer.
	 */
	public int getClassRank(){
		return characterSpecificClassRank;
	}
	/**
	 * Returns the experience gap to the next character.
	 * 
	 * @return The experience gap as a long value.
	 */
	public long getExpBehind(){
		return characterSpecificExperienceBehind;
	}
	/**
	 * Returns the experience gap to character behind.
	 * 
	 * @return The experience gap as a long value.
	 */
	public long getExpAhead(){
		return characterSpecificExperienceAhead;
	}
	/**
	 * Returns the number of characters which are dead in front.
	 * 
	 * @return The number of dead characters as an integer.
	 */
	public int getDeathsAhead(){
		return characterSpecificDeathsAhead;
	}
	/**
	 * Returns if the specific character was found.
	 * 
	 * @return Boolean value if the character was found.
	 */
	public boolean isCharacterFound(){
		return characterFound;
	}
	/**
	 * Returns the required level to be listed in the ladder.
	 * 
	 * @return The required level as a string.
	 */
	public String getRequiredLevel(){
		return reqLevel;
	}
	/**
	 * Returns the required experience to be listed in the ladder.
	 * 
	 * @return The required experience as a string.
	 */
	public String getRequiredExp(){
		return reqExp;
	}
	/**
	 * Adds the CSV information of the online status to the ladder.
	 * 
	 * @param s - The list of the online status.
	 * @param index - The index of the CSV file.
	 */
	private void addOnlineStatus(String[] s, int index){
		for(int i = 0; i < 2000; i++){
			onlineStatus[i+(index-1)*2000] = s[i];
		}
	}
	/**
	 * Adds the CSV information of the character ranks to the ladder.
	 * 
	 * @param s - The list of the character ranks.
	 * @param index - The index of the CSV file.
	 */
	private void addCharacterRank(String[] s, int index){
		for(int i = 0; i < 2000; i++){
			characterRank[i+(index-1)*2000] = s[i];
		}
	}
	/**
	 * Adds the CSV information of the account names to the ladder.
	 * 
	 * @param s - The list of the account names.
	 * @param index - The index of the CSV file.
	 */
	private void addAccountName(String[] s, int index){
		for(int i = 0; i < 2000; i++){
			accountName[i+(index-1)*2000] = s[i];
		}
	}
	/**
	 * Adds the CSV information of the character names to the ladder.
	 * 
	 * @param s - The list of the character names.
	 * @param index - The index of the CSV file.
	 */
	private void addCharacterName(String[] s, int index){
		for(int i = 0; i < 2000; i++){
			characterName[i+(index-1)*2000] = s[i];
		}
	}
	/**
	 * Adds the CSV information of the character classes to the ladder.
	 * 
	 * @param s - The list of the character classes.
	 * @param index - The index of the CSV file.
	 */
	private void addCharacterClass(String[] s, int index){
		for(int i = 0; i < 2000; i++){
			characterClass[i+(index-1)*2000] = s[i];
		}
	}
	/**
	 * Adds the CSV information of the character levels to the ladder.
	 * 
	 * @param s - The list of the character levels.
	 * @param index - The index of the CSV file.
	 */
	private void addCharacterlevel(String[] s, int index){
		for(int i = 0; i < 2000; i++){
			characterLevel[i+(index-1)*2000] = s[i];
		}
	}
	/**
	 * Adds the CSV information of the character experiences to the ladder.
	 * 
	 * @param s - The list of the chracter experiences.
	 * @param index - The index of the CSV file.
	 */
	private void addCharacterExperience(String[] s, int index){
		for(int i = 0; i < 2000; i++){
			characterExperience[i+(index-1)*2000] = s[i];
		}
	}
	/**
	 * Adds the CSV information of the dead status to the ladder.
	 * 
	 * @param s - The list of the dead status.
	 * @param index - The index of the CSV file.
	 */
	private void addCharacterDead(String[] s, int index){
		for(int i = 0; i < 2000; i++){
			characterDead[i+(index-1)*2000] = s[i];
		}
	}
	/**
	 * Updates the ladder and character specific data.
	 * 
	 * @throws Exception If there is an error while updating the ladder.
	 */
	public void update() throws Exception{
		downloadCSVs();
		fill();
		calculateCharacerSpecificData();
		//print(); // for debugging
		//export(); // for debugging
	}
	/**
	 * Exports the current ladder information to a ladder.txt file (for debugging).
	 * 
	 * @throws Exception If there is an error while exporting the ladder information.
	 */
	public void export() throws Exception{
		PrintWriter writer = new PrintWriter ("ladder"); 
	    String s;
	    for(int i = 0; i < 14000; i++){
	    	s = onlineStatus[i] + "," + characterRank[i] + "," + accountName[i] + "," + characterName[i] + "," + characterClass[i] + "," + characterLevel[i] + "," + characterExperience[i] + "," + characterDead[i];
	    	writer.println(s);
	    }
	    writer.close();
	}
}