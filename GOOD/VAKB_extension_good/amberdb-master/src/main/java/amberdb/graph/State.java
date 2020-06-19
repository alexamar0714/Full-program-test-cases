package amberdb.graph;


public enum State {

    
    NEW, MOD, DEL, AMB, BAD;
    
    
    public static State forCode(String s) {
        if (s.equals(State.NEW.toString())) return State.NEW;
        if (s.equals(State.MOD.toString())) return State.MOD;
        if (s.equals(State.DEL.toString())) return State.DEL;
        if (s.equals(State.AMB.toString())) return State.AMB;
        return State.BAD;
    }
}
