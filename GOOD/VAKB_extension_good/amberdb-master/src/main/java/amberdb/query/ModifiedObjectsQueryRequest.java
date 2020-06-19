package amberdb.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Predicate;
import amberdb.version.VersionedVertex;

public class ModifiedObjectsQueryRequest {
    private Date from;
    private Date to;
    private Predicate<VersionedVertex> filterPredicate;
    private List<WorkProperty> propertyFilters;
    private boolean onlyPropertiesWithinTransactionRange;
    private int skip;
    private int take;
    
    public ModifiedObjectsQueryRequest() {
        this(new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE), null, new ArrayList<WorkProperty>(), false, 0, Integer.MAX_VALUE);
    }
    
    public ModifiedObjectsQueryRequest(Date from) {
        this();
        this.from = from;
    }

    public ModifiedObjectsQueryRequest(Date from, Date to) {
        this(from);
        this.to= to;
    }

    public ModifiedObjectsQueryRequest(Date from, int skip, int take) {
        this(from);
        this.skip = skip;
        this.take = take;
    }

    public ModifiedObjectsQueryRequest(Date from, Date to, int skip, int take) {
        this(from, skip, take);
        this.to = to;
    }

    public ModifiedObjectsQueryRequest(Date from, List<WorkProperty> propertyFilters, int skip, int take) {
        this(from, skip, take);
        this.propertyFilters = propertyFilters;
    }
    
    public ModifiedObjectsQueryRequest(Date from, Date to, List<WorkProperty> propertyFilters, int skip, int take) {
        this(from, propertyFilters, skip, take);
        this.to = to;
    }

    public ModifiedObjectsQueryRequest(Date from, Date to, Predicate<VersionedVertex> filterPredicate, List<WorkProperty> propertyFilters, boolean onlyPropertiesWithinTransactionRange, int skip, int take) {
        this.from = from;
        this.to = to;
        this.filterPredicate = filterPredicate;
        this.propertyFilters = propertyFilters;
        this.onlyPropertiesWithinTransactionRange = onlyPropertiesWithinTransactionRange;
        this.skip = skip;
        this.take = take;
    }
    
    public ModifiedObjectsQueryRequest(ModifiedObjectsQueryRequest request) {
        this(request.getFrom(), request.getTo(), request.getFilterPredicate(), request.getPropertyFilters(), request.isOnlyPropertiesWithinTransactionRange(), request.getSkip(), request.getTake());
    }
    
    public boolean hasFilterPredicate() {
        return filterPredicate != null;
    }

    protected Date getFrom() {
        return from;
    }

    protected Date getTo() {
        return to;
    }

    public Predicate<VersionedVertex> getFilterPredicate() {
        return filterPredicate;
    }

    public List<WorkProperty> getPropertyFilters() {
        return propertyFilters;
    }
    
    public boolean isOnlyPropertiesWithinTransactionRange() {
        return onlyPropertiesWithinTransactionRange;
    }

    public int getSkip() {
        return skip;
    }

    public int getTake() {
        return take;
    }
}
