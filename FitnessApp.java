import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Scanner;
public class FitnessApp {
    private static HashMap<String, User> users = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Register User");
            System.out.println("2. Log Body Weight");
            System.out.println("3. View Progress");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerUser();
                    break;
                case "2":
                    logBodyWeight();
                    break;
                case "3":
                    viewProgress();
                    break;
                case "4":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return;
        }
        users.put(username, new User(username));
        System.out.println("User registered successfully.");
    }
    private static void logBodyWeight() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        User user = users.get(username);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        System.out.print("Enter body weight (kg): ");
        String weightStr = scanner.nextLine();
        try {
            LocalDate date = LocalDate.parse(dateStr);
            double weight = Double.parseDouble(weightStr);
            user.logBodyWeight(date, weight);
            System.out.println("Body weight logged successfully.");
        } catch (DateTimeParseException | NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
        }
    }
   
        