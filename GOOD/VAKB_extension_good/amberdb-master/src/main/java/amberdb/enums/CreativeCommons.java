package amberdb.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Creative Commons license information enum that provides
 * a code and label for display
 */
public enum CreativeCommons {
    ATTRIBUTION("CC BY", "Attribution (CC BY)"),
    ATTRIBUTION_SHAREALIKE("CC BY-SA", "Attribution-ShareAlike (CC BY-SA)"),
    ATTRIBUTION_NODERIVATIVES("CC BY-ND", "Attribution-NoDerivatives (CC BY-ND)"),
    ATTRIBUTION_NONCOMMERCIAL("CC BY-NC", "Attribution-NonCommercial (CC BY-NC)"),
    ATTRIBUTION_NONCOMMERCIAL_SHAREALIKE("CC BY-NC-SA", "Attribution-NonCommercial-ShareAlike (CC BY-NC-SA)"),
    ATTRIBUTION_NONCOMMERCIAL_NODERIVATIVES("CC BY-NC-ND", "Attribution-NonCommercial-NoDerivatives (CC BY-NC-ND)"),
    NO_LICENSE("all rights reserved copyright", "No license (all rights reserved copyright)");

    private final String code;
    private final String display;

    CreativeCommons(String code, String display) {
        this.code = code;
        this.display = display;
    }

    public String display() {
        return this.display;
    }

    public String code() {
        return this.code;
    }

    /**
     * Retrieve enum constant based on the code passed
     *
     * @param code - Creative commons code
     * @return CreativeCommons enum constant
     */
    public static CreativeCommons fromString(String code) {
        if (StringUtils.isNotBlank(code)) {
            for (CreativeCommons creativeCommons : CreativeCommons.values()) {
                if (code.equalsIgnoreCase(creativeCommons.code)) {
                    return creativeCommons;
                }
            }
        }
        return null;
    }
}
