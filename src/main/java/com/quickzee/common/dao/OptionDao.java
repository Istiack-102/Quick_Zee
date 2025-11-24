package com.quickzee.common.dao;

import com.quickzee.common.model.Option;
import com.quickzee.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OptionDao {

    // Insert a new option into the database
    public void insert(Option option) throws SQLException {
        String sql = "INSERT INTO options (question_id, text, is_correct) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, option.getQuestion_id());
            ps.setString(2, option.getText());
            ps.setBoolean(3, option.setIs_correct());

            ps.executeUpdate();

            // Get the generated option ID and set it in the object
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    option.setId(rs.getLong(1)); // Set generated ID
                }
            }
        }
    }

    // Find an option by its ID
    public Option findById(Long id) throws SQLException {
        String sql = "SELECT id, question_id, text, is_correct FROM options WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOption(rs);
                }
            }
        }
        return null; // Return null if no option is found
    }

    // Find all options for a specific question
    public List<Option> findByQuestionId(Long questionId) throws SQLException {
        String sql = "SELECT id, question_id, text, is_correct FROM options WHERE question_id = ?";

        List<Option> options = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    options.add(mapRowToOption(rs));
                }
            }
        }
        return options;
    }

    // Update an existing option
    public void update(Option option) throws SQLException {
        String sql = "UPDATE options SET question_id = ?, text = ?, is_correct = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, option.getQuestion_id());
            ps.setString(2, option.getText());
            ps.setBoolean(3, option.setIs_correct());
            ps.setLong(4, option.getId());

            ps.executeUpdate();
        }
    }

    // Delete an option by its ID
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM options WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Helper method to map a ResultSet row to an Option object
    private Option mapRowToOption(ResultSet rs) throws SQLException {
        Option option = new Option();
        option.setId(rs.getLong("id"));
        option.setQuestion_id(rs.getLong("question_id"));
        option.setText(rs.getString("text"));
        option.setCorrect(rs.getBoolean("is_correct"));
        return option;
    }
}
