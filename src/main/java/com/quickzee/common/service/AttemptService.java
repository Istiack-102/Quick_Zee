package com.quickzee.common.service;

import com.quickzee.common.dao.ResultDao;
import com.quickzee.common.dao.QuizDao;
import com.quickzee.common.model.QuizResult;
import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.Question;
import com.quickzee.common.model.Option;
import com.quickzee.common.util.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * AttemptService
 * Handles all quiz attempt-related business logic
 * - Starting quiz attempts
 * - Submitting quiz answers
 * - Calculating scores
 * - Retrieving attempt history
 */
public class AttemptService {

    private final ResultDao resultDao;
    private final QuizDao quizDao;

    public AttemptService() {
        this.resultDao = new ResultDao();
        this.quizDao = new QuizDao();
    }

    /**
     * Start a quiz attempt (load quiz with questions)
     * @param quizId Quiz ID
     * @return Quiz object with all questions and options
     * @throws IllegalStateException if not logged in
     * @throws IllegalArgumentException if quiz not found
     * @throws SQLException if database error occurs
     */
    public Quiz startQuizAttempt(Long quizId) throws SQLException {
        SessionManager.requireLogin();

        if (quizId == null || quizId <= 0) {
            throw new IllegalArgumentException("Invalid quiz ID");
        }

        Quiz quiz = quizDao.findByIdWithQuestions(quizId);

        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("This quiz has no questions yet");
        }

        System.out.println("✅ Quiz loaded: " + quiz.getTitle());
        System.out.println("   Questions: " + quiz.getQuestions().size());
        System.out.println("   Duration: " + quiz.getDuration_minutes() + " minutes");

