package amberdb;


import amberdb.graph.*;
import amberdb.graph.AmberMultipartQuery.QueryClause;
import amberdb.model.*;
import amberdb.query.ModifiedObjectsQueryRequest;
import amberdb.query.ModifiedObjectsQueryResponse;
import amberdb.relation.Acknowledge;
import amberdb.sql.ListLu;
import amberdb.sql.Lookups;
import amberdb.version.VersionedVertex;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONWriter;
import com.tinkerpop.blueprints.util.wrappers.WrapperGraph;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;
import com.tinkerpop.frames.modules.typedgraph.TypedGraphModuleBuilder;
import doss.BlobStore;
import org.apache.commons.lang.StringUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static amberdb.graph.BranchType.*;


public class AmberSession implements AutoCloseable {
    private static final int DEFAULT_MAX_HIERARCHY_DEPTH = 50;
    private final FramedGraph<TransactionalGraph> graph;
    private final BlobStore blobStore;
    private DBI lookupsDbi;
    private final TempDirectory tempDir;

    private final static FramedGraphFactory framedGraphFactory =
            new FramedGraphFactory(
                new JavaHandlerModule(),
                new GremlinGroovyModule(),
                new TypedGraphModuleBuilder()
                .withClass(Copy.class)
                .withClass(File.class)
                .withClass(ImageFile.class)
                .withClass(SoundFile.class)
                .withClass(MovingImageFile.class)
                .withClass(Page.class)
                .withClass(Section.class)
                .withClass(Work.class)
                .withClass(Description.class)
                .withClass(IPTC.class)
                .withClass(GeoCoding.class)
                .withClass(CameraData.class)
                .withClass(EADWork.class)
                .withClass(Tag.class)
                .withClass(Party.class)
                .build());
    private List<AmberPreCommitHook> preCommitHooks = new ArrayList<>();


    /**
     * Constructs an in-memory AmberDb for testing with. Also creates a BlobStore in a temp dir
     */
    public AmberSession() {

        tempDir = new TempDirectory();
        tempDir.deleteOnExit();

        // DOSS
        blobStore = AmberDb.openBlobStore(tempDir.toString());

        // Graph
        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;DATABASE_TO_UPPER=false", "amb", "amb");
        AmberGraph amber = init(dataSource, null);
        graph = openGraph(amber);
    }


    /**
     * Constructs an AmberDb stored on the local filesystem.
     */
    public AmberSession(BlobStore blobStore, Long sessionId) throws IOException {

        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;DATABASE_TO_UPPER=false", "amb", "amb");
        AmberGraph amber = init(dataSource, sessionId);
        tempDir = null;

        // DOSS
        this.blobStore = blobStore;

        // Graph
        graph = openGraph(amber);
    }


    public AmberSession(BlobStore blobStore) throws IOException {

        DataSource dataSource = JdbcConnectionPool.create("jdbc:h2:mem:graph;DB_CLOSE_DELAY=-1;MVCC=TRUE;DATABASE_TO_UPPER=false", "amb", "amb");
        AmberGraph amber = init(dataSource, null);
        tempDir = null;

        // DOSS
        this.blobStore = blobStore;

        // Graph
        graph = openGraph(amber);
    }


    public AmberSession(DataSource dataSource, BlobStore blobStore, Long sessionId) {
        AmberGraph amber = init(dataSource, sessionId);
        tempDir = null;

        // DOSS
        this.blobStore = blobStore;

        // Graph
        graph = openGraph(amber);
    }

    public AmberSession(DataSource dataSource, BlobStore blobStore, Long sessionId, List<AmberPreCommitHook> preCommitHooks) {
        this.preCommitHooks = preCommitHooks;

        AmberGraph amber = init(dataSource, sessionId);
        tempDir = null;

        // DOSS
        this.blobStore = blobStore;

        // Graph
        graph = openGraph(amber);
    }


    private AmberGraph init(DataSource dataSource, Long sessionId) {
        // Lookups dbi
        lookupsDbi = new DBI(dataSource);
        
        // Graph
        AmberGraph amber = new AmberGraph(dataSource);
        if (sessionId != null)
            amber.resume(sessionId);
        return amber;
    }

    public AmberGraph getAmberGraph() {
        return ((OwnedGraph) graph.getBaseGraph()).getAmberGraph();
    }


    public Lookups getLookups() {
        return lookupsDbi.onDemand(Lookups.class);
    }



