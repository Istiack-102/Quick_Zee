package com.quickzee.common.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import com.quickzee.common.util.DBConnection;

/**
 * Main JavaFX Application Entry Point
 */
public class QuickZeeApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Quick_Zee Quiz System");
        primaryStage.setResizable(false);

        // Test database connection
        if (!DBConnection.testConnection()) {
            UIHelper.showError("Database Error",
                    "Failed to connect to database. Please check your configuration.");
            System.exit(1);
        }

        // Show login view
        showLoginView();

        primaryStage.show();
    }

    public static void showLoginView() {
        LoginView loginView = new LoginView();
        primaryStage.setScene(loginView.getScene());
    }

    public static void showRegisterView() {
        RegisterView registerView = new RegisterView();
        primaryStage.setScene(registerView.getScene());
    }

    public static void showAdminDashboard() {
        AdminDashboard dashboard = new AdminDashboard();
        primaryStage.setScene(dashboard.getScene());
    }

    public static void showStudentDashboard() {
        StudentDashboard dashboard = new StudentDashboard();
        primaryStage.setScene(dashboard.getScene());
    }

    public static void showQuizTakingView(Long quizId) {
        QuizTakingView quizView = new QuizTakingView(quizId);
        primaryStage.setScene(quizView.getScene());
    }

    public static void showResultView(Long resultId, int score, int total) {
        ResultView resultView = new ResultView(resultId, score, total);
        primaryStage.setScene(resultView.getScene());
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}