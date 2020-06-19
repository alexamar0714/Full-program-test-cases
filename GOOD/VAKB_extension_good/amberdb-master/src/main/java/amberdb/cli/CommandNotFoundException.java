package amberdb.cli;

class CommandNotFoundException extends CommandLineException {
    private static final long serialVersionUID = 5635444576059213405L;

    CommandNotFoundException(String command) {
        super(String.format("'%s' is not an amber command", command));
    }
}