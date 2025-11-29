package com.quickzee.common.util;

import com.quickzee.common.model.User;

/**
 * SessionManager utility class
 * Manages the currently logged-in user session
 * This is an in-memory session - data is lost when app closes
 */
public class SessionManager {

    // Static instance to hold the logged-in user
    private static User loggedInUser = null;

    /**
     * Set the currently logged-in user
     * @param user The user who just logged in
     */
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
        if (user != null) {
            System.out.println("✅ Session started for: " + user.getName() + " (" + user.getRole() + ")");
        }
    }

    /**
     * Get the currently logged-in user
     * @return User object or null if no one is logged in
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Check if a user is currently logged in
     * @return true if logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    /**
     * Check if the logged-in user is an admin
     * @return true if admin, false otherwise
     */
    public static boolean isAdmin() {
        return loggedInUser != null && "admin".equalsIgnoreCase(loggedInUser.getRole());
    }

    /**
     * Check if the logged-in user is a student
     * @return true if student, false otherwise
     */
    public static boolean isStudent() {
        return loggedInUser != null && "student".equalsIgnoreCase(loggedInUser.getRole());
    }

    /**
     * Get the logged-in user's ID
     * @return User ID or null if not logged in
     */
    public static Long getLoggedInUserId() {
        return loggedInUser != null ? loggedInUser.getId() : null;
    }

    /**
     * Get the logged-in user's name
     * @return User name or "Guest" if not logged in
     */
    public static String getLoggedInUserName() {
        return loggedInUser != null ? loggedInUser.getName() : "Guest";
    }

    /**
     * Get the logged-in user's role
     * @return Role string or "none" if not logged in
     */
    public static String getLoggedInUserRole() {
        return loggedInUser != null ? loggedInUser.getRole() : "none";
    }

    /**
     * Get the logged-in user's semester
     * @return Semester number or null if not logged in or not set
     */
    public static Integer getLoggedInUserSemester() {
        return loggedInUser != null ? loggedInUser.getSemester() : null;
    }

    /**
     * Logout the current user
     */
    public static void logout() {
        if (loggedInUser != null) {
            System.out.println("👋 Logging out: " + loggedInUser.getName());
        }
        loggedInUser = null;
    }

    /**
     * Require login - throw exception if not logged in
     * Useful for protected operations
     */
    public static void requireLogin() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("❌ User must be logged in to perform this action");
        }
    }

    /**
     * Require admin - throw exception if not admin
     * Useful for admin-only operations
     */
    public static void requireAdmin() {
        requireLogin();
        if (!isAdmin()) {
            throw new IllegalStateException("❌ Admin privileges required for this action");
        }
    }

    /**
     * Print current session info (for debugging)
     */
    public static void printSessionInfo() {
        System.out.println("=== Current Session ===");
        if (isLoggedIn()) {
            System.out.println("User: " + getLoggedInUserName());
            System.out.println("Role: " + getLoggedInUserRole());
            System.out.println("Semester: " + getLoggedInUserSemester());
        } else {
            System.out.println("No user logged in");
        }
        System.out.println("======================");
    }
}