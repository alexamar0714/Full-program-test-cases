package amberdb.version;


import java.util.Map;


public class TVertex extends TElement {

    
    public TVertex(TId id, Map<String, Object> properties) {
        super(id, properties); //, graph);
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VERTEX id:[").append(id).append(']');
        sb.append(propertiesAsString());
        sb.append(" hash:"+hashCode());
        return sb.toString();
    }

    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof TVertex) {
            return id.equals(((TVertex) obj).getId());
        }
        return false;
    }
}
