import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private LinkedList<WorkoutSession> history;
    private ArrayList<BodyWeightEntry> bodyweights;

    // Stats per exercise name 
    private HashMap<String, ExerciseStats> exerciseStats;

    public User(String username) {
        this.username = username;
        this.history = new LinkedList<>();
        this.bodyweights = new ArrayList<>();
        this.exerciseStats = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public LinkedList<WorkoutSession> getHistory() {
        return history;
    }

    public void addWorkoutSession(WorkoutSession session) {
        history.add(session);
        rebuildExerciseStats();
    }

    public ArrayList<BodyWeightEntry> getBodyweights() {
        return bodyweights;
    }

    public void addBodyweightEntry(BodyWeightEntry entry) {
        bodyweights.add(entry);
    }

    public HashMap<String, ExerciseStats> getExerciseStats() {
        return exerciseStats;
    }

    public void updateExerciseStats(WorkoutSession session) {
        LocalDate date = session.getDate();

        for (ExerciseEntry e : session.getExercises()) {
            String name = e.getName();
            String key = name.trim().toLowerCase(); // "Bench Press" and "bench press" count as the same exercise

            ExerciseStats stats = exerciseStats.get(key);
            if (stats == null) {
                stats = new ExerciseStats(name);
                exerciseStats.put(key, stats);
            }

            stats.addVolume(e.getVolume());
            stats.updatePR(e.getWeight(), date);
        }
    }

    // Recalculates all volume + PR stats from scratch using the workout history.
    // Used after undo/redo and on load so stats never get out of sync with the history.
    public void rebuildExerciseStats() {
        exerciseStats.clear();
        ArrayList<WorkoutSession> sorted = new ArrayList<>(history);
        sorted.sort((a, b) -> a.getDate().compareTo(b.getDate())); // oldest first so the PR timeline is in order
        for (WorkoutSession s : sorted) {
            updateExerciseStats(s);
        }
    }
}