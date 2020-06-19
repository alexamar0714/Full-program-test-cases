package amberdb.graph;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.graph.AmberMultipartQuery.QueryClause;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static amberdb.graph.BranchType.*;


public class AmberQueryTest extends AbstractDatabaseIntegrationTest {

    public AmberGraph graph;

    @Before
    public void setup() throws MalformedURLException, IOException {
        graph = amberSession.getAmberGraph();
    }

    @Test
    public void testQueryGeneration() throws Exception {

        List<Long> heads = new ArrayList<Long>();
        heads.add(100L);
        
        AmberQuery q = graph.newQuery(heads);
        q.branch(Arrays.asList(new String[] {"isPartOf", "belongsTo"}), Direction.BOTH);
        q.branch(Arrays.asList(new String[] {"isCopyOf", "belongsTo"}), Direction.IN);
        q.branch(Arrays.asList(new String[] {"isFileOf", "belongsTo"}), Direction.IN);
        //s(q.generateFullSubGraphQuery());
    }
 

    @Test
    public void testExecuteQuery() throws Exception {

        // set up database

        Vertex set = graph.addVertex(null);
        set.setProperty("type", "Set");

        Vertex setPart1 = graph.addVertex(null);
        Vertex setPart2 = graph.addVertex(null);

        setPart1.setProperty("type", "Bit");
        setPart2.setProperty("type", "Bit");
        
        setPart1.addEdge("isPartOf", set);
        setPart2.addEdge("isPartOf", set);
        
        Vertex book1 = makeBook("AA", 50, 10);
        Vertex book2 = makeBook("BB", 50, 0);
        Vertex book3 = makeBook("CC", 30, 10);

        book1.addEdge("isPartOf", set);
        book2.addEdge("isPartOf", set);
        book3.addEdge("isPartOf", set);
        
        graph.commit("bookMaker", "made books");
        
        graph.clear();
        
        List<Long> heads = new ArrayList<Long>();
        heads.add((Long) book1.getId());
        heads.add((Long) book2.getId());
        //heads.add((Long) book3.getId());
        
        AmberQuery q = graph.newQuery(heads);
        q.branch(new String[] {"isPartOf"}, Direction.IN);
        q.branch(new String[] {"isPartOf"}, Direction.IN);
        
        q.branch(BRANCH_FROM_ALL, new String[] {"isCopyOf"}, Direction.IN);
        q.branch(BRANCH_FROM_PREVIOUS, new String[] {"isFileOf"}, Direction.IN);
        q.branch(BRANCH_FROM_PREVIOUS, new String[] {"descriptionOf"}, Direction.IN);
        q.branch(BRANCH_FROM_LISTED, new String[] {"isPartOf"}, Direction.OUT, new Integer[] {0});

        initTimer();
        List<Vertex> results = q.execute(true);
        //mark("MILLIS TO RUN");
        
        graph.setLocalMode(true);
        
        
        Assert.assertEquals(results.size(), 706);
        Vertex book = graph.getVertex(book2.getId());
        List<Vertex> pages = (List<Vertex>) book.getVertices(Direction.IN, "isPartOf");
        Assert.assertEquals(pages.size(), 51);
    }
    
    Date then, now;
    private void initTimer() { then = new Date(); }
    private void mark(String msg) {
        Date now = new Date();
        s("-- " + msg + " : " + (now.getTime() - then.getTime()) + "ms");
        then = now;
    }

