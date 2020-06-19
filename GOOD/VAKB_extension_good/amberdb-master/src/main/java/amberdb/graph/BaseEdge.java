package amberdb.graph;


import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class BaseEdge extends BaseElement implements Edge {

    
    private String label;
    protected Long inId;
    protected Long outId;
    
    
    public BaseEdge(Long id, String label, Long outId, Long inId, Map<String, Object> properties, BaseGraph graph) {
        super(id, properties, graph);
        this.label = label;
        this.inId = inId;
        this.outId = outId;
    }

    
    @Override
    public Object getId() {
        return id;
    }

    
    @Override
    public void remove() {
        graph.removeEdge(this);
    }

    
    @Override
    public String getLabel() {
        return label;
    }

    
    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        // argument guard
        if (Direction.BOTH == direction) {  
            throw new IllegalArgumentException("Can only get a vertex from a single direction"); 
        }
        if (direction == Direction.IN) {
            return (Vertex) graph.graphVertices.get(inId);
        } 
        return (Vertex) graph.graphVertices.get(outId); // direction must be out
        // note: we should always be loading an edge's vertices
    }

 
    @Override
    public void setProperty(String propertyName, Object value) {
        // argument guards
        if (propertyName == null || propertyName.matches("(?i)id|\\s*|label")) {
            throw new IllegalArgumentException("Illegal property name [" + propertyName + "]");
        }
        super.setProperty(propertyName, value);
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("edge id:").append(id)
        .append(" label:").append(label)
        .append(" out:").append(outId)
        .append(" in:").append(inId);
        // properties
        sb.append(" {");
        if (properties != null && properties.size() > 0) {
            for (String key : properties.keySet()) {
                sb.append(key).append(':').append(properties.get(key)).append(", ");
            }
            sb.setLength(sb.length()-2);
        }
        sb.append('}');
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
        if (!(obj instanceof BaseEdge)) return false;
        BaseEdge other = (BaseEdge) obj;
        if (id != (Long) other.getId()) return false;
        return true;
    }
}
