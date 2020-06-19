package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum AccessCondition {
    RESTRICTED("Restricted", true, true),
    UNRESTRICTED("Unrestricted", true, false),
    VIEW_ONLY("View Only", true, false),
    ONSITE_ONLY("Onsite Only", true, false),
    METADATA_ONLY("Metadata Only", true, false),
    INTERNAL_ACCESS_ONLY("Internal access only", true, false),
    OPEN("Open", false, true),
    CLOSED("Closed", false, true);
    
    private String code;
    private boolean publicAvailability;
    private boolean internalAccess;
    
    private AccessCondition(String code, boolean publicAvailability, boolean internalAccess) {
        this.code = code;
        this.publicAvailability = publicAvailability;
        this.internalAccess = internalAccess;
    }
    
    public String code() {
        return code;
    }
    
    public boolean publicAvailability() {
        return publicAvailability;
    }
    
    public boolean internalAccess() {
        return internalAccess;
    }
    
    public static AccessCondition fromString(String code) {
        if (code != null) {
            for (AccessCondition ac : AccessCondition.values()) {
                if (code.equalsIgnoreCase(ac.code)) {
                    return ac;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (AccessCondition lu : AccessCondition.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
