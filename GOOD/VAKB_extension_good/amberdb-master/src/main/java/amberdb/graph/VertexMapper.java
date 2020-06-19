package amberdb.graph;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class VertexMapper implements ResultSetMapper<AmberVertexWithState> {

    
    private AmberGraph graph;

    
    public VertexMapper(AmberGraph graph) {
        this.graph = graph;
    }

    
    public AmberVertexWithState map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        
        AmberVertex vertex = new AmberVertex(
                rs.getLong("id"), 
                null,
                graph,
                rs.getLong("txn_start"),
                rs.getLong("txn_end"));

        return new AmberVertexWithState(vertex, rs.getString("state"));
    }
}
