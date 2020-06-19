package amberdb.graph;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;


public class BaseVertex extends BaseElement implements Vertex {

    
    public BaseVertex(long id, Map<String, Object> properties, BaseGraph graph) {
        super(id, properties, graph);
        //graph.addVertexToGraph(this);
    }
    
    
    @Override
    public void remove() {
        graph.removeVertex(this);
    }

    
    @Override
    public Edge addEdge(String label, Vertex inVertex) {
        // argument guard
        if (label == null) throw new IllegalArgumentException("edge label cannot be null");
        Edge edge = graph.addEdge(null, this, inVertex, label);
        return (Edge) edge;
    }
    
    
    private Set<Edge> getEdges(Set<Edge> edgeSet, String... labels) {

        // get edges for all labels if none specified
        if (labels.length == 0) { 
            return edgeSet; 
        }

        List<String> labelList = Arrays.asList(labels);
        Set<Edge> edges = new HashSet<Edge>();
        for (Edge e : edgeSet) {
            if (labelList.contains(e.getLabel())) {
                edges.add(e);
            }
        }
        return edges;
    }

    
    @Override
    public Iterable<Edge> getEdges(Direction direction, String... labels) {
        List<Edge> edges = new ArrayList<>();

        if (direction == Direction.IN || direction == Direction.BOTH) {
            edges.addAll(getEdges(graph.outEdgeSets.get(id), labels));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            edges.addAll(getEdges(graph.inEdgeSets.get(id), labels));
        }
        return edges;
    }


    @Override
    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
        
        List<Vertex> vertices = new ArrayList<Vertex>();

        // get the edges
        Iterable<Edge> edges = ((BaseVertex) this).getEdges(direction, labels);
        for (Edge e : edges) {

            if (direction == Direction.IN) {
                vertices.add(e.getVertex(Direction.OUT));
            } else if (direction == Direction.OUT) {
                vertices.add(e.getVertex(Direction.IN));
            } else if (direction == Direction.BOTH) {
                if (e.getVertex(Direction.IN) == (Vertex) this) {
                    vertices.add(e.getVertex(Direction.OUT));
                } else {
                    vertices.add(e.getVertex(Direction.IN));
                }
            }
        } 
        return vertices;
    }

    
    @Override
    public VertexQuery query() {
        return new DefaultVertexQuery(this);
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("vertex id:").append(id);
        // properties
        sb.append(" {");
        if (properties.size() > 0) {
            for (String key : properties.keySet()) {
                sb.append(key).append(':').append(properties.get(key)).append(", ");
            }
            sb.setLength(sb.length()-2);
        }
        sb.append('}');
        sb.append("-"+hashCode());
        return sb.toString();
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Long) id).hashCode();
        return result;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof BaseVertex)) return false;
        BaseVertex other = (BaseVertex) obj;
        if (id != (Long) other.getId()) return false;
        return true;
    }
}
