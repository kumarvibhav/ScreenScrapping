package com.wego.screenscraping;

import static com.wego.screenscraping.ScrapingHeaderConstants.ACCEPT;
import static com.wego.screenscraping.ScrapingHeaderConstants.ACCEPT_ENCODING;
import static com.wego.screenscraping.ScrapingHeaderConstants.ACCEPT_LANGUAGE;
import static com.wego.screenscraping.ScrapingHeaderConstants.CACHE_CONTROL;
import static com.wego.screenscraping.ScrapingHeaderConstants.CONNECTION;
import static com.wego.screenscraping.ScrapingHeaderConstants.CONTENT_LENGTH;
import static com.wego.screenscraping.ScrapingHeaderConstants.CONTENT_TYPE;
import static com.wego.screenscraping.ScrapingHeaderConstants.COOKIE;
import static com.wego.screenscraping.ScrapingHeaderConstants.HOST;
import static com.wego.screenscraping.ScrapingHeaderConstants.PRAGMA;
import static com.wego.screenscraping.ScrapingHeaderConstants.USER_AGENT;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;

public class ScreenScraper {
	private static final String cookie = "LanguageSelect=in/en; true_loc=in; s_rsid=aa-airasia-in-prd; optimizelySegments=%7B%22196875818%22%3A%22ff%22%2C%22198133615%22%3A%22direct%22%2C%22198389126%22%3A%22none%22%2C%22197393471%22%3A%22false%22%7D; optimizelyEndUserId=oeu1422547709920r0.03563687076826538; optimizelyBuckets=%7B%222419341275%22%3A%220%22%7D; __airasiaga=GA1.2.440211432.1422547711; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3Daa-airasia-in-prd%252Caa-airasia-global%253D%252526pid%25253Dwww.airasia.com%2525253Ain%2525253Aen%2525253Ahome.page%252526pidt%25253D1%252526oid%25253Dfunctiononclick(event)%2525257Breturnvalidate(this)%2525253FSKYSALES.SearchRedirection(event)%2525253Aevent.returnValue%2525253Dfals%252526oidt%25253D2%252526ot%25253DSUBMIT%3B; ASP.NET_SessionId=x2343y45tl2jwcunx3y5ot55; ASBD=1422893643_98B88F9C63728C63D22B28C4B77BCFF369B5499E; skysales=1461379594.20480.0000";
	private static final String postMessage = "eventTarget=&eventArgument=&viewState=%2FwEPDwUBMGRktapVDbdzjtpmxtfJuRZPDMU9XYk%3D&pageToken=&culture=en-GB&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24RadioButtonMarketStructure=RoundTrip&ControlGroupCompactView_AvailabilitySearchInputCompactVieworiginStation1=MEL&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24TextBoxMarketOrigin1=MEL&ControlGroupCompactView_AvailabilitySearchInputCompactViewdestinationStation1=DMK&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24TextBoxMarketDestination1=DMK&date_picker=02%2F11%2F2015&date_picker=&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24DropDownListMarketDay1=11&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24DropDownListMarketMonth1=2015-02&date_picker=02%2F25%2F2015&date_picker=&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24DropDownListMarketDay2=25&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24DropDownListMarketMonth2=2015-02&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24DropDownListPassengerType_ADT=1&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24DropDownListPassengerType_CHD=0&ControlGroupCompactView%24AvailabilitySearchInputCompactView%24DropDownListPassengerType_INFANT=0&ControlGroupCompactView%24MultiCurrencyConversionViewCompactSearchView%24DropDownListCurrency=5037535238&ControlGroupCompactView%24ButtonSubmit=Search&__VIEWSTATEGENERATOR=05F9A2B0&__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=%2FwEPDwUBMGRktapVDbdzjtpmxtfJuRZPDMU9XYk%3D";
	private static final String airAsiaBookingURL = "http://booking.airasia.com/Compact.aspx";
	
	public static void main(String[] args) throws IOException{
		//SocketAddress socketAddress = new  InetSocketAddress("127.0.0.1", 8888);
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, socketAddress);

		URL url = new URL(airAsiaBookingURL);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setRequestMethod("POST");
		httpConnection.setDoOutput(true);
		httpConnection.setInstanceFollowRedirects(false);
		setRequestParameters(httpConnection);
		DataOutputStream dos = new DataOutputStream(httpConnection.getOutputStream());
		dos.writeBytes(postMessage);
		dos.flush();
		dos.close();
		
