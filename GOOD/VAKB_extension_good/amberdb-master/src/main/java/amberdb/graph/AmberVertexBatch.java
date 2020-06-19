package amberdb.graph;


import java.util.ArrayList;
import java.util.List;


public class AmberVertexBatch {
    
    
    List<Long>   id       = new ArrayList<Long>();
    List<Long>   txnStart = new ArrayList<Long>();
    List<Long>   txnEnd   = new ArrayList<Long>();
    List<String> state    = new ArrayList<String>();
    
    void add(AmberVertexWithState statefulVertex) {
        AmberVertex vertex = statefulVertex.vertex;
        
        id.add((Long) vertex.getId());
        txnStart.add(vertex.txnStart);
        txnEnd.add(vertex.txnEnd);
        state.add(statefulVertex.state.name());
    }


    public String contents() {
        StringBuilder s = new StringBuilder();
        for (int i=0; i < id.size(); i++) {
            s.append("vertex:" + id.get(i) + " " + state.get(i) + "\n");
        }
        return s.toString();
    }


    public void clear() {
        id.clear();
        txnStart.clear();
        txnEnd.clear();
        state.clear();
    }
}
