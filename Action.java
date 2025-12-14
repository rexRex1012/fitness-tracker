public abstract class Action {
    protected User user;
    protected String description;

    public Action(User user, String description) {
        this.user = user;
        this.description = description;
    }

    public abstract void undo();
    public abstract void redo();

    public String getDescription() {
        return description;
    }
}
