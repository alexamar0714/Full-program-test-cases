package amberdb.query;

import amberdb.AmberSession;
import amberdb.enums.BibLevel;
import amberdb.graph.*;
import amberdb.model.Copy;
import amberdb.model.Work;
import amberdb.relation.*;
import com.google.common.base.Joiner;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.LongMapper;
import org.skife.jdbi.v2.util.StringMapper;

import java.util.*;

public class WorksQuery {

    public static List<Work> getWorks(AmberSession sess, List<Long> ids) {
        List<Work> works = new ArrayList<>();
        List<Vertex> verts = sess.getAmberGraph().newQuery(ids).execute();
        for (Vertex v : verts) {
            works.add(sess.getGraph().frame(v, Work.class));
        }
        return works;
    }
    
    public static Map<Long, Work> getWorksMap(AmberSession sess, List<Long> ids) {
        Map<Long, Work> works = new HashMap();
        AmberQuery query = sess.getAmberGraph().newQuery(ids);
        query.branch(BranchType.BRANCH_FROM_PREVIOUS, Arrays.asList(IsPartOf.label), Direction.OUT);
        query.branch(BranchType.BRANCH_FROM_LISTED, Arrays.asList(DeliveredOn.label), Direction.IN, Arrays.asList(0));
        query.branch(BranchType.BRANCH_FROM_ALL, Arrays.asList(Tags.label), Direction.IN);
        Map<Long, AmberTransaction> firstTransactionMap = getFirstTransactions(sess, ids);
        Map<Long, AmberTransaction> lastTransactionMap = getLastTransactions(sess, ids);
        List<Vertex> verts = query.execute();
        for (Vertex v : verts) {
            Work work = sess.getGraph().frame(v, Work.class);
            populateFirstTransactionDetails(work, firstTransactionMap);
            populateLastTransactionDetails(work, lastTransactionMap);
            work.getIPTC();
            works.put(Long.valueOf(work.getId()), work);
        }
        return works;
    }

    private static void populateFirstTransactionDetails(Work work, Map<Long, AmberTransaction> map) {
        AmberTransaction transaction = map.get(work.getId());
        if (transaction != null) {
            work.asVertex().setProperty("createdOn", new Date(transaction.getTime()));
            work.asVertex().setProperty("createdBy", transaction.getUser());
        }
    }

    private static void populateLastTransactionDetails(Work work, Map<Long, AmberTransaction> map) {
        AmberTransaction transaction = map.get(work.getId());
        if (transaction != null) {
            work.asVertex().setProperty("modifiedOn", new Date(transaction.getTime()));
            work.asVertex().setProperty("modifiedBy", transaction.getUser());
        }
    }

    public static List<Copy> getCopiesWithWorks(AmberSession sess, List<Long> copyIds) {
        List<Copy> copies = new ArrayList<>();
        AmberQuery query = sess.getAmberGraph().newQuery(copyIds);
        query.branch(BranchType.BRANCH_FROM_ALL, new String[] { "isCopyOf" }, Direction.OUT);
        List<Vertex> vertices = query.execute();
        for (Vertex v : vertices) {
            if (StringUtils.equalsIgnoreCase((String) v.getProperty("type"), "Copy")) {
                copies.add(sess.getGraph().frame(v, Copy.class));
            }
        }
        return copies;
    }

    public static Map<Long, Copy> getCopiesWithWorksMap(AmberSession sess, List<Long> copyIds) {
        Map<Long, Copy> copies = new HashMap<>();
        AmberQuery query = sess.getAmberGraph().newQuery(copyIds);
        query.branch(BranchType.BRANCH_FROM_ALL, new String[] {IsCopyOf.label}, Direction.OUT);
        query.branch(BranchType.BRANCH_FROM_LISTED, Arrays.asList(IsSourceCopyOf.label), Direction.OUT, Arrays.asList(0));
        query.branch(BranchType.BRANCH_FROM_LISTED, Arrays.asList(IsFileOf.label), Direction.IN, Arrays.asList(0));
		query.branch(BranchType.BRANCH_FROM_LISTED, Arrays.asList(Tags.label), Direction.IN, Arrays.asList(0));
        List<Vertex> vertices = query.execute();
        for (Vertex v : vertices) {
            if (StringUtils.equalsIgnoreCase((String) v.getProperty("type"), "Copy")) {
                Copy copy = sess.getGraph().frame(v, Copy.class);
                copies.put(Long.valueOf(copy.getId()), copy);
            }
        }
        return copies;
    }
    
    /**
     * Find the distinct bib levels of all the parents of the specified list of work ids
     */
    public static Set<BibLevel> getDistinctParentBibLevels(AmberSession sess, List<Long> workIds){
        String sql = "" +
                "SELECT DISTINCT bibLevel " +
                "FROM work w, flatedge e " +
                "WHERE e.v_in = w.id " +
                "AND e.label = 'isPartOf' " +
                "AND e.v_out IN (" + Joiner.on(",").join(workIds) + ") ";
        return getDistinctBibLevels(sess, sql);
    }
    
