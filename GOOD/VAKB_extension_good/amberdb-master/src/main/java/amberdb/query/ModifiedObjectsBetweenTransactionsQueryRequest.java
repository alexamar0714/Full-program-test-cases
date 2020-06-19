package amberdb.query;

import java.util.Date;
import java.util.List;

import com.google.common.base.Predicate;

import amberdb.version.VersionedVertex;

public class ModifiedObjectsBetweenTransactionsQueryRequest extends ModifiedObjectsQueryRequest {
    
    private Long fromTxn = null;
    private Long toTxn = null;
    
    public ModifiedObjectsBetweenTransactionsQueryRequest(List<Long> txns) {
        super();
        setTransactions(txns);
    }
    
    public ModifiedObjectsBetweenTransactionsQueryRequest(List<Long> txns, Predicate<VersionedVertex> filterPredicate, List<WorkProperty> propertyFilters, boolean onlyPropertiesWithinTransactionRange, int skip, int take) {
        super(null, null, filterPredicate, propertyFilters, onlyPropertiesWithinTransactionRange, skip, take);
        setTransactions(txns);
    }
    
    public ModifiedObjectsBetweenTransactionsQueryRequest(ModifiedObjectsQueryRequest request, TransactionsBetweenFinder transactionsFinder) {
        super(request);
        setTransactions(transactionsFinder.getTransactionsBetween(request.getFrom(), request.getTo()));
    }

    public boolean hasTransactions() {
        return fromTxn != null && toTxn != null;
    }
    
    public long getFromTxn() {
        return fromTxn;
    }

    public long getToTxn() {
        return toTxn;
    }

    private void setTransactions(List<Long> txns) {
        if (txns == null || txns.size() == 0) {
            this.fromTxn = null;
            this.toTxn = null;
        } else {
            this.fromTxn = txns.get(0);
            this.toTxn = txns.get(txns.size() - 1);
        }
    }
    
    public interface TransactionsBetweenFinder {
        public List<Long> getTransactionsBetween(Date startTime, Date endTime);
    }
}