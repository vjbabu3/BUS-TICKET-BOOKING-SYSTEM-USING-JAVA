package com.bus.dao;

import com.bus.model.Booking;
import com.bus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // INSERT: Book a ticket and return booking ID
    public int bookTicket(Booking booking) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if seat is already occupied by a CONFIRMED booking
            String checkSql = "SELECT COUNT(*) FROM bookings WHERE route_id = ? AND seat_number = ? AND travel_date = ? AND status = 'CONFIRMED'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, booking.getRouteId());
                checkStmt.setInt(2, booking.getSeatNumber());
                checkStmt.setString(3, booking.getTravelDate());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Seat already booked!");
                }
            }

            String sql = "INSERT INTO bookings (route_id, user_id, passenger_name, contact_details, seat_number, travel_date) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, booking.getRouteId());
                pstmt.setInt(2, booking.getUserId());
                pstmt.setString(3, booking.getPassengerName());
                pstmt.setString(4, booking.getContactDetails());
                pstmt.setInt(5, booking.getSeatNumber());
                pstmt.setString(6, booking.getTravelDate());

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Return booking ID
                        }
                    }
                }
                throw new SQLException("Insert successful but ID not generated.");
            }
        } catch (SQLException e) {
            throw e; // Rethrow for UI to handle
        }
    }

    // SELECT: User specific bookings
    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, r.price FROM bookings b JOIN routes r ON b.route_id = r.route_id WHERE b.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingId(rs.getInt("booking_id"));
                b.setRouteId(rs.getInt("route_id"));
                b.setUserId(rs.getInt("user_id"));
                b.setPassengerName(rs.getString("passenger_name"));
                b.setContactDetails(rs.getString("contact_details"));
                b.setSeatNumber(rs.getInt("seat_number"));
                b.setTravelDate(rs.getString("travel_date"));
                b.setPrice(rs.getDouble("price"));
                b.setStatus(rs.getString("status"));
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // DELETE: Cancel a ticket
    public boolean cancelTicket(int bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ADMIN: Total booking count
    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ADMIN: Total revenue
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(r.price),0) FROM bookings b JOIN routes r ON b.route_id=r.route_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ADMIN: Active user count (users who have at least one booking)
    public int getActiveUserCount() {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM bookings";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // SELECT: Check availability
    public List<Integer> getBookedSeats(int routeId, String travelDate) {
        List<Integer> bookedSeats = new ArrayList<>();
        String sql = "SELECT seat_number FROM bookings WHERE route_id = ? AND travel_date = ? AND status = 'CONFIRMED'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routeId);
            pstmt.setString(2, travelDate);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookedSeats.add(rs.getInt("seat_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedSeats;
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Booking b = new Booking();
                b.setBookingId(rs.getInt("booking_id"));
                b.setRouteId(rs.getInt("route_id"));
                b.setUserId(rs.getInt("user_id"));
                b.setPassengerName(rs.getString("passenger_name"));
                b.setContactDetails(rs.getString("contact_details"));
                b.setSeatNumber(rs.getInt("seat_number"));
                b.setTravelDate(rs.getString("travel_date"));
                b.setStatus(rs.getString("status"));
                return b;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
