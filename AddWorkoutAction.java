public class AddWorkoutAction extends Action {
    private WorkoutSession session;

    public AddWorkoutAction(User user, WorkoutSession session) {
        super(user, "Add workout session on " + session.getDate());
        this.session = session;
    }

    @Override
    public void undo() {
        user.getHistory().remove(session);
        user.rebuildExerciseStats(); // removes this session's volume AND any PRs it set
        System.out.println("Undone: Removed workout session from " + session.getDate());
    }

    @Override
    public void redo() {
        user.getHistory().add(session);
        user.rebuildExerciseStats();
        System.out.println("Redone: Added workout session from " + session.getDate());
    }
}
