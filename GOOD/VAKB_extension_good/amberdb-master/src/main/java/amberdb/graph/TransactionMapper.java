package amberdb.graph;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class TransactionMapper implements ResultSetMapper<AmberTransaction> {
    
    
    public AmberTransaction map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        return new AmberTransaction(
                rs.getLong("id"), 
                rs.getLong("time"), 
                rs.getString("user"),
                rs.getString("operation"));
    }
}
