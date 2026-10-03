package eric;

import java.io.IOException;
import java.nio.file.Path;

import eric.parser.ParseException;
import eric.parser.Parser;
import eric.storage.Storage;
import eric.storage.StorageException;
import eric.task.Task;
import eric.task.TaskList;
import eric.ui.Ui;

/**
 * Entry point for Eric, a command-line chatbot that manages a simple task list.
 * Reads commands from standard input in a loop until the user types "bye".
 */
public class Eric {
    /** Where tasks are saved, relative to the folder Eric is run from. Path.of keeps it OS-independent. */
    private static final Path DATA_FILE_PATH = Path.of("data", "duke.txt");
    private static final Storage STORAGE = new Storage(DATA_FILE_PATH);
    private static final Ui UI = new Ui();

    private static final int NO_INDEX = -1;

    /**
     * Runs the chatbot until the user types "bye" or input ends.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        UI.showWelcome();

        TaskList tasks = loadTasks();

        String input = readInput();

        while (!input.equals(Parser.COMMAND_BYE)) {
            handleCommand(input, tasks);
            input = readInput();
        }

        UI.close();
        UI.showGoodbye();
    }

    /**
     * Runs the command in {@code input}, updating {@code tasks} if the command changes it.
     * Invalid commands are reported to the user and leave the task list unchanged.
     *
     * @param input Line typed by the user, without surrounding spaces.
     * @param tasks Task list.
     */
    private static void handleCommand(String input, TaskList tasks) {
        try {
            String command = Parser.parseCommandWord(input);
            String arguments = Parser.getArguments(input, command);
            switch (command) {
            case Parser.COMMAND_LIST -> UI.showTaskList(tasks.getTasks());
            case Parser.COMMAND_MARK -> markTaskByNumber(tasks, arguments, true);
            case Parser.COMMAND_UNMARK -> markTaskByNumber(tasks, arguments, false);
            case Parser.COMMAND_DELETE -> deleteTaskByNumber(tasks, arguments);
            case Parser.COMMAND_TODO -> addTask(tasks, Parser.parseTodo(arguments));
            case Parser.COMMAND_DEADLINE -> addTask(tasks, Parser.parseDeadline(arguments));
            case Parser.COMMAND_EVENT -> addTask(tasks, Parser.parseEvent(arguments));
            default -> throw new IllegalStateException("Unhandled command: " + command);
            }
        } catch (ParseException exception) {
            UI.showError(exception.getMessage(), exception.getFix());
        }
    }

    /**
     * Returns the next command typed by the user, or "bye" if the input has ended (e.g. piped
     * input without "bye"), so that the program exits normally instead of crashing.
     */
    private static String readInput() {
        return UI.hasNextCommand() ? UI.readCommand() : Parser.COMMAND_BYE;
    }

    /**
     * Adds {@code task} to {@code tasks}, tells the user, and saves the tasks.
     */
    private static void addTask(TaskList tasks, Task task) {
        tasks.add(task);
        UI.showTaskAdded(task, tasks.size());
        saveTasks(tasks);
    }

    /**
     * Marks or unmarks the task whose 1-based number is in {@code arguments}, reporting invalid input.
     *
     * @throws ParseException If the task number is missing or is not a plain whole number.
     */
    private static void markTaskByNumber(TaskList tasks, String arguments, boolean isDone)
            throws ParseException {
        String command = isDone ? Parser.COMMAND_MARK : Parser.COMMAND_UNMARK;
        int index = findTaskIndex(tasks, arguments, command);
        if (index == NO_INDEX) {
            return;
        }
        setTaskDone(tasks.get(index), isDone);
        saveTasks(tasks);
    }

    /**
     * Deletes the task whose 1-based number is in {@code arguments}, reporting invalid input.
     *
     * @throws ParseException If the task number is missing or is not a plain whole number.
     */
    private static void deleteTaskByNumber(TaskList tasks, String arguments) throws ParseException {
        int index = findTaskIndex(tasks, arguments, Parser.COMMAND_DELETE);
        if (index == NO_INDEX) {
            return;
        }
        Task removedTask = tasks.remove(index);
        UI.showTaskRemoved(removedTask, tasks.size());
        saveTasks(tasks);
    }

    /**
     * Converts the 1-based task number in {@code arguments} into an index of {@code tasks}.
     * If the number does not refer to a task in the list, prints an error naming {@code command} and
     * returns {@link #NO_INDEX}.
     *
     * @throws ParseException If the task number is missing or is not a plain whole number.
     */
    private static int findTaskIndex(TaskList tasks, String arguments, String command)
            throws ParseException {
        int taskNumber = Parser.parseTaskNumber(arguments, command);

        if (tasks.isEmpty()) {
            UI.showError("There are no tasks to " + command + " yet.",
                    "Add a task first, e.g. " + Parser.EXAMPLE_TODO);
            return NO_INDEX;
        }
        int index = taskNumber - 1;
        if (!tasks.isValidIndex(index)) {
            UI.showError("Task " + taskNumber + " doesn't exist.",
                    "Choose a number from 1 to " + tasks.size() + ". Type list to see the tasks.");
            return NO_INDEX;
        }

        return index;
    }

    /** Updates {@code task}'s done status and prints the matching confirmation. */
    private static void setTaskDone(Task task, boolean isDone) {
        if (isDone) {
            task.markDone();
            UI.showTaskMarked(task);
        } else {
            task.markUndone();
            UI.showTaskUnmarked(task);
        }
    }

    /**
     * Returns the tasks saved in the data file, or an empty task list if nothing has been saved yet.
     * If the file is unusable, it is rejected as a whole: the problem is reported to the user and
     * Eric starts with an empty list instead of failing.
     */
    private static TaskList loadTasks() {
        try {
            return new TaskList(STORAGE.load());
        } catch (StorageException exception) {
            reportRejectedDataFile("because " + exception.getMessage());
            return new TaskList();
        }
    }

    /** Tells the user that the data file was not loaded, why, and how to keep it. */
    private static void reportRejectedDataFile(String reason) {
        UI.showError("I couldn't load your saved tasks from " + STORAGE.getFilePath() + " " + reason,
                "Starting with an empty list. The file will be replaced the next time your tasks change."
                + " To keep it, close Eric, then fix or move the file.");
    }

    /** Saves {@code tasks} to the data file, and reports to the user if that fails. */
    private static void saveTasks(TaskList tasks) {
        try {
            STORAGE.save(tasks.getTasks());
        } catch (IOException exception) {
            UI.showError("I couldn't save your tasks to " + STORAGE.getFilePath() + ".",
                    "Check that the folder can be written to. Reason: " + exception.getMessage());
        }
    }
}
