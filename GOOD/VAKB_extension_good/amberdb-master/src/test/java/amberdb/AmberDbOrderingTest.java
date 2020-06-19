package amberdb;

import amberdb.model.Node;
import amberdb.model.Page;
import amberdb.model.Work;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class AmberDbOrderingTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testPersistReorderedWork() throws IOException {
        Work w1, w2, w3, w4;
        Long sessId;
        List<Long> order = new ArrayList<>();
        List<Long> order1 = new ArrayList<>();
        try (AmberSession db = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()))) {
            w1 = db.addWork();
            List<Work> pages = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                Page p = w1.addPage();
                p.setSubType("Page");
                pages.add(p);
            }
            Collections.reverse(pages);
            w1.orderParts(pages);
            for (Node node : pages) {
                order.add(node.getId());
            }
            sessId = db.suspend();
            db.commit();
            db.close();
        }
        try (AmberSession db = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()), sessId)) {
            w2 = db.findWork(w1.getId());
            assertNotNull(w2);
            Iterable<Page> pages = w2.getPages();
            int i = 0;
            for (Page page : pages) {
                assertTrue(page.getId() == order.get(i).longValue());
                i = i + 1;
            }
            db.close();
        }
        try (AmberSession db = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()), sessId)) {
            w3 = db.findWork(w2.getId());
            assertNotNull(w3);
            List<Work> pages = w3.getPartsOf("Page");
            Collections.reverse(pages);
            w3.orderParts(pages);
            long ssId = db.suspend();
            sessId = db.suspend();
            db.commit();
            int i = 0;
            for (Node page : pages) {
                assertFalse(page.getId() == order.get(i).longValue());
                i = i + 1;
                order1.add(page.getId());
            }
            db.close();
        }
        try (AmberSession db = new AmberSession(AmberDb.openBlobStore(folder.getRoot().getAbsolutePath()), sessId)) {
            w4 = db.findWork(w3.getId());
            assertNotNull(w4);
            List<Work> pages = w4.getPartsOf("Page");
            int i = 0;
            for (Work page : pages) {
                assertTrue(page.getId() == order1.get(i).longValue());
                i = i + 1;
            }
            db.close();
        }
    }
}
