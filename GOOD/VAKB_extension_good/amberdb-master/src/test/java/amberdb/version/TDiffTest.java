package amberdb.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import org.junit.rules.TemporaryFolder;

import amberdb.graph.AmberGraph;

import com.tinkerpop.blueprints.Vertex;


public class TDiffTest {

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
        
        // vertex
        Vertex v = graph.addVertex(null);
        v.setProperty("type", "Work");

        Long id = (Long) v.getId();
        
        v.setProperty("title", "v1");
        v.setProperty("form", "v2");
        v.setProperty("recordSource", "v3");
        v.setProperty("bibId", "v4");
        
        // commit
        long txn1 = graph.commit("test", "c1");
        //s("txn " + txn1);
        
        // modify vertex
        v.setProperty("title", "v5");
        v.setProperty("recordSource", "v6");
        v.removeProperty("bibId");
        v.setProperty("collection", "v7");
        
        // commit
        long txn2 = graph.commit("test", "c2");
        //s("txn " + txn2);
        
        // delete vertex
        v.remove();
        
        // commit
        long txn3 = graph.commit("test", "c3");
        //s("txn " + txn3);

        VersionedVertex vv = vGraph.getVertex(id);
        
        // get NEW transitions (before creation -> v1 and v2);
        TVertexDiff new1 = vv.getDiff(0L, txn1);
        TVertexDiff new2 = vv.getDiff(0L, txn2);
        
        // get DELETED transitions (v1, v2 -> after deletion)
        TVertexDiff del1 = vv.getDiff(txn1, txn3);
        TVertexDiff del2 = vv.getDiff(txn2, txn3);

        // get MODIFIED transition (v1 -> v2)
        TVertexDiff mod1 = vv.getDiff(txn1, txn2);

        // get UNCHANGED transitions (v1 -> v1 and before creation -> after deletion)
        TVertexDiff un1 = vv.getDiff(txn1, txn1);
        TVertexDiff un2 = vv.getDiff(0L, txn3);

        Map<String, Object[]> diffs; 
        //s("--NEW transitions:");
        //printDiffMap(new1);
        diffs = new1.getDiffMap();
        assertNull(diffs.get("title")[0]);
        assertNull(diffs.get("form")[0]);
        assertNull(diffs.get("recordSource")[0]);
        assertNull(diffs.get("bibId")[0]);
        assertEquals("v1", diffs.get("title")[1]);
        assertEquals("v2", diffs.get("form")[1]);
        assertEquals("v3", diffs.get("recordSource")[1]);
        assertEquals("v4", diffs.get("bibId")[1]);

        //s("\n");
        //printDiffMap(new2);        
        diffs = new2.getDiffMap();
        assertNull(diffs.get("title")[0]);
        assertNull(diffs.get("form")[0]);
        assertNull(diffs.get("recordSource")[0]);
        assertNull(diffs.get("collection")[0]);
        assertEquals("v5", diffs.get("title")[1]);
        assertEquals("v2", diffs.get("form")[1]);
        assertEquals("v6", diffs.get("recordSource")[1]);
        assertEquals("v7", diffs.get("collection")[1]);

        //s("\n--DELETED transitions:");
        //printDiffMap(del1);
        diffs = del1.getDiffMap();
        assertNull(diffs.get("title")[1]);
        assertNull(diffs.get("form")[1]);
        assertNull(diffs.get("recordSource")[1]);
        assertNull(diffs.get("bibId")[1]);
        assertEquals("v1", diffs.get("title")[0]);
        assertEquals("v2", diffs.get("form")[0]);
        assertEquals("v3", diffs.get("recordSource")[0]);
        assertEquals("v4", diffs.get("bibId")[0]);
        
        //s("\n");
        //printDiffMap(del2);        
        diffs = del2.getDiffMap();
        assertNull(diffs.get("title")[1]);
        assertNull(diffs.get("form")[1]);
        assertNull(diffs.get("recordSource")[1]);
        assertNull(diffs.get("collection")[1]);
        assertEquals("v5", diffs.get("title")[0]);
        assertEquals("v2", diffs.get("form")[0]);
        assertEquals("v6", diffs.get("recordSource")[0]);
        assertEquals("v7", diffs.get("collection")[0]);

        //s("\n--MODIFIED transitions:");
        //printDiffMap(mod1);
        diffs = mod1.getDiffMap();
        assertEquals("v1", diffs.get("title")[0]);
        assertEquals("v2", diffs.get("form")[0]);
        assertEquals("v3", diffs.get("recordSource")[0]);
        assertEquals("v4", diffs.get("bibId")[0]);
        assertNull(diffs.get("collection")[0]);

        assertEquals("v5", diffs.get("title")[1]);
        assertEquals("v2", diffs.get("form")[1]);
        assertEquals("v6", diffs.get("recordSource")[1]);
        assertEquals("v7", diffs.get("collection")[1]);
        assertNull(diffs.get("bibId")[1]);

        //s("\n--UNCHANGED transitions:");
        //printDiffMap(un1);
        diffs = un1.getDiffMap();
        assertEquals(diffs.get("title")[0], diffs.get("title")[1]);
        assertEquals(diffs.get("form")[0], diffs.get("form")[1]);
        assertEquals(diffs.get("recordSource")[0], diffs.get("recordSource")[1]);
        assertEquals(diffs.get("bibId")[0], diffs.get("bibId")[1]);
        
        //s("\n");
        //printDiffMap(un2);        
        diffs = un2.getDiffMap();
        assertEquals(0, diffs.size());
    }        
    
    @Test
    public void testChangeDiff() throws Exception {
        
        // before creation
        long txn0 = graph.commit("test", "c0");
        
        Vertex v = graph.addVertex(null);
        v.setProperty("type", "Work");
        Long id = (Long) v.getId();
        v.setProperty("title", "v1");
        v.setProperty("form", "v2");
        v.setProperty("recordSource", "v3");
        v.setProperty("bibId", "v4");
        
        // on creation
        long txn1 = graph.commit("test", "c1");

        // after creation
        long txn2 = graph.commit("test", "c2");

        // delete vertex
        v.remove();

        // on deletion
        long txn3 = graph.commit("test", "c3");

        // after deletion
        long txn4 = graph.commit("test", "c4");

        VersionedVertex vv = vGraph.getVertex(id);
        
        assertEquals(vv.getDiff(txn0, txn4).getTransition(), TTransition.UNCHANGED); // before creation after deletion
        assertEquals(vv.getDiff(txn2, txn4).getTransition(), TTransition.DELETED); // after creation after deletion
        assertEquals(vv.getDiff(txn1, txn4).getTransition(), TTransition.DELETED); // on creation after deletion
        assertEquals(vv.getDiff(txn1, txn3).getTransition(), TTransition.DELETED); // on creation on deletion
        assertEquals(vv.getDiff(txn2, txn3).getTransition(), TTransition.DELETED); // after creation on deletion

    }        
    
    private void printDiffMap(TElementDiff t) {
        Map<String, Object[]> diffMap = t.getDiffMap();
        for (Entry<String, Object[]> entry: diffMap.entrySet()) {
            s(entry.getKey() + ": " + entry.getValue()[0] + " -> " + entry.getValue()[1]);
        }
    }
    
    
    private void s(String s) {
        System.out.println(s);
    }
}
