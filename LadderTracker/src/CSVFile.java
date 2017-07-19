import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The CSVFile object can be used to import a specific CSV file.
 * 
 * @author Joschn
 */
public class CSVFile{
	private URL url;
	private int index;
	private int lines;
	private String[] digitalCSV = new String[2000];
	private String[] listOnlineStatus = new String[2000];
	private String[] listRank = new String[2000];
	private String[] listAccount = new String[2000];
	private String[] listCharacter = new String[2000];
	private String[] listClass = new String[2000];
	private String[] listLevel = new String[2000];
	private String[] listExperience = new String[2000];
	private String[] listDead = new String[2000];
	private final String USER_AGENT = "Mozilla/5.0";
	
	/**
	 * Constructor of the CSVFile object.
	 * 
	 * @param initialCSVURLBase - The base of the CSV file URL.
	 * @param initialIndex - The index of the CSV file.
	 */
	public CSVFile(URL initialCSVURLBase, int initialIndex){
		url = initialCSVURLBase;
		index = initialIndex;
	}
	/**
	 * Downlaods the CSV file and stores it in a temporary variable.
	 * 
	 * @throws Exception If there is an error while downloading.
	 */
	private void download() throws Exception{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.getResponseCode();
		connection.setConnectTimeout(60000);
		
		InputStream inputStream = connection.getInputStream();
		
		InputStreamReader inputStreamReader = null;
	    BufferedReader bufferedReader = null;
		String line;
		
		inputStreamReader = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(inputStreamReader);
		
	    int lineNumber = 0;
	    while((line = bufferedReader.readLine()) != null){
			if(lineNumber > 0){
				digitalCSV[lineNumber-1] = line;
			}
			lineNumber ++;
	   }
	    lines = lineNumber-1;
	    inputStreamReader.close();
		bufferedReader.close();
		inputStream.close();
	}
	/**
	 * Converts the CSV file in way that the information is splitted in lists.
	 */
	private void convert(){
		// every line in the CSV
		for(int l = 0; l < lines; l++){
			int countComma = 0, offsetComma = 0, exit = 0;
			// every char in the line
			for(int c = 0; c <= digitalCSV[l].length()-1; c++){
				// if there is a ','
				if(digitalCSV[l].charAt(c) == ','){
					// depending on the actual comma number a specific information is read
					switch(countComma){
						// status
						case 0:	listOnlineStatus[l] = digitalCSV[l].substring(0+offsetComma, c);
								break;
						// rank
						case 1: listRank[l] = digitalCSV[l].substring(0+offsetComma+1, c);
								break;
						// account
						case 2: listAccount[l] = digitalCSV[l].substring(0+offsetComma+1, c);
								break;
						// character
						case 3: listCharacter[l] = digitalCSV[l].substring(0+offsetComma+1, c);
								break;
						// class
						case 4: listClass[l] = digitalCSV[l].substring(0+offsetComma+1, c);
								break;
						// level
						case 5: listLevel[l] = digitalCSV[l].substring(0+offsetComma+1, c);
								break;
						// experience + dead
						case 6: listExperience[l] = digitalCSV[l].substring(0+offsetComma+1, c);
								if(c+1 == digitalCSV[l].length()){
									listDead[l] = "";
								}
								else{
									listDead[l] = "Dead";
								}
								exit = 1;
								break;
						// error
						default: exit = 1;
								 break;
					}
					offsetComma = c;
					countComma++;
					
					if(exit == 1){
						break;
					}
				}
			}
		}
	}
	/**
	 * Returns the list of the online status.
	 * 
	 * @return The list of the online status in a string array.
	 */
	public String[] getListOnlineStatus(){
		return listOnlineStatus;
	}
	/**
	 * Returns the list of the ranks.
	 * 
	 * @return The list of the ranks in a string array.
	 */
	public String[] getListRank(){
		return listRank;
	}
	/**
	 * Returns the list of the account names.
	 * 
	 * @return The list of the account names in a string array.
	 */
	public String[] getListAccount(){
		return listAccount;
	}
	/**
	 * Returns the list of the character names.
	 * 
	 * @return The list of the character names in a string array.
	 */
	public String[] getListCharacter(){
		return listCharacter;
	}
	/**
	 * Return the list of the classes.
	 * 
	 * @return The list of the classes in a string array
	 */
	public String[] getListClass(){
		return listClass;
	}
	/**
	 * Returns the list of the levels.
	 * 
	 * @return The list of the levels in a string array.
	 */
	public String[] getListLevel(){
		return listLevel;
	}
	/**
	 * Returns the list of the experiences.
	 * 
	 * @return The list of the experiences in a string array.
	 */
	public String[] getListExperience(){
		return listExperience;
	}
	/**
	 * Returns the list of the dead status.
	 * 
	 * @return The list of the dead status in a string array.
	 */
	public String[] getListDead(){
		return listDead;
	}
	/**
	 * Returns the CSV file index.
	 * 
	 * @return The CSV file index as an integer.
	 */
	public int getIndex(){
		return index;
	}
	/**
	 * Starts the update process for the CSV file (download and import).
	 * 
	 * @throws Exception If there is an error while downloading.
	 */
	public void update() throws Exception{
		download();
		convert();
	}
}