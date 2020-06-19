package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum CopyType {
    DIGITAL_COPY("d", "Digitised"),
    PHYSICAL_COPY("p", "Physical"),
    BORN_DIGITAL("b", "Born Digital");
    
    private String code;
    private String name;
    
    private CopyType(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static CopyType fromString(String code) {
        if (code != null) {
            for (CopyType ct : CopyType.values()) {
                if (code.equalsIgnoreCase(ct.code)) {
                    return ct;
                }
            }
        }
        return null;
    }
    
    public String code() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (CopyType lu : CopyType.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
