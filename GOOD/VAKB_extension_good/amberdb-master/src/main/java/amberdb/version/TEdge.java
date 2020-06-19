package amberdb.version;


import java.util.Map;


public class TEdge extends TElement implements Comparable<TEdge> {

    
    private String label;
    protected Long inId;
    protected Long outId;
    
    Integer order;

    public static final String SORT_ORDER_PROPERTY_NAME = "edge-order";
    
    public TEdge(TId id, String label, Long outId, Long inId, Map<String, Object> properties, Integer order) {
        super(id, properties); //, graph);
        this.label = label;
        this.inId = inId;
        this.outId = outId;
        this.order = order;
    }

    
    public String getLabel() {
        return label;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EDGE id:[").append(id).append(']')
        .append(" label:").append(label)
        .append(" out:").append(outId)
        .append(" in:").append(inId)
        .append(" order:").append(order);
        sb.append(propertiesAsString());
        sb.append(" hash:"+hashCode());
        return sb.toString();
    }

    
    @Override
    public int hashCode() {
        return ((TId) id).hashCode();
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof TEdge) {
            return id.equals(((TEdge) obj).getId());
        }
        return false;
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(String propertyName) {
        
        // get special sorting property
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            return (T) order;
        }
        return super.getProperty(propertyName);
    }

    public Long getOutId() {
        return outId;
    }

    public Long getInId() {
        return inId;
    }

    @Override
    public int compareTo(TEdge o) {
        return order - ((TEdge) o).order;
    }
}
