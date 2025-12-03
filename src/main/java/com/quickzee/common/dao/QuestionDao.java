package com.quickzee.common.dao;

import com.quickzee.common.model.Question;
import com.quickzee.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao {

    // Insert a new question into the database
    public void insert(Question question) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, ordinal, text) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, question.getQuiz_id());
            ps.setInt(2, question.getOrdinal());
            ps.setString(3, question.getText());

            ps.executeUpdate();

            // Get the generated question ID and set it in the object
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    question.setId(rs.getLong(1));
                }
            }
        }
    }

    // Find a question by its ID
    public Question findById(Long id) throws SQLException {
        String sql = "SELECT id, quiz_id, ordinal, text FROM questions WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuestion(rs);
                }
            }
        }
        return null;
    }

    // Find all questions for a specific quiz, ordered by ordinal
    public List<Question> findByQuizId(Long quizId) throws SQLException {
        String sql = "SELECT id, quiz_id, ordinal, text FROM questions WHERE quiz_id = ? ORDER BY ordinal";

        List<Question> questions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapRowToQuestion(rs));
                }
            }
        }
        return questions;
    }

    // Update an existing question
    public void update(Question question) throws SQLException {
        String sql = "UPDATE questions SET text = ?, ordinal = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, question.getText());
            ps.setInt(2, question.getOrdinal());
            ps.setLong(3, question.getId());

            ps.executeUpdate();
        }
    }

    // Delete a question by its ID
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Helper method to map a ResultSet row to a Question object
    private Question mapRowToQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setQuiz_id(rs.getLong("quiz_id"));  // ← FIXED: Was setId(), now setQuiz_id()
        question.setOrdinal(rs.getInt("ordinal"));
        question.setText(rs.getString("text"));
        return question;
    }
}