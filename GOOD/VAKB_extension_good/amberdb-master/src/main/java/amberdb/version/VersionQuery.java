package amberdb.version;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.skife.jdbi.v2.Handle;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;


public class VersionQuery {

    
    List<Long> head;       
    List<QueryClause> clauses = new ArrayList<QueryClause>();
    private VersionedGraph graph;

    public static final String VERTEX_HISTORY_FROM_TABLE_LIST =
            "node_history \n" +
            "join transaction              on node_history.txn_start = transaction.id\n" +
            "left join work_history        on        work_history.id = node_history.id and        work_history.txn_start = node_history.txn_start and        work_history.txn_end = node_history.txn_end \n" +
            "left join file_history        on        file_history.id = node_history.id and        file_history.txn_start = node_history.txn_start and        file_history.txn_end = node_history.txn_end \n" +
            "left join description_history on description_history.id = node_history.id and description_history.txn_start = node_history.txn_start and description_history.txn_end = node_history.txn_end \n" +
            "left join party_history       on       party_history.id = node_history.id and       party_history.txn_start = node_history.txn_start and       party_history.txn_end = node_history.txn_end \n" +
            "left join tag_history         on         tag_history.id = node_history.id and         tag_history.txn_start = node_history.txn_start and         tag_history.txn_end = node_history.txn_end \n";

    public static final String VERTEX_HISTORY_QUERY_PREFIX = "select * \n from " + VERTEX_HISTORY_FROM_TABLE_LIST;

    protected static final String EDGE_HISTORY_QUERY_PREFIX = "select * \n" +
            "from flatedge_history \n" +
            "join transaction on flatedge_history.txn_start = transaction.id\n" +
            "left join acknowledge_history on acknowledge_history.id = flatedge_history.id and acknowledge_history.txn_start = flatedge_history.txn_start and acknowledge_history.txn_end = flatedge_history.txn_end \n";

    protected VersionQuery(Long head, VersionedGraph graph) {

        // guard
        if (head == null) throw new IllegalArgumentException("Query must have starting vertices");
        
        this.head = new ArrayList<Long>();
        this.head.add(head);
        this.graph = graph;
    }

    
    protected VersionQuery(List<Long> head, VersionedGraph graph) {
        
        // guards
        if (head == null) throw new IllegalArgumentException("Query must have starting vertices");
        head.removeAll(Collections.singleton(null));
        if (head.size() == 0) throw new IllegalArgumentException("Query must have starting vertices");            
        
        this.head = head;
        this.graph = graph;
    }

    
    public VersionQuery branch(List<String> labels, Direction direction) {
         clauses.add(new QueryClause(labels, direction));
        return this;
    }
    
    public VersionQuery branch(String label, Direction direction) {
        clauses.add(new QueryClause(Arrays.asList(label), direction));
       return this;
   }
    
    public VersionQuery branch(String[] labels, Direction direction) {
        clauses.add(new QueryClause(Lists.newArrayList(labels), direction));
       return this;
   }
    
    class QueryClause {
        List<String> labels;
        Direction direction;
        
        QueryClause(List<String> labels, Direction direction) {
            this.labels = labels;
            this.direction = direction;
        }
    }
    

    protected String generateFullSubGraphQuery() {

        int step = 0;
        
        StringBuilder s = new StringBuilder();
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS v0;\n");
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS v1;\n");
        
        s.append("CREATE TEMPORARY TABLE v0 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT) " + graph.tempTableEngine + ";\n");
        
        s.append("CREATE TEMPORARY TABLE v1 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT)" + graph.tempTableEngine + ";\n");
        
        // inject head
        s.append(String.format(

        "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
        + "SELECT DISTINCT 0, id, 0, 'root', 0 \n"
        + "FROM node_history \n"
        + "WHERE id IN (%s); \n",
        numberListToStr(head)));
        
        // add the clauses
        for (QueryClause qc : clauses) {
            
            step++;
            String thisTable = "v" + ( step    % 2);
            String thatTable = "v" + ((step+1) % 2);
            
            String labelsClause = generatelabelsClause(qc.labels);

            if (qc.direction == Direction.BOTH || qc.direction == Direction.IN) {

                s.append(String.format(
                "INSERT INTO %1$s (step, vid, eid, label, edge_order) \n"
                + "SELECT DISTINCT %3$d, v.id, e.id, e.label, e.edge_order  \n"
                + "FROM node_history v, flatedge_history e, %2$s \n"
                + "WHERE 1=1 "
                + labelsClause
                + " AND (e.v_out = v.id AND e.v_in = %2$s.vid) \n"
                + " AND " + thatTable + ".step = " + (step-1) + " ;\n",
                thisTable, thatTable, step));
            }
            
            if (qc.direction == Direction.BOTH || qc.direction == Direction.OUT) {

                s.append(String.format(
                "INSERT INTO %1$s (step, vid, eid, label, edge_order) \n"
                + "SELECT DISTINCT %3$d, v.id, e.id, e.label, e.edge_order  \n"
                + "FROM node_history v, flatedge_history e, %2$s \n"
                + "WHERE 1=1 "
                + labelsClause
                + " AND (e.v_in = v.id AND e.v_out = %2$s.vid) \n"
                + " AND " + thatTable + ".step = " + (step-1) + " ;\n",
                thisTable, thatTable, step));
            }
        }

        // result consolidation
        s.append("INSERT INTO v0 (step, vid, eid, label, edge_order) "
                + "SELECT step, vid, eid, label, edge_order FROM v1;\n");
        
        return s.toString();
        // Draw from v0 for results
    }


