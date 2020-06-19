package amberdb.graph;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import amberdb.graph.AmberProperty;
import amberdb.graph.DataType;

public class AmberPropertyTest {

    private static String str;
    private static Integer number;
    private static Long lnumber;
    private static Boolean bool;
    private static Float fnumber;
    private static Double dnumber;
    private static Date date;
    
    @BeforeClass
    public static void setup() {
        str = "string";
        number = 123;
        lnumber = 123456L;
        bool = true;
        fnumber = (float) 123.45;
        dnumber = (double) 12354;
        
        SimpleDateFormat fmt = new SimpleDateFormat();
        fmt.applyPattern("yyyyMMdd'T'HHmmss");
        try {
            date = fmt.parse("19710101T151515");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testEncodeDecodeAmberDataTypes() {
        byte[] strVal = AmberProperty.encode(str);
        byte[] numberVal = AmberProperty.encode(number);
        byte[] lnumberVal = AmberProperty.encode(lnumber);
        byte[] boolVal = AmberProperty.encode(bool);
        byte[] fnumberVal = AmberProperty.encode(fnumber);
        byte[] dnumberVal = AmberProperty.encode(dnumber);
        byte[] dateVal = AmberProperty.encode(date);
        
        assertNotNull(strVal);
        assertNotNull(numberVal);
        assertNotNull(lnumberVal);
        assertNotNull(boolVal);
        assertNotNull(fnumberVal);
        assertNotNull(dnumberVal);
        assertNotNull(dateVal);
        
        assertEquals(AmberProperty.decode(strVal, DataType.STR), str);
        assertEquals(AmberProperty.decode(numberVal, DataType.INT), number);
        assertEquals(AmberProperty.decode(lnumberVal, DataType.LNG), lnumber);
        assertEquals(AmberProperty.decode(boolVal, DataType.BLN), bool);
        assertEquals(AmberProperty.decode(fnumberVal, DataType.FLT), fnumber);
        assertEquals(AmberProperty.decode(dnumberVal, DataType.DBL), dnumber);
        assertEquals(AmberProperty.decode(dateVal, DataType.DTE), date);
        
    }
}
