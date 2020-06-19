package amberdb.graph;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.tinkerpop.blueprints.Direction;

import amberdb.query.ModifiedObjectsBetweenTransactionsQueryRequest;
import amberdb.query.ModifiedObjectsQueryRequest;
import amberdb.query.ModifiedObjectsQueryResponse;
import amberdb.query.ObjectsQuery;
import amberdb.util.AmberModelTypes;
import amberdb.version.TEdgeDiff;
import amberdb.version.TTransition;
import amberdb.version.TVertexDiff;
import amberdb.version.VersionedEdge;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;


public class AmberHistory {

    private VersionedGraph vGraph;
    private AmberGraph graph;
    
    public AmberHistory(AmberGraph graph) {
        this.graph = graph;
        vGraph = new VersionedGraph(graph.dbi());
    }

    public VersionedGraph loadChangedGraphForPeriod(Date startDate, Date endDate) {
        List<Long> txnsBetween = getTxnsBetween(startDate, endDate);
        return loadChangedGraphForPeriod(txnsBetween.get(0), txnsBetween.get(txnsBetween.size()-1));
    }
    
    public VersionedGraph loadChangedGraphForPeriod(Long txn1, Long txn2) {
        vGraph.loadTransactionGraph(txn1, txn2, true);
        return vGraph;
    }

    /**
     * @return the set of versioned vertices present in the given versioned
     *         graph that have changed over the txn period
     */
    public Set<VersionedVertex> getChangedVertices(VersionedGraph graph, Long txn1, Long txn2) {
        Set<VersionedVertex> changedVertices = new HashSet<>();
        for (VersionedVertex v : vGraph.getVertices()) {
            TVertexDiff diff = v.getDiff(txn1, txn2);
            if (diff.getTransition() != TTransition.UNCHANGED) {
                changedVertices.add(v);
            }
        }
        return changedVertices;
    }    

    
    /**
     * @return the set of versioned edges present in the given versioned
     *         graph that have changed over the txn period
     */
    public Set<VersionedEdge> getChangedEdges(VersionedGraph graph, Long txn1, Long txn2) {
        Set<VersionedEdge> changedEdges = new HashSet<>();
        for (VersionedEdge e : vGraph.getEdges()) {
            TEdgeDiff diff = e.getDiff(txn1, txn2);
            if (diff.getTransition() != TTransition.UNCHANGED) {
                changedEdges.add(e);
            }
        }
        return changedEdges;
    }

    protected ModifiedObjectsQueryResponse getModifiedObjectIds(List<Long> transactions) {
        ObjectsQuery query = new ObjectsQuery(graph);
        return query.getModifiedObjectIds(new ModifiedObjectsBetweenTransactionsQueryRequest(transactions, null, null, false, 0, Integer.MAX_VALUE));
    }
    
    protected Map<Long, String> getModifiedWorkIds(List<Long> transactions) {
        Map<Long, String> modifiedWorks = new HashMap<>();
        Map<Long, String> modifiedObjs = getModifiedObjectIds(transactions).getModifiedObjects();

        for (Long id : modifiedObjs.keySet()) {
            Set<VersionedVertex> works = getWorksForObject(id, new HashSet<VersionedVertex>());
            String changeToObj = modifiedObjs.get(id);
            for (VersionedVertex v : works) {
                Long workId = v.getId();
                String changeToWork = modifiedWorks.get(workId);
                
                // don't overwrite a delete with anything else 
                if (TTransition.DELETED.toString().equals(changeToWork)) { 
                    continue;
                }

                // only write as a delete if it's the work itself that has been deleted
                if (workId.equals(id) && changeToObj.equals(TTransition.DELETED.toString())) {
                    changeToWork = TTransition.DELETED.toString();
                } else {
                    changeToWork = TTransition.MODIFIED.toString();
                }
                
                modifiedWorks.put(workId, changeToWork);
            }
        }
        return modifiedWorks;
    }

