package amberdb.graph;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;

import com.tinkerpop.blueprints.EdgeTestSuite;
import com.tinkerpop.blueprints.GraphTestSuite;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TestSuite;
import com.tinkerpop.blueprints.VertexTestSuite;
import com.tinkerpop.blueprints.util.io.gml.GMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONReaderTestSuite;

public class BaseGraphTest extends com.tinkerpop.blueprints.impls.GraphTest {

    public BaseGraph graph;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        
        System.out.println("Setting up graph");
        graph = new BaseGraph();

    }

    @After
    public void teardown() {}

    public void testVertexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new VertexTestSuite(this));
        printTestPerformance("VertexTestSuite", this.stopWatch());
    }

    public void testEdgeTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new EdgeTestSuite(this));
        printTestPerformance("EdgeTestSuite", this.stopWatch());
    }

    public void testGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphTestSuite(this));
        printTestPerformance("GraphTestSuite", this.stopWatch());
    }

    public void testGraphMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphMLReaderTestSuite(this));
        printTestPerformance("GraphMLReaderTestSuite", this.stopWatch());
    }
    
    public void testGMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GMLReaderTestSuite(this));
        printTestPerformance("GMLReaderTestSuite", this.stopWatch());
    }
    
    public void testGraphSONReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphSONReaderTestSuite(this));
        printTestPerformance("GraphSONReaderTestSuite", this.stopWatch());
    }

    @Override
    public Graph generateGraph(String s) {
        try {
            setup();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Setup failed!");
        }
        return graph;
    }
    @Override
    public Graph generateGraph() {
        return generateGraph("this string's purpose is unknown to me");
    }
    
    public void doTestSuite(final TestSuite testSuite) throws Exception {
        String doTest = System.getProperty("testBaseGraph");
        if (doTest == null || doTest.equals("true")) {
            for (Method method : testSuite.getClass().getDeclaredMethods()) {
                if (method.getName().startsWith("test")) {
                    System.out.println("Testing " + method.getName() + "...");
                    method.invoke(testSuite);
                }
            }
        }
    }
    
    public void testVertexQuery() throws Exception {
        setup();
        assertNotNull(graph.addVertex(null).query());
    }
}