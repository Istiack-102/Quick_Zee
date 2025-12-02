package com.quickzee.common.service;

import com.quickzee.common.dao.UserDao;
import com.quickzee.common.model.User;
import com.quickzee.common.util.InputValidator;
import com.quickzee.common.util.PasswordHasher;
import com.quickzee.common.util.SessionManager;

import java.sql.SQLException;

/**
 * AuthService
 * Handles all authentication-related business logic
 * - User registration
 * - User login
 * - Password verification
 * - Session management
 */
public class AuthService {

    private final UserDao userDao;

    public AuthService() {
        this.userDao = new UserDao();
    }

    /**
     * Register a new user
     * @param name User's full name
     * @param email User's email
     * @param password User's password (will be hashed)
     * @param semester User's semester (can be null)
     * @param role User's role ("admin" or "student")
     * @return Created User object
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public User register(String name, String email, String password, Integer semester, String role)
            throws SQLException {

        // Validate inputs
        if (!InputValidator.isValidName(name)) {
            throw new IllegalArgumentException("Name must be 2-100 characters and contain only letters and spaces");
        }

        if (!InputValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!InputValidator.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        if (semester != null && !InputValidator.isValidSemester(semester)) {
            throw new IllegalArgumentException("Semester must be between 1 and 8");
        }

        if (!InputValidator.isValidRole(role)) {
            throw new IllegalArgumentException("Role must be 'admin' or 'student'");
        }

        // Check if email already exists
        User existingUser = userDao.findByEmail(email);
        if (existingUser != null) {
            throw new IllegalArgumentException("Email already registered. Please use a different email.");
        }

        // Hash password
        String hashedPassword = PasswordHasher.hashPassword(password);

        // Create user object
        User user = new User();
        user.setName(InputValidator.sanitize(name));
        user.setEmail(InputValidator.sanitize(email).toLowerCase());
        user.setPassword(hashedPassword);
        user.setSemester(semester);
        user.setRole(role.toLowerCase());

        // Save to database
        userDao.insert(user);

        System.out.println("✅ User registered successfully!");
        System.out.println("   Name: " + user.getName());
        System.out.println("   Email: " + user.getEmail());
        System.out.println("   Role: " + user.getRole());

        return user;
    }

    /**
     * Register a student (convenience method)
     * @param name Student name
     * @param email Student email
     * @param password Student password
     * @param semester Student semester
     * @return Created User object
     * @throws SQLException if database error occurs
     */
    public User registerStudent(String name, String email, String password, Integer semester)
            throws SQLException {
        return register(name, email, password, semester, "student");
    }

    /**
     * Register an admin (convenience method)
     * @param name Admin name
     * @param email Admin email
     * @param password Admin password
     * @return Created User object
     * @throws SQLException if database error occurs
     */
    public User registerAdmin(String name, String email, String password)
            throws SQLException {
        return register(name, email, password, null, "admin");
    }

    /**
     * Login a user
     * @param email User's email
     * @param password User's password (plain text)
     * @return User object if login successful, null otherwise
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public User login(String email, String password) throws SQLException {

        // Validate inputs
        if (!InputValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (InputValidator.isEmpty(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Find user by email
        User user = userDao.findByEmail(email.trim().toLowerCase());

        if (user == null) {
            System.out.println("❌ Login failed: Email not found");
            return null;
        }

        // Verify password
        boolean passwordMatch = PasswordHasher.verifyPassword(password, user.getPassword());

        if (!passwordMatch) {
            System.out.println("❌ Login failed: Incorrect password");
            return null;
        }

        // Set session
        SessionManager.setLoggedInUser(user);

        System.out.println("✅ Login successful!");
        System.out.println("   Welcome, " + user.getName() + "!");
        System.out.println("   Role: " + user.getRole());

        return user;
    }

    /**
     * Logout the current user
     */
    public void logout() {
        if (SessionManager.isLoggedIn()) {
            String name = SessionManager.getLoggedInUserName();
            SessionManager.logout();
            System.out.println("✅ Logged out successfully. Goodbye, " + name + "!");
        } else {
            System.out.println("⚠ No user is currently logged in");
        }
    }

