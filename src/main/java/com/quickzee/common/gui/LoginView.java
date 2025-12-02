package com.quickzee.common.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.quickzee.common.model.User;
import com.quickzee.common.service.AuthService;
import com.quickzee.common.util.SessionManager;

import java.sql.SQLException;

/**
 * LoginView - User login screen
 */
public class LoginView {

    private final Scene scene;
    private final AuthService authService;

    public LoginView() {
        this.authService = new AuthService();

        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Login card
        VBox loginCard = UIHelper.createCard(null);
        loginCard.setMaxWidth(400);
        loginCard.setMinWidth(400);

        // Header
        Label headerLabel = UIHelper.createHeaderLabel("Quick_Zee");
        Label subLabel = UIHelper.createLabel("Quiz Management System");

        // Email field
        TextField emailField = UIHelper.createTextField("Email");
        emailField.setPrefWidth(350);

        // Password field
        PasswordField passwordField = UIHelper.createPasswordField("Password");
        passwordField.setPrefWidth(350);

        // Login button
        Button loginButton = UIHelper.createPrimaryButton("Login");
        loginButton.setPrefWidth(350);

        // Register link
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");

        // Login action
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));
        passwordField.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));

        // Register action
        registerLink.setOnAction(e -> QuickZeeApp.showRegisterView());

        loginCard.getChildren().addAll(
                headerLabel,
                subLabel,
                new Label(" "),
                UIHelper.createLabel("Email"),
                emailField,
                UIHelper.createLabel("Password"),
                passwordField,
                new Label(" "),
                loginButton,
                registerLink
        );

        root.getChildren().add(loginCard);

        this.scene = new Scene(root, 800, 600);
    }

    private void handleLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            UIHelper.showError("Login Error", "Please enter both email and password");
            return;
        }

        try {
            User user = authService.login(email, password);

            if (user != null) {
                if (SessionManager.isAdmin()) {
                    QuickZeeApp.showAdminDashboard();
                } else {
                    QuickZeeApp.showStudentDashboard();
                }
            } else {
                UIHelper.showError("Login Failed", "Invalid email or password");
            }

        } catch (SQLException e) {
            UIHelper.showError("Database Error", e.getMessage());
        }
    }

    public Scene getScene() {
        return scene;
    }
}