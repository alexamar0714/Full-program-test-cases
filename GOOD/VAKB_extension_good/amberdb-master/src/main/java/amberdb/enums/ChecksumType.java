package amberdb.enums;

public enum ChecksumType {
    
    MD5("MD5"), 
    SHA_1("SHA-1");

    private String code;

    private ChecksumType(String code) {
        this.code = code;
    }

    public static ChecksumType fromString(String code) {
        if (code != null) {
            for (ChecksumType e : ChecksumType.values()) {
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
}
