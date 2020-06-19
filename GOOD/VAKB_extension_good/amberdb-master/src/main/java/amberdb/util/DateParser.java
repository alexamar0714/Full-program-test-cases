package amberdb.util;

import static amberdb.enums.DateExpression.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateParser {
    static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy");
    static final Logger log = LoggerFactory.getLogger(DateParser.class);    
    static final String bulkDateRangePrefix= "(bulk";   
    
    /**
     * parseDateRange: returns the from date and to date in a list of date.
     * examples of acceptable date formats can be parsed accurately:
     *   date range in AS                  start date      end date
     *   null                              null            null
     *   c.1936                            01-01-1936      31-12-1936
     *   c.1930s                           01-01-1930      31-12-1939
     *   1936 or 1937                      01-01-1936      31-12-1937
     *   12 September 1984                 12-09-1984      12-09-1984
     *   [4] June 1937                     04-06-1937      04-06-1937
     *   May 1992                          01-05-1992      31-05-1992
     *   [November] 1935                   01-11-1935      30-11-1935
     *   9-15 December 1938                09-12-1938      15-12-1938
     *   1935-1936                         01-01-1935      31-12-1936
     *   Mar 1817 - Sep 1916               01-03-1817      30-09-1916
     *   03.Mar.1817/09.Sep.1916           03-03-1817      09-09-1916
     *   03.Mar.1817 - 09.Sep.1916         03-03-1817      09-09-1916
     *   1935-c.1936                       01-01-1935      31-12-1936
     *   1914, 1919-1960 (bulk 1930-1958)  01-01-1914      31-12-1960
     * 
     * detectable invalid date formats are the following will throw ParseException 
     * (Invalid date expression..):
     *   text string does not contain any numbers
     *   text string containing only 1-2 digit numbers
     * 
     * limitation: correctness of dates extracted from text string besides
     * the above text expression formats are not guaranteed.
     *     
     * @param dateRangeExpr - input date range string
     * @return the from date and to date of the date range in a date list.
     * @throws ParseException
     * 
     * Note: Defaults: if there's no end date found in the date Range e.g. 1932 -
     *       the end date is then defaults to null.
     *
     *       It is ok to not specify a start date e.g. - 1799, in this case, the
     *       start date in the returned date range will be null.
     *
     *       Input date expression start with year first can have " " (space) or
     *       "." (full stop) as delimiter in the expression e.g. 1970.Mar.01 or
     *       1970 Mar 01, but not "-" as delimiter in the expression i.e. not
     *       1970-Mar-01 (currently not extracted)
     *
     *       limitation for year extraction: it's assumed that any
     *       year value should have the length between 3 - 4 digits in order to be
     *       recognized as a year value.  Year value should appear either at the
     *       beginning of a date expression (followed by a space or / or - or .) or at the 
     *       end of a date expression (preceed by a space or / or - or .).
     *       
     *       limitation for month extraction: it's assumed month is expressed in
     *       full text or 3 letter abbreviation (case insensitive).
     *
     *       format for the day part of a input date expression must be (1 or 2)
     *       digit number between 1 and max days in month.
     *       
     *       limitation for parsing date ranges reference the four seasons:
     *       - winter, spring, summer, autumn
     *       currently any season reference will produce the whole year coverage
     *       for corresponding date extracted from the input date range expression. 
     */
    public static List<Date> parseDateRange(String dateRangeExpr) throws ParseException {
        String numberInStrExpr = "(.*)?([0-9]+)(.*)?";
        if (dateRangeExpr != null) dateRangeExpr = dateRangeExpr.trim();
        if (!StringUtils.isEmpty(dateRangeExpr) && !dateRangeExpr.matches(numberInStrExpr)) {
            throw new ParseException("Invalid date expression: " + dateRangeExpr, 0);
        }
        try {
            return parseDateRange(dateRangeExpr, null);
        } catch (Exception e) {
            throw new ParseException("Invalid date expression: " + dateRangeExpr, 0);
        }
    }
    public static List<Date> parseDateRange(String dateRangeExpr, Date defaultDate) throws ParseException {
        if (dateRangeExpr == null || dateRangeExpr.trim().isEmpty()) return null;
        dateRangeExpr = dateRangeExpr.trim();
        Calendar cal = Calendar.getInstance();
        if (defaultDate != null) cal.setTime(defaultDate);
        
        List<Date> dateRange = new ArrayList<>();      
        dateRangeExpr = dateRangeExpr.replace("[", "").replace("]", "").trim();        
        if (dateRangeExpr.indexOf(bulkDateRangePrefix) >= 0)
            dateRangeExpr = dateRangeExpr.substring(0, dateRangeExpr.indexOf(bulkDateRangePrefix));

        // parse circa date
        if (dateRangeExpr.startsWith("c")) {
            try {
                return parseCircaDateRange(dateRangeExpr);
            } catch (Exception e) {
                dateRangeExpr = CIRCAINSTANT.getPlainExprFromCircaDate(dateRangeExpr);
            }
        }
        
        // parse the date range
        List<String> dateRangePair = getExprPair(dateRangeExpr, INTERVAL.getPatterns());
        if (dateRangePair == null && dateRangeExpr.length() > 2) {
            Integer year = parseYear(dateRangeExpr);           
            if (year == null) {
                throw new ParseException("Failed to parse date expr: " + dateRangeExpr, 0);
            }
            cal.set(Calendar.YEAR, year);
            Date fromDate = parseDate(dateRangeExpr, true, cal);
            Date toDate = parseDate(dateRangeExpr, false, cal);
            dateRange.add(fromDate);
            dateRange.add(toDate);
            return dateRange;
        }
        
        if (dateRangePair != null && dateRangePair.size() == 2) {
            String startDateToken = (dateRangePair.get(0) == null)?"":dateRangePair.get(0).trim();
            String endDateToken = (dateRangePair.get(1) == null)?"":dateRangePair.get(1).trim();
            Integer startYear = parseYear(startDateToken);
            Integer endYear = parseYear(endDateToken);
            if (startYear == null && endYear == null) {
                throw new ParseException("Failed to parse date expr: " + dateRangeExpr, 0);
            } else if (startYear == null) {
                cal.set(Calendar.YEAR, endYear);
            }
            
            if (endDateToken.isEmpty()) {
                defaultDate = constructDate(false, "9999", "DEC", "31");
            } else if (endDateToken.length() < 3 && startDateToken.length() >=4 && YEAR.isYear(startDateToken.substring(0, 4))) {
                int defaultYear = Integer.parseInt(startDateToken.substring(0, 4));
                cal.set(defaultYear, 11, 31);
                if (INSTANT.isDay(endDateToken, cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDateToken));
                if (cal.get(Calendar.YEAR) != defaultYear) {
                    log.debug("Failed to parse date expr : " + dateRangeExpr + ", expect year:" + defaultYear + ", actual year:" + cal.get(Calendar.YEAR) + ", endDateTokenlen: " + endDateToken.length());
                    throw new ParseException("Failed to parse date expr : " + dateRangeExpr, 0);
                }
                defaultDate = cal.getTime();
            }
            Date endDate = null;
            if (endDateToken.length() > 0) {
                if (YEAR.isYear(endDateToken)) {
                    endDate = constructDate(false, endDateToken, "DEC", "31");
                } else {
                    try {
                        List<Date> dates = parseDateRange(endDateToken,defaultDate);
                        if (dates != null && dates.size() > 0)
                            endDate = dates.get(dates.size() - 1);
                    } catch (ParseException e) {
                        boolean isFromDate = false;
                        endDate = parseDate(endDateToken, isFromDate, cal);
                    }
                }
            }
            
            if (endDate != null) {
                cal.setTime(endDate);
            }
            
            log.debug("dateRangePair: " + startDateToken + "," + endDateToken);
            Date startDate = null;
            if (startDateToken.length() > 0) {
                if (YEAR.isYear(startDateToken)) {
                    startDate = constructDate(true, startDateToken, "JAN", "1");
                } else if (MONTH.getMonthOfYear(startDateToken) != null) {
                    int monthOfYear = MONTH.getMonthOfYear(startDateToken);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.MONTH, monthOfYear - 1);
                    startDate = cal.getTime();
                } else {
                    if (INSTANT.isDay(startDateToken, cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
                        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDateToken));
                        startDate = cal.getTime();
                    } else {
                        try {
                            List<Date> dates = parseDateRange(startDateToken, endDate);
                            if (dates != null && dates.size() > 0)
                                startDate = dates.get(0);
                        } catch (ParseException e) {
                            boolean isFromDate = true;
                            startDate = parseDate(startDateToken, isFromDate, cal);
                        }
                    }
                }
            }
            dateRange.add(startDate);
            dateRange.add(endDate);
        }
        if (dateRange.isEmpty()) return parseCircaDateRange(dateRangeExpr);
        return dateRange;
    }
    
    private static Integer parseYear(String dateRangeExpr) {
        if (dateRangeExpr.endsWith("s")) {
            dateRangeExpr = dateRangeExpr.substring(0, dateRangeExpr.length() - 1);
        }
        String yearStrAtStart = "";
        String yearStrAtEnd = "";
        int yearLength = 3;
        if (StringUtils.isEmpty(dateRangeExpr)) {
            return null;
        }
        if (dateRangeExpr.length() > 3) {
            yearLength = 4;
        } else if (dateRangeExpr.length() < 3){
            return null;
        }
        yearStrAtStart = dateRangeExpr.substring(0, yearLength).trim();
        yearStrAtEnd = dateRangeExpr.substring(dateRangeExpr.length() - yearLength).trim();
        Integer year = null;
        try {
            year = Integer.parseInt(yearStrAtStart);
        } catch (NumberFormatException e) {
            try {
                year = Integer.parseInt(yearStrAtEnd);
            } catch (NumberFormatException ex) {
                year = null;
            }
        }
        if (dateRangeExpr.startsWith("" + year + " ") || dateRangeExpr.startsWith("" + year + "/") || 
            dateRangeExpr.startsWith("" + year + "-") || dateRangeExpr.startsWith("" + year + ".") ||
            dateRangeExpr.endsWith(" " + year) || dateRangeExpr.endsWith("/" + year) || dateRangeExpr.endsWith("-" + year) || 
            dateRangeExpr.endsWith("." + year) || dateRangeExpr.equals(""+year)) {
            return year;
        }
        return null;
    }
    
    /**
     * parseCircaDateRange: returns the from date and to date 10 years apart starting from
     *                      01/01/<circa year>
     */
    protected static List<Date> parseCircaDateRange(String dateRangeExpr) throws ParseException {
        List<Date> dateRange = new ArrayList<>();
        dateRangeExpr = dateRangeExpr.trim();
        boolean addDecade = false;
        if (dateRangeExpr.endsWith("s")) {
            addDecade = true;
            dateRangeExpr = dateRangeExpr.substring(0, dateRangeExpr.length() - 1);
        }
        try {
            Pattern circaPattern = CIRCAINTERVAL.getPatterns()[0];
            Matcher matcher = circaPattern.matcher(dateRangeExpr);
            if (matcher.matches()) {
                int startYear = Integer.parseInt(matcher.group(1));
                int endYear = (addDecade) ? startYear + 9 : startYear;
                dateRange.add(constructDate(true, "" + startYear, "JAN", "1"));
                dateRange.add(constructDate(false, "" + endYear, "DEC", "31"));
            }
        } catch (Exception e) {
            dateRangeExpr = dateRangeExpr.replace("c.", "");
            dateRange = parseDateRange(dateRangeExpr);
        }
        return dateRange;
    }
    
    /**
     * parseDate: returns a date approximated from the date expression.
     * @param dateExpr - input date string
     * @param isFromDate - flag indicating whether this is a from date or to date
     * @return the parsed date
     * @throws ParseException
     */
    public static Date parseDate(String dateExpr, boolean isFromDate) throws ParseException {
        return parseDate(dateExpr, isFromDate, null);
    }
    
    public static Date parseDate(String dateExpr, boolean isFromDate, Calendar cal) throws ParseException {
        if (dateExpr == null) return null;
        dateExpr = dateExpr.trim().replace(",", "");
        // check for decade in dateExpr
        if (dateExpr.endsWith("s")) {
            try {
                return parseCircaDateRange("c." + dateExpr).get((isFromDate)?0:1);
            } catch (ParseException e) {
                dateExpr = dateExpr.substring(0, dateExpr.length() - 1);
                return parseDate(dateExpr, isFromDate, cal);
            }
        } 
        
        // parse year
        Map.Entry<Pattern, List<String>> yearAndRestMatch = getMatchedExprPair(dateExpr.trim(), YEAR.getPatterns());
        if (yearAndRestMatch == null) {
            if (cal == null) throw new RuntimeException("failed to extract year for date expression " + dateExpr);
            Integer month;
            if (INSTANT.isDay(dateExpr, cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateExpr) - 1);
            } else if ((month = MONTH.getMonthOfYear(dateExpr)) != null) {
                cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.MONTH, MONTH.getMonthOfYear(dateExpr), month - 1);
            } else {
                throw new ParseException("Invalid date expression: " + dateExpr, 0);
            }
            return cal.getTime();
        }
        
        String year = "";
        String restExpr = "";
        String yrPattern = yearAndRestMatch.getKey().pattern();
        if (yrPattern != null && (yrPattern.startsWith("(\\d") || yrPattern.startsWith("c (\\d"))) {
            year = yearAndRestMatch.getValue().get(0);
            restExpr = yearAndRestMatch.getValue().get(1);
        } else {
            restExpr = yearAndRestMatch.getValue().get(0);
            year = yearAndRestMatch.getValue().get(1);
        }
        
        // parse month
        if (restExpr == null || restExpr.isEmpty() || SEASON.isSeason(restExpr)) 
            return constructDate(isFromDate, year, null, null);
        
        if (MONTH.getMonthOfYear(restExpr.trim()) != null) {
            int monthOfYear = MONTH.getMonthOfYear(restExpr.trim());
            cal.set(Calendar.MONTH, monthOfYear - 1);
            int daysOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            String date = restExpr.substring(restExpr.length() - 2).replaceAll("\\D", "");
            if (!INSTANT.isDay(date, daysOfMonth)) date = null;
            Date potentialDate = constructDate(isFromDate, year, restExpr, date);
            log.debug("date: " + date + ", daysOfMonth: " + daysOfMonth + ", monthOfYear " + monthOfYear);
            
            if (potentialDate != null) return potentialDate;
        }
        
        // parse date
        Map.Entry<Pattern, List<String>> dateAndRestMatch = getMatchedExprPair(restExpr, INSTANT.getPatterns());
        if (dateAndRestMatch == null) return null;
        String date = "";
        String month = "";
        String dtPattern = dateAndRestMatch.getKey().pattern();
        if (dtPattern != null && (dtPattern.startsWith("(\\d"))) {
            date = dateAndRestMatch.getValue().get(0);
            month = dateAndRestMatch.getValue().get(1);
        } else {
            month = dateAndRestMatch.getValue().get(0);
            date = dateAndRestMatch.getValue().get(1);            
        }
        return constructDate(isFromDate, year, month, date);
    }

    /**
     * constructDate: construct a date base on available year, month and date. If date is not provided,
     *                it will be approximately base on whether it's a from date or to date.
     * @param isFromDate - flag indicate whether this is a from date or a to date
     * @param year       - year to construct date from
     * @param month      - month to construct date from
     * @param date       - date to construct date from
     * @param mnthAbbrList - list of abbrivatted month
     * @return the constructed date
     * @throws ParseException
     */
    private static Date constructDate(boolean isFromDate, String year, String month, String date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.clear();

        if (INSTANT.isYear(year))
            cal.set(Calendar.YEAR, Integer.parseInt(year)); 
        Integer monthOfYear = null;
        if (month == null) {
            monthOfYear = (isFromDate)?1:12;
        } else {
            monthOfYear = MONTH.getMonthOfYear(month);
        }
        cal.set(Calendar.MONTH, monthOfYear - 1);
        int daysOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);              
        if (INSTANT.isDay(date, daysOfMonth)) {
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date));
        } else if (isFromDate) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            cal.set(Calendar.DAY_OF_MONTH, daysOfMonth);
        }    
        return cal.getTime();
    }
    
    /**
     * getExprPair: returns the pair of matched expressions passed from the input expr.
     * @param expr - input string
     * @param patterns - patterns containing 2 groups of expressions to be matched
     * @return the pair of matched expressions passed from the input expr.
     */
    private static List<String> getExprPair(String expr, Pattern...patterns) {
        Map.Entry<Pattern, List<String>> match = getMatchedExprPair(expr, patterns);
        if (match == null) return null;
        return match.getValue();
    }
    
    /**
     * getMatchedExprPair: returns a map of matched pattern, and the matched result strings in a list
     * @param expr - input string
     * @param patterns - patterns containing 2 groups of expressions to be matched
     * @return a map of matched pattern, and the matched result strings in a list
     */
    private static Map.Entry<Pattern, List<String>> getMatchedExprPair(String expr, Pattern...patterns) {
        Map<Pattern, List<String>> match = new ConcurrentHashMap<>();
        List<String> exprPair = new ArrayList<>();
        IllegalStateException error = null;
        for (Pattern pattern : patterns) {
            try {
                if (pattern != null) {
                    Matcher matcher = pattern.matcher(expr);
                    if (matcher.find()) {
                        MatchResult result = matcher.toMatchResult();
                        String from = null;
                        String to = null;
                        if (result.groupCount() == 1) {
                            from = matcher.toMatchResult().group(1).trim();
                            to = null;
                        } else if (result.groupCount() > 1) {
                            from = matcher.toMatchResult().group(1).trim();
                            to = matcher.toMatchResult().group(2).trim();
                        }
                        exprPair.add(from);
                        exprPair.add(to);
                        match.put(pattern, exprPair);
                        return match.entrySet().iterator().next();
                    }
                }
            } catch (IllegalStateException e) {
                // if landed this exception, then try to parse the expression with the next pattern
                error = e;
            }
        }
        
        // ok, this expression does not match any pattern defined, need to report this expression in order
        // to have its pattern defined.
        if (error != null) 
            throw new RuntimeException("Failed to parse expr : " + expr + " as "+ error.getMessage(), error);
        return null;
    }
}
