package amberdb.model.sort;


import java.util.Comparator;

import amberdb.model.Work;
import amberdb.util.NaturalSort;

/**
 * Use to sort Works based on a given property.
 *
 * Always sorts Works missing the given property last (ie: if the property is null).
 * WARNING: Json list properties sort on their first value alphabetically (ie: not numerically),
 *          so this class will not sort json lists of numbers correctly.
 * Empty json lists sort like nulls (ie: last)
 */
public class WorkComparator implements Comparator<Work> {

    private String sortPropertyName;
    private boolean sortForward;

    /**
     * @param sortPropertyName The name of Work property to sort on
     * @param sortForward When false, sort in reverse to standard sorting (nulls still sort last)
     */
    public WorkComparator(String sortPropertyName, boolean sortForward) {
        super();
        this.sortPropertyName = sortPropertyName;
        this.sortForward = sortForward;
    }

    @Override
    public int compare(Work w1, Work w2) {
        
        if (w1 == w2) {
            return 0;
        }
        
        // We want the order to always be deterministic to avoid UI behaviour of works showing in different orders
        // So we fall back to ordering by object id if the return value would be 0
        int fallbackOrder = w1.getObjId().compareTo(w2.getObjId()) * (sortForward ? 1 : -1);

        Object o1 = w1.asVertex().getProperty(sortPropertyName);
        Object o2 = w2.asVertex().getProperty(sortPropertyName);

        // null guards
        if (o1 == null) return 1;
        if (o2 == null) return -1;

        // easy comparisons
        if (o1.equals(o2)) return fallbackOrder;

        // check for a json encoded list and treat empty lists same as null
        if (o1 instanceof String && ((String) o1).startsWith("[")) {
            if (o1.equals("[]")) return 1;
        }

     // easy comparisons for Strings, Booleans, Dates and a variety of number formats
        if (o1 instanceof String){
            int i = NaturalSort.compareNatural((String)o1, (String)o2);
            return sortForward ? i : -i;
        }else if (o1 instanceof Comparable) {
            int i = ((Comparable) o1).compareTo(o2);
            return sortForward ? i : -i;
        }

        // can't decide how to compare - call them equal
        return fallbackOrder;
    }
}