    public void setLocalMode(boolean localModeOn) {
        getAmberGraph().setLocalMode(localModeOn);
    }


    /**
     * commit saves everything in the current transaction.
     */
    public void commit() {
        runPreCommitHooks();
        ((TransactionalGraph) graph).commit();
    }

    private void runPreCommitHooks() {
        for (AmberPreCommitHook preCommitHook: preCommitHooks) {
            preCommitHook.hook(getAmberGraph().getNewVertices(),
                    getAmberGraph().getModifiedVertices(),
                    getAmberGraph().getRemovedVertices(),
                    getAmberGraph().getNewEdges(),
                    getAmberGraph().getModifiedEdges(),
                    getAmberGraph().getRemovedEdges(),
                    this);
        }
    }


    /**
     * commit saves everything in the current session and records who did it and why with the transaction record.
     *
     * @param why the operation being performed when the transaction was committed
     */
    public Long commit(String why) {
        return commit("amberdb", why);
    }

    /**
     * commit saves everything in the current session and records who did it and why with the transaction record.
     *
     * @param who the username to associate with the transaction
     * @param why the operation they were fulfilling by commiting the transaction
     */
    public Long commit(String who, String why) {
        runPreCommitHooks();
        return getAmberGraph().commit(who, why);
    }


    /**
     * rollback rollback everything in the current transaction.
     */
    public void rollback() {
        ((TransactionalGraph) graph).rollback();
    }

