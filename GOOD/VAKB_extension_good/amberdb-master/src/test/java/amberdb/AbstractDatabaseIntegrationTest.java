package amberdb;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import javax.sql.DataSource;

public abstract class AbstractDatabaseIntegrationTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    public AmberDb amberDb;
    public AmberSession amberSession;
    public DataSource amberSrc;
    public String tempPath;

    @Before
    public void setupIntegrationTest() {
        if(amberDb == null) {
            tempPath = tempFolder.getRoot().getAbsolutePath();
            amberSrc = JdbcConnectionPool.create("jdbc:h2:mem:amber;DB_CLOSE_DELAY=-1;MVCC=true;DATABASE_TO_UPPER=false", "", "");
            amberDb = new AmberDb(amberSrc, tempPath);
        }
        amberSession = amberDb.begin();
    }

    @After
    public void tearDownIntegrationTest() throws Exception {
        if (amberSession != null) amberSession.close();
        amberSrc.getConnection().prepareStatement("DROP ALL OBJECTS").execute();
    }

}
