package com.bus.model;

public class Booking {
    private int bookingId;
    private int routeId;
    private int userId;
    private String passengerName;
    private String contactDetails;
    private int seatNumber;
    private String travelDate;
    private double price;
    private String status;

    public Booking() {
    }

    public Booking(int bookingId, int routeId, int userId, String passengerName, String contactDetails, int seatNumber,
            String travelDate, double price, String status) {
        this.bookingId = bookingId;
        this.routeId = routeId;
        this.userId = userId;
        this.passengerName = passengerName;
        this.contactDetails = contactDetails;
        this.seatNumber = seatNumber;
        this.travelDate = travelDate;
        this.price = price;
        this.status = status;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
