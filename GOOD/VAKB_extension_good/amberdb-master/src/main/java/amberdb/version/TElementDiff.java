package amberdb.version;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class that represents the difference between 2 elements (edges or vertices)
 * 
 * This class needs to be split into 2:
 * 
 * A class that represents the difference between 2 arbitrary elements and a
 * specialization class that represents the difference between the same element
 * at different transactions.
 *
 * Anyway, as it stands they are currently the one class ... this one. 
 */
public class TElementDiff {

    TElement elem1;
    TElement elem2;
    TTransition transition; 
    
    public TElementDiff(Long txn1, Long txn2, TElement e1, TElement e2) {

        elem1 = e1;
        elem2 = e2;
        
        if (elem1 == null && elem2 == null) {
            transition = TTransition.UNCHANGED;
            return;
        }
        if (elem1 == null) {
            if (elem2.id.end.compareTo(0L) > 0 && elem2.id.end.equals(txn2)) {
                transition = TTransition.UNCHANGED;
            } else {
                transition = TTransition.NEW;
            }
            return;
        }
        if (elem2 == null) {
            if (elem1.id.end.compareTo(0L) > 0 && ((elem1.id.end.equals(txn2) && elem1.id.start.compareTo(txn1) > 0) || (elem1.id.end.equals(txn1)))) {
                transition = TTransition.UNCHANGED;
            } else {
                transition = TTransition.DELETED;
            }
            return;
        }
        if (elem1.equals(elem2)) {
            if (elem1.id.start.equals(txn1) && elem1.id.end.equals(txn2)) {
                transition = TTransition.DELETED;
            } else {
                transition = TTransition.UNCHANGED;
            }
            return;
        }
        transition = TTransition.MODIFIED;
    }
    
    
    public TId[] getId() {
        switch (transition) {
        case NEW:       return new TId[] { elem2.getId() };
        case DELETED:   return new TId[] { elem1.getId() };
        case UNCHANGED: return (elem1 == null) ? new TId[] { null } : new TId[] { elem1.getId() };
        case MODIFIED:  return (elem1.getId().equals(elem2.getId())) ? new TId[] { elem1.getId() } : new TId[] { elem1.getId(), elem2.getId() };
        }
        return null;
    }
    

    public Object getProperty(String propertyName) throws TDiffException {
        switch (transition) {
        case NEW: return elem2.getProperty(propertyName);
        case DELETED: return elem1.getProperty(propertyName);
        case MODIFIED: 
            Object obj1 = elem1.getProperty(propertyName);
            Object obj2 = elem2.getProperty(propertyName);
            if (obj1 == null) {
                return new Object[] { null, obj2 };
            } else {
                return (obj1.equals(obj2)) ? new Object[] { obj1 } : new Object[] { obj1, obj2 };
            }    

        case UNCHANGED: return (elem1 == null) ? ((elem2 == null) ? null : elem2.getProperty(propertyName)) : elem1.getProperty(propertyName);
        }
        throw new TDiffException("Cannot get property. Unknown Transition state: " + transition);
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder(transition + ": " );
        switch (transition) {
        case NEW: 
            sb.append(elem2);
            break;
        case UNCHANGED:
            if (elem1 == null) {
                if (elem2 == null) {
                    sb.append("null -> null (quantum foam)");
                } else {
                    sb.append(elem2);
                }
            } else {
                sb.append(elem1);
            }
            break;
        case DELETED:
            if (elem1 == null) {
                sb.append(elem2);
            } else {
                sb.append(elem1);
            }
            break;
        case MODIFIED: 
            sb.append(printDiff());
            break;
        default:
            throw new TDiffException("Unexpected transition: " + transition);
        }
        return sb.toString();
    }
    

    /**
     * @return true if the element popped into existence after the first txn but
     *         was deleted before the second txn. Thus this element is null at
     *         both txns.
     */
    public boolean isTransient() {
        if (elem1 == null && elem2 == null) {
            return true;
        }
        return false;
    }

    
    private String printDiff() {
        StringBuilder sb = new StringBuilder();
        Set<String> propNames = elem1.getPropertyKeys();
        propNames.addAll(elem2.getPropertyKeys());
        for (String propName : propNames) {
            sb.append(propName + ": ");
            Object[] objArr = (Object[]) getProperty(propName);
            sb.append((objArr[0] != null) ? objArr[0].toString() : "<null>"); 
            if (objArr.length > 1) {
                sb.append(" -> ");
                sb.append((objArr[1] != null) ? objArr[1].toString() : "<null>"); 
            }
            sb.append('\n');
        }
        if (sb.length() > 1) sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    
    public TTransition getTransition() {
        return transition;
    }
    
    
    public Map<String, Object[]> getDiffMap() {

        Map<String, Object[]> diffMap = new HashMap<>();
        Set<String> propertyKeys = new HashSet<>();
        if(elem1 != null) propertyKeys.addAll(elem1.getPropertyKeys());
        if(elem2 != null) propertyKeys.addAll(elem2.getPropertyKeys());

        for (String name : propertyKeys) {

            switch (transition) {
            
            case NEW: 
                diffMap.put(name, new Object[] {null, elem2.getProperty(name)});
                break;
                
            case DELETED: 
                diffMap.put(name, new Object[] {elem1.getProperty(name), null});
                break;

            case MODIFIED: 
            case UNCHANGED:
                diffMap.put(name, new Object[] {elem1.getProperty(name), elem2.getProperty(name)});
            }    
        }
        return diffMap;
    }
}