    public Map<Long, String> getModifiedWorkIds(Date from) {
        return getModifiedWorkIds(getTxnsSince(from));
    }

    public Map<Long, String> getModifiedWorkIds(Date from, Date to) {
        return getModifiedWorkIds(getTxnsBetween(from, to));
    }
    
    public Map<Long, String> getModifiedObjectIds(Date from) {
        return getModifiedObjectIds(getTxnsSince(from)).getModifiedObjects();
    }

    public Map<Long, String> getModifiedOBjectIds(Date from, Date to) {
        return getModifiedObjectIds(getTxnsBetween(from, to)).getModifiedObjects();
    }

    public Map<Long, String> getModifiedObjectIds(Date from, Predicate<VersionedVertex> filterPredicate) {
        ObjectsQuery query = new ObjectsQuery(graph);
        return query.getModifiedObjectIds(new ModifiedObjectsBetweenTransactionsQueryRequest(getTxnsSince(from), filterPredicate, null, false, 0, Integer.MAX_VALUE)).getModifiedObjects();
    }

    public Map<Long, String> getModifiedOBjectIds(Date from, Date to, Predicate<VersionedVertex> filterPredicate) {
        ObjectsQuery query = new ObjectsQuery(graph);
        return query.getModifiedObjectIds(new ModifiedObjectsBetweenTransactionsQueryRequest(getTxnsBetween(from, to), filterPredicate, null, false, 0, Integer.MAX_VALUE)).getModifiedObjects();
    }

    public ModifiedObjectsQueryResponse getModifiedObjectIds(ModifiedObjectsQueryRequest request) {
        ObjectsQuery query = new ObjectsQuery(graph);
        return query.getModifiedObjectIds(new ModifiedObjectsBetweenTransactionsQueryRequest(request, new ModifiedObjectsBetweenTransactionsQueryRequest.TransactionsBetweenFinder() {
            
            @Override
            public List<Long> getTransactionsBetween(Date startTime, Date endTime) {
                return getTxnsBetween(startTime, endTime);
            }
        }));
    }
    
    public ModifiedObjectsQueryResponse getArticlesForIndexing(ModifiedObjectsQueryRequest request) {
        ObjectsQuery query = new ObjectsQuery(graph);

        return query.getArticlesForIndexing(new ModifiedObjectsBetweenTransactionsQueryRequest(request, new ModifiedObjectsBetweenTransactionsQueryRequest.TransactionsBetweenFinder() {
            
            @Override
            public List<Long> getTransactionsBetween(Date startTime, Date endTime) {
                return getTxnsBetween(startTime, endTime);
            }
        }));
        
    }

    private Set<VersionedVertex> getWorksForObject(Long id, Set<VersionedVertex> works) {
        
        VersionedVertex v = vGraph.getVertex(id);
        String type = v.getAtTxnOrLast(Long.MAX_VALUE).getProperty("type");
        
        if (AmberModelTypes.isDescription(type)) {
            for (VersionedVertex parent : v.getVertices(Direction.OUT, "descriptionOf")) {
                getWorksForObject(parent.getId(), works);
            }        
        } else if (AmberModelTypes.isFile(type)) {
            for (VersionedVertex parent : v.getVertices(Direction.OUT, "isFileOf")) {
                getWorksForObject(parent.getId(), works);
            }        
        } else if (AmberModelTypes.isCopy(type)) {
            for (VersionedVertex parent : v.getVertices(Direction.OUT, "isCopyOf")) {
                getWorksForObject(parent.getId(), works);
            }        
        } else if (AmberModelTypes.isWork(type)) {
            works.add(v);
        } 
        return works;
    }
    
    
    public List<Long> getTxnsSince(Date time) {
        return vGraph.dao().getTransactionsSince(time.getTime());
    }

    public List<Long> getTxnsBetween(Date startTime, Date endTime) {
        return vGraph.dao().getTransactionsBetween(startTime.getTime(), endTime.getTime());
    }

    
    public VersionedGraph getVersionedGraph() {
        return vGraph;
    }
}