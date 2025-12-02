package com.quickzee.common.cli;

import java.util.Scanner;

/**
 * CLIHelper - Utility class for CLI operations
 * Provides consistent input/output handling and formatting
 */
public class CLIHelper {

    private static final Scanner scanner = new Scanner(System.in);

    // ========== INPUT METHODS ==========

    /**
     * Read a string input from user
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Read a non-empty string from user
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (!input.isEmpty()) {
                return input;
            }
            printError("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Read an integer input from user
     */
    public static Integer readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return null;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Invalid number. Please enter a valid integer.");
            }
        }
    }

    /**
     * Read an integer within a specific range
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            Integer value = readInt(prompt);
            if (value != null && value >= min && value <= max) {
                return value;
            }
            printError("Please enter a number between " + min + " and " + max);
        }
    }

    /**
     * Read a long input from user
     */
    public static Long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return null;
                }
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                printError("Invalid number. Please enter a valid number.");
            }
        }
    }

    /**
     * Read password (plain text - CLI limitation)
     */
    public static String readPassword(String prompt) {
        System.out.print(prompt);
        // Note: Console.readPassword() doesn't work in IDEs, using regular input
        return scanner.nextLine().trim();
    }

    /**
     * Read yes/no confirmation
     */
    public static boolean readYesNo(String prompt) {
        while (true) {
            String input = readString(prompt + " (y/n): ").toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            printError("Please enter 'y' or 'n'");
        }
    }

    // ========== DISPLAY METHODS ==========

    /**
     * Print a header with title
     */
    public static void printHeader(String title) {
        int width = 60;
        System.out.println();
        System.out.println("═".repeat(width));

        // Center the title
        int padding = (width - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(0, padding)) + title);

        System.out.println("═".repeat(width));
    }

    /**
     * Print a sub-header
     */
    public static void printSubHeader(String title) {
        System.out.println();
        System.out.println("─".repeat(60));
        System.out.println(title);
        System.out.println("─".repeat(60));
    }

    /**
     * Print a separator line
     */
    public static void printSeparator() {
        System.out.println("─".repeat(60));
    }

    /**
     * Print success message
     */
    public static void printSuccess(String message) {
        System.out.println("✅ " + message);
    }

    /**
     * Print error message
     */
    public static void printError(String message) {
        System.out.println("❌ " + message);
    }

    /**
     * Print warning message
     */
    public static void printWarning(String message) {
        System.out.println("⚠️  " + message);
    }

    /**
     * Print info message
     */
    public static void printInfo(String message) {
        System.out.println("ℹ️  " + message);
    }

    /**
     * Clear screen (works in terminal, not in IDE console)
     */
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing fails, just print newlines
            System.out.println("\n".repeat(50));
        }
    }

    /**
     * Pause and wait for user to press Enter
     */
    public static void pause() {
        System.out.println();
        System.out.print("Press ENTER to continue...");
        scanner.nextLine();
    }

    /**
     * Print empty lines for spacing
     */
    public static void printNewLines(int count) {
        for (int i = 0; i < count; i++) {
            System.out.println();
        }
    }

    /**
     * Print welcome banner
     */
    public static void printWelcomeBanner() {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║              WELCOME TO QUICK_ZEE QUIZ SYSTEM              ║");
        System.out.println("║                                                            ║");
        System.out.println("║           Your Ultimate Quiz Management Platform           ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Close scanner (call on application exit)
     */
    public static void closeScanner() {
        scanner.close();
    }
}