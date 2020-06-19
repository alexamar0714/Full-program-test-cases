package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum AcknowledgementKindOfSupport {
    DONOR("donor"),
    LENDER("lender"),     
    SPONSOR("sponsor");  

    private String code;

    private AcknowledgementKindOfSupport(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static AcknowledgementKindOfSupport fromString(String code) {
        if (code != null) {
            for (AcknowledgementKindOfSupport ack : AcknowledgementKindOfSupport.values()) {
                if (code.equalsIgnoreCase(ack.code)) {
                    return ack;
                }
            }
        }
        return null;
    }

    public static List<String> list() {
        List<String> list = new ArrayList<String>();

        for (AcknowledgementKindOfSupport ack : AcknowledgementKindOfSupport.values()) {
            list.add(ack.code);
        }
        return list;
    }
}