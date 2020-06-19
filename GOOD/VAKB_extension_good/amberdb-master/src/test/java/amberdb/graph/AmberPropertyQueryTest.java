package amberdb.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tinkerpop.blueprints.Vertex;


public class AmberPropertyQueryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    public AmberGraph graph;
    
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
    public void testOrQueryGeneration() throws Exception {

        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        Vertex v4 = graph.addVertex(null);
        Vertex v5 = graph.addVertex(null);
        Vertex v6 = graph.addVertex(null);
        Vertex v7 = graph.addVertex(null);
        Vertex v8 = graph.addVertex(null);
        Vertex v9 = graph.addVertex(null);
        Vertex v10 = graph.addVertex(null);

        setType("Work", v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);

        v1.setProperty("abstract", "PVALUE1");
        v1.setProperty("category", "PVALUEN");

        v2.setProperty("abstract", "PVALUE2");

        v3.setProperty("title", "false");
        v4.setProperty("title", "Something different");
        v5.setProperty("title", "true");
        
        v6.setProperty("abstract", "PVALUE1");
        v7.setProperty("title", "PVALUE1");
        
        v8.setProperty("heading", "3");
        v9.setProperty("heading", "0");

        v10.setProperty("abstract", "PVALUE1");
        v10.setProperty("title", "true");
        v10.setProperty("heading", "xxxxXXXXxxxx");
        
        graph.commit("tester", "saving some vertices with properties");
        
        AmberProperty p1 = new AmberProperty(0, "abstract", "PVALUE1");
        AmberProperty p2 = new AmberProperty(0, "title", "true");
        AmberProperty p3 = new AmberProperty(0, "heading", "3");
        
        List<AmberProperty> aps = new ArrayList<AmberProperty>();
        aps.add(p1);
        aps.add(p2);
        aps.add(p3);

        AmberVertexQuery avq = graph.newVertexQuery();
        avq.addCriteria(aps);

        List<Vertex> results = avq.execute();
        
        assertEquals(6, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v5));
        assertTrue(results.remove(v6));
        assertTrue(results.remove(v8));
        assertTrue(results.remove(v10));
        assertTrue(results.remove(v10));
        assertEquals(0, results.size());
    }

	@Test
    public void testAndQueryGeneration() throws Exception {

        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        Vertex v4 = graph.addVertex(null);
        Vertex v5 = graph.addVertex(null);
        Vertex v6 = graph.addVertex(null);
        Vertex v7 = graph.addVertex(null);
        Vertex v8 = graph.addVertex(null);
        Vertex v9 = graph.addVertex(null);
        Vertex v10 = graph.addVertex(null);
        
        setType("Work", v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);

        
        v1.setProperty("abstract", "PVALUE1");
        v1.setProperty("category", "PVALUEN");

        v2.setProperty("abstract", "PVALUE2");
        v2.setProperty("category", "PVALUEN");

        v3.setProperty("title", "false");
        v4.setProperty("title", "Something different");
        v5.setProperty("title", "true");
        
        v6.setProperty("abstract", "PVALUE1");
        v6.setProperty("category", "PVALUEN");

        v7.setProperty("title", "PVALUE1");
        
        v8.setProperty("heading", "3");
        v9.setProperty("heading", "0");

        v10.setProperty("abstract", "PVALUE1");
        v10.setProperty("category", "PVALUEN");
        v10.setProperty("title", "true");
        v10.setProperty("heading", "xxxxXXXXxxxx");
        
        graph.commit("tester", "saving some vertices with properties");
        
        AmberProperty p1 = new AmberProperty(0, "abstract", "PVALUE1");
        AmberProperty p2 = new AmberProperty(0, "category", "PVALUEN");
        
        List<AmberProperty> aps = new ArrayList<AmberProperty>();
        aps.add(p1);
        aps.add(p2);
        
        AmberVertexQuery avq = graph.newVertexQuery();
        avq.combineCriteriaWithAnd();
        avq.addCriteria(aps);
        
        List<Vertex> results = avq.execute();
        
        assertEquals(3, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v6));
        assertTrue(results.remove(v10));
        assertEquals(0, results.size());
    }
    
    @Test
    public void testNullValueInCriteriaAreIgnored() throws Exception {

        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        Vertex v4 = graph.addVertex(null);
        Vertex v5 = graph.addVertex(null);
        Vertex v6 = graph.addVertex(null);
        Vertex v7 = graph.addVertex(null);
        Vertex v8 = graph.addVertex(null);
        Vertex v9 = graph.addVertex(null);
        Vertex v10 = graph.addVertex(null);
        
        setType("Work", v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
        
        v1.setProperty("abstract", "PVALUE1");
        v1.setProperty("category", "PVALUEN");

        v2.setProperty("abstract", "PVALUE2");

        v3.setProperty("title", "false");
        v4.setProperty("title", "Something different");
        v5.setProperty("title", "true");
        
        v6.setProperty("abstract", "PVALUE1");
        v7.setProperty("title", "PVALUE1");
        
        v8.setProperty("heading", "3");
        v9.setProperty("heading", "0");

        v10.setProperty("abstract", "PVALUE1");
        v10.setProperty("title", "true");
        v10.setProperty("heading", "xxxxXXXXxxxx");
        
        graph.commit("tester", "saving some vertices with properties");
        
        AmberProperty p1 = new AmberProperty(0, "abstract", "PVALUE1");
        AmberProperty p2 = new AmberProperty(0, "title", "true");
        AmberProperty p3 = new AmberProperty(0, "heading", null);
        
        List<AmberProperty> aps = new ArrayList<AmberProperty>();
        aps.add(p1);
        aps.add(p2);
        aps.add(p3);

        AmberVertexQuery avq = graph.newVertexQuery();
        avq.addCriteria(aps);

        List<Vertex> results = avq.execute();
        
        assertEquals(5, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v5));
        assertTrue(results.remove(v6));
        assertTrue(results.remove(v10));
        assertTrue(results.remove(v10));
        assertEquals(0, results.size());
    }


    @Test
    public void testJsonListValueQueries() throws Exception {

        Vertex v1 = graph.addVertex(null);
        v1.setProperty("type", "Work");
        v1.setProperty("contributor", "[\"abba\",\"beta\",\"delta\",\"gamma\"]");

        Vertex v2 = graph.addVertex(null);
        v2.setProperty("type", "Work");
        v2.setProperty("contributor", "[\"babba\",\"beta\",\"delta\",\"gamma\"]");

        Vertex v3 = graph.addVertex(null);
        v3.setProperty("type", "Work");
        v3.setProperty("contributor", "[\"beta\",\"delta\",\"gamma\", \"abba\"]");

        graph.commit("tester", "saving some vertices with properties");

        AmberVertexQuery avq = graph.newVertexQuery();
        List<Vertex> results = avq.executeJsonValSearch("contributor", "\"abba\"");

        assertEquals(2, results.size());
        assertTrue(results.remove(v1));
        assertTrue(results.remove(v3));
    }


    void s(String s) {
        System.out.println(s);
    }
    
    private void setType(String type, Vertex... vertices) {
		for (Vertex v: vertices) {
			v.setProperty("type", type);
		}
	}


    
}
