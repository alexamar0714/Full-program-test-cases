package amberdb;


import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import amberdb.graph.AmberGraph;
import amberdb.version.TElementDiff;
import amberdb.version.TTransition;
import amberdb.version.TVertexDiff;
import amberdb.version.VersionedGraph;
import amberdb.version.VersionedVertex;


public class TransactionIndexer {

    
    private VersionedGraph graph;
    private final Logger log = LoggerFactory.getLogger(TransactionIndexer.class);
    
    
    public TransactionIndexer(AmberGraph graph) {
        this.graph = new VersionedGraph(graph.dbi());
    }

    
    public Set<Long>[] findObjectsToBeIndexed(Long startTxn, Long endTxn) {
        
        log.debug("loading graph: Txn-" + startTxn + " -> Txn-" + endTxn);
        graph.loadTransactionGraph(startTxn, endTxn, true);
        
        // get our transaction copies (where their file or file descriptions have been affected)
        // we do this because it is really slow to find the copies for files and/or descriptions
        // by traversing the versioned graph :(
        graph.getTransactionCopies(startTxn, endTxn);

        Set<Long> modifiedObjects = new HashSet<>();
        Set<Long> deletedObjects = new HashSet<>();
        
        Set<Long>[] changedObjectSets = new Set[] { modifiedObjects, deletedObjects }; 
        int numVerticesProcessed = 0;
        int numChanged = 0;
        
        vertexLoop: for (VersionedVertex v : graph.getVertices()) {
            numVerticesProcessed++;

            TVertexDiff diff = v.getDiff(startTxn, endTxn);
            TTransition change = diff.getTransition();
            if (change == TTransition.UNCHANGED) continue vertexLoop;
            numChanged++;
            
            // The type attribute of an NLA vertex shouldn't change
            String type = (String) getUnchangedProperty(diff, "type");
            if (type == null) {
                log.debug("No type or type changed: {}", diff);
                continue vertexLoop;
            }

            Long id = v.getId();
            
            // skip object if it is a file or file description as
            // we've collected those copies earlier
            switch (type) {
            case "Description":
            case "IPTC":
            case "GeoCoding":
            case "CameraData":
            case "File":
            case "ImageFile":
            case "SoundFile":
            case "MovingImageFile":
                continue vertexLoop;
            }

            // check a change in access conditions
            Object ac = diff.getProperty("internalAccessConditions");
            if (change == TTransition.NEW && ac != null && ac.equals("Closed")) {
                log.debug("Internal Access Conditions for NEW object is 'Closed'. Not indexing {}", id);
                continue vertexLoop;
            }
            if (change == TTransition.MODIFIED && ac instanceof Object[]) {
                Object[] cond = (Object[]) ac;
                if (cond.length > 1 && cond[1] != null && cond[1].equals("Closed")) {
                    log.debug("Internal Access Conditions changed: {} -> {} for Item {}", cond[0], cond[1], id);
                    deletedObjects.add(id);
                    continue vertexLoop;
                }
            }

            switch (change) {
            case DELETED:
                deletedObjects.add(id);
                break;
            case NEW:
            case MODIFIED:
                modifiedObjects.add(id);
                break;
            }
        }
        log.debug("Vertices processed: " + numVerticesProcessed);
        log.debug("Changed vertices processed: " + numChanged);
        
        // give deletion precedence over modification
        modifiedObjects.removeAll(deletedObjects);
        
        return changedObjectSets;
    }

    
    /**
     * Return a property from a TDiff if it hasn't changed between versions. The
     * element can have been deleted or created, but the property cannot have 2
     * different values.
     * 
     * @param diff
     *            the element diff to get the property from
     * @param propertyName
     *            the property name
     * @return The property if it hasn't changed, null otherwise
     */
    private Object getUnchangedProperty(TElementDiff diff, String propertyName) {
        Object obj = diff.getProperty(propertyName);
        if (obj instanceof Object[]) {
            Object[] oArray = (Object[]) obj;
            if (oArray.length == 1) {
                return oArray[0];
            }
            return null;
        }
        return obj;
    }
}
