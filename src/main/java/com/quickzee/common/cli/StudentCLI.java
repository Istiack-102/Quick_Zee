package com.quickzee.common.cli;

import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.Question;
import com.quickzee.common.model.Option;
import com.quickzee.common.model.QuizResult;
import com.quickzee.common.service.AuthService;
import com.quickzee.common.service.QuizService;
import com.quickzee.common.service.AttemptService;
import com.quickzee.common.util.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * StudentCLI - Student dashboard and operations
 * Quiz browsing, taking, and history viewing
 */
public class StudentCLI {

    private final AuthService authService;
    private final QuizService quizService;
    private final AttemptService attemptService;

    public StudentCLI() {
        this.authService = new AuthService();
        this.quizService = new QuizService();
        this.attemptService = new AttemptService();
    }

    /**
     * Show student menu
     */
    public void showMenu() {
        while (true) {
            displayMenu();

            int choice = CLIHelper.readInt("Choose an option: ", 0, 6);

            switch (choice) {
                case 1:
                    browseQuizzes();
                    break;
                case 2:
                    takeQuiz();
                    break;
                case 3:
                    viewHistory();
                    break;
                case 4:
                    viewStatistics();
                    break;
                case 5:
                    updateProfile();
                    break;
                case 6:
                    viewProfile();
                    break;
                case 0:
                    logout();
                    return;
            }
        }
    }

    /**
     * Display student menu
     */
    private void displayMenu() {
        CLIHelper.printHeader("STUDENT DASHBOARD");
        System.out.println("Logged in as: " + SessionManager.getLoggedInUserName());
        Integer semester = SessionManager.getLoggedInUserSemester();
        if (semester != null) {
            System.out.println("Semester: " + semester);
        }
        System.out.println();
        System.out.println("1. Browse Available Quizzes");
        System.out.println("2. Take a Quiz");
        System.out.println("3. View My Quiz History");
        System.out.println("4. View My Statistics");
        System.out.println("5. Update Profile");
        System.out.println("6. View Profile");
        System.out.println("0. Logout");
        CLIHelper.printSeparator();
    }

    /**
     * Browse available quizzes for student's semester
     */
    private void browseQuizzes() {
        CLIHelper.printHeader("AVAILABLE QUIZZES");

        Integer semester = SessionManager.getLoggedInUserSemester();

        if (semester == null) {
            CLIHelper.printError("Please set your semester in profile first");
            CLIHelper.pause();
            return;
        }

        try {
            List<Quiz> quizzes = quizService.getQuizzesBySemester(semester);

            if (quizzes.isEmpty()) {
                CLIHelper.printInfo("No quizzes available for semester " + semester);
            } else {
                System.out.printf("%-5s %-35s %-12s%n", "ID", "Title", "Duration");
                CLIHelper.printSeparator();

                for (Quiz quiz : quizzes) {
                    System.out.printf("%-5d %-35s %-12s%n",
                            quiz.getId(),
                            quiz.getTitle().length() > 35 ? quiz.getTitle().substring(0, 32) + "..." : quiz.getTitle(),
                            quiz.getDuration_minutes() + " min"
                    );
                }
            }

            CLIHelper.pause();

        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
        }
    }

    /**
     * Take a quiz
     */
    private void takeQuiz() {
        CLIHelper.printHeader("TAKE QUIZ");

        Long quizId = CLIHelper.readLong("Enter Quiz ID (or 0 to cancel): ");

        if (quizId == null || quizId == 0) {
            return;
        }

        try {
            // Load quiz with questions
            Quiz quiz = attemptService.startQuizAttempt(quizId);

            // Display quiz info
            System.out.println();
            System.out.println("Quiz: " + quiz.getTitle());
            System.out.println("Duration: " + quiz.getDuration_minutes() + " minutes");
            System.out.println("Questions: " + quiz.getQuestions().size());
            System.out.println();
            if (!CLIHelper.readYesNo("Start quiz?")) {
                CLIHelper.printInfo("Quiz cancelled");
                CLIHelper.pause();
                return;
            }

            // Collect answers
            List<Long> selectedAnswers = new ArrayList<>();

            for (int i = 0; i < quiz.getQuestions().size(); i++) {
                Question question = quiz.getQuestions().get(i);

                CLIHelper.printSubHeader("QUESTION " + (i + 1) + " of " + quiz.getQuestions().size());
                System.out.println(question.getText());
                System.out.println();

                // Display options
                for (Option option : question.getOptions()) {
                    System.out.println((option.getOrdinal() + 1) + ". " + option.getText());
                }

                System.out.println();
                int answer = CLIHelper.readInt("Your answer (1-4, or 0 to skip): ", 0, 4);

                if (answer == 0) {
                    selectedAnswers.add(null); // Skipped
                } else {
                    // Find the option ID for the selected answer
                    Option selectedOption = question.getOptions().get(answer - 1);
                    selectedAnswers.add(selectedOption.getId());
                }
            }

            // Submit quiz
            CLIHelper.printInfo("Submitting quiz...");
            QuizResult result = attemptService.submitQuizAttempt(quizId, selectedAnswers);

            // Display result
            attemptService.printDetailedResult(result);
            CLIHelper.pause();

        } catch (IllegalArgumentException e) {
            CLIHelper.printError(e.getMessage());
            CLIHelper.pause();
        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
        }
    }

    /**
     * View quiz attempt history
     */
    private void viewHistory() {
        CLIHelper.printHeader("MY QUIZ HISTORY");

        try {
            attemptService.printMyAttemptHistory();
            CLIHelper.pause();

        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
        }
    }

    /**
     * View statistics
     */
    private void viewStatistics() {
        CLIHelper.printHeader("MY STATISTICS");

        try {
            List<QuizResult> attempts = attemptService.getMyAttempts();

            if (attempts.isEmpty()) {
                CLIHelper.printInfo("No quiz attempts yet");
            } else {
                double avgScore = attemptService.getMyAverageScore();
                double bestScore = attemptService.getMyBestScore();

                System.out.println("Total Attempts: " + attempts.size());
                System.out.println("Average Score: " + String.format("%.2f%%", avgScore));
                System.out.println("Best Score: " + String.format("%.2f%%", bestScore));

                // Count passed/failed
                int passed = 0;
                for (QuizResult result : attempts) {
                    if (attemptService.isPassed(result)) {
                        passed++;
                    }
                }

                System.out.println("Passed: " + passed);
                System.out.println("Failed: " + (attempts.size() - passed));
            }

            CLIHelper.pause();

        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
        }
    }

    /**
     * Update profile
     */
    private void updateProfile() {
        CLIHelper.printHeader("UPDATE PROFILE");

        System.out.println("Leave blank to keep current value");
        System.out.println();

        String newName = CLIHelper.readString("New Name: ");
        Integer newSemester = CLIHelper.readInt("New Semester (1-8): ");

        try {
            authService.updateProfile(
                    newName.isEmpty() ? null : newName,
                    newSemester
            );
            CLIHelper.pause();

        } catch (IllegalArgumentException e) {
            CLIHelper.printError("Validation error: " + e.getMessage());
            CLIHelper.pause();
        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
        }
    }

    /**
     * View profile
     */
    private void viewProfile() {
        CLIHelper.printHeader("MY PROFILE");
        authService.printCurrentUserInfo();
        CLIHelper.pause();
    }

    /**
     * Logout
     */
    private void logout() {
        authService.logout();
        CLIHelper.pause();
    }
}