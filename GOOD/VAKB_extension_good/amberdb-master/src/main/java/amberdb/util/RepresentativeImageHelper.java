package amberdb.util;

import amberdb.AmberSession;
import amberdb.enums.CopyRole;
import amberdb.model.Copy;
import amberdb.model.Work;
import amberdb.query.WorkChildrenQuery;

import java.util.Iterator;
import java.util.List;

public class RepresentativeImageHelper {

    /**
     * Returns the work that contains the access copy to be used for this work's representative image.
     *
     * @return The work with the image copy that should be used to represent this work, or null if no image is
     * specified.
     */
    public static Work getRepresentativeImageWork(Work work, AmberSession amberSession){
        Work repImageOrAccessCopy = getRepImageOrAccessCopy(work);
        if (repImageOrAccessCopy != null) {
            return repImageOrAccessCopy;
        }
        WorkChildrenQuery query = new WorkChildrenQuery(amberSession);
        List<Work> children = query.getChildRange(work.getId(), 0, 1);
        if (!children.isEmpty()) {
            Work child = children.get(0);
            if (WorkUtils.checkCanReturnRepImage(child)) {
                return getRepImageOrAccessCopy(child);
            }
        }
        return null;
    }
    
    public static void setRepresentativeImageWorkProperty(Work work, AmberSession amberSession){
        Work representativeImageWork = getRepresentativeImageWork(work, amberSession);
        if (representativeImageWork != null){
            work.asVertex().setProperty("representativeImageWork", representativeImageWork);    
        }
    }

    private static Work getRepImageOrAccessCopy(Work work) {
        Iterator<Copy> representations = work.getRepresentations().iterator();
        if (representations.hasNext()) {
            Work repWork = representations.next().getWork();
            if (!WorkUtils.checkCanReturnRepImage(repWork)) {
                return null;
            }
            return repWork;
        }
        Copy accessCopy = work.getCopy(CopyRole.ACCESS_COPY);
        if (accessCopy != null && accessCopy.getImageFile() != null) {
            return work;
        } else if (accessCopy == null) {
            Copy accessOnlyCopy = work.getCopy(CopyRole.ACCESS_ONLY_COPY);
            return accessOnlyCopy != null && accessOnlyCopy.getImageFile() != null ? work : null;
        }
        return null;
    }
}
