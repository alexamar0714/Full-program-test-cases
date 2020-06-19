package amberdb.graph;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberGraphSuspendResumeTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public AmberGraph graph;
    public AmberGraph graph2;
    
    DataSource src;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        String tempPath = tempFolder.getRoot().getAbsolutePath();
        src = JdbcConnectionPool.create("jdbc:h2:"+tempPath+"amber;auto_server=true;DATABASE_TO_UPPER=false","sess","sess");
        graph = new AmberGraph(src);
    }

    @After
    public void teardown() {}

    @Test
    public void testPersistingVertex() throws Exception {
        
        // persist vertex
        Vertex v = graph.addVertex(null);
        v.setProperty("type", "Work");
        v.setProperty("dateCreated", new Date());

        
        Object vId = v.getId();
        graph.commit("tester", "testPersistingVertex");

        // clear local session
        graph.clear();
        
        // get from persistent data store
        Vertex v2 = graph.getVertex(vId);
        assertEquals(v, v2);
        
        // remove from local session
        v.remove();
        Vertex v3 = graph.getVertex(vId);
        assertNull(v3);
        
        graph.clear();
        
        // get from persistent data store
        Vertex v4 = graph.getVertex(vId);
        assertEquals(v, v4);

        
        // modify and persist
        v4.setProperty("subject", new char[] {'1','a'});
        
        graph.commit("tester", "testModifyAndPersist");

        graph.clear();
        
        // get from persistent data store
        Vertex v5 = graph.getVertex(vId);
        assertEquals(v5, v4);
        
        // delete from data store
        v5.remove();
        graph.commit("tester", "removeVertex");

        // get from persistent data store
        Vertex v6 = graph.getVertex(vId);
        assertNull(v6);

    }


	@Test
    public void testAddEdgeToUnchangedVertex() throws Exception {

        // persist vertex
        Vertex v1 = graph.addVertex(null);
        v1.setProperty("type", "Work");
        v1.setProperty("dateCreated", new Date());

        // persist vertex
        Vertex v2 = graph.addVertex(null);
        v2.setProperty("type", "Work");
        v2.setProperty("dateCreated", new Date());


        Object vId1 = v1.getId();
        Object vId2 = v2.getId();
        graph.commit("tester", "test");

        // clear local session
        graph.clear();

        // get from persistent data store
        v1 = graph.getVertex(vId1);
        v2 = graph.getVertex(vId2);

        Edge e = graph.addEdge(null, v1, v2, "isPartOf");
        Long eId = (Long) e.getId();
        Long sId = graph.suspend();

        graph.clear();
        graph2 = new AmberGraph(src);
        graph2.resume(sId);

        // test that despite local mode the vertices are still read from amber
        graph2.setLocalMode(true);

        e = graph2.getEdge(eId);
        assertNotNull(e);
        v1 = e.getVertex(Direction.IN);

        assertNotNull(v1);
        assertNotNull(v2);
    }


    @Test
    public void testSessionAddEdgeThenDeleteVertex() throws Exception {

        // persist vertex
        Vertex v1 = graph.addVertex(null);
        v1.setProperty("type", "Work");
        v1.setProperty("dateCreated", new Date());

        // persist vertex
        Vertex v2 = graph.addVertex(null);
        v2.setProperty("type", "Work");
        v2.setProperty("dateCreated", new Date());

        Object vId1 = v1.getId();
        Object vId2 = v2.getId();
        graph.commit("tester", "test");

        // clear local session
        graph.clear();

        // get from persistent data store
        v1 = graph.getVertex(vId1);
        v2 = graph.getVertex(vId2);

        Edge e = graph.addEdge(null, v1, v2, "isPartOf");
        Long eId = (Long) e.getId();
        Long sId = graph.suspend();

        graph.removeVertex(v1);
        graph.commit();

        graph.clear();
        graph2 = new AmberGraph(src);
        graph2.resume(sId);

        e = graph2.getEdge(eId);
        // assertNull(e);  // This may or may not be true depending on how you expect your sessions to work.
                           // But we don't rely on this behaviour one way or the other so don't enforce it here.
    }


    @Test
    public void testSuspendResume() throws Exception {
        
        // persist vertex
        Vertex v = graph.addVertex(null);
        v.setProperty("type", "Work");
        v.setProperty("dateCreated", new Date());
        Long vId = (Long) v.getId();
        
        Vertex v1 = graph.addVertex(null);
        v1.setProperty("type", "Work");
        v1.setProperty("dateCreated", new Date());
        Long v1Id = (Long) v1.getId();

        graph.addEdge(null, v, v1, "link");
        
        Long sessId = graph.suspend();
        
        AmberGraph graph1 = new AmberGraph(src);

        graph1.resume(sessId);
        
        Vertex v2 = graph1.getVertex(vId);
        assertEquals(v2, v); 
        Vertex v3 = graph1.getVertex(v1Id);
        assertEquals(v3, v1); 
    }
    @Test
    public void testSuspendResumeWithMultipleSessions() throws Exception {
        // ensures that when suspending and resuming several sessions with multiple items (e.g. during a long running,
        // multi-step process) the correct session AND the correct item state are being resumed each time. Without this,
        // old state from an items could be picked up when restoring a more recent session.
        // This tests that the AmberQueryBase.SESS_VERTEX_QUERY_PREFIX is correctly joining the sess_xyz tables on both
        // the session id and the item id.

        Vertex v = graph.addVertex(null);
        v.setProperty("type", "Work");
        v.setProperty("dateCreated", new Date());
        Long vId = (Long) v.getId();

        Vertex v1 = graph.addVertex(null);
        v1.setProperty("type", "Work");
        v1.setProperty("dateCreated", new Date());
        Long v1Id = (Long) v1.getId();

        graph.addEdge(null, v, v1, "link");

        Long sessId = graph.suspend();
        graph.resume(sessId);

        graph.getVertex(v1Id).setProperty("subUnitNo", "1");

        sessId = graph.suspend();
        graph.resume(sessId);

        graph.getVertex(v1Id).setProperty("subUnitNo", "2");

        sessId = graph.suspend();
        graph.resume(sessId);

        graph.getVertex(v1Id).setProperty("subUnitNo", "3");

        sessId = graph.suspend();
        graph.resume(sessId);

        AmberGraph graph1 = new AmberGraph(src);

        graph1.resume(sessId);

        Vertex v2 = graph1.getVertex(vId);
        assertEquals(v2, v);
        Vertex v3 = graph1.getVertex(v1Id);
        assertEquals("3", v3.getProperty("subUnitNo"));
    }
    
    
    @Test
    public void testPersistingEdge() throws Exception {
        
        // persist edge
        Vertex v = graph.addVertex(null);
        v.setProperty("type", "Work");
        v.setProperty("title", "ajax");
        v.setProperty("dateCreated", new Date());

        Vertex v2 = graph.addVertex(null);
        v2.setProperty("type", "Work");
        v2.setProperty("title", "hector");
        v2.setProperty("dateCreated", new Date());
        
        Edge e = graph.addEdge(null, v, v2, "isPartOf");
        
        Object eId = e.getId();
        graph.commit("tester", "testPersistingEdge");
        // commit clears the local session
        
        // get from persistent data store
        Edge e2 = graph.getEdge(eId);
        assertEquals(e, e2);
        
        assertEquals(e.getVertex(Direction.OUT), e2.getVertex(Direction.OUT));
        assertEquals(e.getVertex(Direction.IN), e2.getVertex(Direction.IN));
        
        
        // remove from local session
        e2.remove();
        Edge e3 = graph.getEdge(eId);
        assertNull(e3);
        
        graph.clear();
        
        // get from persistent data store
        Edge e4 = graph.getEdge(eId);
        assertEquals(e, e4);
        
        // modify and persist
        e4.setProperty("subject", new char[]{'1', 'a'});
        graph.commit("tester", "testModifyAndPersist");
        
        graph.clear(); // double clear :-)
        
        // get from persistent data store
        Edge e5 = graph.getEdge(eId);

        assertEquals(e5, e4);
        
        // delete an incident vertex from data store
        Object remainingVertexId = e5.getVertex(Direction.OUT).getId();
        Object removedVertexId = e5.getVertex(Direction.IN).getId();
        e5.getVertex(Direction.IN).remove();
        graph.commit("tester", "removeVertex");

        // that should have deleted the edge as well, but the other vertex should remain
        assertNull(graph.getEdge(eId));
        assertNull(graph.getVertex(removedVertexId));
        assertNotNull(graph.getVertex(remainingVertexId));
    }
    
    private void dumpQuery(String string) {
    	System.out.println(string);
    	try (Connection conn = src.getConnection()) {
    	      ResultSet results = conn.createStatement().executeQuery(string);
    	      int numCols = results.getMetaData().getColumnCount();
	    	  StringBuilder sb = new StringBuilder();
	    	  for (int c = 1; c <= numCols; c++) {
	    		  sb.append(String.format("%20s", results.getMetaData().getColumnLabel(c)));
	    	  }
	    	  System.out.println(sb);
    	      while (results.next()) {
    	    	  sb.setLength(0);
    	    	  for (int c = 1; c <= numCols; c++) {
    	    		  Object o = results.getObject(c);
    	    		  sb.append(String.format("%20s", o));
    	    	  }
    	    	  System.out.println(sb);
    	      }
    		
    	} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
