/*
 * Licensed to IAESTE A.s.b.l. (IAESTE) under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership. The Authors
 * (See the AUTHORS file distributed with this work) licenses this file to
 * You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a
 * copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.iaeste.iws.api.util;

import static net.iaeste.iws.api.constants.exchange.IWSExchangeConstants.REFNO_PATTERN;

import net.iaeste.iws.api.constants.IWSConstants;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p>All Input validation is handled via this class. It contains a number of
 * "ensure..." Methods, which all throw {@code IllegalArgumentException} if the
 * input is not allowed.</p>
 *
 * <p>The main purpose of these checks, is to ensure that the IWS Objects fails
 * as early as possible, so no unneeded requests are made to the IWS.</p>
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public abstract class Verifications implements Verifiable {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    // Our internal error strings
    private static final String ERROR_NOT_NULL = "The field %s may not be null.";
    private static final String ERROR_NOT_EMPTY = "The field %s may not be empty.";
    private static final String ERROR_NOT_LONGER = "The field %s may not be longer than %d characters.";
    private static final String ERROR_COLLECTION_LONGER = "The field %s may not contain more than %d Objects.";
    private static final String ERROR_ARRAY_LONGER = "The field %s may not be longer than %d bytes.";
    private static final String ERROR_TOO_SHORT = "The field %s must be at least %d characters long";
    private static final String ERROR_NOT_EXACT_LENGTH = "The field %s is not matching the required length %d.";
    private static final String ERROR_MINIMUM_VALUE = "The field %s must be at least %d.";
    private static final String ERROR_NOT_WITHIN_LIMITS = "The field %s is not within the required limits from %s to %d.";
    private static final String ERROR_ILLEGAL_VALUES = "The field %s contain illegal value %s.";
    private static final String ERROR_DELIMITER_FOUND = "THe field %s contains the internally used delimiter '%s'.";
    private static final String ERROR_INVALID = "The field %s is invalid.";
    private static final String ERROR_INVALID_IDENTIFIER = "The field %s is not a valid Identifier.";
    private static final String ERROR_INVALID_REGEX = "The field %s does not follow the required format %s.";
    private static final String ERROR_NOT_VERIFABLE = "The field %s is not verifiable.";
    private static final String ERROR_INVALID_EMAIL = "The e-mail address %s (%s) is invalid.";
    private static final String ERROR_INVALID_REFNO = "The provided reference number (refno) %s is invalid.";

    // Our internal constants to verify the Id
    private static final String UUID_FORMAT = "[\\da-z]{8}-[\\da-z]{4}-[\\da-z]{4}-[\\da-z]{4}-[\\da-z]{12}";
    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_FORMAT);

    /**
     * To ensure that the data is not causing problems when being accessed via
     * the IWS WebService interface, it is necessary to sanitize it. Problems
     * which has been found is added to this method, to improve the internal
     * data quality.
     *
     * @param source String to sanitize
     * @return Sanitized String
     * @see <a href="https://trac.iaeste.net/ticket/837">837</a> &amp; <a href="https://trac.iaeste.net/ticket/987">987</a>
     */
    public static String sanitize(final String source) {
        String sanitized = source;

        if ((source != null) && !source.isEmpty()) {
            sanitized = IWSConstants.PATTERN_INVALID_CHARS.matcher(
                          IWSConstants.PATTERN_INVALID_SPACES.matcher(
                            IWSConstants.PATTERN_INVALID_NEWLINES.matcher(source).
                            replaceAll("\n")).
                          replaceAll(" ")).
                        replaceAll("");
        }

        return sanitized;
    }

    /**
     * Sanitize Wrapper for Collections.
     *
     * @param source Collection to Sanitize
     * @return Set of sanitized String
     */
    protected static Set<String> sanitize(final Collection<String> source) {
        Set<String> sanitized = null;

        if (source != null) {
            sanitized = new HashSet<>(source.size());
            for (final String str : source) {
                sanitized.add(sanitize(str));
            }
        }

        return sanitized;
    }

    // =========================================================================
    // Standard Methods
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        return ReflectiveStandardMethods.reflectiveEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return ReflectiveStandardMethods.reflectiveHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return ReflectiveStandardMethods.reflectiveToString(this);
    }

    // =========================================================================
    // Ensuring methods for setters, that throws IllegalArgumentExceptions
    // =========================================================================

    /**
     * Throws an {@code IllegalArgumentException} if the given value is null.
     *
     * @param field Name of the field
     * @param value The value for the field
     * @throws IllegalArgumentException if the value is null
     */
    protected static void ensureNotNull(final String field, final Object value) {
        if (value == null) {
            throw new IllegalArgumentException(format(ERROR_NOT_NULL, field));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is empty.
     *
     * @param field Name of the field
     * @param value The value for the field
     * @throws IllegalArgumentException if the value is empty
     */
    protected static void ensureNotEmpty(final String field, final String value) {
        if ((value != null) && value.isEmpty()) {
            throw new IllegalArgumentException(format(ERROR_NOT_EMPTY, field));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is empty.
     *
     * @param field Name of the field
     * @param value The value for the field
     * @throws IllegalArgumentException if the value is empty
     */
    private static void ensureNotEmpty(final String field, final Collection<?> value) {
        if ((value != null) && value.isEmpty()) {
            throw new IllegalArgumentException(format(ERROR_NOT_EMPTY, field));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is null or
     * empty.
     *
     * @param field Name of the field
     * @param value The value of the field
     * @throws IllegalArgumentException if the value is null or empty
     */
    protected static void ensureNotNullOrEmpty(final String field, final String value) {
        ensureNotNull(field, value);
        ensureNotEmpty(field, value);
    }

    /**
    * Throws an {@code IllegalArgumentException} if the given value is null or
    * empty.
    *
    * @param field Name of the field
    * @param value The value of the field
    * @throws IllegalArgumentException if the value is null or empty
    */
    protected static void ensureNotNullOrEmpty(final String field, final Collection<?> value) {
        ensureNotNull(field, value);
        ensureNotEmpty(field, value);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null or too short.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The minimal length for the field
     * @throws IllegalArgumentException if the value is empty or too long
     */
    protected static void ensureNotNullOrTooShort(final String field, final String value, final int length) {
        ensureNotNull(field, value);

        if (value.length() < length) {
            throw new IllegalArgumentException(format(ERROR_TOO_SHORT, field, length));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is too
     * long.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The maximum length for the field
     * @throws IllegalArgumentException if the value is too long
     */
    protected static void ensureNotTooLong(final String field, final Collection<?> value, final int length) {
        if ((value != null) && (value.size() > length)) {
            throw new IllegalArgumentException(format(ERROR_COLLECTION_LONGER, field, length));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is too
     * long.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The maximum length for the field
     * @throws IllegalArgumentException if the value is too long
     */
    protected static void ensureNotTooLong(final String field, final String value, final int length) {
        if ((value != null) && (value.length() > length)) {
            throw new IllegalArgumentException(format(ERROR_NOT_LONGER, field, length));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is too
     * long.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The maximum length for the field
     * @throws IllegalArgumentException if the value is too long
     */
    protected static void ensureNotTooLong(final String field, final byte[] value, final int length) {
        if ((value != null) && (value.length > length)) {
            throw new IllegalArgumentException(format(ERROR_ARRAY_LONGER, field, length));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null or too long.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The maximum length for the field
     * @throws IllegalArgumentException if the value is null or too long
     */
    protected static void ensureNotNullOrTooLong(final String field, final Collection<?> value, final int length) {
        ensureNotNull(field, value);
        ensureNotTooLong(field, value, length);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null or too long.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The maximum length for the field
     * @throws IllegalArgumentException if the value is null or too long
     */
    protected static void ensureNotNullOrTooLong(final String field, final String value, final int length) {
        ensureNotNull(field, value);
        ensureNotTooLong(field, value, length);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null, empty or too long.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The maximum length for the field
     * @throws IllegalArgumentException if the value is null or empty or too long
     */
    protected static void ensureNotNullOrEmptyOrTooLong(final String field, final String value, final int length) {
        ensureNotNullOrEmpty(field, value);
        ensureNotTooLong(field, value, length);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null or not the exact length.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The exact length of the field
     * @throws IllegalArgumentException if the value is null not of exact length
     */
    protected static void ensureNotNullAndExactLength(final String field, final String value, final int length) {
        ensureNotNull(field, value);
        ensureExactLength(field, value, length);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is neither
     * null or less than the value.
     *
     * @param field   Name of the field
     * @param value   The value of the field
     * @param minimum The minimally allowed value for the field
     * @param <T>     The Number type
     * @throws IllegalArgumentException if the value is null or too small
     */
    protected static <T extends Number> void ensureNotNullAndMinimum(final String field, final T value, final T minimum) {
        ensureNotNull(field, value);
        ensureMinimum(field, value, minimum);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given Collection is
     * either null or containing illegal values.
     *
     * @param field      The name of the field (value) to be verified
     * @param value      the Collection to verify
     * @param acceptable Collection of allowed values
     * @param <E>        The Enum Type
     */
    protected static <E extends Enum<?>> void ensureNotNullAndContains(final String field, final Collection<E> value, final Collection<E> acceptable) {
        ensureNotNull(field, value);

        for (final E found : value) {
            if (!acceptable.contains(found)) {
                throw new IllegalArgumentException(format(ERROR_ILLEGAL_VALUES, field, found));
            }
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given Enum is
     * either null or not in the list of allowed values.
     *
     * @param field      The name of the field (value) to be verified
     * @param value      the Enum to verify
     * @param acceptable Collection of allowed values
     * @param <E>        The Enum type
     */
    protected static <E extends Enum<?>> void ensureNotNullAndContains(final String field, final E value, final Collection<E> acceptable) {
        ensureNotNull(field, value);

        if (!acceptable.contains(value)) {
            throw new IllegalArgumentException(format(ERROR_ILLEGAL_VALUES, field, value));
        }
    }

    protected static <E> void ensureNotContaining(final String field, final Collection<E> value, final String... forbidden) {
        if ((value != null) && !value.isEmpty() && (forbidden != null) && (forbidden.length > 0)) {
            for (final E collectionField : value) {
                for (final String forbiddenValue : forbidden) {
                    throwIfContaining(field, collectionField, forbiddenValue);
                }
            }
        }
    }

    private static <E> void throwIfContaining(final String field, final E value, final String forbidden) {
        final String collectionValue = lower(value.toString());
        final String toFind = lower(forbidden);

        if (collectionValue.contains(toFind)) {
            throw new IllegalArgumentException(format(ERROR_DELIMITER_FOUND, field, forbidden));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is not the
     * exact length.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @param length The exact length of the field
     * @throws IllegalArgumentException if the value is not the exact length
     */
    private static void ensureExactLength(final String field, final String value, final int length) {
        if ((value != null) && (value.length() != length)) {
            throw new IllegalArgumentException(format(ERROR_NOT_EXACT_LENGTH, field, length));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is less
     * than the value.
     *
     * @param field   Name of the field
     * @param value   The value of the field
     * @param minimum The minimally allowed value for the field
     * @param <T>     The Number type
     * @throws IllegalArgumentException if the value is too small
     */
    private static <T extends Number> void ensureMinimum(final String field, final T value, final T minimum) {
        if ((value != null) && (value.doubleValue() < minimum.doubleValue())) {
            throw new IllegalArgumentException(format(ERROR_MINIMUM_VALUE, field, minimum));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is not
     * within the given limits.
     *
     * @param field   Name of the field
     * @param value   The value of the field
     * @param minimum The minimally allowed value for the field
     * @param maximum The maximally allowed value for the field
     * @param <T>     The Number type
     * @throws IllegalArgumentException if the value is null not of exact length
     */
    protected static <T extends Number> void ensureWithinLimits(final String field, final T value, final T minimum, final T maximum) {
        if ((value != null) && ((value.doubleValue() < minimum.doubleValue()) || (value.doubleValue() > maximum.doubleValue()))) {
            throw new IllegalArgumentException(format(ERROR_NOT_WITHIN_LIMITS, field, minimum, maximum));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null or not within the given limits.
     *
     * @param field   Name of the field
     * @param value   The value of the field
     * @param minimum The minimally allowed value for the field
     * @param maximum The maximally allowed value for the field
     * @param <T>     The Number type
     * @throws IllegalArgumentException if the value is null not of exact length
     */
    protected static <T extends Number> void ensureNotNullAndWithinLimits(final String field, final T value, final T minimum, final T maximum) {
        ensureNotNull(field, value);
        ensureWithinLimits(field, value, minimum, maximum);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null or doesn't follow the provided regular expression.
     *
     * @param field   Name of the field
     * @param value   The value of the field
     * @param pattern The Pattern for the Regular Expression
     * @param regex   The Regular Expression
     */
    protected static void ensureNotNullAndFollowRegex(final String field, final String value, final Pattern pattern, final String regex) {
        ensureNotNull(field, value);

        if (!pattern.matcher(value).matches()) {
            throw new IllegalArgumentException(format(ERROR_INVALID_REGEX, field, regex));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null or not verifiable.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @throws IllegalArgumentException if the value is either null or not verifiable
     */
    protected static void ensureVerifiable(final String field, final Verifiable value) {
        if ((value != null) && !value.validate().isEmpty()) {
            throw new IllegalArgumentException(format(ERROR_NOT_VERIFABLE, field));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is either
     * null or not verifiable.
     *
     * @param field  Name of the field
     * @param value  The value of the field
     * @throws IllegalArgumentException if the value is either null or not verifiable
     */
    protected static void ensureNotNullAndVerifiable(final String field, final Verifiable value) {
        ensureNotNull(field, value);
        ensureVerifiable(field, value);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given Id is invalid,
     * i.e. if it not null and the format doesn't match the required format.
     *
     * @param field Name of the Id
     * @param value The value for the Id
     * @throws IllegalArgumentException if the Id doesn't follow the correct format
     */
    public static void ensureValidId(final String field, final String value) {
        if ((value != null) && !UUID_PATTERN.matcher(value).matches()) {
            // The error message is deliberately not showing the format of our Id
            // type - no need to grant hackers too much information, since all
            // legal requests should not have a problem obtaining legal Id's
            throw new IllegalArgumentException(format(ERROR_INVALID, field));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is neither
     * an IWS Id (UUID) or a valid Offer Reference Number.
     *
     * @param field Name of the Identifier
     * @param value The value of the Identifier
     * @throws IllegalArgumentException if the value is not a valid identifier
     */
    private static void ensureValidIdentifier(final String field, final String value) {
        if ((value != null) && !(UUID_PATTERN.matcher(value).matches() || REFNO_PATTERN.matcher(value).matches())) {
            throw new IllegalArgumentException(format(ERROR_INVALID_IDENTIFIER, field));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given list of
     * Identifiers contain invalid identifiers.
     *
     * @param field  Name of the field
     * @param values List of Identifiers to check for validity
     * @throws IllegalArgumentException if the list contain invalid identifiers
     */
    private static void ensureValidIdentifiers(final String field, final Collection<String> values) {
        if (values != null) {
            for (final String id : values) {
                ensureValidIdentifier(field, id);
            }
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given Id is invalid,
     * i.e. if it is either null or doesn't match the required format.
     *
     * @param field Name of the Id
     * @param value The value for the Id
     * @throws IllegalArgumentException if the Id is invalid
     */
    protected static void ensureNotNullAndValidId(final String field, final String value) {
        ensureNotNull(field, value);
        ensureValidId(field, value);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given list of
     * identifiers is either null, empty or contain invalid entries.
     *
     * @param field  Name of the field
     * @param values List of Identifiers to check
     * @throws IllegalArgumentException if the values is null, empty or invalid
     */
    protected static void ensureNotNullOrEmptyAndValidIdentifiers(final String field, final Collection<String> values) {
        ensureNotNullOrEmpty(field, values);
        ensureValidIdentifiers(field, values);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given list of
     * identifiers is either null or contain invalid entries.
     *
     * @param field  Name of the field
     * @param values List of Identifiers to check
     * @throws IllegalArgumentException if the values is null or invalid
     */
    protected static void ensureNotNullAndValidIdentifiers(final String field, final Collection<String> values) {
        ensureNotNull(field, values);
        ensureValidIdentifiers(field, values);
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is not a
     * valid e-mail address, i.e. if it is either null or doesn't match the
     * required format.
     *
     * @param field Name of the field, value is ignored in this method
     * @param value The value to verify
     * @throws IllegalArgumentException if the e-mail address is invalid
     */
    protected static void ensureValidEmail(final String field, final String value) {
        if ((value != null) && !IWSConstants.EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(format(ERROR_INVALID_EMAIL, value, field));
        }
    }

    /**
     * Throws an {@code IllegalArgumentException} if the given value is not a
     * valid e-mail address, i.e. if it is either null or doesn't match the
     * required format.
     *
     * @param field Name of the field
     * @param value The value to verify
     * @throws IllegalArgumentException if the e-mail address is invalid
     */
    protected static void ensureNotNullAndValidEmail(final String field, final String value) {
        ensureNotNullOrTooLong(field, value, 100);
        ensureValidEmail(field, value);
    }

    private static void ensureValidRefno(final String refno) {
        if (!REFNO_PATTERN.matcher(refno).matches()) {
            throw new IllegalArgumentException(format(ERROR_INVALID_REFNO, refno));
        }
    }

    protected static void ensureNotNullAndValidRefno(final String field, final String refno) {
        ensureNotNull(field,  refno);
        ensureValidRefno(refno);
    }

    // =========================================================================
    // Other Methods
    // =========================================================================

    /**
     * <p>Calculates the Exchange Year for Offers. Used for both searching for
     * Offers, and for creating Offers. According to the specifications, the
     * Exchange year changes on September first to the next year, meaning that
     * the "Current Exchange Year" is the same as the "Current Year" until
     * September 1st, and the following year afterword.</p>
     * <ul>
     *   <li>Example 1: April 1st, 2014 =&gt; Current Exchange Year is 2014</li>
     *   <li>Example 2: August 31st, 2014 =&gt; Current Exchange Year is 2014</li>
     *   <li>Example 3: September 1st, 2014 =&gt; Current Exchange Year is 2015</li>
     *   <li>Example 4: October 1st, 2014 =&gt; Current Exchange Year is 2015</li>
     * </ul>
     *
     * @return Current Exchange Year
     */
    public static int calculateExchangeYear() {
        final Date date = new Date();

        return date.getCurrentYear() + ((date.getCurrentMonth() >= Calendar.SEPTEMBER) ? 1 : 0);
    }

    /**
     * For those cases where an {@code IllegalArgumentException} should be
     * thrown, but with a generic value, this method is used.
     *
     * @param field Name of the field
     * @throws IllegalArgumentException as the field is invalid
     */
    protected static void throwIllegalArgumentException(final String field) {
        throw new IllegalArgumentException(format(ERROR_INVALID, field));
    }

    /**
     * The method takes a value, and verifies that this value is not null. If an
     * error is found, then the information is added to the validation
     * Map.
     *
     * @param validation Map with Error information
     * @param field      The name of the field (value) to be verified
     * @param value      The value to verify
     */
    protected static void isNotNull(final Map<String, String> validation, final String field, final Object value) {
        if (value == null) {
            addError(validation, field, "The field may not be null.");
        }
    }

    /**
     * The method takes a value, and verifies that it is neither null, nor
     * containing illegal values.
     *
     * @param validation Map with Error information
     * @param field      The name of the field (value) to be verified
     * @param value      the Collection to verify
     * @param acceptable Collection of allowed values
     * @param <E>        The Enum type
     */
    protected static <E extends Enum<?>> void isNotNullAndContains(final Map<String, String> validation, final String field, final Collection<E> value, final Collection<E> acceptable) {
        isNotNull(validation, field, value);

        if (value != null) {
            boolean containIllegalValue = false;

            for (final E found : value) {
                if (!acceptable.contains(found)) {
                    containIllegalValue = true;
                }
            }

            if (containIllegalValue) {
                addError(validation, field, "The field contains illegal values.");
            }
        }
    }

    /**
     * The method takes a value, and verifies that it is neither null, nor
     * containing illegal values.
     *
     * @param validation Map with Error information
     * @param field      The name of the field (value) to be verified
     * @param value      the Enum to verify
     * @param acceptable Collection of allowed values
     * @param <E>        The Enum type
     */
    protected static <E extends Enum<?>> void isNotNullAndContains(final Map<String, String> validation, final String field, final E value, final Collection<E> acceptable) {
        isNotNull(validation, field, value);

        if ((value != null) && !acceptable.contains(value)) {
            addError(validation, field, "The field contains illegal values.");
        }
    }

    /**
     * The method takes a value, and verifies that this value is verifiable. If
     * an error is found, then the information is added to the validation
     * Map.
     *
     * @param validation Map with Error information
     * @param field      The name of the field (value) to be verified
     * @param value      The value to verify
     */
    protected static void isVerifiable(final Map<String, String> validation, final String field, final Verifiable value) {
        if (value != null) {
            for (final Map.Entry<String, String> entry : value.validate().entrySet()) {
                addError(validation, "Object[" + field + "] field:" + entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * The method takes a value, and verifies that this value is neither null
     * nor not verifiable. If an error is found, then the information is added
     * to the validation Map.
     *
     * @param validation Map with Error information
     * @param field      The name of the field (value) to be verified
     * @param value      The value to verify
     */
    protected static void isNotNullAndVerifiable(final Map<String, String> validation, final String field, final Verifiable value) {
        isNotNull(validation, field, value);
        isVerifiable(validation, field, value);
    }

    /**
     * <p>The method adds error messages for fields with checks for existing
     * messages.</p>
     *
     * <p>If the field in validation Map already had an error, then the error
     * messages are concatenated.</p>
     *
     * @param validation   Map with Error information
     * @param field        The name of the field to add error
     * @param errorMessage The error message for the field
     */
    private static void addError(final Map<String, String> validation, final String field, final String errorMessage) {
        final String message;

        if (validation.containsKey(field)) {
            message = format("%s\n%s", validation.get(field), errorMessage);
        } else {
            message = errorMessage;
        }

        validation.put(field, message);
    }

    /**
     * Formats a given String, using the built-in String format method. If there
     * is a problem with formatting the String, then the method will throw an
     * IllegalFormatException. Otherwise, the method will return the result of
     * formatting the String.
     *
     * @param message The String to be formatted
     * @param args    The Arguments for the formatted String
     * @return Formatted String
     */
    protected static String format(final String message, final Object... args) {
        return String.format(message, args);
    }

    private static String lower(final String str) {
        return str.toLowerCase(IWSConstants.DEFAULT_LOCALE);
    }
}
