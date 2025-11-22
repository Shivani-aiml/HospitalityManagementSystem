# 🏨 Hospitality Management System (Enterprise Edition)

A comprehensive, full-stack desktop application designed to streamline hotel operations. Built with **Java Core**, **Swing (FlatLaf)**, and **MySQL**, this system handles the complete lifecycle of hotel management—from inventory control to guest reservations with conflict detection.

## 🚀 Key Features

* **Master Dashboard:** A unified command center with real-time data visualization and global search capabilities (SQL JOINs).
* **Smart Booking Engine:**
    * **Cascading Selection:** Hotel -> Room -> Guest logic prevents data mismatch.
    * **Conflict Detection Algorithm:** $O(1)$ check prevents double-bookings and ensures room availability.
    * **Anti-Scam Logic:** Prevents duplicate active bookings for the same guest.
* **Transactional Integrity:** Full ACID compliance using JDBC Transactions to sync Reservations and Room Inventory.
* **Modern UI/UX:** Implemented `FlatLaf` for a professional, flat-design interface with intuitive navigation.

## 🛠 Tech Stack

* **Language:** Java (JDK 21)
* **Frontend:** Java Swing + FlatLaf Library
* **Database:** MySQL 8.0
* **Architecture:** MVC (Model-View-Controller) & DAO Design Pattern

## 📸 Screenshots

*(Upload screenshots of your "Master Dashboard" and "Reservation Screen" here later!)*

## ⚙️ Installation & Setup

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/YOUR_USERNAME/HospitalityManagementSystem.git](https://github.com/YOUR_USERNAME/HospitalityManagementSystem.git)
    ```
2.  **Database Setup:**
    * Import the `schema_reset.sql` file into MySQL Workbench.
    * Update `src/com/hms/db/DatabaseConnector.java` with your MySQL password.
3.  **Run the App:**
    * Open in Eclipse/IntelliJ.
    * Run `src/com/hms/ui/MainMenu.java`.

## 📄 License
This project is open-source and available under the MIT License.
