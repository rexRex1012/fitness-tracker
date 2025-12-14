import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class User {

    private String username;
    private LinkedList<WorkoutSession> history;
    private ArrayList<BodyWeightEntry> bodyweights;
    private HashMap<String, ExerciseStats> exerciseStats; // Track stats per exercise
    private Stack<Action> undoStack;
    private Stack<Action> redoStack;

    public User(String username) {
        this.username = username;
        this.history = new LinkedList<>();
        this.bodyweights = new ArrayList<>();
        this.exerciseStats = new HashMap<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public String getUsername() {
        return username;
    }

    public LinkedList<WorkoutSession> getHistory() {
        return history;
    }

    public void addWorkoutSession(WorkoutSession session) {
        history.add(session);
    }

    public ArrayList<BodyWeightEntry> getBodyweights() {
        return bodyweights;
    }

    public void addBodyweightEntry(BodyWeightEntry entry) {
        bodyweights.add(entry);
    }

    public void logBodyWeight(java.time.LocalDate date, double weight) {
        BodyWeightEntry entry = new BodyWeightEntry(date, weight);
        bodyweights.add(entry);
    }

    // Get the most recent bodyweight for bodyweight exercises
    public double getCurrentBodyweight() {
        if (bodyweights.isEmpty()) {
            return 0.0; // Default if no bodyweight logged
        }
        return bodyweights.get(bodyweights.size() - 1).getWeight();
    }

    public HashMap<String, ExerciseStats> getExerciseStats() {
        return exerciseStats;
    }

    public Stack<Action> getUndoStack() {
        return undoStack;
    }

    public Stack<Action> getRedoStack() { //TEST
        return redoStack;
    }

    public void updateExerciseStats(WorkoutSession session) {
        for (ExerciseEntry exercise : session.getExercises()) {
            String exerciseName = exercise.getName();

            //IF DOESNT EXIST CREATE
            if (!exerciseStats.containsKey(exerciseName)) {
                exerciseStats.put(exerciseName, new ExerciseStats(exerciseName));
            }

            ExerciseStats stats = exerciseStats.get(exerciseName);

            // Update volume
            stats.addVolume(exercise.getVolume());

            // PR CHECK
            if (stats.updatePR(exercise.getWeight(), session.getDate())) {
                System.out.println("new pr: " + exerciseName + ": " + exercise.getWeight() + " lbs!");
            }
        }
    }

    // Calculate muscle group volumes
    public HashMap<String, Double> getMuscleGroupVolumes() {
        HashMap<String, Double> volumes = new HashMap<>();

        for (WorkoutSession session : history) {
            for (ExerciseEntry exercise : session.getExercises()) {
                String muscleGroup = exercise.getMuscleGroup().toLowerCase();
                double volume = exercise.getVolume();
                volumes.put(muscleGroup, volumes.getOrDefault(muscleGroup, 0.0) + volume);
            }
        }

        return volumes;
    }

    public int getWorkoutCount() {
        return history.size();
    }

    // finds ur LEAST workout so the minium, and recommenbds it, the lowest total volume exercise
    public String getRecommendations() {
        HashMap<String, Double> volumes = getMuscleGroupVolumes();

        if (volumes.isEmpty()) {
            return "Start logging workouts to get personalized recommendations!";
        }

        String lowestMuscle = null;
        double lowestVolume = Double.MAX_VALUE;

        for (HashMap.Entry<String, Double> entry : volumes.entrySet()) {
            if (entry.getValue() < lowestVolume) {
                lowestVolume = entry.getValue();
                lowestMuscle = entry.getKey();
            }
        }

        return "💡 Recommendation: Focus more on " + lowestMuscle + " (current volume: " +
               String.format("%.0f", lowestVolume) + " lbs). This muscle group is undertrained compared to others.";
    }

}
