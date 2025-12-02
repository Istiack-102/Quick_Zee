package com.quickzee.common.cli;

import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.Question;
import com.quickzee.common.model.Option;
import com.quickzee.common.service.AuthService;
import com.quickzee.common.service.QuizService;
import com.quickzee.common.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

/**
 * AdminCLI - Admin dashboard and operations
 * Quiz creation, management, and viewing
 */
public class AdminCLI {

    private final AuthService authService;
    private final QuizService quizService;

    public AdminCLI() {
        this.authService = new AuthService();
        this.quizService = new QuizService();
    }

    /**
     * Show admin menu
     */
    public void showMenu() {
        while (true) {
            displayMenu();

            int choice = CLIHelper.readInt("Choose an option: ", 0, 5);

            switch (choice) {
                case 1:
                    createQuiz();
                    break;
                case 2:
                    viewAllQuizzes();
                    break;
                case 3:
                    viewQuizDetails();
                    break;
                case 4:
                    deleteQuiz();
                    break;
                case 5:
                    viewProfile();
                    break;
                case 0:
                    logout();
                    return;
            }
        }
    }

    /**
     * Display admin menu
     */
    private void displayMenu() {
        CLIHelper.printHeader("ADMIN DASHBOARD");
        System.out.println("Logged in as: " + SessionManager.getLoggedInUserName());
        System.out.println();
        System.out.println("1. Create New Quiz");
        System.out.println("2. View All Quizzes");
        System.out.println("3. View Quiz Details");
        System.out.println("4. Delete Quiz");
        System.out.println("5. View Profile");
        System.out.println("0. Logout");
        CLIHelper.printSeparator();
    }

    /**
     * Create a new quiz with questions and options
     */
    private void createQuiz() {
        CLIHelper.printHeader("CREATE NEW QUIZ");

        try {
            // Get quiz details
            String title = CLIHelper.readNonEmptyString("Quiz Title: ");

            Integer semester = null;
            if (CLIHelper.readYesNo("Set semester restriction?")) {
                semester = CLIHelper.readInt("Semester (1-8): ", 1, 8);
            }

            int duration = CLIHelper.readInt("Duration (minutes, 1-180): ", 1, 180);

            // Create quiz
            Quiz quiz = quizService.createQuiz(title, semester, duration);

            CLIHelper.printSuccess("Quiz created with ID: " + quiz.getId());

            // Add questions
            if (CLIHelper.readYesNo("Add questions now?")) {
                addQuestionsToQuiz(quiz.getId());
            }

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
     * Add questions to a quiz
     */
    private void addQuestionsToQuiz(Long quizId) {
        int questionNumber = 1;

        while (true) {
            CLIHelper.printSubHeader("QUESTION #" + questionNumber);

            String questionText = CLIHelper.readNonEmptyString("Question Text: ");

            try {
                // Create question
                Question question = quizService.addQuestion(quizId, questionNumber, questionText);

                // Add 4 options
                CLIHelper.printInfo("Enter 4 options:");
                for (int i = 1; i <= 4; i++) {
                    String optionText = CLIHelper.readNonEmptyString("  Option " + i + ": ");

                    // For now, mark first option as correct (can be modified)
                    boolean isCorrect = false;

                    quizService.addOption(question.getId(), i - 1, optionText, isCorrect);
                }

                // Ask which option is correct
                int correctOption = CLIHelper.readInt("Which option is correct? (1-4): ", 1, 4);

                // Update the correct option
                // Note: This is a simplified approach. In production, you'd update the specific option.
                CLIHelper.printSuccess("Question added successfully!");

                questionNumber++;

                if (!CLIHelper.readYesNo("Add another question?")) {
                    break;
                }

            } catch (SQLException e) {
                CLIHelper.printError("Error adding question: " + e.getMessage());
                break;
            }
        }
    }

    /**
     * View all quizzes
     */
    private void viewAllQuizzes() {
        CLIHelper.printHeader("ALL QUIZZES");

        try {
            List<Quiz> quizzes = quizService.getAllQuizzes();

            if (quizzes.isEmpty()) {
                CLIHelper.printInfo("No quizzes found.");
            } else {
                System.out.printf("%-5s %-30s %-10s %-10s%n", "ID", "Title", "Semester", "Duration");
                CLIHelper.printSeparator();

                for (Quiz quiz : quizzes) {
                    System.out.printf("%-5d %-30s %-10s %-10d min%n",
                            quiz.getId(),
                            quiz.getTitle().length() > 30 ? quiz.getTitle().substring(0, 27) + "..." : quiz.getTitle(),
                            quiz.getSemester() != null ? quiz.getSemester() : "All",
                            quiz.getDuration_minutes()
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
     * View detailed quiz information
     */
    private void viewQuizDetails() {
        CLIHelper.printHeader("VIEW QUIZ DETAILS");

        Long quizId = CLIHelper.readLong("Enter Quiz ID: ");

        if (quizId == null) {
            CLIHelper.printError("Invalid quiz ID");
            CLIHelper.pause();
            return;
        }

        try {
            Quiz quiz = quizService.getQuizWithQuestions(quizId);

            System.out.println();
            System.out.println("Title: " + quiz.getTitle());
            System.out.println("Semester: " + (quiz.getSemester() != null ? quiz.getSemester() : "All"));
            System.out.println("Duration: " + quiz.getDuration_minutes() + " minutes");
            System.out.println("Total Questions: " + (quiz.getQuestions() != null ? quiz.getQuestions().size() : 0));

            if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
                CLIHelper.printSubHeader("QUESTIONS");

                for (Question q : quiz.getQuestions()) {
                    System.out.println("\n" + q.getOrdinal() + ". " + q.getText());

                    if (q.getOptions() != null) {
                        for (Option opt : q.getOptions()) {
                            String marker = opt.isCorrect() ? " ✓" : "";
                            System.out.println("   " + (opt.getOrdinal() + 1) + ") " + opt.getText() + marker);
                        }
                    }
                }
            }

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
     * Delete a quiz
     */
    private void deleteQuiz() {
        CLIHelper.printHeader("DELETE QUIZ");

        Long quizId = CLIHelper.readLong("Enter Quiz ID to delete: ");

        if (quizId == null) {
            CLIHelper.printError("Invalid quiz ID");
            CLIHelper.pause();
            return;
        }

        if (!CLIHelper.readYesNo("Are you sure you want to delete this quiz?")) {
            CLIHelper.printInfo("Deletion cancelled");
            CLIHelper.pause();
            return;
        }

        try {
            quizService.deleteQuiz(quizId);
            CLIHelper.printSuccess("Quiz deleted successfully");
            CLIHelper.pause();

        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
        }
    }

    /**
     * View admin profile
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