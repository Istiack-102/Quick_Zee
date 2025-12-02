package com.quickzee.common.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.QuizResult;
import com.quickzee.common.service.QuizService;
import com.quickzee.common.service.AttemptService;
import com.quickzee.common.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

/**
 * StudentDashboard - Student main screen
 */
public class StudentDashboard {

    private final Scene scene;
    private final QuizService quizService;
    private final AttemptService attemptService;

    public StudentDashboard() {
        this.quizService = new QuizService();
        this.attemptService = new AttemptService();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Main content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Welcome message
        Label welcomeLabel = UIHelper.createHeaderLabel(
                "Welcome, " + SessionManager.getLoggedInUserName() + "!"
        );

        // Available quizzes section
        VBox quizzesSection = createQuizzesSection();

        // History section
        VBox historySection = createHistorySection();

        content.getChildren().addAll(welcomeLabel, quizzesSection, historySection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.setCenter(scrollPane);

        this.scene = new Scene(root, 900, 700);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: " + UIHelper.PRIMARY_COLOR + ";");
        topBar.setAlignment(Pos.CENTER_RIGHT);

        Label titleLabel = new Label("Quick_Zee - Student Dashboard");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: " + UIHelper.PRIMARY_COLOR + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 15;" +
                        "-fx-cursor: hand;"
        );
        logoutButton.setOnAction(e -> {
            SessionManager.logout();
            QuickZeeApp.showLoginView();
        });

        topBar.getChildren().addAll(titleLabel, spacer, logoutButton);

        return topBar;
    }

    private VBox createQuizzesSection() {
        VBox section = UIHelper.createCard("Available Quizzes");
        section.setPrefHeight(300);

        Integer semester = SessionManager.getLoggedInUserSemester();

        if (semester == null) {
            Label noSemesterLabel = UIHelper.createLabel("Please set your semester in profile");
            section.getChildren().add(noSemesterLabel);
            return section;
        }

        try {
            List<Quiz> quizzes = quizService.getQuizzesBySemester(semester);

            if (quizzes.isEmpty()) {
                Label noQuizzesLabel = UIHelper.createLabel("No quizzes available for your semester");
                section.getChildren().add(noQuizzesLabel);
            } else {
                TableView<Quiz> table = new TableView<>();
                table.setPrefHeight(200);

                TableColumn<Quiz, Long> idCol = new TableColumn<>("ID");
                idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                idCol.setPrefWidth(50);

                TableColumn<Quiz, String> titleCol = new TableColumn<>("Title");
                titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
                titleCol.setPrefWidth(300);

                TableColumn<Quiz, Integer> durationCol = new TableColumn<>("Duration (min)");
                durationCol.setCellValueFactory(new PropertyValueFactory<>("duration_minutes"));
                durationCol.setPrefWidth(120);

                TableColumn<Quiz, Void> actionCol = new TableColumn<>("Action");
                actionCol.setPrefWidth(100);
                actionCol.setCellFactory(param -> new TableCell<>() {
                    private final Button takeButton = UIHelper.createSuccessButton("Take Quiz");

                    {
                        takeButton.setOnAction(e -> {
                            Quiz quiz = getTableView().getItems().get(getIndex());
                            if (UIHelper.showConfirmation("Start Quiz",
                                    "Are you ready to start the quiz?\n\nTitle: " + quiz.getTitle() +
                                            "\nDuration: " + quiz.getDuration_minutes() + " minutes")) {
                                QuickZeeApp.showQuizTakingView(quiz.getId());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : takeButton);
                    }
                });

                table.getColumns().addAll(idCol, titleCol, durationCol, actionCol);
                table.getItems().addAll(quizzes);

                section.getChildren().add(table);
            }

        } catch (SQLException e) {
            Label errorLabel = UIHelper.createLabel("Error loading quizzes: " + e.getMessage());
            section.getChildren().add(errorLabel);
        }

        return section;
    }

    private VBox createHistorySection() {
        VBox section = UIHelper.createCard("My Quiz History");
        section.setPrefHeight(250);

        try {
            List<QuizResult> results = attemptService.getMyAttempts();

            if (results.isEmpty()) {
                Label noResultsLabel = UIHelper.createLabel("No quiz attempts yet");
                section.getChildren().add(noResultsLabel);
            } else {
                TableView<QuizResult> table = new TableView<>();
                table.setPrefHeight(150);

                TableColumn<QuizResult, Long> quizIdCol = new TableColumn<>("Quiz ID");
                quizIdCol.setCellValueFactory(new PropertyValueFactory<>("quizId"));
                quizIdCol.setPrefWidth(80);

                TableColumn<QuizResult, Integer> scoreCol = new TableColumn<>("Score");
                scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
                scoreCol.setPrefWidth(80);

                TableColumn<QuizResult, Integer> totalCol = new TableColumn<>("Total");
                totalCol.setCellValueFactory(new PropertyValueFactory<>("totalQuestions"));
                totalCol.setPrefWidth(80);

                TableColumn<QuizResult, String> percentageCol = new TableColumn<>("Percentage");
                percentageCol.setCellFactory(param -> new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            QuizResult result = getTableView().getItems().get(getIndex());
                            double percentage = attemptService.calculatePercentage(result);
                            setText(String.format("%.2f%%", percentage));
                        }
                    }
                });
                percentageCol.setPrefWidth(100);

                TableColumn<QuizResult, String> gradeCol = new TableColumn<>("Grade");
                gradeCol.setCellFactory(param -> new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            QuizResult result = getTableView().getItems().get(getIndex());
                            double percentage = attemptService.calculatePercentage(result);
                            String grade = attemptService.getGrade(percentage);
                            setText(grade);
                        }
                    }
                });
                gradeCol.setPrefWidth(80);

                table.getColumns().addAll(quizIdCol, scoreCol, totalCol, percentageCol, gradeCol);
                table.getItems().addAll(results);

                section.getChildren().add(table);

                // Statistics
                double avgScore = attemptService.getMyAverageScore();
                double bestScore = attemptService.getMyBestScore();

                HBox statsBox = new HBox(30);
                statsBox.setAlignment(Pos.CENTER);
                statsBox.setPadding(new Insets(10, 0, 0, 0));

                Label avgLabel = UIHelper.createLabel(String.format("Average: %.2f%%", avgScore));
                Label bestLabel = UIHelper.createLabel(String.format("Best: %.2f%%", bestScore));

                statsBox.getChildren().addAll(avgLabel, bestLabel);
                section.getChildren().add(statsBox);
            }

        } catch (SQLException e) {
            Label errorLabel = UIHelper.createLabel("Error loading history: " + e.getMessage());
            section.getChildren().add(errorLabel);
        }

        return section;
    }

    public Scene getScene() {
        return scene;
    }
}