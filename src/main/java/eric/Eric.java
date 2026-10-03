package eric;

import java.io.IOException;
import java.nio.file.Path;

import eric.exception.EricException;
import eric.parser.Parser;
import eric.storage.Storage;
import eric.storage.StorageException;
import eric.task.Task;
import eric.task.TaskList;
import eric.ui.Ui;

/**
 * Eric, a command-line chatbot that manages a simple task list. It reads commands from the user
 * until the user types "bye", and keeps the tasks in a file so that they are there the next time.
 * The work is shared between {@link Ui} (talks to the user), {@link Parser} (understands commands),
 * {@link TaskList} (holds the tasks) and {@link Storage} (saves and loads the tasks).
 */
public class Eric {
    /** Where tasks are saved, relative to the folder Eric is run from. Path.of keeps it OS-independent. */
    private static final Path DATA_FILE_PATH = Path.of("data", "duke.txt");

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;

    /**
     * Creates Eric with an empty task list. The saved tasks are loaded when {@link #run()} starts.
     *
     * @param dataFilePath Location of the file in which the tasks are saved.
     */
    public Eric(Path dataFilePath) {
        ui = new Ui();
        storage = new Storage(dataFilePath);
        tasks = new TaskList();
    }

    /**
     * Starts Eric: greets the user, loads the saved tasks, and carries out the user's commands until
     * the user types "bye" or the input ends.
     */
    public void run() {
        ui.showWelcome();

        tasks = loadTasks();

        String input = readInput();

        while (!input.equals(Parser.COMMAND_BYE)) {
            handleCommand(input);
            input = readInput();
        }

        ui.close();
        ui.showGoodbye();
    }

    /**
     * Runs Eric, saving the tasks in the default data file.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Eric(DATA_FILE_PATH).run();
    }

    /**
     * Runs the command in {@code input}, updating the task list if the command changes it.
     * Invalid commands are reported to the user and leave the task list unchanged.
     *
     * @param input Line typed by the user, without surrounding spaces.
     */
    private void handleCommand(String input) {
        try {
            String command = Parser.parseCommandWord(input);
            String arguments = Parser.getArguments(input, command);
            switch (command) {
            case Parser.COMMAND_LIST -> ui.showTaskList(tasks.getTasks());
            case Parser.COMMAND_MARK -> markTaskByNumber(arguments, true);
            case Parser.COMMAND_UNMARK -> markTaskByNumber(arguments, false);
            case Parser.COMMAND_DELETE -> deleteTaskByNumber(arguments);
            case Parser.COMMAND_TODO -> addTask(Parser.parseTodo(arguments));
            case Parser.COMMAND_DEADLINE -> addTask(Parser.parseDeadline(arguments));
            case Parser.COMMAND_EVENT -> addTask(Parser.parseEvent(arguments));
            default -> throw new IllegalStateException("Unhandled command: " + command);
            }
        } catch (EricException exception) {
            ui.showError(exception.getMessage(), exception.getFix());
        }
    }

    /**
     * Returns the next command typed by the user, or "bye" if the input has ended (e.g. piped
     * input without "bye"), so that the program exits normally instead of crashing.
     */
    private String readInput() {
        return ui.hasNextCommand() ? ui.readCommand() : Parser.COMMAND_BYE;
    }

    /** Adds {@code task} to the task list, tells the user, and saves the tasks. */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        saveTasks();
    }

    /**
     * Marks or unmarks the task whose 1-based number is in {@code arguments}.
     *
     * @throws EricException If the task number is missing, is not a plain whole number, or does not
     *         refer to a task in the list.
     */
    private void markTaskByNumber(String arguments, boolean isDone) throws EricException {
        String command = isDone ? Parser.COMMAND_MARK : Parser.COMMAND_UNMARK;
        int index = findTaskIndex(arguments, command);
        setTaskDone(tasks.get(index), isDone);
        saveTasks();
    }

    /**
     * Deletes the task whose 1-based number is in {@code arguments}.
     *
     * @throws EricException If the task number is missing, is not a plain whole number, or does not
     *         refer to a task in the list.
     */
    private void deleteTaskByNumber(String arguments) throws EricException {
        int index = findTaskIndex(arguments, Parser.COMMAND_DELETE);
        Task removedTask = tasks.remove(index);
        ui.showTaskRemoved(removedTask, tasks.size());
        saveTasks();
    }

    /**
     * Converts the 1-based task number in {@code arguments} into an index of the task list.
     *
     * @throws EricException If the task number is missing, is not a plain whole number, or does not
     *         refer to a task in the list.
     */
    private int findTaskIndex(String arguments, String command) throws EricException {
        int taskNumber = Parser.parseTaskNumber(arguments, command);

        if (tasks.isEmpty()) {
            throw new EricException("There are no tasks to " + command + " yet.",
                    "Add a task first, e.g. " + Parser.EXAMPLE_TODO);
        }
        int index = taskNumber - 1;
        if (!tasks.isValidIndex(index)) {
            throw new EricException("Task " + taskNumber + " doesn't exist.",
                    "Choose a number from 1 to " + tasks.size() + ". Type list to see the tasks.");
        }
        return index;
    }

    /** Updates {@code task}'s done status and prints the matching confirmation. */
    private void setTaskDone(Task task, boolean isDone) {
        if (isDone) {
            task.markDone();
            ui.showTaskMarked(task);
        } else {
            task.markUndone();
            ui.showTaskUnmarked(task);
        }
    }

    /**
     * Returns the tasks saved in the data file, or an empty task list if nothing has been saved yet.
     * If the file is unusable, it is rejected as a whole: the problem is reported to the user and
     * Eric starts with an empty list instead of failing.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (StorageException exception) {
            reportRejectedDataFile("because " + exception.getMessage());
            return new TaskList();
        }
    }

    /** Tells the user that the data file was not loaded, why, and how to keep it. */
    private void reportRejectedDataFile(String reason) {
        ui.showError("I couldn't load your saved tasks from " + storage.getFilePath() + " " + reason,
                "Starting with an empty list. The file will be replaced the next time your tasks change."
                + " To keep it, close Eric, then fix or move the file.");
    }

    /** Saves the task list to the data file, and reports to the user if that fails. */
    private void saveTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException exception) {
            ui.showError("I couldn't save your tasks to " + storage.getFilePath() + ".",
                    "Check that the folder can be written to. Reason: " + exception.getMessage());
        }
    }
}
