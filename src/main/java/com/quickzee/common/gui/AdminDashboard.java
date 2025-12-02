package com.quickzee.common.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.quickzee.common.util.SessionManager;

/**
 * AdminDashboard - Admin main screen (simplified)
 */
public class AdminDashboard {

    private final Scene scene;

    public AdminDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: " + UIHelper.PRIMARY_COLOR + ";");
        topBar.setAlignment(Pos.CENTER_RIGHT);

        Label titleLabel = new Label("Quick_Zee - Admin Dashboard");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: " + UIHelper.PRIMARY_COLOR + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 15;"
        );
        logoutButton.setOnAction(e -> {
            SessionManager.logout();
            QuickZeeApp.showLoginView();
        });

        topBar.getChildren().addAll(titleLabel, spacer, logoutButton);
        root.setTop(topBar);

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);

        Label welcomeLabel = UIHelper.createHeaderLabel("Welcome, Admin!");
        Label infoLabel = UIHelper.createLabel("Use the CLI for quiz management features");

        content.getChildren().addAll(welcomeLabel, infoLabel);
        root.setCenter(content);

        this.scene = new Scene(root, 900, 700);
    }

    public Scene getScene() {
        return scene;
    }
}