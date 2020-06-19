package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum SubType {

    PAGE("page"),
    BOOK("book"),
    ARTICLE("article"),
    CHAPTER("chapter"),
    VOLUME("volume"),
    COLLECTION("collection"),
    SERIES("series"),
    FOLDER("folder");

    private String code;

    private SubType(String code) {
        this.code = code;
    }

    public static SubType fromString(String code) {
        if (code != null) {
            for (SubType e : SubType.values()) {
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
        for (SubType lu : SubType.values()) {
            list.add(lu.code());
        }
        return list;
    }
    
    public static List<String> map(String[] subtypes) {
        List<String> subTypes = new ArrayList<String>();
        for (String subtype : subtypes) {
            subTypes.add(SubType.valueOf(subtype.toUpperCase()).code());
        }
        return subTypes;
    }
}
