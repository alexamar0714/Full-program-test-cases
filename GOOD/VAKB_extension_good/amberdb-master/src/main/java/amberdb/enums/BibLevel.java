package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum BibLevel{
    
    SET("Set", 4), 
    ITEM("Item", 3), 
    PART("Part", 2),
    SECTION("Section", 1),
    NO_BIB_LEVEL("", 0);

    private String code;
    private Integer level;

    private BibLevel(String code, Integer level) {
        this.code = code;
        this.level = level;
    }

    public static BibLevel fromString(String code) {
        if (code != null) {
            for (BibLevel e : BibLevel.values()) {
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
    
    public Integer level() {
        return level;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (BibLevel lu : BibLevel.values()) {
            list.add(lu.code());
        }
        return list;
    }
   
}
