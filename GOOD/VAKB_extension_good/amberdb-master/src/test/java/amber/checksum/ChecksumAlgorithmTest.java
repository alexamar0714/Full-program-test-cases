package amber.checksum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class ChecksumAlgorithmTest {

    @Test
    public void fromString() {
        assertThat(ChecksumAlgorithm.fromString("MD5"), is(ChecksumAlgorithm.MD5));
    }
    
    @Test
    public void fromStringLowerCase() {
        assertThat(ChecksumAlgorithm.fromString("md5"), is(ChecksumAlgorithm.MD5));
    }
    
    @Test
    public void fromStringNoHash() {
        assertThat(ChecksumAlgorithm.fromString("SHA1"), is(ChecksumAlgorithm.SHA1));
    }
    
    @Test
    public void fromStringWithHash() {
        assertThat(ChecksumAlgorithm.fromString("SHA-1"), is(ChecksumAlgorithm.SHA1));
    }

}
