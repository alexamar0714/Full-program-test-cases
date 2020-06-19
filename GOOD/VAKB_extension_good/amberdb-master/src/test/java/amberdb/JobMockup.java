package amberdb;

import java.nio.file.Path;
import java.util.List;

public class JobMockup {
    long txId;
    List<Path> files;
    List<Long> workIds;

    public long getAmberTxId() {
        // TODO Auto-generated method stub
        return txId;
    }
    
    public void setAmberTxId(long txId) {
        this.txId = txId;
    }
    
    public List<Long> getWorks() {
        return workIds;
    }
    
    public String getDefaultDevice() {
        return "device";
    }
    
    public String getDefaultSoftware() {
        return "apps";
    }
}
