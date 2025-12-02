package com.quickzee.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * PasswordHasher utility class
 * Uses SHA-256 for password hashing
 * Provides secure password storage and verification
 */
public class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";

    /**
     * Hash a password using SHA-256
     * @param password Plain text password
     * @return Hashed password as hexadecimal string
     * @throws RuntimeException if hashing fails
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);

            // Perform hashing
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            return bytesToHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verify a password against its hash
     * @param password Plain text password to verify
     * @param hashedPassword Stored hashed password
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }

        try {
            String newHash = hashPassword(password);
            return newHash.equals(hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Convert byte array to hexadecimal string
     * @param bytes Byte array to convert
     * @return Hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {
            // Convert each byte to 2-digit hex
            String hex = Integer.toHexString(0xff & b);

            // Add leading zero if needed
            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Generate a sample hashed password (for testing/setup)
     * @param password Password to hash
     */
    public static void printHashedPassword(String password) {
        System.out.println("Original: " + password);
        System.out.println("Hashed:   " + hashPassword(password));
        System.out.println("Length:   " + hashPassword(password).length() + " characters");
    }

    /**
     * Test the password hasher (for debugging)
     */
    public static void testHasher() {
        System.out.println("\n=== Password Hasher Test ===");

        String password = "myPassword123";
        String hash1 = hashPassword(password);
        String hash2 = hashPassword(password);

        System.out.println("Password: " + password);
        System.out.println("Hash 1:   " + hash1);
        System.out.println("Hash 2:   " + hash2);
        System.out.println("Hashes match: " + hash1.equals(hash2));

        System.out.println("\n--- Verification Test ---");
        System.out.println("Correct password: " + verifyPassword(password, hash1));
        System.out.println("Wrong password:   " + verifyPassword("wrongPassword", hash1));

        System.out.println("===========================\n");
    }
}