package com.quickzee.common.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.Question;
import com.quickzee.common.model.Option;
import com.quickzee.common.model.QuizResult;
import com.quickzee.common.service.AttemptService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizTakingView - Quiz taking interface WITH COUNTDOWN TIMER
 * Features:
 * - Real-time countdown timer
 * - Color-coded warnings (green → orange → red)
 * - Auto-disable options when time expires
 * - Auto-submit quiz
 */
public class QuizTakingView {

    private final Scene scene;
    private final AttemptService attemptService;
    private final Quiz quiz;
    private final List<Long> selectedAnswers;

    private int currentQuestionIndex = 0;
    private int timeRemainingSeconds;

    // UI Components
    private Label timerLabel;
    private HBox timerPanel;
    private Label questionLabel;
    private VBox optionsBox;
    private Button nextButton;
    private Button prevButton;
    private Button submitButton;
    private List<RadioButton> currentRadioButtons;
    private ToggleGroup toggleGroup;

    // Timer
    private Timeline countdown;

    public QuizTakingView(Long quizId) {
        this.attemptService = new AttemptService();
        this.selectedAnswers = new ArrayList<>();

        // Load quiz
        try {
            this.quiz = attemptService.startQuizAttempt(quizId);

            // Initialize answers list (null = unanswered)
            for (int i = 0; i < quiz.getQuestions().size(); i++) {
                selectedAnswers.add(null);
            }

            // Initialize timer (convert minutes to seconds)
            this.timeRemainingSeconds = quiz.getDuration_minutes() * 60;

        } catch (SQLException e) {
            UIHelper.showError("Error", "Failed to load quiz: " + e.getMessage());
            throw new RuntimeException(e);
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Timer panel (TOP)
        timerPanel = createTimerPanel();
        root.setTop(timerPanel);

        // Question panel (CENTER)
        VBox questionPanel = createQuestionPanel();
        root.setCenter(questionPanel);

        // Navigation panel (BOTTOM)
        HBox navPanel = createNavigationPanel();
        root.setBottom(navPanel);

        // Load first question
        loadQuestion(0);

        // Start countdown timer
        startTimer();

        this.scene = new Scene(root, 900, 700);
    }

    // ============ TIMER PANEL ============

    private HBox createTimerPanel() {
        HBox panel = new HBox();
        panel.setPadding(new Insets(15));
        panel.setAlignment(Pos.CENTER);
        panel.setStyle(
                "-fx-background-color: " + UIHelper.TIMER_NORMAL + ";" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );

        Label clockIcon = new Label("⏱");
        clockIcon.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");

        timerLabel = new Label();
        timerLabel.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );
        updateTimerDisplay();

        panel.getChildren().addAll(clockIcon, new Label("  "), timerLabel);

        return panel;
    }

