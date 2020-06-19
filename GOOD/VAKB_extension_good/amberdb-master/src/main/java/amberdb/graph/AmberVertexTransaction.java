package amberdb.graph;

public class AmberVertexTransaction {

    AmberTransaction transaction;
    long vertexId;
    
    
    public AmberVertexTransaction(long transactionId, Long time, String user, String operation, long vertexId) {
        this.transaction = new AmberTransaction(transactionId, time, user, operation);
        this.vertexId = vertexId;
    }

    public AmberTransaction getTransaction() {
        return transaction;
    }


    public void setTransaction(AmberTransaction transaction) {
        this.transaction = transaction;
    }


    public long getVertexId() {
        return vertexId;
    }


    public void setVertexId(long vertexId) {
        this.vertexId = vertexId;
    }

    @Override
    public String toString() {
        return "AmberVertexTransaction{" +
                "transaction=" + transaction +
                ", vertexId=" + vertexId +
                '}';
    }
}
