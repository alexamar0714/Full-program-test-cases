package amberdb.cli;

import amberdb.AmberDb;
import amberdb.AmberSession;
import com.jolbox.bonecp.BoneCPDataSource;

public class CmdLoadLookups extends Command {

    public CmdLoadLookups() {
        // e.g. load simple (name, value) lookups:
        //             java -cp $CLASSPATH -Damberdb.list.file=$APP_PATH/listSeeds.txt load_lookups $AMBER_URL $AMBER_USER $AMBER_PASS $AMBER_PATH
        //      load tools lookups: 
        //             java -cp $CLASSPATH -Damberdb.lookups.tools.file=$APP_PATH/toolSeeds.txt load_lookups $AMBER_URL $AMBER_USER $AMBER_PASS $AMBER_PATH
        //      load tools mapping to other lookup typs including tool types, tool categories and material types
        //             java -cp $CLASSPATH -Dmaberdb.maps.tools.file=$APP_PATH/toolMaps.txt load_lookups $AMBER_URL $AMBER_USER $AMBER_PASS $AMBER_PATH
        super("load_lookups", "<dbUrl> <dbUser> <dbPassword> <rootPath>", "load initial lookups through the lookups configuration files defined in properties.");
    }

    @Override
    void execute(Arguments args) throws Exception {
        if (args != null && !args.isEmpty()) {
            String dbUrl = args.first();
            String dbUser = args.rest().first();
            String dbPasswd = args.rest().rest().first();
            String rootPath = args.rest().rest().rest().first();
            BoneCPDataSource ds = new BoneCPDataSource();
            ds.setDisableJMX(true);
            ds.setJdbcUrl(dbUrl);
            ds.setUsername(dbUser);
            ds.setPassword(dbPasswd);
            
            try (AmberSession db = new AmberDb(ds, rootPath).begin()) {
                db.getLookups().seedInitialLookups();
            }
        } else {
            try (AmberSession db = new AmberSession()) {
                db.getLookups().seedInitialLookups();
            }
        }
    }
}
