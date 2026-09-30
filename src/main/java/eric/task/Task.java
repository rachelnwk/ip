package eric.task;

/**
 * Base class for tasks tracked by Eric. Every task has a description and a
 * done/not-done status. Concrete task types (Todo, Deadline, Event) supply
 * their own type tag and extend the displayed text with their extra details.
 */
public abstract class Task {
    /** Separates the columns of a task in the save file. */
    protected static final String FILE_SEPARATOR = " | ";

    protected String description;
    protected boolean isDone;

    /**
     * Creates a task that is not yet done.
     *
     * @param description What the task is about.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns "X" if the task is done, or a space otherwise. */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Marks this task as done. */
    public void markDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markUndone() {
        isDone = false;
    }

    /** Returns the one-letter tag identifying the task type, e.g. "T", "D", "E". */
    public abstract String getTypeIcon();

    /** Returns this task as one line of the save file, e.g. "T | 1 | read book". */
    public String toFileString() {
        return getTypeIcon() + FILE_SEPARATOR + (isDone ? "1" : "0") + FILE_SEPARATOR + description;
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
