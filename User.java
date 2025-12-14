import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class User {

    private String username;

    private LinkedList<WorkoutSession> history;
    private ArrayList<BodyWeightEntry> bodyweights;

    // Needed for AddWorkoutAction + PR/volume tracking
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
        updateExerciseStats(session);
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

            ExerciseStats stats = exerciseStats.get(name);
            if (stats == null) {
                stats = new ExerciseStats(name);
                exerciseStats.put(name, stats);
            }

           
            stats.addVolume(e.getVolume());

            
            stats.updatePR(e.getWeight(), date);
        }
    }
}