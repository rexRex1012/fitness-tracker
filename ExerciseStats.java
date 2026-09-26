import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class ExerciseStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private String exerciseName;
    private double totalVolume;
    private double prWeight;
    private ArrayList<String> prTimeline;

    public ExerciseStats(String exerciseName) {
        this.exerciseName = exerciseName;
        this.totalVolume = 0;
        this.prWeight = 0;
        this.prTimeline = new ArrayList<>();
    }

    public void addVolume(double volume) {
        totalVolume += volume;
    }

    public void updatePR(double weight, LocalDate date) {
        if (weight > prWeight) {
            prWeight = weight;
            prTimeline.add(date + " → " + weight + " lbs");
        }
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public double getPrWeight() {
        return prWeight;
    }

    public ArrayList<String> getPrTimeline() {
        return prTimeline;
    }

    @Override
    public String toString() {
        return exerciseName +
               " | PR: " + prWeight + " lbs" +
               " | Total Volume: " + totalVolume;
    }
} 
