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

        String input = in.nextLine();

        while (!input.equals("bye")) {
            System.out.println("____________________________________________________________");
            System.out.println(input);
            System.out.println("____________________________________________________________");
            input = in.nextLine();
        }

        in.close();
        System.out.println("____________________________________________________________");
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("____________________________________________________________");

    }
}
