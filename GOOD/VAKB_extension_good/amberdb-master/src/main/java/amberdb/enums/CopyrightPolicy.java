package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum CopyrightPolicy {
    PERPETUAL("Perpetual"),
    INCOPYRIGHT("In Copyright"),
    OUTOFCOPYRIGHT("Out of Copyright"),
    NOKNOWNRESTRICTIONS("No known copyright restrictions"),
    MIXEDCOPYRIGHT("Mixed copyright");
    
    private String code;
    
    private CopyrightPolicy(String code) {
        this.code = code;
    }
    
    public String code() {
        return code;
    }
    
    public static CopyrightPolicy fromString(String code) {
        if (code != null) {
            for (CopyrightPolicy cp : CopyrightPolicy.values()) {
                if (code.equalsIgnoreCase(cp.code)) {
                    return cp;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (CopyrightPolicy cp  : CopyrightPolicy.values()) {
            list.add(cp.code);
        }
        return list;
    }
}
