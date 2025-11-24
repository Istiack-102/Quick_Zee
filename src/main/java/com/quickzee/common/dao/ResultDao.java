package com.quickzee.common.dao;

import com.quickzee.common.model.QuizResult;
import com.quickzee.common.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ResultDAO
 * Handles quiz submission, score calculation,
 * and saving data into quiz_results and quiz_result_answers tables.
 */
public class ResultDao {

    /**
     * Submit a quiz attempt.
     *
     * @param userId            The ID of the user taking the quiz
     * @param quizId            The ID of the quiz (quizzes.id)
     * @param selectedOptionIds List of selected option IDs, one per question
     *                          in the same order as questions.ordinal.
     *                          If a question is skipped, the element can be null or -1.
     */
    public QuizResult submitQuiz(long userId,
                                 long quizId,
                                 List<Long> selectedOptionIds) throws SQLException {

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1) Load all question IDs for this quiz (ordered by ordinal)
            List<Long> questionIds = loadQuestionIdsForQuiz(conn, quizId);

            int totalQuestions = questionIds.size();
            if (totalQuestions == 0) {
                throw new SQLException("No questions found for quizId=" + quizId);
            }

            // 2) Build a map of all options' correctness (optionId -> isCorrect)
            Map<Long, Boolean> optionCorrectMap = loadOptionCorrectMap(conn, questionIds);

            // Optional: map optionId -> questionId (if needed later)
            Map<Long, Long> optionToQuestionMap = loadOptionToQuestionMap(conn, questionIds);

            // 3) Calculate score and prepare answer data
            int score = 0;
            List<AnswerRow> answerRows = new ArrayList<>();

            for (int i = 0; i < totalQuestions; i++) {
                Long questionId = questionIds.get(i);

                Long selectedOptionId = null;
                if (selectedOptionIds != null && i < selectedOptionIds.size()) {
                    Long candidate = selectedOptionIds.get(i);
                    if (candidate != null && candidate > 0) {
                        selectedOptionId = candidate;
                    }
                }

                // Score calculation
                if (selectedOptionId != null) {
                    boolean isCorrect = optionCorrectMap.getOrDefault(selectedOptionId, false);
                    if (isCorrect) {
                        score++;
                    }
                }

                // Keep data to insert later into quiz_result_answers
                AnswerRow row = new AnswerRow();
                row.questionId = questionId;
                row.selectedOptionId = selectedOptionId;
                answerRows.add(row);
            }

            // 4) Insert one row into quiz_results
            long resultId = insertQuizResult(conn, userId, quizId, score, totalQuestions);

            // 5) Insert one row per question into quiz_result_answers
            insertQuizResultAnswers(conn, resultId, answerRows);

            // 6) Commit transaction if everything succeeded
            conn.commit();

            // 7) Build Java-side QuizResult object to return
            QuizResult result = new QuizResult();
            result.setId(resultId);
            result.setUserId(userId);
            result.setQuizId(quizId);
            result.setScore(score);
            result.setTotalQuestions(totalQuestions);
            result.setSubmittedAt(LocalDateTime.now()); // approximately matches DB time

            return result;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Roll back everything on error
                } catch (SQLException ignore) {
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    /**
     * Find all quiz results for a given user (optional usage).
     */
    public List<QuizResult> findByUserId(long userId) throws SQLException {
        String sql = "SELECT id, user_id, quiz_id, score, total_questions, submitted_at " +
                "FROM quiz_results WHERE user_id = ? ORDER BY submitted_at DESC";

        List<QuizResult> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizResult r = new QuizResult();
                    r.setId(rs.getLong("id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setQuizId(rs.getLong("quiz_id"));
                    r.setScore(rs.getInt("score"));
                    r.setTotalQuestions(rs.getInt("total_questions"));

                    Timestamp ts = rs.getTimestamp("submitted_at");
                    if (ts != null) {
                        r.setSubmittedAt(ts.toLocalDateTime());
                    }

                    list.add(r);
                }
            }
        }
        return list;
    }

    // ==================== Private helper methods ====================

    // Load all question IDs for a quiz, ordered by ordinal
    private List<Long> loadQuestionIdsForQuiz(Connection conn, long quizId) throws SQLException {
        String sql = "SELECT id FROM questions WHERE quiz_id = ? ORDER BY ordinal";
        List<Long> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getLong("id"));
                }
            }
        }
        return list;
    }

    // Build a map of optionId -> isCorrect for all options under given questions
    private Map<Long, Boolean> loadOptionCorrectMap(Connection conn,
                                                    List<Long> questionIds) throws SQLException {
        Map<Long, Boolean> map = new HashMap<>();
        if (questionIds.isEmpty()) return map;

        StringBuilder sb = new StringBuilder(
                "SELECT id, is_correct FROM options WHERE question_id IN ("
        );
        for (int i = 0; i < questionIds.size(); i++) {
            sb.append("?");
            if (i < questionIds.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");

        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < questionIds.size(); i++) {
                ps.setLong(i + 1, questionIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long optionId = rs.getLong("id");
                    boolean isCorrect = rs.getBoolean("is_correct");
                    map.put(optionId, isCorrect);
                }
            }
        }
        return map;
    }

    // Build a map of optionId -> questionId (useful if you need reverse mapping)
    private Map<Long, Long> loadOptionToQuestionMap(Connection conn,
                                                    List<Long> questionIds) throws SQLException {
        Map<Long, Long> map = new HashMap<>();
        if (questionIds.isEmpty()) return map;

        StringBuilder sb = new StringBuilder(
                "SELECT id, question_id FROM options WHERE question_id IN ("
        );
        for (int i = 0; i < questionIds.size(); i++) {
            sb.append("?");
            if (i < questionIds.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");

        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < questionIds.size(); i++) {
                ps.setLong(i + 1, questionIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long optionId = rs.getLong("id");
                    long qId = rs.getLong("question_id");
                    map.put(optionId, qId);
                }
            }
        }
        return map;
    }

    // Insert one row into quiz_results and return the generated result ID
    private long insertQuizResult(Connection conn,
                                  long userId,
                                  long quizId,
                                  int score,
                                  int totalQuestions) throws SQLException {

        String sql = "INSERT INTO quiz_results " +
                "(user_id, quiz_id, score, total_questions, submitted_at, created_at) " +
                "VALUES (?, ?, ?, ?, NOW(), NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setLong(2, quizId);
            ps.setInt(3, score);
            ps.setInt(4, totalQuestions);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Failed to get generated id for quiz_results");
                }
            }
        }
    }

    // Insert all answers into quiz_result_answers (one row per question)
    private void insertQuizResultAnswers(Connection conn,
                                         long resultId,
                                         List<AnswerRow> answers) throws SQLException {

        String sql = "INSERT INTO quiz_result_answers " +
                "(result_id, question_id, selected_option_id, created_at) " +
                "VALUES (?, ?, ?, NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (AnswerRow row : answers) {
                ps.setLong(1, resultId);
                ps.setLong(2, row.questionId);

                if (row.selectedOptionId == null) {
                    // If user skipped the question, store NULL
                    ps.setNull(3, Types.BIGINT);
                } else {
                    ps.setLong(3, row.selectedOptionId);
                }

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // Internal helper class to carry per-question data before insert
    private static class AnswerRow {
        long questionId;
        Long selectedOptionId; // null = skipped
    }
}
