package amber.checksum;

import java.util.ArrayList;
import java.util.List;

public enum ChecksumAlgorithm {
    SHA1 ("SHA-1", new String[]{"SHA-1", "SHA1"}), 
    SHA256 ("SHA-256", new String[]{"SHA-256", "SHA256"}), 
    SHA384 ("SHA-384", new String[]{"SHA-384", "SHA384"}), 
    SHA512 ("SHA-512", new String[]{"SHA-512", "SHA512"}), 
    MD5 ("MD5", new String[]{"MD5", "MD-5"});
    
    private String algorithm;
    private String[] alias;

    private ChecksumAlgorithm(String algorithm, String[] alias) {
        this.algorithm = algorithm;
        this.alias = alias;
    }

    public String algorithm() {
        return algorithm;
    }

    public List<String> alias() {
        List<String> list = new ArrayList<>(); 
        for (String str: alias){
            list.add(str);
            list.add(str.toLowerCase());
        }
        return list;
    }

    public static ChecksumAlgorithm fromString(String algorithm){
        for (ChecksumAlgorithm item: values()){
            String noHash = item.algorithm.replaceAll("-", "");
            if (noHash.equalsIgnoreCase(algorithm.replaceAll("-", ""))){
                return item;
            }
        }
        return null;
    }
}
