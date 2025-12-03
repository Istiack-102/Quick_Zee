package com.quickzee.common.util;

import java.util.regex.Pattern;

/**
 * InputValidator utility class
 * Validates user inputs to ensure data integrity and security
 */
public class InputValidator {

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Check if a string is null or empty (after trimming)
     * @param str String to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if a string is NOT empty
     * @param str String to check
     * @return true if not empty, false otherwise
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate that a string is within a certain length range
     * @param str String to check
     * @param minLength Minimum length (inclusive)
     * @param maxLength Maximum length (inclusive)
     * @return true if within range, false otherwise
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (isEmpty(str)) {
            return minLength == 0;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets minimum requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        // Minimum 6 characters for simplicity (can be made stricter)
        return isNotEmpty(password) && password.length() >= 6;
    }

    /**
     * Validate that a string contains only digits
     * @param str String to check
     * @return true if only digits, false otherwise
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.trim().matches("\\d+");
    }

    /**
     * Validate and parse integer from string
     * @param str String to parse
     * @return Parsed integer or null if invalid
     */
    public static Integer parseInteger(String str) {
        if (isEmpty(str)) {
            return null;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validate and parse long from string
     * @param str String to parse
     * @return Parsed long or null if invalid
     */
    public static Long parseLong(String str) {
        if (isEmpty(str)) {
            return null;
        }
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validate semester (1-8)
     * @param semester Semester to validate
     * @return true if valid (1-8), false otherwise
     */
    public static boolean isValidSemester(Integer semester) {
        return semester != null && semester >= 0 && semester <= 8;
    }

    /**
     * Validate choice within a range (e.g., menu options)
     * @param choice User's choice
     * @param min Minimum valid value
     * @param max Maximum valid value
     * @return true if within range, false otherwise
     */
    public static boolean isValidChoice(Integer choice, int min, int max) {
        return choice != null && choice >= min && choice <= max;
    }

    /**
     * Validate quiz duration (1-180 minutes)
     * @param minutes Duration in minutes
     * @return true if valid, false otherwise
     */
    public static boolean isValidDuration(Integer minutes) {
        return minutes != null && minutes >= 1 && minutes <= 180;
    }

    /**
     * Sanitize string input (remove leading/trailing whitespace, prevent null)
     * @param str String to sanitize
     * @return Sanitized string (empty string if null)
     */
    public static String sanitize(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * Validate that a string is not too long (for database TEXT fields)
     * @param str String to check
     * @param maxLength Maximum allowed length
     * @return true if within limit, false otherwise
     */
    public static boolean isWithinMaxLength(String str, int maxLength) {
        if (str == null) {
            return true;
        }
        return str.length() <= maxLength;
    }

    /**
     * Validate role (admin or student)
     * @param role Role string to validate
     * @return true if valid role, false otherwise
     */
    public static boolean isValidRole(String role) {
        if (isEmpty(role)) {
            return false;
        }
        String normalized = role.trim().toLowerCase();
        return normalized.equals("admin") || normalized.equals("student");
    }

    /**
     * Print validation error message
     * @param fieldName Name of the field that failed validation
     * @param reason Reason for failure
     */
    public static void printValidationError(String fieldName, String reason) {
        System.out.println("❌ Validation Error - " + fieldName + ": " + reason);
    }

    /**
     * Validate name (2-100 characters, letters and spaces only)
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (isEmpty(name)) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= 2 &&
                trimmed.length() <= 100 &&
                trimmed.matches("[a-zA-Z\\s]+");
    }

    /**
     * Validate quiz title (3-255 characters)
     * @param title Title to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidQuizTitle(String title) {
        return isValidLength(title, 3, 255);
    }

    /**
     * Validate question text (10-5000 characters)
     * @param text Question text to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidQuestionText(String text) {
        return isValidLength(text, 10, 5000);
    }

    /**
     * Validate option text (1-1000 characters)
     * @param text Option text to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidOptionText(String text) {
        return isValidLength(text, 1, 1000);
    }
}