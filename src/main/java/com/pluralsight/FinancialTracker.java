package com.pluralsight;

import java.io.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class FinancialTracker {

    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    //This is ANSI escape codes, and this changes the color of the text in the console.
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";

    public static void main(String[] args) {

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

    /**
     * This method runs as soon as the program starts. It syncs the CSV file with the ArrayList. It will check if the
     * exists first so the program doesn't crash. Then it reads the file, splits the text at each pipe symbol and turns
     * the data into a transaction object.
     */
    public static void loadTransactions(String fileName) {
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
        /* This is a "try with resource". Safest way to close a BufferReader, It will close the file for me automatically
        * instead of my closing it manually with ".close". */
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                LocalDate date = LocalDate.parse(data[0], DATE_FMT);
                LocalTime time = LocalTime.parse(data[1], TIME_FMT);
                String description = data[2];
                String vendor = data[3];
                double amount = Double.parseDouble(data[4]);

                Transaction transaction = new Transaction(date, time, description, vendor, amount );
                transactions.add(transaction);
            }
        } catch (Exception e) {
            System.out.println("Error reading file");
        }
    }

    /**
     * This method prompts the user for the Date & Time, Description, Vendor, and amount. I made sure the amount entered
     * is a positive amount. It splits the Date & Time input to match the file format, I also used a FIleWriter and set
     * it to true so that it saves the new transactions to the bottom of the file without erasing or overwriting the
     * current data.
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
                             //find a way to get it to keep asking
            }
            //Better way!
            String[] dateTimeParts = dateTime.split(" ");
            LocalDate date = LocalDate.parse(dateTimeParts[0], DATE_FMT);
            LocalTime time = LocalTime.parse(dateTimeParts[1], TIME_FMT);

            Transaction transaction = new Transaction(date, time, description, vendor, userAmount);
            transactions.add(transaction);

            String formatted = String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor,  userAmount);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                bw.write(formatted);
                bw.newLine();
                System.out.println(GREEN + "Deposit Recorded\n" + RESET);
            } catch (Exception e)  {
                System.err.println("Error adding information" + e.getMessage());
            }
    }

    /**
     * In this method I did the same thing as I did in my addDeposit method, but I made it so the user enters a positive amount,
     * and it will save to the file as a negative number.
     */
    /**
     *
     * @param scanner
     */
    private static void addPayment(Scanner scanner) {
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
            return; //find a way to get it to keep asking
        }
        String[] dateTimeParts = dateTime.split(" ");
        LocalDate date = LocalDate.parse(dateTimeParts[0], DATE_FMT);
        LocalTime time = LocalTime.parse(dateTimeParts[1], TIME_FMT);
        double amount = -userAmount;

        String formatted = String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor,  amount);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String line;
            bw.write(formatted);
            bw.newLine();
            System.out.println(GREEN + "Deposit Recorded\n" + RESET);
        } catch (Exception e)  {
            System.err.println("Error adding information" + e.getMessage());
        }
    }

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

    /**
     * Displays all transactions in a formatted table.
     * Uses a for each loop to present data from newest to oldest.
     * Uses getters and printf formatting to ensure column alignment.
    * */
    private static void displayLedger() {
        printHeader();

        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        for(Transaction transaction : transactions) {
            printTransactions(transaction);
        }
    }

    /**
     * Filters and displays only positive transaction amounts.
     * Uses a for each loop to print entries where the amount is greater than zero.
     **/
    private static void displayDeposits() {
        printHeader();

        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        for(Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                printTransactions(transaction);
            }
        }
    }

    /**
     * Filters and displays only negative transaction amounts.
     * Uses a for each loop to print entries where the amount is greater than zero.
     **/
    private static void displayPayments() {
        printHeader();

        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        for(Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                printTransactions(transaction);
            }
        }
    }

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

    /**
     * Prints the table title for the displayLedger, displayDeposits, and displayPayments methods.
     */
    private static void printHeader() {
        System.out.println("DATE\t\tTIME\t\tDESCRIPTION\t\tVENDOR\t\t\tAMOUNT");
        System.out.println("----------------------------------------------------------------------");
    }

    /**
     * Takes the transactions and prints it in a formatted row.
     * Uses printf to align the data into columns so the ledger is easy for the user to read.
     * @param transaction
     */
    private static void printTransactions(Transaction transaction) {
        System.out.printf("%-12s %-12s %-12s %-12s %10.2f\n", transaction.getDate(), transaction.getTime(), transaction.getDescription(),
                transaction.getVendor(), transaction.getAmount());
    }
}
