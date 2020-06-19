package amberdb.graph;


import java.util.ArrayList;
import java.util.List;
import static amberdb.graph.State.DEL;


public class AmberEdgeBatch {

    
    List<Long>    id        = new ArrayList<Long>();
    List<Long>    txnStart  = new ArrayList<Long>();
    List<Long>    txnEnd    = new ArrayList<Long>();
    List<Long>    vertexOut = new ArrayList<Long>();
    List<Long>    vertexIn  = new ArrayList<Long>();
    List<String>  label     = new ArrayList<String>();
    List<Integer> order     = new ArrayList<Integer>();
    List<String>  state     = new ArrayList<String>();
    
    
    void add(AmberEdgeWithState wrapper) {
        AmberEdge edge = wrapper.edge;
        State state = wrapper.state;
        
        id.add((Long) edge.getId());
        txnStart.add(edge.txnStart);
        txnEnd.add(edge.txnEnd);
        
        if (state != null && state == DEL) {
            vertexOut.add(null);
            vertexIn.add(null);
        } else {
            vertexOut.add((Long) edge.outId);
            vertexIn.add((Long) edge.inId);
        }
        label.add(edge.getLabel());
        order.add(edge.order);
        this.state.add(state == null? null : state.name());
    }
    
    
    public String contents() {
        StringBuilder s = new StringBuilder();
        for (int i=0; i < id.size(); i++) {
            s.append("edge:" + id.get(i) + " " + label.get(i) + " out:" 
                    + vertexOut.get(i) + " in:" + vertexIn.get(i) + " " 
                    + state.get(i) + "\n");
        }
        return s.toString();
    }


    public void clear() {
        id.clear();
        txnStart.clear();
        txnEnd.clear();
        vertexOut.clear();
        vertexIn.clear();
        label.clear();
        order.clear();
        state.clear();
    }
}
