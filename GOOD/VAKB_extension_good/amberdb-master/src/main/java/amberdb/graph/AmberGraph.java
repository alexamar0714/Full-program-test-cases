package amberdb.graph;


import amberdb.graph.dao.AmberDao;
import amberdb.graph.dao.AmberDaoH2;
import amberdb.graph.dao.AmberDaoMySql;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.*;
import org.apache.commons.lang.StringUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Transaction;
import org.skife.jdbi.v2.TransactionStatus;
import org.skife.jdbi.v2.logging.PrintStreamLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import static amberdb.graph.State.*;


public class AmberGraph extends BaseGraph 
        implements Graph, TransactionalGraph, IdGenerator, 
        ElementModifiedListener, EdgeFactory, VertexFactory, RevertableGraph {

    private static final int COMMIT_BATCH_SIZE = 4000;
    private static final int BIG_COMMIT_THRESHOLD = 20000;
    private static final int DEFAULT_RETRIES = 4;
    private static final int DEFAULT_BACKOFF_MILLIS = 300;

    private static final Logger log = LoggerFactory.getLogger(AmberGraph.class);
    
    public static final DataSource DEFAULT_DATASOURCE = 
            JdbcConnectionPool.create("jdbc:h2:mem:persist;DATABASE_TO_UPPER=false","pers","pers");
    
    private static final Map<String, String>  vertexToTableMap = new HashMap<>();
    static {
    	vertexToTableMap.put("work",            "work");
    	vertexToTableMap.put("eadwork",         "work");
    	vertexToTableMap.put("page",            "work");
    	vertexToTableMap.put("copy",            "work");
    	vertexToTableMap.put("section",         "work");
    	vertexToTableMap.put("file",            "file");
    	vertexToTableMap.put("imagefile",       "file");
    	vertexToTableMap.put("movingimagefile", "file");
    	vertexToTableMap.put("soundfile",       "file");
    	vertexToTableMap.put("description",     "description");
    	vertexToTableMap.put("cameradata",      "description");
    	vertexToTableMap.put("geocoding",       "description");
    	vertexToTableMap.put("iptc",            "description");
    	vertexToTableMap.put("party",           "party");
    	vertexToTableMap.put("tag",             "tag");
    }

    private static final Map<String, String>  edgeToTableMap = new HashMap<>();
    static {
    	edgeToTableMap.put("label",          "flatedge");
    	edgeToTableMap.put("acknowledge",    "acknowledge");
    	edgeToTableMap.put("deliveredon",    "flatedge");
    	edgeToTableMap.put("descriptionof",  "flatedge");
    	edgeToTableMap.put("existson",       "flatedge");
    	edgeToTableMap.put("iscopyof",       "flatedge");
    	edgeToTableMap.put("isderivativeof", "flatedge");
    	edgeToTableMap.put("isfileof",       "flatedge");
    	edgeToTableMap.put("ispartof",       "flatedge");
    	edgeToTableMap.put("represents",     "flatedge");
    	edgeToTableMap.put("tags",           "flatedge");
    }

    protected DBI dbi;
    private AmberDao dao;

    private String dbProduct;
    protected String tempTableEngine = "";
    protected String tempTableCharSet = "";
    protected String tempTableDrop = "";
    
    protected Map<Object, Edge> removedEdges = new HashMap<>();
    protected Map<Object, Vertex> removedVertices = new HashMap<>();
    
    private Set<Edge> newEdges = new HashSet<Edge>();
    private Set<Vertex> newVertices = new HashSet<Vertex>();

    protected Set<Edge> modifiedEdges = new HashSet<Edge>();
    protected Set<Vertex> modifiedVertices = new HashSet<Vertex>();
    
    private boolean localMode = false;

    /**
     * Local mode puts the Amber Graph in a state where it will only query for
     * elements in the current session ie: it will not look for elements in
     * the Amber Graph's persistent data store. This can speed up queries 
     * significantly. When localMode is on, AmberQueries can still be used to 
     * populate the local graph and suspend, resume and commit should also 
     * work against the persistent data store. 
     * 
     * @param localModeOn if true sets local mode to on, off if false.
     */
    public void setLocalMode(boolean localModeOn) {
        localMode = localModeOn;
    }
    
    public boolean inLocalMode() {
        return localMode;
    }

    public AmberGraph() {
        initGraph(DEFAULT_DATASOURCE);
    }

    public AmberGraph(DataSource dataSource) {
        initGraph(dataSource);
    }

    private void initGraph(DataSource dataSource) {
        idGen = this;
        edgeFactory = this;
        vertexFactory = this;
        elementModListener = this;

        dbi = new DBI(dataSource);
        dao = selectDao(dataSource);
        if (!dao.schemaTablesExist()) {
            log.trace("Graph schema doesn't exist - creating ...");
            createAmberSchema();
        }
    }
    
    private AmberDao selectDao(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            dbProduct = conn.getMetaData().getDatabaseProductName();
            log.debug("Amber database type is {}", dbProduct);
        } catch (SQLException e) {
            log.trace("could not determine the database type - assuming it is H2");
            log.trace(e.getMessage());
        }

        if (dbProduct.equals("MySQL")) {
            tempTableEngine = "ENGINE=memory";
            tempTableCharSet = "CHARACTER SET utf8mb4";
            tempTableDrop = "TEMPORARY";
            return dbi.onDemand(AmberDaoMySql.class);
        } else if (dbProduct.equals("H2")) {
            return dbi.onDemand(AmberDaoH2.class);
        } else { // default to H2
            return dbi.onDemand(AmberDaoH2.class);
        }
    }

    public void createAmberSchema() {
        dao.createIdGeneratorTable();
        dao.createTransactionTable();
        
        dao.createV2Tables();
       
        newId(); // seed generator with id > 0
    }

    /**
     * Generate a new id unique within the persistent data store.
     *  
     * @return A unique persistent id
     */
    public Long newId() {
        dao.begin();
        Long newId = dao.newId();
        // occasionally clean up the id generation table (every 1000 pis or so)
        if (newId % 1000 == 995) {
            dao.garbageCollectIds(newId);
        }
        dao.commit();
        return newId;
    }    
    
    public void elementModified(Object element) {
        if (element instanceof Edge) {
            modifiedEdges.add((Edge) element);
        } else if (element instanceof Vertex) {
            modifiedVertices.add((Vertex) element);
        }
    }

    public AmberDao dao() {
        return dao;
    }

    public DBI dbi() {
        return dbi;
    }
    
    public String toString() {
        return ("ambergraph");
    }    

    @Override
    public void removeEdge(Edge e) {
        removedEdges.put(e.getId(), e);
        super.removeEdge(e);
    }

    @Override
    public void removeVertex(Vertex v) {
        // guard
        if (!graphVertices.containsKey(v.getId())) {
            throw new IllegalStateException("Cannot remove non-existent vertex : " + v.getId());
        }
        
        for (Edge e : v.getEdges(Direction.BOTH)) {
            removedEdges.put(e.getId(), e);
        }
        removedVertices.put(v.getId(), v);
        super.removeVertexWithoutGuard(v); // existence check already performed above
    }

    @Override
    public void revertVertex(Vertex v) {
        if (!graphVertices.containsKey(v.getId())) {
            throw new IllegalStateException("Cannot remove non-existent vertex : " + v.getId());
        }

        for (Edge e : v.getEdges(Direction.BOTH)) {
            revertEdge(e);
        }

        removedVertices.remove(v.getId());
        removeVertex(newVertices, v);
        removeVertex(modifiedVertices, v);
    }

    @Override
    public void revertEdge(Edge e) {
        removedEdges.remove(e.getId());
        removeEdge(newEdges, e);
        removeEdge(modifiedEdges, e);
    }

    private void removeEdge(Set<Edge> edges, Edge edge) {
        if (!edges.remove(edge)) {
            Iterator<Edge> iterator = edges.iterator();

            while (iterator.hasNext()) {
                Edge next = iterator.next();
                if (next.getId() != null && next.getId().equals(edge.getId())) {
                    iterator.remove();
                }
            }
        }
    }

    private void removeVertex(Set<Vertex> vertices, Vertex vertex) {
        if (!vertices.remove(vertex)) {
            Iterator<Vertex> iterator = vertices.iterator();

            while (iterator.hasNext()) {
                Vertex next = iterator.next();
                if (next.getId() != null && next.getId().equals(vertex.getId())) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public Edge addEdge(Object id, Vertex out, Vertex in, String label) {
        Edge edge = super.addEdge(id, out, in, label);
        newEdges.add(edge);
        return edge;
    }

    @Override
    public Vertex addVertex(Object id) {
        Vertex vertex = super.addVertex(id);
        newVertices.add(vertex);
        return vertex;
    }

    public Long suspend() {
        Long sessId;

        if (getModifiedElementCount() > BIG_COMMIT_THRESHOLD) {
            log.warn("Graph to be committed exceeds {} elements. Using big suspend to process.",
                    BIG_COMMIT_THRESHOLD);
       
            sessId = suspendBig();
            clear();
        } else {
            // set up batch sql data structures
            sessId = newId();

            removeDuplicateGraphItems();

            Map<String, Set<AmberVertex>> newVerticesByType      = getVerticesByType(newVertices);
            Map<String, Set<AmberVertex>> modifiedVerticesByType = getVerticesByType(modifiedVertices);
            Map<String, Set<AmberVertex>> removedVerticesByType  = getVerticesByType(removedVertices.values());
            Map<String, Set<AmberEdge>>   newEdgesByType         = getEdgesByType(newEdges);
            Map<String, Set<AmberEdge>>   modifiedEdgesByType    = getEdgesByType(modifiedEdges);
            Map<String, Set<AmberEdge>>   removedEdgesByType     = getEdgesByType(removedEdges.values());
            
            dao.begin();
            
            suspendIntoFlatVertexTables(sessId, newVerticesByType,      NEW);
            suspendIntoFlatVertexTables(sessId, modifiedVerticesByType, MOD);
            suspendIntoFlatVertexTables(sessId, removedVerticesByType , DEL);
            suspendIntoFlatEdgeTables(  sessId, newEdgesByType,         NEW);
            suspendIntoFlatEdgeTables(  sessId, modifiedEdgesByType,    MOD);
            suspendIntoFlatEdgeTables(  sessId, removedEdgesByType ,    DEL);
            
            dao.commit();
        }

        return sessId;
    }

    private void removeDuplicateGraphItems() {
        modifiedEdges.removeAll(removedEdges.values());
        newEdges.removeAll(removedEdges.values());
        for (Edge newEdge : newEdges) {
            removedEdges.remove(newEdge.getId());
        }
        modifiedEdges.removeAll(newEdges);
        modifiedVertices.removeAll(removedVertices.values());
        newVertices.removeAll(removedVertices.values());
        for (Vertex newVertex : newVertices) {
            removedVertices.remove(newVertex.getId());
        }
        modifiedVertices.removeAll(newVertices);
    }

	private Map<String, Set<AmberVertex>> getVerticesByType(Collection<Vertex> vertices) {
		Map<String, Set<AmberVertex>> verticesByType = new HashMap<>();
        for (Vertex vertex : vertices) {
            addVertex(verticesByType, (AmberVertex) vertex);
        }
		return verticesByType;
	}
	
	private Map<String, Set<AmberEdge>> getEdgesByType(Collection<Edge> edges) {
		Map<String, Set<AmberEdge>> edgesByType = new HashMap<>();
        for (Edge edge : edges) {
            addEdge(edgesByType, (AmberEdge) edge);
        }
		return edgesByType;
	}

    @Override
    public Vertex newVertex(Object id, Map<String, Object> properties, Graph graph) {
        return new AmberVertex((Long) id, properties, (AmberGraph) graph, 0L, 0L);
    }

    @Override
    public Edge newEdge(Object id, String label, Vertex outVertex, Vertex inVertex, 
            Map<String, Object> properties, Graph graph) {
        return new AmberEdge((Long) id, label, (AmberVertex) outVertex, (AmberVertex) inVertex, 
                properties, (AmberGraph) graph, 0L, 0L, 0);
    }
    
    public void clear() {
        super.clear();
        clearChangeSets();
    }

    private void clearChangeSets() {
        removedEdges.clear();
        removedVertices.clear();

        newEdges.clear();
        newVertices.clear();

        modifiedEdges.clear();
        modifiedVertices.clear();
    }

    public void destroySession(Long sessId) {
        log.debug("removing session {} from the session tables", sessId);
        dao.begin();
        dao.clearFlatSession(sessId);
        dao.commit();
    }
    
    public void resume(Long sessId) {
        clear();

        // Restore vertices to the graph before edges because 
        // edge construction depends on vertex existence
        List<AmberVertexWithState> vertexStateWrappers = resumeFlatVertices(sessId);
        for (AmberVertexWithState wrapper : vertexStateWrappers) {
            AmberVertex vertex = wrapper.vertex;

            State state = wrapper.state;
            if (state.equals(DEL)) {
                removedVertices.put(vertex.getId(), vertex);
                continue;
            }

            addVertexToGraph(vertex);

            if (state == NEW) {
                newVertices.add(vertex);
            } else if (state == MOD) {
                modifiedVertices.add(vertex);
            }
        }

        List<AmberEdgeWithState> edgeStateWrappers = resumeFlatEdges(sessId);
        for (AmberEdgeWithState wrapper : edgeStateWrappers) {
            AmberEdge edge = wrapper.edge;

            State state = wrapper.state;
            if (state == DEL) {
                removedEdges.put(edge.getId(), edge);
                continue;
            }

            if (edge.inId == 0 || edge.outId == 0) {
                log.warn("Stale session resumed: Failed to restore [{}] from session {}", edge, sessId);
                log.warn("-- One or both of the incident vertices was deleted from the database after session suspension but before resumption.");
                continue;
            }

            addEdgeToGraph(edge);

            if (state == NEW) {
                newEdges.add(edge);
            } else if (state == MOD) {
                modifiedEdges.add(edge);
            }
        }
        
        log.debug("resuming -- vertices - deleted:{} new:{} modified:{} -- edges - deleted:{} new:{} modified:{}", 
                removedVertices.size(), newVertices.size(), modifiedVertices.size(), 
                removedEdges.size(), newEdges.size(), modifiedEdges.size());
    }

    private List<AmberVertexWithState> resumeFlatVertices(Long sessId) {
        List<AmberVertexWithState> vertices;

        try (Handle h = dbi.open()) {
            String sql = AmberQueryBase.SESS_VERTEX_QUERY_PREFIX + " where sess_node.s_id = :sessId";

            vertices = h.createQuery(sql)
                .bind("sessId", sessId)
                .map(new AmberVertexWithStateMapper(this)).list();
        }

        return vertices;
    }

    private List<AmberEdgeWithState> resumeFlatEdges(Long sessId) {
        List<AmberEdgeWithState> edges;
        try (Handle h = dbi.open()) {
            edges = h.createQuery(
                    AmberQueryBase.SESS_EDGE_QUERY_PREFIX + " where f.s_id = :sessId ")
                .bind("sessId", sessId)
                .map(new EdgeMapper(this, false)).list();
        }
        return edges;
    }

    public int getModifiedElementCount() {
        return newVertices.size() +
                modifiedVertices.size() +
                removedVertices.size() +
                newEdges.size() +
                modifiedEdges.size() +
                removedEdges.size();
    }

    public Long commit(String user, String operation) {

        if (getModifiedElementCount() > BIG_COMMIT_THRESHOLD) {
            log.warn("Graph to be committed exceeds {} elements. Using big commit to process.", BIG_COMMIT_THRESHOLD);
            return commitBig(user, operation);
        }
        
        Long txnId = suspend();
        commitSqlWrappedWithRetry(txnId, user, operation, DEFAULT_RETRIES, DEFAULT_BACKOFF_MILLIS);
        clearChangeSets();
        return txnId;
    }

    @Override
    public void commit() {
        commit("amberdb", "commit");
    }

    @Override
    public Vertex getVertex(Object id) {
        return getVertex(id, localMode);
    } 

    @Override
    public Iterable<Edge> getEdges() {
        if (!localMode) {
            AmberEdgeQuery avq = new AmberEdgeQuery(this); 
            List<Edge> amberEdges = avq.execute();
            Set<Edge> edges = Sets.newHashSet(super.getEdges());
            for (Edge e : amberEdges) {
                edges.add(e);
            }
            return edges;
        } else {
            return super.getEdges();
        }
    }
    
    /**
     * Currently, 'label' cannot be used as the key to return matching labeled
     * edges.
     */
    @Override
    public Iterable<Edge> getEdges(String key, Object value) {
        if (!localMode) {
            AmberEdgeQuery avq = new AmberEdgeQuery(this); 
            avq.addCriteria(key, value);
            List<Edge> amberEdges = avq.execute();
            Set<Edge> edges = Sets.newHashSet(super.getEdges(key, value));
            for (Edge e : amberEdges) {
                edges.add(e);
            }
            return edges;
        } else {
            return super.getEdges(key, value);
        }    
    }

    protected Vertex getVertex(Object id, boolean localOnly) {
        Vertex vertex = super.getVertex(id);
        if (vertex != null) return vertex;
        if (localOnly) return null;
        
        // super may have returned null because the id didn't parse
        if (parseId(id) == null) return null;
       
        AmberVertexWithState vs;
        try (Handle h = dbi.open()) {
            vs = h.createQuery(
            		"select *, 'AMB' state " +
            		"from node " +
            		"left join work        on        work.id = node.id " +
            		"left join file        on        file.id = node.id " +
            		"left join description on description.id = node.id " +
            		"left join party       on       party.id = node.id " +
            		"left join tag         on         tag.id = node.id " +
            		"where node.id = :id")
                .bind("id", parseId(id))
                .map(new AmberVertexWithStateMapper(this)).first();

            if (vs == null) return null;
            vertex = vs.vertex;
            if (removedVertices.containsKey(vertex.getId())) return null;

            AmberVertex v = (AmberVertex) vertex;
            addVertexToGraph(v);
        }
        
        return vertex;
    } 

    @Override
    public Edge getEdge(Object id) {
        return getEdge(id, localMode);
    } 
    
    private Edge getEdge(Object id, boolean localOnly) {
        
        Edge edge = super.getEdge(id);
        if (edge != null) return edge;
        if (localOnly) return null;
        
        // super may have returned null because the id didn't parse
        if (parseId(id) == null) return null;
        
        AmberEdge e;
        try (Handle h = dbi.open()) {
            AmberEdgeWithState es = h.createQuery(
                "select *, 'AMB' state "
                + "from flatedge " 
                + "left join acknowledge on acknowledge.id = flatedge.id " 
                + "where flatedge.id = :id")
                .bind("id", parseId(id))
                .map(new AmberEdgeWithStateMapper(this, false)).first();

            if (es == null) return null;
            edge = es.edge;
            if (removedEdges.containsKey(edge.getId())) return null;
        
            e = (AmberEdge) edge;
            addEdgeToGraph(e);
        }

        return e;
    } 
    
    /**
     * Notes on Tinkerpop Graph interface method implementations for
     * 
     *    Iterable<Edge> getEdges()
     *    Iterable<Edge> getEdges(String key, Object value)
     *    Iterable<Vertex> getVertices()
     *    Iterable<Vertex> getVertices(String key, Object value)
     * 
     * To avoid crashing a large amber system these methods limit the number
     * of elements returned from persistent storage (currently 10000).
     */
    
    @Override
    public Iterable<Vertex> getVertices() {
        if (!localMode) {  
            new AmberVertexQuery(this).execute();
        }
        return super.getVertices();
    }
    
    @Override
    public Iterable<Vertex> getVertices(String key, Object value) {
        if (!localMode) {
            AmberVertexQuery avq = new AmberVertexQuery(this); 
            avq.addCriteria(key, value);
            avq.execute(); 
        }
        return super.getVertices(key, value); 
    }

    /**
     * Required for searching on values in json encoded string lists. Needed in Banjo
     * @param key The name of the property containing a json encoded string list 
     * @param value The value to search for in the list
     * @return Any matching vertices
     */
    public Iterable<Vertex> getVerticesByJsonListValue(String key, String value) {
        if (!localMode) {
            AmberVertexQuery avq = new AmberVertexQuery(this); 
            avq.executeJsonValSearch(key, value);
        }
        
        List<Vertex> vertices = new ArrayList<>();
        for (Vertex vertex : graphVertices.values()) {
            String s = vertex.getProperty(key);
            if (s != null && s.contains("\""+value+"\"")) {
                vertices.add(vertex);
            }
        }
        return vertices;        
    }    

    /**
     * Used by AmberVertex.
     */
    protected void getBranch(Long id, Direction direction, String[] labels) {
        AmberQuery q = new AmberQuery(id, this);
        q.branch(Lists.newArrayList(labels), direction);
        q.execute();
    }

    public AmberQuery newQuery(Long id) {
        return new AmberQuery(id, this);
    }

    public AmberQuery newQuery(List<Long> ids) {
        return new AmberQuery(ids, this);
    }

    public AmberMultipartQuery newMultipartQuery(List<Long> ids) {
        return new AmberMultipartQuery(this, ids);
    }

    public AmberMultipartQuery newMultipartQuery(Long... ids) {
        return new AmberMultipartQuery(this, ids);
    }
    
    public AmberVertexQuery newVertexQuery() {
        return new AmberVertexQuery(this);
    }

    @Override
    public void shutdown() {
        dao.close();
        super.shutdown();
    }
    
    public List<AmberTransaction> getTransactionsByVertexId(Long id) {
        return dao.getTransactionsByVertexId(id); 
    }

    public List<AmberTransaction> getTransactionsByEdgeId(Long id) {
        return dao.getTransactionsByEdgeId(id);
    }
    
    public AmberTransaction getTransaction(Long id) {
        return dao.getTransaction(id);
    }

    public AmberTransaction getFirstTransactionForVertexId(Long id) {
        return dao.getFirstTransactionForVertexId(id);
    }

    public AmberTransaction getFirstTransactionForEdgeId(Long id) {
        return dao.getFirstTransactionForEdgeId(id);
    }

    public void commitPersistedSession(Long txnId, String user, String operation) {
        commitSqlWrappedWithRetry(txnId, user, operation, DEFAULT_RETRIES, DEFAULT_BACKOFF_MILLIS);
    }

    private void commitSqlWrappedWithRetry(Long txnId, String user, String operation, int retries, int backoffDelay) {
        int tryCount = 0;
        int backoff = backoffDelay;
        retryLoop: while (true) {
            try {
                dao.begin();
                String sqllog = System.getProperty("sqllog");
                if (sqllog != null) {
                    dao.getHandle().setSQLLog(new PrintStreamLog());
                }
                // End current elements where this transaction modifies or deletes them.
                // Additionally, end edges orphaned by this procedure.
                log.debug("ending elements");
                dao.endNodes(txnId);
                dao.endWorks(txnId);
                dao.endFiles(txnId);
                dao.endTags(txnId);
                dao.endParties(txnId);
                dao.endDescriptions(txnId);
                dao.endFlatedges(txnId);
                dao.endAcknowledgements(txnId);
                // start new elements for new and modified transaction elements
                log.debug("starting elements");
                dao.startNodes(txnId);
                dao.startWorks(txnId);
                dao.startFiles(txnId);
                dao.startTags(txnId);
                dao.startParties(txnId);
                dao.startDescriptions(txnId);
                dao.startFlatedges(txnId);
                dao.startAcknowledgements(txnId);
                // Refactor note: need to check when adding (modding?) edges that both ends exist
                dao.insertTransaction(txnId, new Date().getTime(), user, operation);
                dao.commit();
                log.debug("commit complete");
                break retryLoop;
            } catch (RuntimeException e) {
                try {
                    dao.rollback();
                } catch (Exception rollbackException) {
                    log.warn("Rollback failed: {}", rollbackException);
                }
                if (tryCount < retries) {
                    log.warn("AmberDb commit failed: Reason: {}\n" +
                            "Retry after {} milliseconds", e.getMessage(), backoff);
                    tryCount++;
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ie) {
                        log.error("Backoff delay failed :", ie); // noted
                    }
                    backoff = backoff *2;
                } else {
                    log.error("AmberDb commit failed after {} retries: Reason:", retries, e);
                    throw e;
                }
            }
        }
    }

    public String getTempTableDrop() {
        return tempTableDrop;
    }

    public String getTempTableEngine() {
        return tempTableEngine;
    }

    public String getTempTableCharSet() {
        return tempTableCharSet;
    }

    private void bigSuspendVertices(Long sessId) {
        AmberVertexBatch vertices = new AmberVertexBatch();
        AmberPropertyBatch properties = new AmberPropertyBatch();

        Map<String, Set<AmberVertex>> removedVerticesByType = new HashMap<>();
        Map<String, Set<AmberVertex>> newVerticesByType      = new HashMap<>();
        Map<String, Set<AmberVertex>> modifiedVerticesByType = new HashMap<>();

        log.debug("suspending verts -- deleted:{} new:{} modified:{} ", 
                removedVertices.size(), newVertices.size(), modifiedVertices.size());

        int batchLimit = 0;

        for (Vertex v : removedVertices.values()) {
            modifiedVertices.remove(v);
            if (newVertices.remove(v)) continue;
            vertices.add(new AmberVertexWithState((AmberVertex) v, DEL));
            addVertex(removedVerticesByType, (AmberVertex) v);

            batchLimit++;
            if (batchLimit >= COMMIT_BATCH_SIZE) {
                log.debug("Batched vertex marshalling");

                batchLimit = suspendIntoFlatVertexTablesAndClearBatch(sessId, vertices, null, removedVerticesByType, DEL, batchLimit);
            }
        }
        
        if (batchLimit > 0) {
            batchLimit = suspendIntoFlatVertexTablesAndClearBatch(sessId, vertices, null, removedVerticesByType, DEL, batchLimit);
        }

        for (Vertex v : graphVertices.values()) {
            AmberVertex av = (AmberVertex) v;
            if (newVertices.contains(v)) {
                modifiedVertices.remove(v); // a modified new vertex is just a new vertex
                vertices.add(new AmberVertexWithState(av, NEW));
                properties.add((Long) av.getId(), av.getProperties());
                addVertex(newVerticesByType, av);
            } else if (modifiedVertices.contains(v)) {
                vertices.add(new AmberVertexWithState(av, MOD));
                properties.add((Long) av.getId(), av.getProperties());
                addVertex(modifiedVerticesByType, av);
            }
            batchLimit++;
            batchLimit += (av.getProperties() == null) ? 0 : av.getProperties().size();
            if (batchLimit >= COMMIT_BATCH_SIZE) {
                log.debug("Batched vertex marshalling");

                suspendIntoFlatVertexTables(sessId, newVerticesByType,      NEW);
                suspendIntoFlatVertexTablesAndClearBatch(sessId, vertices, properties, modifiedVerticesByType, MOD, batchLimit);
                newVerticesByType.clear();
            }
        }
        
        if (batchLimit > 0) {
            suspendIntoFlatVertexTables(sessId, newVerticesByType,      NEW);
            batchLimit = suspendIntoFlatVertexTablesAndClearBatch(sessId, vertices, properties, modifiedVerticesByType, MOD, batchLimit);
            newVerticesByType.clear();
        }
    }

    private int suspendIntoFlatVertexTablesAndClearBatch(Long sessId, AmberVertexBatch vertices, AmberPropertyBatch properties,
            Map<String, Set<AmberVertex>> verticesByType, State state, int batchLimit) {
        suspendIntoFlatVertexTables(sessId, verticesByType , state);
        
        vertices.clear();
        if (properties != null) {
            properties.clear();
        }
        batchLimit = 0;
        verticesByType.clear();
        return batchLimit;
    }
    
    

	private void bigSuspendEdges(Long sessId) {
        AmberEdgeBatch edges = new AmberEdgeBatch();
        AmberPropertyBatch properties = new AmberPropertyBatch();

        Map<String, Set<AmberEdge>>   newEdgesByType         = getEdgesByType(newEdges);
        Map<String, Set<AmberEdge>>   modifiedEdgesByType    = getEdgesByType(modifiedEdges);
        Map<String, Set<AmberEdge>>   removedEdgesByType     = getEdgesByType(removedEdges.values());

        log.debug("suspending edges -- deleted:{} new:{} modified:{}", 
                removedEdges.size(), newEdges.size(), modifiedEdges.size());

        int batchLimit = 0;
        for (Edge e : removedEdges.values()) {
            modifiedEdges.remove(e);
            if (newEdges.remove(e)) continue;
            edges.add(new AmberEdgeWithState((AmberEdge) e, DEL));
            addEdge(removedEdgesByType, (AmberEdge) e);

            batchLimit++;
            if (batchLimit >= COMMIT_BATCH_SIZE) {
                log.debug("Batched edge marshalling");

                batchLimit = suspendIntoFlatEdgeTablesAndClearBatch(sessId, edges, removedEdgesByType, DEL, batchLimit);
            }
        }
        if (batchLimit > 0) {
            batchLimit = suspendIntoFlatEdgeTablesAndClearBatch(sessId, edges, removedEdgesByType, DEL, batchLimit);
        }

        for (Edge e : graphEdges.values()) {
            AmberEdge ae = (AmberEdge) e;
            if (newEdges.contains(e)) {
                modifiedEdges.remove(e); // a modified new edge is just a new edge
                edges.add(new AmberEdgeWithState(ae, NEW));
                properties.add((Long) ae.getId(), ae.getProperties());
                addEdge(newEdgesByType, (AmberEdge) e);
            } else if (modifiedEdges.contains(e)) {
                edges.add(new AmberEdgeWithState(ae, MOD));
                properties.add((Long) ae.getId(), ae.getProperties());
                addEdge(modifiedEdgesByType, (AmberEdge) e);
            }
            batchLimit++;
            batchLimit += (ae.getProperties() == null) ? 0 : ae.getProperties().size();
            if (batchLimit >= COMMIT_BATCH_SIZE) {
                log.debug("Batched edge marshalling");

                suspendIntoFlatEdgeTables(  sessId, newEdgesByType,         NEW);
                suspendIntoFlatEdgeTablesAndClearBatch(sessId, edges, modifiedEdgesByType, MOD, batchLimit);
                newEdgesByType.clear();
            }
        }
        if (batchLimit > 0) {
            suspendIntoFlatEdgeTables(  sessId, newEdgesByType,         NEW);
            batchLimit = suspendIntoFlatEdgeTablesAndClearBatch(sessId, edges, modifiedEdgesByType, MOD, batchLimit);
            newEdgesByType.clear();
        }
    }

    private int suspendIntoFlatEdgeTablesAndClearBatch(Long sessId, AmberEdgeBatch edges,
            Map<String, Set<AmberEdge>> edgesByType, State state, int batchLimit) {
        suspendIntoFlatEdgeTables(  sessId, edgesByType, state);

        edges.clear();
        batchLimit = 0;
        edgesByType.clear();
        return batchLimit;
    }

    public Long suspendBig() {

        // set up batch sql data structures
        final Long sessId = newId();
        dao.inTransaction(new Transaction<Long, AmberDao>() {
            @Override
            public Long inTransaction(AmberDao dao,
                    TransactionStatus transactionStatus) throws Exception {
                bigSuspendEdges(sessId);
                bigSuspendVertices(sessId);
                log.info("finished big suspend");
                return sessId;
            }
        });

        return sessId;
    }

    public Long commitBig(String user, String operation) {

        Long txnId = suspendBig();
        commitSqlWrappedWithRetry(txnId, user, operation, DEFAULT_RETRIES, DEFAULT_BACKOFF_MILLIS);
        log.debug("Commence clearing session");
        dao.clearFlatSession(txnId);
        log.debug("Finished clearing session");

        clearChangeSets();
        return txnId;
    }

    public void commitBig() {
        commitBig("amberdb", "commit");
    }

    public List<AmberVertex> getVerticesByTransactionId(Long id) {
        
        try (Handle h = dbi.open()) {
            List<AmberVertexWithState> vs = h.createQuery(
                "(SELECT DISTINCT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + "FROM transaction t, node_history v "
                + "WHERE t.id = :id "
                + "AND v.txn_start = t.id) "
                + "UNION "
                + "(SELECT DISTINCT v.id, v.txn_start, v.txn_end, 'AMB' state "
                + "FROM transaction t, node_history v "
                + "WHERE t.id = :id "
                + "AND v.txn_end = t.id) "
                + "ORDER BY id")
                .bind("id", id)
                .map(new AmberVertexWithStateMapper(this)).list();

            List<AmberVertex> vertices = new ArrayList<>();
            for (AmberVertexWithState v : vs) {
                vertices.add(v.vertex);
            }
            return vertices;
        }
    }

    public List<AmberEdge> getEdgesByTransactionId(Long id) {
        
        try (Handle h = dbi.open()) {
            List<AmberEdgeWithState> es = h.createQuery(
                "(SELECT DISTINCT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, 'AMB' state "
                + "FROM transaction t, flatedge_history e "
                + "WHERE t.id = :id "
                + "AND e.txn_start = t.id) "
                + "UNION "
                + "(SELECT DISTINCT e.id, e.txn_start, e.txn_end, e.v_out, e.v_in, e.label, e.edge_order, 'AMB' state "
                + "FROM transaction t, flatedge_history e "
                + "WHERE t.id = :id "
                + "AND e.txn_end = t.id) "
                + "ORDER BY id")
                .bind("id", id)
                .map(new EdgeMapper(this, false)).list();

            List<AmberEdge> edges = new ArrayList<>();
            for (AmberEdgeWithState e : es) {
                edges.add(e.edge);
            }
            return edges;
        }
    }
    
    public List<Vertex> getRemovedVertices() {
        return new ArrayList(removedVertices.values());
    }

    public List<Vertex> getNewVertices() {
        return new ArrayList<>(newVertices);
    }

    public List<Vertex> getModifiedVertices() {
        return new ArrayList(modifiedVertices);
    }

    public List<Edge> getRemovedEdges() {
        return new ArrayList(removedEdges.values());
    }

    public List<Edge> getNewEdges() {
        return new ArrayList<>(newEdges);
    }

    public List<Edge> getModifiedEdges() {
        return new ArrayList(modifiedEdges);
    }

	private void addVertex(Map<String, Set<AmberVertex>> verticesByType, AmberVertex v) {
		final String type = v.getProperty("type");
		if (StringUtils.isNotBlank(type)) {
			Set<AmberVertex> set = verticesByType.get(type);
			if (set == null) {
				set = new HashSet<>();
				verticesByType.put(type, set);
			}
			set.add(v);
		}
	}

	private void addEdge(Map<String, Set<AmberEdge>> edgesByType, AmberEdge e) {
		final String type = e.getLabel();
		if (StringUtils.isNotBlank(type)) {
			Set<AmberEdge> set = edgesByType.get(type);
			if (set == null) {
				set = new HashSet<>();
				edgesByType.put(type, set);
			}
			set.add(e);
		}
	}
	
	private void suspendIntoFlatVertexTables(Long sessId, Map<String, Set<AmberVertex>> verticesByType, State operation) {
        for (Entry<String, Set<AmberVertex>> entry: verticesByType.entrySet()) {
        	String vertexType = entry.getKey().toLowerCase();
        	String table = vertexToTableMap.get(vertexType);
            dao.suspendIntoNodeTable(sessId, operation, entry.getValue());
        	if (StringUtils.isNotBlank(table)) {
        		dao.suspendIntoFlatVertexTable(sessId, operation, "sess_" + table, entry.getValue());
        	}
        }
	}

	private void suspendIntoFlatEdgeTables(Long sessId, Map<String, Set<AmberEdge>> edgesByType, State operation) {
        for (Entry<String, Set<AmberEdge>> entry: edgesByType.entrySet()) {
        	String edgeType = entry.getKey().toLowerCase();
        	String table = edgeToTableMap.get(edgeType);
            dao.suspendIntoFlatEdgeTable(sessId, operation, entry.getValue());
        	if (StringUtils.isNotBlank(table) && !"flatedge".equals(table)) {
    			dao.suspendIntoFlatEdgeSpecificTable(sessId, operation, "sess_" + table, entry.getValue());
        	}
        }
	}
	
    public Iterable<Vertex> getVerticesByAliasValue(String key, String value) {
        if (!localMode) {
            AmberVertexQuery avq = new AmberVertexQuery(this); 
            avq.executeAliasSearch(value);
        }
        
        List<Vertex> vertices = new ArrayList<>();
        for (Vertex vertex : graphVertices.values()) {
            String s = vertex.getProperty(key);
            if (s != null && s.contains("\""+value+"\"")) {
                vertices.add(vertex);
            }
        }
        return vertices;        
    }   
}