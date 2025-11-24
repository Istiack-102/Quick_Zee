package com.quickzee.common.dao;

import com.quickzee.common.model.User;
import com.quickzee.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao{

    // Create (register)
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, semester, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());  // TODO: hash password later
            if (user.getSemester() != null) {
                ps.setInt(4, user.getSemester());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, user.getRole() != null ? user.getRole() : "user");

            ps.executeUpdate();

            // Get generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
        }
    }

    // Read: find by id
    public User findById(Long id) throws SQLException {
        String sql = "SELECT id, name, email, password, semester, role FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    // Read: find by email (for login)
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, password, semester, role FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    // Read: find all users (optional)
    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, name, email, password, semester, role FROM users";
        List<User> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        }
        return list;
    }

    // Update
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, semester = ?, role = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            if (user.getSemester() != null) {
                ps.setInt(4, user.getSemester());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, user.getRole());
            ps.setLong(6, user.getId());

            ps.executeUpdate();
        }
    }

    // Delete
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Login helper (simple, without hashing)
    public User login(String email, String password) throws SQLException {
        String sql = "SELECT id, name, email, password, semester, role FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);  // TODO: compare hashed password later

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null; // invalid credentials
    }

    // ==== Private helper ====
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        int sem = rs.getInt("semester");
        if (!rs.wasNull()) {
            u.setSemester(sem);
        }
        u.setRole(rs.getString("role"));
        return u;
    }
}
