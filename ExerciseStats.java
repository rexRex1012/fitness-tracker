import java.time.LocalDate; // for time 
import java.util.ArrayList;

public class ExerciseStats {
    private String exerciseName;
    private double personalRecord; // the user BEST record
    private ArrayList<PREntry> prTimeline; // PR 
    private double totalVolume; // cummaltiove volume

    public ExerciseStats(String exerciseName) {
        this.exerciseName = exerciseName;
        this.personalRecord = 0.0;
        this.prTimeline = new ArrayList<>();
        this.totalVolume = 0.0;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public double getPersonalRecord() {
        return personalRecord;
    }

    public ArrayList<PREntry> getPRTimeline() {
        return prTimeline;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public void addVolume(double volume) {
        this.totalVolume += volume;
    }

    // update PR if new weight is higher, they hit a new record
    public boolean updatePR(double weight, LocalDate date) {
        if (weight > personalRecord) {
            personalRecord = weight;
            prTimeline.add(new PREntry(weight, date));
            return true; // return true so we know we hit a new
        }
        return false; // if false then we did not hit a new PR
    }

    @Override
    public String toString() {
        return exerciseName + " - PR: " + personalRecord + " lbs, Total Volume: " + totalVolume + " lbs";
    }

    public static class PREntry {
        private double weight;
        private LocalDate date;

        public PREntry(double weight, LocalDate date) {
            this.weight = weight;
            this.date = date;
        }

        public double getWeight() {
            return weight;
        }

        public LocalDate getDate() {
            return date;
        }

        @Override
        public String toString() {
            return date + ": " + weight + " lbs";
        }
    }
}
