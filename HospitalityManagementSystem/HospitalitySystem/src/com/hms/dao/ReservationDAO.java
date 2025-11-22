package com.hms.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import com.hms.db.DatabaseConnector;
import com.hms.models.Reservation;

public class ReservationDAO {

    // 1. ADD RESERVATION
    public void addReservation(Reservation res) throws SQLException {
        String sql = "INSERT INTO reservations (guest_id, room_id, check_in_date, check_out_date, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.connect();
            conn.setAutoCommit(false); 

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, res.getGuestId());
            pstmt.setInt(2, res.getRoomId());
            pstmt.setDate(3, res.getCheckInDate());
            pstmt.setDate(4, res.getCheckOutDate());
            pstmt.setDouble(5, res.getTotalPrice());
            pstmt.setString(6, res.getStatus());
            pstmt.executeUpdate();
            pstmt.close();

            String updateRoom = "UPDATE rooms SET status = 'Occupied' WHERE id = ?";
            pstmt = conn.prepareStatement(updateRoom);
            pstmt.setInt(1, res.getRoomId());
            pstmt.executeUpdate();

            conn.commit(); 

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    // 2. CHECK AVAILABILITY
    public boolean isRoomAvailable(int roomId, Date checkIn, Date checkOut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE room_id = ? AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setDate(2, checkOut);
            pstmt.setDate(3, checkIn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        }
        return false;
    }

    // 3. ANTI-SCAM
    public boolean hasGuestActiveBooking(int guestId, int roomId, Date checkIn, Date checkOut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations r " +
                     "JOIN rooms rm ON r.room_id = rm.id " +
                     "WHERE r.guest_id = ? " +
                     "AND rm.hotel_id = (SELECT hotel_id FROM rooms WHERE id = ?) " + 
                     "AND r.check_in_date < ? AND r.check_out_date > ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, guestId);
            pstmt.setInt(2, roomId);
            pstmt.setDate(3, checkOut);
            pstmt.setDate(4, checkIn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    // 4. DELETE
    public void deleteReservation(int id) throws SQLException {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setInt(1, id);
             pstmt.executeUpdate();
        }
    }

    // --- 5. THE FIXED REPORT (Aliasing) ---
    public Vector<Vector<String>> getReservationReport(String searchTerm) throws SQLException {
        Vector<Vector<String>> data = new Vector<>();
        
        // CRITICAL FIX: We use 'AS' to rename columns to avoid confusion
        String sql = "SELECT " +
                     "r.id, " +
                     "g.name AS guest_name, " +        // Explicitly call this guest_name
                     "rm.room_number, " +
                     "h.name AS hotel_name, " +        // Explicitly call this hotel_name
                     "r.check_in_date, " +
                     "r.check_out_date, " +
                     "r.total_price, " +
                     "r.status " +
                     "FROM reservations r " +
                     "JOIN guests g ON r.guest_id = g.id " +
                     "JOIN rooms rm ON r.room_id = rm.id " +
                     "JOIN hotels h ON rm.hotel_id = h.id ";

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql += "WHERE g.name LIKE ? OR rm.room_number LIKE ?";
        }
        
        sql += " ORDER BY r.check_in_date DESC";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                pstmt.setString(1, "%" + searchTerm + "%");
                pstmt.setString(2, "%" + searchTerm + "%");
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(rs.getInt("id")));
                
                // NOW WE USE THE SPECIFIC NICKNAMES
                row.add(rs.getString("guest_name"));   // Correctly grabs Elon Musk
                row.add(rs.getString("room_number"));
                row.add(rs.getString("hotel_name"));   // Correctly grabs Grand FAANG
                
                row.add(rs.getDate("check_in_date").toString());
                row.add(rs.getDate("check_out_date").toString());
                row.add("$" + rs.getDouble("total_price"));
                row.add(rs.getString("status"));
                data.add(row);
            }
        }
        return data;
    }
    
    public Vector<Vector<String>> getMasterReport(String searchTerm) throws SQLException {
        return getReservationReport(searchTerm);
    }

    public List<Reservation> getAllReservations() throws SQLException {
        return new ArrayList<>();
    }
}