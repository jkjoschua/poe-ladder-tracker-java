import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

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
	private List<String> leagueStart = new ArrayList<String>();
	private List<String> leagueEnd = new ArrayList<String>();

	/**
	 * Constructor of the LeagueCollector object.
	 * 
	 * @throws Exception If there is an error.
	 */
	public LeagueCollector() throws Exception{
		Thread.sleep(3000);
		apiURL = new URL("http://api.pathofexile.com/leagues?type=main&compact=0");
		download();
		//analyze();
		analyzeNEW();
		convertDates();
	}
	/**
	 * Downloads the JSON file.
	 * 
	 * @throws Exception If there is an error while downloading.
	 */
	private void download() throws Exception{
		HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		InputStream inputStream = connection.getInputStream();
		
		InputStreamReader inputStreamReader = null;
	    BufferedReader bufferedReader = null;
		String line;
		
		inputStreamReader = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(inputStreamReader);
		
	    while((line = bufferedReader.readLine()) != null){
	    	json = line;
	    }
	    
	    System.out.println(json);
	    
	    inputStreamReader.close();
		bufferedReader.close();
		inputStream.close();
	}
	/**
	 * Analyzes the JSON file and imports specific data.
	 * @throws JSONException 
	 */
	private void analyzeNEW() throws JSONException{
		JSONArray a = new JSONArray(json);
		
		for (int i = 0; i < a.length(); i++) {
			JSONObject tmp = (JSONObject) a.get(i);
			leagueNameList.add(tmp.getString("id"));	
			
			String url = tmp.getString("url");
			
			if(!url.equals("null")){
				url = url.replace("\"", "");
				leagueThreadID.add(generateThreadID(url));
			}
			else{
				leagueThreadID.add(url);
			}
			
			leagueStart.add(tmp.getString("startAt"));
			leagueEnd.add(tmp.getString("endAt"));	
		}
		
    }
	private void analyze(){
		int c = 0, e = 0;
		boolean searchID = true, searchURL = false, searchStartAt = false, searchEndAt = false;

		for(c = 0; c < json.length()-10; c++){	
			// id search
			if(searchID){
				// id indicator
				if(json.substring(c, c+6).equals("\"id\":\"")){
					for(e = c+6; e < json.length()-1; e++){
						// end of value indicator
						if(json.substring(e, e+1).equals(",")){
						
							String id = json.substring(c+5, e);
							
							if(!id.equals("null")){
								id = id.replace("\"", "");
							}
							
							leagueNameList.add(id);							
							searchID = false;
							searchURL = true;
							c = e+1;
							break;
						}					
					}
				}
			}
			// url search
			if(searchURL){
				// url indicator
				if(json.substring(c, c+6).equals("\"url\":")){
					for(e = c+6; e < json.length()-6; e++){
						// end of value indicator
						if(json.substring(e, e+1).equals(",")){
							
							String url = json.substring(c+6, e);
							
							if(!url.equals("null")){
								url = url.replace("\"", "");
								leagueThreadID.add(generateThreadID(url));
							}
							else{
								leagueThreadID.add(url);
							}
							
							searchURL = false;
							searchStartAt = true;
							c = e+1;
							break;
						}
					}
				}
			}
			// startAt search
			if(searchStartAt){
				// startAt indicator
				if(json.substring(c, c+10).equals("\"startAt\":")){
					for(e = c+10; e < json.length()-10; e++){
						// end of value indicator
						if(json.substring(e, e+1).equals(",")){
							
							String startAt = json.substring(c+10, e);
							if(!startAt.equals("null")){
								startAt = startAt.replace("\"", "");
							}
							
							leagueStart.add(startAt);
							searchStartAt = false;
							searchEndAt = true;
							c = e+1;
							break;
						}
					}
				}
			}
			// endAt search
			if(searchEndAt){
				// startAt indicator
				if(json.substring(c, c+8).equals("\"endAt\":")){
					for(e = c+8; e < json.length()-8; e++){
						// end of value indicator
						if(json.substring(e, e+1).equals(",")){
							
							String endAt = json.substring(c+8, e);
							if(!endAt.equals("null")){
								endAt = endAt.replace("\"", "");
							}
							
							leagueEnd.add(endAt);							
							searchEndAt = false;
							searchID = true;
							c = e+1;
							break;
						}
					}
				}
			}
		}
    }
	/**
	 * Converts the JSON date & time to the local one with another format.
	 */
	public void convertDates(){
		for(int x = 0; x < 2; x++){
			for(int i = 0; i < leagueEnd.size(); i++){
				String date;
				if(x == 0){
					date = leagueEnd.get(i);
				}
				else{
					date = leagueStart.get(i);
				}
				
				if(date.charAt(0) == 'n' && date.charAt(1) == 'u' && date.charAt(2) == 'l' && date.charAt(3) == 'l'){
					if(x == 0){
						leagueEnd.set(i, "never");
					}
					else{
						leagueStart.set(i, "never");
					}
				}
				else{
					if(date.length() == 20){
						if(date.charAt(4) == '-' && date.charAt(7) == '-' && date.charAt(10) == 'T' && date.charAt(19) == 'Z'){
							
							try {
								Date parsedDate;
								SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
								isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
								parsedDate = isoFormat.parse(date);
								
								Calendar cal = Calendar.getInstance();
							    cal.setTime(parsedDate);
							    String formattedDate;
							    
								if(cal.get(Calendar.DAY_OF_MONTH) < 10){
									formattedDate = "0" + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))+ ".";
								}
								else{
									formattedDate = Integer.toString(cal.get(Calendar.DAY_OF_MONTH))+ ".";
								}
								
								if(cal.get(Calendar.MONTH)+1 < 10){
									formattedDate = formattedDate + "0" + Integer.toString(cal.get(Calendar.MONTH)+1) + "." + Integer.toString(cal.get(Calendar.YEAR)) + " @ ";
								}
								else{
									formattedDate = formattedDate + Integer.toString(cal.get(Calendar.MONTH)+1) + "." + Integer.toString(cal.get(Calendar.YEAR)) + " @ ";
								}
								
								if(cal.get(Calendar.HOUR_OF_DAY) < 10 ){
									formattedDate = formattedDate + "0" + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + ":";
								}
								else{
									formattedDate = formattedDate + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + ":";
								}
								
								if(cal.get(Calendar.MINUTE) < 10 ){
									formattedDate = formattedDate + "0" + Integer.toString(cal.get(Calendar.MINUTE));
								}
								else{
									formattedDate = formattedDate + Integer.toString(cal.get(Calendar.MINUTE));
								}
								
								if(x == 0){
									leagueEnd.set(i, formattedDate);
								}
								else{
									leagueStart.set(i, formattedDate);
								}
							} catch (ParseException e){
								e.printStackTrace();
							}
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
		for(int c = url.length(); c > url.length()-20; c--){
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
	/**
	 * Returns a list of the start of leagues/races.
	 * 
	 * @return List of strings containing the start of leagues/races.
	 */
	public List<String> getLeagueStart(){
		return leagueStart;
	}
	/**
	 * Returns a list of the end of leagues/races.
	 * 
	 * @return List of strings containing the end of leagues/races.
	 */
	public List<String> getLeagueEnd(){
		return leagueEnd;
	}
}