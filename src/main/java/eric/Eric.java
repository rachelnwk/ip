package eric;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import eric.storage.Storage;
import eric.storage.StorageException;
import eric.task.Deadline;
import eric.task.Event;
import eric.task.Task;
import eric.task.Todo;

/**
 * Entry point for Eric, a command-line chatbot that manages a simple task list.
 * Reads commands from standard input in a loop until the user types "bye".
 */
public class Eric {
    private static final String DIVIDER = "____________________________________________________________";

    /** Where tasks are saved, relative to the folder Eric is run from. Path.of keeps it OS-independent. */
    private static final Path DATA_FILE_PATH = Path.of("data", "duke.txt");
    private static final Storage STORAGE = new Storage(DATA_FILE_PATH);

    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_BYE = "bye";

    private static final int NO_INDEX = -1;

    private static final String MARKER_BY = "/by";
    private static final String MARKER_FROM = "/from";
    private static final String MARKER_TO = "/to";

    private static final String MESSAGE_COMMAND_LIST =
            "Available commands: todo, deadline, event, list, mark, unmark, delete, bye.";
    private static final String EXAMPLE_TODO = "todo read book";
    private static final String EXAMPLE_DEADLINE = "deadline return book /by Sunday";
    private static final String EXAMPLE_EVENT = "event project meeting /from Mon 2pm /to 4pm";

    /**
     * Runs the chatbot until the user types "bye" or input ends.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        printBanner();

        Scanner in = new Scanner(System.in);
        List<Task> tasks = loadTasks();

        String input = readInput(in);

        while (!input.equals(COMMAND_BYE)) {
            handleCommand(input, tasks);
            input = readInput(in);
        }

        in.close();
        printWithDivider(" Bye. Hope to see you again soon!");
    }

    /**
     * Runs the command in {@code input}, updating {@code tasks} if the command changes it.
     * Invalid commands are reported to the user and leave the task list unchanged.
     *
     * @param input Line typed by the user, without surrounding spaces.
     * @param tasks Task list.
     */
    private static void handleCommand(String input, List<Task> tasks) {
        if (input.isEmpty()) {
            printError("You didn't type a command.", MESSAGE_COMMAND_LIST);
        } else if (input.equals(COMMAND_LIST)) {
            printTaskList(tasks);
        } else if (isCommand(input, COMMAND_MARK)) {
            markTaskByInput(tasks, input.substring(COMMAND_MARK.length()), true);
        } else if (isCommand(input, COMMAND_UNMARK)) {
            markTaskByInput(tasks, input.substring(COMMAND_UNMARK.length()), false);
        } else if (isCommand(input, COMMAND_DELETE)) {
            deleteTaskByInput(tasks, input.substring(COMMAND_DELETE.length()));
        } else if (isCommand(input, COMMAND_TODO)) {
            addTask(tasks, parseTodo(input));
        } else if (isCommand(input, COMMAND_DEADLINE)) {
            addTask(tasks, parseDeadline(input));
        } else if (isCommand(input, COMMAND_EVENT)) {
            addTask(tasks, parseEvent(input));
        } else {
            printError("I don't know the command \"" + input + "\".", MESSAGE_COMMAND_LIST);
        }
    }

    /**
     * Returns the next line of input without surrounding spaces, or "bye" if the input has
     * ended (e.g. piped input without "bye"), so that the program exits normally instead of
     * crashing.
     */
    private static String readInput(Scanner in) {
        return in.hasNextLine() ? in.nextLine().trim() : COMMAND_BYE;
    }

    /** Returns true if {@code input} is exactly {@code command} or starts with it followed by a space. */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Returns the index of {@code marker} (e.g. "/by") in {@code text}, or -1 if absent.
     * The marker must be a whole word, so "/tomorrow" is not mistaken for "/to".
     */
    private static int findMarker(String text, String marker) {
        int index = text.indexOf(marker);
        while (index != -1) {
            int end = index + marker.length();
            boolean isStartOfWord = index == 0 || text.charAt(index - 1) == ' ';
            boolean isEndOfWord = end == text.length() || text.charAt(end) == ' ';
            if (isStartOfWord && isEndOfWord) {
                return index;
            }
            index = text.indexOf(marker, index + 1);
        }
        return -1;
    }

