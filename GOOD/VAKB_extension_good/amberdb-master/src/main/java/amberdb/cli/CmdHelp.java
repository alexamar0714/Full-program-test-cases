package amberdb.cli;

import static java.lang.System.out;

public class CmdHelp extends Command {

    public CmdHelp() {
        super("help", "<command>", "Display a list of commands or help for a given command.");
    }
    
    void execute(Arguments args) {
        if (args.isEmpty()) {
            listCommands();
        } else {
            Command.get(args.first()).usage();
        }
    }

    void listCommands() {
        out.println("usage: amberdb <command> [<args>]");
        out.println("");
        out.println("Available commands:");
        for (Command command : Command.list()) {
            out.println(String.format("  %-10s%s", command.name(),
                    command.description()));
        }
    }
}
