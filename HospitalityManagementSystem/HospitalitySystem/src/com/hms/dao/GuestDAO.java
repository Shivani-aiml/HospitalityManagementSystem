package com.hms.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.hms.db.DatabaseConnector;
import com.hms.models.Guest;

public class GuestDAO {

    // CREATE
    public void addGuest(Guest guest) throws SQLException {
        String sql = "INSERT INTO guests (name, email, phone) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, guest.getName());
            pstmt.setString(2, guest.getEmail());
            pstmt.setString(3, guest.getPhone());
            
            pstmt.executeUpdate();
        }
    }

    // READ
    public List<Guest> getAllGuests() throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Guest guest = new Guest(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                guests.add(guest);
            }
        }
        return guests;
    }
    
    // --- NEW METHOD: DELETE GUEST ---
    public void deleteGuest(int id) throws SQLException {
        String sql = "DELETE FROM guests WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}