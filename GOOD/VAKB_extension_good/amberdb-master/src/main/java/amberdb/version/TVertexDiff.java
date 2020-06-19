package amberdb.version;


public class TVertexDiff extends TElementDiff {

    
    public TVertexDiff(Long txn1, Long txn2, TVertex e1, TVertex e2) {
        super(txn1, txn2, e1, e2);
    }
}
