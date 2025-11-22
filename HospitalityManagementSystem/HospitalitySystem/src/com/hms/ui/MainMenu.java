package com.hms.ui;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf; // IMPORT THE NEW LIBRARY
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        // 1. Setup the Main Window Frame
        setTitle("Hospitality Management System (Enterprise Edition)");
        setSize(500, 600); // Made it slightly bigger
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new GridLayout(6, 1, 15, 15)); // Increased gaps for cleaner look

        // 2. Create Title Label
        JLabel titleLabel = new JLabel("Hotel Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26)); // Cleaner Font
        titleLabel.setForeground(new Color(33, 150, 243)); // Professional Blue
        add(titleLabel);

        // 3. Create Buttons (Using Helper Method)
        JButton btnAddHotel = createStyledButton("Manage Hotels");
        JButton btnAddRoom = createStyledButton("Manage Rooms");
        JButton btnAddGuest = createStyledButton("Manage Guests");
        JButton btnAddReservation = createStyledButton("New Reservation");
        JButton btnViewData = createStyledButton("Master Dashboard");

        // 4. Add Action Listeners
        btnAddHotel.addActionListener(e -> openAddHotelScreen());
        btnAddRoom.addActionListener(e -> openAddRoomScreen());
        btnAddGuest.addActionListener(e -> openAddGuestScreen());
        btnAddReservation.addActionListener(e -> openAddReservationScreen());
        btnViewData.addActionListener(e -> openViewDataScreen());

        // 5. Add Buttons to the Frame
        add(btnAddHotel);
        add(btnAddRoom);
        add(btnAddGuest);
        add(btnAddReservation);
        add(btnViewData);
    }

    // Helper method to make buttons look modern and uniform
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setFocusPainted(false); // Removes the ugly click border
        return button;
    }

    // --- Navigation Methods ---
    private void openAddHotelScreen() { new HotelScreen().setVisible(true); }
    private void openAddRoomScreen() { new RoomScreen().setVisible(true); }
    private void openAddGuestScreen() { new GuestScreen().setVisible(true); }
    private void openAddReservationScreen() { new ReservationScreen().setVisible(true); }
    private void openViewDataScreen() { new ViewDataScreen().setVisible(true); }

    public static void main(String[] args) {
        // --- THIS IS THE MAGIC STEP ---
        try {
            // Set the theme to FlatLightLaf (Modern White)
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize Modern UI");
        }

        // Ensure the UI runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MainMenu().setVisible(true);
        });
    }
}