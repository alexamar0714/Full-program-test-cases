package amberdb.version;

import amberdb.graph.AmberEdge;
import amberdb.graph.AmberGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class VersionQueryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    public AmberGraph graph;
    public VersionedGraph vGraph;

    Path tempPath;
    DataSource src;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        tempPath = Paths.get(tempFolder.getRoot().getAbsolutePath());
        src = JdbcConnectionPool.create("jdbc:h2:"+tempPath.toString()+"amber;auto_server=true;DATABASE_TO_UPPER=false","sess","sess");
        graph = new AmberGraph(src);
        vGraph = new VersionedGraph(src);
    }

    @After
    public void teardown() {}


    @Test
    public void testTxns1() throws Exception {
        
        // lets try some volume stuff
        String title1 = "Blinky kills again";
        long txn1 = createBook(20, title1);
        
        // modify some bits
        Vertex book = graph.getVertices("title", title1).iterator().next();
        for (Vertex page : book.getVertices(Direction.IN, "isPartOf")) {
            if ((Integer) page.getProperty("subUnitNo") % 2 == 0)
                page.setProperty("publisher", "Wolfenstein");
            if ((Integer) page.getProperty("subUnitNo") % 3 == 0)
                page.remove();            
        }
        Long txn2 = graph.commit("test", "modified book 1");
       
        // modify some more ...
        // add 3 pages
        createPage(book, 10);
        createPage(book, 20);
        createPage(book, 40);
        Long txn3 = graph.commit("test", "modified book 1 again");
        
        // make another book
        String title2 = "Blinky rises";
        long txn4 = createBook(10, title2);

        // reorder some pages
        book = graph.getVertices("title", title2).iterator().next();
        for (Vertex page : book.getVertices(Direction.IN, "isPartOf")) {
            if ((Integer) page.getProperty("subUnitNo") % 20 == 0) {
                Edge e = page.getEdges(Direction.OUT, "isPartOf").iterator().next();
                e.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, 44);
            }
        }
        Long txn5 = graph.commit("test", "modified book 2");

        // and another book
        String title3 = "Blinky unleashed";
        long txn6 = createBook(15, title3);

        // reorder some pages
        book = graph.getVertices("title", title3).iterator().next();
        for (Vertex page : book.getVertices(Direction.IN, "isPartOf")) {
            if ((Integer) page.getProperty("subUnitNo") % 3 == 0) {
                Edge e = page.getEdges(Direction.OUT, "isPartOf").iterator().next();
                e.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, 44);
            }
            if (page.getProperty("subUnitNo").equals(3)) {
                page.remove();
            }
            if (page.getProperty("subUnitNo").equals(5)) {
                page.setProperty("publisher", "foo fum");
            }
        }
        Long txn7 = graph.commit("test", "modified book 3");

        
        // Here we go
        VersionQuery vq = new VersionQuery((Long) book.getId(), vGraph);
        List<String> labels = new ArrayList<>();
        labels.add("isPartOf");
        labels.add("isCopyOf");
        labels.add("isFileOf");
        labels.add("descriptionOf");
        vq.branch(labels, Direction.IN);
        vq.branch(labels, Direction.IN);
        vq.branch(labels, Direction.IN);
        vq.branch(labels, Direction.IN);

        List<VersionedVertex> l = vq.execute();

        int works = 0;
        int pages = 0;
        int copies = 0;
        int files = 0;
        int descs = 0;
        
        for (VersionedVertex v: l) {
            switch ((String) v.getFirst().getProperty("type")) {
            case "Work": works++; break;
            case "Page": pages++; break;
            case "Copy": copies++; break;
            case "File": files++; break;
            case "Description": descs++; break;
            }
        }
        assertEquals(1, works);
        assertEquals(15, pages);
        assertEquals(30, copies);
        assertEquals(30, files);
        assertEquals(30, descs);
        
    }        

    
    public static void s(String s) {
        System.out.println(s);
    }
    
    
    private Long createBook(int numPages, String title) {
        
        Vertex book = graph.addVertex(null);
        book.setProperty("type", "Work");
        book.setProperty("bibLevel", "Item");
        book.setProperty("title", title);
        
        for (int i = 0; i < numPages; i++) {
            createPage(book, i);
        }
        return graph.commit("test", "create book");
    }
    
    
    private Vertex createPage(Vertex book, int pageNum) {
        Vertex page = graph.addVertex(null);
        
        page.setProperty("title", "page " + pageNum);
        page.setProperty("subUnitNo", pageNum);
        page.setProperty("type", "Page");
        page.setProperty("bibLevel", "Part");
        
        Edge rel = graph.addEdge(null, page, book, "isPartOf");
        rel.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, pageNum);
        
        createCopy(page, "Master");
        createCopy(page, "CoMaster");
        
        return page;
    }

    private Vertex createCopy(Vertex page, String subtype) {
        Vertex copy = graph.addVertex(null);
        copy.setProperty("type", "Copy");
        copy.setProperty("subtype", subtype);
        copy.addEdge("isCopyOf", page);
        createFile(copy);
        return copy;
    }
    
    private Vertex createFile(Vertex copy) {
        Vertex file = graph.addVertex(null);
        file.setProperty("type", "File");
        file.setProperty("fileName", "fiddle-de.do");
        file.addEdge("isFileOf", copy);
        createDescription(file);
        return file;
    }
    
    
    private Vertex createDescription(Vertex file) {
        Vertex desc = graph.addVertex(null);
        desc.setProperty("type", "Description");
        desc.setProperty("alternativeTitle", "foo foo");
        desc.addEdge("descriptionOf", file);
        return desc;
    }
}
