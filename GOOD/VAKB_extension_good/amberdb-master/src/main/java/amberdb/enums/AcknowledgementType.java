package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum AcknowledgementType {
    MATERIAL("of material"), 
    CREATION("of creation of finding aids"), 
    DONATION("of donation of digitised copy"), 
    ARRANGEMENT("of arrangement & description"), 
    DIGITISATION("of digitisation");

    private String code;

    private AcknowledgementType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static AcknowledgementType fromString(String code) {
        if (code != null) {
            for (AcknowledgementType ack : AcknowledgementType.values()) {
                if (code.equalsIgnoreCase(ack.code)) {
                    return ack;
                }
            }
        }
        return null;
    }

    public static List<String> list() {
        List<String> list = new ArrayList<String>();

        for (AcknowledgementType ack : AcknowledgementType.values()) {
            list.add(ack.code);
        }
        return list;
    }
}
