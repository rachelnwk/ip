package eric.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains the list of tasks, in the order in which they were added, and the operations on it.
 * Positions in the list are zero-based indexes: the first task has index 0.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list that holds the given tasks, in the same order.
     *
     * @param tasks Tasks to start with. The list is copied, so later changes to it do not affect
     *         this task list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns true if the list has no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns true if {@code index} is the index of a task in the list. */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns the task at {@code index}.
     *
     * @param index Zero-based index of the task, which must be valid.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Adds {@code task} to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at {@code index}. The tasks after it move up by one place.
     *
     * @param index Zero-based index of the task, which must be valid.
     * @return The task that was removed.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns a read-only view of the tasks, e.g. for showing or saving them. */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
