import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class FitnessApp {

    private static final HashMap<String, User> users = new HashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    private static User currentUser = null;

    // Undo/redo stacks use your Action classes
    private static final Stack<Action> undoStack = new Stack<>();
    private static final Stack<Action> redoStack = new Stack<>();

    public static void main(String[] args) {
        while (true) {
            if (currentUser == null) {
                showStartMenu();
            } else {
                showUserMenu();
            }
        }
    }

    //  START MENU (login/register/exit)

    private static void showStartMenu() {
        System.out.println("\n=== SmartFit Tracker ===");
        System.out.println("1) Login");
        System.out.println("2) Register");
        System.out.println("3) Exit");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> login();
            case "2" -> register();
            case "3" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid option. Try again.");
        }
    }

    private static void register() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim().toLowerCase();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        if (users.containsKey(username)) {
            System.out.println("That username already exists.");
            return;
        }

        User u = new User(username);
        users.put(username, u);
        currentUser = u;

        // Optional: clear stacks on new login
        undoStack.clear();
        redoStack.clear();

        System.out.println("Registered and logged in as: " + currentUser.getUsername());
    }

    private static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim().toLowerCase();

        User u = users.get(username);
        if (u == null) {
            System.out.println("User not found. Register first.");
            return;
        }

        currentUser = u;

        // Optional: clear stacks on login (keeps undo/redo per session-run)
        undoStack.clear();
        redoStack.clear();

        System.out.println("Logged in as: " + currentUser.getUsername());
    }

    // USER MENU 

    private static void showUserMenu() {
        System.out.println("\n--- User Menu (" + currentUser.getUsername() + ") ---");
        System.out.println("1) Add workout session");
        System.out.println("2) Log bodyweight");
        System.out.println("3) View progress");
        System.out.println("4) Undo");
        System.out.println("5) Redo");
        System.out.println("6) Logout");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> addWorkoutSession();
            case "2" -> logBodyWeight();
            case "3" -> viewProgress();
            case "4" -> undo();
            case "5" -> redo();
            case "6" -> logout();
            default -> System.out.println("Invalid option. Try again.");
        }
    }

    private static void logout() {
        System.out.println("Logged out.");
        currentUser = null;
        undoStack.clear();
        redoStack.clear();
    }

    //ACTIONS: UNDO / REDO 

    private static void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        Action a = undoStack.pop();
        a.undo();
        redoStack.push(a);
    }

    private static void redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo.");
            return;
        }
        Action a = redoStack.pop();
        a.redo();
        undoStack.push(a);
    }

    //  FEATURE: LOG BODYWEIGHT (with Action) 

    private static void logBodyWeight() {
        LocalDate date = readDateOrToday("Enter date (YYYY-MM-DD) [Enter for today]: ");

        System.out.print("Enter body weight (lbs): ");
        String weightStr = scanner.nextLine().trim();

        try {
            double weight = Double.parseDouble(weightStr);
            BodyWeightEntry entry = new BodyWeightEntry(date, weight);

            // Do + track action
            currentUser.addBodyweightEntry(entry);
            Action action = new AddBodyWeightAction(currentUser, entry);
            undoStack.push(action);
            redoStack.clear();

            System.out.println("Body weight logged successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid weight. Try again.");
        }
    }

    // ---------------- FEATURE: ADD WORKOUT SESSION (with AddWorkoutAction) ----------------

    private static void addWorkoutSession() {
        System.out.println("\nAdd Workout Session");
        LocalDate date = readDateOrToday("Session date (YYYY-MM-DD) [Enter for today]: ");

        WorkoutSession session = new WorkoutSession(date);

        // Tags
        System.out.print("Tags (comma-separated, e.g., legs,push,cardio) [optional]: ");
        String tagLine = scanner.nextLine().trim();
        if (!tagLine.isEmpty()) {
            String[] tags = tagLine.split(",");
            for (String t : tags) {
                String clean = t.trim();
                if (!clean.isEmpty()) session.addTag(clean);
            }
        }

        // Exercises
        System.out.println("Add exercises. Type 'done' as the name to finish.");
        while (true) {
            System.out.print("Exercise name: ");
            String name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("done")) break;
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                continue;
            }

            System.out.print("Muscle group (e.g., chest/legs/back/shoulders/arms/core): ");
            String muscle = scanner.nextLine().trim();
            if (muscle.isEmpty()) muscle = "Unknown";

            int sets = readInt("Sets: ", 1, 100);
            int reps = readInt("Reps: ", 1, 1000);
            double weight = readDouble("Weight (lbs): ", 0, 10000);

            ExerciseEntry entry = new ExerciseEntry(name, sets, reps, weight, muscle);
            session.addExercise(entry);

            System.out.println("Added: " + entry);
        }

        if (session.getExercises().isEmpty()) {
            System.out.println("No exercises added. Workout discarded.");
            return;
        }

        // Add to history + update stats
        currentUser.addWorkoutSession(session);

        // Track undo/redo via Action
        Action action = new AddWorkoutAction(currentUser, session);
        undoStack.push(action);
        redoStack.clear();

        System.out.println("Workout saved. Total volume: " + session.getTotalVolume());
    }

    // ---------------- VIEW PROGRESS ----------------

    private static void viewProgress() {
        System.out.println("\n=== Progress for " + currentUser.getUsername() + " ===");

        // Workouts
        System.out.println("Workouts logged: " + currentUser.getHistory().size());
        if (!currentUser.getHistory().isEmpty()) {
            System.out.println("\nMost recent workout:");
            System.out.println(currentUser.getHistory().getLast());
        }

        // Bodyweight
        System.out.println("\nBodyweight entries: " + currentUser.getBodyweights().size());
        if (!currentUser.getBodyweights().isEmpty()) {
            BodyWeightEntry first = currentUser.getBodyweights().get(0);
            BodyWeightEntry last = currentUser.getBodyweights().get(currentUser.getBodyweights().size() - 1);

            System.out.println("First: " + first);
            System.out.println("Last : " + last);

            double diff = last.getWeight() - first.getWeight();
            System.out.printf("Trend: %.1f lbs%n", diff);
        }

        // Exercise PRs / volume summary (quick look)
        if (!currentUser.getExerciseStats().isEmpty()) {
            System.out.println("\nExercise stats (PR + total volume):");
            for (ExerciseStats stats : currentUser.getExerciseStats().values()) {
                System.out.println(" - " + stats);
            }
        } else {
            System.out.println("\nNo exercise stats yet (log a workout first).");
        }
    }

    // ---------------- INPUT HELPERS ----------------

    private static LocalDate readDateOrToday(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) return LocalDate.now();
            try {
                return LocalDate.parse(raw);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use YYYY-MM-DD (example: 2025-12-14).");
            }
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(raw);
                if (val < min || val > max) {
                    System.out.println("Enter a number from " + min + " to " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(raw);
                if (val < min || val > max) {
                    System.out.println("Enter a number from " + min + " to " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}