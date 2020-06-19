package amberdb.graph;


public class AmberVertexWithState {

    public State state;
    public AmberVertex vertex;

    
    public AmberVertexWithState(AmberVertex vertex, State state) {
        this.vertex = vertex;
        this.state = state;
    }

    public AmberVertexWithState(AmberVertex vertex, String state) {
        this.vertex = vertex;
        this.state = State.forCode(state);
    }

}
