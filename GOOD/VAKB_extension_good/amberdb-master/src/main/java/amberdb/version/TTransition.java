package amberdb.version;

import java.util.ArrayList;
import java.util.List;

public enum TTransition {

    
    NEW("New"),
    DELETED("Deleted"),
    MODIFIED("Modified"),
    UNCHANGED("Unchanged");
    
    private String code;

    private TTransition(String code) {
        this.code = code;
    }

    public static TTransition fromString(String code) {
        if (code != null) {
            for (TTransition t : TTransition.values()) {
                if (code.equalsIgnoreCase(t.code)) {
                    return t;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (TTransition t : TTransition.values()) {
            list.add(t.code());
        }
        return list;
    }

    public String code() {
        return code;
    }
}
