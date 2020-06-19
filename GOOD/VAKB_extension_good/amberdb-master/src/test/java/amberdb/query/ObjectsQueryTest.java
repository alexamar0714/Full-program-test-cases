package amberdb.query;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.graph.AmberGraph;
import com.tinkerpop.blueprints.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ObjectsQueryTest extends AbstractDatabaseIntegrationTest {

    public AmberGraph graph;

    @Before
    public void setup() throws IOException {
        graph = amberSession.getAmberGraph();
    }

    @Test
    public void testGetModifiedObjectIds() throws Exception {
        Vertex v1 = graph.addVertex(null);
        Vertex v2 = graph.addVertex(null);
        Vertex v3 = graph.addVertex(null);
        v1.setProperty("title", "t1");
        v1.setProperty("type", "work");
        v2.setProperty("title", "t2");
        v2.setProperty("type", "work");
        v3.setProperty("title", "t3");
        v3.setProperty("type", "work");

        graph.addEdge(null, v1, v2, "connect");

        graph.commit();

        v1.setProperty("title", "t1.1");
        v2.setProperty("title", "t2.1");

        graph.commit();

        v1.setProperty("title", "t1.2");
        v3.setProperty("bibId", "666");

        graph.commit();

        v2.setProperty("title", "t2.2"); // not committed, so should not appear

        ObjectsQuery query = new ObjectsQuery(graph);
        List<WorkProperty> propertyFilters = new ArrayList<>();
        propertyFilters.add(new WorkProperty("title", "t1.1"));
        propertyFilters.add(new WorkProperty("bibId", "666"));
        ModifiedObjectsBetweenTransactionsQueryRequest request = new ModifiedObjectsBetweenTransactionsQueryRequest(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L), null, propertyFilters, false, 0, 100);

        ModifiedObjectsQueryResponse modifiedObjectIds = query.getModifiedObjectIds(request);

        assertEquals("v1 and v3 should be returned", 2L, modifiedObjectIds.getResultSize());
        assertTrue("v1 should be returned because its title was t1.1 at some point during the transaction range", modifiedObjectIds.getModifiedObjects().containsKey(v1.getId()));
        assertTrue("v3 should be returned because its bibId was 666 at some point during the transaction range", modifiedObjectIds.getModifiedObjects().containsKey(v3.getId()));
    }
}
