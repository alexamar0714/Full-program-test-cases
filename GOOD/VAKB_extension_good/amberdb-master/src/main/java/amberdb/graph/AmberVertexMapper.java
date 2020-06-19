package amberdb.graph;


import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import amberdb.graph.dao.AmberDao;


public class AmberVertexMapper implements ResultSetMapper<AmberVertex>  {

    
    private AmberGraph graph;
    
    public static Set<String> skipProps = new HashSet<>();
    static {
    	skipProps.add("id");
    	skipProps.add("txn_start");
    	skipProps.add("txn_end");
    	skipProps.add("state");
		skipProps.add("step");
		skipProps.add("vid");
		skipProps.add("eid");
		skipProps.add("label");
		skipProps.add("edge_order");
		skipProps.add("s_id");
	}

    
    public AmberVertexMapper(AmberGraph graph) {
        this.graph = graph;
    }

    @Override
    public AmberVertex map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        
        AmberVertex vertex = new AmberVertex(
                rs.getLong("id"), 
                null,
                graph,
                rs.getLong("txn_start"),
                rs.getLong("txn_end"));
        Map<String, Object> properties = vertex.getProperties();
        ResultSetMetaData metadata = rs.getMetaData();
        int numColumns = metadata.getColumnCount();
        for(int column = 1; column <= numColumns; column++) {
        	String label = metadata.getColumnLabel(column);
        	if (!skipProps.contains(label)) {
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
