package amberdb.enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import amberdb.model.Copy;

/**
 * CopyRole ENUM that provides a code and label for display.
 *
 * the details for 0-12th copy roles must not be changed.
 * the 13 - 47 copy roles are in alphabetical order, this
 * must be maintained at all time when a new copy role is
 * added.
 */
public enum CopyRole {
    ORIGINAL_COPY("o", "Original",0, false),
    MASTER_COPY("m", "Master",1, false),
    DERIVATIVE_MASTER_COPY("dm", "Derivative master",2, false),
    CO_MASTER_COPY("c", "Co-master",3, false),
    DIGITAL_DISTRIBUTION_COPY("d", "Digital distribution",4, false),
    RELATED_METADATA_COPY("rm", "Related metadata",5, false),
    SUMMARY_COPY("s", "Other summary", "No",6, false),
    TRANSCRIPT_COPY("tr", "Other transcript", "No",7, false),
    LISTENING_1_COPY("l1", "Listening 1",8, false),
    LISTENING_2_COPY("l2", "Listening 2",9, false),
    LISTENING_3_COPY("l3", "Listening 3",10, false),
    WORKING_COPY("w", "Working",11, false),
    ANALOGUE_DISTRIBUTION_COPY("ad", "Analogue distribution",12, false),
    ACCESS_COPY("ac", "Access",130, false),
    ACCESS_ONLY_COPY ("ao", "Access-only", 135, false),
    ARCHIVE_COPY("a", "Archive",140, false),
    EDITED_COPY("ed", "Edited",150, false),
    ELECTRONIC_SUMMARY("se", "Electronic summary", "No",160, false),
    ELECTRONIC_TRANSCRIPT("te", "Electronic transcript", "No",170, false),
    EXAMINATION_COPY("e", "Examination",180, false),
    FILTERED_COPY("fc", "Filtered",190, false),
    FINDING_AID_COPY("fa", "Finding aid",200, false),
    FINDING_AID_SUPPLEMENTARY_COPY("fas", "Finding aid supplementary",201, true),
    FINDING_AID_PRINT_COPY("fap", "Finding aid print",210, false),
    FINDING_AID_VIEW_COPY("fav", "Finding aid view",220, false),
    FLIGHT_DIAGRAM_COPY ("fd", "Flight diagram", 230, false),
    IMAGE_PACKAGE("ip", "Image package",240, false),
    INDEX_COPY ("i", "Index", 250, false),
    LIST_COPY("dl", "List",260, false),
    MASTER_ANALOGUE_COPY("ma", "Master analogue",269, false),
    MICROFORM_COPY("mf", "Microform",270, false),
    OCR_METS_COPY("mt", "OCR mets",280, false),
    OCR_ALTO_COPY("at", "OCR alto",290, false),
    OCR_JSON_COPY("oc", "OCR json",300, false),
    PAPER_SUMMARY("sp", "Paper summary", "No",310, false),
    PAPER_TRANSCRIPT("tp", "Paper transcript", "No",320, false),
    PRINT_COPY("p", "Print",330, false),
    PRODUCTION_MASTER_AUDIO_LEFT_COPY("pmal", "Production master audio left",340, false),
    PRODUCTION_MASTER_AUDIO_RIGHT_COPY("pmar", "Production master audio right",341, false),
    PRODUCTION_MASTER_VIDEO_COPY("pmv", "Production master video",342, false),
    QUICKTIME_FILE_1_COPY("sb1", "QuickTime file 1",350, false),
    QUICKTIME_FILE_2_COPY("sb2", "QuickTime file 2",360, false),
    QUICKTIME_REF_1_COPY("rb1", "QuickTime reference 1",370, false),
    QUICKTIME_REF_2_COPY("rb2", "QuickTime reference 2",380, false),
    QUICKTIME_REF_3_COPY("rb3", "QuickTime reference 3",390, false),
    QUICKTIME_REF_4_COPY("rb4", "QuickTime reference 4",400, false),
    REAL_MEDIA_FILE_COPY("sa1", "RealMedia file",410, false),
    REAL_MEDIA_REF_COPY("ra1", "RealMedia reference",420, false),
    RTF_TRANSCRIPT("tt", "RTF transcript", "No",430, false),
    SECOND_COPY("so", "Second copy",439, false),
    SPECIAL_DELIVERY_COPY("sd", "Special delivery",440, false),
    STRUCTURAL_MAP_COPY("sm", "Structural map",450, false),
    THUMBNAIL_COPY("t", "Thumbnail",460, false),
    TIME_CODED_SUMMARY("sc", "Time coded summary", "Yes",470, false),
    TIME_CODED_TRANSCRIPT_COPY("tc", "Time coded transcript", "Yes",480, false),
    AUDIO_DATA_DELIVERY_COPY("td", "Audio data delivery", "Yes",482, false),
    UNKNOWN_COPY ("u", "Unknown", 485, false),
    VIEW_COPY("v", "View",490, false),
    VISUAL_NAVIGATION_DELIVERY_COPY ("vn", "Visual navigation delivery", 500, false);


    private String code;
    private String display;
    private String timed;
    private Integer order;
    private boolean supportMultipleCopies;

    private CopyRole(String code, String display, Integer order, boolean supportMultipleCopies) {
        this(code, display, null, order, supportMultipleCopies);
    }

    private CopyRole(String code, String display, String timed, Integer order, boolean supportMultipleCopies) {
        this.code = code;
        this.display = display;
        this.timed = timed;
        this.order = order;
        this.supportMultipleCopies = supportMultipleCopies;
    }

    public static CopyRole fromString(String code) {
        if (code != null) {
            for (CopyRole c : CopyRole.values()) {
                if (code.equalsIgnoreCase(c.code)) {
                    return c;
                }
            }
        }
        return null;
    }

    public String code() {
        return this.code;
    }

    public String display() {
        return this.display;
    }

    public String timed() {
        return this.timed;
    }

    public int ord() {
        return this.order;
    }

    public boolean isSupportMultipleCopies() {
        return supportMultipleCopies;
    }

    /**
     * Returns a List of <STRONG>codes</STRONG>
     */
    public static List<String> list() {
        List<String> list = new ArrayList<>();
        for (CopyRole c : CopyRole.values()) {
            list.add(c.code());
        }
        return list;
    }

    /**
     * Returns a List of <STRONG>CopyRole</STRONG> objects that have been sorted
     * alphabetically by the CopyRole.value
     */
    public static List<CopyRole> listAlphabetically() {
        List<CopyRole> list = new ArrayList<>();
        Collections.addAll(list, CopyRole.values());

        Collections.sort(list, new Comparator<CopyRole>() {
            public int compare(CopyRole r1, CopyRole r2) {
                return r1.display().compareTo(r2.display());
            }
        });

        return list;
    }

    public static Collection<String> sortCopyRoleList(Iterable<String> copyRoles) {
        TreeMap<Integer, String> rearranged = new TreeMap<>();
        for (String copyRoleStr : copyRoles) {
            CopyRole copyRole = CopyRole.fromString(copyRoleStr);
            if (copyRole != null) {
                rearranged.put(copyRole.ord(), copyRoleStr);
            }
        }
        return rearranged.values();
    }

    public static Collection<Copy> reorderCopyList(Iterable<Copy> copies) {
        TreeMap<Integer, Copy> rearranged = new TreeMap<>();
        for (Copy copy : copies) {
            CopyRole copyRole = CopyRole.fromString(copy.getCopyRole());
            if (copyRole != null){
                rearranged.put(copyRole.ord(), copy);
            }
        }
        return rearranged.values();
    }
}
