public class AddWorkoutAction extends Action {
    private WorkoutSession session;

    public AddWorkoutAction(User user, WorkoutSession session) {
        super(user, "Add workout session on " + session.getDate());
        this.session = session;
    }

    @Override
    public void undo() {
        user.getHistory().remove(session);
        // revert the exercise stats entered?
        for (ExerciseEntry exercise : session.getExercises()) {
            String exerciseName = exercise.getName();
            ExerciseStats stats = user.getExerciseStats().get(exerciseName);
            if (stats != null) {
                // remove from volume
                stats.addVolume(-exercise.getVolume());
            }
        }
        System.out.println("Undone: Removed workout session from " + session.getDate());
    }

    @Override
    public void redo() { // reads from history
        user.getHistory().add(session);
        user.updateExerciseStats(session);
        System.out.println("Redone: Added workout session from " + session.getDate());
    }
}