    public JsonNode serializeToJson() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GraphSONWriter.outputGraph(graph.getBaseGraph(), bos);
        return new ObjectMapper().reader().readTree(bos.toString("UTF-8"));
    }


    protected FramedGraph<TransactionalGraph> openGraph(TransactionalGraph graph) {
        TransactionalGraph g = new OwnedGraph(graph);
        return framedGraphFactory.create(g);
    }


    /**
     * Finds a work by id.
     */
    public Work findWork(long objectId) {
        return findModelObjectById(objectId, Work.class);
    }


    /**
     * Finds a work by id or alias.
     */
    public Work findWork(String idOrAlias) {
        // @todo aliases
        try {
            return findWork(Long.parseLong(idOrAlias));
        } catch (NumberFormatException e) {
            return findWork(PIUtil.parse(idOrAlias));
        }
    }

    /**
     * Retrieves the hierarchy of works that leads to the specified object. Only retrieves basic summary information for
     * performance reasons. Invokes {@link #getHierarchySummaryForWork(long, Integer)}.
     *
     * This method limits the hierarchy depth to {@link #DEFAULT_MAX_HIERARCHY_DEPTH}.
     *
     * @param workId The work to retrieve the hierarchy for. This will be the LAST step in the hierarchy.
     * @return Ordered list of WorkSummaries for each step in the hierarchy that leads to the specified object.
     * @see #getHierarchySummaryForWork(long, Integer)
     */
    public List<WorkSummary> getHierarchySummaryForWork(long workId) {
        return getHierarchySummaryForWork(workId, null);
    }

    /**
     * Retrieves the hierarchy of works that leads to the specified object. Only retrieves basic summary information for
     * performance reasons.
     *
     * If the actual hierarchy depth exceeds the maxAncestors parameter, then the parentId of the first item will not be
     * null, indicating that it has a parent and therefore there is more to the hierarchy. Otherwise, the first item's
     * parent will always be null to indicate that it is the root of the hierarchy.
     *
     * @param workId The work to retrieve the hierarchy for. This will be the LAST step in the hierarchy.
     * @param maxAncestors The maximum number of ancestors to include in the hierarchy. This limits the depth of the
     *                     hierarchy, but always operates in REVERSE - i.e. the requested work plus (maxAncestors - 1)
     *                     ancestors. Defaults to {@link #DEFAULT_MAX_HIERARCHY_DEPTH} if not specified.
     * @return Ordered list of WorkSummaries for each step in the hierarchy that leads to the specified object.
     */
    public List<WorkSummary> getHierarchySummaryForWork(long workId, Integer maxAncestors) {
        int maxDepth = maxAncestors == null ? DEFAULT_MAX_HIERARCHY_DEPTH : maxAncestors;

        List<WorkSummary> summaries = new ArrayList<>();

        try (Handle handle = lookupsDbi.open()) {
            String sql = "select parent.id parentId,  \n" +
                    "           child.id childId,  \n" +
                    "           child.title,  \n" +
                    "           child.bibLevel,  \n" +
                    "           child.bibId,  \n" +
                    "           child.collection,  \n" +
                    "           child.form,  \n" +
                    "           child.recordSource,  \n" +
                    "           child.localSystemNumber,  \n" +
                    "           child.accessConditions,  \n" +
                    "           child.internalAccessConditions,  \n" +
                    "           child.creator,  \n" +
                    "           child.dateCreated,  \n" +
                    "           child.subUnitType,  \n" +
                    "           child.subUnitNo,  \n" +
                    "           child.sensitiveMaterial,  \n" +
                    "           child.digitalStatus,  \n" +
                    "           child.digitalStatusDate \n" +
                    " from work child \n" +
                    "   left join flatedge e on child.id = e.v_out and e.label = 'isPartOf' \n" +
                    "   left join work parent on e.v_in = parent.id \n" +
                    "   left join node n on n.id = child.id \n" +
                    " where n.type != 'Section' and child.id = :id ";

            Query query = handle.createQuery(sql);

            addSummary(query, workId, summaries, 1, maxDepth);

            Map<String, String> collections = new HashMap<>();

            for (ListLu item : getLookups().findLookupsFor("collection", "n")) {
                collections.put(item.getCode(), item.getValue());
            }

            for (WorkSummary summary : summaries) {
                summary.setCollectionName(collections.get(summary.getCollectionCode()));
            }

            Collections.reverse(summaries);
        }

        return summaries;
    }

    private void addSummary(Query query, Long id, List<WorkSummary> summaries, int currentDepth, int maxDepth) {
        query.bind("id", id);

        Map<String, Object> work = (Map<String, Object>) query.first();

        if (work != null) {
            WorkSummary summary = new WorkSummary();
            summary.setId(id);
            summary.setParentId((Long) work.get("parentId"));
            summary.setTitle((String) work.get("title"));
            summary.setCollectionCode((String) work.get("collection"));
            summary.setForm((String) work.get("form"));
            summary.setCreator((String) work.get("creator"));
            summary.setAccessConditions((String) work.get("accessConditions"));
            summary.setInternalAccessConditions((String) work.get("internalAccessConditions"));
            summary.setDigitalStatus((String) work.get("digitalStatus"));
            summary.setDigitalStatusDate((Date) work.get("digitalStatusDate"));
            summary.setDateCreated((Date) work.get("dateCreated"));
            summary.setRecordSource((String) work.get("recordSource"));
            summary.setLocalSystemNumber((String) work.get("localSystemNumber"));
            summary.setBibLevel((String) work.get("bibLevel"));
            summary.setBibId((String) work.get("bibId"));
            summary.setSubUnitType((String) work.get("subUnitType"));
            summary.setSubUnitNumber((String) work.get("subUnitNo"));
            summary.setSensitiveMaterial((String) work.get("sensitiveMaterial"));
            summary.setObjectId(PIUtil.format((Long) work.get("childId")));

            summaries.add(summary);

            if (summary.getParentId() != null && currentDepth < maxDepth) {
                addSummary(query, summary.getParentId(), summaries, currentDepth + 1, maxDepth);
            }
        }
    }

    /**
     * Finds some object and return it as the supplied model type.
     *
     * @param objectId the ID of the graph vertex you want to fetch
     * @param returnClass The type of the class that you expect the object to be
     * @param <T> The type of the class that you expect the object to be
     * @throws ClassCastException thrown if the specified type is not what the object actually is
     * @return an object of the specified type
     */
    public <T> T findModelObjectById(long objectId, Class<T> returnClass) {
        // TODO This should do some validation that the class is as expected, but that is almost impossible.
        T obj = graph.getVertex(objectId, returnClass);
        if (obj == null) {
            throw new NoSuchObjectException(objectId);
        }
        return obj;
    }

    /**
     * Finds some object and return it as the supplied model type.
     */
    public <T> T findModelObjectById(String objectId, Class<T> returnClass) {
        try {
            return findModelObjectById(Long.parseLong(objectId), returnClass);
        }
        catch (NumberFormatException nfe) {
            return findModelObjectById(PIUtil.parse(objectId), returnClass);
        }
    }


    /**
     * Finds a work by voyager number.
     */
    public Section findWorkByVn(String vnLink) {
        return graph.frame(graph.getVertices("bibId", vnLink).iterator().next(), Section.class);
    }


    /**
     * Finds a work by voyager number.
     */
    public Section findWorkByVn(long vnLink) {
        return graph.frame(graph.getVertices("bibId", Long.toString(vnLink)).iterator().next(), Section.class);
    }


    /**
     * Finds nodes that have a given value in a json string list property.
     * @param propertyName The property to search on
     * @param value The value to search for
     * @param <T> The class of Object to return (eg: Work, Copy, Node)
     */
    public <T> List<T> findModelByValueInJsonList(String propertyName, String value, Class<T> T) {
        List<T> nodes = new ArrayList<>();
        for (Vertex match : getAmberGraph().getVerticesByJsonListValue(propertyName, value)) {
            // add matched vertex from framed graph
            nodes.add(graph.getVertex(match.getId(), T));
        }
        return nodes;
    }


    /**
     * Finds nodes that have a property containing the given value.
     * @param propertyName The property to search on
     * @param value The value to search for
     * @param <T> The class of Object to return (eg: Work, Copy, Node)
     */
    public <T> List<T> findModelByValue(String propertyName, Object value, Class<T> T) {
        List<T> nodes = new ArrayList<>();
        for (Vertex match : getAmberGraph().getVertices(propertyName, value)) {
            // add matched vertex from framed graph
            nodes.add(graph.getVertex(match.getId(), T));
        }
        return nodes;
    }
    
    public VersionedVertex findVersionedVertex(long objectId) {
        return getAmberHistory().getVersionedGraph().getVertex(objectId);
    }

    /**
     * Creates a new work.
     *
     * @return the work
     */
    public Work addWork() {
        return graph.addVertex(null, Work.class);
    }


    /**
     * Noting deletion of all the vertices representing the work, its copies, and its copy files
     * within the session. This method will orphan any child works.
     */
    public void deleteWork(final Work work) {

        // delete copies of work
        Iterable<Copy> copies = work.getCopies();
        if (copies != null) {
            for (Copy copy : copies) {
                deleteCopy(copy);
            }
        }

        // descriptions
        for (Description desc : work.getDescriptions()) {
            graph.removeVertex(desc.asVertex());
        }

        // delete work
        graph.removeVertex(work.asVertex());
    }

    /**
     * Clear all changes made to the provided work, AND ITS RELATED RECORDS (e.g. copies, chldren, descriptions etc) from the current session
     *
     * @param work The modified work to revert
     */
    public void revertWork(Work work) {
        AmberGraph g = getAmberGraph();

        // recursively revert all children and their descendants
        Iterable<Work> children = work.getChildren();
        if (children != null) {
            for (Work child : children) {
                revertWork(child);
            }
        }

        Iterable<Copy> copies = work.getCopies();
        if (copies != null) {
            for (Copy copy : copies) {
                revertCopy(copy);
            }
        }

        for (Description desc : work.getDescriptions()) {
            g.revertVertex(desc.asVertex());
        }

        for (Tag desc : work.getTags()) {
            g.revertVertex(desc.asVertex());
        }

        for (Acknowledge ack : work.getAcknowledgements()) {
            g.revertEdge(ack.asEdge());

            if (ack.getParty() != null) {
                g.revertVertex(ack.getParty().asVertex());
            }
        }

        // revert work
        g.revertVertex(work.asVertex());
    }


    public Map<String, Integer> deleteWorksFast(Map<String, Integer> counts, final Work... works) {

        List<Long> ids = new ArrayList<>();
        for (Work w : works) {
            ids.add(w.getId());
        }
        loadMultiLevelWorks(ids);

        AmberGraph g = getAmberGraph();
        boolean prevMode = g.inLocalMode();
        g.setLocalMode(true);
        counts = deleteWorks(counts, works);
        g.setLocalMode(prevMode);

        return counts;
    }


    public void deleteWorksFast(final Work... works) {

        List<Long> ids = new ArrayList<>();
        for (Work w : works) {
            ids.add(w.getId());
        }
        loadMultiLevelWorks(ids);

        AmberGraph g = getAmberGraph();
        boolean prevMode = g.inLocalMode();
        g.setLocalMode(true);
        deleteWorks(works);
        g.setLocalMode(prevMode);
    }


    /**
     * Recursively delete a collection of Works and all their children (including Copies, Files
     * and Descriptions).
     *
     * @param works The works to be deleted
     */
    public void deleteWorks(final Work... works) {

        for (Work work : works) {

            /* first, get children works */
            List<Work> children = Lists.newArrayList(work.getChildren());

            /* to avoid cycles, next delete the work (plus all its sub-objects) */

            // copies
            for (Copy copy : work.getCopies()) {
                deleteCopy(copy);
            }

            // descriptions
            for (Description desc : work.getDescriptions()) {
                graph.removeVertex(desc.asVertex());
            }

            // the work itself
            graph.removeVertex(work.asVertex());

            /* finally, process the children */
            deleteWorks(children.toArray(new Work[children.size()]));
        }
    }


    /**
     * Delete all the vertices representing the copy, all its files and their descriptions.
     * @param copy The copy to be deleted
     */
    public void deleteCopy(final Copy copy) {
        for (File file : copy.getFiles()) {
            deleteFile(file);
        }
        graph.removeVertex(copy.asVertex());
    }

    /**
     * Clear all changes made to the provided copy, and its related files, from the current session
     *
     * @param copy The modified copy to revert
     */
    public void revertCopy(final Copy copy) {
        AmberGraph g = getAmberGraph();

        for (File file : copy.getFiles()) {
            revertFile(file);
        }

        g.revertVertex(copy.asVertex());
    }

    /**
     * Delete the vertices representing a file including its descriptions.
     * @param file The file to be deleted
     */
    public void deleteFile(final File file) {
        for (Description desc : file.getDescriptions()) {
            graph.removeVertex(desc.asVertex());
        }
        graph.removeVertex(file.asVertex());
    }

    /**
     * Clear all changes made to the provided file and any related descriptions from the current session
     *
     * @param file The modified file to revert
     */
    public void revertFile(final File file) {
        AmberGraph g = getAmberGraph();

        for (Description desc : file.getDescriptions()) {
            g.revertVertex(desc.asVertex());
        }

        g.revertVertex(file.asVertex());
    }


    /**
     * Noting deletion of all the vertices representing the work, its copies, and its copy files
     * within the session.
     * @param page The page to be deleted
     */
    public void deletePage(final Page page) {
        deleteWork(page);
    }
    public void revertPage(final Page page) {
        revertWork(page);
    }


    @Override
    public void close() {
        graph.shutdown();
        if (tempDir != null) {
            tempDir.delete();
        }
        if (blobStore != null) {
            blobStore.close();
        }
    }


    /**
     * Suspend suspends the current transaction.
     *
     * @return the id of the current transaction.
     */
    public long suspend() {
        return ((OwnedGraph) graph.getBaseGraph()).getAmberGraph().suspend();
    }


    /**
     * Recover recovers the suspended transaction of the specified txId.
     *
     * @param txId
     *            the transaction id of the suspended transaction to reover.
     */
    public void recover(Long txId) {
        ((OwnedGraph) graph.getBaseGraph()).getAmberGraph().resume(txId);
    }


    /**
     * getGraph
     *
     * @return the framed graph, handly for groovy tests.
     */
    public FramedGraph<TransactionalGraph> getGraph() {
        return graph;
    }


    /**
     * Wrapper around the graph that stores a reference to the AmberDb which
     * owns it. See {@link AmberSession#ownerOf(Graph)}.
     */
    private class OwnedGraph extends WrappedGraph<TransactionalGraph> implements TransactionalGraph  {

        public OwnedGraph(TransactionalGraph baseGraph) {
            super(baseGraph);
        }

        public AmberSession getOwner() {
            return AmberSession.this;
        }

        public AmberGraph getAmberGraph() {
            return (AmberGraph) baseGraph;
        }

        @Override
        public Vertex addVertex(Object id) {
            /* Workaround a bug in AdjcancyAnnotationHandler in Frames 2.4.0.
             * See https://github.com/tinkerpop/frames/commit/b139067d576
             * Once Frames 2.4.1 is released we may remove this method.
             */
            if (id instanceof Class) {
                id = null;
            }
            return super.addVertex(id);
        }

        @Override
        public void commit() {
            baseGraph.commit();
        }

        @Override
        public void rollback() {
            baseGraph.rollback();
        }

        @Override
        @Deprecated
        public void stopTransaction(Conclusion arg0) {}
    }


    /**
     * Returns the AmberDb instance that owns the given graph.
     */
    public static AmberSession ownerOf(Graph graph) {
        if (graph instanceof OwnedGraph) {
            return ((OwnedGraph)graph).getOwner();
        } else if (graph instanceof WrapperGraph<?>) {
            return ownerOf(((WrapperGraph<?>)graph).getBaseGraph());
        } else {
            throw new RuntimeException("Not an AmberDb graph: " + graph);
        }
    }


    /**
     * Returns the DOSS BlobStore for this AmberDb.
     */
    public BlobStore getBlobStore() {
        return blobStore;
    }

    /**
     * Get the ids of objects that have been modified since a given time. If an
     * edge has been modified, then both its connected objects (vertices) are
     * returned
     *
     * @param from
     *            time of first modifications to be included
     * @return a map of object ids and how they changed
     */
    public Map<Long, String> getModifiedObjectIds(Date from) {
        return getAmberHistory().getModifiedObjectIds(from);
    }

    public ModifiedObjectsQueryResponse getArticlesForIndexing(ModifiedObjectsQueryRequest request) {
        return getAmberHistory().getArticlesForIndexing(request);
    }
    
    public ModifiedObjectsQueryResponse getModifiedObjectIds(ModifiedObjectsQueryRequest request) {
        return getAmberHistory().getModifiedObjectIds(request);
    }

    public AmberTransaction getTransaction(long id) {
        return getAmberGraph().getTransaction(id);
    }

    /**
     * Get the ids of works that have been modified since a given time.
     *
     * @param when
     *            time of first modifications to be included
     * @return a map of work ids and how the work was changed
     */
    public Map<Long, String> getModifiedWorkIds(Date when) {
        return getAmberHistory().getModifiedWorkIds(when);
    }


    public AmberHistory getAmberHistory() {
        return new AmberHistory(getAmberGraph());
    }


    /**
     * Removes a suspended session from the database. Note: this can be a session other than this current one.
     *
     * @param sessId
     *            The id of the session to be removed.
     */
    public void removePersistedSession(Long sessId) {
        getAmberGraph().destroySession(sessId);
    }

    /**
     * Commits the persisted session with the given id. Note: this can be a session other than this current one.
     *
     * @param sessId
     *            The id of the session to be committed.
     * @param user
     *            Who is committing the session
     * @param operation
     *            The purpose of why the session has been committed
     */
    public void commitPersistedSession(Long sessId, String user, String operation) {
        getAmberGraph().commitPersistedSession(sessId, user, operation);
    }

    public Tag addTag() {
        return graph.addVertex(null, Tag.class);
    }

    public Tag addTag(String name) {
        Tag t = addTag();
        t.setName(name);
        return t;
    }
    
    public Tag addTagForCollection(String collection, String tagName, String attributeName, boolean multivaluedAttribute) throws IOException {
        Tag tag = addTag();
        tag.setName(tagName);
        ObjectMapper om = new ObjectMapper();
        List<Work> works = findModelByValue("collection", collection, Work.class);
        LinkedHashMap<String, List<Long>> map = new LinkedHashMap<>();
        for (Work work : works) {
            Object val = work.asVertex().getProperty(attributeName);
            if (val != null) {
                List<String> valList = null;
                if (multivaluedAttribute) {
                    valList = om.readValue(val.toString(), new TypeReference<List<String>>(){});
                } else {
                    valList = new ArrayList<>();
                    valList.add(val.toString());
                }
                
                for (String value : valList) {
                    mapWork(map, work, value);
                }
            }
        }
        tag.setDescription(om.writeValueAsString(map));
        return tag;
    }
    
    private void mapWork(LinkedHashMap<String, List<Long>> map, Work work, String value) {
        List<Long> mappedWorks = map.get(value);
        if (mappedWorks == null) {
            mappedWorks = new ArrayList<Long>();
            map.put(value.toString(), mappedWorks);
        }
        mappedWorks.add(work.getId());
    }

    public Tag findTag(String name) {
        for (Tag t : getAllTags()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }


    public Tag getTag(Long id) {
        return findModelObjectById(id, Tag.class);
    }


    public Iterable<Tag> getAllTags() {
        return graph.getVertices("type", "Tag", Tag.class);
    }

    public void deleteTag(Tag tag) {
        graph.removeVertex(tag.asVertex());
    }

    private Party addParty() {
        return graph.addVertex(null, Party.class);
    }

    public Party addParty(String name) {
        return addParty(name, null, null);
    }

    public Party addParty(String name, String orgUrl, String logoUrl) {
        if (StringUtils.isEmpty(name)) { //name is mandatory
            return null;
        }
        Party party = addParty();

        party.setName(name);
        party.setOrgUrl(orgUrl);
        party.setLogoUrl(logoUrl);
        party.setSuppressed(false);
        return party;
    }

    public Iterable<Party> getAllParties() {
        return graph.getVertices("type", "Party", Party.class);
    }

    public Party findParty(String name) {
        for (Party party : getAllParties()) {
            if (StringUtils.equalsIgnoreCase(party.getName(), name)) {
                return party;
            }
        }
        return null;
    }

    /**
     * Recursively delete a Work and all its children (including Copies, Files
     * and Descriptions). Returns an updated count of the different object types
     * deleted added to an existing map of keys to counters.
     *
     * @param counts
     *            A map containing counts of the different object types deleted
     * @param works
     *            The works to be deleted
     */
    public Map<String, Integer> deleteWorks(Map<String, Integer> counts, final Work... works) {

        for (Work work : works) {

            /* first, get children works */
            List<Work> children = Lists.newArrayList(work.getChildren() == null ? Collections.emptyList() : work.getChildren());

            /* next, to avoid cycles, delete the work (plus all its sub-objects) */

            // copies
            Iterable<Copy> copies = work.getCopies();
            if (copies != null) {
                for (Copy copy : work.getCopies()) {
                    deleteCopy(counts, copy);
                }
            }

            // descriptions
            Iterable<Description> descriptions = work.getDescriptions();
            if (descriptions != null) {
                for (Description desc : work.getDescriptions()) {
                    graph.removeVertex(desc.asVertex());
                    increment(counts, "Description");
                }
            }

            // the work itself
            graph.removeVertex(work.asVertex());
            increment(counts, "Work");

            /* finally, process the children */
            deleteWorks(counts, children.toArray(new Work[children.size()]));
        }

        return counts;
    }


    private static final int MAX_DEPTH = 15;
    /**
     * Return a Map of Work ids for Works that are 'represented' by Copies that are
     * descendants of the given list of Works (ie: Copies of the listed Works or any
     * of their descendants). The Work id maps to its representing Copy id.
     *
     * @param representedBy
     *            A map with Work ids as the key mapping to their representing Copy's id
     * @param depth
     *            Used to prevent cycles while traversing the graph. Maximum depth set by MAX_DEPTH
     * @param works
     *            The Works to be searched for representing Copies
     */
    protected Map<Long, Long> getWorksRepresentedByCopiesOf(Map<Long, Long> representedBy, int depth, final Work... works) {
        if (depth >= MAX_DEPTH) return representedBy; // possibly throw Exception instead ?
        for (Work work : works) {
            // process this work's copies
            for (Copy copy : work.getCopies()) {
                for (Work rep : copy.getRepresentedWorks()) {
                    representedBy.put(rep.getId(), copy.getId());
                }
            }
            // process this work's descendant works
            getWorksRepresentedByCopiesOf(representedBy, depth+1, Iterables.toArray(work.getChildren(), Work.class));
        }
        return representedBy;
    }
    public Map<Long, Long> getWorksRepresentedByCopiesOf(final Work... works) {
        return getWorksRepresentedByCopiesOf(new HashMap<Long, Long>(), 0, works);
    }


    /**
     * Delete all the vertices representing the copy, all its files and their
     * descriptions. Returns an updated count of the different object types
     * deleted added to an existing map of keys to counters.
     *
     * @param counts
     *            A map of object type to number deleted
     * @param copy
     *            The copy to be deleted
     */
    public Map<String, Integer> deleteCopy(Map<String, Integer> counts, final Copy copy) {

        for (File file : copy.getFiles()) {
            for (Description desc : file.getDescriptions()) {
                graph.removeVertex(desc.asVertex());
                increment(counts, "Description");
            }
            graph.removeVertex(file.asVertex());
            increment(counts, "File");
        }
        graph.removeVertex(copy.asVertex());
        increment(counts, "Copy");

        return counts;
    }


    /**
     * Convenience method to increment a count in a map of keys to counts
     *
     * @param countMap
     *            The map to update
     * @param key
     *            The key of the count to increment
     */
    private void increment(Map<String, Integer> countMap, String key) {
        Integer count = countMap.get(key);
        if (count == null) {
            count = 1;
        } else {
            count = count + 1;
        }
        countMap.put(key, count);
    }


    /**
     * Load the given works into memory. IMPORTANT: Please read the return value
     * as it is not an intuitive result.
     *
     * @param ids
     *            The list of works to load
     * @return Only the vertices related to these works already saved to amber.
     *         Vertices related to these works that have not yet been saved will
     *         NOT appear here. Parents of the work are not included
     */
    public List<Vertex> loadMultiLevelWorks(final Long... ids) {

        AmberGraph g = getAmberGraph();

        List<Vertex> components;
        try (AmberMultipartQuery q = g.newMultipartQuery(ids)) {

            String numPartsInAmberQuery =
                    "SELECT COUNT(flatedge.id) num "
                    + "FROM flatedge, v1 "
                    + "WHERE v1.step = %d "
                    + "AND v1.vid = flatedge.v_in "
                    + "AND flatedge.label = 'isPartOf'; ";

            QueryClause qc = q.new QueryClause(BRANCH_FROM_PREVIOUS, new String[] { "isPartOf" }, Direction.IN);

            int step;
            q.startQuery();

            boolean moreParts = true;
            while (moreParts) {
                step = q.step + 1; // add 1 because the checkQuery is run after the following step is executed
                List<Map<String, Object>> numPartsInAmberResult = q.continueWithCheck(String.format(numPartsInAmberQuery, step), qc);
                Long numParts = (Long) numPartsInAmberResult.get(0).get("num");
                if (numParts.equals(0L)) {
                    moreParts = false;
                }
            }

            // get all the copies, files etc
            q.continueWithCheck(null,
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"deliveredOn"}, Direction.IN), // gets delivery parent
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"represents"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"isCopyOf"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"isFileOf"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"descriptionOf"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"tags"}, Direction.IN),
                    q.new QueryClause(BRANCH_FROM_ALL, new String[] {"acknowledge"}, Direction.OUT)
                    );
            components = q.getResults(true);
        }
        return components;
    }
    
    public List<Vertex> loadParentsAndCopies(final List<Long> ids) {
        AmberGraph g = getAmberGraph();

        List<Vertex> components;
        AmberQuery q = g.newQuery(ids);

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {0});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {0});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {1,2});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {1,2});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {3,4});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {3,4});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {5,6});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {5,6});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {7,8});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {7,8});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {9,10});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {9,10});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {11,12});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {11,12});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {13,14});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {13,14});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {15,16});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {15,16});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {17,18});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {17,18});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {19,20});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {19,20});

        q.branch(BRANCH_FROM_LISTED, new String[] { "isPartOf"}, Direction.OUT, new Integer[] {21,22});
        q.branch(BRANCH_FROM_LISTED, new String[] { "deliveredOn"}, Direction.IN, new Integer[] {21,22});


        // get all the copies, files etc
        q.branch(BRANCH_FROM_ALL, new String[]{"isCopyOf"}, Direction.IN);
        components = q.execute(false);

        return components;

    }

    /**
     * loadWorks return a stream of works given input record ids.  This method is designed to 
     * be applied during reporting or mass work records export.  Please note this method does not
     * traverse each work records from corresponding record ids, ie. not providing the children 
     * records.
     * @param ids are input record ids.
     * @return stream of work records corresponding to the input record ids.
     */
    public Stream<Work> loadWorks(final List<Long> ids) {
        AmberGraph g = getAmberGraph();
        AmberQuery q = g.newQuery(ids);
        q.setLoadGraph(false);
        return q.execute().stream().map(record -> graph.frame(record, Work.class));
    }

    /**
     * loadWorks return a stream of works in session given input record ids.  This method is designed to 
     * be applied during reporting or mass work records export.  Please note this method does not
     * traverse each work records from corresponding record ids, ie. not providing the children 
     * records.
     * @param ids
     * @return
     */
    public Stream<Work> loadWorksInSession(final List<Long> ids) {
        AmberGraph g = getAmberGraph();
        AmberQuery q = g.newQuery(ids);
        q.setInSession(true);
        q.setLoadGraph(false);
        return q.execute().stream().map(record -> graph.frame(record, Work.class));
    }
    
    public List<Vertex> loadMultiLevelWorks(final List<Long> ids) {
        return loadMultiLevelWorks(ids.toArray(new Long[ids.size()]));
    }
    
    
    
    public <T> List<T> findModelByAliasValue(String propertyName, String value, Class<T> T) {
        List<T> nodes = new ArrayList<>();
        for (Vertex match : getAmberGraph().getVerticesByAliasValue(propertyName, value)) {
            // add matched vertex from framed graph
            nodes.add(graph.getVertex(match.getId(), T));
        }
        return nodes;
    }
}
