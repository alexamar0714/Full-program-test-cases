package amberdb.graph;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;


public class AmberGraphDaoTest {

    public AmberGraph graph;
    
    @Before
    public void setup() throws MalformedURLException, IOException {
        graph = new AmberGraph();
    }

    @After
    public void teardown() {}

    @Test
    public void updateEdgeOrder() throws Exception {

        Vertex vp = graph.addVertex(null);
        
        Vertex v1 = graph.addVertex(null); 
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        Vertex v4 = graph.addVertex(null);

        Edge e1 = graph.addEdge(null, v1, vp, "isPartOf");
        Edge e2 = graph.addEdge(null, v2, vp, "isPartOf");
        Edge e3 = graph.addEdge(null, v3, vp, "isPartOf");
        Edge e4 = graph.addEdge(null, v4, vp, "isPartOf");
        
        ((AmberVertex) vp).setEdgeOrder(v1, "isPartOf", Direction.IN, 1);
        ((AmberVertex) vp).setEdgeOrder(v2, "isPartOf", Direction.IN, 2);
        ((AmberVertex) vp).setEdgeOrder(v3, "isPartOf", Direction.IN, 3);
        ((AmberVertex) vp).setEdgeOrder(v4, "isPartOf", Direction.IN, 4);
        
        assertEquals(new Integer(1), (Integer) e1.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(new Integer(2), (Integer) e2.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(new Integer(3), (Integer) e3.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(new Integer(4), (Integer) e4.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));

        ((AmberVertex) vp).setEdgeOrder(v1, "isPartOf", Direction.IN, 4);
        ((AmberVertex) vp).setEdgeOrder(v2, "isPartOf", Direction.IN, 3);
        ((AmberVertex) vp).setEdgeOrder(v3, "isPartOf", Direction.IN, 2);
        ((AmberVertex) vp).setEdgeOrder(v4, "isPartOf", Direction.IN, 1);
        
        assertEquals(new Integer(4), (Integer) e1.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(new Integer(3), (Integer) e2.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(new Integer(2), (Integer) e3.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        assertEquals(new Integer(1), (Integer) e4.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
    }
    
    public void s(String s) {
        System.out.println(s);
    }
}
