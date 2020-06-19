package amber.checksum;

public class Checksum {
    private final ChecksumAlgorithm algorithm;
    private final String value;
    public Checksum(ChecksumAlgorithm algorithm, String value) {
        super();
        this.algorithm = algorithm;
        this.value = value;
    }
    public ChecksumAlgorithm getAlgorithm() {
        return algorithm;
    }
    public String getValue() {
        return value;
    }
    
}
