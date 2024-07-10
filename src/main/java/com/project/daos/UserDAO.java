package com.project.daos;

import com.project.models.User;
import com.project.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");

                User user = new User(id, username, password);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    public User selectUserById(int id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // Set the parameter for the placeholder

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");

                    user = new User(id, username, password);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user with id " + id + ": " + e.getMessage());
        }

        return user;
    }

    public User selectUserByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";
    
        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setString(1, username); // Set the parameter for the placeholder
    
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String password = rs.getString("password");
    
                    user = new User(id, username, password);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user with username " + username + ": " + e.getMessage());
        }
    
        return user;
    }

    public User insertNewUser(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?,?)";

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                int generatedUserId = rs.getInt(1);
                user.setId(generatedUserId); // Set the generated id in the user object
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Error creating user with username: " + user.getUsername() + ". " + e.getMessage());
        }
        return null;
    }

    public void deleteUserById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DatabaseConnector.connect();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            // System.out.println("User Deleted");

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }
}
