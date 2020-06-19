package amberdb.graph;


import java.util.Map;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;


public interface VertexFactory {
    Vertex newVertex(Object id, Map<String, Object> properties, Graph graph); 
}
