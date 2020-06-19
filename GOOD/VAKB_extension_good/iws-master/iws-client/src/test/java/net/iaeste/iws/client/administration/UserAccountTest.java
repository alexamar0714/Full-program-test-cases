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
package net.iaeste.iws.client.administration;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import net.iaeste.iws.api.Students;
import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.Address;
import net.iaeste.iws.api.dtos.AuthenticationToken;
import net.iaeste.iws.api.dtos.Group;
import net.iaeste.iws.api.dtos.Person;
import net.iaeste.iws.api.dtos.User;
import net.iaeste.iws.api.dtos.exchange.Student;
import net.iaeste.iws.api.enums.Gender;
import net.iaeste.iws.api.enums.UserStatus;
import net.iaeste.iws.api.requests.AccountNameRequest;
import net.iaeste.iws.api.requests.CreateUserRequest;
import net.iaeste.iws.api.requests.FetchGroupRequest;
import net.iaeste.iws.api.requests.FetchRoleRequest;
import net.iaeste.iws.api.requests.FetchUserRequest;
import net.iaeste.iws.api.requests.SearchUserRequest;
import net.iaeste.iws.api.requests.UserRequest;
import net.iaeste.iws.api.requests.student.FetchStudentsRequest;
import net.iaeste.iws.api.responses.CreateUserResponse;
import net.iaeste.iws.api.responses.Response;
import net.iaeste.iws.api.responses.FetchGroupResponse;
import net.iaeste.iws.api.responses.FetchPermissionResponse;
import net.iaeste.iws.api.responses.FetchRoleResponse;
import net.iaeste.iws.api.responses.FetchUserResponse;
import net.iaeste.iws.api.responses.SearchUserResponse;
import net.iaeste.iws.api.responses.student.FetchStudentsResponse;
import net.iaeste.iws.api.util.Date;
import net.iaeste.iws.client.StudentClient;
import net.iaeste.iws.common.notification.NotificationField;
import net.iaeste.iws.common.notification.NotificationType;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class UserAccountTest extends AbstractAdministration {

    @Override
    public void setUp() {
        token = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="91f0e4e2e5e3f8f0d1f8f0f4e2e5f4bff0e5">[email protected]</a>", "austria");
        spy.clear();
    }

    @Override
    public void tearDown() {
        logout(token);
    }

    @Test
    public void testUsernameChange() {
        // First, let's create a new account for Harvey Rabbit, which we can
        // use to test with.
        final String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c1a9a0b3b7a4b881a8a0a4b2b5a4efb4b2">[email protected]</a>";
        // There was a bug related to the Changing of Username, as it failed if
        // the password contained upper/lower case letters. Since IWS followed
        // the rule from IW3 and only looked at Passwords case insensitive.
        final String password = "Harvey's password which is SUPER secret.";
        final CreateUserRequest createRequest = new CreateUserRequest(username, password, "Harvey", "Rabbit");
        final CreateUserResponse createResponse = administration.createUser(token, createRequest);
        assertThat(createResponse.isOk(), is(true));

        // To ensure that we can use the account, we have to activate it. Once
        // it is activated, we can move on to the actual test.
        final String activationCode = readCode(NotificationType.ACTIVATE_NEW_USER);
        final Response activateResponse = administration.activateUser(activationCode);
        assertThat(activateResponse.isOk(), is(true));

        // Now we have a fresh new account which is active. So we can now try to
        // change the Username. To do this, we have to log in as the user, since
        // only a user may change his/her own username.
        final String newUsername = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="60120102020914200901051314054e1513">[email protected]</a>";
        final AuthenticationToken myToken = login(username, password);
        // We're building the request with the user itself as User Object
        final UserRequest updateRequest = new UserRequest(createResponse.getUser());
        // We're setting a new username here, which we can use.
        updateRequest.setNewUsername(newUsername);
        // To update the username, we need to provide some credentials,
        // otherwise the IWS will reject the request. This is needed to ensure
        // that nobody is attempting to hijack an active account.
        updateRequest.setPassword(password);
        // Let's just clear the Spy before we're using it :-)
        final Response updateResponse = administration.controlUserAccount(myToken, updateRequest);
        assertThat(updateResponse.isOk(), is(true));
        // Changing username must work without being logged in, well actually
        // it doesn't matter. As it doesn't use the current Session.
        logout(myToken);
        // Now, we can read out the update Code from the Notifications, which
        // is a cheap way of reading the value from the e-mail that is send.
        final String updateCode = readCode(NotificationType.UPDATE_USERNAME);
        final Response resetResponse = administration.updateUsername(updateCode);
        assertThat(resetResponse.isOk(), is(true));

        // Final part of the test, login with the new username, and ensure that
        // the UserId we're getting is the same as the previous Id.
        final AuthenticationToken newToken = login(newUsername, password);
        assertThat(newToken, is(not(nullValue())));
        final FetchPermissionResponse permissionResponse = access.fetchPermissions(newToken);
        assertThat(permissionResponse.isOk(), is(true));
        assertThat(permissionResponse.getAuthorizations().get(0).getUserGroup().getUser().getUserId(), is(createResponse.getUser().getUserId()));
        logout(newToken);
        // And done - Long test, but worth it :-)
    }

    @Test
    public void testAccountNameChange() {
        token.setGroupId(findMemberGroup(token).getGroupId());
        final String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b9d8d5dfd6d7caf9d0d8dccacddc97cadc">[email protected]</a>";
        final CreateUserRequest createRequest = new CreateUserRequest(username, "Alfons", "Åberg");
        final CreateUserResponse createResponse = administration.createUser(token, createRequest);
        final AccountNameRequest request = new AccountNameRequest();
        request.setUser(createResponse.getUser());
        request.setLastname("Aaberg");

        // To rename someone, we need to be Administrator, for the test,
        // Australia acts as Administrator, so we're using this Account for it
        final AuthenticationToken adminToken = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="88e9fdfbfcfae9e4e1e9c8e1e9edfbfceda6e9fd">[email protected]</a>", "australia");
        final Response response = administration.changeAccountName(adminToken, request);
        assertThat(response.isOk(), is(true));
        logout(adminToken);

        // Now we can verify that the name was changed
        final FetchUserRequest fetchRequest = new FetchUserRequest();
        fetchRequest.setUserId(createResponse.getUser().getUserId());
        final FetchUserResponse fetchResponse = administration.fetchUser(token, fetchRequest);
        assertThat(fetchResponse.isOk(), is(true));
        assertThat(fetchResponse.getUser().getLastname(), is("Aaberg"));
    }

    /**
     * From the Production logs on 2016-03-05, there is a problem with a user
     * who have tried to change the username of an existing account to the same
     * as is used for another account.
     */
    @Ignore("Test is not done yet")
    @Test
    public void testChangingUsernameToExisting() {
        final UserRequest request = new UserRequest();
        // We're currently logged in as Austria, so we're trying to change the
        // username to Germany.
        request.setNewUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="630406110e020d1a230a02061017064d0706">[email protected]</a>");
        final Response response = administration.controlUserAccount(token, request);
        assertThat(response.getError(), is(IWSErrors.FATAL));
    }

    /**
     * Test for the Trac Bug report #452 - to reproduce:
     * <ol>
     * <li>login as austria</li>
     * <li>create a new user</li>
     * <li>activate the user</li>
     * <li>delete the user</li>
     * </ol>
     */
    @Test
    public void testDeletingActiveUser() {
        // 1. Login as Austria
        //      - The test is using Austria per default
        // 2. Create a new User
        final String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3a5755405b484e7a535b5f494e5f145b4e">[email protected]</a>";
        final CreateUserRequest createRequest = new CreateUserRequest(username, "Wolfgang", "Amadeus");
        final CreateUserResponse createResponse = administration.createUser(token, createRequest);
        assertThat(createResponse.isOk(), is(true));

        // 3. Activate the NEW User
        final NotificationType type = NotificationType.ACTIVATE_NEW_USER;
        final NotificationField field = NotificationField.CODE;
        final String activationCode = spy.getNext(type).getFields().get(field);
        final Response activateResponse = administration.activateUser(activationCode);
        assertThat(activateResponse.isOk(), is(true));

        // 4. Delete the ACTIVE User
        final UserRequest deleteRequest = new UserRequest();
        deleteRequest.setUser(createResponse.getUser());
        deleteRequest.setNewStatus(UserStatus.DELETED);
        final Response deleteResponse = administration.controlUserAccount(token, deleteRequest);
        assertThat(deleteResponse.isOk(), is(true));
    }

    @Test
    public void testDeletingNewUser() {
        final String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="620003010a220b03071116074c0316">[email protected]</a>";
        final CreateUserRequest createRequest = new CreateUserRequest(username, "Johann", "Sebastian");
        final CreateUserResponse createResponse = administration.createUser(token, createRequest);
        assertThat(createResponse.isOk(), is(true));

        final UserRequest deleteRequest = new UserRequest();
        deleteRequest.setUser(createResponse.getUser());
        deleteRequest.setNewStatus(UserStatus.DELETED);
        final Response deleteResponse = administration.controlUserAccount(token, deleteRequest);
        assertThat(deleteResponse.isOk(), is(true));

        // Okay, now we're using the activation link to activate the Account
        final NotificationType type = NotificationType.ACTIVATE_NEW_USER;
        final NotificationField field = NotificationField.CODE;
        final String activationCode = spy.getNext(type).getFields().get(field);
        final Response activateResponse = administration.activateUser(activationCode);
        assertThat(activateResponse.isOk(), is(false));
        assertThat(activateResponse.getError(), is(IWSErrors.AUTHENTICATION_ERROR));
        assertThat(activateResponse.getMessage(), is("No account for this user was found."));
    }

    /**
     * Test for the Trac Bug report #442.
     */
    @Test
    public void testCreateUserWithExistingUsername() {
        final String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="234e4a404b42464f0d534a4048464f4142564651634a42465057460d4257">[email protected]</a>";
        final CreateUserRequest request1 = new CreateUserRequest(username, "Michael", "Pickelbauer");
        final CreateUserRequest request2 = new CreateUserRequest(username, "Hugo", "Mayer");

        // Now let's create the two user accounts
        final CreateUserResponse response1 = administration.createUser(token, request1);
        final CreateUserResponse response2 = administration.createUser(token, request2);

        // The first request should work like a charm
        assertThat(response1, is(not(nullValue())));
        assertThat(response1.isOk(), is(true));

        // The second request should fail, as we already have a user with this
        // username in the system
        assertThat(response2, is(not(nullValue())));
        assertThat(response2.isOk(), is(false));
        assertThat(response2.getError(), is(IWSErrors.USER_ACCOUNT_EXISTS));
        assertThat(response2.getMessage(), is("An account for the user with username " + username + " already exists."));
    }

    @Test
    public void testCreateAccountWithPassword() {
        // Create the new User Request Object
        final CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="76171a061e173611171b1b1758181302">[email protected]</a>");
        createUserRequest.setPassword("beta");
        createUserRequest.setFirstname("Alpha");
        createUserRequest.setLastname("Gamma");

        // Now, perform the actual test - create the Account, and verify that
        // the response is ok, and that a Notification was sent
        final CreateUserResponse result = administration.createUser(token, createUserRequest);
        assertThat(result.isOk(), is(true));
        assertThat(result.getUser(), is(not(nullValue())));
        assertThat(result.getUser().getUserId(), is(not(nullValue())));
        // Creating a new User should generate an Activate User notification
        final NotificationType type = NotificationType.ACTIVATE_NEW_USER;
        assertThat(spy.size(type), is(1));
        final String activationCode = spy.getNext(type).getFields().get(NotificationField.CODE);
        assertThat(activationCode, is(not(nullValue())));
    }

    @Test
    public void testCreateAccountWithoutPassword() {
        // Create the new User Request Object
        final CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="7c1e19081d3c1b1d11111d52121908">[email protected]</a>");
        createUserRequest.setFirstname("Beta");
        createUserRequest.setLastname("Gamma");

        // Now, perform the actual test - create the Account, and verify that
        // the response is ok, and that a Notification was sent
        final Response result = administration.createUser(token, createUserRequest);
        assertThat(result.isOk(), is(true));
        // Creating a new User should generate an Activate User notification
        final NotificationType type = NotificationType.ACTIVATE_NEW_USER;
        assertThat(spy.size(type), is(1));
        final String activationCode = spy.getNext(type).getFields().get(NotificationField.CODE);
        assertThat(activationCode, is(not(nullValue())));

        // Check that the user is in the list of members
        final String memberGroupId = findMemberGroup(token).getGroupId();
        token.setGroupId(memberGroupId);
        final FetchGroupRequest groupRequest = new FetchGroupRequest(memberGroupId);
        final FetchGroupResponse groupResponse = administration.fetchGroup(token, groupRequest);
        assertThat(groupResponse, is(not(nullValue())));
    }

    /**
     * Testing updating user actually updates the fields. See Trac #438, #524.
     */
    @Test
    public void testUpdatePerson() {
        final AuthenticationToken myToken = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3f595651535e515b7f565e5a4c4b5a115956">[email protected]</a>", "finland");
        final Group memberGroup = findMemberGroup(myToken);
        final FetchGroupRequest groupRequest = new FetchGroupRequest(memberGroup.getGroupId());
        groupRequest.setUsersToFetch(FetchGroupRequest.UserFetchType.ACTIVE);
        final FetchGroupResponse groupResponse1 = administration.fetchGroup(myToken, groupRequest);
        assertThat(groupResponse1.isOk(), is(true));
        assertThat(groupResponse1.getMembers().size(), is(1));

        // Now, let's update the Object, and send it into the IWS
        final User myself = groupResponse1.getMembers().get(0).getUser();
        final Person person = myself.getPerson();
        final Address address = new Address();
        address.setStreet1("Mainstreet 1");
        address.setPostalCode("12345");
        address.setCity("Cooltown");
        address.setState("Coolstate");
        person.setAddress(address);
        person.setFax("My fax");
        person.setBirthday(new Date("01-JAN-1980"));
        person.setMobile("+1 1234567890");
        person.setGender(Gender.UNKNOWN);
        person.setPhone("+1 0987654321");
        person.setUnderstoodPrivacySettings(true);
        person.setAcceptNewsletters(false);
        myself.setPerson(person);
        final UserRequest updateRequest = new UserRequest();
        updateRequest.setUser(myself);
        final Response updateResult = administration.controlUserAccount(myToken, updateRequest);
        assertThat(updateResult.isOk(), is(true));

        // Let's find the account again, and verify that the updates were applied
        final FetchUserRequest userRequest = new FetchUserRequest();
        userRequest.setUserId(myself.getUserId());
        final FetchUserResponse userResponse = administration.fetchUser(myToken, userRequest);
        assertThat(userResponse.isOk(), is(true));
        final User myUpdate = userResponse.getUser();
        final Person updatedPerson = myUpdate.getPerson();
        assertThat(updatedPerson.getAlternateEmail(), is(person.getAlternateEmail()));
        assertThat(updatedPerson.getBirthday(), is(person.getBirthday()));
        assertThat(updatedPerson.getFax(), is(person.getFax()));
        assertThat(updatedPerson.getGender(), is(person.getGender()));
        assertThat(updatedPerson.getMobile(), is(person.getMobile()));
        assertThat(updatedPerson.getPhone(), is(person.getPhone()));
        assertThat(updatedPerson.getUnderstoodPrivacySettings(), is(person.getUnderstoodPrivacySettings()));
        assertThat(updatedPerson.getAcceptNewsletters(), is(person.getAcceptNewsletters()));

        final Address updatedAddress = updatedPerson.getAddress();
        assertThat(updatedAddress.getStreet1(), is(address.getStreet1()));
        assertThat(updatedAddress.getStreet2(), is(address.getStreet2()));
        assertThat(updatedAddress.getPostalCode(), is(address.getPostalCode()));
        assertThat(updatedAddress.getCity(), is(address.getCity()));
        assertThat(updatedAddress.getState(), is(address.getState()));

        // Wrapup... logout
        logout(myToken);
    }

    /**
     * The test attempts to run through the different steps in the user creation
     * process, and verify that the new Student account has the expected
     * permissions. This is the steps being tested:
     * <ol>
     * <li><b>Success</b>: Create new Student Account</li>
     * <li><b>Failure</b>: Attempt to login before activation</li>
     * <li><b>Success</b>: Activate Student Account</li>
     * <li><b>Success</b>: Login with newly created & activated Student Account</li>
     * <li><b>Success</b>: Read Permissions for Student, verify Apply for Open Offers</li>
     * <li><b>Success</b>: Logout from Student Account</li>
     * </ol>
     */
    @Test
    public void testCreateStudentAccount() {
        // For this test, we also need the Access Client
        //final AccessClient accessClient = new AccessClient();

        // Create the new User Request Object
        final String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1b686f6e7f7e756f5b7c7a76767a35757e6f">[email protected]</a>";
        final String password = "myPassword";
        final CreateUserRequest createUserRequest = new CreateUserRequest(username, password, "Student", "Graduate");
        createUserRequest.setStudentAccount(true);

        final Students students = new StudentClient();
        final FetchStudentsRequest fetchStudentsRequest = new FetchStudentsRequest();
        final FetchStudentsResponse beforeStudentsResponse = students.fetchStudents(token, fetchStudentsRequest);

        // Now, perform the actual test - create the Account, and verify that
        // the response is ok, and that a Notification was sent
        final Response result = administration.createUser(token, createUserRequest);
        assertThat(result.isOk(), is(true));
        assertThat(spy.size(), is(0));

        //TODO Pavel 2014-04-15: students are not supposed to receive activation email now so the following test should not work
        //                       since no notification is generated. Once the emails are sent, following lines have to be uncommented
//        assertThat(spy.size(), is(1));
//        final String activationCode = spy.getNext().getFields().get(NotificationField.CODE);
//        assertThat(activationCode, is(not(nullValue())));

        // Attempt to login using the new User Account. It should not yet work,
        // since the account is not activated
//        final AuthenticationRequest request = new AuthenticationRequest(username, password);
//        final AuthenticationResponse response1 = accessClient.generateSession(request);
//        assertThat(response1.isOk(), is(false));
//        assertThat(response1.getError(), is(IWSErrors.AUTHENTICATION_ERROR));

        // Verify that the Students exists
        final FetchStudentsResponse afterFetchStudentsResponse = students.fetchStudents(token, fetchStudentsRequest);
        assertThat(afterFetchStudentsResponse.isOk(), is(true));
        assertThat(afterFetchStudentsResponse.getStudents().size(), is(beforeStudentsResponse.getStudents().size() + 1));

        // Activate the Account
//        final Fallible acticationResult = administration.activateUser(activationCode);
//        assertThat(acticationResult.isOk(), is(true));

        // Now, attempt to login again
//        final AuthenticationResponse response2 = accessClient.generateSession(request);
//        assertThat(response2.isOk(), is(true));
//        assertThat(response2.getToken(), is(not(nullValue())));

        //// Now, read the Permissions that the student has, basically, there is
        //// only 1 permission - which is applying for Open Offers
        //final FetchPermissionResponse permissionResponse = accessClient.fetchPermissions(response2.getToken());
        //assertThat(permissionResponse.isOk(), is(true));
        //// The following fails, since the order of the UserGroup Object in the
        //// Authorization Object is undefined, for this reason, the code is
        //// commented out
        //assertThat(permissionResponse.getAuthorizations().get(0).getUserGroup().getRole().getPermissions().contains(Permission.APPLY_FOR_OPEN_OFFER), is(true));

        // Deprecate the Students Session, the test is over :-)
//        final Fallible deprecateSessionResult = accessClient.deprecateSession(response2.getToken());
//        assertThat(deprecateSessionResult.isOk(), is(true));
    }

    /**
     * This test is a verification of the bugfix for Trac bug #704, concerning
     * deletion of Student Accounts. Although not explicitly listed, Accounts
     * are only completely deleted, if the User in question is in status new.
     */
    @Test
    public void testCreateAndDeleteStudentAccount() {
        // Create the new User Request Object
        final String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="94e7e0e1f0f1fae0d4f2f8fde4f2f8fbe4bafaf1e0">[email protected]</a>";
        final String password = "myPassword";
        final CreateUserRequest createUserRequest = new CreateUserRequest(username, password, "Student", "Graduate");
        createUserRequest.setStudentAccount(true);

        final Students students = new StudentClient();
        final FetchStudentsRequest fetchStudentsRequest = new FetchStudentsRequest();
        final FetchStudentsResponse beforeStudentsResponse = students.fetchStudents(token, fetchStudentsRequest);

        // Now, perform the actual test - create the Account, and verify that
        // the response is ok, and that a Notification was sent
        final CreateUserResponse created = administration.createUser(token, createUserRequest);
        assertThat(created.isOk(), is(true));

        // Verify that the Students exists
        final FetchStudentsResponse afterFetchStudentsResponse = students.fetchStudents(token, fetchStudentsRequest);
        assertThat(afterFetchStudentsResponse.isOk(), is(true));
        assertThat(afterFetchStudentsResponse.getStudents().size(), is(beforeStudentsResponse.getStudents().size() + 1));

        // Delete the Student Account
        final UserRequest deleteRequest = new UserRequest();
        deleteRequest.setUser(created.getUser());
        deleteRequest.setNewStatus(UserStatus.DELETED);
        final Response deleteResult = administration.controlUserAccount(token, deleteRequest);
        assertThat(deleteResult.isOk(), is(true));

        // Verify that the Student is deleted
        final FetchStudentsResponse deleteFetchStudentsResponse = students.fetchStudents(token, fetchStudentsRequest);
        assertThat(deleteFetchStudentsResponse.isOk(), is(true));
        assertThat(deleteFetchStudentsResponse.getStudents().size(), is(beforeStudentsResponse.getStudents().size()));
    }

    /**
     * This test verifies that the createStudent functionality (Trac #773) is
     * working correctly.
     */
    @Test
    public void testStudentsCreateStudentAccount() {
        // If the normal token is used for this test - then it causes problems
        // for other tests. If the student account is added via the admin
        // method, then no problem occurs, but via the student method... no so
        // much luck!
        final AuthenticationToken myToken = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6e1d1e0f07002e070f0b1d1a0b400b1d">[email protected]</a>", "spain");
        final Students students = new StudentClient();
        // Create the new User Request Object
        final String username = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="067572736263687246606a6976606a6f7628686372">[email protected]</a>";
        final String password = "myPassword";
        final CreateUserRequest createUserRequest = new CreateUserRequest(username, password, "Student", "Graduate");

        final FetchStudentsRequest fetchStudentsRequest = new FetchStudentsRequest();
        final FetchStudentsResponse beforeStudentsResponse = students.fetchStudents(myToken, fetchStudentsRequest);

        // Now, perform the actual test - create the Account, and verify that
        // the response is ok, and that a Notification was sent
        final CreateUserResponse created = students.createStudent(myToken, createUserRequest);
        assertThat(created.isOk(), is(true));

        // Verify that the Students exists
        final FetchStudentsResponse afterFetchStudentsResponse = students.fetchStudents(myToken, fetchStudentsRequest);
        assertThat(afterFetchStudentsResponse.isOk(), is(true));
        assertThat(afterFetchStudentsResponse.getStudents().size(), is(beforeStudentsResponse.getStudents().size() + 1));
        logout(myToken);
    }

    @Test
    public void testCreateAndListStudentAccounts() {
        final Students students = new StudentClient();

        // create some student users
        final CreateUserRequest createUserRequest1 = new CreateUserRequest("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a3d0939392e3d6cdcad5c6d1d0cad7da8dc6c7d6">[email protected]</a>", "password1", "Student1", "Graduate1");
        createUserRequest1.setStudentAccount(true);

        final CreateUserRequest createUserRequest2 = new CreateUserRequest("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d0a3e0e0e290a5beb9a6b5a2a3b9a4a9feb5b4a5">[email protected]</a>", "password2", "Student2", "Graduate2");
        createUserRequest2.setStudentAccount(true);

        final CreateUserRequest createUserRequest3 = new CreateUserRequest("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="512261616211243f38273423223825287f343524">[email protected]</a>", "password3", "Student3", "Graduate3");
        createUserRequest3.setStudentAccount(true);

        final CreateUserResponse createUserResponse1 = administration.createUser(token, createUserRequest1);
        assertThat(createUserResponse1.isOk(), is(true));

        final CreateUserResponse createUserResponse2 = administration.createUser(token, createUserRequest2);
        assertThat(createUserResponse2.isOk(), is(true));

        final CreateUserResponse createUserResponse3 = administration.createUser(token, createUserRequest3);
        assertThat(createUserResponse3.isOk(), is(true));

        // fetch the students group
        final FetchStudentsRequest studentsRequest = new FetchStudentsRequest();
        final FetchStudentsResponse studentsResponse = students.fetchStudents(token, studentsRequest);
        assertThat(studentsResponse, is(not(nullValue())));
        assertThat(studentsResponse.isOk(), is(true));

        assertThat(studentsResponse.getStudents().size(), is(greaterThanOrEqualTo(3)));

        // Extract the users from the group
        final List<Student> foundStudents = studentsResponse.getStudents();
        final List<User> users = new ArrayList<>(foundStudents.size());
        for (final Student student : foundStudents) {
            users.add(student.getUser());
        }

        assertThat(users.contains(createUserResponse1.getUser()), is(true));
        assertThat(users.contains(createUserResponse2.getUser()), is(true));
        assertThat(users.contains(createUserResponse3.getUser()), is(true));
    }

    /**
     * Testing that the generated Alias is correctly set to the name plus an
     * increasing number, of multiple people with the same name are
     * created.<br />
     * Note, the test is using pure lowercase variants, since the IWS will
     * internally convert all usernames and aliases to lowercase.
     */
    @Test
    public void createDuplicateAccount() {
        final String username1 = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="14637d78787d757954677c757f716764717566713a777b79">[email protected]</a>";
        final String username2 = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b0c7d9dcdcd9d1ddf0c3d8d1dbd5c3c0d5d1c2d59eded5c4">[email protected]</a>";
        final String username3 = "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2f58464343464e426f5c474e444a5c5f4a4e5d4a01405d48">[email protected]</a>";
        final String firstname = "william";
        final String lastname = "shakespeare";
        final String address = "@iaeste.org";

        final CreateUserRequest request1 = new CreateUserRequest(username1, firstname, lastname);
        final CreateUserResponse response1 = administration.createUser(token, request1);
        assertThat(response1, is(not(nullValue())));
        assertThat(response1.isOk(), is(true));
        assertThat(response1.getUser(), is(not(nullValue())));
        assertThat(response1.getUser().getAlias(), is(firstname + '.' + lastname + address));

        final CreateUserRequest request2 = new CreateUserRequest(username2, firstname, lastname);
        final CreateUserResponse response2 = administration.createUser(token, request2);
        assertThat(response2, is(not(nullValue())));
        assertThat(response2.isOk(), is(true));
        assertThat(response2.getUser(), is(not(nullValue())));
        assertThat(response2.getUser().getAlias(), is(firstname + '.' + lastname + 2 + address));

        final CreateUserRequest request3 = new CreateUserRequest(username3, firstname, lastname);
        final CreateUserResponse response3 = administration.createUser(token, request3);
        assertThat(response3, is(not(nullValue())));
        assertThat(response3.isOk(), is(true));
        assertThat(response3.getUser(), is(not(nullValue())));
        assertThat(response3.getUser().getAlias(), is(firstname + '.' + lastname + 3 + address));
    }

    @Test
    public void deleteNewAccount() {
        // Create the new User Request Object
        final CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="15727478787455777061743b7b7061">[email protected]</a>");
        createUserRequest.setFirstname("Gamma");
        createUserRequest.setLastname("Beta");

        // First, we create the User account that we wish to delete
        final CreateUserResponse response = administration.createUser(token, createUserRequest);
        assertThat(response.isOk(), is(true));

        // Now, try to delete the account
        final UserRequest request = new UserRequest();
        request.setUser(response.getUser());
        request.setNewStatus(UserStatus.DELETED);
        final Response deletedUserResponse = administration.controlUserAccount(token, request);

        assertThat(deletedUserResponse, is(not(nullValue())));
        assertThat(deletedUserResponse.isOk(), is(true));
    }

    @Test
    public void testSearchUsersByName() {
        // The token requires that we set a GroupId, since the Search Request
        // is possible for many types of Groups
        token.setGroupId(findMemberGroup(token).getGroupId());

        // Setting a partial name to search for. The search will look at both
        // first and last names which contain this as part of their name. In
        // this case, we're searching for "man", which will yield the following:
        //  - Germany
        //  - Oman
        //  - Romania
        final SearchUserRequest request = new SearchUserRequest();
        request.setName("man");

        // Now, invoke the search for users with a partial name given
        final SearchUserResponse response = administration.searchUsers(token, request);

        // And verify the result
        assertThat(response.isOk(), is(true));
        assertThat(response.getUsers().size(), is(3));
    }

    @Test
    public void testSearchUsersByGroupAndName() {
        final Group group = findMemberGroup(token);
        // The token requires that we set a GroupId, since the Search Request
        // is possible for many types of Groups
        token.setGroupId(group.getGroupId());

        // If we're only interested in finding a person to add to a Subgroup in
        // a country, the search can be limited to a specific Member Group.
        // However, the name must always be present.
        final SearchUserRequest request = new SearchUserRequest(group, "NS");

        // Now, invoke the search for users with a partial name given
        final SearchUserResponse response = administration.searchUsers(token, request);

        // And verify the result
        assertThat(response.isOk(), is(true));
        assertThat(response.getUsers().size(), is(1));
    }

    @Test
    public void testFetchRoles() {
        // Uses the National Group for Austria to get the list of Groups. As
        // Member Groups are not allowed to fetch the information, controlling
        // of accounts in Members is done together with handling of National
        // Members. The main reason for this, is to avoid that someone may
        // perform actions on the National Group, but invoking their Membership
        // privileges without having a National Group membership.
        final FetchRoleRequest request = new FetchRoleRequest(findNationalGroup(token).getGroupId());
        final FetchRoleResponse response = administration.fetchRoles(token, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(true));
        // There should be a total of 4 roles for this Group
        assertThat(response.getRoles().size(), is(4));
    }
}
