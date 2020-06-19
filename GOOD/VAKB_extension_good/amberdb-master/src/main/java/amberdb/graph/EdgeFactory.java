package amberdb.graph;


import java.util.Map;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;


public interface EdgeFactory {
    Edge newEdge(Object id, String label, Vertex inVertex, Vertex outVertex, Map<String, Object> properties, Graph graph); 
}
