package amberdb.version;

import amberdb.TransactionIndexer;
import amberdb.graph.AmberEdge;
import amberdb.graph.AmberGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;


public class TransactionsTest {

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
    public void testTxns2() throws Exception {
        
        // create simple graph
        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        
        v1.setProperty("type", "Work");
        v2.setProperty("type", "Work");
        v3.setProperty("type", "Work");

        v1.setProperty("title", "v1");
        v2.setProperty("title", "v2");
        v3.setProperty("title", "v3");
        
        v1.setProperty("bibId", 1);
        v2.setProperty("bibId", 2);
        v3.setProperty("bibId", 3);
        
        v1.addEdge("links", v2);
        v2.addEdge("links", v3);
        v3.addEdge("links", v1);
        
        // commit
        graph.commit("test", "c1");
        
        // do some things
        v1.setProperty("title", "vertex 1");
        v2.setProperty("bibId", 100);
        Edge e4 = graph.addEdge(null, v1, v2, "ordered");
        
        // commit
        graph.commit("test", "c2");
        
        // do some more things
        v3.remove();
        e4.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, 99);
        
        // commit
        graph.commit("test", "c2");
        
        vGraph.loadTransactionGraph(0L, 100L, true);

        Assert.assertEquals(((List) vGraph.getVertices()).size(), 3);
        Assert.assertEquals(((List) vGraph.getEdges()).size(), 4);
    }        
    
    
    @Test
    public void testTxns3() throws Exception {
        
        // lets try some volume stuff
        String title1 = "Blinky kills again";
        long txn1 = createBook(20, title1);
        
        // modify some bits
        Vertex book = graph.getVertices("title", title1).iterator().next();
        for (Vertex page : book.getVertices(Direction.IN, "isPartOf")) {
            if ((Integer) page.getProperty("subUnitNo") % 4 == 1) {
                page.setProperty("publisher", "Black Wolf");
            }
            if ((Integer) page.getProperty("subUnitNo") % 4 == 0) {
                page.remove();            
            }
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
        long txn4 = createBook(40, title2);

        // reorder some pages
        book = graph.getVertices("title", title2).iterator().next();
        for (Vertex page : book.getVertices(Direction.IN, "isPartOf")) {
            if ((Integer) page.getProperty("subUnitNo") % 5 == 0) {
                Edge e = page.getEdges(Direction.OUT, "isPartOf").iterator().next();
                e.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, (Integer) page.getProperty("subUnitNo") + 3000);
            }
        }
        book.setProperty("internalAccessConditions", "Closed");
        Long txn5 = graph.commit("test", "modified book 2");
        
        TransactionIndexer cl = new TransactionIndexer(graph);
        Set<Long>[] objSets = cl.findObjectsToBeIndexed(1L, txn1);
        Set<Long> modified = objSets[0];
        Set<Long> deleted = objSets[1];
        Assert.assertEquals(modified.size(), 61);
        Assert.assertEquals(deleted.size(), 0);
        
        cl = new TransactionIndexer(graph);
        objSets = cl.findObjectsToBeIndexed(txn1, txn2);
        modified = objSets[0];
        deleted = objSets[1];
        Assert.assertEquals(modified.size(), 5);
        Assert.assertEquals(deleted.size(), 5);
        
        cl = new TransactionIndexer(graph);
        objSets = cl.findObjectsToBeIndexed(txn2, txn3);
        modified = objSets[0];
        deleted = objSets[1];
        Assert.assertEquals(modified.size(), 9);
        Assert.assertEquals(deleted.size(), 0);

        cl = new TransactionIndexer(graph);
        objSets = cl.findObjectsToBeIndexed(txn3, txn4);
        modified = objSets[0];
        deleted = objSets[1];
        Assert.assertEquals(modified.size(), 121);
        Assert.assertEquals(deleted.size(), 0);

        cl = new TransactionIndexer(graph);
        objSets = cl.findObjectsToBeIndexed(txn4, txn5);
        modified = objSets[0];
        deleted = objSets[1];
        Assert.assertEquals(modified.size(), 0);
        Assert.assertEquals(deleted.size(), 1);
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
        
        page.setProperty("title", "title " + pageNum);
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
        file.setProperty("filename", "fiddle-de.do");
        file.addEdge("isFileOf", copy);
        return file;
    }
}