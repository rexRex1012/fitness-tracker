public class ExerciseEntry {
    private String name;
    private int sets;
    private int reps;
    private double weight;
    private String muscleGroup;

    public ExerciseEntry(String name, int sets, int reps, double weight, String muscleGroup) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.muscleGroup = muscleGroup;
    }

    public String getName() { return name; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public double getWeight() { return weight; }
    public String getMuscleGroup() { return muscleGroup; }

    public double getVolume() {
        return sets * reps * weight;
    }

    @Override
    public String toString() {
        return name + " | " + sets + "x" + reps + " @ " + weight + " lbs | " + muscleGroup;
    }
}