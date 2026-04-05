package com.bus.model;

public class Route {
    private int routeId;
    private String source;
    private String destination;
    private String busNumber;
    private int totalSeats;
    private double price;
    private String departureTime;

    public Route() {
    }

    public Route(int routeId, String source, String destination, String busNumber, int totalSeats, double price, String departureTime) {
        this.routeId = routeId;
        this.source = source;
        this.destination = destination;
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.price = price;
        this.departureTime = departureTime;
    }

    // Getters and Setters
    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "R" + routeId + ": " + source + " - " + destination + " (" + departureTime + ")";
    }
}
