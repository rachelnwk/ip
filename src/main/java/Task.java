/**
 * Base class for tasks tracked by Eric. Every task has a description and a
 * done/not-done status. Concrete task types (Todo, Deadline, Event) supply
 * their own type tag and extend the displayed text with their extra details.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markDone() {
        isDone = true;
    }

    public void markUndone() {
        isDone = false;
    }

    /** One-letter tag identifying the task type, e.g. "T", "D", "E". */
    public abstract String getTypeIcon();

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
