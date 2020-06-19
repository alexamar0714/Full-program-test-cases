package amberdb.graph;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class EdgeMapper implements ResultSetMapper<AmberEdgeWithState> {
    
    
    private AmberGraph graph;
    private boolean localOnly; 
    
    
    public EdgeMapper(AmberGraph graph, boolean localOnly) {
        this.graph = graph;
        this.localOnly = localOnly;
    }
    
    
    public AmberEdgeWithState map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        Long inId = rs.getLong("v_in");
        Long outId = rs.getLong("v_out");
        
        AmberVertex in = (AmberVertex) graph.getVertex(inId, localOnly);
        AmberVertex out = (AmberVertex) graph.getVertex(outId, localOnly);

        if (in == null) in = (AmberVertex) graph.removedVertices.get(inId);
        if (out == null) out = (AmberVertex) graph.removedVertices.get(outId);

        AmberEdge edge = new AmberEdge(
                rs.getLong("id"), 
                rs.getString("label"),
                (out == null) ? new AmberVertex(0, null, graph, 0L, 0L) : out, 
                (in == null) ? new AmberVertex(0, null, graph, 0L, 0L) : in,
                null,
                graph,
                rs.getLong("txn_start"),
                rs.getLong("txn_end"),
                rs.getInt("edge_order"));
        return new AmberEdgeWithState(edge, rs.getString("state"));
    }
}
