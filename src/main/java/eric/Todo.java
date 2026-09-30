package eric;

/**
 * A task with no date attached, e.g. "todo read book".
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description What the task is about.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }
}
