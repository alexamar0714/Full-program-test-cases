/* Utils.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright Ⓒ 2014-2015 Universiteit Gent
 * 
 * This file is part of the Degage Web Application
 * 
 * Corresponding author (see also AUTHORS.txt)
 * 
 * Kris Coolsaet
 * Department of Applied Mathematics, Computer Science and Statistics
 * Ghent University 
 * Krijgslaan 281-S9
 * B-9000 GENT Belgium
 * 
 * The Degage Web Application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Degage Web Application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with the Degage Web Application (file LICENSE.txt in the
 * distribution).  If not, see http://www.gnu.org/licenses/.
 */

package controllers;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Various utility functions and constants for use in controllers and views
 */
public final class Utils {

    public static Locale DEFAULT_LOCALE = new Locale("NL", "be");

    private static DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    public static String toString(TemporalAccessor ta) {
        return ta == null ? null : DATETIME_FORMATTER.format(ta);
    }

    private static DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    public static String toDateString(TemporalAccessor ta) {
        return ta == null ? null : DATE_FORMATTER.format(ta);
    }

    private static DateTimeFormatter LOCALIZED_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("eee dd MMM yyyy HH:mm", DEFAULT_LOCALE).withZone(ZoneId.systemDefault());

    public static String toLocalizedString(Instant instant) {
        return instant == null ? null : LOCALIZED_DATETIME_FORMATTER.format(instant.atZone(ZoneId.systemDefault()));
    }

    public static String toLocalizedString(LocalDateTime dateTime) {
        return dateTime == null ? null : LOCALIZED_DATETIME_FORMATTER.format(dateTime);
    }

    private static DateTimeFormatter LOCALIZED_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("eee dd MMM yyyy", DEFAULT_LOCALE).withZone(ZoneId.systemDefault());

    public static String toLocalizedDateString(Instant instant) {
        return instant == null ? null : LOCALIZED_DATE_FORMATTER.format(instant.atZone(ZoneId.systemDefault()));
    }

    public static String toLocalizedDateString(LocalDateTime dateTime) {
        return dateTime == null ? null : LOCALIZED_DATE_FORMATTER.format(dateTime);
    }

    private static DateTimeFormatter LOCALIZED_SHORT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yy", DEFAULT_LOCALE).withZone(ZoneId.systemDefault());

    public static String toLocalizedDateStringShort(LocalDate localDate) {
        return localDate == null ? null : LOCALIZED_SHORT_DATE_FORMATTER.format(localDate);
    }

    private static DateTimeFormatter LOCALIZED_LONG_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", DEFAULT_LOCALE).withZone(ZoneId.systemDefault());

    public static String toLocalizedDateString(LocalDate localDate) {
        return localDate == null ? null : LOCALIZED_LONG_DATE_FORMATTER.format(localDate);
    }

    public static String toLocalizedDateStringWithDayOfWeek(LocalDate localDate) {
        return localDate == null ? null : LOCALIZED_DATE_FORMATTER.format(localDate);
    }


    private static DateTimeFormatter LOCALIZED_WEEK_DAY_FORMATTER =
            DateTimeFormatter.ofPattern("EE dd/MM", DEFAULT_LOCALE).withZone(ZoneId.systemDefault());

    public static String toLocalizedWeekDayString(LocalDate localDate) {
        return localDate == null ? null : LOCALIZED_WEEK_DAY_FORMATTER.format(localDate);
    }

    public static Instant toInstant(String string) {
        return (string == null || string.isEmpty())
                ? null
                : ZonedDateTime.parse(string, DATETIME_FORMATTER).toInstant();
    }

    public static LocalDate toLocalDate(String string) {
        return (string == null || string.isEmpty())
                ? null
                : LocalDate.parse(string, DATE_FORMATTER);
    }

    public static LocalDateTime toLocalDateTime(String string) {
        return (string == null || string.isEmpty())
                ? null
                : LocalDateTime.parse(string, DATETIME_FORMATTER);
    }

    /**
     * Check whether a string is null or consists entirely of whitespace
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        } else {
            int i = 0;
            int length = str.length();
            while (i < length && Character.isWhitespace(str.charAt(i)) ) {
                i++;
            }
            return i == length;
        }
    }

    /**
     * Split a list into a number of lists
     */
    public static<T> Iterable<List<T>> splitList (List<T> list, int columns) {
        List<List<T>> result = new ArrayList<>(columns);
        for (int i = 0; i < columns; i++) {
            result.add(new ArrayList<>());
        }
        int height = (list.size() + columns - 1) / columns;
        int row = 0;
        int column = 0;
        for (T element : list) {
            if (row == height) {
                row = 0;
                column ++;
            }
            result.get(column).add(element);
            row ++;
        }
        return result;
    }
}
