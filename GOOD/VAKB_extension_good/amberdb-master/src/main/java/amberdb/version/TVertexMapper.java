package amberdb.version;


import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import amberdb.graph.AmberVertexMapper;
import amberdb.graph.dao.AmberDao;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;


public class TVertexMapper implements ResultSetMapper<TVertex> {

    public TVertex map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        
        TVertex vertex = new TVertex(
                new TId(
                        rs.getLong("id"), 
                        rs.getLong("txn_start"), 
                        rs.getLong("txn_end"),
                        rs.getLong("time")),
                null);
        Map<String, Object> properties = vertex.getProperties();
        ResultSetMetaData metadata = rs.getMetaData();
        int numColumns = metadata.getColumnCount();
        for(int column = 1; column <= numColumns; column++) {
            String label = metadata.getColumnLabel(column);
            if (!AmberVertexMapper.skipProps.contains(label)) {
                Object o = rs.getObject(column);
                if (o != null) {
                    if (o instanceof Clob) {
                        Clob clob = (Clob) o;
                        o = clob.getSubString(1,  (int) clob.length());
                    }
                    if (AmberDao.fieldMappingReverse.containsKey(label)) {
                        label = AmberDao.fieldMappingReverse.get(label);
                    }
                    properties.put(label, o);
                }
            }
        }
        return vertex;
    }
}
