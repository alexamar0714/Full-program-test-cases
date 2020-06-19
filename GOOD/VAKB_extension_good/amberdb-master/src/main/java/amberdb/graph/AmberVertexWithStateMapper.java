package amberdb.graph;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class AmberVertexWithStateMapper implements ResultSetMapper<AmberVertexWithState> {
    
    private AmberVertexMapper amberVertexMapper;
    
    public AmberVertexWithStateMapper(AmberGraph graph) {
    	amberVertexMapper = new AmberVertexMapper(graph);
    }
    
    @Override
    public AmberVertexWithState map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new AmberVertexWithState(amberVertexMapper.map(index, rs, ctx), rs.getString("state"));
    }
}
