import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public class WorkoutSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private ArrayList<ExerciseEntry> exercises;
    private HashSet<String> tags; 

    public WorkoutSession(LocalDate date) {
        this.date = date;
        this.exercises = new ArrayList<>();
        this.tags = new HashSet<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public ArrayList<ExerciseEntry> getExercises() {
        return exercises;
    }

    public void addExercise(ExerciseEntry entry) {
        exercises.add(entry);
    }

    public void addTag(String tag) {
        tags.add(tag.toLowerCase());
    }

    public HashSet<String> getTags() {
        return tags;
    }

    public double getTotalVolume() {
        double total = 0;
        for (ExerciseEntry e : exercises) {
            total += e.getVolume();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ").append(date).append("\n");
        sb.append("Tags: ").append(tags).append("\n");
        sb.append("Exercises:\n");

        for (ExerciseEntry e : exercises) {
            sb.append("  ").append(e).append("\n");
        }

        sb.append("Total Volume: ").append(getTotalVolume());
        return sb.toString();
    }
}