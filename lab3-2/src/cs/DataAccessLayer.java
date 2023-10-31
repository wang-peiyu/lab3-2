package cs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataAccessLayer {
    private String url = "jdbc:mysql://localhost/user?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8";
    private String username = "root";
    private String password = "123456";

    public String addContact(String id, String name, String address, String phone) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String insertQuery = "INSERT INTO person (id, name, address, phone) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(insertQuery)) {
                statement.setString(1, id);
                statement.setString(2, name);
                statement.setString(3, address);
                statement.setString(4, phone);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    return "Contact added successfully.";
                } else {
                    return "Failed to add the contact.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public List<String> viewContacts() {
        List<String> contacts = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String selectQuery = "SELECT * FROM person";
            try (PreparedStatement statement = conn.prepareStatement(selectQuery);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String address = resultSet.getString("address");
                    String phone = resultSet.getString("phone");
                    contacts.add("ID: " + id + ", Name: " + name + ", Address: " + address + ", Phone: " + phone);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contacts;
    }

    public String updateContact(String id, String newName, String newAddress, String newPhone) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String updateQuery = "UPDATE person SET name = ?, address = ?, phone = ? WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(updateQuery)) {
                statement.setString(1, newName);
                statement.setString(2, newAddress);
                statement.setString(3, newPhone);
                statement.setString(4, id);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    return "Contact updated successfully.";
                } else {
                    return "No contact found with the specified ID.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String deleteContact(String id) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String deleteQuery = "DELETE FROM person WHERE id = ?";
            try (PreparedStatement statement = conn.prepareStatement(deleteQuery)) {
                statement.setString(1, id);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    return "Contact deleted successfully.";
                } else {
                    return "No contact found with the specified ID.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
