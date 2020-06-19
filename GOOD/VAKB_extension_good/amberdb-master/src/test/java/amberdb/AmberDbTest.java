package amberdb;

import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.File;
import amberdb.model.Page;
import amberdb.model.Work;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class AmberDbTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @Test
    public void testPersistence() throws IOException {
        Work w1, w2;
        Long sessId;
        try (AmberSession db = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()))) {
            w1 = db.addWork();
            sessId = db.suspend();
        }
        try (AmberSession db = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()), sessId)) {
            assertNotNull(db.findWork(w1.getId()));
            w2 = db.addWork();
            db.commit();
            db.close();
        }
        assertNotEquals(w1.getId(), w2.getId());
    }

    @Test
    public void testIngestBook() throws IOException {
        
        Path tmpFile = folder.newFile().toPath();
        Files.write(tmpFile, "Hello world".getBytes());

        
        AmberDb adb = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","per","per"), folder.getRoot().getAbsolutePath());
        
        Long sessId;
        Long bookId;
        try (AmberSession db = adb.begin()) {
            Work book = db.addWork();
            bookId = book.getId();
            book.setTitle("Test book");
            for (int i = 0; i < 10; i++) {
                book.addPage().addCopy(tmpFile, CopyRole.MASTER_COPY, "image/tiff");
            }
            sessId = db.suspend();
        }
        
        String line;
        try (AmberSession db = adb.resume(sessId)) {
            
            // now, can we retrieve the files ?
            Work book2 = db.findWork(bookId);
            
            Page p1 = book2.getPage(1);
            Copy c1 = p1.getCopy(CopyRole.MASTER_COPY);
            File f1 = c1.getFile();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(f1.openStream()));
            line = br.readLine();
            
            db.commit();
            db.close();
        }
        // next, persist the session (by closing it) open a new one and get the contents

        adb = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","per","per"), folder.getRoot().getAbsolutePath());
        try (AmberSession db = adb.begin()) {

            Work book2 = db.findWork(bookId);
            
            Page p1 = book2.getPage(1);
            Copy c1 = p1.getCopy(CopyRole.MASTER_COPY);
            File f1 = c1.getFile();

            BufferedReader br = new BufferedReader(new InputStreamReader(f1.openStream()));
            assertEquals(line, br.readLine());
            db.close();
        }
    }
 
    
    @Test
    public void testSuspendResume() throws IOException {
        
        AmberDb adb = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","per","per"), folder.getRoot().getAbsolutePath());
        
        Long sessId;
        Long bookId;
        
        Work book;
        
        try (AmberSession db = adb.begin()) {
            book = db.addWork();
            bookId = book.getId();
            book.setTitle("Test book");
            sessId = db.suspend();
        }

        AmberDb adb2 = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","per","per"), folder.getRoot().getAbsolutePath());
        try (AmberSession db = adb2.resume(sessId)) {
            
            // now, can we retrieve the files ?
            Work book2 = db.findWork(bookId);
            assertEquals(book, book2);
            db.close();
        }
    }

    @Test
    public void testCommitPersistedSession() throws IOException {
        Work w1, w2;
        Long id;
        Long sessId;

        // Suspend a session with a work
        try (AmberSession session = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()))) {
            w1 = session.addWork();
            id = w1.getId();
            sessId = session.suspend();
        }

        // Check it's not in amber
        try (AmberSession session = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()))) {
            boolean thrown = false;
            try {
                session.findWork(id);
            } catch (NoSuchObjectException e) {
                thrown = true;
            }
            assertTrue(thrown);
        }

        // Commit from separate session
        try (AmberSession session = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()))) {
            session.commitPersistedSession(sessId, "user", "testing");
        }

        // Check it's now in amber
        try (AmberSession session = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()))) {
            w2 = session.findWork(id);
            assertEquals(w1, w2);
        }
    }

    @Test
    public void testRemovePersistedSession() throws IOException {
        Work w1, w2;
        Long id;
        Long sessId;

        // Suspend a session with a work
        try (AmberSession session = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()))) {
            w1 = session.addWork();
            id = w1.getId();
            sessId = session.suspend();
        }

        // Check it can be resumed (and its contents found)
        try (AmberSession session = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()), sessId)) {
            w2 = session.findWork(id);
            assertEquals(w1, w2);
        }

        // Remove the session
        try (AmberSession session = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()))) {
            session.removePersistedSession(sessId);
        }

        // Check that it's gone (nothing restored)
        try (AmberSession session = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()), sessId)) {
            boolean thrown = false;
            try {
                session.findWork(id);
            } catch (NoSuchObjectException e) {
                thrown = true;
            }
            assertTrue(thrown);
        }
    }

    void s(String s) {
    	System.out.println(s);
    }
}
