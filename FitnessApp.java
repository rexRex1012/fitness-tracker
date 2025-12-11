 import java.time.LocalDate;

public class FitnessApp {
    public static void main(String[] args) {

        WorkoutSession session = new WorkoutSession(LocalDate.now());
        ExerciseEntry e = new ExerciseEntry("Bench Press", 3, 10, 95, "Chest");

        session.addExercise(e);

        System.out.println(session);
    }
}