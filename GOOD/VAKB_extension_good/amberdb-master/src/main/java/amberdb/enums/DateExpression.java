package amberdb.enums;

import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DateExpression {
    INTERVAL(new String[]{
            "(.*)\\s*-\\s*(.*)", // e.g. 1898 - 1988
            "(.*)\\s*/\\s*(.*)", // e.g. 03.Mar.1817/09.Sep.1916
            "(.*)\\s*or\\s*(.*)",// e.g. 1920 or 1921
            "(.*)\\s*,\\s*(.*)"  // e.g. 1938,2002          
    }), 
    CIRCAINTERVAL(new String[]{
            "c.\\s*(.*)\\s*"     // e.g. c1980s
    }), 
    INSTANT(new String[]{
            "(\\d{1,2})\\s*[\\s\\-\\./](.*)", // e.g. 9 November 1990
            "(.*)\\s*[,\\s\\-\\./](\\d{1,2})" // e.g. 1990 November 9
    }), 
    CIRCAINSTANT(new String[]{
            "c.\\s*(.*)\\s*",
            "c\\s*(.*)\\s*"
    }),
    SEASON(new String[] {
            "WINTER",
            "SPRING",
            "SUMMER",
            "AUTUMN"
    }),
    YEAR(new String[]{
            "(\\d{3,4})\\s*[\\s\\-\\./](.*)", // which matches the year (in 3 or 4 digits)
                                              // first appears in the date expression and 
                                              // the rest of the date expression:
                                              // e.g. (1890) - Apr 1900
                                              //      (1890)/Apr 1900
                                              //      (1890).April.31
                                              //      (1890) April 31
            "(.*)\\s*[,\\s\\-\\./](\\d{3,4})", // e.g. April 31 (1890)
            "(\\d{3,4})"                       // e.g. (560)
    }), 
    MONTH(new String[]{
            "JAN",
            "FEB",
            "MAR",
            "APR",
            "MAY",
            "JUN",
            "JUL",
            "AUG",
            "SEP",
            "OCT",
            "NOV",
            "DEC"            
    });
    
    String[] expressions;
    
    DateExpression(String... expressions) {
        this.expressions = expressions;
    }
    
    public Pattern[] getPatterns() {
        Pattern[] patterns = new Pattern[expressions.length];
        for (int i = 0; i < expressions.length; i++) {
            patterns[i] = Pattern.compile(expressions[i]);
        }
        return patterns;
    }
        
    public Integer getMonthOfYear(String expr) {
        if (expr.length() < 3) return null;
        String inputMonth = expr.trim().toUpperCase().substring(0, 3);
        int monthOfYear = Arrays.asList(expressions).indexOf(inputMonth) + 1;
        return (monthOfYear > 0)? monthOfYear : null;
    }
    
    public String getAbbrMonth(int monthOfYear) {
        return expressions[monthOfYear - 1];
    }
    
    public String getPlainExprFromCircaDate(String expr) {
        Pattern[] patterns = getPatterns();
        for (Pattern pattern : patterns) {
            try {
                Matcher matcher = pattern.matcher(expr);
                if (matcher.find()) {
                    MatchResult result = matcher.toMatchResult();
                    if (result.groupCount() >= 1)
                        return matcher.toMatchResult().group(1).trim();
                }
            } catch (IllegalStateException e) {
                // try next pattern
            }
        }
        return expr;
    }
    
    public boolean isYear(String expr) {
        if (expr == null || expr.trim().length() < 3 || expr.trim().length() > 4) return false;
        try {
            return (Integer.parseInt(expr.trim()) > 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public boolean isSeason(String expr) {
        if (expr == null || expr.isEmpty()) return false;
        return Arrays.asList(expressions).contains(expr.trim().toUpperCase());
    }
    
    public boolean isDay(String expr, int daysOfMonth) {
        if (expr == null || expr.trim().length() > 2) return false;
        try {
            Integer day = Integer.parseInt(expr.trim());
            return (day > 0 && day <= daysOfMonth);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
