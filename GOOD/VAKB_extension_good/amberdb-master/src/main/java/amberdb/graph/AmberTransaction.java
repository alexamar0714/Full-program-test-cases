package amberdb.graph;

import java.util.Date;


public class AmberTransaction {

    Long id;
    Long time;
    String user;
    String operation;
    
    
    public AmberTransaction(long id, Long time, String user, String operation) {
        this.id = id;
        this.time = time;
        this.user = user;
        this.operation = operation;
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" id:").append(id)
          .append(" time:").append(new Date(time))
          .append(" user:").append(user)
          .append(" operation:").append(operation);
        return super.toString() + sb.toString();
    }
    
    
    public Long getId() {
        return id;
    }
    
    
    public Long getTime() {
        return time;
    }
    
    
    public String getUser() {
        return user;
    }
    
    
    public String getOperation() {
        return operation;
    }
}