    @Test
    public void testContinuingQueryForDelete() throws Exception {

        initTimer();
        
        // set up database

        Vertex set = graph.addVertex(null);
        set.setProperty("type", "Set");

        Vertex setPart1 = graph.addVertex(null);
        Vertex setPart2 = graph.addVertex(null);

        setPart1.setProperty("type", "Set");
        setPart2.setProperty("type", "Set");
        
        setPart1.addEdge("isPartOf", set);
        setPart2.addEdge("isPartOf", set);
        
        Vertex book1 = makeBook("1-1", 10, 10);
        Vertex book2 = makeBook("1-2", 10, 0);
        Vertex book3 = makeBook("1-3", 10, 10);

        Vertex book4 = makeBook("2-1", 10, 10);
        Vertex book5 = makeBook("2-2", 10, 0);
        Vertex book6 = makeBook("2-3", 10, 10);
        
        Vertex book7 = makeBook("1-1-1", 10, 10);
        Vertex book8 = makeBook("1-1-2", 10, 0);
        Vertex book9 = makeBook("1-1-3", 10, 10);

        Vertex book10 = makeBook("1-1-1-1", 10, 10);
        Vertex book11 = makeBook("1-1-1-2", 10, 0);
        Vertex book12 = makeBook("1-1-1-3", 10, 10);

        Vertex book13 = makeBook("1-2-1", 10, 10);
        Vertex book14 = makeBook("1-2-2", 10, 0);
        Vertex book15 = makeBook("1-2-3", 10, 10);

        Vertex book16 = makeBook("2-2-1", 10, 10);
        Vertex book17 = makeBook("2-2-2", 10, 0);
        Vertex book18 = makeBook("2-2-3", 10, 10);
        
        book1.addEdge("isPartOf", setPart1);
        book2.addEdge("isPartOf", setPart1);
        book3.addEdge("isPartOf", setPart1);

        book4.addEdge("isPartOf", setPart2);
        book5.addEdge("isPartOf", setPart2);
        book6.addEdge("isPartOf", setPart2);
        
        book7.addEdge("isPartOf", book1);
        book8.addEdge("isPartOf", book1);
        book9.addEdge("isPartOf", book1);
        
        book10.addEdge("isPartOf", book7);
        book11.addEdge("isPartOf", book7);
        book12.addEdge("isPartOf", book7);
        
        book13.addEdge("isPartOf", book2);
        book14.addEdge("isPartOf", book2);
        book15.addEdge("isPartOf", book2);
        
        book16.addEdge("isPartOf", book5);
        book17.addEdge("isPartOf", book5);
        book18.addEdge("isPartOf", book5);
        
        //mark("before commit");
        
        graph.commit("bookMaker", "made books");
        graph.clear();

        //mark("after commit");
        
        List<Long> heads = new ArrayList<Long>();
        heads.add((Long) set.getId());
        
        List<Vertex> deletees;
        try (AmberMultipartQuery q = graph.newMultipartQuery(heads)) {

            String numKids = 
                    "SELECT COUNT(flatedge.id) num "
                    + "FROM flatedge, v1 "
                    + "WHERE v1.step = %d " 
                    + "AND v1.vid = flatedge.v_in "
                    + "AND flatedge.label = 'isPartOf' ";

            QueryClause qc = q.new QueryClause(BRANCH_FROM_PREVIOUS, new String[] { "isPartOf" }, Direction.IN);

            boolean moreParts = true;
            int step = 0;
            q.startQuery();
            while (moreParts) {
                step = q.step + 1; // add 1 because the checkQuery is run after the following step is executed
                List<Map<String, Object>> thing = q.continueWithCheck(String.format(numKids, step), qc);
                Long numParts = (Long) thing.get(0).get("num");
                switch (step) {
                case 1: 
                    Assert.assertTrue(numParts.equals(6L));
                    break;
                case 2: 
                    Assert.assertTrue(numParts.equals(75L));
                    break;
                case 3: 
                    Assert.assertTrue(numParts.equals(102L));
                    break;
                case 4: 
                    Assert.assertTrue(numParts.equals(33L));
                    break;
                case 5: 
                    Assert.assertTrue(numParts.equals(0L));
                    break;
                default:    
                }
                //mark("iteration " + step);
                if (numParts.equals(0L)) moreParts = false;
            }    

            // get all the copies, files etc
            q.continueWithCheck(null,
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] { "isCopyOf" }, Direction.IN), 
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] { "isFileOf" }, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] { "descriptionOf" }, Direction.IN));
            //mark("find copies files and descriptions");

            deletees = q.getResults();
            
            //mark("getting results");
        }
        
        int i = 0;
        graph.setLocalMode(true);
        for (Vertex v : deletees) {
            graph.removeVertex(v);
            i++;
        }
        graph.setLocalMode(false);
        Assert.assertEquals(i, 1299);
        //mark("delete in mem");

        graph.commit("test", "kill them all");
        //mark("committed delete");

    }
    
    
    void s(String s) {
        System.out.println(s);
    }
    
    
    private Vertex makeBook(String title, int numPages, int numProps) {

        Vertex book = graph.addVertex(null);
        book.setProperty("title", title);
        book.setProperty("type", "Work");

        // add a section
        Vertex section = graph.addVertex(null);
        section.setProperty("type", "Section");
        
        for (int i = 0; i < numPages; i++) {
            Vertex page = addPage(book, i, numProps);
            section.addEdge("existsOn", page);
        }

        section.addEdge("isPartOf", book);
        
        return book;
    }
    

    private Vertex addPage(Vertex book, int num, int numProps) {
        Vertex page = graph.addVertex(null);
        page.setProperty("type", "Work");
        page.setProperty("subUnitNo", num);
        addCopy(page, "Master", num, numProps);
        addCopy(page, "Co-master", num, numProps);
        Edge e = page.addEdge("isPartOf", book);
        e.setProperty("edge-order", num);
        return page;
    }
    
    private Vertex addCopy(Vertex page, String type, int num, int numProps) {
        Vertex copy = graph.addVertex(null);
        copy.setProperty("type", "Copy");
        copy.setProperty("subUnitNo", num);
        addFile(copy, num, numProps);
        copy.addEdge("isCopyOf", page);
        return copy;
    }
    
    private Vertex addFile(Vertex copy, int num, int numProps) {
        Vertex file = graph.addVertex(null);
        file.setProperty("type", "File");
        addDesc(file, num);
        file.addEdge("isFileOf", copy);
        return file;
    }

    private Vertex addDesc(Vertex file, int num) {
        Vertex desc = graph.addVertex(null);
        desc.setProperty("type", "Description");
        desc.setProperty("alternativeTitle", num);
        desc.addEdge("descriptionOf", file);
        return desc;
    }

}
