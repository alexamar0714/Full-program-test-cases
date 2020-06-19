package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum Orientation {
    
    LANDSCAPE("Landscape"), 
    PORTRAIT("Portrait");

    private String code;

    private Orientation(String code) {
        this.code = code;
    }

    public static Orientation fromString(String code) {
        if (code != null) {
            for (Orientation e : Orientation.values()) {
                if (code.equalsIgnoreCase(e.code)) {
                    return e;
                }
            }
        }
        return null;
    }

    public String code() {
        return code;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (Orientation lu : Orientation.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
