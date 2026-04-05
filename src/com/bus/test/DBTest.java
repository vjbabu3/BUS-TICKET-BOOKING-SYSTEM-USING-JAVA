package com.bus.test;

import com.bus.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("DATABASE_CONNECTED_SUCCESSFULLY");
                System.out.println("Columns of 'routes' table:");
                try (java.sql.ResultSet rs = conn.getMetaData().getColumns("bus_booking", null, "routes", "%")) {
                    while (rs.next()) {
                        System.out.println("- " + rs.getString("COLUMN_NAME") + " (" + rs.getString("TYPE_NAME") + ")");
                    }
                }
            } else {
                System.out.println("DATABASE_CONNECTION_FAILED");
            }
        } catch (SQLException e) {
            System.err.println("DATABASE_CONNECTION_ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
