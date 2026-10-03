package eric;

import java.nio.file.Path;

import eric.command.AddCommand;
import eric.command.Command;
import eric.command.DeleteCommand;
import eric.command.ListCommand;
import eric.command.MarkCommand;
import eric.exception.EricException;
import eric.parser.Parser;
import eric.storage.Storage;
import eric.storage.StorageException;
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
            Command command = createCommand(input);
            command.execute(tasks, ui, storage);
        } catch (EricException exception) {
            ui.showError(exception.getMessage(), exception.getFix());
        }
    }

    /**
     * Returns the command that {@code input} asks for.
     *
     * @param input Line typed by the user, without surrounding spaces.
     * @throws EricException If {@code input} is not a valid command.
     */
    private static Command createCommand(String input) throws EricException {
        String commandWord = Parser.parseCommandWord(input);
        String arguments = Parser.getArguments(input, commandWord);
        return switch (commandWord) {
        case ListCommand.COMMAND_WORD -> new ListCommand();
        case MarkCommand.COMMAND_WORD_MARK ->
                new MarkCommand(Parser.parseTaskNumber(arguments, commandWord), true);
        case MarkCommand.COMMAND_WORD_UNMARK ->
                new MarkCommand(Parser.parseTaskNumber(arguments, commandWord), false);
        case DeleteCommand.COMMAND_WORD ->
                new DeleteCommand(Parser.parseTaskNumber(arguments, commandWord));
        case Parser.COMMAND_TODO -> new AddCommand(Parser.parseTodo(arguments));
        case Parser.COMMAND_DEADLINE -> new AddCommand(Parser.parseDeadline(arguments));
        case Parser.COMMAND_EVENT -> new AddCommand(Parser.parseEvent(arguments));
        default -> throw new IllegalStateException("Unhandled command: " + commandWord);
        };
    }

    /**
     * Returns the next command typed by the user, or "bye" if the input has ended (e.g. piped
     * input without "bye"), so that the program exits normally instead of crashing.
     */
    private String readInput() {
        return ui.hasNextCommand() ? ui.readCommand() : Parser.COMMAND_BYE;
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
}
