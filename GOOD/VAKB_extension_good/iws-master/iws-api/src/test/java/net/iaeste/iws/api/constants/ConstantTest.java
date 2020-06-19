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
package net.iaeste.iws.api.constants;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import net.iaeste.iws.api.util.Date;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class ConstantTest {

    /**
     * <p>Private methods should never be tested, as they are part of an
     * internal workflow. Classes should always be tested via their contract,
     * i.e. public methods.</p>
     *
     * <p>However, for Utility Classes, with a Private Constructor, the contract
     * disallows instantiation, so the constructor is thus not testable via
     * normal means. This little Test method will verify that the contract is
     * kept, and that the Constructor is not made public.</p>
     */
    @Test
    public void testPrivateConstructor() {
        try {
            final Constructor<IWSConstants> constructor = IWSConstants.class.getDeclaredConstructor();
            assertThat(constructor.isAccessible(), is(false));
            constructor.setAccessible(true);
            final IWSConstants mapper = constructor.newInstance();
            assertThat(mapper, is(not(nullValue())));
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            fail("Could not invoke Private Constructor: " + e.getMessage());
        }
    }

    @Test
    public void testDateFormat() {
        // According to Trac bug report #412, the date format used in IW4, is
        // the same as in IWS. The requested format is on the ISO Format
        // yyyy-MM-dd, but dd-MMM-yyyy. This test just verifies that.
        final String oct14 = "14-OCT-2010";
        final Date date = new Date(oct14);
        assertThat(date.toString(), is(oct14));
    }

    /**
     * ReDoS or Regular expression Denial of Services, is a way to exploit the
     * complexity of a regular expression engine. It is an advise from
     * <a href="https://www.owasp.org/index.php/Regular_expression_Denial_of_Service_-_ReDoS">OWASP</a>.
     * As the e-mail addresses are used as an input parameter, it is added here,
     * to ensure that any such will not cause the IWS to become unstable.
     */
    @Test
    public void testReDoSResilience() {
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("aaaaaaaaaaaaaaaaaaaaaaaa!").matches(), is(false));
    }

    /**
     * <p>This test verifies the e-mail pattern we're using. The Wikipedia page
     * for e-mail addresses contains a list of known valid and invalid addresses
     * which is used as a base for this test.</p>
     *
     * <p><a href="http://en.wikipedia.org/wiki/Email_address">See</a> for more
     * information.</p>
     *
     * <p>Note; the goal with this test is not to verify that the IWS is 100%
     * compliant, but that it supports the most common cases and that none of
     * the noncomplying addresses is falling through.</p>
     */
    @Test
    public void testCompliantEmailAddresses() {
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b3dddad0d6d2ddd7c0dadec3dfd6f3d6cbd2dec3dfd69dd0dcde">[email protected]</a>").matches(), is(true));
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="10667562693e737f7d7d7f7e507568717d607c753e737f7d">[email protected]</a>").matches(), is(true));
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1677387a7f62627a73387a737871627e6f3874636238707f7873567273666238736e777b667a733875797b">[email protected]</a>").matches(), is(true));
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bfdbd6cccfd0ccdeddd3da91cccbc6d3da91dad2ded6d391c8d6cbd794ccc6d2ddd0d3ffdac7ded2cfd3da91dcd0d2">[email protected]</a>").matches(), is(true));
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="82edf6eae7f0ace7efe3ebeeaff5ebf6eaafe6e3f1eac2e7fae3eff2eee7ace1edef">[email protected]</a>").matches(), is(true));
        //assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="611412041321">[email protected]</a>[IPv6:2001:db8:1ff::a0b:dbd0]").matches(), is(true));
        //assertThat(IWSConstants.EMAIL_PATTERN.matcher("\"much.more unusual\"@example.com").matches(), is(true));
        //assertThat(IWSConstants.EMAIL_PATTERN.matcher("\"<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a6d0c3d4df88d3c8d3d5d3c7ca88e688d3c8d3d5d3c7ca88c5c9cb">[email protected]</a>\"@example.com").matches(), is(true));
        //assertThat(IWSConstants.EMAIL_PATTERN.matcher("\"very.(),:;<>[]\".VERY.\"<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="037566717a43">[email protected]</a>\\ \"very\".unusual\"@strange.example.com").matches(), is(true));
        // (top-level domains are valid hostname's)
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bfcfd0cccbddd0c7ffdcd0d2">[email protected]</a>").matches(), is(true));
        // (local domain name with no TLD)
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a4c5c0c9cdcae4c9c5cdc8d7c1d6d2c1d695">[email protected]</a>").matches(), is(true));
        assertThat(IWSConstants.EMAIL_PATTERN.matcher(" !#$%&'*+-/=?^_`{}|<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d2ac92b7aab3bfa2beb7fcbda0b5">[email protected]</a>").matches(), is(true));
        //assertThat(IWSConstants.EMAIL_PATTERN.matcher("\"()<>[]:,;@\\\"!#$%&'*+-/=?^_`{}| ~.a\"@example.org").matches(), is(true));
        // (space between the quotes)
        //assertThat(IWSConstants.EMAIL_PATTERN.matcher("\" \"@example.org").matches(), is(true));
        // (Unicode characters in local part)
        //assertThat(IWSConstants.EMAIL_PATTERN.matcher("üñîçøðé@example.com").matches(), is(true));
        // (Unicode characters in domain part)
        //assertThat(IWSConstants.EMAIL_PATTERN.matcher("üñîçøðé@üñîçøðé.com").matches(), is(true));

        // Explicitly testing the case from Trac ticket #725
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e18c80938a8ea1888084929584cf8c8895">[email protected]</a>").matches(), is(true));
        // Testing e-mail which was rejected April 17th
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="105d7f7e797b713e5b455d5d555c50757e63717d3e7565">[email protected]</a>").matches(), is(true));
        // Testing e-mail change which was requested by the Board on 2014-05-19
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("Lorna.O'<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4a012b242f0a2838233e23392229253f242923266425382d">[email protected]</a>").matches(), is(true));
        // To protect user privacy, the e-mail address is being updated to
        // uuid @ deleted.now.
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e99a86848c86878ca98d8c858c9d8c8dc787869e">[email protected]</a>").matches(), is(true));
    }

    /**
     * This is the negative test for the e-mail address Pattern being used. The
     * example have all been taken from the Wikipedia page about e-mail
     * <a href="http://en.wikipedia.org/wiki/Email_address">addresses</a>, and
     * is just here to verify that no matter what, our regex may not allow any
     * of these.
     */
    @Test
    public void testNonCompliantEmailAddresses() {
        // (an @ character must separate the local and domain parts)
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("Abc.example.com").matches(), is(false));
        // (only one @ is allowed outside quotation marks)
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d09190b2">[email protected]</a>@<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="731033160b121e031f165d101c1e">[email protected]</a>").matches(), is(false));
        // (none of the special characters in this local part is allowed outside quotation marks)
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("a\"b(c)d,e:f;g<h>i[j\\k]<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="dbb79bbea3bab6abb7bef5b8b4b6">[email protected]</a>").matches(), is(false));
        // (quoted strings must be dot separated, or the only element making up the local-part)
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("just\"not\"<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="06746f616e7246637e676b766a632865696b">[email protected]</a>").matches(), is(false));
        // (spaces, quotes, and backslashes may only exist when within quoted strings and preceded by a backslash)
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("this is\"not\\<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c5a4a9a9aab2a0a185a0bda4a8b5a9a0eba6aaa8">[email protected]</a>").matches(), is(false));
        // (even if escaped (preceded by a backslash), spaces, quotes, and backslashes must still be contained by quotes)
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("this\\ still\\\"not\\<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="046568686b73616044617c65697468612a676b69">[email protected]</a>").matches(), is(false));
        // (top-level domains following up a dot requires minimum two alphabetic characters (ISO_3166-1_alpha-2 code), for Brazil e.g. it must be br according to ISO 3166-1).
        assertThat(IWSConstants.EMAIL_PATTERN.matcher("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c1a4aca0a8ad81a3b3a0bba8adefa3">[email protected]</a>").matches(), is(false));
    }
}