    private void startTimer() {
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemainingSeconds--;
            updateTimerDisplay();

            // Update colors based on time
            updateTimerColors();

            // Check if time's up
            if (timeRemainingSeconds <= 0) {
                handleTimeExpired();
            }
        }));

        countdown.setCycleCount(Animation.INDEFINITE);
        countdown.play();
    }

    private void updateTimerDisplay() {
        int minutes = timeRemainingSeconds / 60;
        int seconds = timeRemainingSeconds % 60;
        timerLabel.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
    }

    private void updateTimerColors() {
        String bgColor;

        if (timeRemainingSeconds <= 60) {
            // Last minute - RED
            bgColor = UIHelper.TIMER_CRITICAL;

            // Flash effect
            if (timeRemainingSeconds <= 30 && timeRemainingSeconds % 2 == 0) {
                timerPanel.setOpacity(0.7);
            } else {
                timerPanel.setOpacity(1.0);
            }

        } else if (timeRemainingSeconds <= 300) {
            // Last 5 minutes - ORANGE
            bgColor = UIHelper.TIMER_WARNING;
            timerPanel.setOpacity(1.0);

        } else {
            // Normal - GREEN
            bgColor = UIHelper.TIMER_NORMAL;
            timerPanel.setOpacity(1.0);
        }

        timerPanel.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );
    }

    private void handleTimeExpired() {
        countdown.stop();

        // Disable all options
        disableAllOptions();

        // Show time's up dialog
        UIHelper.showWarning("Time's Up!",
                "The quiz time has expired. Your quiz will be auto-submitted.");

        // Auto-submit
        autoSubmitQuiz();
    }

    private void disableAllOptions() {
        if (currentRadioButtons != null) {
            for (RadioButton radio : currentRadioButtons) {
                radio.setDisable(true);
                radio.setStyle("-fx-opacity: 0.5;");
            }
        }

        nextButton.setDisable(true);
        prevButton.setDisable(true);
    }

    private void autoSubmitQuiz() {
        try {
            QuizResult result = attemptService.submitQuizAttempt(quiz.getId(), selectedAnswers);

            countdown.stop();

            UIHelper.showSuccess("Quiz Submitted",
                    "Your quiz has been submitted successfully!");

            QuickZeeApp.showResultView(result.getId(), result.getScore(), result.getTotalQuestions());

        } catch (SQLException e) {
            UIHelper.showError("Submission Error", e.getMessage());
        }
    }

    // ============ QUESTION PANEL ============

    private VBox createQuestionPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(30));
        panel.setAlignment(Pos.TOP_CENTER);

        // Question card
        VBox questionCard = UIHelper.createCard(null);
        questionCard.setMaxWidth(700);
        questionCard.setMinHeight(400);

        // Question number and text
        questionLabel = new Label();
        questionLabel.setWrapText(true);
        questionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Options container
        optionsBox = new VBox(15);
        optionsBox.setPadding(new Insets(20, 0, 0, 0));

        questionCard.getChildren().addAll(questionLabel, new Separator(), optionsBox);
        panel.getChildren().add(questionCard);

        return panel;
    }

    private void loadQuestion(int index) {
        currentQuestionIndex = index;
        Question question = quiz.getQuestions().get(index);

        // Update question label
        questionLabel.setText("Question " + (index + 1) + " of " + quiz.getQuestions().size() +
                "\n\n" + question.getText());

        // Clear previous options
        optionsBox.getChildren().clear();
        currentRadioButtons = new ArrayList<>();
        toggleGroup = new ToggleGroup();

        // Create radio buttons for options
        for (Option option : question.getOptions()) {
            RadioButton radio = new RadioButton(option.getText());
            radio.setToggleGroup(toggleGroup);
            radio.setStyle("-fx-font-size: 16px;");
            radio.setUserData(option.getId());

            // Check if this option was previously selected
            if (selectedAnswers.get(index) != null &&
                    selectedAnswers.get(index).equals(option.getId())) {
                radio.setSelected(true);
            }

            // Save selection
            radio.setOnAction(e -> {
                selectedAnswers.set(currentQuestionIndex, (Long) radio.getUserData());
            });

            currentRadioButtons.add(radio);
            optionsBox.getChildren().add(radio);
        }

        // Update navigation buttons
        prevButton.setDisable(index == 0);
        nextButton.setText(index == quiz.getQuestions().size() - 1 ? "Review & Submit" : "Next");
    }

    // ============ NAVIGATION PANEL ============

    private HBox createNavigationPanel() {
        HBox panel = new HBox(15);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");

        prevButton = UIHelper.createButton("← Previous", "#757575");
        prevButton.setPrefWidth(150);
        prevButton.setOnAction(e -> {
            if (currentQuestionIndex > 0) {
                loadQuestion(currentQuestionIndex - 1);
            }
        });

        nextButton = UIHelper.createPrimaryButton("Next →");
        nextButton.setPrefWidth(150);
        nextButton.setOnAction(e -> {
            if (currentQuestionIndex < quiz.getQuestions().size() - 1) {
                loadQuestion(currentQuestionIndex + 1);
            } else {
                showReviewDialog();
            }
        });

        submitButton = UIHelper.createSuccessButton("Submit Quiz");
        submitButton.setPrefWidth(150);
        submitButton.setVisible(false);
        submitButton.setOnAction(e -> handleSubmit());

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        panel.getChildren().addAll(prevButton, spacer1, nextButton, spacer2, submitButton);

        return panel;
    }

    private void showReviewDialog() {
        int answered = 0;
        for (Long answer : selectedAnswers) {
            if (answer != null) answered++;
        }

        int unanswered = quiz.getQuestions().size() - answered;

        String message = "Quiz Review:\n\n" +
                "Total Questions: " + quiz.getQuestions().size() + "\n" +
                "Answered: " + answered + "\n" +
                "Unanswered: " + unanswered + "\n\n" +
                "Are you sure you want to submit?";

        if (UIHelper.showConfirmation("Review Quiz", message)) {
            handleSubmit();
        }
    }

    private void handleSubmit() {
        try {
            QuizResult result = attemptService.submitQuizAttempt(quiz.getId(), selectedAnswers);

            countdown.stop();

            UIHelper.showSuccess("Quiz Submitted", "Your quiz has been submitted successfully!");

            QuickZeeApp.showResultView(result.getId(), result.getScore(), result.getTotalQuestions());

        } catch (SQLException e) {
            UIHelper.showError("Submission Error", e.getMessage());
        }
    }

    public Scene getScene() {
        return scene;
    }
}