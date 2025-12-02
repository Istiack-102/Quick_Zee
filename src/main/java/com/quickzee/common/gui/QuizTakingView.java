package com.quickzee.common.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.Question;
import com.quickzee.common.model.Option;
import com.quickzee.common.model.QuizResult;
import com.quickzee.common.service.AttemptService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * QuizTakingView - shows ALL questions serially in a scrollable view (one page)
 * - Each question has its own ToggleGroup (independent selections)
 * - Real-time countdown timer
 * - On expiry: everything is disabled/greyed out except the Submit button
 * - Uses Platform.runLater for dialogs to avoid showAndWait during animation/layout pulses
 */
public class QuizTakingView {

    private final Scene scene;
    private final AttemptService attemptService;
    private final Quiz quiz;
    private final List<Long> selectedAnswers; // index -> optionId (null = unanswered)

    private int timeRemainingSeconds;

    // UI Components
    private Label timerLabel;
    private HBox timerPanel;
    private VBox questionsContainer; // holds all question cards
    private Button submitButton;

    // Timer
    private Timeline countdown;

    // Keep per-question toggleGroups and radio lists so we can disable them on expiry
    private final Map<Integer, ToggleGroup> toggleGroups = new HashMap<>();
    private final Map<Integer, List<RadioButton>> radioButtonsPerQuestion = new HashMap<>();

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

        // Questions panel (CENTER) - show all questions serially
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        questionsContainer = new VBox(20);
        questionsContainer.setPadding(new Insets(20));
        questionsContainer.setAlignment(Pos.TOP_CENTER);

        buildAllQuestionCards();

        scrollPane.setContent(questionsContainer);
        root.setCenter(scrollPane);

        // Bottom panel with submit
        HBox bottom = createBottomPanel();
        root.setBottom(bottom);

        // Start countdown timer
        startTimer();

        this.scene = new Scene(root, 900, 700);
    }

    private HBox createTimerPanel() {
        HBox panel = new HBox();
        panel.setPadding(new Insets(15));
        panel.setAlignment(Pos.CENTER);
        panel.setStyle(
                "-fx-background-color: " + UIHelper.TIMER_NORMAL + ";" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );

        Label clockIcon = new Label("⏱");
        clockIcon.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        timerLabel = new Label();
        timerLabel.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );
        updateTimerDisplay();

        panel.getChildren().addAll(clockIcon, new Label("  "), timerLabel);

        return panel;
    }

    private HBox createBottomPanel() {
        HBox panel = new HBox(15);
        panel.setPadding(new Insets(15));
        panel.setAlignment(Pos.CENTER_RIGHT);
        panel.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");

        submitButton = UIHelper.createSuccessButton("Submit Quiz");
        submitButton.setPrefWidth(180);
        submitButton.setOnAction(e -> handleSubmit());

        panel.getChildren().add(submitButton);
        return panel;
    }

    private void buildAllQuestionCards() {
        questionsContainer.getChildren().clear();

        List<Question> questions = quiz.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            VBox card = UIHelper.createCard(null);
            card.setMaxWidth(800);

            Label qLabel = new Label("Question " + (i + 1) + " of " + questions.size() + "\n\n" + q.getText());
            qLabel.setWrapText(true);
            qLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            VBox opts = new VBox(10);
            opts.setPadding(new Insets(10, 0, 0, 0));

            ToggleGroup tg = new ToggleGroup();
            toggleGroups.put(i, tg);

            List<RadioButton> radios = new ArrayList<>();

            for (Option opt : q.getOptions()) {
                RadioButton rb = new RadioButton(opt.getText());
                rb.setToggleGroup(tg);
                rb.setUserData(opt.getId());
                rb.setWrapText(true);
                rb.setStyle("-fx-font-size: 14px;");

                // restore previous selection if any
                if (selectedAnswers.get(i) != null && selectedAnswers.get(i).equals(opt.getId())) {
                    rb.setSelected(true);
                }

                final int questionIndex = i;
                rb.setOnAction(ev -> selectedAnswers.set(questionIndex, (Long) rb.getUserData()));

                radios.add(rb);
                opts.getChildren().add(rb);
            }

            radioButtonsPerQuestion.put(i, radios);

            card.getChildren().addAll(qLabel, new Separator(), opts);
            questionsContainer.getChildren().add(card);
        }

    }

    private void startTimer() {
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemainingSeconds--;
            updateTimerDisplay();
            updateTimerColors();

            if (timeRemainingSeconds <= 0) {
                // stop and perform expiry handling
                countdown.stop();
                handleTimeExpired();
            }
        }));

        countdown.setCycleCount(Animation.INDEFINITE);
        countdown.play();
    }

    private void updateTimerDisplay() {
        int minutes = Math.max(0, timeRemainingSeconds) / 60;
        int seconds = Math.max(0, timeRemainingSeconds) % 60;
        timerLabel.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
    }

    private void updateTimerColors() {
        String bgColor;

        if (timeRemainingSeconds <= 60) {
            bgColor = UIHelper.TIMER_CRITICAL;
            if (timeRemainingSeconds <= 30 && timeRemainingSeconds % 2 == 0) {
                timerPanel.setOpacity(0.7);
            } else {
                timerPanel.setOpacity(1.0);
            }

        } else if (timeRemainingSeconds <= 300) {
            bgColor = UIHelper.TIMER_WARNING;
            timerPanel.setOpacity(1.0);
        } else {
            bgColor = UIHelper.TIMER_NORMAL;
            timerPanel.setOpacity(1.0);
        }

        timerPanel.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );
    }

    private void handleTimeExpired() {
        // Disable all interactive controls except submit
        disableAllExceptSubmit();

        // Show a non-blocking warning after current pulse
        Platform.runLater(() -> UIHelper.showWarning("Time's Up!",
                "The quiz time has expired. All controls are disabled. Please submit your quiz."));

        // Ensure submit is visible and enabled for manual submission
        Platform.runLater(() -> {
            submitButton.setDisable(false);
            submitButton.requestFocus();
        });
    }

    private void disableAllExceptSubmit() {
        // Disable all radio buttons and grey them
        for (Map.Entry<Integer, List<RadioButton>> e : radioButtonsPerQuestion.entrySet()) {
            for (RadioButton rb : e.getValue()) {
                rb.setDisable(true);
                rb.setStyle("-fx-opacity: 0.6;");
            }
        }

        // You may also visually dim question cards
        for (javafx.scene.Node node : questionsContainer.getChildren()) {
            node.setOpacity(0.9);
        }

        // Ensure submit button is visible and enabled (but leave its logic to handleSubmit)
        if (submitButton != null) {
            submitButton.setDisable(false);
        }
    }

    private void handleSubmit() {
        try {
            QuizResult result = attemptService.submitQuizAttempt(quiz.getId(), selectedAnswers);

            if (countdown != null) countdown.stop();

            Platform.runLater(() -> {
                UIHelper.showSuccess("Quiz Submitted", "Your quiz has been submitted successfully!");
                QuickZeeApp.showResultView(result.getId(), result.getScore(), result.getTotalQuestions());
            });

        } catch (SQLException e) {
            Platform.runLater(() -> UIHelper.showError("Submission Error", e.getMessage()));
        }
    }

    public Scene getScene() {
        return scene;
    }
}
