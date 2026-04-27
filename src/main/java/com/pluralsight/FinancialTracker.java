package com.pluralsight;

import java.io.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;


/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        final String RESET = "\u001B[0m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        final String PURPLE = "\u001B[35m";

        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println(YELLOW + "Welcome to TransactionApp" + RESET);
            System.out.println(BLUE + "Choose an option:" + RESET);
            System.out.println(PURPLE + "D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit" + RESET);

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.err.println("Invalid option\n");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        // TODO: create file if it does not exist, then read each line,
        //       parse the five fields, build a Transaction object,
        //       and add it to the transactions list.
        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                System.out.println("File " + fileName + " created.\n\n");
            }else {
                System.out.println("File " + fileName + " exists.\n\n");
            }
        }catch (Exception e) {
            System.err.println("Error finding file" + e.getMessage());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                LocalDate date = LocalDate.parse(data[0], DATE_FMT);
                LocalTime time = LocalTime.parse(data[1], TIME_FMT);
                String name = data[2];
                String company = data[3];
                double amount = Double.parseDouble(data[4]);

                Transaction transaction = new Transaction(date, time, name,company, amount );
                transactions.add(transaction);
            }
        } catch (Exception e) {
            System.out.println("Error reading file");
        }
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {
        // TODO
        final String RESET = "\u001B[0m";
        final String BLUE = "\u001B[34m";
        final String GREEN = "\u001B[32m";

        System.out.print(BLUE + "Date & Time (yyyy-MM-dd HH:mm:ss): ");
        String dateTime = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine();
        System.out.print("Amount (positive): " + RESET);
        double userAmount = Double.parseDouble(scanner.nextLine());

        if (userAmount <= 0) {
            System.err.println("Invalid Amount");
            return;
        }
        String[] dateTimeParts = dateTime.split(" ");
        LocalDate date = LocalDate.parse(dateTimeParts[0], DATE_FMT);
        LocalTime time = LocalTime.parse(dateTimeParts[1], TIME_FMT);
        double amount = userAmount;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String line;
            bw.write(date + "|" + time + "|" + description + "|" + vendor + "|" + "$" + amount);
            bw.newLine();
            System.out.println(GREEN + "Deposit Recorded\n" + RESET);
        } catch (Exception e)  {
            System.err.println("Error adding information" + e.getMessage());
        }
    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        // TODO
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        final String RESET = "\u001B[0m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        final String PURPLE = "\u001B[35m";

        boolean running = true;
        while (running) {
            System.out.println(YELLOW + "Ledger"  + RESET);
            System.out.println(BLUE + "Choose an option:" + RESET);
            System.out.println(PURPLE + "A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home" + RESET);

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.err.println("Invalid option\n");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() { /* TODO – print all transactions in column format */ }

    private static void displayDeposits() { /* TODO – only amount > 0               */ }

    private static void displayPayments() { /* TODO – only amount < 0               */ }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        final String RESET = "\u001B[0m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        final String PURPLE = "\u001B[35m";

        boolean running = true;
        while (running) {
            System.out.println(YELLOW + "Reports" + RESET);
            System.out.println(BLUE + "Choose an option:" + RESET);
            System.out.println(PURPLE + "1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back" + RESET);

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {/* TODO – month-to-date report */ }
                case "2" -> {/* TODO – previous month report */ }
                case "3" -> {/* TODO – year-to-date report   */ }
                case "4" -> {/* TODO – previous year report  */ }
                case "5" -> {/* TODO – prompt for vendor then report */ }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.err.println("Invalid option\n");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // TODO – iterate transactions, print those within the range
    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */
        return null;
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double   or null */
        return null;
    }
}
