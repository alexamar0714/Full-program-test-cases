package amberdb.util;

import amberdb.AmberDb;
import amberdb.AmberSession;
import amberdb.sql.ListLu;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class LookupRefreshTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void TestRefreshLookupData() throws IOException {

        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:amberdb;DATABASE_TO_UPPER=false", "amberdb", "amberdb");
        
        try (AmberSession db = new AmberDb(ds, folder.getRoot().getAbsolutePath()).begin()) {
            List<ListLu> srcLu = LookupRefresh.synchronizeLookups(db);
            Map<String, ListLu> destMap = LookupRefresh.indexLookups(db.getLookups().findActiveLookups());
            for (ListLu lu : srcLu) {
                String nameCode = lu.getName() + "_" + lu.getCode();
                assertNotNull(destMap.get(nameCode));
            }
        }
    }
}
