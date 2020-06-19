package amberdb.graph;


public class AmberEdgeWithState {

    
    public State state;
    public AmberEdge edge;
    
    public AmberEdgeWithState(AmberEdge edge, State state) {
        this.edge = edge;
        this.state = state;
    }
    
    public AmberEdgeWithState(AmberEdge edge, String state) {
        this.edge = edge;
        this.state = State.forCode(state);
    }
}
