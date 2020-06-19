package amberdb.cli;

import static java.lang.System.err;

import java.util.Arrays;

/**
 * AmberDb command-line interface
 */
public class Main {
    static void printError(Throwable e) {
        if (System.getProperty("amberdb.trace") != null) {
            e.printStackTrace();
        } else {
            err.println("amberdb: " + e.getLocalizedMessage());
            Throwable cause = e.getCause();
            while (cause != null) {
                err.println("caused by " + cause);
                cause = cause.getCause();
            }
        }
    }

    public static void main(String... arguments) throws Exception {
        try {
            Arguments args = new Arguments(Arrays.asList(arguments));
            if (args.isEmpty()) {
                Command.get("help").execute(args);
            } else {
                Command.get(args.first()).execute(args.rest());
            }
        } catch (CommandLineException e) {
            printError(e);
        }
    }
}
