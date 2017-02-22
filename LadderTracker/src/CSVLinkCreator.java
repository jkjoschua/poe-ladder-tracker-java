import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * The CSVLinkCreator object can be used to create the base of the CSV file link out of the forum thread id.
 * The base of the CSV file link means that the index number of a specific CSV file (at the end of the link) is missing and has to be
 * added.
 * 
 * @author Joschn
 */
public class CSVLinkCreator{
	private String forumThreadID;
	private String forumThreadLink;
	private String csvFileLink;
	private String xPath = "//a[@class='exportCsv button1']";
	private int waitForJavaScript = 5000;
	
	/**
	 * Constructor for the CSVLinkCreator object.
	 * 
	 * @param ThreadID - The forum thread ID of the league/race.
	 */
	public CSVLinkCreator(String ThreadID){
		forumThreadID = ThreadID;
		forumThreadLink = "http://www.pathofexile.com/forum/view-thread/" + forumThreadID;
	}
	/**
	 * Starts the CSV link creation process.
	 * 
	 * @throws Exception If there was an error while getting the CSV specific information from the forum thread.
	 */
	public void create() throws Exception{
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
	    HtmlPage page = (HtmlPage) webClient.getPage(forumThreadLink);
	    webClient.waitForBackgroundJavaScriptStartingBefore(waitForJavaScript);
	    HtmlAnchor anchor = (HtmlAnchor) page.getByXPath(xPath).get(0);
	    csvFileLink = anchor.getHrefAttribute();
	    csvFileLink = "http://www.pathofexile.com" + csvFileLink.subSequence(0, csvFileLink.length()-1);
	    webClient.close();  
	}
	/**
	 * Returns the base of the CSV file link (without the index number at the end).
	 * 
	 * @return The base of the CSV file link.
	 */
	public String getCSVFileLink(){
		return csvFileLink;
	}
}