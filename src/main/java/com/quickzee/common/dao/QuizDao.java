package com.quickzee.common.dao;

import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.Question;
import com.quickzee.common.model.Option;
import com.quickzee.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDao {

    /**
     * Find all quizzes for a given semester.
     * This is typically used for the dashboard list.
     * It does NOT load questions/options, only quiz header info.
     */
    public List<Quiz> findBySemester(int semester) throws SQLException {
        String sql = "SELECT id, title, semester, duration_minutes " +
                "FROM quizzes WHERE semester = ? ORDER BY id";

        List<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, semester);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(mapRowToQuiz(rs));
                }
            }
        }
        return quizzes;
    }

    /**
     * Find a single quiz by id and load all its questions and options.
     * This is used when the user actually starts a quiz.
     */
    public Quiz findByIdWithQuestions(long quizId) throws SQLException {
        String sql = "SELECT id, title, semester, duration_minutes " +
                "FROM quizzes WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, quizId);

            Quiz quiz = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    quiz = mapRowToQuiz(rs);
                } else {
                    return null; // quiz not found
                }
            }

            // Load questions (and their options) for this quiz
            List<Question> questions = loadQuestionsForQuiz(conn, quizId);
            quiz.setQuestions(questions);

            return quiz;
        }
    }



    /**
     * Map one row from the quizzes table to a Quiz object (without questions).
     */
    private Quiz mapRowToQuiz(ResultSet rs) throws SQLException {
        Quiz q = new Quiz();
        q.setId(rs.getLong("id"));
        q.setTitle(rs.getString("title"));

        int sem = rs.getInt("semester");
        if (!rs.wasNull()) {
            q.setSemester(sem);
        }

        int duration = rs.getInt("duration_minutes");
        if (!rs.wasNull()) {
            q.setDuration_minutes(duration);
        }

        // questions will be loaded separately
        return q;
    }

    /**
     * Load all questions for a quiz, including each question's options.
     */
    private List<Question> loadQuestionsForQuiz(Connection conn, long quizId) throws SQLException {
        String sql = "SELECT id, quiz_id, ordinal, text " +
                "FROM questions WHERE quiz_id = ? ORDER BY ordinal";

        List<Question> questions = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question q = new Question();
                    q.setId(rs.getLong("id"));
                    q.setQuiz_id(rs.getLong("quiz_id"));
                    q.setOrdinal(rs.getInt("ordinal"));
                    q.setText(rs.getString("text"));

                    // Load options for this question
                    List<Option> options = loadOptionsForQuestion(conn, q.getId());
                    q.setOptions(options);

                    questions.add(q);
                }
            }
        }
        return questions;
    }

    /**
     * Load all options for a given question.
     */
    private List<Option> loadOptionsForQuestion(Connection conn, long questionId) throws SQLException {
        String sql = "SELECT id, question_id, ordinal, text, is_correct " +
                "FROM options WHERE question_id = ? ORDER BY ordinal";

        List<Option> options = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Option opt = new Option();
                    opt.setId(rs.getLong("id"));
                    opt.setQuestion_id(rs.getLong("question_id"));
                    opt.setOrdinal(rs.getInt("ordinal"));
                    opt.setText(rs.getString("text"));
                    opt.setIs_correct(rs.getInt("is_correct"));
                    options.add(opt);
                }
            }
        }
        return options;
    }

// Additional helpful methods for admin functionality

    /**
     * Insert a new quiz (without questions)
     */
    public void insert(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (title, semester, duration_minutes) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, quiz.getTitle());
            if (quiz.getSemester() != null) {
                ps.setInt(2, quiz.getSemester());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, quiz.getDuration_minutes() != null ? quiz.getDuration_minutes() : 15);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    quiz.setId(rs.getLong(1));
                }
            }
        }
    }

    /**
     * Get all quizzes (for admin view)
     */
    public List<Quiz> findAll() throws SQLException {
        String sql = "SELECT id, title, semester, duration_minutes FROM quizzes ORDER BY id DESC";

        List<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                quizzes.add(mapRowToQuiz(rs));
            }
        }
        return quizzes;
    }

    /**
     * Delete a quiz by ID
     */
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM quizzes WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}