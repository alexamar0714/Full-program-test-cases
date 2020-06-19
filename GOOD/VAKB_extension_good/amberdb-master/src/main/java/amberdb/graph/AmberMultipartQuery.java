package amberdb.graph;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.skife.jdbi.v2.Handle;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;


public class AmberMultipartQuery extends AmberQueryBase implements AutoCloseable {

    /** starting vertex ids */
    List<Long> head;
    

    /**
     * Create a query with a collection of starting vertices. As branches are
     * added the query will return the subgraph covered by traversing the
     * branches specified in order.
     * 
     * @param head
     *            The collection of starting vertices
     * @param graph
     *            The graph to query
     */
    protected AmberMultipartQuery(AmberGraph graph, List<Long> head) {
        super(graph);
        
        // guards
        if (head == null)
            throw new IllegalArgumentException("Query must have starting vertices");
        head.removeAll(Collections.singleton(null));
        if (head.size() == 0)
            throw new IllegalArgumentException("Query must have starting vertices");
        this.head = head;
    }
    
    
    protected AmberMultipartQuery(AmberGraph graph, Long... head) {
        super(graph);
        
        // guards
        if (head == null || head.length < 1)
            throw new IllegalArgumentException("Query must have starting vertices");
        List<Long> header = Arrays.asList(head);
        header.removeAll(Collections.singleton(null));
        if (header.size() == 0)
            throw new IllegalArgumentException("Query must have starting vertices");
        this.head = header;
    }
    
    
    public class QueryClause {
        
        List<String> labels;
        Direction direction;
        List<Integer> branchList;
        BranchType branchType;
        
        public QueryClause(BranchType branchType, List<String> labels, Direction direction, List<Integer> branchList) {
            this.branchType = branchType;
            this.labels = labels;
            this.direction = direction;
            this.branchList = branchList;
        }
        
        public QueryClause(BranchType branchType, String[] labels, Direction direction, Integer[] branchList) {
            this.branchType = branchType;
            this.labels = Arrays.asList(labels);
            this.direction = direction;
            if (branchList != null) {
                this.branchList = Arrays.asList(branchList);
            }
        }
        
        public QueryClause(BranchType branchType, List<String> labels, Direction direction) {
            this.branchType = branchType;
            this.labels = labels;
            this.direction = direction;
            this.branchList = null;
        }
        
        public QueryClause(BranchType branchType, String[] labels, Direction direction) {
            this.branchType = branchType;
            this.labels = Arrays.asList(labels);
            this.direction = direction;
            this.branchList = null;
        }
    }

    
    private String generateQuerySetup() {
        step = 0;
        StringBuilder s = new StringBuilder();
        s.append(createTmpTables());
        s.append(insertStartVertices());
        return s.toString();
    }    

    
    private String createTmpTables() {

        StringBuilder s = new StringBuilder();
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS v0;\n");
        s.append("CREATE TEMPORARY TABLE v0 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT) " + graph.tempTableEngine + ";\n");

        // double buffer table to get around mysql limitation
        // of not being able to open the same temporary table
        // more than once in a query
        s.append("DROP " + graph.tempTableDrop + " TABLE IF EXISTS v1;\n");
        s.append("CREATE TEMPORARY TABLE v1 ("
                + "step INT, "
                + "vid BIGINT, "
                + "eid BIGINT, "
                + "label VARCHAR(100), "
                + "edge_order BIGINT) " + graph.tempTableEngine + ";\n");

        return s.toString();
    }    
    

    private String insertStartVertices() {

        StringBuilder s = new StringBuilder();
        s.append(String.format(
                "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
                + "SELECT 0, id, 0, 'root', 0 \n"
                + "FROM node \n"
                + "WHERE id IN (%s); \n",
                numberListToStr(head)));
        
        s.append("INSERT INTO v1 (step, vid, eid, label, edge_order) \n"
                + "SELECT step, vid, eid, label, edge_order \n"
                + "FROM v0; \n");
        
        return s.toString();
    }    

    
    public int step = 0; // keep a track of the last branching step
    
