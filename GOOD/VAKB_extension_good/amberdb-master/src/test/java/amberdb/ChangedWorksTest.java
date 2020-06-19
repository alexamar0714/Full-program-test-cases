package amberdb;

import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Work;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ChangedWorksTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    public AmberSession sess;


    String tempPath;
    DataSource src;
    
    @Before
    public void setup() throws MalformedURLException, IOException, Exception {
        System.out.println("Setting up graph");
        tempPath = tempFolder.getRoot().getAbsolutePath();
        src = JdbcConnectionPool.create("jdbc:h2:"+ tempPath +"amber;auto_server=true;DATABASE_TO_UPPER=false","sess","sess");
        sess = new AmberDb(src, tempPath).begin();
    }

    @After
    public void teardown() throws IOException {
        sess.close();
    }


    @Test
    public void testGetChangedWorks() throws Exception {

        Date time1 = new Date();
        Work book1 = createBook(20, "blinky 1");
        sess.commit("tester", "testing");
        
        Date time2 = new Date();
        Work book2 = createBook(20, "blinky 2");
        sess.commit("tester", "testing");

        Date time3 = new Date();
        Work book3 = createBook(20, "blinky 3");
        sess.commit("tester", "testing");

        Date time4 = new Date();
        Work book4 = createBook(20, "blinky 4");
        sess.commit("tester", "testing");

        Map<Long, String> changed;
        
        changed = sess.getModifiedWorkIds(time1);
        assertEquals(changed.size(), 84);
        
        changed = sess.getModifiedWorkIds(time2);
        assertEquals(changed.size(), 63);

        changed = sess.getModifiedWorkIds(time3);
        assertEquals(changed.size(), 42);
        
        changed = sess.getModifiedWorkIds(time4);
        assertEquals(changed.size(), 21);

        // delete 3 pages
        Date time5 = new Date();
        sess.deletePage(book3.getPage(3));
        sess.deletePage(book3.getPage(5));
        sess.deletePage(book3.getPage(7));
        sess.commit("tester", "testing");
        
        changed = sess.getModifiedWorkIds(time5);
        assertEquals(changed.size(), 4); // 3 deleted pages + 1 modified book

        // delete an entire book
        Date time6 = new Date();
        for (Page p : book2.getPages()) {
            sess.deletePage(p);
        }
        sess.deleteWork(book2);
        sess.commit("tester", "testing");
        
        changed = sess.getModifiedWorkIds(time6);
        assertEquals(changed.size(), 21); // 3 deleted pages + 1 modified book
    }        

    
    public static void s(String s) {
        System.out.println(s);
    }
    
    
    private Work createBook(int numPages, String title) {
        Work book = sess.addWork();
        book.setTitle(title);
        for (int i = 0; i < numPages; i++) {
            createPage(book, i);
        }
        return book;
    }
    
    
    private void createPage(Work book, int pageNum) {
        Page page = book.addPage();
        page.setOrder(pageNum);
        page.setTitle("page " + pageNum);
        page.setSummary("summary for " + pageNum);
        createCopy(page, CopyRole.MASTER_COPY);
        createCopy(page, CopyRole.CO_MASTER_COPY);
    }

    private void createCopy(Page page, CopyRole role) {
        Copy copy = page.addCopy();
        copy.setCopyRole(role.toString());
        createFile(copy);
    }
    
    private void createFile(Copy copy) {
        File file = copy.addFile();
        file.setNotes("Oh here is a file");
    }
}
