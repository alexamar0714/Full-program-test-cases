/*
 * Copyright 2017 Axway Software
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.axway.ats.rbv.imap.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import com.axway.ats.action.objects.MimePackage;
import com.axway.ats.action.objects.model.PackageException;
import com.axway.ats.rbv.BaseTest;
import com.axway.ats.rbv.MetaData;
import com.axway.ats.rbv.imap.ImapMetaData;
import com.axway.ats.rbv.imap.Test_ImapStorage;
import com.axway.ats.rbv.imap.rules.HeaderRule;
import com.axway.ats.rbv.imap.rules.HeaderRule.HeaderMatchMode;
import com.axway.ats.rbv.model.MetaDataIncorrectException;
import com.axway.ats.rbv.model.RbvException;

public class Test_HeaderRule extends BaseTest {

    private MetaData metaData;

    @Before
    public void setUp() throws Exception {

        MimePackage mailMessage = new MimePackage(Test_ImapStorage.class.getResourceAsStream("mail.msg"));
        metaData = new ImapMetaData(mailMessage);
    }

    @Test
    public void setExpectedValue() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender", "", HeaderMatchMode.FIND, "setExpectedValue", true);

        //positive
        rule.setExpectedValue("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f0999e969f83c1de9d95829391849f9c9f8495829995b0979d91999cde939f9d">[email protected]</a>");
        assertTrue(rule.isMatch(metaData));

        //negative
        rule.setExpectedValue("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4f382a3d3e382a3d0f28222e2623612c2022">[email protected]</a>");
        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeEqualsPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="533a3d353c20627d3e36213032273c3f3c2736213a3613343e323a3f7d303c3e">[email protected]</a>",
                                         HeaderMatchMode.EQUALS,
                                         "isMatchModeEqualsPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeFindPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b1d8dfd7dec2809fdcd4c3d2d0c5dedddec5d4c3d8d4f1d6dcd0d8dd9fd2dedc">[email protected]</a>",
                                         HeaderMatchMode.FIND,
                                         "isMatchModeFindPositive",
                                         true);

        //the whole string
        assertTrue(rule.isMatch(metaData));

        //part of the string
        rule.setExpectedValue("gmail.com");
        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeLeftPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "infos1.mercatoloterie",
                                         HeaderMatchMode.LEFT,
                                         "isMatchModeLeftPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRightPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "@gmail.com",
                                         HeaderMatchMode.RIGHT,
                                         "isMatchModeRightPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRegexPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         ".*@gmail.com",
                                         HeaderMatchMode.REGEX,
                                         "isMatchModeRegexPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeEqualsNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f29b9c949d81c3dc9f97809193869d9e9d8697809b97b2959f939b9edc919d">[email protected]</a>",
                                         HeaderMatchMode.EQUALS,
                                         "isMatchModeEqualsNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeFindNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "infos1.mercatoloteriegmail.com",
                                         HeaderMatchMode.FIND,
                                         "isMatchModeFindNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeLeftNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "gmail.com",
                                         HeaderMatchMode.LEFT,
                                         "isMatchModeLeftNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRightNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "infos1",
                                         HeaderMatchMode.RIGHT,
                                         "isMatchModeRightNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRegexNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "infos1.mercatoloterie.?gmail.com.+",
                                         HeaderMatchMode.REGEX,
                                         "isMatchModeRegexNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeFindSecondMimePartPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "WINDOWS-1252",
                                         1,
                                         0,
                                         HeaderMatchMode.FIND,
                                         "isMatchModeFindSecondMimePartPositive",
                                         true);

        //the whole string
        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeLeftSecondMimePartPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "text/html",
                                         1,
                                         0,
                                         HeaderMatchMode.LEFT,
                                         "isMatchModeLeftSecondMimePartPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRightSecondMimePartPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "charset=WINDOWS-1252",
                                         1,
                                         0,
                                         HeaderMatchMode.RIGHT,
                                         "isMatchModeRightSecondMimePartPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRegexSecondMimePartPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "text.*",
                                         1,
                                         0,
                                         HeaderMatchMode.REGEX,
                                         "isMatchModeRegexSecondMimePartPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeFindSecondMimePartNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "asdf",
                                         1,
                                         0,
                                         HeaderMatchMode.FIND,
                                         "isMatchModeFindSecondMimePartNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeLeftSecondMimePartNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "asdf",
                                         1,
                                         0,
                                         HeaderMatchMode.LEFT,
                                         "isMatchModeLeftSecondMimePartNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRightSecondMimePartNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "text",
                                         1,
                                         0,
                                         HeaderMatchMode.RIGHT,
                                         "isMatchModeRightSecondMimePartNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRegexSecondMimePartNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "asdf.*",
                                         1,
                                         0,
                                         HeaderMatchMode.REGEX,
                                         "isMatchModeRegexSecondMimePartNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeFindThirdHeaderPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Received",
                                         "invoked from network",
                                         2,
                                         HeaderMatchMode.FIND,
                                         "isMatchModeFindThirdHeaderPositive",
                                         true);

        //the whole string
        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeLeftThirdHeaderPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Received",
                                         "(qmail 20592",
                                         2,
                                         HeaderMatchMode.LEFT,
                                         "isMatchModeLeftThirdHeaderPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRightThirdHeaderPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Received",
                                         "14:33:17 -0000",
                                         2,
                                         HeaderMatchMode.RIGHT,
                                         "isMatchModeRightThirdHeaderPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRegexThirdHeaderPositive() throws RbvException {

        HeaderRule rule = new HeaderRule("Received",
                                         ".qmail 20592.*",
                                         2,
                                         HeaderMatchMode.REGEX,
                                         "isMatchModeRegexThirdHeaderPositive",
                                         true);

        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeFindThirdHeaderNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "asdf",
                                         1,
                                         0,
                                         HeaderMatchMode.FIND,
                                         "isMatchModeFindThirdHeaderNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeLeftThirdHeaderNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "asdf",
                                         1,
                                         0,
                                         HeaderMatchMode.LEFT,
                                         "isMatchModeLeftThirdHeaderNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRightThirdHeaderNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "text",
                                         1,
                                         0,
                                         HeaderMatchMode.RIGHT,
                                         "isMatchModeRightThirdHeaderNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchModeRegexThirdHeaderNegative() throws RbvException {

        HeaderRule rule = new HeaderRule("Content-Type",
                                         "asdf.*",
                                         1,
                                         0,
                                         HeaderMatchMode.REGEX,
                                         "isMatchModeRegexThirdHeaderNegative",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test( expected = RbvException.class)
    public void isMatchNullMetaData() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2940474f465a1807444c5b4a485d4645465d4c5b404c694e44484045074a4644">[email protected]</a>",
                                         HeaderMatchMode.FIND,
                                         "isMatchNullMetaData",
                                         true);

        assertFalse(rule.isMatch(null));

    }

    @Test( expected = MetaDataIncorrectException.class)
    public void isMatchWrongMetaData() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="deb7b0b8b1adeff0b3bbacbdbfaab1b2b1aabbacb7bb9eb9b3bfb7b2f0bdb1b3">[email protected]</a>",
                                         HeaderMatchMode.FIND,
                                         "isMatchEmptyMetaData",
                                         true);
        metaData = new MetaData();
        assertFalse(rule.isMatch(metaData));
    }

    @Test( expected = MetaDataIncorrectException.class)
    public void isMatchEmptyMetaData() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender",
                                         "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9af3f4fcf5e9abb4f7ffe8f9fbeef5f6f5eeffe8f3ffdafdf7fbf3f6b4f9f5f7">[email protected]</a>",
                                         HeaderMatchMode.FIND,
                                         "isMatchEmptyMetaData",
                                         true);
        metaData = new ImapMetaData(null);
        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchNoSuchHeader() throws RbvException {

        HeaderRule rule = new HeaderRule("Sender123",
                                         "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d8b1b6beb7abe9f6b5bdaabbb9acb7b4b7acbdaab1bd98bfb5b9b1b4f6bbb7b5">[email protected]</a>",
                                         HeaderMatchMode.FIND,
                                         "isMatchNoSuchHeader",
                                         true);

        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchHeaderValueAtAnyPosition() throws RbvException, PackageException,
                                                  FileNotFoundException {

        HeaderRule rule;

        rule = new HeaderRule("Test-Header",
                              "some value1",
                              HeaderMatchMode.EQUALS,
                              "isMatchHeaderValueAtAnyPosition",
                              true);
        assertTrue(rule.isMatch(metaData));

        rule = new HeaderRule("Test-Header",
                              "some value2",
                              HeaderMatchMode.EQUALS,
                              "isMatchHeaderValueAtAnyPosition",
                              true);
        assertTrue(rule.isMatch(metaData));

        rule = new HeaderRule("Test-Header",
                              "some value3",
                              HeaderMatchMode.EQUALS,
                              "isMatchHeaderValueAtAnyPosition",
                              true);
        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchHeaderValueAtAnyPositionNegative() throws RbvException, PackageException,
                                                          FileNotFoundException {

        HeaderRule rule = new HeaderRule("Test-Header",
                                         "some value4",
                                         HeaderMatchMode.EQUALS,
                                         "isMatchHeaderValueAtAnyPositionNegative",
                                         true);
        assertFalse(rule.isMatch(metaData));
    }

    @Test
    public void isMatchPartHeaderValueAtAnyPosition() throws RbvException, PackageException,
                                                      FileNotFoundException {

        HeaderRule rule;

        rule = new HeaderRule("Test-Part-Header",
                              "some part value1",
                              0,
                              -1,
                              HeaderMatchMode.EQUALS,
                              "isMatchPartHeaderValueAtAnyPosition",
                              true);
        assertTrue(rule.isMatch(metaData));

        rule = new HeaderRule("Test-Part-Header",
                              "some part value2",
                              0,
                              -1,
                              HeaderMatchMode.EQUALS,
                              "isMatchPartHeaderValueAtAnyPosition",
                              true);
        assertTrue(rule.isMatch(metaData));

        rule = new HeaderRule("Test-Part-Header",
                              "some part value3",
                              0,
                              -1,
                              HeaderMatchMode.EQUALS,
                              "isMatchPartHeaderValueAtAnyPosition",
                              true);
        assertTrue(rule.isMatch(metaData));
    }

    @Test
    public void isMatchPartHeaderValueAtAnyPositionNegative() throws RbvException, PackageException,
                                                              FileNotFoundException {

        HeaderRule rule = new HeaderRule("Test-Part-Header",
                                         "some part value4",
                                         0,
                                         -1,
                                         HeaderMatchMode.EQUALS,
                                         "isMatchPartHeaderValueAtAnyPositionNegative",
                                         true);
        assertFalse(rule.isMatch(metaData));
    }
}
