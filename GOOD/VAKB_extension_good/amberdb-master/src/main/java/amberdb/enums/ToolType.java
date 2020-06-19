package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum ToolType {
    SIGNAL_SOURCE("Signal source", "Signal source"),
    AD_CONVERTER("A/D converter", "A/D converter"),
    SOUND_CARD("Sound card", "Sound card");
    
    private String code;
    private String display;
    
    private ToolType(String code, String display) {
        this.code = code;
        this.display = display;
    }
    
    public static ToolType fromString(String code) {
        if (code != null) {
            for (ToolType tt : ToolType.values()) {
                if (code.equalsIgnoreCase(tt.code)) {
                    return tt;
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
        for (ToolType t : ToolType.values()) {
            list.add(t.code());
        }
        return list;
    }
}
