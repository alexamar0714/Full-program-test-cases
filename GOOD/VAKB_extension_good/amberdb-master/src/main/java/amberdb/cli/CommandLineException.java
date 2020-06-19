package amberdb.cli;

class CommandLineException extends RuntimeException {
    private static final long serialVersionUID = -2055434329764121553L;

    CommandLineException(String message) {
        super(message);
    }
}