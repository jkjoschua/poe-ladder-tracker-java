import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The LeagueCollector object is used to get all leagues and races that are currently of interest.
 * 
 * @author Joschn
 */
public class LeagueCollector{
	private URL apiURL;
	private String json;
	private List<String> leagueNameList = new ArrayList<String>();
	private List<String> leagueThreadID = new ArrayList<String>();

	/**
	 * Constructor of the LeagueCollector object.
	 * 
	 * @throws Exception If there is an error.
	 */
	public LeagueCollector() throws Exception{
		apiURL = new URL("http://api.pathofexile.com/leagues?type=main&compact=0");
		download();
		analyze();
	}
	/**
	 * Downloads the JSON file.
	 * 
	 * @throws Exception If there is an error while downloading.
	 */
	private void download() throws Exception{
		HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();		
		InputStream inputStream = connection.getInputStream();
		
		InputStreamReader inputStreamReader = null;
	    BufferedReader bufferedReader = null;
		String line;
		
		inputStreamReader = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(inputStreamReader);
		
	    while((line = bufferedReader.readLine()) != null){
	    	json = line;
	    }
	    
	    inputStreamReader.close();
		bufferedReader.close();
		inputStream.close();
	}
	/**
	 * Analyzes the JSON file and extracts the thread IDs and names of the leagues/races.
	 */
	private void analyze(){
		int e = 0, f = 0, g = 0;		
		// every char in the json line
		for(int c = 0; c < json.length(); c++){		
			// not out of boundary
			if((json.length()-10) > c){			
				// found indicator for league name
				if(json.substring(c, c+6).equals("\"id\":\"")){
					// every next char beginning after league indicator
					for(e = c+6; e < c+6+500; e++){
						
						// if there is another " the league name can be read
						if(json.substring(e, e+1).equals("\"")){
							//System.out.println("    FOUND");
							String name = json.substring(c+6, e);
							leagueNameList.add(name);
							//System.out.println("      NAME::: " + name);
							break;
						}					
					}			
					// every next char beginning after league name
					for(f = e; f < e+500; f++){				
						// found indicator for url for that lague
						if(json.substring(f, f+7).equals("\"url\":\"")){
							// every next char beginning after url indicator
							for(g = f+7; g < c+6+500; g++){						
								// if there is another " get the url
								if(json.substring(g, g+1).equals("\"")){
									String url = json.substring(f+7, g);
									String threadID = generateThreadID(url);						
									leagueThreadID.add(threadID);
									break;
								}
							}
							break;
						}
					}
				}
			}	
		}
    }
	/**
	 * Extracts the thread ID out of an url.
	 * 
	 * @param url - URL as a string which contains the thread ID.
	 * @return The thread ID as a string.
	 */ 
	private String generateThreadID(String url){
		for(int c = url.length(); c > url.length()-15; c--){
			if(url.substring(c-1, c).equals("/")){
				return url.substring(c);
			}
		}
		return null;
	}
	/**
	 * Returns a list of the names of leagues/races.
	 * 
	 * @return List of strings containing the names of leagues/races.
	 */
	public List<String> getLeagueNameList(){
		return leagueNameList;
	}
	/**
	 * Returns a list of the thread IDs of leagues/races.
	 * 
	 * @return List of strings containing the thread IDs of leagues/races.
	 */
	public List<String> getLeagueThreadID(){
		return leagueThreadID;
	}
}