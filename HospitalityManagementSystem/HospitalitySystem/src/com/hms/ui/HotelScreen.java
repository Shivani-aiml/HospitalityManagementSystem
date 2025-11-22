package com.hms.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.hms.dao.HotelDAO;
import com.hms.models.Hotel;

public class HotelScreen extends JFrame {
    private JTextField nameField, locationField, amenitiesField;
    private JTable hotelTable;
    private DefaultTableModel tableModel;
    private HotelDAO hotelDAO;

    public HotelScreen() {
        hotelDAO = new HotelDAO();

        setTitle("Manage Hotels");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add / Manage Hotels"));

        inputPanel.add(new JLabel("Hotel Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Location:"));
        locationField = new JTextField();
        inputPanel.add(locationField);

        inputPanel.add(new JLabel("Amenities (comma separated):"));
        amenitiesField = new JTextField();
        inputPanel.add(amenitiesField);

        // Standard Add Button
        JButton addButton = new JButton("Add Hotel");
        addButton.setBackground(new Color(0, 153, 76)); // Professional Green
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addHotel());
        inputPanel.add(addButton);

        // --- FIXED DELETE BUTTON (Red Background, White Text) ---
        JButton deleteButton = new JButton("Delete Selected Hotel");
        deleteButton.setBackground(Color.RED);       // Red Background
        deleteButton.setForeground(Color.WHITE);     // White Text
        deleteButton.setOpaque(true);                // Force color on Mac
        deleteButton.setBorderPainted(false);        // Flat style
        deleteButton.addActionListener(e -> deleteHotel());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        // --- Center Panel ---
        String[] columnNames = {"ID", "Name", "Location", "Amenities"};
        tableModel = new DefaultTableModel(columnNames, 0);
        hotelTable = new JTable(tableModel);
        hotelTable.setAutoCreateRowSorter(true); // Enable Sorting
        
        loadHotels();

        add(new JScrollPane(hotelTable), BorderLayout.CENTER);
    }

    private void addHotel() {
        String name = nameField.getText();
        String location = locationField.getText();
        String amenities = amenitiesField.getText();

        if (name.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Location are required!");
            return;
        }

        Hotel newHotel = new Hotel(name, location, amenities);
        try {
            hotelDAO.addHotel(newHotel);
            JOptionPane.showMessageDialog(this, "Hotel Added Successfully!");
            nameField.setText("");
            locationField.setText("");
            amenitiesField.setText("");
            loadHotels();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteHotel() {
        int selectedRow = hotelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a hotel to delete!");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            hotelDAO.deleteHotel(id);
            JOptionPane.showMessageDialog(this, "Hotel Deleted Successfully!");
            loadHotels();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage() + "\n(Delete Rooms first!)");
        }
    }

    private void loadHotels() {
        try {
            tableModel.setRowCount(0);
            List<Hotel> hotels = hotelDAO.getAllHotels();
            for (Hotel h : hotels) {
                tableModel.addRow(new Object[]{
                    h.getId(), h.getName(), h.getLocation(), h.getAmenities()
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}