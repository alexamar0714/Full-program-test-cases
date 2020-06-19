package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum SubUnitType {
    ARTICLE("Article"),
    BOOK("Book"), 
    BOX("Box"), 
    CLASS("Class"),
    COLLECTION("Collection"),
    COVER("Cover"), 
    COVER_BACK("Cover - Back"), 
    COVER_FRONT("Cover - Front"), 
    COVER_INSIDE_BACK("Cover - Inside Back"), 
    COVER_INSIDE_FRONT("Cover - Inside Front"),
    CORRIGENDUM("Corrigendum"),
    ENCLOSURE("Enclosure"), 
    ENVELOPE("Envelope"),
    FILE("File"),
    FLY_LEAF("Fly Leaf"), 
    FLYLEAF_RECTO("Flyleaf recto"), 
    FLYLEAF_VERSO("Flyleaf verso"), 
    FOLDER("Folder"), 
    FOLIO("Folio"), 
    FOLIO_RECTO("Folio recto"), 
    FOLIO_VERSO("Folio verso"),
    FONDS("Fonds"),
    ILLUSTRATION("Illustration"), 
    INDEX("Index"), 
    INVITATION("Invitation"), 
    ISSUE("Issue"), 
    ITEM("Item"),
    MAP("Map"),
    MUSIC_SCORE("Music score"),
    OBVERSE("Obverse"), 
    OTHERLEVEL("Otherlevel"),
    PAGE("Page"), 
    PAGE_UNNUMBERED("Page - Unnumbered"), 
    PART("Part"), 
    PIECE("Piece"), 
    PLATE("Plate"), 
    PLATE_LEAF("Plate - Leaf"), 
    PLATE_VERSO("Plate - Verso"), 
    PROGRAM("Program"),
    RECORDGRP("Recordgrp"),
    RECTO("Recto"), 
    REVERSE("Reverse"), 
    SECTION("Section"), 
    SERIES("Series"),
    SHEET("Sheet"),
    SUBFONDS("Subfonds"),
    SUBGRP("Subgrp"),
    SUBSERIES("Subseries"),
    SUPPLEMENTARY_MATERIAL("Supplementary Material"),
    TILE("Tile"), 
    VERSO("Verso"), 
    VOLUME("Volume"); 
    
    private String code;
    
    private SubUnitType(String code) {
        this.code = code;
    }
    
    public static SubUnitType fromString(String code) {
        if (code != null) {
            for (SubUnitType t : SubUnitType.values()) {
                if (code.equalsIgnoreCase(t.code)) {
                    return t;
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
        for (SubUnitType t : SubUnitType.values()) {
            list.add(t.code());
        }
        return list;
    }
}
