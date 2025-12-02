package com.quickzee.common.cli;

import com.quickzee.common.model.User;
import com.quickzee.common.util.DBConnection;
import com.quickzee.common.util.SessionManager;

/**
 * MainCLI - Entry point for the Quick_Zee application
 * Handles main menu and routing to other CLI modules
 */
public class MainCLI {

    private final AuthCLI authCLI;
    private final AdminCLI adminCLI;
    private final StudentCLI studentCLI;

    public MainCLI() {
        this.authCLI = new AuthCLI();
        this.adminCLI = new AdminCLI();
        this.studentCLI = new StudentCLI();
    }

    /**
     * Main application entry point
     */
    public static void main(String[] args) {
        // Test database connection first
        if (!DBConnection.testConnection()) {
            CLIHelper.printError("Failed to connect to database!");
            CLIHelper.printError("Please check your database configuration in DBConnection.java");
            return;
        }

        CLIHelper.printSuccess("Database connection successful!");
        CLIHelper.pause();

        // Start the application
        MainCLI app = new MainCLI();
        app.start();
    }

    /**
     * Start the application
     */
    public void start() {
        CLIHelper.printWelcomeBanner();

        while (true) {
            showMainMenu();

            int choice = CLIHelper.readInt("Choose an option: ", 0, 4);

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleStudentRegistration();
                    break;
                case 3:
                    handleAdminRegistration();
                    break;
                case 4:
                    showAbout();
                    break;
                case 0:
                    handleExit();
                    return;
            }
        }
    }

    /**
     * Display main menu
     */
    private void showMainMenu() {
        CLIHelper.printHeader("MAIN MENU");
        System.out.println("1. Login");
        System.out.println("2. Register as Student");
        System.out.println("3. Register as Admin");
        System.out.println("4. About Quick_Zee");
        System.out.println("0. Exit");
        CLIHelper.printSeparator();
    }

    /**
     * Handle login
     */
    private void handleLogin() {
        User user = authCLI.showLoginMenu();

        if (user != null) {
            // Route based on role
            if (SessionManager.isAdmin()) {
                adminCLI.showMenu();
            } else if (SessionManager.isStudent()) {
                studentCLI.showMenu();
            }

            // After logout, return to main menu
            CLIHelper.printSuccess("Returned to main menu");
        }
    }

    /**
     * Handle student registration
     */
    private void handleStudentRegistration() {
        boolean success = authCLI.showStudentRegistrationMenu();

        if (success) {
            CLIHelper.printSuccess("Registration successful! You can now login.");
            CLIHelper.pause();
        }
    }

    /**
     * Handle admin registration
     */
    private void handleAdminRegistration() {
        boolean success = authCLI.showAdminRegistrationMenu();

        if (success) {
            CLIHelper.printSuccess("Admin registration successful! You can now login.");
            CLIHelper.pause();
        }
    }

    /**
     * Show about information
     */
    private void showAbout() {
        CLIHelper.printHeader("ABOUT QUICK_ZEE");
        System.out.println("Quick_Zee Quiz Management System");
        System.out.println("Version: 1.0 (Offline Edition)");
        System.out.println();
        System.out.println("Features:");
        System.out.println("  • Create and manage quizzes");
        System.out.println("  • Multiple-choice questions");
        System.out.println("  • Automatic scoring");
        System.out.println("  • Performance tracking");
        System.out.println("  • Role-based access (Admin/Student)");
        System.out.println();
        System.out.println("Developed with Java + MySQL");
        CLIHelper.printSeparator();
        CLIHelper.pause();
    }

    /**
     * Handle application exit
     */
    private void handleExit() {
        CLIHelper.printHeader("GOODBYE!");
        System.out.println("Thank you for using Quick_Zee Quiz System");
        System.out.println("Have a great day! 👋");
        CLIHelper.printSeparator();

        // Cleanup
        SessionManager.logout();
        CLIHelper.closeScanner();

        System.exit(0);
    }
}