package amberdb.graph;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.tinkerpop.blueprints.Vertex;

import amberdb.graph.dao.AmberDao;


public class AmberVertexQuery extends AmberQueryBase {
    
    // limit the number of vertices returned by a query without criteria
    static final int MAX_VERTICES = 10000; 
    
    List<AmberProperty> properties = new ArrayList<AmberProperty>();
    /* whether to combine criteria using 'and' or 'or' */
    private boolean combineWithOr = true; // default to 'or' 
    
    
    public void combineCriteriaWithOr() {
        combineWithOr = true;
    }
    
    
    public void combineCriteriaWithAnd() {
        combineWithOr = false;
    }


    public AmberVertexQuery(AmberGraph graph) {
        super(graph);
    }
    

    /**
     * Add criteria of the form (property) name = value to the query. Notes:
     * Criteria with a null value will be ignored. Multi-value, json encoded
     * criteria will only match unreliably and probably shouldn't be used.
     * 
     * @param name
     *            The name of a property
     * @param value
     *            The value the property must be for a vertex to meet the
     *            criteria
     */
    public void addCriteria(String name, Object value) {
        
        // guards
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Property must have a name");
        
        AmberProperty ap = new AmberProperty(0, name, value);
        this.properties.add(ap);
    }

    
    /**
     * Add Property criteria to the query. Notes: Criteria with a null value
     * will be ignored. Multi-value, json encoded criteria will only match
     * unreliably and shouldn't be used for criteria.
     * 
     * @param properties A list of criteria properties
     */
    public void addCriteria(List<AmberProperty> properties) {
        
        // guards
        if (properties == null) throw new IllegalArgumentException("Property query must specify properties");
        properties.removeAll(Collections.singleton(null));
        if (properties.size() == 0) throw new IllegalArgumentException("Property query must specify properties");            
        
        this.properties.addAll(properties);
    }


    /**
     * Add Property criteria to the query. Notes: Criteria with a null value
     * will be ignored. Multi-value, json encoded criteria will only match
     * unreliably and shouldn't be used for criteria.
     * 
     * @param properties An array of criteria properties
     */
    public void addCriteria(AmberProperty... properties) {

        // guards
        if (properties == null) throw new IllegalArgumentException("Property query must specify properties");
        List<AmberProperty> props = Arrays.asList(properties);
        props.removeAll(Collections.singleton(null));
        if (props.size() == 0) throw new IllegalArgumentException("Property query must specify properties");            
        
        this.properties.addAll(props);
    }
    
    
    protected String generateAndQuery() {
        StringBuilder s = new StringBuilder(VERTEX_QUERY_PREFIX + "where \n");
        
        for (int i = 0; i < properties.size(); i++) {
        	String columnName = properties.get(i).getName();
        	if (AmberDao.nodeFields.contains(columnName)) {
        	    columnName = "node." + columnName;
        	}
        	s.append(columnName + " = :value"+ i + " \n and ");
        }
        s.setLength(s.length()-4);
        return s.toString();
    }
    

    protected String generateOrQuery() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < properties.size(); i++) {
        	String columnName = properties.get(i).getName();
            if (AmberDao.nodeFields.contains(columnName)) {
                columnName = "node." + columnName;
            }
            s.append(VERTEX_QUERY_PREFIX +
            		"where " + columnName + " = :value"+ i + " \n"
                    + "UNION ALL \n");
        }
        s.setLength(s.length()-13);
        s.append(";\n");
        return s.toString();
    }
    

    protected String generateAllQuery() {
        
        StringBuilder s = new StringBuilder();
        s.append(VERTEX_QUERY_PREFIX +
                "LIMIT " + MAX_VERTICES);
        return s.toString();        
    }
    
    
    protected String generateQuery() {
        
        if (properties.size() == 0) {
            return generateAllQuery();
        }
        
        if (combineWithOr) {
            return generateOrQuery();
        } else {
            return generateAndQuery();        
        }
    }
    
    
    public List<Vertex> execute() {
        try (Handle h = graph.dbi().open()) {
            h.begin();
            String sql = generateQuery();
            Query<Map<String, Object>> q = h.createQuery(sql);
            for (int i = 0; i < properties.size(); i++) {
                q.bind("value"+i, properties.get(i).getValue());
            }
            return getVertices(q.map(new AmberVertexMapper(graph)).list());
        }
    }
 
	public List<Vertex> executeJsonValSearch(String name, String value) {
        // quote any characters that could cause issues with the regex matching - assumes we are performing an exact match on value
        for (String quote : Arrays.asList(".", "(", ")", "[", "]", "{", "}")) {
            value = value.replace(quote, "\\" + quote);
        }

        String sql = VERTEX_QUERY_PREFIX + "where ";
        if (AmberDao.nodeFields.contains(name)) {
            sql += "node.";
        }
        sql += name + " regexp :value";
        
        try (Handle h = graph.dbi().open()) {
            Query<Map<String, Object>> q = h.begin().createQuery(sql).bind("value", value);
            return getVertices(q.map(new AmberVertexMapper(graph)).list());
        }
    }

	
    public List<Vertex> executeAliasSearch(String value) {
        String sql = "select * from node left join alternate_pi on alternate_pi.obj_id = node.id where alternate_pi.legacy_id = :value";
        try (Handle h = graph.dbi().open()) {
            Query<Map<String, Object>> q = h.begin().createQuery(sql).bind("value", value);
            return getVertices(q.map(new AmberVertexMapper(graph)).list());
        }
    }
}
