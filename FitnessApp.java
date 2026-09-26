import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class FitnessApp {

    private static final Scanner scanner = new Scanner(System.in);

    // Persistence
    private static final DataStore dataStore = new DataStore("smartfit_data.ser");

    // Multi-user storage
    private static HashMap<String, User> users;

    // Logged-in user
    private static User currentUser = null;

    // Undo/Redo stacks (not persisted)
    private static final Stack<Action> undoStack = new Stack<>();
    private static final Stack<Action> redoStack = new Stack<>();

    public static void main(String[] args) {
        users = dataStore.load();
        for (User u : users.values()) u.rebuildExerciseStats(); // fixes stats saved by older versions
        System.out.println("Loaded " + users.size() + " user(s).");

        while (true) {
            if (currentUser == null) startMenu();
            else userMenu();
        }
    }

    // ---------------- START MENU ----------------

    private static void startMenu() {
        System.out.println("\n=== SmartFit Tracker ===");
        System.out.println("1) Login");
        System.out.println("2) Register");
        System.out.println("3) Exit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> login();
            case "2" -> register();
            case "3" -> exitAndSave();
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

        User user = new User(username);
        users.put(username, user);
        currentUser = user;

        undoStack.clear();
        redoStack.clear();

        dataStore.save(users);
        System.out.println("Registered and logged in as: " + currentUser.getUsername());
    }

    private static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim().toLowerCase();

        User user = users.get(username);
        if (user == null) {
            System.out.println("User not found. Register first.");
            return;
        }

        currentUser = user;
        undoStack.clear();
        redoStack.clear();

        System.out.println("Logged in as: " + currentUser.getUsername());
    }

    private static void logout() {
        dataStore.save(users);
        currentUser = null;
        undoStack.clear();
        redoStack.clear();
        System.out.println("Logged out (saved).");
    }

    private static void exitAndSave() {
        dataStore.save(users);
        System.out.println("Goodbye!");
        System.exit(0);
    }

    // ---------------- USER MENU ----------------

    private static void userMenu() {
        System.out.println("\n--- User Menu (" + currentUser.getUsername() + ") ---");
        System.out.println("1) Add workout session");
        System.out.println("2) Log bodyweight");
        System.out.println("3) View workouts (history / sort / filter)");
        System.out.println("4) View PR timeline");
        System.out.println("5) View progress (summary)");
        System.out.println("6) Undo");
        System.out.println("7) Redo");
        System.out.println("8) Logout");
        System.out.println("9) Exit");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> addWorkoutSession();
            case "2" -> logBodyWeight();
            case "3" -> workoutsMenu();          // ✅ Step 1 + 2
            case "4" -> viewPRTimeline();        // ✅ Step 3
            case "5" -> viewProgress();
            case "6" -> undo();
            case "7" -> redo();
            case "8" -> logout();
            case "9" -> exitAndSave();
            default -> System.out.println("Invalid option. Try again.");
        }
    }

    // ---------------- UNDO / REDO ----------------

    private static void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        Action a = undoStack.pop();
        a.undo();
        redoStack.push(a);
        dataStore.save(users);
    }

    private static void redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo.");
            return;
        }
        Action a = redoStack.pop();
        a.redo();
        undoStack.push(a);
        dataStore.save(users);
    }

    // ---------------- BODYWEIGHT ----------------

    private static void logBodyWeight() {
        LocalDate date = readDateOrToday("Entry date (YYYY-MM-DD) [Enter for today]: ");

        System.out.print("Body weight (lbs): ");
        String weightStr = scanner.nextLine().trim();

        try {
            double weight = Double.parseDouble(weightStr);
            if (weight <= 0 || weight > 1500) {
                System.out.println("Weight must be between 0 and 1500 lbs.");
                return;
            }
            BodyWeightEntry entry = new BodyWeightEntry(date, weight);

            Action action = new AddBodyWeightAction(currentUser, entry);
            action.redo();

            undoStack.push(action);
            redoStack.clear();

            dataStore.save(users);
            System.out.println("Body weight logged.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid weight. Try again.");
        }
    }

    // ---------------- WORKOUTS ----------------

    private static void addWorkoutSession() {
        System.out.println("\nAdd Workout Session");

        LocalDate date = readDateOrToday("Session date (YYYY-MM-DD) [Enter for today]: ");
        WorkoutSession session = new WorkoutSession(date);

        System.out.print("Tags (comma-separated, e.g., legs,push,cardio) [optional]: ");
        String tagLine = scanner.nextLine().trim();
        if (!tagLine.isEmpty()) {
            String[] tags = tagLine.split(",");
            for (String t : tags) {
                String clean = t.trim();
                if (!clean.isEmpty()) session.addTag(clean);
            }
        }

        System.out.println("Add exercises. Type 'done' as the exercise name to finish.");
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

        Action action = new AddWorkoutAction(currentUser, session);
        action.redo();

        undoStack.push(action);
        redoStack.clear();

        dataStore.save(users);
        System.out.println("Workout saved. Total volume: " + session.getTotalVolume());
    }

    // ---------------- STEP 1 + 2: HISTORY / SORT / FILTER ----------------

    private static void workoutsMenu() {
        while (true) {
            System.out.println("\n=== Workouts Menu ===");
            System.out.println("1) View all workouts");
            System.out.println("2) Sort workouts by date");
            System.out.println("3) Sort workouts by total volume");
            System.out.println("4) Filter workouts by tag");
            System.out.println("5) Filter workouts by exercise name");
            System.out.println("6) Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> printSessions(toList(currentUser.getHistory()));
                case "2" -> {
                    ArrayList<WorkoutSession> list = toList(currentUser.getHistory());
                    selectionSortByDate(list);
                    printSessions(list);
                }
                case "3" -> {
                    ArrayList<WorkoutSession> list = toList(currentUser.getHistory());
                    selectionSortByVolume(list);
                    printSessions(list);
                }
                case "4" -> filterByTag();
                case "5" -> filterByExercise();
                case "6" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void filterByTag() {
        System.out.print("Enter tag to filter (e.g., legs): ");
        String tag = scanner.nextLine().trim().toLowerCase();
        if (tag.isEmpty()) {
            System.out.println("Tag cannot be empty.");
            return;
        }

        ArrayList<WorkoutSession> results = new ArrayList<>();
        for (WorkoutSession s : currentUser.getHistory()) {
            if (s.getTags().contains(tag)) results.add(s);
        }

        System.out.println("\nFiltered by tag: " + tag);
        printSessions(results);
    }

    private static void filterByExercise() {
        System.out.print("Enter exercise name to filter (e.g., bench press): ");
        String target = scanner.nextLine().trim().toLowerCase();
        if (target.isEmpty()) {
            System.out.println("Exercise name cannot be empty.");
            return;
        }

        ArrayList<WorkoutSession> results = new ArrayList<>();

        for (WorkoutSession s : currentUser.getHistory()) {
            boolean found = false;
            for (ExerciseEntry e : s.getExercises()) {
                if (e.getName().toLowerCase().equals(target)) {
                    found = true;
                    break;
                }
            }
            if (found) results.add(s);
        }

        System.out.println("\nFiltered by exercise: " + target);
        printSessions(results);
    }

    private static void printSessions(ArrayList<WorkoutSession> sessions) {
        if (sessions.isEmpty()) {
            System.out.println("No workouts found.");
            return;
        }

        int i = 1;
        for (WorkoutSession s : sessions) {
            System.out.println("\n--- Session " + (i++) + " ---");
            System.out.println(s);
        }
    }

    private static ArrayList<WorkoutSession> toList(Iterable<WorkoutSession> iterable) {
        ArrayList<WorkoutSession> list = new ArrayList<>();
        for (WorkoutSession s : iterable) list.add(s);
        return list;
    }

    // Custom sorting algorithm (Selection Sort)
    private static void selectionSortByDate(ArrayList<WorkoutSession> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < list.size(); j++) {
                // earliest date first
                if (list.get(j).getDate().isBefore(list.get(minIndex).getDate())) {
                    minIndex = j;
                }
            }
            swap(list, i, minIndex);
        }
    }

    // Custom sorting algorithm (Selection Sort)
    private static void selectionSortByVolume(ArrayList<WorkoutSession> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < list.size(); j++) {
                // highest volume first
                if (list.get(j).getTotalVolume() > list.get(maxIndex).getTotalVolume()) {
                    maxIndex = j;
                }
            }
            swap(list, i, maxIndex);
        }
    }

    private static void swap(ArrayList<WorkoutSession> list, int i, int j) {
        if (i == j) return;
        WorkoutSession temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    // ---------------- STEP 3: PR TIMELINE ----------------

    private static void viewPRTimeline() {
        if (currentUser.getExerciseStats().isEmpty()) {
            System.out.println("No exercise stats yet. Log a workout first.");
            return;
        }

        System.out.println("\nExercises with stats:");
        for (ExerciseStats s : currentUser.getExerciseStats().values()) {
            System.out.println(" - " + s.getExerciseName());
        }

        System.out.print("\nEnter exercise name: ");
        String target = scanner.nextLine().trim();

        ExerciseStats stats = currentUser.getExerciseStats().get(target.toLowerCase());
        if (stats == null) {
            System.out.println("Exercise not found in stats.");
            return;
        }

        System.out.println("\n=== PR Timeline for " + stats.getExerciseName() + " ===");
        if (stats.getPrTimeline().isEmpty()) {
            System.out.println("No PR updates yet.");
            return;
        }

        for (String line : stats.getPrTimeline()) {
            System.out.println(" - " + line);
        }
    }

    // ---------------- VIEW PROGRESS (summary) ----------------

    private static void viewProgress() {
        System.out.println("\n=== Progress for " + currentUser.getUsername() + " ===");

        System.out.println("Workouts logged: " + currentUser.getHistory().size());
        System.out.println("Bodyweight entries: " + currentUser.getBodyweights().size());

        if (!currentUser.getBodyweights().isEmpty()) {
            // Use the earliest and latest DATES, not the order they were typed in
            BodyWeightEntry first = currentUser.getBodyweights().get(0);
            BodyWeightEntry last = first;
            for (BodyWeightEntry b : currentUser.getBodyweights()) {
                if (b.getDate().isBefore(first.getDate())) first = b;
                if (!b.getDate().isBefore(last.getDate())) last = b;
            }
            double diff = last.getWeight() - first.getWeight();

            System.out.println("First: " + first);
            System.out.println("Last : " + last);
            System.out.printf("Trend: %.1f lbs%n", diff);
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