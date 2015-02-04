package com.wego.screenscraping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AirAsiaScraper implements FlightDetailsScraper {
    private static final String DEPARTURE_DETAIL_TABLE_ID      = "fareTable1_4";
    private static final String RETURN_DETAIL_TABLE_ID         = "fareTable2_4";
    private static final String FLIGHT_ROW_CLASS               = "rgRow";
    private static final String JOURNEY_TYPE_ELEMENT_ID        = "ControlGroupAvailabilitySearchInputSelectView_AvailabilitySearchInputSelectView_RoundTrip";
    private static final String CHARACTER_SET                  = "UTF-8";
    private static final String LOW_FARE_ELEMENT_ID            = "iconLowfare1";
    private static final String PREMIUM_FARE_ELEMENT_ID        = "iconHiflyer1";
    private static final String BUSINESS_CLASS_FARE_ELEMENT_ID = "iconPremium1";
    private static final String UTCDATE_ID                     = "UTCDATE";
    private static final String P_TAG                          = "p";
    private static final String SEGMENTSTATION_CLASS           = "segmentStation";
    private static final String TD_TAG                         = "td";
    private static final String SPAN_TAG                       = "span";

    private final Element       bodyElement;

    public AirAsiaScraper(Document htmlDocument) {
        this.bodyElement = htmlDocument.body();
    }

    public static void main(String[] args) throws IOException {
        AirAsiaScraper airAsiaScraper = new AirAsiaScraper(Jsoup.parse(new File(args[0]), CHARACTER_SET));
        airAsiaScraper.extractFlightDetails();
    }

    public FlightDetails extractFlightDetails() {
        List<String> classTypes = getClassTypes();
        List<Flight> departureFlights = extractDepartureDetails();
        FlightDetails flightDetails;

        String journeyType = bodyElement.getElementById(JOURNEY_TYPE_ELEMENT_ID).attr("value");
        String departReturnDate = getDepartReturnDate();
        String departureDate = departReturnDate.substring(0, departReturnDate.indexOf(':'));
        if (journeyType.equals("RoundTrip")) {
            List<Flight> returnFlights = extractReturnDetails();
            String returnDate = departReturnDate.substring(departReturnDate.indexOf(':') + 1);
            flightDetails = new FlightDetails(getStartPlace(), getEndPlace(), departureDate, returnDate, classTypes, departureFlights, returnFlights);
        } else {
            flightDetails = new FlightDetails(getStartPlace(), getEndPlace(), departureDate, classTypes, departureFlights);
        }

        return flightDetails;
    }

    public List<Flight> extractDepartureDetails() {
        return getFlights(DEPARTURE_DETAIL_TABLE_ID);
    }

    public List<Flight> extractReturnDetails() {
        return getFlights(RETURN_DETAIL_TABLE_ID);
    }

    public List<String> getClassTypes() {
        List<String> classTypes = new ArrayList<String>();
        classTypes.add(bodyElement.getElementById(LOW_FARE_ELEMENT_ID).ownText());
        classTypes.add(bodyElement.getElementById(PREMIUM_FARE_ELEMENT_ID).ownText());
        classTypes.add(bodyElement.getElementById(BUSINESS_CLASS_FARE_ELEMENT_ID).getElementsByTag("a").first().ownText());

        return classTypes;
    }

    private List<Flight> getFlights(String tableId) {
        Elements flightDetailElements = bodyElement.getElementById(tableId).getElementsByClass(FLIGHT_ROW_CLASS);
        List<Flight> flights = new ArrayList<Flight>();
        Flight flight;
        String arrivalDate;
        String localDepartureTime;
        String departureDate;
        String departurePlace;
        String arrivalPlace;
        String localArrivalTime;
        List<Double> prices;
        String price;

        for (Element flightDetailElement : flightDetailElements) {
            Elements dateTimeElements = flightDetailElement.getElementsByClass(SEGMENTSTATION_CLASS);

            // To get the local departure time, utc arrival date and local
            // arrival time
            Element dateTimeElement = dateTimeElements.first();
            localDepartureTime = dateTimeElement.getElementsByTag(P_TAG).first().ownText();
            departurePlace = localDepartureTime.substring(localDepartureTime.indexOf("(") + 1, localDepartureTime.indexOf(")"));
            localDepartureTime = localDepartureTime.substring(0, localDepartureTime.indexOf(" ")-2);
            departureDate = dateTimeElements.get(0).getElementById(UTCDATE_ID).ownText();
            
            dateTimeElement = dateTimeElements.get(1);
            arrivalDate = dateTimeElement.getElementById(UTCDATE_ID).ownText();
            localArrivalTime = dateTimeElement.getElementsByTag(P_TAG).first().ownText();
            arrivalPlace = localArrivalTime.substring(localArrivalTime.indexOf("(") + 1, localArrivalTime.indexOf(")"));
            localArrivalTime = localArrivalTime.substring(0, localArrivalTime.indexOf(" ")-2);

            // to get price details
            Elements flightClassPriceDetails = flightDetailElement.getElementsByTag(TD_TAG);
            prices = new ArrayList<Double>();
            for (int i = 1; i < flightClassPriceDetails.size(); i++) {
                price = flightClassPriceDetails.get(i).getElementsByTag(SPAN_TAG).first().ownText();
                price = price.substring(0, price.indexOf(" "));
                price = price.replace(",", "");
                prices.add(Double.valueOf(price));
            }

            flight = new Flight(departurePlace, departureDate, localDepartureTime, arrivalPlace, arrivalDate, localArrivalTime, prices);
            flights.add(flight);
        }

        return flights;
    }
    
    private String getStartPlace() {
        return bodyElement.getElementById("ControlGroupAvailabilitySearchInputSelectView_AvailabilitySearchInputSelectView_TextBoxMarketOrigin1").attr("value");
    }
    
    private String getEndPlace() {
        return bodyElement.getElementById("ControlGroupAvailabilitySearchInputSelectView_AvailabilitySearchInputSelectView_TextBoxMarketDestination1").attr("value");
    }
    
    private String getDepartReturnDate() {
        String data = bodyElement.getElementById("footer").data();
        int indexOfDepartDate = data.indexOf("departDate:\"");
        int indexOfreturnDate = data.indexOf("\",returnDate:\"");
        data = data.substring(indexOfDepartDate + 12, indexOfreturnDate) + ":" + data.substring(indexOfreturnDate + 16, data.indexOf("\"}])"));
        return data;
    }
}