    private String generateBranchingQuery(QueryClause[] clauses) {

        StringBuilder s = new StringBuilder();
        for (QueryClause qc : clauses) {
            step++;
            String beginQuery = 
                    "INSERT INTO v0 (step, vid, eid, label, edge_order) \n"
                    + "SELECT " + step + ", v.id, e.id, e.label, e.edge_order  \n"
                    + "FROM node v, flatedge e, v1 \n"
                    + "WHERE ";

            String inDirection = " AND (e.v_out = v.id AND e.v_in = v1.vid) \n";
            String outDirection = " AND (e.v_in = v.id AND e.v_out = v1.vid) \n";
            String labels = generateLabelsClause(qc.labels);
            String branchType = generateBranchTypeClause(qc, step);
            
            if (qc.direction == Direction.BOTH || qc.direction == Direction.IN) {
                s.append(beginQuery + labels + inDirection + branchType + ";\n");
            }    
            if (qc.direction == Direction.BOTH || qc.direction == Direction.OUT) {
                s.append(beginQuery + labels + outDirection + branchType + ";\n");
            }    

            // copy results to v1
            s.append(
                    "INSERT INTO v1 (step, vid, eid, label, edge_order) \n"
                    + "SELECT step, vid, eid, label, edge_order \n"
                    + "FROM v0 \n"
                    + "WHERE v0.step = " + step + " ;\n");
        }
       
        return s.toString().replaceAll("WHERE\\s*AND", "WHERE ");
        // Draw from v1 for results
    }
    
    
    private String generateBranchTypeClause(QueryClause qc, int step) {
        String clause;
        switch (qc.branchType) {
        case BRANCH_FROM_PREVIOUS:
            clause = " AND v1.step = " + (step-1) + " \n";
            break;
        case BRANCH_FROM_LISTED:
            clause = " AND v1.step IN (" + numberListToStr(qc.branchList) + ") \n";
            break;
        case BRANCH_FROM_UNLISTED:    
            clause = " AND v1.step NOT IN (" + numberListToStr(qc.branchList) + ") \n";
            break;
        case BRANCH_FROM_ALL:
        default:    
            clause = "";
        }
        return clause;
    }
    
    
    public List<Vertex> getResults() {
        return getResults(false);
    }

    
    public List<Vertex> getResults(boolean fillEdges) {
        List<Vertex> vertices;
        Map<Long, Map<String, Object>> propMaps = getVertexPropertyMaps(h, "v0", "vid");
        vertices = getVertices(h, graph, propMaps, "v0", "vid", "edge_order");

        // Warning: Filled edge properties won't all be populated, only edges that were followed
        propMaps = getEdgePropertyMaps(h, "v0", "eid");
        if (fillEdges) {
            getFillEdges(h, graph, propMaps, "v0", "vid", "v1", "vid");
        } else {
            getEdges(h, graph, propMaps, "v0", "eid");
        }
        return vertices;
    }


    public List<Map<String, Object>> continueWithCheck(String checkQuery, QueryClause... branchConditions) {

        // run the generated query
        if (branchConditions.length > 0) {
            h.begin();
            h.createStatement(generateBranchingQuery(branchConditions)).execute();
            h.commit();
        }

        // run the check query
        if (checkQuery != null && !checkQuery.trim().isEmpty())
            return h.createQuery(checkQuery).list();
        return null;
    }

    
    public List<Map<String, Object>> startQuery(String checkQuery, QueryClause... branchConditions) {

        h = graph.dbi().open();

        String fullQuery = generateQuerySetup();
        if (branchConditions.length > 0) {
            fullQuery = fullQuery + generateBranchingQuery(branchConditions);
        }

        // run the generated query
        h.begin();
        h.createStatement(fullQuery).execute();
        h.commit();

        // run the check query
        if (checkQuery != null && !checkQuery.isEmpty()) {
            return h.createQuery(checkQuery).list();
        }
        return null;
    }


    private Handle h;
    public void startQuery() {
        h = graph.dbi().open();
        h.begin();
        h.createStatement(generateQuerySetup()).execute();
        h.commit();
    }
    
    
    public void close() {
        if (h != null) { h.close(); }
    }
}