    /**
     * Find the distinct bib levels of all the children of the specified list of work ids
     */
    public static Set<BibLevel> getDistinctChildrenBibLevels(AmberSession sess, List<Long> workIds){
        String sql = "" +
                "SELECT DISTINCT bibLevel " +
                "FROM work w, flatedge e " +
                "WHERE e.v_out = w.id " +
                "AND e.label = 'isPartOf' " +
                "AND e.v_in in (" + Joiner.on(",").join(workIds) + ") ";
        return getDistinctBibLevels(sess, sql);
    }
    
    private static Set<BibLevel> getDistinctBibLevels(AmberSession sess, final String sql){
        List<String> bibLevelCodes = new ArrayList<>();
        try (Handle h = sess.getAmberGraph().dbi().open()) {
            bibLevelCodes = h.createQuery(sql).map(StringMapper.FIRST).list();
        }
        Set<BibLevel> bibLevels = new HashSet<>();
        for (String code : bibLevelCodes) {
            BibLevel bibLevel = BibLevel.fromString(code);
            if (bibLevel != null){
                bibLevels.add(bibLevel);
            }
        }
        return bibLevels;
    }
    
    private static Map<Long, AmberTransaction> getFirstTransactions(AmberSession sess, List<Long> workIds){
        if (CollectionUtils.isNotEmpty(workIds)) {
            String sql = "" +
                    "SELECT t1.time, t1.user, t1.operation, t2.transaction_id, t2.vertex_id " +
                    "FROM transaction t1, " +
                    " (SELECT MIN(t.id) transaction_id, v.id vertex_id from transaction t, node_history v " +
                    " WHERE t.id = v.txn_start and v.id in (" + Joiner.on(",").join(workIds) + ") group by v.id) t2 " +
                    "WHERE t1.id = t2.transaction_id ";
            return getTransactions(sess, sql);
        }
        return new HashMap<>();
    }

    private static Map<Long, AmberTransaction> getLastTransactions(AmberSession sess, List<Long> workIds){
        if (CollectionUtils.isNotEmpty(workIds)) {
            String sql = "" +
                    "SELECT t1.time, t1.user, t1.operation, t2.transaction_id, t2.vertex_id " +
                    "FROM transaction t1, " +
                    " (SELECT MAX(t.id) transaction_id, v.id vertex_id from transaction t, node v " +
                    " WHERE t.id = v.txn_start and v.id in (" + Joiner.on(",").join(workIds) + ") group by v.id) t2 " +
                    "WHERE t1.id = t2.transaction_id ";
            return getTransactions(sess, sql);
        }
        return new HashMap<>();
    }

    private static Map<Long, AmberTransaction> getTransactions(AmberSession sess, String sql){
        Map<Long, AmberTransaction> map = new HashMap<>();
        List<AmberVertexTransaction> list;
        try (Handle h = sess.getAmberGraph().dbi().open()) {
            list = h.createQuery(sql).map(new VertexTransactionMapper()).list();
        }
        for (AmberVertexTransaction transaction : list) {
            map.put(transaction.getVertexId(), transaction.getTransaction());
        }
        return map;
    }

    /**
     * Find the first N vertex ids order by the txn_start column desc among the specified list of vertex ids
     * @param vertexIds input vertex Ids
     * @param n the first N rows returned by the query
     */
    public static List<Long> getNLastCreatedVertexIds(AmberSession sess, List<Long> vertexIds, long n){
        if (CollectionUtils.isNotEmpty(vertexIds)){
            String sql = "SELECT id FROM node WHERE id IN (" + Joiner.on(",").join(vertexIds) + ") GROUP BY id ORDER BY MIN(txn_start) DESC LIMIT :n";
            try (Handle h = sess.getAmberGraph().dbi().open()) {
                return h.createQuery(sql).bind("n", n).map(LongMapper.FIRST).list();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Find the a list of work ids given the specified work properties (name/value pair)
     *
     * Only string(STR) type is catered for now.
     *
     * Please be aware of the performance. You shouldn't use it to return a large result set.
     * For example, you shouldn't use it to return collection = 'nla.pic' and recordSource = 'voyager'
     *
     * You should specify at least one search criteria that limits the result to only a few records
     * For example, return collection = 'nla.pic' and recordSource = 'voyager' and title = 'abcde'
     * @return
     */
    public static List<Long> getWorkIdsByProperties(AmberSession sess, List<WorkProperty> workProperties){
        if (CollectionUtils.isNotEmpty(workProperties)){
            try (Handle h = sess.getAmberGraph().dbi().open()) {
                PropertyQueryAssembler assembler = new PropertyQueryAssembler(workProperties);
                return assembler.query(h).list();
            }
        }
        return new ArrayList<>();
    }
}
