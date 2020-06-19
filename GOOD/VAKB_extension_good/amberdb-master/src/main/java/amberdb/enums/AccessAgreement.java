package amberdb.enums;

public enum AccessAgreement {
    BASIC_ACCESS("BasicAccess"),
    OPEN_ACCESS_IMMEDIATELY("OpenAccessImmediately"),
    OPEN_ACCESS_EMBARGOED("OpenAccessEmbargoed");
    
    private String code;
    
    private AccessAgreement(String code) {
        this.code = code;
    }
    
    public String code() {
        return code;
    }
    
    public static AccessAgreement fromString(String code) {
        if (code != null) {
            for (AccessAgreement ac : AccessAgreement.values()) {
                if (code.equalsIgnoreCase(ac.code)) {
                    return ac;
                }
            }
        }
        return null;
    }
}

