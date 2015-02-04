package com.wego.screenscraping;

import java.util.List;

public interface FlightDetailsScraper {
    FlightDetails extractFlightDetails();
    List<Flight> extractDepartureDetails();
	List<Flight> extractReturnDetails();
}