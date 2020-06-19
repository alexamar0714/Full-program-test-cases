package amberdb.query;

import java.util.LinkedHashMap;

public class ModifiedObjectsQueryResponse {
    private LinkedHashMap<Long, ModifiedObject> modifiedObjects;

    private boolean hasMore;
    private long skipForNextBatch;

    public ModifiedObjectsQueryResponse() {
        this(new LinkedHashMap<Long, ModifiedObject>(), false, -1);
    }

    public ModifiedObjectsQueryResponse(LinkedHashMap<Long, ModifiedObject> modifiedObjects, boolean hasMore, long skipForNextBatch) {
        this.modifiedObjects = modifiedObjects;
        this.hasMore = hasMore;
        this.skipForNextBatch = skipForNextBatch;
    }

    public LinkedHashMap<Long, String> getModifiedObjects() {
        LinkedHashMap<Long, String> modifiedObjects = new LinkedHashMap<Long, String>();
        LinkedHashMap<Long, ModifiedObject> detailed = getModifiedObjectsDetailed();

        for(Long key : getModifiedObjectsDetailed().keySet()) {
            modifiedObjects.put(key, detailed.get(key).transition);
        }

        return modifiedObjects;
    }

    public LinkedHashMap<Long, ModifiedObject> getModifiedObjectsDetailed() {
        return modifiedObjects;
    }

    
    public long getResultSize() {
        return modifiedObjects.size();
    }

    public boolean hasMore() {
        return hasMore;
    }

    public long getSkipForNextBatch() {
        return skipForNextBatch;
    }
    
    public static class ModifiedObject {
        public String transition;
        public String reason;
        public String accessCondition;
        
        public ModifiedObject(String transition, String reason, String accessCondition) {
            this.transition = transition;
            this.reason = reason;
            this.accessCondition = accessCondition;
        }
        
        public ModifiedObject(String transition) {
            this(transition, "", "");
        }
    }
}