    private <T> String numberListToStr(List<T> numbers) {
        StringBuilder s = new StringBuilder();
        for (T n : numbers) {
            s.append(n).append(',');
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    private String strListToStr(List<String> strs) {
        StringBuilder s = new StringBuilder();
        for (String str : strs) {
            // dumbass sql injection protection (not real great)
            s.append("'" + str.replaceAll("'", "\\'") + "',");
        }
        s.setLength(s.length()-1);
        return s.toString();
    }
    
    
    private String generatelabelsClause(List<String> labels) {
        if (labels == null || labels.size() == 0) return "";
        return " AND e.label IN (" + strListToStr(labels) + ") \n"; 
    }
    
    
    public List<VersionedVertex> execute() {

        List<VersionedVertex> vertices;
        try (Handle h = graph.dbi().open()) {

            // run the generated query
            h.begin();
            h.createStatement(generateFullSubGraphQuery()).execute();
            h.commit();

            // and reap the rewards
            Map<TId, Map<String, Object>> propMaps = getVertexPropertyMaps(h);
            vertices = getVertices(h, graph, propMaps);
            getEdges(h, graph, propMaps);
        }
        return vertices;
    }
    
    
    private Map<TId, Map<String, Object>> getVertexPropertyMaps(Handle h) {
        
        List<TVertex> vertices = h.createQuery(VERTEX_HISTORY_QUERY_PREFIX + "inner join v0 on v0.vid = node_history.id ").map(new TVertexMapper()).list();

        Map<TId, Map<String, Object>> propertyMaps = new HashMap<TId, Map<String, Object>>();
        for (TVertex vertex : vertices) {
            propertyMaps.put((TId) vertex.getId(), vertex.getProperties());
        }
        return propertyMaps;
    }
    
    
    private List<VersionedVertex> getVertices(Handle h , VersionedGraph graph, Map<TId, Map<String, Object>> propMaps) {

        List<VersionedVertex> gotVertices = new ArrayList<>(); 
        
        List<TVertex> vertices = h.createQuery(
                "SELECT v.id, v.txn_start, v.txn_end, t.time "
                + "FROM node_history v, v0, transaction t "
                + "WHERE v.id = v0.vid and t.id = v.txn_start")
                .map(new TVertexMapper()).list();

        // add them to the graph
        Map<Long, Set<TVertex>> vertexSets = new HashMap<>();
        for (TVertex vertex : vertices) {
            Long versId = vertex.getId().id;
            if (vertexSets.get(versId) == null) 
                vertexSets.put(versId, new HashSet<TVertex>()); 
            vertexSets.get(versId).add(vertex);    
            vertex.replaceProperties(propMaps.get(vertex.getId()));
        }
        for (Set<TVertex> vSet : vertexSets.values()) {
            VersionedVertex v = new VersionedVertex(vSet, graph);
            graph.addVertexToGraph(v);
            gotVertices.add(v);
        }
        return gotVertices;
    }
    
    
    private void getEdges(Handle h , VersionedGraph graph, Map<TId, Map<String, Object>> propMaps) {
        
        List<TEdge> edges = h.createQuery(
                "SELECT e.id, e.txn_start, e.txn_end, e.label, e.v_in, e.v_out, e.edge_order, t.time "
                + "FROM flatedge_history e, v0, transaction t "
                + "WHERE e.id = v0.eid and t.id = e.txn_start")
                .map(new TEdgeMapper()).list();
        
        // add them to the graph
        Map<Long, Set<TEdge>> edgeSets = new HashMap<>();
        for (TEdge edge : edges) {
            if (edge == null) { // if either vertex doesn't exist 
                continue;
            }
            Long versId = edge.getId().id;
            if (edgeSets.get(versId) == null) 
                edgeSets.put(versId, new HashSet<TEdge>()); 
            edgeSets.get(versId).add(edge);    
            edge.replaceProperties(propMaps.get(edge.getId()));
        }            
        for (Set<TEdge> eSet : edgeSets.values()) {
            VersionedEdge e = new VersionedEdge(eSet, graph);
            graph.addEdgeToGraph(e);
        }
    }
}
