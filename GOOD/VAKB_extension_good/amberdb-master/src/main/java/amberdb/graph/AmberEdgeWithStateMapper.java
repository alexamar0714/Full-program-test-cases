package amberdb.graph;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class AmberEdgeWithStateMapper implements ResultSetMapper<AmberEdgeWithState> {
    
    private AmberEdgeMapper amberEdgeMapper;
    
    public AmberEdgeWithStateMapper(AmberGraph graph, boolean localOnly) {
        amberEdgeMapper = new AmberEdgeMapper(graph, localOnly);
    }
    
    @Override
    public AmberEdgeWithState map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new AmberEdgeWithState(amberEdgeMapper.map(index, rs, ctx), rs.getString("state"));
    }
}
