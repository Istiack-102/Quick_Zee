package com.quickzee.common.gui;

import com.quickzee.common.dao.OptionDao;
import com.quickzee.common.dao.QuestionDao;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.quickzee.common.model.Quiz;
import com.quickzee.common.model.Question;
import com.quickzee.common.model.Option;
import com.quickzee.common.service.AuthService;
import com.quickzee.common.service.QuizService;
import com.quickzee.common.util.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * AdminDashboard - Complete admin interface with all features
 * - Create quizzes with questions and options
 * - View all quizzes
 * - View quiz details
 * - Delete quizzes
 * - Profile management
 */
public class AdminDashboard {

    private final Scene scene;
    private final AuthService authService;
    private final QuizService quizService;
    private BorderPane mainLayout;

    public AdminDashboard() {
        this.authService = new AuthService();
        this.quizService = new QuizService();

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Top bar
        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);

        // Sidebar menu
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        // Show dashboard by default
        showDashboardView();

        this.scene = new Scene(mainLayout, 1200, 800);
    }



    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: " + UIHelper.PRIMARY_COLOR + ";");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Quick_Zee - Admin Dashboard");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Admin: " + SessionManager.getLoggedInUserName());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: " + UIHelper.PRIMARY_COLOR + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        );
        logoutButton.setOnAction(e -> {
            SessionManager.logout();
            QuickZeeApp.showLoginView();
        });

        topBar.getChildren().addAll(titleLabel, spacer, userLabel, new Label("  "), logoutButton);

        return topBar;
    }



    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPadding(new Insets(20, 0, 20, 0));

        Button dashboardBtn = createMenuButton("📊 Dashboard");
        Button createQuizBtn = createMenuButton("➕ Create Quiz");
        Button viewQuizzesBtn = createMenuButton("📝 View All Quizzes");
        Button profileBtn = createMenuButton("👤 My Profile");

        dashboardBtn.setOnAction(e -> showDashboardView());
        createQuizBtn.setOnAction(e -> showCreateQuizView());
        viewQuizzesBtn.setOnAction(e -> showViewQuizzesView());
        profileBtn.setOnAction(e -> showProfileView());

        sidebar.getChildren().addAll(
                dashboardBtn,
                createQuizBtn,
                viewQuizzesBtn,
                new Separator(),
                profileBtn
        );

        return sidebar;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(15, 20, 15, 20));
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #34495e;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        ));

        return button;
    }



    private void showDashboardView() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));

        Label welcomeLabel = UIHelper.createHeaderLabel("Welcome, Admin!");

        // Statistics cards
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        try {
            List<Quiz> allQuizzes = quizService.getAllQuizzes();

            VBox totalQuizzesCard = createStatCard("Total Quizzes", String.valueOf(allQuizzes.size()), "#3498db");
            VBox activeQuizzesCard = createStatCard("Active Quizzes", String.valueOf(allQuizzes.size()), "#2ecc71");
            VBox totalQuestionsCard = createStatCard("Total Questions", calculateTotalQuestions(allQuizzes), "#e74c3c");

            statsBox.getChildren().addAll(totalQuizzesCard, activeQuizzesCard, totalQuestionsCard);

        } catch (SQLException e) {
            Label errorLabel = UIHelper.createLabel("Error loading statistics: " + e.getMessage());
            statsBox.getChildren().add(errorLabel);
        }

        // Quick actions
        VBox quickActionsCard = UIHelper.createCard("Quick Actions");
        quickActionsCard.setMaxWidth(600);

        Button createQuizBtn = UIHelper.createPrimaryButton("Create New Quiz");
        createQuizBtn.setPrefWidth(300);
        createQuizBtn.setOnAction(e -> showCreateQuizView());

        Button viewQuizzesBtn = UIHelper.createSuccessButton("View All Quizzes");
        viewQuizzesBtn.setPrefWidth(300);
        viewQuizzesBtn.setOnAction(e -> showViewQuizzesView());

        quickActionsCard.getChildren().addAll(createQuizBtn, viewQuizzesBtn);

        content.getChildren().addAll(welcomeLabel, statsBox, quickActionsCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        mainLayout.setCenter(scrollPane);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPrefWidth(200);
        card.setPrefHeight(150);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        card.getChildren().addAll(valueLabel, titleLabel);

        return card;
    }

    private String calculateTotalQuestions(List<Quiz> quizzes) {
        int total = 0;
        for (Quiz quiz : quizzes) {
            try {
                total += quizService.getQuestionCount(quiz.getId());
            } catch (SQLException e) {
                // Skip this quiz
            }
        }
        return String.valueOf(total);
    }



    private void showCreateQuizView() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_CENTER);

        Label headerLabel = UIHelper.createHeaderLabel("Create New Quiz");

        // Quiz details form
        VBox formCard = UIHelper.createCard("Quiz Details");
        formCard.setMaxWidth(700);

        // Title
        Label titleLabel = UIHelper.createLabel("Quiz Title:");
        TextField titleField = UIHelper.createTextField("Enter quiz title");
        titleField.setPrefWidth(650);

        // Semester
        Label semesterLabel = UIHelper.createLabel("Semester (optional):");
        HBox semesterBox = new HBox(10);
        CheckBox semesterCheckBox = new CheckBox("Restrict to semester");
        ComboBox<Integer> semesterComboBox = new ComboBox<>();
        for (int i = 1; i <= 8; i++) {
            semesterComboBox.getItems().add(i);
        }
        semesterComboBox.setValue(1);
        semesterComboBox.setDisable(true);

        semesterCheckBox.setOnAction(e -> semesterComboBox.setDisable(!semesterCheckBox.isSelected()));
        semesterBox.getChildren().addAll(semesterCheckBox, semesterComboBox);

        // Duration
        Label durationLabel = UIHelper.createLabel("Duration (minutes):");
        Spinner<Integer> durationSpinner = new Spinner<>(1, 180, 30);
        durationSpinner.setEditable(true);
        durationSpinner.setPrefWidth(150);

        formCard.getChildren().addAll(
                titleLabel, titleField,
                new Label(" "),
                semesterLabel, semesterBox,
                new Label(" "),
                durationLabel, durationSpinner
        );

        // Create quiz button
        Button createButton = UIHelper.createSuccessButton("Create Quiz & Add Questions");
        createButton.setPrefWidth(300);
        createButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            Integer semester = semesterCheckBox.isSelected() ? semesterComboBox.getValue() : 0;
            int duration = durationSpinner.getValue();

            if (title.isEmpty()) {
                UIHelper.showError("Validation Error", "Please enter a quiz title");
                return;
            }

            try {
                Quiz quiz = quizService.createQuiz(title, semester, duration);
                UIHelper.showSuccess("Quiz Created", "Quiz created with ID: " + quiz.getId());

                // Show question creation dialog
                showAddQuestionsDialog(quiz.getId());

            } catch (IllegalArgumentException ex) {
                UIHelper.showError("Validation Error", ex.getMessage());
            } catch (SQLException ex) {
                UIHelper.showError("Database Error", ex.getMessage());
            }
        });

        content.getChildren().addAll(headerLabel, formCard, createButton);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        mainLayout.setCenter(scrollPane);
    }



    private void showAddQuestionsDialog(Long quizId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Questions to Quiz");
        dialog.setHeaderText("Add questions and options to your quiz");

        // Create content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(700);

        // Questions list
        VBox questionsBox = new VBox(10);
        ScrollPane questionsScroll = new ScrollPane(questionsBox);
        questionsScroll.setPrefHeight(400);
        questionsScroll.setFitToWidth(true);

        // Store questions data
        List<QuestionData> questionsData = new ArrayList<>();

        // Add question button
        Button addQuestionBtn = UIHelper.createPrimaryButton("+ Add Question");
        addQuestionBtn.setOnAction(e -> {
            QuestionData qData = new QuestionData(questionsData.size() + 1);
            questionsData.add(qData);
            questionsBox.getChildren().add(createQuestionForm(qData, questionsData, questionsBox));
        });

        // Add first question automatically
        QuestionData firstQuestion = new QuestionData(1);
        questionsData.add(firstQuestion);
        questionsBox.getChildren().add(createQuestionForm(firstQuestion, questionsData, questionsBox));

        content.getChildren().addAll(
                new Label("Add your questions below. Each question must have 4 options with one correct answer."),
                questionsScroll,
                addQuestionBtn
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle save
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    saveQuestions(quizId, questionsData);
                    UIHelper.showSuccess("Questions Added",
                            questionsData.size() + " questions added successfully!");
                    showViewQuizzesView();
                } catch (Exception ex) {
                    UIHelper.showError("Error", "Failed to save questions: " + ex.getMessage());
                }
            }
            return buttonType;
        });

        dialog.showAndWait();
    }

    // Question form component
    private VBox createQuestionForm(QuestionData qData, List<QuestionData> allQuestions, VBox container) {
        VBox questionCard = UIHelper.createCard("Question " + qData.number);
        questionCard.setStyle(questionCard.getStyle() + "-fx-border-color: " + UIHelper.PRIMARY_COLOR + "; -fx-border-width: 2;");

        // Question text
        Label qLabel = UIHelper.createLabel("Question Text:");
        TextArea questionText = new TextArea();
        questionText.setPromptText("Enter your question here...");
        questionText.setPrefRowCount(3);
        questionText.setWrapText(true);
        qData.questionField = questionText;

        // Options
        Label optionsLabel = UIHelper.createLabel("Options (select the correct answer):");
        VBox optionsBox = new VBox(10);

        ToggleGroup correctGroup = new ToggleGroup();

        for (int i = 0; i < 4; i++) {
            HBox optionBox = new HBox(10);
            optionBox.setAlignment(Pos.CENTER_LEFT);

            RadioButton correctRadio = new RadioButton();
            correctRadio.setToggleGroup(correctGroup);
            qData.correctRadios[i] = correctRadio;

            TextField optionField = UIHelper.createTextField("Option " + (i + 1));
            optionField.setPrefWidth(550);
            qData.optionFields[i] = optionField;

            optionBox.getChildren().addAll(
                    new Label((i + 1) + "."),
                    optionField,
                    new Label("Correct?"),
                    correctRadio
            );

            optionsBox.getChildren().add(optionBox);
        }

        // Delete button
        Button deleteBtn = UIHelper.createDangerButton("Delete Question");
        deleteBtn.setOnAction(e -> {
            allQuestions.remove(qData);
            container.getChildren().remove(questionCard);

            // Renumber questions
            for (int i = 0; i < allQuestions.size(); i++) {
                allQuestions.get(i).number = i + 1;
            }
        });

        questionCard.getChildren().addAll(
                qLabel, questionText,
                new Label(" "),
                optionsLabel, optionsBox,
                new Label(" "),
                deleteBtn
        );

        return questionCard;
    }

    // Helper class to store question data
    private static class QuestionData {
        int number;
        TextArea questionField;
        TextField[] optionFields = new TextField[4];
        RadioButton[] correctRadios = new RadioButton[4];

        QuestionData(int number) {
            this.number = number;
        }
    }

    private void saveQuestions(Long quizId, List<QuestionData> questionsData) throws SQLException {
        for (QuestionData qData : questionsData) {
            String questionText = qData.questionField.getText().trim();

            if (questionText.isEmpty()) {
                throw new IllegalArgumentException("Question " + qData.number + " text cannot be empty");
            }

            // Find correct option
            int correctIndex = -1;
            for (int i = 0; i < 4; i++) {
                if (qData.correctRadios[i].isSelected()) {
                    correctIndex = i;
                    break;
                }
            }

            if (correctIndex == -1) {
                throw new IllegalArgumentException("Question " + qData.number + " must have a correct answer selected");
            }

            // Check all options are filled
            for (int i = 0; i < 4; i++) {
                if (qData.optionFields[i].getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Question " + qData.number + " must have all 4 options filled");
                }
            }

            // Save question
            Question question = quizService.addQuestion(quizId, qData.number, questionText);

            // Save options
            for (int i = 0; i < 4; i++) {
                String optionText = qData.optionFields[i].getText().trim();
                boolean isCorrect = (i == correctIndex);
                quizService.addOption(question.getId(), i, optionText, isCorrect);
            }
        }
    }



    private void showViewQuizzesView() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));

        Label headerLabel = UIHelper.createHeaderLabel("All Quizzes");

        // Quizzes table
        VBox tableCard = UIHelper.createCard("Quiz List");
        tableCard.setMaxWidth(1000);

        try {
            List<Quiz> quizzes = quizService.getAllQuizzes();

            if (quizzes.isEmpty()) {
                Label noQuizzesLabel = UIHelper.createLabel("No quizzes found. Create one to get started!");
                tableCard.getChildren().add(noQuizzesLabel);
            } else {
                TableView<Quiz> table = new TableView<>();
                table.setPrefHeight(400);

                TableColumn<Quiz, Long> idCol = new TableColumn<>("ID");
                idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                idCol.setPrefWidth(60);

                TableColumn<Quiz, String> titleCol = new TableColumn<>("Title");
                titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
                titleCol.setPrefWidth(350);

                TableColumn<Quiz, Integer> semesterCol = new TableColumn<>("Semester");
                semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
                semesterCol.setPrefWidth(100);
                semesterCol.setCellFactory(column -> new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else if (item == 0) {
                            setText("All");
                        } else {
                            setText(item.toString());
                        }
                    }
                });

                TableColumn<Quiz, Integer> durationCol = new TableColumn<>("Duration (min)");
                durationCol.setCellValueFactory(new PropertyValueFactory<>("duration_minutes"));
                durationCol.setPrefWidth(120);

                TableColumn<Quiz, Void> questionsCol = new TableColumn<>("Questions");
                questionsCol.setPrefWidth(100);
                questionsCol.setCellFactory(param -> new TableCell<>() {
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            Quiz quiz = getTableView().getItems().get(getIndex());
                            try {
                                int count = quizService.getQuestionCount(quiz.getId());
                                setText(String.valueOf(count));
                            } catch (SQLException e) {
                                setText("Error");
                            }
                        }
                    }
                });

                TableColumn<Quiz, Void> actionsCol = new TableColumn<>("Actions");
                actionsCol.setPrefWidth(300);
                actionsCol.setCellFactory(param -> new TableCell<>() {
                    private final Button viewBtn = new Button("View");
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {
                        viewBtn.setStyle("-fx-background-color: " + UIHelper.PRIMARY_COLOR + "; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");
                        editBtn.setStyle("-fx-background-color: " + UIHelper.WARNING_COLOR + "; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");
                        deleteBtn.setStyle("-fx-background-color: " + UIHelper.ERROR_COLOR + "; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10;");

                        viewBtn.setOnAction(e -> {
                            Quiz quiz = getTableView().getItems().get(getIndex());
                            showQuizDetailsDialog(quiz.getId());
                        });

                        editBtn.setOnAction(e -> {
                            Quiz quiz = getTableView().getItems().get(getIndex());
                            showEditQuizDialog(quiz.getId());
                        });

                        deleteBtn.setOnAction(e -> {
                            Quiz quiz = getTableView().getItems().get(getIndex());
                            if (UIHelper.showConfirmation("Delete Quiz",
                                    "Are you sure you want to delete this quiz?\n\n" +
                                            "Title: " + quiz.getTitle() + "\n\n" +
                                            "This action cannot be undone!")) {
                                try {
                                    quizService.deleteQuiz(quiz.getId());
                                    UIHelper.showSuccess("Quiz Deleted", "Quiz deleted successfully");
                                    showViewQuizzesView(); // Refresh
                                } catch (SQLException ex) {
                                    UIHelper.showError("Error", "Failed to delete quiz: " + ex.getMessage());
                                }
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5, viewBtn, editBtn, deleteBtn);
                            setGraphic(buttons);
                        }
                    }
                });

                table.getColumns().addAll(idCol, titleCol, semesterCol, durationCol, questionsCol, actionsCol);
                table.getItems().addAll(quizzes);

                tableCard.getChildren().add(table);
            }

        } catch (SQLException e) {
            Label errorLabel = UIHelper.createLabel("Error loading quizzes: " + e.getMessage());
            tableCard.getChildren().add(errorLabel);
        }

        content.getChildren().addAll(headerLabel, tableCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        mainLayout.setCenter(scrollPane);
    }



    private void showQuizDetailsDialog(Long quizId) {
        try {
            Quiz quiz = quizService.getQuizWithQuestions(quizId);

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Quiz Details");
            dialog.setHeaderText(quiz.getTitle());

            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setPrefWidth(700);

            // Quiz info
            Label semesterLabel = UIHelper.createLabel("Semester: " +
                    (quiz.getSemester() != null ? (quiz.getSemester() == 0 ? "All" : quiz.getSemester().toString()) : "All"));
            Label durationLabel = UIHelper.createLabel("Duration: " + quiz.getDuration_minutes() + " minutes");
            Label questionsCountLabel = UIHelper.createLabel("Total Questions: " +
                    (quiz.getQuestions() != null ? quiz.getQuestions().size() : 0));

            content.getChildren().addAll(semesterLabel, durationLabel, questionsCountLabel, new Separator());

            // Questions
            if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
                Label questionsHeader = UIHelper.createSubHeaderLabel("Questions:");
                content.getChildren().add(questionsHeader);

                for (Question q : quiz.getQuestions()) {
                    VBox questionBox = new VBox(5);
                    questionBox.setPadding(new Insets(10));
                    questionBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");

                    Label questionLabel = new Label(q.getOrdinal() + ". " + q.getText());
                    questionLabel.setWrapText(true);
                    questionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    questionBox.getChildren().add(questionLabel);

                    if (q.getOptions() != null) {
                        for (Option opt : q.getOptions()) {
                            String marker = opt.isCorrect() ? " ✅" : "";
                            Label optionLabel = new Label("   " + (opt.getOrdinal() + 1) + ") " + opt.getText() + marker);
                            optionLabel.setWrapText(true);
                            questionBox.getChildren().add(optionLabel);
                        }
                    }

                    content.getChildren().add(questionBox);
                }
            }

            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setPrefHeight(500);
            scrollPane.setFitToWidth(true);

            dialog.getDialogPane().setContent(scrollPane);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            dialog.showAndWait();

        } catch (SQLException e) {
            UIHelper.showError("Error", "Failed to load quiz details: " + e.getMessage());
        }
    }



    private void showProfileView() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_CENTER);

        Label headerLabel = UIHelper.createHeaderLabel("My Profile");

        VBox profileCard = UIHelper.createCard("Profile Information");
        profileCard.setMaxWidth(500);

        Label nameLabel = UIHelper.createLabel("Name: " + SessionManager.getLoggedInUserName());
        Label emailLabel = UIHelper.createLabel("Email: " + SessionManager.getLoggedInUser().getEmail());
        Label roleLabel = UIHelper.createLabel("Role: Admin");

        profileCard.getChildren().addAll(nameLabel, emailLabel, roleLabel);

        content.getChildren().addAll(headerLabel, profileCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        mainLayout.setCenter(scrollPane);
    }



    private void showEditQuizDialog(Long quizId) {
        try {
            Quiz existingQuiz = quizService.getQuizWithQuestions(quizId);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Quiz");
            dialog.setHeaderText("Edit Quiz: " + existingQuiz.getTitle());

            // Create tabs for basic info and questions
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            // ===== TAB 1: BASIC INFO =====
            Tab basicInfoTab = new Tab("Basic Information");
            VBox basicInfoContent = new VBox(15);
            basicInfoContent.setPadding(new Insets(20));

            // Title
            Label titleLabel = UIHelper.createLabel("Quiz Title:");
            TextField titleField = UIHelper.createTextField("Enter quiz title");
            titleField.setText(existingQuiz.getTitle());
            titleField.setPrefWidth(600);

            // Semester
            Label semesterLabel = UIHelper.createLabel("Semester (optional):");
            HBox semesterBox = new HBox(10);
            CheckBox semesterCheckBox = new CheckBox("Restrict to semester");
            ComboBox<Integer> semesterComboBox = new ComboBox<>();
            for (int i = 1; i <= 8; i++) {
                semesterComboBox.getItems().add(i);
            }

            if (existingQuiz.getSemester() != null && existingQuiz.getSemester() != 0) {
                semesterCheckBox.setSelected(true);
                semesterComboBox.setValue(existingQuiz.getSemester());
                semesterComboBox.setDisable(false);
            } else {
                // Semester is 0 or null = "All Semesters"
                semesterCheckBox.setSelected(false);
                semesterComboBox.setValue(1);
                semesterComboBox.setDisable(true);
            }

            semesterCheckBox.setOnAction(e -> semesterComboBox.setDisable(!semesterCheckBox.isSelected()));
            semesterBox.getChildren().addAll(semesterCheckBox, semesterComboBox);

            // Duration
            Label durationLabel = UIHelper.createLabel("Duration (minutes):");
            Spinner<Integer> durationSpinner = new Spinner<>(1, 180, existingQuiz.getDuration_minutes());
            durationSpinner.setEditable(true);
            durationSpinner.setPrefWidth(150);

            basicInfoContent.getChildren().addAll(
                    titleLabel, titleField,
                    new Label(" "),
                    semesterLabel, semesterBox,
                    new Label(" "),
                    durationLabel, durationSpinner
            );

            basicInfoTab.setContent(basicInfoContent);

            // ===== TAB 2: QUESTIONS =====
            Tab questionsTab = new Tab("Questions");
            VBox questionsContent = new VBox(20);
            questionsContent.setPadding(new Insets(20));

            // Questions list
            VBox questionsBox = new VBox(10);
            ScrollPane questionsScroll = new ScrollPane(questionsBox);
            questionsScroll.setPrefHeight(400);
            questionsScroll.setFitToWidth(true);

            // Store questions data
            List<EditQuestionData> questionsData = new ArrayList<>();

            // Load existing questions
            if (existingQuiz.getQuestions() != null) {
                for (Question q : existingQuiz.getQuestions()) {
                    EditQuestionData qData = new EditQuestionData(q.getOrdinal(), q.getId());
                    qData.questionText = q.getText();

                    // Load options
                    if (q.getOptions() != null) {
                        for (int i = 0; i < q.getOptions().size() && i < 4; i++) {
                            Option opt = q.getOptions().get(i);
                            qData.optionTexts[i] = opt.getText();
                            qData.optionIds[i] = opt.getId();
                            if (opt.isCorrect()) {
                                qData.correctIndex = i;
                            }
                        }
                    }

                    questionsData.add(qData);
                    questionsBox.getChildren().add(createEditQuestionForm(qData, questionsData, questionsBox));
                }
            }

            // Add question button
            Button addQuestionBtn = UIHelper.createPrimaryButton("+ Add New Question");
            addQuestionBtn.setOnAction(e -> {
                EditQuestionData qData = new EditQuestionData(questionsData.size() + 1, null);
                questionsData.add(qData);
                questionsBox.getChildren().add(createEditQuestionForm(qData, questionsData, questionsBox));
            });

            questionsContent.getChildren().addAll(
                    new Label("Edit existing questions or add new ones. Changes will be saved when you click Save."),
                    questionsScroll,
                    addQuestionBtn
            );

            questionsTab.setContent(questionsContent);

            // Add tabs
            tabPane.getTabs().addAll(basicInfoTab, questionsTab);

            dialog.getDialogPane().setContent(tabPane);
            dialog.getDialogPane().setPrefSize(800, 600);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Handle save
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        // Update basic info
                        updateQuizBasicInfo(
                                quizId,
                                titleField.getText().trim(),
                                semesterCheckBox.isSelected() ? semesterComboBox.getValue() : 0,
                                durationSpinner.getValue()
                        );

                        // Update questions
                        updateQuizQuestions(quizId, questionsData);

                        UIHelper.showSuccess("Quiz Updated", "Quiz updated successfully!");
                        showViewQuizzesView(); // Refresh

                    } catch (Exception ex) {
                        UIHelper.showError("Error", "Failed to update quiz: " + ex.getMessage());
                    }
                }
                return buttonType;
            });

            dialog.showAndWait();

        } catch (SQLException e) {
            UIHelper.showError("Error", "Failed to load quiz: " + e.getMessage());
        }
    }

    // Helper class for edit mode
    private static class EditQuestionData {
        int number;
        Long questionId; // null if new question
        String questionText = "";
        String[] optionTexts = new String[4];
        Long[] optionIds = new Long[4]; // null if new option
        int correctIndex = 0;

        TextArea questionField;
        TextField[] optionFields = new TextField[4];
        RadioButton[] correctRadios = new RadioButton[4];
        boolean markedForDeletion = false;

        EditQuestionData(int number, Long questionId) {
            this.number = number;
            this.questionId = questionId;
            for (int i = 0; i < 4; i++) {
                optionTexts[i] = "";
            }
        }
    }

    // Create edit question form
    private VBox createEditQuestionForm(EditQuestionData qData, List<EditQuestionData> allQuestions, VBox container) {
        VBox questionCard = UIHelper.createCard("Question " + qData.number + (qData.questionId == null ? " (NEW)" : ""));
        questionCard.setStyle(questionCard.getStyle() +
                "-fx-border-color: " + (qData.questionId == null ? UIHelper.SUCCESS_COLOR : UIHelper.PRIMARY_COLOR) +
                "; -fx-border-width: 2;");

        // Question text
        Label qLabel = UIHelper.createLabel("Question Text:");
        TextArea questionText = new TextArea();
        questionText.setPromptText("Enter your question here...");
        questionText.setPrefRowCount(3);
        questionText.setWrapText(true);
        questionText.setText(qData.questionText);
        qData.questionField = questionText;

        // Options
        Label optionsLabel = UIHelper.createLabel("Options (select the correct answer):");
        VBox optionsBox = new VBox(10);

        ToggleGroup correctGroup = new ToggleGroup();

        for (int i = 0; i < 4; i++) {
            HBox optionBox = new HBox(10);
            optionBox.setAlignment(Pos.CENTER_LEFT);

            RadioButton correctRadio = new RadioButton();
            correctRadio.setToggleGroup(correctGroup);
            if (i == qData.correctIndex) {
                correctRadio.setSelected(true);
            }
            qData.correctRadios[i] = correctRadio;

            TextField optionField = UIHelper.createTextField("Option " + (i + 1));
            optionField.setPrefWidth(500);
            optionField.setText(qData.optionTexts[i]);
            qData.optionFields[i] = optionField;

            Label statusLabel = new Label(qData.optionIds[i] != null ? "" : "(NEW)");
            statusLabel.setStyle("-fx-text-fill: " + UIHelper.SUCCESS_COLOR + "; -fx-font-weight: bold;");

            optionBox.getChildren().addAll(
                    new Label((i + 1) + "."),
                    optionField,
                    new Label("Correct?"),
                    correctRadio,
                    statusLabel
            );

            optionsBox.getChildren().add(optionBox);
        }

        // Delete button
        Button deleteBtn = UIHelper.createDangerButton(qData.questionId == null ? "Remove" : "Delete Question");

        // Store original action
        final boolean[] isMarkedForDeletion = {qData.markedForDeletion};

        deleteBtn.setOnAction(e -> {
            if (qData.questionId != null && !isMarkedForDeletion[0]) {
                // Mark for deletion
                if (UIHelper.showConfirmation("Delete Question",
                        "Are you sure you want to delete this question?\n\n" +
                                "This will permanently remove it from the quiz.")) {
                    qData.markedForDeletion = true;
                    isMarkedForDeletion[0] = true;
                    questionCard.setOpacity(0.5);
                    questionCard.setStyle(questionCard.getStyle().replaceAll("-fx-border-color: [^;]+", "-fx-border-color: " + UIHelper.ERROR_COLOR));
                    deleteBtn.setText("Undo Delete");
                    deleteBtn.setStyle("-fx-background-color: " + UIHelper.WARNING_COLOR + "; -fx-text-fill: white; -fx-cursor: hand;");
                }
            } else if (qData.questionId != null && isMarkedForDeletion[0]) {
                // Undo deletion
                qData.markedForDeletion = false;
                isMarkedForDeletion[0] = false;
                questionCard.setOpacity(1.0);
                questionCard.setStyle(questionCard.getStyle().replaceAll("-fx-border-color: [^;]+", "-fx-border-color: " + UIHelper.PRIMARY_COLOR));
                deleteBtn.setText("Delete Question");
                deleteBtn.setStyle("-fx-background-color: " + UIHelper.ERROR_COLOR + "; -fx-text-fill: white; -fx-cursor: hand;");
            } else {
                // Remove from list (new question)
                allQuestions.remove(qData);
                container.getChildren().remove(questionCard);

                // Renumber
                for (int i = 0; i < allQuestions.size(); i++) {
                    allQuestions.get(i).number = i + 1;
                }
            }
        });

        questionCard.getChildren().addAll(
                qLabel, questionText,
                new Label(" "),
                optionsLabel, optionsBox,
                new Label(" "),
                deleteBtn
        );

        return questionCard;
    }

    // Update quiz basic info (title, semester, duration)
    private void updateQuizBasicInfo(Long quizId, String title, Integer semester, int duration) throws SQLException {
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Quiz title cannot be empty");
        }

        try {
            // Direct SQL update
            String sql = "UPDATE quizzes SET title = ?, semester = ?, duration_minutes = ? WHERE id = ?";
            try (java.sql.Connection conn = com.quickzee.common.util.DBConnection.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, title);
                if (semester != null) {
                    ps.setInt(2, semester);
                } else {
                    ps.setNull(2, java.sql.Types.INTEGER); // ✨ FIXED: was ps.setInt
                }
                ps.setInt(3, duration);
                ps.setLong(4, quizId);

                ps.executeUpdate();
            }

        } catch (Exception e) {
            throw new SQLException("Failed to update quiz: " + e.getMessage());
        }
    }

    // Update quiz questions
    private void updateQuizQuestions(Long quizId, List<EditQuestionData> questionsData) throws SQLException {
        QuestionDao questionDao = new QuestionDao();
        OptionDao optionDao = new OptionDao();

        // Delete marked questions
        for (EditQuestionData qData : questionsData) {
            if (qData.markedForDeletion && qData.questionId != null) {
                questionDao.deleteById(qData.questionId);
            }
        }

        // Update or create questions
        for (EditQuestionData qData : questionsData) {
            if (qData.markedForDeletion) continue;

            String questionText = qData.questionField.getText().trim();

            if (questionText.isEmpty()) {
                throw new IllegalArgumentException("Question " + qData.number + " text cannot be empty");
            }

            // Find correct option
            int correctIndex = -1;
            for (int i = 0; i < 4; i++) {
                if (qData.correctRadios[i].isSelected()) {
                    correctIndex = i;
                    break;
                }
            }

            if (correctIndex == -1) {
                throw new IllegalArgumentException("Question " + qData.number + " must have a correct answer selected");
            }

            // Check all options filled
            for (int i = 0; i < 4; i++) {
                if (qData.optionFields[i].getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Question " + qData.number + " must have all 4 options filled");
                }
            }

            if (qData.questionId == null) {
                // New question
                Question question = quizService.addQuestion(quizId, qData.number, questionText);

                for (int i = 0; i < 4; i++) {
                    String optionText = qData.optionFields[i].getText().trim();
                    boolean isCorrect = (i == correctIndex);
                    quizService.addOption(question.getId(), i, optionText, isCorrect);
                }
            } else {
                // Update existing question
                Question q = new Question();
                q.setId(qData.questionId);
                q.setQuiz_id(quizId);
                q.setOrdinal(qData.number);
                q.setText(questionText);
                questionDao.update(q);

                for (int i = 0; i < 4; i++) {
                    String optionText = qData.optionFields[i].getText().trim();
                    boolean isCorrect = (i == correctIndex);

                    if (qData.optionIds[i] == null) {
                        // New option
                        quizService.addOption(qData.questionId, i, optionText, isCorrect);
                    } else {
                        // Update existing option
                        Option opt = new Option();
                        opt.setId(qData.optionIds[i]);
                        opt.setQuestion_id(qData.questionId);
                        opt.setOrdinal(i);
                        opt.setText(optionText);
                        opt.setCorrect(isCorrect);
                        optionDao.update(opt);
                    }
                }
            }
        }
    }

    public Scene getScene() {
        return scene;
    }
}