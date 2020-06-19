package amberdb.cli;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.List;

import amberdb.AmberSession;
import amberdb.model.Work;

abstract class Command {

    final static Command COMMANDS[] = {
            new CmdEdit(),
            new CmdHelp(),
            new CmdInfo(),
            new CmdLoadLookups()
    };

    final String name, descrption, parameters;

    Command(String name, String parameters, String description) {
        this.name = name;
        this.parameters = parameters;
        this.descrption = description;
    }

    abstract void execute(Arguments args) throws Exception;

    protected AmberSession openAmberDb() {
        AmberSession db = new AmberSession();
        Work work = db.addWork();
        work.setTitle("The lost work");
        work.setCreator("Anonymous");
        return db;
    }

    public String name() {
        return name;
    }

    public String description() {
        return descrption;
    }

    public String parameters() {
        return parameters;
    }

    void usage() {
        out.println("usage: amerdb " + name() + " " + parameters());
        out.println(description());
    }

    static Command get(String name) {
        for (Command cmd : COMMANDS) {
            if (cmd.name().equals(name)) {
                return cmd;
            }
        }
        throw new CommandNotFoundException(name);
    }

    static List<Command> list() {
        return Arrays.asList(COMMANDS);
    }
}