    /**
     * Change password for logged-in user
     * @param currentPassword Current password
     * @param newPassword New password
     * @throws IllegalStateException if not logged in
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public void changePassword(String currentPassword, String newPassword) throws SQLException {

        SessionManager.requireLogin();

        // Validate new password
        if (!InputValidator.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }

        // Get current user
        User user = SessionManager.getLoggedInUser();

        // Verify current password
        boolean passwordMatch = PasswordHasher.verifyPassword(currentPassword, user.getPassword());

        if (!passwordMatch) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Hash new password
        String hashedPassword = PasswordHasher.hashPassword(newPassword);

        // Update in database
        user.setPassword(hashedPassword);
        userDao.update(user);

        System.out.println("✅ Password changed successfully!");
    }

    /**
     * Update user profile (name, semester)
     * @param newName New name (can be null to keep current)
     * @param newSemester New semester (can be null to keep current)
     * @throws IllegalStateException if not logged in
     * @throws IllegalArgumentException if validation fails
     * @throws SQLException if database error occurs
     */
    public void updateProfile(String newName, Integer newSemester) throws SQLException {

        SessionManager.requireLogin();

        User user = SessionManager.getLoggedInUser();
        boolean updated = false;

        // Update name if provided
        if (newName != null && !newName.trim().isEmpty()) {
            if (!InputValidator.isValidName(newName)) {
                throw new IllegalArgumentException("Name must be 2-100 characters and contain only letters and spaces");
            }
            user.setName(InputValidator.sanitize(newName));
            updated = true;
        }

        // Update semester if provided
        if (newSemester != null) {
            if (!InputValidator.isValidSemester(newSemester)) {
                throw new IllegalArgumentException("Semester must be between 1 and 8");
            }
            user.setSemester(newSemester);
            updated = true;
        }

        if (updated) {
            userDao.update(user);
            System.out.println("✅ Profile updated successfully!");
        } else {
            System.out.println("⚠ No changes made");
        }
    }

    /**
     * Check if an email is already registered
     * @param email Email to check
     * @return true if email exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean isEmailRegistered(String email) throws SQLException {
        if (!InputValidator.isValidEmail(email)) {
            return false;
        }
        return userDao.findByEmail(email.trim().toLowerCase()) != null;
    }

    /**
     * Get current logged-in user
     * @return User object or null if not logged in
     */
    public User getCurrentUser() {
        return SessionManager.getLoggedInUser();
    }

    /**
     * Check if current user is admin
     * @return true if admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return SessionManager.isAdmin();
    }

    /**
     * Check if current user is student
     * @return true if student, false otherwise
     */
    public boolean isCurrentUserStudent() {
        return SessionManager.isStudent();
    }

    /**
     * Print current user info
     */
    public void printCurrentUserInfo() {
        if (!SessionManager.isLoggedIn()) {
            System.out.println("⚠ No user logged in");
            return;
        }

        User user = SessionManager.getLoggedInUser();
        System.out.println("\n=== Current User Info ===");
        System.out.println("Name:     " + user.getName());
        System.out.println("Email:    " + user.getEmail());
        System.out.println("Role:     " + user.getRole());
        System.out.println("Semester: " + (user.getSemester() != null ? user.getSemester() : "N/A"));
        System.out.println("========================\n");
    }

    /**
     * Validate login credentials without logging in (for testing)
     * @param email User email
     * @param password User password
     * @return true if credentials are valid, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean validateCredentials(String email, String password) throws SQLException {
        try {
            User user = userDao.findByEmail(email.trim().toLowerCase());
            if (user == null) {
                return false;
            }
            return PasswordHasher.verifyPassword(password, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }
}