    /** Prints the startup banner and greeting. */
    private static void printBanner() {
        System.out.println(DIVIDER);
        String banner = "███████╗██████╗ ██╗ ██████╗\n"
                + "██╔════╝██╔══██╗██║██╔════╝\n"
                + "█████╗  ██████╔╝██║██║     \n"
                + "██╔══╝  ██╔══██╗██║██║     \n"
                + "███████╗██║  ██║██║╚██████╗\n"
                + "╚══════╝╚═╝  ╚═╝╚═╝ ╚═════╝\n";
        System.out.println(banner);
        System.out.println("Hello! I'm Eric.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
    }

    /** Prints every task in the list, numbered from 1. */
    private static void printTaskList(List<Task> tasks) {
        System.out.println(DIVIDER);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        System.out.println(DIVIDER);
    }

    /**
     * Adds {@code task} to {@code tasks} and prints the confirmation message,
     * unless {@code task} is null (a parse error already reported its own
     * OOPS message), in which case the list is left unchanged.
     */
    private static void addTask(List<Task> tasks, Task task) {
        if (task == null) {
            return;
        }
        tasks.add(task);
        printWithDivider(" Got it. I've added this task:\n   " + task
                + "\n Now you have " + tasks.size() + " tasks in the list.");
        saveTasks(tasks);
    }

    /** Parses "todo DESCRIPTION"; returns null (after printing an error) if the description is empty. */
    private static Task parseTodo(String input) {
        String description = input.substring(COMMAND_TODO.length()).trim();
        if (description.isEmpty()) {
            printError("The description of a todo is empty.",
                    "Type a description after \"todo\", e.g. " + EXAMPLE_TODO);
            return null;
        }
        if (hasNoFileSeparator(description)) {
            return new Todo(description);
        }
        return null;
    }

    /** Parses "deadline DESCRIPTION /by DATE"; returns null (after printing an error) if invalid. */
    private static Task parseDeadline(String input) {
        String arguments = input.substring(COMMAND_DEADLINE.length()).trim();
        int byIndex = findMarker(arguments, MARKER_BY);
        if (byIndex == -1) {
            printError("A deadline needs a /by date, but I couldn't find one.",
                    "Use the format: deadline DESCRIPTION /by DATE, e.g. " + EXAMPLE_DEADLINE);
            return null;
        }

        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + MARKER_BY.length()).trim();
        if (description.isEmpty()) {
            printError("The description of a deadline is empty.",
                    "Type a description before /by, e.g. " + EXAMPLE_DEADLINE);
            return null;
        }
        if (by.isEmpty()) {
            printError("The date after /by is empty.",
                    "Type when the task is due after /by, e.g. " + EXAMPLE_DEADLINE);
            return null;
        }
        if (hasNoFileSeparator(description, by)) {
            return new Deadline(description, by);
        }
        return null;
    }

