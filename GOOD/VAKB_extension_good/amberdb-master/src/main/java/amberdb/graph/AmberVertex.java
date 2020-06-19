package amberdb.graph;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberVertex extends BaseVertex {

    
    Long txnStart;
    Long txnEnd;
    
    
    public AmberVertex(long id, Map<String, Object> properties, 
            AmberGraph graph, Long txnStart, Long txnEnd) {
        
        super(id, properties, graph);
        this.txnStart = txnStart;
        this.txnEnd = txnEnd;
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" start:" ).append(txnStart)
          .append(" end:"   ).append(txnEnd);
        return super.toString() + sb.toString();
    }
    
    
    @SuppressWarnings("unchecked")
    public Iterable<Edge> getEdges(Direction direction, String... labels) {
        AmberGraph g = (AmberGraph) graph;
        if (!g.inLocalMode()) g.getBranch(this.id, direction, labels);
        List<Edge> edges = (List<Edge>) super.getEdges(direction, labels);
        List<AmberEdge> amberEdges = (List<AmberEdge>) (List<? extends Edge>) edges;
        Collections.sort(amberEdges);

        return edges;
    }


    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
        List<Vertex> vertices = (List<Vertex>) super.getVertices(direction, labels);

        return vertices;
    }
    
    
    public AmberGraph getAmberGraph() {
        return (AmberGraph) graph;
    }


    public boolean equals(Object obj) {
        return super.equals(obj) 
                && (obj instanceof AmberVertex); 
    }
    
    
    public List<AmberTransaction> getAllTransactions() {
        return ((AmberGraph) graph).getTransactionsByVertexId(id);
    }
    
    
    public AmberTransaction getLastTransaction() {
        if (txnStart == null || txnStart == 0 ) return null;
        return ((AmberGraph) graph).getTransaction(txnStart);
    }
    
    
    public AmberTransaction getFirstTransaction() {
        return ((AmberGraph) graph).getFirstTransactionForVertexId(id);
    }
    
    
    /**
     * This method is the same as the regular getEdges method, but adds the
     * constraint of the Edges returned being incident to a particular Vertex
     * 
     * @param adjacent
     *            The Vertex on the other end of returned Edges
     * @param direction
     *            The direction for returned Edges (wrt this Vertex)
     * @param labels
     *            The labels returned Edges can have
     * @return A list of conforming edges
     */
    public List<Edge> getEdges(Vertex adjacent, Direction direction, String... labels) {
        List<Edge> edges = new ArrayList<>();
        for (Edge e : this.getEdges(direction, labels)) {
            Vertex v = e.getVertex(Direction.IN);
            if (v.equals(this)) {
                v = e.getVertex(Direction.OUT);
            }
            if (v.getId().equals(adjacent.getId())) { 
                edges.add(e);
            }
        }
        return edges;
    }

    
    /* note: This method can set more than one edge's order */
    public void setEdgeOrder(Vertex adjacent, String label, Direction direction, Integer order) {
        List<Edge> edges = this.getEdges(adjacent, direction, label);
        for (Edge e: edges) {
            e.setProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME, order);
        }
    }

    
    /* note: This method can get more than one edge's order */
    public List<Integer> getEdgeOrder(Vertex adjacent, String label, Direction direction) {
        List<Integer> orders = new ArrayList<>();
        List<Edge> edges = this.getEdges(adjacent, direction, label);
        for (Edge e: edges) {
            orders.add((Integer) e.getProperty(AmberEdge.SORT_ORDER_PROPERTY_NAME));
        }
        return orders;
    }
    
    
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("id", (Long)getId());
        root.put("txnStart", txnStart);
        root.put("txnEnd", txnEnd);

        ObjectNode propertiesNode = mapper.createObjectNode();
        root.put("properties", propertiesNode);
        for (String prop : getPropertyKeys()) {
            propertiesNode.put(prop, (String)getProperty(prop));
        }

        return root.toString();
    }


	public Long getTxnStart() {
		return txnStart;
	}


	public Long getTxnEnd() {
		return txnEnd;
	}
}

