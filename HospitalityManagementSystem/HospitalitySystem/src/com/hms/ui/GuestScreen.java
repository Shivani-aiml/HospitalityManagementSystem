package com.hms.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.hms.dao.GuestDAO;
import com.hms.models.Guest;

public class GuestScreen extends JFrame {
    private JTextField nameField, emailField, phoneField;
    private JTable guestTable;
    private DefaultTableModel tableModel;
    private GuestDAO guestDAO;

    public GuestScreen() {
        guestDAO = new GuestDAO();

        setTitle("Manage Guests");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Register / Manage Guests"));

        inputPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email Address:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);

        // Standard Add Button
        JButton addButton = new JButton("Register Guest");
        addButton.setBackground(new Color(0, 153, 76));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addGuest());
        inputPanel.add(addButton);

        // --- FIXED DELETE BUTTON ---
        JButton deleteButton = new JButton("Delete Selected Guest");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteGuest());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        String[] columnNames = {"Guest ID", "Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0);
        guestTable = new JTable(tableModel);
        guestTable.setAutoCreateRowSorter(true); // Enable Sorting
        
        loadGuests();

        add(new JScrollPane(guestTable), BorderLayout.CENTER);
    }

    private void addGuest() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required!");
            return;
        }

        Guest newGuest = new Guest(name, email, phone);
        try {
            guestDAO.addGuest(newGuest);
            JOptionPane.showMessageDialog(this, "Guest Registered Successfully!");
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            loadGuests();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteGuest() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a guest to delete!");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            guestDAO.deleteGuest(id);
            JOptionPane.showMessageDialog(this, "Guest Deleted Successfully!");
            loadGuests();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Cannot delete guest with active bookings.");
        }
    }

    private void loadGuests() {
        try {
            tableModel.setRowCount(0);
            List<Guest> guests = guestDAO.getAllGuests();
            for (Guest g : guests) {
                tableModel.addRow(new Object[]{
                    g.getId(), g.getName(), g.getEmail(), g.getPhone()
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}