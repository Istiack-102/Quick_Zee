package com.quickzee.common.cli;

import com.quickzee.common.model.User;
import com.quickzee.common.service.AuthService;
import com.quickzee.common.util.InputValidator;

import java.sql.SQLException;

/**
 * AuthCLI - Handles authentication-related UI
 * Login and registration screens
 */
public class AuthCLI {

    private final AuthService authService;

    public AuthCLI() {
        this.authService = new AuthService();
    }

    /**
     * Show login menu
     * @return User object if successful, null otherwise
     */
    public User showLoginMenu() {
        CLIHelper.printHeader("LOGIN");

        String email = CLIHelper.readNonEmptyString("Email: ");
        String password = CLIHelper.readPassword("Password: ");

        try {
            User user = authService.login(email, password);

            if (user != null) {
                CLIHelper.printSuccess("Login successful! Welcome, " + user.getName() + "!");
                CLIHelper.pause();
                return user;
            } else {
                CLIHelper.printError("Invalid email or password");
                CLIHelper.pause();
                return null;
            }

        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
            return null;
        }
    }

    /**
     * Show student registration menu
     * @return true if successful, false otherwise
     */
    public boolean showStudentRegistrationMenu() {
        CLIHelper.printHeader("STUDENT REGISTRATION");

        try {
            // Collect information
            String name = CLIHelper.readNonEmptyString("Full Name: ");

            String email;
            while (true) {
                email = CLIHelper.readNonEmptyString("Email: ");
                if (InputValidator.isValidEmail(email)) {
                    break;
                }
                CLIHelper.printError("Invalid email format. Please try again.");
            }

            String password;
            while (true) {
                password = CLIHelper.readPassword("Password (min 6 characters): ");
                if (InputValidator.isValidPassword(password)) {
                    break;
                }
                CLIHelper.printError("Password must be at least 6 characters.");
            }

            Integer semester = CLIHelper.readInt("Semester (1-8): ", 1, 8);

            // Register
            User user = authService.registerStudent(name, email, password, semester);

            CLIHelper.printSuccess("Registration successful!");
            CLIHelper.printInfo("You can now login with your email and password.");
            CLIHelper.pause();
            return true;

        } catch (IllegalArgumentException e) {
            CLIHelper.printError("Validation error: " + e.getMessage());
            CLIHelper.pause();
            return false;
        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
            return false;
        }
    }

    /**
     * Show admin registration menu
     * @return true if successful, false otherwise
     */
    public boolean showAdminRegistrationMenu() {
        CLIHelper.printHeader("ADMIN REGISTRATION");

        CLIHelper.printWarning("Admin registration requires verification");
        String adminCode = CLIHelper.readString("Admin Code: ");

        // Simple verification (in production, use a proper system)
        if (!"ADMIN123".equals(adminCode)) {
            CLIHelper.printError("Invalid admin code!");
            CLIHelper.pause();
            return false;
        }

        try {
            // Collect information
            String name = CLIHelper.readNonEmptyString("Full Name: ");

            String email;
            while (true) {
                email = CLIHelper.readNonEmptyString("Email: ");
                if (InputValidator.isValidEmail(email)) {
                    break;
                }
                CLIHelper.printError("Invalid email format. Please try again.");
            }

            String password;
            while (true) {
                password = CLIHelper.readPassword("Password (min 6 characters): ");
                if (InputValidator.isValidPassword(password)) {
                    break;
                }
                CLIHelper.printError("Password must be at least 6 characters.");
            }

            // Register
            User user = authService.registerAdmin(name, email, password);

            CLIHelper.printSuccess("Admin registration successful!");
            CLIHelper.printInfo("You can now login with your email and password.");
            CLIHelper.pause();
            return true;

        } catch (IllegalArgumentException e) {
            CLIHelper.printError("Validation error: " + e.getMessage());
            CLIHelper.pause();
            return false;
        } catch (SQLException e) {
            CLIHelper.printError("Database error: " + e.getMessage());
            CLIHelper.pause();
            return false;
        }
    }
}