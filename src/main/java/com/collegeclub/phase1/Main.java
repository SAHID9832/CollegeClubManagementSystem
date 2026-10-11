package com.collegeclub.phase1;

import java.util.Scanner;

/**
 * Faculty Reference Phase 1 Console Application.
 * Prompts user for registration credentials using Scanner, validates inputs using string logic,
 * and reports registration status.
 */
public class Main {

    public static boolean register(String username, String password) {
        try {
            Validation.validateUsername(username);
            Validation.validatePassword(password);
            User user = new User(username, password);
            System.out.println("Registration Successful!");
            user.displayUser();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Registration Failed.");
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   COLLEGE CLUB MANAGEMENT SYSTEM - PHASE 1");
        System.out.println("        Console User Registration Setup");
        System.out.println("==================================================");

        if (args != null && args.length >= 2) {
            register(args[0], args[1]);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        register(username, password);
    }
}
