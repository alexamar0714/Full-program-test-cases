package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum DepositType {
	OnlineLegal("Online legal deposit"),
	OnlineGovernment("Online government deposit"),
	OnlineVoluntary("Online voluntary deposit"),
	OfflineVoluntary("Offline voluntary deposit"),
	OnlineSpecialCollection("Online special collection deposit");
    
    private String code;
    
    private DepositType(String code) {
        this.code = code;
    }
    
    public String code() {
        return code;
    }
    
    public static DepositType fromString(String code) {
        if (code != null) {
            for (DepositType dm : DepositType.values()) {
                if (code.equalsIgnoreCase(dm.code)) {
                    return dm;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (DepositType dm  : DepositType.values()) {
            list.add(dm.code);
        }
        return list;
    }
}
