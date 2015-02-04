package com.wego.screenscraping;

import java.util.List;

public class FlightDetails {
    private String            endPlace;
    private String            startPlace;
    private String            startDate;
    private String            returnDate;
    private List<Flight>      onwardFlights;
    private List<Flight>      returnFlights;
    private List<String>      classTypes;

    public FlightDetails(String startPlace, String destination, String departureDate, String returnDate, List<String> classTypes, List<Flight> departFlights, List<Flight> returnFlights) {
        this(startPlace, destination, departureDate, classTypes, departFlights);
        this.returnDate = returnDate;
        this.returnFlights = returnFlights;
    }

    public FlightDetails(String source, String destination, String departureDate, List<String> classTypes, List<Flight> departFlights) {
        this.startPlace = source;
        this.endPlace = destination;
        this.startDate = departureDate;
        this.onwardFlights = departFlights;
        this.classTypes = classTypes;
    }

    /**
     * @return the departFlights
     */
    public List<Flight> getOnwardFlights() {
        return onwardFlights;
    }

    /**
     * @return the endPlace
     */
    public String getDestination() {
        return endPlace;
    }

    /**
     * @return the startPlace
     */
    public String getSource() {
        return startPlace;
    }

    /**
     * @return the startDate
     */
    public String getStartingDate() {
        return startDate;
    }

    /**
     * @return the returnDate
     */
    public String getReturnDate() {
        return returnDate;
    }

    /**
     * @return the returnFlights
     */
    public List<Flight> getReturnFlights() {
        return returnFlights;
    }
    
    public List<String> getClassTypes() {
        return classTypes;
    }
}
