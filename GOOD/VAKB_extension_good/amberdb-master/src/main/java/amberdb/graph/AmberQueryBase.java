package amberdb.graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class AmberQueryBase {

    protected static final String SESS_VERTEX_QUERY_PREFIX = "select * \n" +
            " from sess_node \n" +
            " left join sess_work        on        sess_work.id = sess_node.id and sess_work.s_id = sess_node.s_id \n" +
            " left join sess_file        on        sess_file.id = sess_node.id and sess_file.s_id = sess_node.s_id \n" +
            " left join sess_description on sess_description.id = sess_node.id and sess_description.s_id = sess_node.s_id \n" +
            " left join sess_party       on       sess_party.id = sess_node.id and sess_party.s_id = sess_node.s_id \n" +
            " left join sess_tag         on         sess_tag.id = sess_node.id and sess_tag.s_id = sess_node.s_id \n";

    protected static final String VERTEX_QUERY_PREFIX = "select * \n" +
                "from node \n" +
                "left join work        on        work.id = node.id \n" +
                "left join file        on        file.id = node.id \n" +
                "left join description on description.id = node.id \n" +
                "left join party       on       party.id = node.id \n" +
                "left join tag         on         tag.id = node.id \n";

    protected static final String EDGE_QUERY_PREFIX = "select * \n" +
            "from flatedge \n" +
            "left join acknowledge on flatedge.id = acknowledge.id \n";

    protected static final String SESS_EDGE_QUERY_PREFIX = "select * \n"
            + " from sess_flatedge f \n"
            + " left join sess_acknowledge on sess_acknowledge.id = f.id and sess_acknowledge.s_id = f.s_id \n";

    
    /** The graph associated with this query */
    protected AmberGraph graph;
    
    
    public AmberQueryBase(AmberGraph graph) {
        this.graph = graph;
    }
    
    /**
     * getElementPropertyMaps: this method is required to provide compatibility for service that extends AmberQueryBase.
     */
    protected Map<Long, Map<String, Object>> getElementPropertyMaps(Handle h, String elementIdTable, String elementIdColumn) {
        return getVertexPropertyMaps(h, elementIdTable, elementIdColumn);
    }
    
    protected Map<Long, Map<String, Object>> getVertexPropertyMaps(Handle h, String elementIdTable, String elementIdColumn) {
        
        List<AmberVertex> vertices = h.createQuery(
                String.format("%1$s inner join %2$s on %2$s.%3$s = node.id %n", VERTEX_QUERY_PREFIX, elementIdTable, elementIdColumn)).map(new AmberVertexMapper(graph)).list();

        Map<Long, Map<String, Object>> propertyMaps = new HashMap<Long, Map<String, Object>>();
        for (AmberVertex vertex : vertices) {
            propertyMaps.put((Long) vertex.getId(), vertex.getProperties());
        }
        return propertyMaps;
    }

    protected Map<Long, Map<String, Object>> getEdgePropertyMaps(Handle h, String elementIdTable, String elementIdColumn) {
        
        List<AmberEdge> edges = h.createQuery(
                String.format("%1$s inner join %2$s on %2$s.%3$s = flatedge.id %n", EDGE_QUERY_PREFIX, elementIdTable, elementIdColumn)).map(new AmberEdgeMapper(graph, false)).list();

        Map<Long, Map<String, Object>> propertyMaps = new HashMap<Long, Map<String, Object>>();
        for (AmberEdge edge : edges) {
            propertyMaps.put((Long) edge.getId(), edge.getProperties());
        }
        return propertyMaps;
    }
    
        
    protected List<Vertex> getVertices(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps,
            String vertexIdTable, String vertexIdColumn, String vertexOrderColumn) {

        List<Vertex> vertices = new ArrayList<>();
        List<AmberVertexWithState> wrappedVertices = h.createQuery(String.format(
                "SELECT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + " FROM node v, %1$s "
                + " WHERE "
                + " v.id = %1$s.%2$s "
                + " ORDER BY %1$s.%3$s, %1$s.%2$s",
                vertexIdTable, vertexIdColumn, vertexOrderColumn))
                .map(new AmberVertexWithStateMapper(graph)).list();

        // add them to the graph
        for (AmberVertexWithState wrapper : wrappedVertices) {
            AmberVertex vertex = wrapper.vertex; 

            if (graph.removedVertices.containsKey(vertex.getId())) {
                continue;
            }
            if (graph.graphVertices.containsKey(vertex.getId())) {
                vertices.add(graph.graphVertices.get(vertex.getId()));
            } else {
                vertex.replaceProperties(propMaps.get((Long) vertex.getId()));
                graph.addVertexToGraph(vertex);
                vertices.add(vertex);
            }
        } 
        return vertices;
    }
    

    protected List<Vertex> getVertices(List<AmberVertex> amberVertices) {
    	List<Vertex> vertices = new ArrayList<>();
    	for(AmberVertex amberVertex: amberVertices) {
            if (graph.removedVertices.containsKey(amberVertex.getId())) {
                continue;
            }
            if (graph.graphVertices.containsKey(amberVertex.getId())) {
                vertices.add(graph.graphVertices.get(amberVertex.getId()));
            } else {
                graph.addVertexToGraph(amberVertex);
                vertices.add(amberVertex);
            }
    	}
		return vertices;
	}

    

    protected List<Edge> getEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps, 
            String edgeIdTable, String edgeIdColumn) {

        List<Edge> edges = new ArrayList<>();
        List<AmberEdgeWithState> wrappedEdges = h.createQuery(String.format(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + " FROM flatedge e, %1$s "
                + " WHERE "
                + " e.id = %1$s.%2$s",
                edgeIdTable, edgeIdColumn))
                .map(new EdgeMapper(graph, true)).list();
        
        // don't add them to the graph
        for (AmberEdgeWithState wrapper : wrappedEdges) {

            if (wrapper == null) { // if either vertex doesn't exist 
                continue;
            }
            AmberEdge edge = wrapper.edge; 
            Long edgeId = (Long) edge.getId();
            
            if (graph.graphEdges.containsKey(edgeId) || graph.removedEdges.containsKey(edgeId)) {
                continue;
            } 
            edge.replaceProperties(propMaps.get(edgeId));
            graph.addEdgeToGraph(edge);
            edges.add(edge);
        }        
        return edges;
    }
    
    protected List<Edge> getEdges(List<AmberEdge> amberEdges) {
        List<Edge> edges = new ArrayList<>();
        for(AmberEdge amberEdge: amberEdges) {
            if (graph.removedEdges.containsKey(amberEdge.getId())) {
                continue;
            }
            if (graph.graphEdges.containsKey(amberEdge.getId())) {
                edges.add(graph.graphEdges.get(amberEdge.getId()));
            } else {
                graph.addEdgeToGraph(amberEdge);
                edges.add(amberEdge);
            }
        }
        return edges;
    }


    
    protected void getFillEdges(Handle h , AmberGraph graph, Map<Long, Map<String, Object>> propMaps,
            String inVertexIdTable, String inVertexIdColumn, String outVertexIdTable, String outVertexIdColumn) {

        List<AmberEdgeWithState> wrappedEdges = h.createQuery(String.format(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, 'AMB' state "
                + " FROM flatedge e, %1$s, %3$s "
                + " WHERE "
                + "     e.v_in = %1$s.%2$s "
                + " AND e.v_out = %3$s.%4$s ",
                inVertexIdTable, inVertexIdColumn, outVertexIdTable, outVertexIdColumn))
                .map(new EdgeMapper(graph, true)).list();
        
        // add them to the graph
        for (AmberEdgeWithState wrapper : wrappedEdges) {

            if (wrapper == null) { // if either vertex doesn't exist 
                continue;
            }
            AmberEdge edge = wrapper.edge; 
            Long edgeId = (Long) edge.getId();
            
            if (graph.graphEdges.containsKey(edgeId) || graph.removedEdges.containsKey(edgeId)) {
                continue;
            } 
            edge.replaceProperties(propMaps.get(edgeId));
            graph.addEdgeToGraph(edge);
        }        
    }
    
    
    protected <T> String numberListToStr(List<T> numbers) {
        StringBuilder s = new StringBuilder();
        for (T n : numbers) {
            s.append(n).append(',');
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    
    protected String strListToStr(List<String> strs) {
        StringBuilder s = new StringBuilder();
        for (String str : strs) {
            // dumbass sql injection protection (not real great)
            s.append("'" + str.replaceAll("'", "\\'") + "',");
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    
    protected String generateLabelsClause(List<String> labels) {
        if (labels == null || labels.size() == 0) return "";
        return " AND e.label IN (" + strListToStr(labels) + ") \n"; 
    }
}
