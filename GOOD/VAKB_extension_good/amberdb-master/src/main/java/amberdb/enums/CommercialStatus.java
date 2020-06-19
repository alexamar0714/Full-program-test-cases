package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum CommercialStatus {
    COMMERCIAL("Commercial"),
    NONCOMMERCIAL("Noncommerc");
    
    private String code;
    
    private CommercialStatus(String code) {
        this.code = code;
    }
    
    public String code() {
        return code;
    }
    
    public static CommercialStatus fromString(String code) {
        if (code != null) {
            for (CommercialStatus cp : CommercialStatus.values()) {
                if (code.equalsIgnoreCase(cp.code)) {
                    return cp;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (CommercialStatus cp  : CommercialStatus.values()) {
            list.add(cp.code);
        }
        return list;
    }
}
