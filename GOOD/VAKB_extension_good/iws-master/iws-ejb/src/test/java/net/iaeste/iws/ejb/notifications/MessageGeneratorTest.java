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
package net.iaeste.iws.ejb.notifications;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import net.iaeste.iws.common.configuration.Settings;
import net.iaeste.iws.common.exceptions.NotificationException;
import net.iaeste.iws.common.notification.NotificationField;
import net.iaeste.iws.common.notification.NotificationType;
import net.iaeste.iws.ejb.emails.MessageField;
import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.1
 */
public final class MessageGeneratorTest {

    @Test(expected = NotificationException.class)
    public void testGenerateGeneral() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.GENERAL);
    }

    @Test
    public void testGenerateUpdateUsername() {
        final Settings settings = new Settings();
        settings.setBaseUrl("BASE_URL");
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);
        fields.put(NotificationField.CODE, "RESET_CODE");

        final Map<MessageField, String> result = generator.generate(fields, NotificationType.UPDATE_USERNAME);
        assertThat(result.size(), is(2));
        assertThat(result.get(MessageField.SUBJECT), is("IntraWeb Email Address Change Confirmation"));
        assertThat(result.get(MessageField.MESSAGE), containsString("BASE_URL"));
        assertThat(result.get(MessageField.MESSAGE), containsString("RESET_CODE"));
    }

    @Test(expected = NotificationException.class)
    public void testGenerateUsernameUpdated() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.USERNAME_UPDATED);
    }

    @Test
    public void testGenerateActivateNewUser() {
        final Settings settings = new Settings();
        settings.setBaseUrl("BASE_URL");
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);
        fields.put(NotificationField.CODE, "USER_CODE");
        fields.put(NotificationField.EMAIL, "USER_EMAIL");
        fields.put(NotificationField.CLEARTEXT_PASSWORD, "CLEAR_TEXT_PASSWORD");

        final Map<MessageField, String> result = generator.generate(fields, NotificationType.ACTIVATE_NEW_USER);
        assertThat(result.size(), is(2));
        assertThat(result.get(MessageField.SUBJECT), is("Your IAESTE A.s.b.l. IntraWeb Account"));
        assertThat(result.get(MessageField.MESSAGE), containsString("USER_CODE"));
        assertThat(result.get(MessageField.MESSAGE), containsString("USER_EMAIL"));
        assertThat(result.get(MessageField.MESSAGE), containsString("CLEAR_TEXT_PASSWORD"));
    }

    @Test(expected = NotificationException.class)
    public void testGenerateActivateSuspendedUser() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.ACTIVATE_SUSPENDED_USER);
    }

    @Test(expected = NotificationException.class)
    public void testGenerateSuspendActiveUser() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.SUSPEND_ACTIVE_USER);
    }

    @Test(expected = NotificationException.class)
    public void testGenerateNewUser() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.NEW_USER);
    }

    @Test
    public void testGenerateResetPassword() {
        final Settings settings = new Settings();
        settings.setBaseUrl("BASE_URL");
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);
        fields.put(NotificationField.CODE, "RESET_CODE");

        final Map<MessageField, String> result = generator.generate(fields, NotificationType.RESET_PASSWORD);
        assertThat(result.size(), is(2));
        assertThat(result.get(MessageField.SUBJECT), is("IntraWeb Password Reset Request"));
        assertThat(result.get(MessageField.MESSAGE), containsString("BASE_URL"));
        assertThat(result.get(MessageField.MESSAGE), containsString("RESET_CODE"));
    }

    @Test
    public void testGenerateResetSession() {
        final Settings settings = new Settings();
        settings.setBaseUrl("BASE_URL");
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);
        fields.put(NotificationField.CODE, "RESET_CODE");

        final Map<MessageField, String> result = generator.generate(fields, NotificationType.RESET_SESSION);
        assertThat(result.size(), is(2));
        assertThat(result.get(MessageField.SUBJECT), is("IntraWeb Reset Session Request"));
        assertThat(result.get(MessageField.MESSAGE), containsString("BASE_URL"));
        assertThat(result.get(MessageField.MESSAGE), containsString("RESET_CODE"));
    }

    @Test(expected = NotificationException.class)
    public void testGenerateNewGroup() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.NEW_GROUP);
    }

    @Test(expected = NotificationException.class)
    public void testGenerateChangeInGroupMembers() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.CHANGE_IN_GROUP_MEMBERS);
    }

    @Test
    public void testGenerateNewGroupOwner() {
        final Settings settings = new Settings();
        settings.setBaseUrl("BASE_URL");
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);
        fields.put(NotificationField.GROUP_NAME, "GROUP_NAME");

        final Map<MessageField, String> result = generator.generate(fields, NotificationType.NEW_GROUP_OWNER);
        assertThat(result.size(), is(2));
        assertThat(result.get(MessageField.SUBJECT), is("IntraWeb Group Owner Assignment Notification"));
        assertThat(result.get(MessageField.MESSAGE), containsString("GROUP_NAME"));
    }

    @Test(expected = NotificationException.class)
    public void testGenerateProcessEmailAlias() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.PROCESS_EMAIL_ALIAS);
    }

    @Test(expected = NotificationException.class)
    public void testGenerateProcessMailingList() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.PROCESS_MAILING_LIST);
    }

    @Test(expected = NotificationException.class)
    public void testGenerateUserActivated() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.USER_ACTIVATED);
    }

    @Test(expected = NotificationException.class)
    public void testGenerateNewStudent() {
        final Settings settings = new Settings();
        final MessageGenerator generator = new MessageGenerator(settings);
        final Map<NotificationField, String> fields = new EnumMap<>(NotificationField.class);

        generator.generate(fields, NotificationType.NEW_STUDENT);
    }
}
