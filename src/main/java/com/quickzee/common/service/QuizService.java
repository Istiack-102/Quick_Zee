package com.quickzee.common.service;

import com.quickzee.common.dao.QuizDao;
import com.quickzee.common.dao.QuestionDao;
import com.quickzee.common.dao.OptionDao;
import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.Question;
import com.quickzee.common.model.Option;
import com.quickzee.common.util.InputValidator;
import com.quickzee.common.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

/**
 * QuizService
 * Handles all quiz-related business logic
 * - Creating quizzes
 * - Adding questions and options
 * - Retrieving quizzes
 * - Deleting quizzes
 */
public class QuizService {

    private final QuizDao quizDao;
    private final QuestionDao questionDao;
    private final OptionDao optionDao;

    public QuizService() {
        this.quizDao = new QuizDao();
        this.questionDao = new QuestionDao();
        this.optionDao = new OptionDao();
    }

    /**
     * Create a new quiz (admin only)
     * @param title Quiz title
     * @param semester Semester (can be null)
     * @param durationMinutes Duration in minutes
     * @return Created Quiz object with generated ID
     * @throws IllegalStateException if not admin
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public Quiz createQuiz(String title, Integer semester, Integer durationMinutes)
            throws SQLException {

        // Check admin permission
        SessionManager.requireAdmin();

        // Validate inputs
        if (!InputValidator.isValidQuizTitle(title)) {
            throw new IllegalArgumentException("Quiz title must be between 3 and 255 characters");
        }

        if (semester != null && semester != 0 && !InputValidator.isValidSemester(semester)) {
            throw new IllegalArgumentException("Semester must be between 1 and 8, or 0 for all semesters");
        }

        if (durationMinutes == null || !InputValidator.isValidDuration(durationMinutes)) {
            throw new IllegalArgumentException("Duration must be between 1 and 180 minutes");
        }

        // Create quiz object
        Quiz quiz = new Quiz();
        quiz.setTitle(InputValidator.sanitize(title));
        quiz.setSemester(semester);
        quiz.setDuration_minutes(durationMinutes);

        // Save to database
        quizDao.insert(quiz);

        System.out.println("✅ Quiz created successfully with ID: " + quiz.getId());
        return quiz;
    }

    /**
     * Add a question to a quiz (admin only)
     * @param quizId Quiz ID
     * @param ordinal Question order (1, 2, 3, ...)
     * @param text Question text
     * @return Created Question object with generated ID
     * @throws IllegalStateException if not admin
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public Question addQuestion(Long quizId, Integer ordinal, String text)
            throws SQLException {

        // Check admin permission
        SessionManager.requireAdmin();

        // Validate inputs
        if (quizId == null || quizId <= 0) {
            throw new IllegalArgumentException("Invalid quiz ID");
        }

        if (ordinal == null || ordinal < 1) {
            throw new IllegalArgumentException("Question ordinal must be at least 1");
        }

        if (!InputValidator.isValidQuestionText(text)) {
            throw new IllegalArgumentException("Question text must be between 10 and 5000 characters");
        }

        // Create question object
        Question question = new Question();
        question.setQuiz_id(quizId);
        question.setOrdinal(ordinal);
        question.setText(InputValidator.sanitize(text));

        // Save to database
        questionDao.insert(question);

        System.out.println("✅ Question added successfully with ID: " + question.getId());
        return question;
    }

    /**
     * Add an option to a question (admin only)
     * @param questionId Question ID
     * @param ordinal Option order (0, 1, 2, 3)
     * @param text Option text
     * @param isCorrect Whether this is the correct answer
     * @return Created Option object with generated ID
     * @throws IllegalStateException if not admin
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public Option addOption(Long questionId, Integer ordinal, String text, boolean isCorrect)
            throws SQLException {

        // Check admin permission
        SessionManager.requireAdmin();

        // Validate inputs
        if (questionId == null || questionId <= 0) {
            throw new IllegalArgumentException("Invalid question ID");
        }

        if (ordinal == null || ordinal < 0) {
            throw new IllegalArgumentException("Option ordinal must be at least 0");
        }

        if (!InputValidator.isValidOptionText(text)) {
            throw new IllegalArgumentException("Option text must be between 1 and 1000 characters");
        }

        // Create option object
        Option option = new Option();
        option.setQuestion_id(questionId);
        option.setOrdinal(ordinal);
        option.setText(InputValidator.sanitize(text));
        option.setCorrect(isCorrect);

        // Save to database
        optionDao.insert(option);

        System.out.println("✅ Option added successfully with ID: " + option.getId());
        return option;
    }

    /**
     * Get all quizzes for a specific semester
     * @param semester Semester number
     * @return List of quizzes
     * @throws SQLException if database error occurs
     */
    public List<Quiz> getQuizzesBySemester(int semester) throws SQLException {
        SessionManager.requireLogin();

        if (!InputValidator.isValidSemester(semester)) {
            throw new IllegalArgumentException("Semester must be between 1 and 8");
        }

        return quizDao.findBySemester(semester);
    }