        return quiz;
    }

    /**
     * Submit a quiz attempt
     * @param quizId Quiz ID
     * @param selectedOptionIds List of selected option IDs (one per question, in order)
     * @return QuizResult object with score
     * @throws IllegalStateException if not logged in
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public QuizResult submitQuizAttempt(Long quizId, List<Long> selectedOptionIds)
            throws SQLException {

        SessionManager.requireLogin();

        if (quizId == null || quizId <= 0) {
            throw new IllegalArgumentException("Invalid quiz ID");
        }

        if (selectedOptionIds == null) {
            throw new IllegalArgumentException("No answers provided");
        }

        Long userId = SessionManager.getLoggedInUserId();

        // Submit through DAO (which handles scoring)
        QuizResult result = resultDao.submitQuiz(userId, quizId, selectedOptionIds);

        System.out.println("✅ Quiz submitted successfully!");
        System.out.println("   Score: " + result.getScore() + "/" + result.getTotalQuestions());

        return result;
    }

    /**
     * Get all quiz attempts for the logged-in user
     * @return List of QuizResult objects
     * @throws IllegalStateException if not logged in
     * @throws SQLException if database error occurs
     */
    public List<QuizResult> getMyAttempts() throws SQLException {
        SessionManager.requireLogin();

        Long userId = SessionManager.getLoggedInUserId();
        return resultDao.findByUserId(userId);
    }

    /**
     * Get all quiz attempts for a specific user (admin only)
     * @param userId User ID
     * @return List of QuizResult objects
     * @throws IllegalStateException if not admin
     * @throws SQLException if database error occurs
     */
    public List<QuizResult> getAttemptsByUser(Long userId) throws SQLException {
        SessionManager.requireAdmin();

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return resultDao.findByUserId(userId);
    }

    /**
     * Calculate percentage score
     * @param result QuizResult object
     * @return Percentage (0-100)
     */
    public double calculatePercentage(QuizResult result) {
        if (result == null || result.getTotalQuestions() == 0) {
            return 0.0;
        }

        return (result.getScore() * 100.0) / result.getTotalQuestions();
    }

    /**
     * Get grade based on percentage
     * @param percentage Percentage score (0-100)
     * @return Grade string (A+, A, B+, etc.)
     */
    public String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 85) return "A";
        if (percentage >= 80) return "A-";
        if (percentage >= 75) return "B+";
        if (percentage >= 70) return "B";
        if (percentage >= 65) return "B-";
        if (percentage >= 60) return "C+";
        if (percentage >= 55) return "C";
        if (percentage >= 50) return "C-";
        if (percentage >= 45) return "D";
        return "F";
    }

    /**
     * Check if user passed (score >= 50%)
     * @param result QuizResult object
     * @return true if passed, false otherwise
     */
    public boolean isPassed(QuizResult result) {
        return calculatePercentage(result) >= 50.0;
    }

    /**
     * Print detailed result
     * @param result QuizResult object
     */
    public void printDetailedResult(QuizResult result) {
        if (result == null) {
            System.out.println("No result to display");
            return;
        }

        double percentage = calculatePercentage(result);
        String grade = getGrade(percentage);
        boolean passed = isPassed(result);

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         QUIZ RESULT SUMMARY          ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║ Score:          %2d / %-2d              ║%n",
                result.getScore(), result.getTotalQuestions());
        System.out.printf("║ Percentage:     %.2f%%              ║%n", percentage);
        System.out.printf("║ Grade:          %-2s                  ║%n", grade);
        System.out.printf("║ Status:         %-6s              ║%n",
                passed ? "PASSED" : "FAILED");
        System.out.println("╚══════════════════════════════════════╝\n");
    }

    /**
     * Print all attempts for logged-in user
     * @throws SQLException if database error occurs
     */
    public void printMyAttemptHistory() throws SQLException {
        List<QuizResult> attempts = getMyAttempts();

        if (attempts == null || attempts.isEmpty()) {
            System.out.println("📋 No quiz attempts found");
            return;
        }

        System.out.println("\n=== MY QUIZ ATTEMPT HISTORY ===");
        System.out.println("Total Attempts: " + attempts.size());
        System.out.println("─".repeat(60));

        for (int i = 0; i < attempts.size(); i++) {
            QuizResult result = attempts.get(i);
            double percentage = calculatePercentage(result);
            String grade = getGrade(percentage);

            System.out.printf("%d. Quiz ID: %d | Score: %d/%d (%.1f%%) | Grade: %s | Date: %s%n",
                    i + 1,
                    result.getQuizId(),
                    result.getScore(),
                    result.getTotalQuestions(),
                    percentage,
                    grade,
                    result.getSubmittedAt() != null ? result.getSubmittedAt().toString() : "N/A"
            );
        }
        System.out.println("═".repeat(60) + "\n");
    }

    /**
     * Get average score across all attempts for logged-in user
     * @return Average percentage (0-100)
     * @throws SQLException if database error occurs
     */
    public double getMyAverageScore() throws SQLException {
        List<QuizResult> attempts = getMyAttempts();

        if (attempts == null || attempts.isEmpty()) {
            return 0.0;
        }

        double totalPercentage = 0.0;
        for (QuizResult result : attempts) {
            totalPercentage += calculatePercentage(result);
        }

        return totalPercentage / attempts.size();
    }

    /**
     * Get best score for logged-in user
     * @return Best percentage (0-100)
     * @throws SQLException if database error occurs
     */
    public double getMyBestScore() throws SQLException {
        List<QuizResult> attempts = getMyAttempts();

        if (attempts == null || attempts.isEmpty()) {
            return 0.0;
        }

        double bestPercentage = 0.0;
        for (QuizResult result : attempts) {
            double percentage = calculatePercentage(result);
            if (percentage > bestPercentage) {
                bestPercentage = percentage;
            }
        }

        return bestPercentage;
    }

    /**
     * Validate selected answers before submission
     * @param quiz Quiz object with questions
     * @param selectedOptionIds List of selected option IDs
     * @return true if valid, false otherwise
     */
    public boolean validateAnswers(Quiz quiz, List<Long> selectedOptionIds) {
        if (quiz == null || quiz.getQuestions() == null) {
            return false;
        }

        int questionCount = quiz.getQuestions().size();

        if (selectedOptionIds == null || selectedOptionIds.size() != questionCount) {
            System.out.println("❌ Answer count mismatch. Expected: " + questionCount +
                    ", Got: " + (selectedOptionIds != null ? selectedOptionIds.size() : 0));
            return false;
        }

        return true;
    }

    /**
     * Create a list of null answers (for skipped questions)
     * @param questionCount Number of questions
     * @return List with null values
     */
    public List<Long> createEmptyAnswerList(int questionCount) {
        List<Long> answers = new ArrayList<>();
        for (int i = 0; i < questionCount; i++) {
            answers.add(null);
        }
        return answers;
    }
}