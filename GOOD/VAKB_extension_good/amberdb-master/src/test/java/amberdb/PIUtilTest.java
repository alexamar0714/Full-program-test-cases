package amberdb;

import static org.junit.Assert.*;

import org.junit.Test;

public class PIUtilTest {

    @Test
    public void testCreateRightCheckDigit() {
        Integer expected = 4;
        long input = 572L;
        assertEquals("The check digit of " + input + " should be " + expected + ".", expected, PIUtil.taq(input));
    }

    @Test
    public void testReturnValidPI() {
        String expected = "nla.obj-5724";
        long input = 572L;
        assertEquals("The returned PI for obj id " + input + " should be " + expected + ".", expected, PIUtil.format(input));
        assertTrue("The returned PI " + PIUtil.format(input) + " should be valid.", PIUtil.isValid(PIUtil.format(input)));
    }

    @Test
    public void testDetectInvalidPI() {
        String input = "nla.obj-5723";
        assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));

        input = "5723";
        assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));

        input = "nla.obj-";
        assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));

        input = "nla.obj-fgsfds";
        assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));

        input = "!!!Are Emus valid PIs???";
        assertFalse("The input PI '" + input + "' is invalid.", PIUtil.isValid(input));
    }

    @Test
    public void testReturnValidObjId() {
        Long expected = 572L;
        String input = "nla.obj-5724";
        assertEquals("The returned objId should be " + expected + ".", expected, PIUtil.parse(input));
    }

    @Test(expected = InvalidObjectIDException.class)
    public void testReturnExceptionForInvalidObjId() {
        PIUtil.parse("nla.obj-5723");
    }

    @Test(expected = InvalidObjectIDException.class)
    public void testCheckDigitsForNull() {
        PIUtil.taq(null);
    }
}
