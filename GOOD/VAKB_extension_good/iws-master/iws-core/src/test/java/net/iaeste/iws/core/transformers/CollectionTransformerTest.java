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
package net.iaeste.iws.core.transformers;

import static net.iaeste.iws.core.transformers.CollectionTransformer.explodeEnumSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import net.iaeste.iws.api.constants.exchange.IWSExchangeConstants;
import net.iaeste.iws.api.enums.Language;
import net.iaeste.iws.api.enums.exchange.FieldOfStudy;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author  Pavel Fiala / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class CollectionTransformerTest {

    @Test
    public void testConcatEnumCollection() {
        // Collection is unordered
        final Language[] languages = { Language.FRENCH, Language.ENGLISH };
        final String expectedString1 = String.format("%s%s%s", languages[0], IWSExchangeConstants.SET_DELIMITER, languages[1]);
        final String expectedString2 = String.format("%s%s%s", languages[1], IWSExchangeConstants.SET_DELIMITER, languages[0]);

        final List<Language> list = Arrays.asList(languages);
        final Set<Language> set = EnumSet.copyOf(list);

        final String resultFromList = CollectionTransformer.concatEnumCollection(list);
        final String resultFromSet = CollectionTransformer.concatEnumCollection(set);

        assertThat(resultFromList.equals(expectedString1) || resultFromList.equals(expectedString2), is(true));
        assertThat(resultFromSet.equals(expectedString1) || resultFromSet.equals(expectedString2), is(true));
    }

    @Test
    public void testExplodeEnumListAndSet() {
        final List<Language> expectedList = new ArrayList<>(2);
        expectedList.add(Language.ENGLISH);
        expectedList.add(Language.FRENCH);
        final Set<Language> expectedSet = EnumSet.copyOf(expectedList);

        final String stringArgument = String.format("%s%s%s", Language.ENGLISH.name(), IWSExchangeConstants.SET_DELIMITER, Language.FRENCH.name());

        final List<Language> resultList = CollectionTransformer.explodeEnumList(Language.class, stringArgument);
        final Set<Language> resultSet = explodeEnumSet(Language.class, stringArgument);

        assertThat(resultList, is(expectedList));
        assertThat(resultSet, is(expectedSet));
    }

    @Test
    public void testJoin() {
        final String[] elementsToJoin = { "str1", "str2" };
        // Collection is unordered
        final String expectedString1 = String.format("%s%s%s", elementsToJoin[0], IWSExchangeConstants.SET_DELIMITER, elementsToJoin[1]);
        final String expectedString2 = String.format("%s%s%s", elementsToJoin[1], IWSExchangeConstants.SET_DELIMITER, elementsToJoin[0]);
        final List<String> stringList = Arrays.asList(elementsToJoin);

        final String result = CollectionTransformer.join(stringList);

        assertThat(result.equals(expectedString1) || result.equals(expectedString2), is(true));
    }

    @Test
    public void testExplodeStringListAndSet() {
        final String[] strings = { "str1", "str2" };
        final String stringToExplode = String.format("%s%s%s", strings[0], IWSExchangeConstants.SET_DELIMITER, strings[1]);
        final List<String> expectedList = Arrays.asList(strings);
        final Set<String> expectedSet = new HashSet<>(expectedList);

        final List<String> resultList = CollectionTransformer.explodeStringList(stringToExplode);
        final Set<String> resultSet = CollectionTransformer.explodeStringSet(stringToExplode);

        assertThat(resultList, is(expectedList));
        assertThat(resultSet, is(expectedSet));
    }

    @Test
    public void testExplodeOneItemStringTicket873() {
        final String[] strings = { "str1" };
        final String stringToExplode = strings[0];
        final List<String> expectedList = Arrays.asList(strings);
        final Set<String> expectedSet = new HashSet<>(expectedList);

        final List<String> resultList = CollectionTransformer.explodeStringList(stringToExplode);
        final Set<String> resultSet = CollectionTransformer.explodeStringSet(stringToExplode);

        assertThat(resultList, is(expectedList));
        assertThat(resultSet, is(expectedSet));
    }

    // =========================================================================
    // Tests to improve logic, based on log errors
    // -------------------------------------------------------------------------
    // In the production log files, numerous cases of errors with the enum's
    // were seen. The IWS logic was extended to reduce the number of user
    // errors. The following tests are all checking values which caused an error
    // in the log files.
    //   Three tests have been added, one for single known values, one where an
    // invalid field separator is used, with an internal compensation and the
    // remaining values which is completely invalid.
    // =========================================================================

    @Test
    public void testNamesWithMatches() {
        final Collection<FieldOfStudy> result1 = explodeEnumSet(FieldOfStudy.class, "Architecture");
        assertThat(result1.size(), is(1));
        assertThat(result1.contains(FieldOfStudy.ARCHITECTURE), is(true));

        final Collection<FieldOfStudy> result2 = explodeEnumSet(FieldOfStudy.class, "MECHANICAL ENGINEERING");
        assertThat(result2.size(), is(1));
        assertThat(result2.contains(FieldOfStudy.MECHANICAL_ENGINEERING), is(true));

        final Collection<FieldOfStudy> result3 = explodeEnumSet(FieldOfStudy.class, "Mechanical Engineering");
        assertThat(result3.size(), is(1));
        assertThat(result3.contains(FieldOfStudy.MECHANICAL_ENGINEERING), is(true));

        final Collection<FieldOfStudy> result4 = explodeEnumSet(FieldOfStudy.class, "ENERGY ENGINEERING");
        assertThat(result4.size(), is(1));
        assertThat(result4.contains(FieldOfStudy.ENERGY_ENGINEERING), is(true));

        final Collection<FieldOfStudy> result5 = explodeEnumSet(FieldOfStudy.class, "Electrical Engineering");
        assertThat(result5.size(), is(1));
        assertThat(result5.contains(FieldOfStudy.ELECTRICAL_ENGINEERING), is(true));

        final Collection<FieldOfStudy> result6 = explodeEnumSet(FieldOfStudy.class, "Environmental Engineering");
        assertThat(result6.size(), is(1));
        assertThat(result6.contains(FieldOfStudy.ENVIRONMENTAL_ENGINEERING), is(true));

        final Collection<FieldOfStudy> result7 = explodeEnumSet(FieldOfStudy.class, "Chemistry");
        assertThat(result7.size(), is(1));
        assertThat(result7.contains(FieldOfStudy.CHEMISTRY), is(true));

        final Collection<FieldOfStudy> result8 = explodeEnumSet(FieldOfStudy.class, "IT");
        assertThat(result8.size(), is(1));
        assertThat(result8.contains(FieldOfStudy.IT), is(true));
    }

    @Test
    public void testWithCommaDelimiter() {
        final Collection<FieldOfStudy> result = explodeEnumSet(FieldOfStudy.class, "Physics, Electrical engineering");
        assertThat(result.size(), is(2));
        assertThat(result.contains(FieldOfStudy.PHYSICS), is(true));
        assertThat(result.contains(FieldOfStudy.ELECTRICAL_ENGINEERING), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch1() {
        explodeEnumSet(FieldOfStudy.class, "Computer Science");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch2() {
        explodeEnumSet(FieldOfStudy.class, "ECONOMICS");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch3() {
        explodeEnumSet(FieldOfStudy.class, " ECONOMICS");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch4() {
        explodeEnumSet(FieldOfStudy.class, "Food Technology");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch5() {
        explodeEnumSet(FieldOfStudy.class, "Any scientific field is eligible");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch6() {
        explodeEnumSet(FieldOfStudy.class, "finance , business administration");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch7() {
        explodeEnumSet(FieldOfStudy.class, "Marketing, Business");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch8() {
        explodeEnumSet(FieldOfStudy.class, "ELECTRICAL_ENGINEERING OR MECHANICAL ENGINEERING");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameWithNoMatch9() {
        explodeEnumSet(FieldOfStudy.class, "Computer Science / Computer Engineering / Electrical Engineering");
    }
}
