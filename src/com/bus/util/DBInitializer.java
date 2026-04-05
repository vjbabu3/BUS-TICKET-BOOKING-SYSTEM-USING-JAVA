package com.bus.util;

import java.sql.*;

public class DBInitializer {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "bus_booking";
    private static final String USER = "root";
    private static final String PASSWORD = "vr131149";

    public static void initialize() {
        System.out.println("Starting DB Initialization Check...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 1. Create database if it doesn't exist
            try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            // 2. Setup tables
            try (Connection conn = DriverManager.getConnection(BASE_URL + DB_NAME, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                // Users Table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) UNIQUE NOT NULL, " +
                        "password VARCHAR(100) NOT NULL, " +
                        "role VARCHAR(20) DEFAULT 'user')");

                // Routes Table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS routes (" +
                        "route_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "source VARCHAR(100) NOT NULL, " +
                        "destination VARCHAR(100) NOT NULL, " +
                        "bus_number VARCHAR(20) NOT NULL, " +
                        "total_seats INT DEFAULT 24, " +
                        "price DOUBLE DEFAULT 500.0, " +
                        "departure_time VARCHAR(20) DEFAULT '10:00 AM')");

                // Bookings Table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bookings (" +
                        "booking_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "route_id INT, " +
                        "user_id INT, " +
                        "passenger_name VARCHAR(100) NOT NULL, " +
                        "contact_details VARCHAR(100) NOT NULL, " +
                        "seat_number INT NOT NULL, " +
                        "travel_date VARCHAR(20) NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'CONFIRMED', " +
                        "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");

                // MIGRATION: Add status column if it doesn't exist (for existing DBs)
                try {
                    stmt.executeUpdate("ALTER TABLE bookings ADD COLUMN status VARCHAR(20) DEFAULT 'CONFIRMED' AFTER travel_date");
                    System.out.println("Migration: status column added to bookings.");
                } catch (SQLException e) {
                    // Column already exists, ignore
                }

                System.out.println("Tables verified.");

                // 3. Seed initial data if empty
                seedData(conn);
            }

        } catch (Exception e) {
            System.err.println("Database Initialization Failed: " + e.getMessage());
        }
    }

    private static void seedData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Check Users
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) == 0) {
            System.out.println("Seeding default users...");
            String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "Admin Account"); pstmt.setString(2, "admin@quickbus.com"); pstmt.setString(3, "admin789"); pstmt.setString(4, "admin"); pstmt.addBatch();
                pstmt.setString(1, "Verified User"); pstmt.setString(2, "user@quickbus.com"); pstmt.setString(3, "user123"); pstmt.setString(4, "user"); pstmt.addBatch();
                pstmt.setString(1, "Vijay Kumar"); pstmt.setString(2, "vijay@example.com"); pstmt.setString(3, "vijay456"); pstmt.setString(4, "user"); pstmt.addBatch();
                pstmt.executeBatch();
            }
        }

        // Check Routes (50 Buses)
        rs = stmt.executeQuery("SELECT COUNT(*) FROM routes");
        if (rs.next() && rs.getInt(1) < 10) { // If less than 10, seed the full network
            System.out.println("Seeding 50+ premium routes...");
            String sql = "INSERT INTO routes (source, destination, bus_number, total_seats, price, departure_time) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String[][] basic = {
                    {"Delhi", "Agra", "DL-AGR-202", "24", "650.0", "06:00 AM"},
                    {"Delhi", "Varanasi", "DL-VAR-101", "32", "1200.0", "08:30 PM"},
                    {"Mumbai", "Goa", "MH-GOA-404", "32", "1500.0", "10:00 PM"},
                    {"Bangalore", "Hyderabad", "KA-HYD-606", "32", "1100.0", "09:45 PM"},
                    {"Chennai", "Madurai", "TN-MAD-901", "30", "850.0", "11:00 PM"}
                };
                for(String[] r : basic) {
                    pstmt.setString(1, r[0]); pstmt.setString(2, r[1]); pstmt.setString(3, r[2]);
                    pstmt.setInt(4, Integer.parseInt(r[3])); pstmt.setDouble(5, Double.parseDouble(r[4]));
                    pstmt.setString(6, r[5]);
                    pstmt.addBatch();
                }

                // Add 50 more programmatically
                String[] cities = {"Delhi", "Mumbai", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Ahmedabad", "Pune", "Jaipur", "Lucknow", "Patna", "Kochi"};
                for (int i = 1; i <= 50; i++) {
                    String src = cities[i % cities.length];
                    String dst = cities[(i + 3) % cities.length];
                    if(src.equals(dst)) dst = cities[(i + 5) % cities.length];
                    
                    pstmt.setString(1, src);
                    pstmt.setString(2, dst);
                    pstmt.setString(3, "QB-" + (100 + i));
                    pstmt.setInt(4, (i % 2 == 0) ? 24 : 32);
                    pstmt.setDouble(5, 450.0 + (i * 10));
                    pstmt.setString(6, (7 + (i % 12)) + ":00 " + (i % 2 == 0 ? "AM" : "PM"));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                System.out.println("50+ Routes successfully seeded.");
            }
        }
    }
}
