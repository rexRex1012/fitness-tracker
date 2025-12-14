public class AddBodyWeightAction extends Action {
    private BodyWeightEntry entry;

    public AddBodyWeightAction(User user, BodyWeightEntry entry) {
        super(user, "Log bodyweight: " + entry.getWeight() + " lbs on " + entry.getDate());
        this.entry = entry;
    }

    @Override
    public void undo() {
        user.getBodyweights().remove(entry);
        System.out.println("Undone: Removed bodyweight entry from " + entry.getDate());
    }

    @Override
    public void redo() {
        user.getBodyweights().add(entry);
        System.out.println("Redone: Added bodyweight entry from " + entry.getDate());
    }
}
