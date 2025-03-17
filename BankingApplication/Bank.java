import java.io.*;
import java.util.*;

class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private String holderName;
    private double balance;
    private int pin; // PIN for authentication
    private List<String> transactionHistory;

    public Account(String accountNumber, String holderName, double initialBalance, int pin) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance;
        this.pin = pin;
        this.transactionHistory = new ArrayList<>();
        addTransaction("Account created with balance: Rs." + initialBalance);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean authenticate(int enteredPin) {
        return this.pin == enteredPin;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposited: Rs." + amount);
            System.out.println("-----> Rs." + amount + " deposited successfully!");
        } else {
            System.out.println("❌ Invalid deposit amount!");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addTransaction("Withdrawn: Rs." + amount);
            System.out.println("-----> Rs." + amount + " withdrawn successfully!");
        } else {
            System.out.println("❌ Insufficient funds or invalid amount!");
        }
    }

    public void applyInterest(double rate) {
        double interest = balance * (rate / 100);
        balance += interest;
        addTransaction("Interest added: Rs." + interest);
    }

    private void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public void showTransactionHistory() {
        System.out.println("\n----> Transaction History for " + holderName + " (" + accountNumber + ")");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }
}

class Bank {
    private static final String FILE_NAME = "bank_accounts.dat";
    private static final Scanner scanner = new Scanner(System.in);
    private static List<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {
        loadAccounts();

        while (true) {
            System.out.println("\n Welcome to the Banking System");
            System.out.println("1 Create Account");
            System.out.println("2 Login to Account");
            System.out.println("3 Save & Exit");
            System.out.print(" Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    loginToAccount();
                    break;
                case 3:
                    saveAccounts();
                    System.out.println(" Exiting. Thank you!");
                    return;
                default:
                    System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }

    private static void createAccount() {
        System.out.print(" Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        System.out.print(" Enter Account Holder Name: ");
        String holderName = scanner.nextLine();
        System.out.print(" Enter Initial Balance: ");
        double initialBalance = scanner.nextDouble();
        System.out.print(" Set a 4-digit PIN: ");
        int pin = scanner.nextInt();

        Account newAccount = new Account(accountNumber, holderName, initialBalance, pin);
        accounts.add(newAccount);
        System.out.println(" Account created successfully!");
    }

    private static void loginToAccount() {
        System.out.print(" Enter Account Number: ");
        String accountNumber = scanner.nextLine();
        System.out.print(" Enter PIN: ");
        int pin = scanner.nextInt();

        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber) && acc.authenticate(pin)) {
                System.out.println(" Login successful!");
                manageAccount(acc);
                return;
            }
        }
        System.out.println("❌ Invalid account number or PIN!");
    }

    private static void manageAccount(Account account) {
        while (true) {
            System.out.println("\n🏦 Account Menu (" + account.getHolderName() + ")");
            System.out.println("1 Deposit Money");
            System.out.println("2 Withdraw Money");
            System.out.println("3 View Transaction History");
            System.out.println("4 Apply Interest (5%)");
            System.out.println("5 Logout");
            System.out.print(" Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.print(" Enter deposit amount: ");
                    account.deposit(scanner.nextDouble());
                    break;
                case 2:
                    System.out.print(" Enter withdrawal amount: ");
                    account.withdraw(scanner.nextDouble());
                    break;
                case 3:
                    account.showTransactionHistory();
                    break;
                case 4:
                    account.applyInterest(5);
                    System.out.println(" Interest applied at 5% rate!");
                    break;
                case 5:
                    System.out.println(" Logging out...");
                    return;
                default:
                    System.out.println(" Invalid choice! Try again.");
            }
        }
    }

    private static void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(accounts);
            System.out.println(" Accounts saved successfully!");
        } catch (IOException e) {
            System.err.println(" Error saving account data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            accounts = (List<Account>) ois.readObject();
            System.out.println(" Accounts loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(" No existing accounts found, starting fresh.");
        }
    }
}
