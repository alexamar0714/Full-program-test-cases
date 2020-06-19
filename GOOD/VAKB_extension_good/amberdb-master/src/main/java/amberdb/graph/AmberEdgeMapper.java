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


public class AmberEdgeMapper implements ResultSetMapper<AmberEdge>  {

    
    private AmberGraph graph;
    private boolean localOnly; 
    
    public static Set<String> skipProps = new HashSet<>();
    static {
    	skipProps.add("id");
        skipProps.add("label");
    	skipProps.add("txn_start");
    	skipProps.add("txn_end");
        skipProps.add("v_in");
        skipProps.add("v_out");
        skipProps.add("edge_order");
    	skipProps.add("state");
    	skipProps.add("s_id");
    }

    
    public AmberEdgeMapper(AmberGraph graph, boolean localOnly) {
        this.graph = graph;
        this.localOnly = localOnly;
    }

    @Override
    public AmberEdge map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        
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
                (int) rs.getLong("edge_order"));
        
        Map<String, Object> properties = edge.getProperties();
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
        return edge;
    }
}
