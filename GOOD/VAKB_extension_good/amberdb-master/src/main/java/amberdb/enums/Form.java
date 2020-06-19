package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum Form {
    
    BOOK("Book"), 
    JOURNAL("Journal"),
    MULTIBOOK("Multi Volume Book"),
    MANUSCRIPT("Manuscript"),
    MAP("Map"),
    MUSIC("Music"),
    PICTURE("Picture"),
    SOUND_RECORDING("Sound recording"),
    OTHER_AUSTRALIAN("Other - Australian"),
    OTHER_GENERAL("Other - General"),
    INTERNAL_PHOTOGRAPH("Internal photograph"),
    CONSERVATION("Conservation"),
    DIGITALPUBLICATION("Digital Publication");

    private String code;

    private Form(String code) {
        this.code = code;
    }

    public static Form fromString(String code) {
        if (code != null) {
            for (Form e : Form.values()) {
                if (code.equalsIgnoreCase(e.code)) {
                    return e;
                }
            }
        }
        return null;
    }
    
    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (Form lu : Form.values()) {
            list.add(lu.code());
        }
        return list;
    }

    public String code() {
        return code;
    }
}
