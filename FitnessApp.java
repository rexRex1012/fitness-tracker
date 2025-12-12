import java.time.LocalDate;

public class FitnessApp {
    public static void main(String[] args) {

        // 1. Create user
        User user = new User("rex");

        // 2. Create workout session
        WorkoutSession session = new WorkoutSession(LocalDate.now());
        ExerciseEntry e = new ExerciseEntry("Bench Press", 3, 10, 95, "Chest");
        session.addExercise(e);

        // 3. Add workout to user
        user.addWorkoutSession(session);

        // 4. Add bodyweight entry
        BodyWeightEntry bw = new BodyWeightEntry(LocalDate.now(), 178.5);
        user.addBodyweightEntry(bw);

        // 5. Print user info
        System.out.println(user);

        // 6. Print workouts
        System.out.println("Workout History:");
        for (WorkoutSession s : user.getHistory()) {
            System.out.println(s);
        }

        // 7. Print bodyweight history
        System.out.println("Bodyweight History:");
        for (BodyWeightEntry b : user.getBodyweights()) {
            System.out.println(b);
        }
    }
}