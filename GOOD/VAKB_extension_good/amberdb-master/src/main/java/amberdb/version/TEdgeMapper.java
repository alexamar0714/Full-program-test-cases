package amberdb.version;


import amberdb.graph.AmberEdgeMapper;
import amberdb.graph.dao.AmberDao;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;


public class TEdgeMapper implements ResultSetMapper<TEdge> {

    public TEdge map(int index, ResultSet rs, StatementContext ctx)
            throws SQLException {

        TEdge edge = new TEdge(
                new TId(
                        rs.getLong("id"),
                        rs.getLong("txn_start"), 
                        rs.getLong("txn_end"),
                        rs.getLong("time")),
                rs.getString("label"), 
                rs.getLong("v_out"), 
                rs.getLong("v_in"), 
                null, 
                rs.getInt("edge_order"));

        Map<String, Object> properties = edge.getProperties();
        ResultSetMetaData metadata = rs.getMetaData();
        int numColumns = metadata.getColumnCount();
        for(int column = 1; column <= numColumns; column++) {
            String label = metadata.getColumnLabel(column);
            if (!AmberEdgeMapper.skipProps.contains(label)) {
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
        return edge;
    }
}
