package com.hms.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.hms.dao.RoomDAO;
import com.hms.models.Room;

public class RoomScreen extends JFrame {
    private JTextField hotelIdField, roomNumberField, priceField;
    private JComboBox<String> typeBox;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private RoomDAO roomDAO;

    public RoomScreen() {
        roomDAO = new RoomDAO();

        setTitle("Manage Rooms");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Manage Rooms"));

        inputPanel.add(new JLabel("Hotel ID:"));
        hotelIdField = new JTextField();
        inputPanel.add(hotelIdField);

        inputPanel.add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        inputPanel.add(roomNumberField);

        inputPanel.add(new JLabel("Room Type:"));
        String[] types = {"Single", "Double", "Suite", "Penthouse"};
        typeBox = new JComboBox<>(types);
        inputPanel.add(typeBox);
        
        inputPanel.add(new JLabel("Price per Night:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        // Standard Add Button
        JButton addButton = new JButton("Add Room");
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addRoom());
        inputPanel.add(addButton);

        // --- FIXED DELETE BUTTON ---
        JButton deleteButton = new JButton("Delete Selected Room");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteRoom());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        String[] columnNames = {"Room ID", "Hotel ID", "Room No", "Type", "Price", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        roomTable = new JTable(tableModel);
        roomTable.setAutoCreateRowSorter(true); // Enable Sorting
        
        loadRooms();

        add(new JScrollPane(roomTable), BorderLayout.CENTER);
    }

    private void addRoom() {
        try {
            if (roomNumberField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room Number cannot be empty.");
                return;
            }
            int hotelId = Integer.parseInt(hotelIdField.getText().trim());
            String roomNo = roomNumberField.getText().trim();
            String type = (String) typeBox.getSelectedItem();
            double price = Double.parseDouble(priceField.getText().trim());
            
            if (roomDAO.isRoomNumberExists(hotelId, roomNo)) {
                JOptionPane.showMessageDialog(this, "Room " + roomNo + " already exists!");
                return;
            }
            
            Room newRoom = new Room(hotelId, roomNo, type, price, "Available");
            roomDAO.addRoom(newRoom);
            
            JOptionPane.showMessageDialog(this, "Room Added Successfully!");
            hotelIdField.setText("");
            roomNumberField.setText("");
            priceField.setText("");
            loadRooms();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid Numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            roomDAO.deleteRoom(id);
            JOptionPane.showMessageDialog(this, "Room Deleted Successfully!");
            loadRooms();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadRooms() {
        try {
            tableModel.setRowCount(0);
            List<Room> rooms = roomDAO.getAllRooms();
            for (Room r : rooms) {
                tableModel.addRow(new Object[]{
                    r.getId(), r.getHotelId(), r.getRoomNumber(), r.getType(), r.getPrice(), r.getStatus()
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}