    /** Parses "event DESCRIPTION /from START /to END"; returns null (after printing an error) if invalid. */
    private static Task parseEvent(String input) {
        String arguments = input.substring(COMMAND_EVENT.length()).trim();
        int fromIndex = findMarker(arguments, MARKER_FROM);
        int toIndex = findMarker(arguments, MARKER_TO);
        if (!hasValidEventMarkers(fromIndex, toIndex)) {
            return null;
        }

        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + MARKER_FROM.length(), toIndex).trim();
        String to = arguments.substring(toIndex + MARKER_TO.length()).trim();
        if (description.isEmpty()) {
            printError("The description of an event is empty.",
                    "Type a description before /from, e.g. " + EXAMPLE_EVENT);
            return null;
        }
        if (from.isEmpty()) {
            printError("The start time after /from is empty.",
                    "Type when the event starts after /from, e.g. " + EXAMPLE_EVENT);
            return null;
        }
        if (to.isEmpty()) {
            printError("The end time after /to is empty.",
                    "Type when the event ends after /to, e.g. " + EXAMPLE_EVENT);
            return null;
        }
        if (hasNoFileSeparator(description, from, to)) {
            return new Event(description, from, to);
        }
        return null;
    }

    /**
     * Returns true if none of {@code texts} contains the separator of the save file columns. Otherwise
     * prints an error, because such a task could not be read back from the file, and returns false.
     */
    private static boolean hasNoFileSeparator(String... texts) {
        for (String text : texts) {
            if (text.contains(Task.FILE_SEPARATOR)) {
                printError("A task can't contain \"" + Task.FILE_SEPARATOR + "\", because that separates "
                        + "the columns of the save file.",
                        "Remove the \"" + Task.FILE_SEPARATOR
                        + "\" from your command, e.g. use a comma instead.");
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if an event has both /from and /to, in that order. Otherwise prints an
     * error explaining which marker is missing or misplaced, and returns false.
     */
    private static boolean hasValidEventMarkers(int fromIndex, int toIndex) {
        String format = "Use the format: event DESCRIPTION /from START /to END, e.g. " + EXAMPLE_EVENT;
        if (fromIndex == -1 && toIndex == -1) {
            printError("An event needs a /from time and a /to time, but I couldn't find either.", format);
            return false;
        }
        if (fromIndex == -1) {
            printError("An event needs a /from time, but I couldn't find one.", format);
            return false;
        }
        if (toIndex == -1) {
            printError("An event needs a /to time, but I couldn't find one.", format);
            return false;
        }
        if (toIndex < fromIndex) {
            printError("/to comes before /from.", "Put /from first, then /to. " + format);
            return false;
        }
        return true;
    }

    /** Marks or unmarks the task whose 1-based number is in {@code numberText}, reporting invalid input. */
    private static void markTaskByInput(List<Task> tasks, String numberText, boolean isDone) {
        String command = isDone ? COMMAND_MARK : COMMAND_UNMARK;
        int index = findTaskIndex(tasks, numberText, command);
        if (index == NO_INDEX) {
            return;
        }
        setTaskDone(tasks.get(index), isDone);
        saveTasks(tasks);
    }

    /** Deletes the task whose 1-based number is in {@code numberText}, reporting invalid input. */
    private static void deleteTaskByInput(List<Task> tasks, String numberText) {
        int index = findTaskIndex(tasks, numberText, COMMAND_DELETE);
        if (index == NO_INDEX) {
            return;
        }
        Task removedTask = tasks.remove(index);
        printWithDivider(" Noted. I've removed this task:\n   " + removedTask
                + "\n Now you have " + tasks.size() + " tasks in the list.");
        saveTasks(tasks);
    }

    /**
     * Converts the 1-based task number in {@code numberText} into an index of {@code tasks}.
     * If the number is missing, not a plain integer or out of range, prints an error naming
     * {@code command} and returns {@link #NO_INDEX}.
     */
    private static int findTaskIndex(List<Task> tasks, String numberText, String command) {
        String trimmedText = numberText.trim();
        if (trimmedText.isEmpty()) {
            printError("The task number is missing.",
                    "Type the number of a task after \"" + command + "\", e.g. " + command + " 2");
            return NO_INDEX;
        }

        if (!isPlainInteger(trimmedText)) {
            printError("\"" + trimmedText + "\" is not a valid task number.",
                    "Use a plain whole number (no + sign or leading zeros), e.g. " + command
                    + " 2. Type list to see the task numbers.");
            return NO_INDEX;
        }
        int taskNumber = Integer.parseInt(trimmedText);

        if (tasks.isEmpty()) {
            printError("There are no tasks to " + command + " yet.",
                    "Add a task first, e.g. " + EXAMPLE_TODO);
            return NO_INDEX;
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            printError("Task " + taskNumber + " doesn't exist.",
                    "Choose a number from 1 to " + tasks.size() + ". Type list to see the tasks.");
            return NO_INDEX;
        }

        return taskNumber - 1;
    }

    /**
     * Returns true if {@code text} is an integer written in its plain form, e.g. "2" or "-1",
     * but not "+2", "02" or "-0", and small enough to fit in an int.
     */
    private static boolean isPlainInteger(String text) {
        try {
            return String.valueOf(Integer.parseInt(text)).equals(text);
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /** Updates {@code task}'s done status and prints the matching confirmation. */
    private static void setTaskDone(Task task, boolean isDone) {
        if (isDone) {
            task.markDone();
            printWithDivider(" Nice! I've marked this task as done:\n   " + task);
        } else {
            task.markUndone();
            printWithDivider(" OK, I've marked this task as not done yet:\n   " + task);
        }
    }

    /**
     * Returns the tasks saved in the data file, or an empty list if nothing has been saved yet.
     * If the file is unusable, it is rejected as a whole: the problem is reported to the user and
     * Eric starts with an empty list instead of failing.
     */
    private static List<Task> loadTasks() {
        try {
            return STORAGE.load();
        } catch (StorageException exception) {
            reportRejectedDataFile("because " + exception.getMessage());
            return new ArrayList<>();
        }
    }

    /** Tells the user that the data file was not loaded, why, and how to keep it. */
    private static void reportRejectedDataFile(String reason) {
        printError("I couldn't load your saved tasks from " + STORAGE.getFilePath() + " " + reason,
                "Starting with an empty list. The file will be replaced the next time your tasks change."
                + " To keep it, close Eric, then fix or move the file.");
    }

    /** Saves {@code tasks} to the data file, and reports to the user if that fails. */
    private static void saveTasks(List<Task> tasks) {
        try {
            STORAGE.save(tasks);
        } catch (IOException exception) {
            printError("I couldn't save your tasks to " + STORAGE.getFilePath() + ".",
                    "Check that the folder can be written to. Reason: " + exception.getMessage());
        }
    }

    /**
     * Prints an error between divider lines, stating what went wrong and how to correct it.
     *
     * @param problem Why the command could not be carried out.
     * @param fix What the user should do or type instead.
     */
    private static void printError(String problem, String fix) {
        printWithDivider(" OOPS!!! " + problem + "\n " + fix);
    }

    /** Prints {@code message} between two divider lines. */
    private static void printWithDivider(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }
}
