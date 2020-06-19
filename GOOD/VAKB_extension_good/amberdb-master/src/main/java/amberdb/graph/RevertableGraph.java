package amberdb.graph;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public interface RevertableGraph {
    void revertVertex(Vertex v);

    void revertEdge(Edge e);
}
