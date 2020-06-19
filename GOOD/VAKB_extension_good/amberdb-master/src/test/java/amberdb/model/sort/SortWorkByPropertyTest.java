package amberdb.model.sort;

import amberdb.AbstractDatabaseIntegrationTest;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

import amberdb.model.Work;

public class SortWorkByPropertyTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void sorting() throws Exception {

        List<Work> w = new ArrayList<>();

        Work w1 = amberSession.addWork();
        Work w2 = amberSession.addWork();
        Work w3 = amberSession.addWork();
        Work w4 = amberSession.addWork();
        Work w5 = amberSession.addWork();
        Work w6 = amberSession.addWork();

        w1.setTitle("e");
        w2.setTitle("b");
        w3.setTitle("a");
        w4.setTitle("d");
        w5.setTitle("c");
        // leave w6 title null

        w.add(w6);
        w.add(w5);
        w.add(w4);
        w.add(w3);
        w.add(w2);
        w.add(w1);

        Collections.sort(w, new WorkComparator("title", true));
        assertEquals(w.get(0), w3);
        assertEquals(w.get(1), w2);
        assertEquals(w.get(2), w5);
        assertEquals(w.get(3), w4);
        assertEquals(w.get(4), w1);
        assertEquals(w.get(5), w6);

        Collections.sort(w, new WorkComparator("title", false));
        assertEquals(w.get(0), w1);
        assertEquals(w.get(1), w4);
        assertEquals(w.get(2), w5);
        assertEquals(w.get(3), w2);
        assertEquals(w.get(4), w3);
        assertEquals(w.get(5), w6);


        w1.setAlias(Arrays.asList("xab","boots"));
        w2.setAlias(Arrays.asList("zab"));
        w3.setAlias(Arrays.asList("xab","boots"));
        w4.setAlias(Arrays.asList("flute","boots"));
        // leave w5 null
        w6.asVertex().setProperty("alias", "[]");

        Collections.sort(w, new WorkComparator("alias", true));
        assertEquals(w.get(0).getAlias().get(0), "flute");
        assertEquals(w.get(1).getAlias().get(0), "xab");
        assertEquals(w.get(2).getAlias().get(0), "xab");
        assertEquals(w.get(3).getAlias().get(0), "zab");
        assertEquals(w.get(4).getAlias().size(), 0);
        assertEquals(w.get(5).getAlias().size(), 0);

        Collections.sort(w, new WorkComparator("alias", false));
        assertEquals(w.get(0).getAlias().get(0), "zab");
        assertEquals(w.get(1).getAlias().get(0), "xab");
        assertEquals(w.get(2).getAlias().get(0), "xab");
        assertEquals(w.get(3).getAlias().get(0), "flute");
        assertEquals(w.get(4).getAlias().size(), 0);
        assertEquals(w.get(5).getAlias().size(), 0);


        w1.setIngestJobId(5L);
        w2.setIngestJobId(0L);
        w3.setIngestJobId(-12L);
        w4.setIngestJobId(2225L);
        // leave w5 null;
        w6.setIngestJobId(0L);

        Collections.sort(w, new WorkComparator("ingestJobId", true));
        assertEquals(w.get(0), w3);
        assertEquals(w.get(1).getIngestJobId(), new Long(0));
        assertEquals(w.get(2).getIngestJobId(), new Long(0));
        assertEquals(w.get(3), w1);
        assertEquals(w.get(4), w4);
        assertEquals(w.get(5), w5);

        Collections.sort(w, new WorkComparator("ingestJobId", false));
        assertEquals(w.get(0), w4);
        assertEquals(w.get(1), w1);
        assertEquals(w.get(2).getIngestJobId(), new Long(0));
        assertEquals(w.get(3).getIngestJobId(), new Long(0));
        assertEquals(w.get(4), w3);
        assertEquals(w.get(5), w5);
    }
}

