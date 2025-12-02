package com.quickzee.common.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import com.quickzee.common.service.AttemptService;

/**
 * ResultView - Display quiz results
 */
public class ResultView {

    private final Scene scene;
    private final AttemptService attemptService;

    public ResultView(Long resultId, int score, int total) {
        this.attemptService = new AttemptService();

        VBox root = new VBox(30);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Result card
        VBox resultCard = UIHelper.createCard(null);
        resultCard.setMaxWidth(500);
        resultCard.setAlignment(Pos.CENTER);

        // Header
        Label headerLabel = new Label("Quiz Completed!");
        headerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + UIHelper.SUCCESS_COLOR + ";");

        // Calculate percentage and grade
        double percentage = (score * 100.0) / total;
        String grade = attemptService.getGrade(percentage);
        boolean passed = percentage >= 50.0;

        // Score display
        Label scoreLabel = new Label(score + " / " + total);
        scoreLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");

        // Percentage
        Label percentageLabel = new Label(String.format("%.2f%%", percentage));
        percentageLabel.setStyle("-fx-font-size: 36px;");

        // Grade
        Label gradeLabel = new Label("Grade: " + grade);
        gradeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        // Status
        Label statusLabel = new Label(passed ? "✅ PASSED" : "❌ FAILED");
        statusLabel.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + (passed ? UIHelper.SUCCESS_COLOR : UIHelper.ERROR_COLOR) + ";"
        );

        // Back button
        Button backButton = UIHelper.createPrimaryButton("Back to Dashboard");
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> QuickZeeApp.showStudentDashboard());

        resultCard.getChildren().addAll(
                headerLabel,
                new Label(" "),
                scoreLabel,
                percentageLabel,
                gradeLabel,
                statusLabel,
                new Label(" "),
                backButton
        );

        root.getChildren().add(resultCard);

        this.scene = new Scene(root, 800, 600);
    }

    public Scene getScene() {
        return scene;
    }
}