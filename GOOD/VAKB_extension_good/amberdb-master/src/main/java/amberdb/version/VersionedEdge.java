package amberdb.version;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;


public class VersionedEdge {

    
    List<TEdge> edges = new ArrayList<>();
    VersionedGraph graph;
    
    public VersionedEdge(Set<TEdge> edges, VersionedGraph graph) {
        validate(edges);
        this.edges.addAll(edges);
        Collections.sort(this.edges, new Comparator<TEdge>() {
            public int compare(TEdge e1, TEdge e2) {
                return e1.id.compareTo(e2.id);
            }
        });
        this.graph = graph;
    }
    
    
    public Long getId() {
        return edges.get(0).id.id;
    }
    
    
    public TEdge getAtTxn(Long txn) {
        for (TEdge e : edges) {
            if (e.id.start <= txn && (e.id.end > txn || e.id.end == 0)) return e;
        }
        return null;
    }
    
    
    public TEdge getCurrent() {
        for (TEdge e : edges) {
            if (e.id.end == 0) return e; 
        }
        return null;
    }
    

    public TEdge getFirst() {
        long start = Long.MAX_VALUE;
        TEdge firstEdge = null;
        for (TEdge e : edges) {
            if (e.id.start < start) firstEdge = e;
        }
        return firstEdge;
    }

    
    private void validate(Set<TEdge> edges) {
        if (edges == null || edges.isEmpty())
            throw new RuntimeException("Cannot create VersionedEdge as edges set is null or empty");
        Long id = edges.iterator().next().id.id;
        boolean sameIds = true;
        int current = 0;
        for (TEdge e : edges) {
            if (!e.id.id.equals(id)) sameIds = false;
            if (e.id.end.equals(0L)) current++;
        }
        if (!sameIds || current > 1) {
            this.edges.addAll(edges);
            throw new RuntimeException("Edges supplied to VersionedEdge are bad: " + toString());
        }    
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VERSIONED EDGE\n");
        for (TEdge e : edges) {
            sb.append("\t" + e + "\n");
        }
        return sb.toString();
    }
    
    
    public VersionedVertex getVertex(Direction direction) throws IllegalArgumentException {
        // argument guard
        if (Direction.BOTH == direction) {  
            throw new IllegalArgumentException("Can only get a vertex from a single direction"); 
        }
        if (direction == Direction.IN) {
            return graph.graphVertices.get(edges.get(0).inId);
        } 
        return graph.graphVertices.get(edges.get(0).outId); // direction must be out
        // note: we should always be loading an edge's vertices
    }
    
    
    public TEdgeDiff getDiff(Long txn1, Long txn2) {
        TEdge e1 = getAtTxn(txn1);
        TEdge e2 = getAtTxn(txn2);
        
        return new TEdgeDiff(txn1, txn2, e1,e2);
    }
    
    
    public List<TEdge> getTEdges() {
        return edges;
    }
    
    public static TEdge findFirstEdgeByTransactionId(Iterable<VersionedEdge> versionedEdges, Long transactionId) {
        for (VersionedEdge versionedEdge : versionedEdges){
            TEdge edge = versionedEdge.getAtTxn(transactionId);
            if (edge != null){
                return edge;
            }
        }
        return null;
    }
    
}
