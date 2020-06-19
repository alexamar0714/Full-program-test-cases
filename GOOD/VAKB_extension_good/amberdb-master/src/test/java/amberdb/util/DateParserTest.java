package amberdb.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DateParserTest {
    final String[] testDateRangeExprs = {
       null,
       "       ",
       " - 1799",
       "1732 -",
       "1732  ",
       "  1799",
       "c.1990s", // circa date
       "1817 - 1916",
       "1817/1916",
       "Mar 1817 - Sep 1916",
       "03.Mar.1817/09.Sep.1916",
       "1732.Mar",
       "1732 Mar 9",
       "12 September 1984",
       "May 1993"
    };
        
    final String[] dateRangePattern = { 
        "(.*)\\s*-\\s*(.*)",
        "(.*)\\s*/\\s*(.*)"};
    
    final String[] bulkDateRangePattern = {
        "\\s*\\(bulk (.*)\\s*-\\s*(.*)\\)"
    };
    
    List<Date> expectedFromDate = new ArrayList<>();
    List<Date> expectedToDate = new ArrayList<>();
    final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy");
    
    @Before
    public void setup() throws ParseException {
        expectedFromDate.add(null);
        expectedFromDate.add(null);
        expectedFromDate.add(null);
        expectedFromDate.add(dateFmt.parse("01/01/1732"));
        expectedFromDate.add(dateFmt.parse("01/01/1732"));
        expectedFromDate.add(dateFmt.parse("01/01/1799"));
        expectedFromDate.add(dateFmt.parse("01/01/1990"));
        expectedFromDate.add(dateFmt.parse("01/01/1817"));
        expectedFromDate.add(dateFmt.parse("01/01/1817"));
        expectedFromDate.add(dateFmt.parse("01/03/1817"));
        expectedFromDate.add(dateFmt.parse("03/03/1817"));
        expectedFromDate.add(dateFmt.parse("01/03/1732"));
        expectedFromDate.add(dateFmt.parse("09/03/1732"));
        expectedFromDate.add(dateFmt.parse("12/09/1984"));
        expectedFromDate.add(dateFmt.parse("01/05/1993"));
        expectedToDate.add(null);
        expectedToDate.add(null);
        expectedToDate.add(dateFmt.parse("31/12/1799"));
        expectedToDate.add(dateFmt.parse("01/01/1732"));
        expectedToDate.add(dateFmt.parse("31/12/1732"));
        expectedToDate.add(dateFmt.parse("31/12/1799"));
        expectedToDate.add(dateFmt.parse("31/12/1999"));
        expectedToDate.add(dateFmt.parse("31/12/1916"));
        expectedToDate.add(dateFmt.parse("31/12/1916"));
        expectedToDate.add(dateFmt.parse("30/09/1916"));
        expectedToDate.add(dateFmt.parse("09/09/1916"));
        expectedToDate.add(dateFmt.parse("31/03/1732"));
        expectedToDate.add(dateFmt.parse("09/03/1732"));  // to chk
        expectedToDate.add(dateFmt.parse("12/09/1984"));
        expectedToDate.add(dateFmt.parse("31/05/1993"));
    }
    
    @Test(expected = ParseException.class)
    public void testInvalidDateRangeWithoutNumber() throws ParseException {
        String str = "invalid date range str";
        List<Date> dateRange = DateParser.parseDateRange(str);
    }
    
    @Test(expected = ParseException.class)
    public void testInvalidDateRangeWithNumber() throws ParseException {
        String str = "19th century";
        List<Date> dateRange = DateParser.parseDateRange(str);
    }
    
    @Test(expected = ParseException.class)
    public void testInvalidDateRangeWithAHyphen() throws ParseException {
        String str = "19th-century";
        List<Date> dateRange = DateParser.parseDateRange(str);
    }
    
    @Test
    public void testAdditionalDateRangePattern() throws ParseException {
        String dateRange1 = "c.1936";
        String expectedStartDate1 = "01-01-1936";
        String expectedEndDate1 = "31-12-1936";
        List<Date> dates1 = DateParser.parseCircaDateRange(dateRange1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        assertEquals(expectedStartDate1, dateFormat.format(dates1.get(0)));
        assertEquals(expectedEndDate1, dateFormat.format(dates1.get(1)));
        
        String dateRange2 = "1936 or 1937";
        String expectedStartDate2 = "01-01-1936";
        String expectedEndDate2 = "31-12-1937";
        List<Date> dates2 = DateParser.parseDateRange(dateRange2);
        assertEquals(expectedStartDate2, dateFormat.format(dates2.get(0)));
        assertEquals(expectedEndDate2, dateFormat.format(dates2.get(1)));
        
        String dateRange3 = "4 June 1937";
        String expectedStartDate3 = "04-06-1937";
        String expectedEndDate3 = "04-06-1937";
        List<Date> dates3 = DateParser.parseDateRange(dateRange3);
        assertEquals(expectedStartDate3, dateFormat.format(dates3.get(0)));
        assertEquals(expectedEndDate3, dateFormat.format(dates3.get(1)));
        
        String dateRange4 = "November 1937";
        String expectedStartDate4 = "01-11-1937";
        String expectedEndDate4 = "30-11-1937";
        List<Date> dates4 = DateParser.parseDateRange(dateRange4);
        assertEquals(expectedStartDate4, dateFormat.format(dates4.get(0)));
        assertEquals(expectedEndDate4, dateFormat.format(dates4.get(1)));
        
        String dateRange5 = "[4] June 1937";
        String expectedStartDate5 = "04-06-1937";
        String expectedEndDate5 = "04-06-1937";
        List<Date> dates5 = DateParser.parseDateRange(dateRange5);
        assertEquals(expectedStartDate5, dateFormat.format(dates5.get(0)));
        assertEquals(expectedEndDate5, dateFormat.format(dates5.get(1)));
        
        String dateRange6 = "[November] 1937";
        String expectedStartDate6 = "01-11-1937";
        String expectedEndDate6 = "30-11-1937";
        List<Date> dates6 = DateParser.parseDateRange(dateRange6);
        assertEquals(expectedStartDate6, dateFormat.format(dates6.get(0)));
        assertEquals(expectedEndDate6, dateFormat.format(dates6.get(1)));
        
        String dateRange7 = "1914, 1919-1960 (bulk 1930-1958)";
        String expectedStartDate7 = "01-01-1914";
        String expectedEndDate7 = "31-12-1960";
        List<Date> dates7 = DateParser.parseDateRange(dateRange7);
        assertEquals(expectedStartDate7, dateFormat.format(dates7.get(0)));
        assertEquals(expectedEndDate7, dateFormat.format(dates7.get(1)));
        
        String dateRange8 = "9-15 December 1938";
        String expectedStartDate8 = "09-12-1938";
        String expectedEndDate8 = "15-12-1938";
        List<Date> dates8 = DateParser.parseDateRange(dateRange8);
        assertEquals(expectedStartDate8, dateFormat.format(dates8.get(0)));
        assertEquals(expectedEndDate8, dateFormat.format(dates8.get(1)));
        
        String dateRange9 = "c.1928-2002";
        String expectedStartDate9 = "01-01-1928";
        String expectedEndDate9 = "31-12-2002";
        List<Date> dates9 = DateParser.parseDateRange(dateRange9);
        assertEquals(expectedStartDate9, dateFormat.format(dates9.get(0)));
        assertEquals(expectedEndDate9, dateFormat.format(dates9.get(1)));
    }
    
    @Test
    public void testCircaDateRangePattern() throws ParseException {
        String dateRange = "c.1990s";
        String expectedStartDate = "01-01-1990";
        String expectedEndDate = "31-12-1999";
        List<Date> dates = DateParser.parseCircaDateRange(dateRange);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        assertEquals(expectedStartDate, dateFormat.format(dates.get(0)));
        assertEquals(expectedEndDate, dateFormat.format(dates.get(1)));
        
        dateRange = "1910-c.1990";
        expectedStartDate = "01-01-1910";
        expectedEndDate = "31-12-1990";
        dates = DateParser.parseDateRange(dateRange);
        assertEquals(expectedStartDate, dateFormat.format(dates.get(0)));
        assertEquals(expectedEndDate, dateFormat.format(dates.get(1)));
    }
    
    @Test
    public void testIndivDate() throws ParseException {
        String dateRangeExpr = "1732";
        String expectedStartDate = "01-01-1732";
        String expectedEndDate = "31-12-1732";
        
        List<Date> dateRange = DateParser.parseDateRange(dateRangeExpr);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        assertEquals(expectedStartDate, dateFormat.format(dateRange.get(0)));
        assertEquals(expectedEndDate, dateFormat.format(dateRange.get(1)));
    }
    
    @Test 
    public void testBulkDateRangePattern() throws ParseException {
        String testBulkDateRangeExprs = "1894-1935 (bulk 1919-1935)";
        String expectedFromDate = "01/01/1894";
        String expectedToDate = "31/12/1935";
        List<Date> dateRange = DateParser.parseDateRange(testBulkDateRangeExprs);
        assertEquals(expectedFromDate, dateFmt.format(dateRange.get(0)));
        assertEquals(expectedToDate, dateFmt.format(dateRange.get(1)));
        
        String testBulkDateRangeExprs1 = "1914, 1919-1960 (bulk 1930-1958)";
        String expectedFromDate1 = "01/01/1914";
        String expectedToDate1 = "31/12/1960";
        List<Date> dateRange1 = DateParser.parseDateRange(testBulkDateRangeExprs1);
        System.out.println(dateRange1.get(0));
        System.out.println(dateRange1.get(1));
        assertEquals(expectedFromDate1, dateFmt.format(dateRange1.get(0)));
        assertEquals(expectedToDate1, dateFmt.format(dateRange1.get(1)));
    }
    
    @Test
    public void testDateRangePattern() throws ParseException {
        int i = 0;
        for (String dateRangeExpr : testDateRangeExprs) {
            System.out.println("date range: " + dateRangeExpr);
            List<Date> dateRange = DateParser.parseDateRange(dateRangeExpr);
            
            if (dateRangeExpr == null || dateRangeExpr.trim().isEmpty())
                assertNull(dateRange);
            else {
                if (dateRange.isEmpty()) {
                    boolean isFromDate = true;
                    Date fromDate = DateParser.parseDate(dateRangeExpr, isFromDate);
                    assertEquals(dateFmt.format(expectedFromDate.get(i)), dateFmt.format(fromDate));
                } else {
                    if (expectedFromDate.get(i) == null) {
                        assertNull(dateRange.get(0));
                    } else {
                        if (dateRange.get(0) == null) {
                            assertEquals(dateFmt.format(expectedFromDate.get(i)), dateFmt.format(dateRange.get(0)));
                        }
                    }

                    if (expectedToDate.get(i) == null) {
                        assertNull(dateRange.get(1));
                    } else {
                        if (dateRange.get(1) != null) {
                            assertEquals(dateFmt.format(expectedToDate.get(i)), dateFmt.format(dateRange.get(1)));
                        }
                    }
                }
            }
            i++;
        }
    }
}
