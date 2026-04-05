package com.bus.dao;

import com.bus.model.Route;
import com.bus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM routes";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Route r = new Route();
                r.setRouteId(rs.getInt("route_id"));
                r.setSource(rs.getString("source"));
                r.setDestination(rs.getString("destination"));
                r.setBusNumber(rs.getString("bus_number"));
                r.setTotalSeats(rs.getInt("total_seats"));
                r.setPrice(rs.getDouble("price"));
                r.setDepartureTime(rs.getString("departure_time"));
                routes.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public Route getRouteById(int routeId) {
        String sql = "SELECT * FROM routes WHERE route_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Route r = new Route();
                    r.setRouteId(rs.getInt("route_id"));
                    r.setSource(rs.getString("source"));
                    r.setDestination(rs.getString("destination"));
                    r.setBusNumber(rs.getString("bus_number"));
                    r.setTotalSeats(rs.getInt("total_seats"));
                    r.setPrice(rs.getDouble("price"));
                    r.setDepartureTime(rs.getString("departure_time"));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
