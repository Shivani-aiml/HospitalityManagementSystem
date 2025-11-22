package com.hms.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.hms.db.DatabaseConnector;
import com.hms.models.Room;

public class RoomDAO {

    public void addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (hotel_id, room_number, type, price, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, room.getHotelId());
            pstmt.setString(2, room.getRoomNumber());
            pstmt.setString(3, room.getType());
            pstmt.setDouble(4, room.getPrice());
            pstmt.setString(5, room.getStatus());
            pstmt.executeUpdate();
        }
    }

    // Used for validation
    public boolean isRoomNumberExists(int hotelId, String roomNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE hotel_id = ? AND room_number = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hotelId);
            pstmt.setString(2, roomNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(new Room(
                    rs.getInt("id"), rs.getInt("hotel_id"), rs.getString("room_number"),
                    rs.getString("type"), rs.getDouble("price"), rs.getString("status")
                ));
            }
        }
        return rooms;
    }

    // --- NEW METHOD: GET AVAILABLE ROOMS BY HOTEL ---
    // This is the key for the UX improvement
    public List<Room> getAvailableRoomsByHotel(int hotelId) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE hotel_id = ? AND status = 'Available'"; // Only show free rooms!
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, hotelId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(
                    rs.getInt("id"), rs.getInt("hotel_id"), rs.getString("room_number"),
                    rs.getString("type"), rs.getDouble("price"), rs.getString("status")
                ));
            }
        }
        return rooms;
    }

    public void deleteRoom(int id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}