		// Handle redirection from /Compact.aspx to /Select.aspx to get flight details
		if(httpConnection.getResponseCode() == 302) {
		    // Add new hitting location to the host
			url = new URL("http://booking.airasia.com" + httpConnection.getHeaderField("Location"));
			// get cookie from response to be added to existing cookie for new response
			String cookies = httpConnection.getHeaderField("Set-Cookie");
			httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			if(cookies != null) {
				httpConnection.setRequestProperty("Cookie", cookie + ";" +cookies);
			}
			setRequestParametersRedirect(httpConnection);
			httpConnection.connect();
		}
		System.out.println(httpConnection.getResponseCode());
		System.out.println(httpConnection.getResponseMessage());
		
		// Convert encoded HTML response to human readable format
		GZIPInputStream gZipInputStream = new GZIPInputStream(httpConnection.getInputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(gZipInputStream));
		String line;
		StringBuilder sb = new StringBuilder();
		while((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		
		// Create AirAsiaScarper object to extract flight details
		AirAsiaScraper airAsiaScraper = new AirAsiaScraper(Jsoup.parse(sb.toString()));
		FlightDetails flightDetails = airAsiaScraper.extractFlightDetails();
		printResult(flightDetails);
	}
	
	/**
     * To set header parameters for post request
     * 
     * @param httpConnection
     *     {@link HttpURLConnection} object
     */
	public static void setRequestParameters(HttpURLConnection httpConnection) {
		httpConnection.setRequestProperty(HOST, "booking.airasia.com");
		httpConnection.setRequestProperty(ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*;q=0.8");
		httpConnection.setRequestProperty(CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
		httpConnection.setRequestProperty(CONTENT_LENGTH, "1555");
		httpConnection.setRequestProperty(COOKIE, cookie);
		httpConnection.setRequestProperty(PRAGMA, "no-cache");
		httpConnection.setRequestProperty(CACHE_CONTROL, "no-cache");
		setCommonHeaders(httpConnection);
	}
	
	/**
	 * To set header parameters for request to do redirection
	 * 
	 * @param httpConnection
	 *     {@link HttpURLConnection} object
	 */
	public static void setRequestParametersRedirect(HttpURLConnection httpConnection) {
		httpConnection.setRequestProperty(ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpConnection.setRequestProperty("Referer", "http://www.airasia.com/in/en/home.page?cid=1");
		setCommonHeaders(httpConnection);
	}
	
	/**
	 * To set the common header parameters for request
	 * 
	 * @param httpConnection
	 *     {@link HttpURLConnection} object
	 */
	private static void setCommonHeaders(HttpURLConnection httpConnection) {
	    httpConnection.setRequestProperty(USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0");
	    httpConnection.setRequestProperty(ACCEPT_LANGUAGE, "en-US,en;q=0.5");
	    httpConnection.setRequestProperty(ACCEPT_ENCODING, "gzip, deflate");
	    httpConnection.setRequestProperty(CONNECTION, "keep-alive");
	}
	
	private static void printResult(FlightDetails flightDetails) throws IOException {	    
	    System.out.println(frameResult(flightDetails.getClassTypes(), flightDetails.getOnwardFlights(), flightDetails.getStartingDate(), flightDetails.getSource(), flightDetails.getDestination()));
	    
	    if(flightDetails.getReturnFlights() != null) {
	        System.out.println("\n\n\n");
	        System.out.println(frameResult(flightDetails.getClassTypes(), flightDetails.getReturnFlights(), flightDetails.getReturnDate(), flightDetails.getDestination(), flightDetails.getSource()));
	    }
	}
	
	private static String frameResult(List<String> classTypes, List<Flight> flights, String departureDate, String fromPlace, String toPlace) {
	    StringBuilder sb = new StringBuilder();
        sb.append("Journey Details from ").append(fromPlace).append(" to ").append(toPlace).append("\n---------------------------------------------------")
                .append("\n\nDeparture Date & Time\tArrival Date and Time");

        for(String classType : classTypes) {
            sb.append("\t").append(classType);
        }
        
        for(Flight flight : flights) {
            sb.append("\n").append(departureDate).append(" ").append(flight.getDepartureTime()).append("\t\t").append(flight.getArrivalDate()).append(" ").append(flight.getArrivalTime());
            for(int i = 0; i < classTypes.size(); i++) {
                sb.append("\t\t").append(flight.getPrice(i));
            }
        }
        
        return sb.toString();
	}
}