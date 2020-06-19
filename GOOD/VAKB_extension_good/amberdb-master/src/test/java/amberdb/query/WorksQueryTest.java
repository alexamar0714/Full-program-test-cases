package amberdb.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.enums.BibLevel;
import amberdb.model.Copy;
import amberdb.model.Work;

public class WorksQueryTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void getWorksQueryTest() throws IOException {

        List<Long> ids = new ArrayList<>(); 
        for (int i = 0; i < 100; i++) {
            Work w = amberSession.addWork();
            if (i % 2 == 0) {
                ids.add(w.getId());
            }
        }
        amberSession.commit();
        amberSession.getAmberGraph().clear();
        List<Work> works = WorksQuery.getWorks(amberSession, ids);
        assertEquals(works.size(), 50);
    }

    @Test
    public void getCopiesWithWorksTest() throws IOException {

        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Work w = amberSession.addWork();
            Copy c = w.addCopy();
            if (i % 2 == 0) {
                ids.add(c.getId());
            }
        }
        amberSession.commit();
        amberSession.getAmberGraph().clear();
        amberSession.getAmberGraph().setLocalMode(true);
        List<Copy> copies = WorksQuery.getCopiesWithWorks(amberSession, ids);
        assertEquals(copies.size(), 50);
        for (Copy c : copies) {
            assertNotNull(c.getWork());
        }
    }
    
    @Test
    public void getDistinctChildrenBibLevels(){
        Work work1 = amberSession.addWork();
        Work child1 = amberSession.addWork();
        child1.setBibLevel("Item");
        Work child2 = amberSession.addWork();
        child2.setBibLevel("Set");
        Work child3 = amberSession.addWork();
        Work child4 = amberSession.addWork();
        child4.setBibLevel("Item, Item");
        work1.addChild(child1);
        work1.addChild(child2);
        work1.addChild(child3);
        work1.addChild(child4);
        Work work2 = amberSession.addWork();
        Work child5 = amberSession.addWork();
        child5.setBibLevel("Part");
        work2.addChild(child5);
        Work work3 = amberSession.addWork();
        amberSession.commit();
        amberSession.getAmberGraph().clear();
        amberSession.getAmberGraph().setLocalMode(true);
        Set<BibLevel> bibLevels = WorksQuery.getDistinctChildrenBibLevels(amberSession, Arrays.asList(work1.getId(), work2.getId(), work3.getId()));
        Set<BibLevel> expected = new HashSet<>(Arrays.asList(BibLevel.SET, BibLevel.ITEM, BibLevel.PART));
        assertThat(bibLevels, is(expected));
    }
    
    @Test
    public void getDistinctParentBibLevels(){
        Work parent1 = amberSession.addWork();
        Work child1 = amberSession.addWork();
        parent1.setBibLevel("item");
        parent1.addChild(child1);
        Work parent2 = amberSession.addWork();
        Work child2 = amberSession.addWork();
        parent2.setBibLevel("set");
        parent2.addChild(child2);
        Work workNoParent = amberSession.addWork();
        amberSession.commit();
        amberSession.getAmberGraph().clear();
        amberSession.getAmberGraph().setLocalMode(true);
        Set<BibLevel> bibLevels = WorksQuery.getDistinctParentBibLevels(amberSession, Arrays.asList(child1.getId(), child2.getId(), workNoParent.getId()));
        Set<BibLevel> expected = new HashSet<>(Arrays.asList(BibLevel.SET, BibLevel.ITEM));
        assertThat(bibLevels, is(expected));
    }
  
}
