package com.quickzee.common.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * UIHelper - Common UI components and styling
 */
public class UIHelper {

    // Color scheme
    public static final String PRIMARY_COLOR = "#2196F3";
    public static final String SUCCESS_COLOR = "#4CAF50";
    public static final String ERROR_COLOR = "#F44336";
    public static final String WARNING_COLOR = "#FF9800";
    public static final String TIMER_NORMAL = "#4CAF50";
    public static final String TIMER_WARNING = "#FF9800";
    public static final String TIMER_CRITICAL = "#F44336";

    /**
     * Create styled button
     */
    public static Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        );

        button.setOnMouseEntered(e -> button.setOpacity(0.8));
        button.setOnMouseExited(e -> button.setOpacity(1.0));

        return button;
    }

    /**
     * Create primary button
     */
    public static Button createPrimaryButton(String text) {
        return createButton(text, PRIMARY_COLOR);
    }

    /**
     * Create success button
     */
    public static Button createSuccessButton(String text) {
        return createButton(text, SUCCESS_COLOR);
    }

    /**
     * Create danger button
     */
    public static Button createDangerButton(String text) {
        return createButton(text, ERROR_COLOR);
    }

    /**
     * Create styled text field
     */
    public static TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-padding: 10;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );
        return field;
    }

    /**
     * Create styled password field
     */
    public static PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-padding: 10;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );
        return field;
    }

    /**
     * Create header label
     */
    public static Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        label.setTextFill(Color.web(PRIMARY_COLOR));
        return label;
    }

    /**
     * Create sub-header label
     */
    public static Label createSubHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        return label;
    }

    /**
     * Create normal label
     */
    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", 14));
        return label;
    }

    /**
     * Create timer label
     */
    public static Label createTimerLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        label.setTextFill(Color.web(TIMER_NORMAL));
        return label;
    }

    /**
     * Show success alert
     */
    public static void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show error alert
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show warning alert
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show confirmation dialog
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Create VBox with standard spacing
     */
    public static VBox createVBox(double spacing) {
        VBox vbox = new VBox(spacing);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    /**
     * Create HBox with standard spacing
     */
    public static HBox createHBox(double spacing) {
        HBox hbox = new HBox(spacing);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    /**
     * Create card panel
     */
    public static VBox createCard(String title) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #ddd;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        if (title != null && !title.isEmpty()) {
            Label titleLabel = createSubHeaderLabel(title);
            card.getChildren().add(titleLabel);
            card.getChildren().add(new Separator());
        }

        return card;
    }
}