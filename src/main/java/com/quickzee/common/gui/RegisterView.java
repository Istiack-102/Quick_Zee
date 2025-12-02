package com.quickzee.common.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.quickzee.common.service.AuthService;
import com.quickzee.common.util.InputValidator;

import java.sql.SQLException;

/**
 * RegisterView - User registration screen
 */
public class RegisterView {

    private final Scene scene;
    private final AuthService authService;

    public RegisterView() {
        this.authService = new AuthService();

        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Register card
        VBox registerCard = UIHelper.createCard(null);
        registerCard.setMaxWidth(400);
        registerCard.setMinWidth(400);

        // Header
        Label headerLabel = UIHelper.createHeaderLabel("Register");
        Label subLabel = UIHelper.createLabel("Create your account");

        // Role selection
        Label roleLabel = UIHelper.createLabel("Register as:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Student", "Admin");
        roleComboBox.setValue("Student");
        roleComboBox.setPrefWidth(350);

        // Name field
        TextField nameField = UIHelper.createTextField("Full Name");
        nameField.setPrefWidth(350);

        // Email field
        TextField emailField = UIHelper.createTextField("Email");
        emailField.setPrefWidth(350);

        // Password field
        PasswordField passwordField = UIHelper.createPasswordField("Password (min 6 characters)");
        passwordField.setPrefWidth(350);

        // Semester field (only for students)
        Label semesterLabel = UIHelper.createLabel("Semester (1-8):");
        ComboBox<Integer> semesterComboBox = new ComboBox<>();
        for (int i = 1; i <= 8; i++) {
            semesterComboBox.getItems().add(i);
        }
        semesterComboBox.setValue(1);
        semesterComboBox.setPrefWidth(350);

        // Show/hide semester based on role
        roleComboBox.setOnAction(e -> {
            boolean isStudent = roleComboBox.getValue().equals("Student");
            semesterLabel.setVisible(isStudent);
            semesterLabel.setManaged(isStudent);
            semesterComboBox.setVisible(isStudent);
            semesterComboBox.setManaged(isStudent);
        });

        // Register button
        Button registerButton = UIHelper.createSuccessButton("Register");
        registerButton.setPrefWidth(350);

        // Back to login link
        Hyperlink loginLink = new Hyperlink("Already have an account? Login here");

        // Register action
        registerButton.setOnAction(e -> handleRegister(
                nameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                roleComboBox.getValue().equals("Student") ? semesterComboBox.getValue() : null,
                roleComboBox.getValue().toLowerCase()
        ));

        // Login action
        loginLink.setOnAction(e -> QuickZeeApp.showLoginView());

        registerCard.getChildren().addAll(
                headerLabel,
                subLabel,
                new Label(" "),
                roleLabel,
                roleComboBox,
                UIHelper.createLabel("Full Name"),
                nameField,
                UIHelper.createLabel("Email"),
                emailField,
                UIHelper.createLabel("Password"),
                passwordField,
                semesterLabel,
                semesterComboBox,
                new Label(" "),
                registerButton,
                loginLink
        );

        root.getChildren().add(registerCard);

        this.scene = new Scene(root, 800, 600);
    }

    private void handleRegister(String name, String email, String password, Integer semester, String role) {
        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIHelper.showError("Validation Error", "Please fill in all required fields");
            return;
        }

        if (!InputValidator.isValidEmail(email)) {
            UIHelper.showError("Validation Error", "Invalid email format");
            return;
        }

        if (!InputValidator.isValidPassword(password)) {
            UIHelper.showError("Validation Error", "Password must be at least 6 characters");
            return;
        }

        try {
            authService.register(name, email, password, semester, role);

            UIHelper.showSuccess("Registration Successful",
                    "Your account has been created! You can now login.");

            QuickZeeApp.showLoginView();

        } catch (IllegalArgumentException e) {
            UIHelper.showError("Validation Error", e.getMessage());
        } catch (SQLException e) {
            UIHelper.showError("Database Error", e.getMessage());
        }
    }

    public Scene getScene() {
        return scene;
    }
}