    /**
     * Get all quizzes (admin only)
     * @return List of all quizzes
     * @throws SQLException if database error occurs
     */
    public List<Quiz> getAllQuizzes() throws SQLException {
        SessionManager.requireAdmin();
        return quizDao.findAll();
    }

    /**
     * Get a quiz with all its questions and options
     * @param quizId Quiz ID
     * @return Quiz object with questions and options loaded
     * @throws SQLException if database error occurs
     */
    public Quiz getQuizWithQuestions(Long quizId) throws SQLException {
        SessionManager.requireLogin();

        if (quizId == null || quizId <= 0) {
            throw new IllegalArgumentException("Invalid quiz ID");
        }

        Quiz quiz = quizDao.findByIdWithQuestions(quizId);

        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);
        }

        return quiz;
    }

    /**
     * Delete a quiz (admin only)
     * @param quizId Quiz ID to delete
     * @throws SQLException if database error occurs
     */
    public void deleteQuiz(Long quizId) throws SQLException {
        SessionManager.requireAdmin();

        if (quizId == null || quizId <= 0) {
            throw new IllegalArgumentException("Invalid quiz ID");
        }

        quizDao.deleteById(quizId);
        System.out.println("✅ Quiz deleted successfully");
    }

    /**
     * Get all questions for a quiz
     * @param quizId Quiz ID
     * @return List of questions
     * @throws SQLException if database error occurs
     */
    public List<Question> getQuestionsByQuizId(Long quizId) throws SQLException {
        SessionManager.requireLogin();

        if (quizId == null || quizId <= 0) {
            throw new IllegalArgumentException("Invalid quiz ID");
        }

        return questionDao.findByQuizId(quizId);
    }

    /**
     * Get all options for a question
     * @param questionId Question ID
     * @return List of options
     * @throws SQLException if database error occurs
     */
    public List<Option> getOptionsByQuestionId(Long questionId) throws SQLException {
        SessionManager.requireLogin();

        if (questionId == null || questionId <= 0) {
            throw new IllegalArgumentException("Invalid question ID");
        }

        return optionDao.findByQuestionId(questionId);
    }

    /**
     * Validate that a quiz has at least one question with 4 options each
     * @param quizId Quiz ID
     * @return true if valid, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean isQuizValid(Long quizId) throws SQLException {
        List<Question> questions = questionDao.findByQuizId(quizId);

        if (questions == null || questions.isEmpty()) {
            return false;
        }

        for (Question question : questions) {
            List<Option> options = optionDao.findByQuestionId(question.getId());

            // Each question must have exactly 4 options
            if (options == null || options.size() != 4) {
                return false;
            }

            // At least one option must be correct
            boolean hasCorrectOption = false;
            for (Option option : options) {
                if (option.isCorrect()) {
                    hasCorrectOption = true;
                    break;
                }
            }

            if (!hasCorrectOption) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the total number of questions in a quiz
     * @param quizId Quiz ID
     * @return Number of questions
     * @throws SQLException if database error occurs
     */
    public int getQuestionCount(Long quizId) throws SQLException {
        List<Question> questions = questionDao.findByQuizId(quizId);
        return questions != null ? questions.size() : 0;
    }

    /**
     * Print quiz summary (for debugging/display)
     * @param quizId Quiz ID
     * @throws SQLException if database error occurs
     */
    public void printQuizSummary(Long quizId) throws SQLException {
        Quiz quiz = quizDao.findByIdWithQuestions(quizId);

        if (quiz == null) {
            System.out.println("Quiz not found!");
            return;
        }

        System.out.println("\n=== Quiz Summary ===");
        System.out.println("Title: " + quiz.getTitle());
        System.out.println("Semester: " + quiz.getSemester());
        System.out.println("Duration: " + quiz.getDuration_minutes() + " minutes");
        System.out.println("Total Questions: " + (quiz.getQuestions() != null ? quiz.getQuestions().size() : 0));
        System.out.println("==================\n");
    }
}