package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum ToolCategory {
    DEVICE("d", "Device"),
    SOFTWARE("s", "Software");
    
    private String code;
    private String display;
    
    private ToolCategory(String code, String display) {
        this.code = code;
        this.display = display;
    }
    
    public static ToolCategory fromString(String code) {
        if (code != null) {
            for (ToolCategory tc : ToolCategory.values()) {
                if (code.equalsIgnoreCase(tc.code)) {
                    return tc;
                }
            }
        }
        return null;
    }
    
    public String code() {
        return code;
    }
    
    public String display() {
        return display;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (ToolCategory tc : ToolCategory.values()) {
            list.add(tc.code());
        }
        return list;
    }
}
