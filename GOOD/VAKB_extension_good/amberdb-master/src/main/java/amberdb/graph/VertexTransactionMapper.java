package amberdb.graph;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class VertexTransactionMapper implements ResultSetMapper<AmberVertexTransaction> {
    
    
    public AmberVertexTransaction map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        return new AmberVertexTransaction(
                rs.getLong("transaction_id"), 
                rs.getLong("time"), 
                rs.getString("user"),
                rs.getString("operation"),
                rs.getLong("vertex_id"));
    }
}
