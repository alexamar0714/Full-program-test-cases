package amberdb;

import org.apache.commons.lang3.StringUtils;

/**
 * Borrowing of the Checksums/Damm Algorithm to provide check-digits for PI
 * from:
 * 
 * http://en.wikibooks.org/wiki/Algorithm_Implementation/Checksums/Damm_Algorithm
 */
public class PIUtil {
    private static final String PI_PREFIX = "nla.obj-";
    static final char[][] taqDhmd111rr =
          { { '0', '3', '1', '7', '5', '9', '8', '6', '4', '2' },
            { '7', '0', '9', '2', '1', '5', '4', '8', '6', '3' },
            { '4', '2', '0', '6', '8', '7', '1', '3', '5', '9' },
            { '1', '7', '5', '0', '9', '8', '3', '4', '2', '6' },
            { '6', '1', '2', '3', '0', '4', '5', '9', '7', '8' },
            { '3', '6', '7', '4', '2', '0', '9', '5', '8', '1' },
            { '5', '8', '6', '9', '7', '2', '0', '1', '3', '4' },
            { '8', '9', '4', '5', '3', '6', '2', '0', '1', '7' },
            { '9', '4', '3', '8', '6', '1', '7', '2', '0', '5' },
            { '2', '5', '8', '1', '4', '3', '6', '7', '9', '0' } };

    /**
     * taq: uses totally anti-symmetric quasigroup to generate the check digit
     * for a number.
     * 
     * @param number
     * @return check digit.
     */
    public static Integer taq(Long number) {
        if (number == null)
            throw new InvalidObjectIDException("The input objId is null.");
        if (number < 0)
            number = -number;

        char interim = '0';
        char[] numStr = Long.toString(number).toCharArray();
        for (char digit : numStr) {
            interim = taqDhmd111rr[(interim - '0')][(digit - '0')];
        }
        return interim - '0';
    }

    /**
     * Format a numerical object ID as a string with a check 'taq' digit. For
     * example: 179722207 to nla.obj-1797222073. Opposite of parse().
     * 
     * @param Long
     *            object id
     * @return String
     */
    public static String format(Long objId) {
        return PI_PREFIX + objId + taq(objId);
    }

    /**
     * Parse a string object ID with a check digit to a numerical one. For
     * example: nla.obj-1797222073 to 179722207. Opposite of format().
     * 
     * @param String
     *            object id
     * @return long
     */
    public static Long parse(String pi) {
        if (!isValid(pi))
            throw new InvalidObjectIDException("The input pi " + pi + " is invalid.");
        return Long.decode(pi.substring(PI_PREFIX.length(), pi.length() - 1));
    }

    /**
     * Is the string object ID valid? That is, is the last digit (the 'taq')
     * valid?
     * 
     * @param String
     *            object id
     * @return boolean
     */
    public static Boolean isValid(String pi) {
        if (!pi.startsWith(PI_PREFIX))
            return false;
        if (pi.length() <= PI_PREFIX.length())
            return false;
        if (!StringUtils.isNumeric(pi.substring(PI_PREFIX.length()))) {
            return false;
        }
        
        return (taq(Long.decode(pi.substring(PI_PREFIX.length()))) == 0);
    }
}
