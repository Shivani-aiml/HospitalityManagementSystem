package com.hms.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.hms.db.DatabaseConnector;
import com.hms.models.Hotel;

public class HotelDAO {

    // CREATE: Add a new hotel to the database
    public void addHotel(Hotel hotel) throws SQLException {
        String sql = "INSERT INTO hotels (name, location, amenities) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hotel.getName());
            pstmt.setString(2, hotel.getLocation());
            pstmt.setString(3, hotel.getAmenities());
            
            pstmt.executeUpdate();
        }
    }

    // READ: Get all hotels
    public List<Hotel> getAllHotels() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Hotel hotel = new Hotel(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getString("amenities")
                );
                hotels.add(hotel);
            }
        }
        return hotels;
    }
    
    // --- NEW METHOD: DELETE HOTEL ---
    public void deleteHotel(int hotelId) throws SQLException {
        String sql = "DELETE FROM hotels WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hotelId);
            pstmt.executeUpdate();
        }
    }
}