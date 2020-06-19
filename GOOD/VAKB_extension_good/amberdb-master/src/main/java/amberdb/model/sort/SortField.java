package amberdb.model.sort;

public enum SortField {

    ALIAS ("alias", "Alias"),
    SUBUNIT_NUMBER ("subUnitNo", "Sub unit number"),
    SHEET_NAME ("sheetName", "Sheet Name"),
    ISSUE_DATE ("issueDate", "Issue Date"), 
    DIGITAL_STATUS ("digitalStatus", "Capture status"),
    SUBUNIT_TYPE ("subUnitType", "Sub unit type"),
    ACCESS_CONDITIONS ("accessConditions", "Public availability"),
    HOLDING_NUMBER ("holdingNumber", "Holding number");

    private SortField(String fieldName, String displayName) {
        this.fieldName = fieldName;
        this.displayName = displayName;
    }
 
    public static SortField fromString(String fieldName) {
        for (SortField field : SortField.values()) {
            if (field.fieldName.equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        return null;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public String getDisplayName() {
        return displayName;
    }

    String fieldName;
    String displayName;
}