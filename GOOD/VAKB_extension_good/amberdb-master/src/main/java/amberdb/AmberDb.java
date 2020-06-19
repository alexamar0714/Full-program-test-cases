package amberdb;

import amberdb.sql.ListLu;
import amberdb.sql.Lookups;
import amberdb.sql.LookupsSchema;
import doss.BlobStore;
import doss.CorruptBlobStoreException;
import doss.DOSS;
import doss.local.LocalBlobStore;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AmberDb {
    final private DataSource dataSource;
    final private String rootPath;
    
    public AmberDb(DataSource dataSource, String rootPath) {
        this.dataSource = dataSource;
        this.rootPath = rootPath;
        initLookupData(dataSource);
    }

    public AmberSession begin() {        
        return new AmberSession(dataSource, openBlobStore(rootPath), null);
    }
    
    public AmberSession resume(long sessionId) {
        AmberSession as = new AmberSession(dataSource, openBlobStore(rootPath), sessionId);
        return as;
    }

    public void close() {
    }

    private void initLookupData(DataSource dataSource) {
        // NLA specific lookup table config
        DBI lookupsDbi = new DBI(dataSource);
        LookupsSchema luSchema = lookupsDbi.onDemand(LookupsSchema.class);
        Lookups lookups = lookupsDbi.onDemand(Lookups.class);
        if (!luSchema.schemaTablesExist()) {
            luSchema.createLookupsSchema();
            List<ListLu> list = lookups.findActiveLookups();
            luSchema.setupToolsAssociations(list);
        }
        if(!luSchema.carrierAlgorithmTableExist()){
            luSchema.createCarrierAlgorithmTable();
        }
        lookups.migrate();
        lookupsDbi.close(luSchema);
        lookupsDbi.close(lookups);
        lookupsDbi = null;
    }

    static BlobStore openBlobStore(String root) {
        BlobStore blobStore;
        try {
            if (root.startsWith("file:") || root.startsWith("doss:")) {
                blobStore = DOSS.open(root);
            } else {
                blobStore = LocalBlobStore.open(Paths.get(root));
            }
        } catch (CorruptBlobStoreException | IOException e) {
            Path rootPath = Paths.get(root);
            try {
                LocalBlobStore.init(rootPath);
            } catch (IOException e2) {
                throw new RuntimeException("Unable to initialize blobstore: " + e2.getMessage(), e2);
            }
            blobStore = LocalBlobStore.open(rootPath);
        }
        return blobStore;
    }
}
