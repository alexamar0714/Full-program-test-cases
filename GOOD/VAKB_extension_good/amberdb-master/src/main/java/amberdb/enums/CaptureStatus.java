package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum CaptureStatus {
    
    CAPTURED("Captured"),
    NOT_CAPTURED("Not Captured"),
    PARTIALLY_CAPTURED("Partially Captured"),
    PRESERVED_ANALOGUE("Preserved analogue");

    private String code;

    private CaptureStatus(String code) {
        this.code = code;
    }

    public static CaptureStatus fromString(String code) {
        if (code != null) {
            for (CaptureStatus e : CaptureStatus.values()) {
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
        for (CaptureStatus lu : CaptureStatus.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
