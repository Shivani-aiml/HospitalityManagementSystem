package com.hms.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Vector;
import com.hms.dao.*;
import com.hms.models.*;

public class ReservationScreen extends JFrame {
    private JComboBox<String> hotelBox, guestBox, roomBox;
    private JTextField checkInField, checkOutField, priceField;
    private JTable resTable;
    private DefaultTableModel tableModel;
    
    private ReservationDAO resDAO;
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private HotelDAO hotelDAO;

    public ReservationScreen() {
        resDAO = new ReservationDAO();
        guestDAO = new GuestDAO();
        roomDAO = new RoomDAO();
        hotelDAO = new HotelDAO();

        setTitle("Reservation Management (Professional)");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- INPUT PANEL ---
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10)); 
        inputPanel.setBorder(BorderFactory.createTitledBorder("New Booking"));

        // 1. Hotel & Room Logic
        inputPanel.add(new JLabel("1. Select Hotel:"));
        hotelBox = new JComboBox<>();
        hotelBox.addActionListener(e -> updateRoomList());
        inputPanel.add(hotelBox);

        inputPanel.add(new JLabel("2. Select Available Room:"));
        roomBox = new JComboBox<>();
        inputPanel.add(roomBox);

        // 3. Guest Logic
        inputPanel.add(new JLabel("3. Select Guest:"));
        guestBox = new JComboBox<>();
        inputPanel.add(guestBox);

        // 4. Dates
        inputPanel.add(new JLabel("Check-In (DD-MM-YYYY):"));
        checkInField = new JTextField();
        inputPanel.add(checkInField);
        
        inputPanel.add(new JLabel("Check-Out (DD-MM-YYYY):"));
        checkOutField = new JTextField();
        inputPanel.add(checkOutField);
        
        inputPanel.add(new JLabel("Total Price (Auto-calc):"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        // Buttons
        JButton addButton = new JButton("Confirm Booking");
        addButton.setBackground(new Color(0, 153, 76)); 
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addReservation());
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Cancel Selected");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteReservation());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // --- TABLE PANEL (IMPROVED) ---
        // Showing NAMES not IDs
        String[] columnNames = {"ID", "Guest Name", "Room No", "Hotel", "Check-In", "Check-Out", "Price", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resTable = new JTable(tableModel);
        resTable.setAutoCreateRowSorter(true);
        
        // Load Data
        loadGuestDropdown();
        loadHotelDropdown();
        loadReservations(); // Now loads Names!

        add(new JScrollPane(resTable), BorderLayout.CENTER);
    }

    private void addReservation() {
        try {
            int guestId = getSelectedId(guestBox);
            int roomId = getSelectedId(roomBox);
            
            if (guestId == -1 || roomId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a Hotel, Room, and Guest.");
                return;
            }

            Date checkIn = parseDate(checkInField.getText().trim());
            Date checkOut = parseDate(checkOutField.getText().trim());

            if (checkIn == null || checkOut == null) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format!");
                return;
            }
            
            if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                JOptionPane.showMessageDialog(this, "Check-Out must be after Check-In.");
                return;
            }

            // 1. CONFLICT CHECK: Is the Room free?
            if (!resDAO.isRoomAvailable(roomId, checkIn, checkOut)) {
                JOptionPane.showMessageDialog(this, "Room Conflict: This room is already booked.");
                return;
            }

            // 2. ANTI-SCAM CHECK: Does this Guest already have a room here?
            if (resDAO.hasGuestActiveBooking(guestId, roomId, checkIn, checkOut)) {
                JOptionPane.showMessageDialog(this, 
                    "SCAM PREVENTION ALERT:\n" +
                    "This Guest already has an active booking in this hotel for these dates.\n" +
                    "Multiple room bookings per guest are restricted.",
                    "Policy Violation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double price;
            if (priceField.getText().trim().isEmpty()) {
                long days = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
                if (days <= 0) days = 1;
                price = days * 100.0; 
            } else {
                price = Double.parseDouble(priceField.getText().trim());
            }

            Reservation res = new Reservation(0, guestId, roomId, checkIn, checkOut, price, "Confirmed");
            resDAO.addReservation(res);
            
            JOptionPane.showMessageDialog(this, "Booking Confirmed!");
            loadReservations();
            updateRoomList(); 
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // --- DATA LOADERS ---
    private void loadReservations() {
        try {
            tableModel.setRowCount(0);
            // We use the REPORT method (Joins) instead of the basic list
            Vector<Vector<String>> data = resDAO.getReservationReport("");
            for (Vector<String> row : data) {
                tableModel.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadHotelDropdown() {
        try {
            java.util.List<Hotel> hotels = hotelDAO.getAllHotels();
            hotelBox.removeAllItems();
            hotelBox.addItem("--- Select Hotel ---");
            for (Hotel h : hotels) hotelBox.addItem(h.getId() + " - " + h.getName());
        } catch (Exception e) { }
    }

    private void updateRoomList() {
        String selectedHotel = (String) hotelBox.getSelectedItem();
        roomBox.removeAllItems();
        if (selectedHotel == null || selectedHotel.startsWith("---")) return;
        try {
            int hotelId = Integer.parseInt(selectedHotel.split(" - ")[0]);
            java.util.List<Room> rooms = roomDAO.getAvailableRoomsByHotel(hotelId);
            for (Room r : rooms) roomBox.addItem(r.getId() + " - " + r.getRoomNumber() + " (" + r.getType() + ") - $" + r.getPrice());
        } catch (Exception e) { }
    }

    private void loadGuestDropdown() {
        try {
            java.util.List<Guest> guests = guestDAO.getAllGuests();
            guestBox.removeAllItems();
            for (Guest g : guests) guestBox.addItem(g.getId() + " - " + g.getName());
        } catch (Exception e) { }
    }

    private int getSelectedId(JComboBox<String> box) {
        String selected = (String) box.getSelectedItem();
        if (selected == null || selected.startsWith("---")) return -1;
        return Integer.parseInt(selected.split(" - ")[0]);
    }
    
    private void deleteReservation() {
         int selectedRow = resTable.getSelectedRow();
         if (selectedRow == -1) {
             JOptionPane.showMessageDialog(this, "Select a booking to cancel.");
             return;
         }
         // ID is in the first column (index 0)
         int id = Integer.parseInt((String) tableModel.getValueAt(selectedRow, 0));
         try {
             resDAO.deleteReservation(id);
             JOptionPane.showMessageDialog(this, "Cancelled!");
             loadReservations();
             updateRoomList();
         } catch(Exception e) { }
    }

    private Date parseDate(String dateStr) {
        String[] formats = {"yyyy-MM-dd", "dd-MM-yyyy", "MM/dd/yyyy", "dd/MM/yyyy"};
        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                java.util.Date utilDate = sdf.parse(dateStr);
                return new Date(utilDate.getTime());
            } catch (Exception e) {}
        }
        return null;
    }
}