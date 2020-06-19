package amberdb.graph;

import java.util.List;
import java.util.Map;


@SuppressWarnings("rawtypes")
public class AmberEdge extends BaseEdge implements Comparable {

    
    Long txnStart;
    Long txnEnd;
    Integer order;

    public static final String SORT_ORDER_PROPERTY_NAME = "edge-order";
    
    
    public AmberEdge(Long id, String label, AmberVertex outVertex, 
            AmberVertex inVertex, Map<String, Object> properties, 
            AmberGraph graph, Long txnStart, Long txnEnd, Integer order) {
        
        super(id, label, (Long) outVertex.getId(), (Long) inVertex.getId(), 
                properties, graph);
        this.txnStart = txnStart;
        this.txnEnd = txnEnd;
        this.order = order;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" start:" ).append(txnStart)
          .append(" end:"   ).append(txnEnd)
          .append(" order:" ).append(order);
        return super.toString() + sb.toString();
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

    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T removeProperty(String propertyName) {
        
        // you cannot remove the special sorting
        // property this method just returns it
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            return (T) order;
        }
        return super.removeProperty(propertyName);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        
        if (!(value instanceof Integer) && propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            throw new IllegalArgumentException(SORT_ORDER_PROPERTY_NAME +
                    " property type must be Integer, was [" + value.getClass() + "].");
        }
        
        // set special sorting property
        if (propertyName.equals(SORT_ORDER_PROPERTY_NAME)) {
            order = (Integer) value;
            graph.elementModListener.elementModified(this);
            return;
        }     
        super.setProperty(propertyName, value);
    }
    
    
    @Override
    public int compareTo(Object o) {
        if (o instanceof AmberEdge) {
            return order - ((AmberEdge) o).order;
        }
        return -1;
    }
    

    public boolean equals(Object obj) {
        return super.equals(obj) 
                && (obj instanceof AmberEdge); 
    }
    
    
    public List<AmberTransaction> getAllTransactions() {
        return ((AmberGraph)graph).getTransactionsByEdgeId(id);
    }
    
    
    public AmberTransaction getLastTransaction() {
        if (txnStart == null || txnStart == 0 ) return null;
        return ((AmberGraph) graph).getTransaction(txnStart);
    }
    
    
    public AmberTransaction getFirstTransaction() {
        return ((AmberGraph) graph).getFirstTransactionForEdgeId(id);
    }    
    
    public String toJson() {
        StringBuilder sb = new StringBuilder("{\n");
        sb.append("  \"id\": "        + getId()    + ",\n")
          .append("  \"txnStart\": "  + txnStart   + ",\n")
          .append("  \"txnEnd\": "    + txnEnd     + ",\n")
          .append("  \"label\": "     + getLabel() + ",\n")
          .append("  \"inVertex\": "  + inId       + ",\n")
          .append("  \"outVertex\": " + outId      + ",\n")
          .append("  \"properties\": {\n");
        for (String prop : getPropertyKeys()) {
            sb.append("    \"" + prop + "\": " + getProperty(prop) + "\n");
        }
        sb.append("  }\n}");
        return sb.toString();
    }
    
    public Long getTxnStart() {
        return txnStart;
    }

    public Long getTxnEnd() {
        return txnEnd;
    }

    public Long getInId() {
        return inId;
    }

    public Long getOutId() {
        return outId;
    }

    public Integer getOrder() {
        return order;
    }
}
