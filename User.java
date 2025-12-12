import java.util.ArrayList;
import java.util.LinkedList;

public class User {
    
private String username;
    
private LinkedList<WorkoutSession> history;

private ArrayList<BodyWeightEntry> bodyweights;

    public User(String username) {
        this.username = username;
        this.history = new LinkedList<>();
        this.bodyweights = new ArrayList<>();
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



}
