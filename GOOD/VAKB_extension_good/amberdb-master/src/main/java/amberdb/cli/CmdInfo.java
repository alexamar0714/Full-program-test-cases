package amberdb.cli;

import amberdb.AmberSession;
import amberdb.model.Work;

public class CmdInfo extends Command {

    public CmdInfo() {
        super("info", "<objId ...>", "displays metadata for objects");
    }

    void execute(Arguments args) throws Exception {
        try (AmberSession db = openAmberDb()) {
            for (String id : args) {
                Work work = db.findWork(id);
                info(work);
            }
        }
    }

    private void info(Work work) {
        System.out.println(work.getObjId());
        for (String key : work.asVertex().getPropertyKeys()) {
            System.out.println("  " + key + ": " + work.asVertex().getProperty(key));
        }
    }
}
