import java.security.*;
import java.util.*;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
class User {
    private final String username;
    private final String passwordHash;
    private double balance;
    private final List<String> transactionHistory = new ArrayList<>();
    public User(String username, String password) {
        this.username = username;this.passwordHash = hashPassword(password);
        this.balance = 0.0;}
    private String hashPassword(String password) {
        try {MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);}
        catch (NoSuchAlgorithmException e) {throw new RuntimeException(e);}
    }
    public boolean authenticate(String password) {return this.passwordHash.equals(hashPassword(password));}
    public void deposit(double amount) {
        if (amount > 0) {balance += amount;transactionHistory.add("Deposited: " + amount);}}
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {balance -= amount;
            transactionHistory.add("Withdrawn: " + amount);
            return true;}
        return false;}
    public boolean transfer(User recipient, double amount) {
        if (recipient != null && withdraw(amount)) {recipient.deposit(amount);
            transactionHistory.add("Transferred " + amount + " to " + recipient.getUsername());
            return true;}
        return false;}
    public String getUsername() {return username;}
    public double getBalance() {return balance;}
    public List<String> getTransactionHistory() {return new ArrayList<>(transactionHistory);}
}
public class BankingSystem {
    private static final Map<String, User> users = new HashMap<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {System.out.println("1. Register\n2. Login\n3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {case 1 -> registerUser(scanner);
                case 2 -> loginUser(scanner);
                case 3 -> {System.out.println("Exiting...");
                    scanner.close();
                    return;}
                default -> System.out.println("Invalid choice!");}}}
    private static void registerUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {System.out.println("Username already exists!");
            return;}
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        users.put(username, new User(username, password));
        System.out.println("User registered successfully!");}
    private static void loginUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        User user = users.get(username);
        if (user != null && user.authenticate(password)) {System.out.println("Login successful!");
            userMenu(user, scanner);}
        else {System.out.println("Invalid credentials!");}}
    private static void userMenu(User user, Scanner scanner) {
        while (true) {
            System.out.println("1. Deposit\n2. Withdraw\n3. Transfer\n4. Balance\n5. Transactions\n6. Logout");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {case 1 -> {
                    System.out.print("Enter amount: ");
                    double amount = scanner.nextDouble();
                    user.deposit(amount);
                    System.out.println("Deposited successfully.");}
                case 2 -> {System.out.print("Enter amount: ");
                    double amount = scanner.nextDouble();
                    if (user.withdraw(amount)) {System.out.println("Withdrawn successfully.");}
                    else {System.out.println("Insufficient funds or invalid amount!");}}
                case 3 -> {
                    System.out.print("Enter recipient username: ");
                    String recipientUsername = scanner.nextLine();
                    User recipient = users.get(recipientUsername);
                    if (recipient != null) {System.out.print("Enter amount: ");
                        double amount = scanner.nextDouble();
                        if (user.transfer(recipient, amount)) {System.out.println("Transfer successful.");}
                        else {System.out.println("Transfer failed: insufficient funds or invalid amount.");}}
                    else {System.out.println("User not found.");}}
                case 4 -> System.out.println("Balance: " + user.getBalance());
                case 5 -> {System.out.println("Transaction History:");
                    for (String transaction : user.getTransactionHistory()) {System.out.println(transaction);}}
                case 6 -> {System.out.println("Logging out...");
                    return;}
                default -> System.out.println("Invalid choice!");}}}
}
