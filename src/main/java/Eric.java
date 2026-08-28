import java.util.Scanner;

public class Eric {
    public static void main(String[] args) {
        System.out.println("____________________________________________________________");
        String banner = "███████╗██████╗ ██╗ ██████╗\n"
                      + "██╔════╝██╔══██╗██║██╔════╝\n"
                      + "█████╗  ██████╔╝██║██║     \n"
                      + "██╔══╝  ██╔══██╗██║██║     \n"
                      + "███████╗██║  ██║██║╚██████╗\n"
                      + "╚══════╝╚═╝  ╚═╝╚═╝ ╚═════╝\n";
        System.out.println(banner);

        System.out.println("Hello! I'm Eric.");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner in = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;

        String input = in.nextLine();

        while (!input.equals("bye")) {
            if (input.equals("list")) {
                System.out.println("____________________________________________________________");

                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i].description);
                }

                System.out.println("____________________________________________________________");
            }
            else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5));

                tasks[taskNumber - 1].markDone();

                System.out.println("____________________________________________________________");
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   [X] " + tasks[taskNumber - 1].description);
                System.out.println("____________________________________________________________");

            }
            else if (input.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(input.substring(7));

                tasks[taskNumber - 1].markUndone();

                System.out.println("____________________________________________________________");
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   [ ] " + tasks[taskNumber - 1].description);
                System.out.println("____________________________________________________________");

            }
            else {
            tasks[taskCount] = new Task(input);;
            taskCount++;

            System.out.println("____________________________________________________________");
            System.out.println(" added: " + input);
            System.out.println("____________________________________________________________");
            }

            input = in.nextLine();
        }

        in.close();
        System.out.println("____________________________________________________________");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");

    }
}
