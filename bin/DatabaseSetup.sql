-- Premium Database Setup for QuickBus
-- Features: Foreign key integrity, Unique constraints for double-booking prevention.

CREATE DATABASE IF NOT EXISTS bus_booking;
USE bus_booking;

-- 1. Users Table
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'user'
);

-- 2. Routes Table
CREATE TABLE routes (
    route_id INT PRIMARY KEY AUTO_INCREMENT,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    bus_number VARCHAR(20) NOT NULL,
    total_seats INT DEFAULT 24,
    price DOUBLE DEFAULT 500.0,
    departure_time VARCHAR(20) DEFAULT '10:00 AM'
);

-- 3. Bookings Table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    route_id INT,
    user_id INT,
    passenger_name VARCHAR(100) NOT NULL,
    contact_details VARCHAR(100) NOT NULL,
    seat_number INT NOT NULL,
    travel_date VARCHAR(20) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE(route_id, seat_number, travel_date)
);

-- Sample Routes (Premium Network)
INSERT INTO routes (source, destination, bus_number, total_seats, price, departure_time) VALUES 
('Delhi', 'Varanasi', 'DL-VAR-101', 32, 1200.0, '08:30 PM'),
('Delhi', 'Agra', 'DL-AGR-202', 24, 650.0, '06:00 AM'),
('Delhi', 'Shimla', 'DL-SHM-303', 30, 850.0, '09:00 PM'),
('Mumbai', 'Goa', 'MH-GOA-404', 32, 1500.0, '10:00 PM'),
('Mumbai', 'Pune', 'MH-PUN-505', 24, 450.0, '07:00 AM'),
('Bangalore', 'Hyderabad', 'KA-HYD-606', 32, 1100.0, '09:45 PM'),
('Bangalore', 'Chennai', 'KA-CHN-707', 30, 950.0, '11:15 PM'),
('Hyderabad', 'Vijayawada', 'TS-VIJ-808', 24, 600.0, '05:30 AM'),
('Vijayawada', 'Hyderabad', 'AP-HYD-909', 24, 600.0, '11:00 PM'),
('Ahmedabad', 'Surat', 'GJ-SUR-111', 30, 500.0, '02:00 PM'),
('Chandigarh', 'Delhi', 'CH-DL-222', 32, 450.0, '04:00 AM');

-- Initial Users
INSERT INTO users (name, email, password, role) VALUES 
('Admin Account', 'admin@quickbus.com', 'admin789', 'admin'),
('Verified User', 'user@quickbus.com', 'user123', 'user'),
('Vijay Kumar', 'vijay@example.com', 'vijay456', 'user');
