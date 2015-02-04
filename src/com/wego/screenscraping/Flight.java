package com.wego.screenscraping;

import java.util.List;

/**
 * Object to keep data related to any flight
 * 
 * @author KumarVibhav
 *
 */
public class Flight {
    private String       departurePlace;
    private String       arrivalPlace;
    private String       departureDate;
    private String       departuretime;
    private String       arrivalDate;
    private String       arrivalTime;
    private List<Double> prices;

    public Flight(String departurePlace, String departureDate, String departureTime, String arrivalPlace, String arrivalDate, String arrivalTime, List<Double> prices) {
        this.departurePlace = departurePlace;
        this.departureDate = departureDate;
        this.departuretime = departureTime;
        this.arrivalPlace = arrivalPlace;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.prices = prices;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        return departuretime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice(int index) {
        return prices.get(index);
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    /**
     * @return the departurePlace
     */
    public String getDeparturePlace() {
        return departurePlace;
    }

    /**
     * @return the arrivalPlace
     */
    public String getArrivalPlace() {
        return arrivalPlace;
    }
}
