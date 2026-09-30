package eric;

import java.util.Scanner;

/**
 * Entry point for Eric, a command-line chatbot that manages a simple task list.
 * Reads commands from standard input in a loop until the user types "bye".
 */
public class Eric {
    private static final String DIVIDER = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    private static final String PREFIX_MARK = "mark ";
    private static final String PREFIX_UNMARK = "unmark ";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";

    private static final String MARKER_BY = "/by ";
    private static final String MARKER_FROM = "/from ";
    private static final String MARKER_TO = "/to ";

    /**
     * Runs the chatbot until the user types "bye" or input ends.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        printBanner();

        Scanner in = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        String input = in.hasNextLine() ? in.nextLine() : "bye";

        while (!input.equals("bye")) {
            if (input.equals("list")) {
                printTaskList(tasks, taskCount);
            } else if (input.startsWith(PREFIX_MARK)) {
                markTaskByInput(tasks, taskCount, input.substring(PREFIX_MARK.length()), true);
            } else if (input.startsWith(PREFIX_UNMARK)) {
                markTaskByInput(tasks, taskCount, input.substring(PREFIX_UNMARK.length()), false);
            } else if (isCommand(input, COMMAND_TODO)) {
                taskCount = addTask(tasks, taskCount, parseTodo(input));
            } else if (isCommand(input, COMMAND_DEADLINE)) {
                taskCount = addTask(tasks, taskCount, parseDeadline(input));
            } else if (isCommand(input, COMMAND_EVENT)) {
                taskCount = addTask(tasks, taskCount, parseEvent(input));
            } else {
                printWithDivider(" OOPS!!! I'm sorry, but I don't know what that means :-(");
            }

            // Treat end of input (e.g. piped input without "bye") like "bye" instead of crashing
            input = in.hasNextLine() ? in.nextLine() : "bye";
        }

        in.close();
        printWithDivider(" Bye. Hope to see you again soon!");
    }

    /** Returns true if {@code input} is exactly {@code command} or starts with it followed by a space. */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
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

    /** Prints every task added so far, numbered from 1. */
    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
        System.out.println(DIVIDER);
    }

    /**
     * Adds {@code task} to {@code tasks} and prints the confirmation message,
     * unless {@code task} is null (a parse error already reported its own
     * OOPS message), in which case the list is left unchanged.
     */
    private static int addTask(Task[] tasks, int taskCount, Task task) {
        if (task == null) {
            return taskCount;
        }
        tasks[taskCount] = task;
        taskCount++;
        printWithDivider(" Got it. I've added this task:\n   " + task
                + "\n Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }

    /** Parses "todo DESCRIPTION"; returns null (after printing an error) if the description is empty. */
    private static Task parseTodo(String input) {
        String description = input.substring(COMMAND_TODO.length()).trim();
        if (description.isEmpty()) {
            printWithDivider(" OOPS!!! The description of a todo cannot be empty.");
            return null;
        }
        return new Todo(description);
    }

    /** Parses "deadline DESCRIPTION /by DATE"; returns null (after printing an error) if malformed. */
    private static Task parseDeadline(String input) {
        String args = input.substring(COMMAND_DEADLINE.length()).trim();
        int byIndex = args.indexOf(MARKER_BY);
        if (byIndex == -1) {
            printWithDivider(" OOPS!!! A deadline needs a description and a /by date, "
                    + "e.g. deadline return book /by Sunday");
            return null;
        }

        String description = args.substring(0, byIndex).trim();
        String by = args.substring(byIndex + MARKER_BY.length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            printWithDivider(" OOPS!!! A deadline needs a description and a /by date, "
                    + "e.g. deadline return book /by Sunday");
            return null;
        }
        return new Deadline(description, by);
    }

    /** Parses "event DESCRIPTION /from START /to END"; returns null (after printing an error) if invalid. */
    private static Task parseEvent(String input) {
        String args = input.substring(COMMAND_EVENT.length()).trim();
        int fromIndex = args.indexOf(MARKER_FROM);
        int toIndex = args.indexOf(MARKER_TO);
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            printWithDivider(" OOPS!!! An event needs a description, a /from time, and a /to time, "
                    + "e.g. event project meeting /from Mon 2pm /to 4pm");
            return null;
        }

        String description = args.substring(0, fromIndex).trim();
        String from = args.substring(fromIndex + MARKER_FROM.length(), toIndex).trim();
        String to = args.substring(toIndex + MARKER_TO.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            printWithDivider(" OOPS!!! An event needs a description, a /from time, and a /to time, "
                    + "e.g. event project meeting /from Mon 2pm /to 4pm");
            return null;
        }
        return new Event(description, from, to);
    }

    /** Marks or unmarks the task whose 1-based number is in {@code numberText}, reporting invalid input. */
    private static void markTaskByInput(Task[] tasks, int taskCount, String numberText, boolean done) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText.trim());
        } catch (NumberFormatException exception) {
            printWithDivider(" OOPS!!! That doesn't look like a valid task number.");
            return;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            printWithDivider(" OOPS!!! I couldn't find task " + taskNumber + ".");
            return;
        }

        setTaskDone(tasks[taskNumber - 1], done);
    }

    /** Updates {@code task}'s done status and prints the matching confirmation. */
    private static void setTaskDone(Task task, boolean done) {
        if (done) {
            task.markDone();
            printWithDivider(" Nice! I've marked this task as done:\n   " + task);
        } else {
            task.markUndone();
            printWithDivider(" OK, I've marked this task as not done yet:\n   " + task);
        }
    }

    /** Prints {@code message} between two divider lines. */
    private static void printWithDivider(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }
}
