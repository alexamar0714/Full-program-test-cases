package amberdb.query;

public class WorkProperty {
    private final String name;
    private final String value;
    private final boolean caseSensitive;

    public WorkProperty(String name, String value) {
        this(name, value, false);
    }

    public WorkProperty(String name, String value, boolean caseSensitive) {
        this.name = name;
        this.value = value;
        this.caseSensitive = caseSensitive;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public enum CaseSensitiveType{
        CASE_SENSITIVE, CASE_INSENSITIVE;
    }
}
