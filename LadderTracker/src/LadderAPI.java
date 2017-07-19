import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;

public class LadderAPI{
	private String[] characterRank = new String[14000], characterName = new String[14000], characterClass = new String[14000], characterExperience = new String[14000], characterDead = new String[14000], characterLevel = new String[14000];
	private String character, characterSpecificClass, reqLevel, reqExp;
	private int characterSpecificClassRank, characterSpecificDeathsAhead, characterSpecificExpPerHourTimestampLow, characterSpecificExpPerHourTimestampHigh, timeoutHTTPConnection = 60000;
	private long characterSpecificExperienceBehind, characterSpecificExperienceAhead, characterSpecificExpPerHourReferenceLow = 0, characterSpecificExpPerHourReferenceHigh = 0;
	private double characterSpecificProgress;
	private boolean characterFound = false, characterFoundExtern = false;
	private String leagName;
	private int characterSpecificRank = 99999;
	
	public LadderAPI(String initialCharacterName, String initialLeagName){
		leagName = initialLeagName;
		character = initialCharacterName;
	}
	public void update() throws IOException, InterruptedException{

		characterFound = false;
		characterFoundExtern = false;
		characterSpecificRank = 99999;
		
		LadderDownloadThread T0 = new LadderDownloadThread(0, leagName);
		LadderDownloadThread T1 = new LadderDownloadThread(1, leagName);
		LadderDownloadThread T2 = new LadderDownloadThread(2, leagName);
		LadderDownloadThread T3 = new LadderDownloadThread(3, leagName);
		LadderDownloadThread T4 = new LadderDownloadThread(4, leagName);
		
		T0.start();
		T1.start();
		T2.start();
		T3.start();
		T4.start();		
		
		T0.join();
		T1.join();
		T2.join();
		T3.join();
		T4.join();
		
//		if(characterFound){
//			searchCharacter(character, 0, characterSpecificRank-1);
//		}
		
		calculateCharacerSpecificData();
	}
	@SuppressWarnings("unused")
	private void export() throws Exception{
		PrintWriter writer = new PrintWriter ("ladder"); 
	    String s;
	    for(int i = 0; i < 14000; i++){
	    	s = characterRank[i] + "," + characterName[i] + "," + characterClass[i] + "," + characterLevel[i] + "," + characterExperience[i];
	    	writer.println(s);
	    }
	    writer.close();
	}
	private void importLadderSection(String jsonLadder, int ladderOffest){
		
		int c, d, e, f, g, entry = 0, length = jsonLadder.length();
		
		for(c = 0; c < length-7; c++){
			// search for rank indicator
			if(jsonLadder.substring(c, c+7).equals("\"rank\":")){
				
				// save rank
				String tmpCharacterRank = jsonLadder.substring(c+7, subStringEnd(jsonLadder, c+7));
				characterRank[entry+ladderOffest] = tmpCharacterRank;
				
				for(d = c+6; d < length; d++){
					// search for dead indicator
					if(jsonLadder.substring(d, d+7).equals("\"dead\":")){
						
						// save dead
						String tmpCharacterDead = jsonLadder.substring(d+7, subStringEnd(jsonLadder, d+7));
						characterDead[entry+ladderOffest] = tmpCharacterDead;
						break;
					}
				}
				
				for(e = d+6; e < length; e++){
					// search for character name indicator
					if(jsonLadder.substring(e, e+20).equals("\"character\":{\"name\":")){
								
						// save character name
						String tmpCharacterName = jsonLadder.substring(e+20+1, subStringEnd(jsonLadder, e+20+1)-1);
						characterName[entry+ladderOffest] = tmpCharacterName;
						
						// save level
						String tmpCharacterLevel = jsonLadder.substring(subStringEnd(jsonLadder, e+20+1)+9, subStringEnd(jsonLadder, subStringEnd(jsonLadder, e+20+1)+8));
						characterLevel[entry+ladderOffest] = tmpCharacterLevel;
						break;
					}
				}
				
				for(f = e+20; f < length; f++){
					// search for character class
					if(jsonLadder.substring(f, f+8).equals("\"class\":")){
									
						// save class
						String tmpCharacterClass = jsonLadder.substring(f+8+1, subStringEnd(jsonLadder, f+8+1)-1);
						characterClass[entry+ladderOffest] = tmpCharacterClass;
						break;
					}
				}
									
				for(g = f+8; g < length; g++){
					// search for character experience				
					if(jsonLadder.substring(g, g+13).equals("\"experience\":")){
											
						// save class
						String tmpCharacterExperience = jsonLadder.substring(g+13, subStringEnd(jsonLadder, g+13)-1);
						characterExperience[entry+ladderOffest] = tmpCharacterExperience;
						c = subStringEnd(jsonLadder, g+13);
						entry++;
						break;
					}				
				}
			}
		}
	}
	private int subStringEnd(String jsonLadder, int offset){
		int x;
		for(x = offset; offset < jsonLadder.length(); x++){	
			if(jsonLadder.substring(x, x+1).equals(",")){
				return x;
			}
		}
		return -1;
	}
	private void searchCharacter(String name, int start, int end){
		for(int x = start; x < end; x++){
			if(characterName[x].equals(name) && (x+1 < characterSpecificRank)){
				characterFound = true;
				characterSpecificRank = x+1;
			}
		}
	}
	@SuppressWarnings("unused")
	private void printLadder(int start, int end){
		for(int x = start; x < end; x++){
			System.out.println(characterRank[x] + "," + characterName[x] + "," + characterClass[x] + "," + characterLevel[x] + "," + characterExperience[x]);
		}
	}
	private void calculateCharacerSpecificData(){
		
		if(!characterFound){
			reqLevel = null;
			reqExp = null;
			
			for(int j = 0; j < 14000; j++){
				if(characterName[j] == null && j > 1){
					reqLevel = characterLevel[j-1];
					reqExp = characterExperience[j-1];
				}
				
				if(characterName[j] != null && j == 13999){
					reqLevel = characterLevel[j];
					reqExp = characterExperience[j];
				}
			}
			return;
		}
		
		int ladderIndex = characterSpecificRank-1;
		characterSpecificClass = characterClass[ladderIndex];
		characterSpecificProgress = ((double) Long.parseLong(characterExperience[ladderIndex])/4250334444L)*100;
		
		if(characterSpecificExpPerHourReferenceLow == 0){
			characterSpecificExpPerHourTimestampLow = (int) new Date().getTime();
			characterSpecificExpPerHourReferenceLow = Long.parseLong(characterExperience[ladderIndex]);
			characterSpecificExpPerHourReferenceHigh = 0;
		}
		else{
			characterSpecificExpPerHourReferenceHigh = Long.parseLong(characterExperience[ladderIndex]);
			characterSpecificExpPerHourTimestampHigh = (int) new Date().getTime();
		}
		
		if(ladderIndex == 0){
			characterSpecificExperienceBehind = 0;
		}
		else{
			characterSpecificExperienceBehind = Long.parseLong(characterExperience[ladderIndex-1], 10)-Long.parseLong(characterExperience[ladderIndex], 10);
		}
		if(ladderIndex == 13999 || characterExperience[ladderIndex+1] == null){
			characterSpecificExperienceAhead = 0;
		}
		else{
			characterSpecificExperienceAhead = Long.parseLong(characterExperience[ladderIndex], 10)-Long.parseLong(characterExperience[ladderIndex+1], 10);
		}
		
		characterSpecificClassRank = 1;
		characterSpecificDeathsAhead = 0;
		
		for(int j = 0; j < ladderIndex; j++){
			if(characterDead[j].equals("true")){
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
		if(characterFound){
			characterFoundExtern = true;
		}
	}
	public String getExpPerHour(){
		if(characterSpecificExpPerHourReferenceLow == 0 || characterSpecificExpPerHourReferenceHigh == 0){
			return "next update";
		}
		else{
			long exp = characterSpecificExpPerHourReferenceHigh-characterSpecificExpPerHourReferenceLow;
			int time = (characterSpecificExpPerHourTimestampHigh-characterSpecificExpPerHourTimestampLow)/1000;
			int expPerHour = (int) (exp/time)*60*60;			
			double tmp;
			DecimalFormat decimalFormat = new DecimalFormat("##0.0");
			
			if(expPerHour < 999){
				tmp = (double) expPerHour;
				return  Integer.toString(expPerHour);
			}
			else if(expPerHour < 999999){
				tmp = (double) expPerHour/1000;
				return decimalFormat.format(tmp) + "K";
			}
			else{
				tmp = (double) expPerHour/1000000;
				return decimalFormat.format(tmp) + "M";
			}
		}
	}
	public String getProgress(){
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		return decimalFormat.format(characterSpecificProgress);
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
		return characterFoundExtern;
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
	 * Rests the reference point for the calculation of the experience per hour.
	 */
	public void resetExpHour(){
		characterSpecificExpPerHourReferenceLow = 0;
	}
	private class LadderDownloadThread extends Thread implements Runnable{
		private int offset;
		private String league;
		private final String USER_AGENT = "Mozilla/5.0";
		
		public LadderDownloadThread(int startOffset, String startleague){
			offset = startOffset;
			league = startleague;
		}
		@SuppressWarnings("resource")
		public void run(){
			int start, end, diff;
			URL apiURL;
			HttpURLConnection connection;
			InputStream inputStream;
			InputStreamReader inputStreamReader;
		    BufferedReader bufferedReader;
			String api, line, currentSection;
			boolean done = false;
			
			for(int x = offset; x < 70; x = x+5){
				done = false;
						
				while(!done){
					start = (int) new Date().getTime();
					
					api = "http://api.pathofexile.com/ladders?id=" + league + "&offset=" + Integer.toString(200*x) + "&limit=200";
					try {
						apiURL = new URL(api);
						connection = (HttpURLConnection) apiURL.openConnection();	
						connection.setRequestProperty("User-Agent", USER_AGENT);
						connection.getResponseCode();
						connection.setConnectTimeout(timeoutHTTPConnection);	
						inputStream = connection.getInputStream();
						inputStreamReader = null;
					    bufferedReader = null;
					    currentSection = null;
						inputStreamReader = new InputStreamReader(inputStream);
						bufferedReader = new BufferedReader(inputStreamReader);
						
					    while((line = bufferedReader.readLine()) != null){
					    	currentSection = line;
					    }
					    
					    inputStreamReader.close();
						bufferedReader.close();
						inputStream.close();
						
						importLadderSection(currentSection, 200*x);
						end = (int) new Date().getTime();
						diff = end-start;
						
						if(diff < 1000){
							done = true;
							Thread.sleep(1000-diff);
						}
						
					} catch (Exception e) {
						done = false;
						e.printStackTrace();
					}
					if(!done){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if(characterFound){
					if(characterSpecificRank < x*200){
						break;
					}
				}
				else{
					searchCharacter(character, 200*x, 200*(x+1));
					if(characterFound && characterSpecificRank < x*200){
						break;
					}
				}
			}
		}
	};
}