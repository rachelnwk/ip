import java.util.Scanner;

/**
 * Entry point for Eric, a command-line chatbot that manages a simple task list.
 * Reads commands from standard input in a loop until the user types "bye".
 */
public class Eric {
    private static final String DIVIDER = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        printBanner();

        Scanner in = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        String input = in.nextLine();

        while (!input.equals("bye")) {
            if (input.equals("list")) {
                printTaskList(tasks, taskCount);
            } else if (input.startsWith("mark ")) {
                markTaskByInput(tasks, taskCount, input.substring(5), true);
            } else if (input.startsWith("unmark ")) {
                markTaskByInput(tasks, taskCount, input.substring(7), false);
            } else if (input.equals("todo") || input.startsWith("todo ")) {
                taskCount = addTask(tasks, taskCount, parseTodo(input));
            } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                taskCount = addTask(tasks, taskCount, parseDeadline(input));
            } else if (input.equals("event") || input.startsWith("event ")) {
                taskCount = addTask(tasks, taskCount, parseEvent(input));
            } else {
                printWithDivider(" OOPS!!! I'm sorry, but I don't know what that means :-(");
            }

            input = in.nextLine();
        }

        in.close();
        printWithDivider("Bye. Hope to see you again soon!");
    }

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

    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println(DIVIDER);
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

    private static Task parseTodo(String input) {
        String description = input.length() > 4 ? input.substring(4).trim() : "";
        if (description.isEmpty()) {
            printWithDivider(" OOPS!!! The description of a todo cannot be empty.");
            return null;
        }
        return new Todo(description);
    }

    private static Task parseDeadline(String input) {
        String args = input.length() > 8 ? input.substring(8).trim() : "";
        int byIndex = args.indexOf("/by ");
        if (byIndex == -1) {
            printWithDivider(" OOPS!!! A deadline needs a description and a /by date, "
                    + "e.g. deadline return book /by Sunday");
            return null;
        }

        String description = args.substring(0, byIndex).trim();
        String by = args.substring(byIndex + 4).trim();
        if (description.isEmpty() || by.isEmpty()) {
            printWithDivider(" OOPS!!! A deadline needs a description and a /by date, "
                    + "e.g. deadline return book /by Sunday");
            return null;
        }
        return new Deadline(description, by);
    }

    private static Task parseEvent(String input) {
        String args = input.length() > 5 ? input.substring(5).trim() : "";
        int fromIndex = args.indexOf("/from ");
        int toIndex = args.indexOf("/to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            printWithDivider(" OOPS!!! An event needs a description, a /from time, and a /to time, "
                    + "e.g. event project meeting /from Mon 2pm /to 4pm");
            return null;
        }

        String description = args.substring(0, fromIndex).trim();
        String from = args.substring(fromIndex + 6, toIndex).trim();
        String to = args.substring(toIndex + 4).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            printWithDivider(" OOPS!!! An event needs a description, a /from time, and a /to time, "
                    + "e.g. event project meeting /from Mon 2pm /to 4pm");
            return null;
        }
        return new Event(description, from, to);
    }

    private static void markTaskByInput(Task[] tasks, int taskCount, String numberText, boolean done) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText.trim());
        } catch (NumberFormatException e) {
            printWithDivider(" OOPS!!! That doesn't look like a valid task number.");
            return;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            printWithDivider(" OOPS!!! I couldn't find task " + taskNumber + ".");
            return;
        }

        setTaskDone(tasks[taskNumber - 1], done);
    }

    private static void setTaskDone(Task task, boolean done) {
        if (done) {
            task.markDone();
            printWithDivider(" Nice! I've marked this task as done:\n   " + task);
        } else {
            task.markUndone();
            printWithDivider(" OK, I've marked this task as not done yet:\n   " + task);
        }
    }

    private static void printWithDivider(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }
}
