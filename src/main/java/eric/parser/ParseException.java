package eric.parser;

/**
 * Signals that a command typed by the user could not be understood. It says what is wrong, in
 * the message, and how the user can correct it, so both can be shown to the user.
 */
public class ParseException extends Exception {
    private final String fix;

    /**
     * Creates an exception that explains a problem with a command and how to fix it.
     *
     * @param problem What is wrong with the command.
     * @param fix What the user should do or type instead.
     */
    public ParseException(String problem, String fix) {
        super(problem);
        this.fix = fix;
    }

    /** Returns what the user should do or type instead. */
    public String getFix() {
        return fix;
    }
}
