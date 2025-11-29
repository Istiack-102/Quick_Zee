package com.quickzee.common.dao;

import com.quickzee.common.model.Option;
import com.quickzee.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OptionDao {

    // Insert a new option into the database
    public void insert(Option option) throws SQLException {
        String sql = "INSERT INTO options (question_id, ordinal, text, is_correct) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, option.getQuestion_id());
            ps.setInt(2, option.getOrdinal());  // ← ADDED: Set ordinal
            ps.setString(3, option.getText());
            ps.setInt(4, option.getIs_correct());  // ← FIXED: Was setIs_correct(), now getIs_correct()

            ps.executeUpdate();

            // Get the generated option ID and set it in the object
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    option.setId(rs.getLong(1));
                }
            }
        }
    }

    // Find an option by its ID
    public Option findById(Long id) throws SQLException {
        String sql = "SELECT id, question_id, ordinal, text, is_correct FROM options WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOption(rs);
                }
            }
        }
        return null;
    }

    // Find all options for a specific question
    public List<Option> findByQuestionId(Long questionId) throws SQLException {
        String sql = "SELECT id, question_id, ordinal, text, is_correct FROM options WHERE question_id = ? ORDER BY ordinal";

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
        String sql = "UPDATE options SET question_id = ?, ordinal = ?, text = ?, is_correct = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, option.getQuestion_id());
            ps.setInt(2, option.getOrdinal());  // ← ADDED: Set ordinal
            ps.setString(3, option.getText());
            ps.setInt(4, option.getIs_correct());  // ← FIXED: Was setIs_correct(), now getIs_correct()
            ps.setLong(5, option.getId());

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
        option.setOrdinal(rs.getInt("ordinal"));  // ← ADDED: Map ordinal
        option.setText(rs.getString("text"));
        option.setIs_correct(rs.getInt("is_correct"));  // ← FIXED: Use setIs_correct(Integer)
        return option;
    }
}