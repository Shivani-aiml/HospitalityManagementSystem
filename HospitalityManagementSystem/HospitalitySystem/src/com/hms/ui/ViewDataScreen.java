package com.hms.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;
import com.hms.dao.ReservationDAO;

public class ViewDataScreen extends JFrame {
    private JTable reportTable;
    private DefaultTableModel model;
    private ReservationDAO resDAO;
    private JTextField searchField;

    public ViewDataScreen() {
        resDAO = new ReservationDAO();

        setTitle("Master Command Center");
        setSize(1200, 700); // Big screen for big data
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP TOOLBAR ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(new Color(240, 240, 240));
        
        JLabel searchLabel = new JLabel("Search System:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        searchField = new JTextField(30);
        searchField.setToolTipText("Search by Guest Name, Hotel Name, or Room Number");
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(33, 150, 243)); // Blue
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> loadData(searchField.getText()));

        JButton refreshBtn = new JButton("Reset View");
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadData("");
        });

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        // --- THE MASTER TABLE ---
        // Notice: NO IDs displayed except Reservation ID for reference
        String[] columns = {"Res ID", "Guest Name", "Room No", "Hotel Branch", "Check-In", "Check-Out", "Price", "Status"};
        model = new DefaultTableModel(columns, 0);
        
        reportTable = new JTable(model);
        reportTable.setRowHeight(30); // Taller rows for readability
        reportTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        reportTable.setAutoCreateRowSorter(true); // Click headers to sort!

        loadData(""); // Load all data initially

        add(new JScrollPane(reportTable), BorderLayout.CENTER);
    }

    private void loadData(String searchTerm) {
        try {
            model.setRowCount(0); // Clear table
            // Fetch the "Human Readable" data
            Vector<Vector<String>> data = resDAO.getMasterReport(searchTerm);
            
            for (Vector<String> row : data) {
                model.addRow(row);
            }
            
            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No records found for: " + searchTerm);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }
}