package amberdb.version;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;


public class VersionedVertex {

    
    List<TVertex> vertices = new ArrayList<>();
    VersionedGraph graph;
    
    public VersionedVertex(Set<TVertex> vertices, VersionedGraph graph) {
        validate(vertices);
        this.vertices.addAll(vertices);
        Collections.sort(this.vertices, new Comparator<TVertex>() {
            public int compare(TVertex v1, TVertex v2) {
                return v1.id.compareTo(v2.id);
            }
        });
        this.graph = graph;
    }
    

    public Long getId() {
        return vertices.get(0).id.id;
    }

    
    public TVertex getAtTxn(Long txn) {
        for (TVertex v : vertices) {
            if (v.id.start <= txn && (v.id.end > txn || v.id.end == 0)) return v;
        }
        return null;
    }
    
    
    public TVertex getAtTxnOrLast(Long txn) {
        TVertex v = getAtTxn(txn);
        if (v == null) {
            v = vertices.get(vertices.size()-1);
        }
        return v;
    }
    
    
    public TVertex getCurrent() {
        for (TVertex v : vertices) {
            if (v.id.end.equals(0L)) return v; 
        }
        return null;
    }
    
    
    public TVertexDiff getDiff(Long txn1, Long txn2) {
        TVertex v1 = getAtTxn(txn1);
        TVertex v2 = getAtTxn(txn2);
        
        return new TVertexDiff(txn1, txn2, v1, v2);
    }
    

    public TVertex getFirst() {
        long start = Long.MAX_VALUE;
        TVertex firstVertex = null;
        for (TVertex v : vertices) {
            if (v.id.start < start) firstVertex = v;
        }
        return firstVertex;
    }

    
    private void validate(Set<TVertex> vertices) {        
        if (vertices == null || vertices.isEmpty())
            throw new RuntimeException("Cannot create VersionedVertex as vertices set is null or empty");
        Long id = vertices.iterator().next().id.id;
        boolean sameIds = true;
        int current = 0;
        for (TVertex v : vertices) {
            if (!v.id.id.equals(id)) sameIds = false;
            if (v.id.end.equals(0L)) current++;
        }
        if (!sameIds || current > 1) {
            this.vertices.addAll(vertices);
            throw new RuntimeException("Vertices supplied to VersionedVertex are bad: " + vertices.toString());
        }    
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VERSIONED VERTEX\n");
        for (TVertex v : vertices) {
            sb.append("\t" + v + "\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    
    private Set<VersionedEdge> getEdges(Set<VersionedEdge> edgeSet, String... labels) {

        // get edges for all labels if none specified
        if (labels.length == 0) { 
            return edgeSet; 
        }

        List<String> labelList = Arrays.asList(labels);
        Set<VersionedEdge> edges = new HashSet<>();
        for (VersionedEdge e : edgeSet) {
            for (TEdge ve : e.edges) {
                if (labelList.contains(ve.getLabel())) {
                    edges.add(e);
                }
            }
        }
        return edges;
    }

    
    public Iterable<VersionedEdge> getEdges(Direction direction, String... labels) {
        
        VersionedGraph g = (VersionedGraph) graph;
        if (!g.inLocalMode()) g.getBranch(this.getId(), direction, labels);
        
        List<VersionedEdge> edges = new ArrayList<>();
        if (direction == Direction.IN || direction == Direction.BOTH) {
            edges.addAll(getEdges(graph.outEdgeSets.get(this.getId()), labels));
        }
        if (direction == Direction.OUT || direction == Direction.BOTH) {
            edges.addAll(getEdges(graph.inEdgeSets.get(this.getId()), labels));
        }
        return edges;
    }


    public Iterable<VersionedVertex> getVertices(Direction direction, String... labels) {
        
        List<VersionedVertex> vertices = new ArrayList<>();

        // get the edges
        Iterable<VersionedEdge> edges = this.getEdges(direction, labels);
        for (VersionedEdge e : edges) {

            if (direction == Direction.IN) {
                vertices.add(e.getVertex(Direction.OUT));
            } else if (direction == Direction.OUT) {
                vertices.add(e.getVertex(Direction.IN));
            } else if (direction == Direction.BOTH) {
                if (e.getVertex(Direction.IN) == this) {
                    vertices.add(e.getVertex(Direction.OUT));
                } else {
                    vertices.add(e.getVertex(Direction.IN));
                }
            }
        } 
        return vertices;
    }
     
    
    public List<TVertex> getTVertices() {
        return vertices;
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    public boolean equals(Object o) {
        if (o instanceof VersionedVertex) {
            if (this.getId().equals(((VersionedVertex) o).getId())) return true;
        }
        return false;
    }
}
