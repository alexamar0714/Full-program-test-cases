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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.AuthenticationToken;
import net.iaeste.iws.api.dtos.Group;
import net.iaeste.iws.api.dtos.Role;
import net.iaeste.iws.api.dtos.User;
import net.iaeste.iws.api.dtos.UserGroup;
import net.iaeste.iws.api.enums.Action;
import net.iaeste.iws.api.enums.GroupType;
import net.iaeste.iws.api.requests.CreateUserRequest;
import net.iaeste.iws.api.requests.FetchGroupRequest;
import net.iaeste.iws.api.requests.FetchRoleRequest;
import net.iaeste.iws.api.requests.OwnerRequest;
import net.iaeste.iws.api.requests.UserGroupAssignmentRequest;
import net.iaeste.iws.api.responses.CreateUserResponse;
import net.iaeste.iws.api.responses.FetchGroupResponse;
import net.iaeste.iws.api.responses.FetchRoleResponse;
import net.iaeste.iws.api.responses.UserGroupResponse;
import net.iaeste.iws.api.responses.Response;
import org.junit.Test;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class UserGroupTest extends AbstractAdministration {

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp() {
        token = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9bfffef5f6fae9f0dbf2fafee8effeb5fff0">[email protected]</a>", "denmark");
        spy.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown() {
        logout(token);
    }

    @Test
    public void testFetchingRoles() {
        // Brazil has been added as Owner to the Board
        final AuthenticationToken myToken = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="513323302b383d113830342225347f3323">[email protected]</a>", "brazil");
        final String sidGroupId = "80962576-3e38-4858-be0d-57252e7316b1";
        final FetchRoleRequest request = new FetchRoleRequest(sidGroupId);
        final FetchRoleResponse response = administration.fetchRoles(myToken, request);
        assertThat(response.isOk(), is(true));
        assertThat(response.getRoles().isEmpty(), is(false));

        // Always remember to logout
        logout(myToken);
    }

    @Test
    public void testAddingUserToNationalGroup() {
        final CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="80f5f3e5f2c0e9e1e5f3f4e5aee4eb">[email protected]</a>");
        createUserRequest.setFirstname("Firstname");
        createUserRequest.setLastname("Lastname");
        final CreateUserResponse createUserResponse = administration.createUser(token, createUserRequest);
        assertThat(createUserResponse.isOk(), is(true));

        final Group nationalGroup = findNationalGroup(token);
        final FetchRoleRequest fetchRoleRequest = new FetchRoleRequest(nationalGroup.getGroupId());
        final FetchRoleResponse fetchRoleResponse = administration.fetchRoles(token, fetchRoleRequest);

        // Add the user to the National Group
        final UserGroup userGroup = new UserGroup();
        userGroup.setGroup(nationalGroup);
        userGroup.setUser(createUserResponse.getUser());
        userGroup.setRole(fetchRoleResponse.getRoles().get(1));
        final UserGroupAssignmentRequest request = new UserGroupAssignmentRequest();
        request.setUserGroup(userGroup);
        final Response response = administration.processUserGroupAssignment(token, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(true));

        // Delete the user again
        request.setAction(Action.DELETE);
        final Response deleteResponse = administration.processUserGroupAssignment(token, request);
        assertThat(deleteResponse, is(not(nullValue())));
        assertThat(deleteResponse.isOk(), is(true));
    }

    @Test
    public void testChangingOwnershipOfLocalCommittee() {
        final User user = createAndActiveUser(token, "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="51303d213930113830342225347f353a">[email protected]</a>", "Alpha", "Beta");
        final Group group = createGroup(token, GroupType.LOCAL, "LC Copenhagen");

        final OwnerRequest request = new OwnerRequest();
        request.setGroup(group);
        request.setUser(user);
        final Response response = administration.changeGroupOwner(token, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(true));
        final FetchGroupRequest groupRequest = new FetchGroupRequest(group.getGroupId());
        groupRequest.setUsersToFetch(FetchGroupRequest.UserFetchType.ALL);
        final FetchGroupResponse groupResponse = administration.fetchGroup(token, groupRequest);
        assertThat(groupResponse.isOk(), is(true));
        assertThat(groupResponse.getMembers().size(), is(2));
    }

    /**
     * This is a proof of error test for Bug #482. The bug caused a huge
     * problem, since all attempts at changing the NS for a country caused the
     * new NS to have the permissions as either Moderator or Member! The error
     * was noticed since many countries suddenly couldn't access their domestic
     * offers. And the reason - no NS!<br />
     *   When changing an NS, there was two requests invoked, although only one
     * was needed. The first request was to change the NS, and the second was
     * to change the settings. If the Object send contained the wrong RoleId,
     * then the IWS neglected to check this Id, and only verified that you
     * didn't attempt to change the user to Owner. Since the test was lacking,
     * the newly created / updated NS account was downgraded.
     */
    @Test
    public void testChangeNSWithWrongNSRole() {
        // We need to use a different token, since this test will otherwise
        // cause other tests to fail!
        final AuthenticationToken alternativeToken = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fd9b8f9c939e98bd949c988e8998d39b8f">[email protected]</a>", "france");
        final User user = createAndActiveUser(alternativeToken, "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="80efede5e7e1c0e9e1e5f3f4e5aee4eb">[email protected]</a>", "Omega", "Beta");
        final Group group = findNationalGroup(alternativeToken);

        // Change the Owner
        final OwnerRequest request = new OwnerRequest();
        request.setGroup(group);
        request.setUser(user);
        final Response response = administration.changeGroupOwner(alternativeToken, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(true));

        // Fetch the list of assignable Roles
        final FetchRoleRequest roleRequest = new FetchRoleRequest();
        roleRequest.setGroupId(group.getGroupId());
        final FetchRoleResponse roleResponse = administration.fetchRoles(alternativeToken, roleRequest);
        Role member = null;
        for (final Role role : roleResponse.getRoles()) {
            if ("Member".equals(role.getRoleName())) {
                member = role;
            }
        }

        // Now attempt to change the role
        final UserGroup userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);
        userGroup.setRole(member);
        final UserGroupAssignmentRequest userGroupAssignmentRequest = new UserGroupAssignmentRequest();
        userGroupAssignmentRequest.setUserGroup(userGroup);
        final UserGroupResponse assignmentResponse = administration.processUserGroupAssignment(alternativeToken, userGroupAssignmentRequest);

        // Now, we can check that it didn't work
        assertThat(assignmentResponse.isOk(), is(false));
        assertThat(assignmentResponse.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(assignmentResponse.getMessage(), is("It is not permitted to change the current Owner."));

        // Logout from the account
        logout(alternativeToken);
    }

    @Test
    public void testChangingNationalSecretaryToNewMember() {
        final User user = createUser(token, "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1b797e6f7a5b727a7e686f7e357f70">[email protected]</a>", "Beta", "Alpha");
        final Group group = findNationalGroup(token);

        final OwnerRequest request = new OwnerRequest();
        request.setGroup(group);
        request.setUser(user);
        final Response response = administration.changeGroupOwner(token, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(false));
        assertThat(response.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(response.getMessage(), is("Cannot reassign ownership to an inactive person."));
    }

    @Test
    public void testChangingNationalSecretaryToActiveMember() {
        // We need to use a different token, since this test will otherwise
        // cause other tests to fail!
        final AuthenticationToken alternativeToken = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a5cbcad7d2c4dce5ccc4c0d6d1c08bcbca">[email protected]</a>", "norway");
        final User user = createAndActiveUser(alternativeToken, "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5334323e3e32133a32362027367d3738">[email protected]</a>", "Gamma", "Beta");
        final Group group = findNationalGroup(alternativeToken);

        // Change the Owner
        final OwnerRequest request = new OwnerRequest();
        request.setGroup(group);
        request.setUser(user);
        final Response response = administration.changeGroupOwner(alternativeToken, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(true));

        // Ensure that we now have 2 members
        final FetchGroupRequest groupRequest = new FetchGroupRequest(group.getGroupId());
        groupRequest.setUsersToFetch(FetchGroupRequest.UserFetchType.ACTIVE);
        final FetchGroupResponse groupResponse = administration.fetchGroup(alternativeToken, groupRequest);
        assertThat(groupResponse.isOk(), is(true));
        assertThat(groupResponse.getMembers().size(), is(2));

        // And just to verify that we're no longer the owner - we're attempting
        // to change the Ownership again, and this time expecting an
        // Authorization error
        final Response failedResponse = administration.changeGroupOwner(token, request);
        assertThat(failedResponse, is(not(nullValue())));
        assertThat(failedResponse.isOk(), is(false));
        assertThat(failedResponse.getError(), is(IWSErrors.AUTHORIZATION_ERROR));
        assertThat(failedResponse.getMessage().contains("is not permitted to perform the action 'Change Group Owner'."), is(true));
        logout(alternativeToken);
    }

    @Test
    public void testChangingNationalSecretaryToSelf() {
        final FetchGroupRequest groupRequest = new FetchGroupRequest(GroupType.NATIONAL);
        groupRequest.setUsersToFetch(FetchGroupRequest.UserFetchType.ACTIVE);
        final FetchGroupResponse groupResponse = administration.fetchGroup(token, groupRequest);
        final Group group = groupResponse.getGroup();
        final UserGroup user = groupResponse.getMembers().get(0);

        final OwnerRequest request = new OwnerRequest();
        request.setGroup(group);
        request.setUser(user.getUser());
        final Response response = administration.changeGroupOwner(token, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(false));
        assertThat(response.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(response.getMessage(), is("Cannot reassign ownership to the current Owner."));
    }

    @Test
    public void testChangingNationalSecretatyToNonMember() {
        final AuthenticationToken newToken = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1977766b6e78605970787c6a6d7c377776">[email protected]</a>", "norway");
        final User user = createAndActiveUser(newToken, "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4e232b232c2b3c0e272f2b3d3a2b602021">[email protected]</a>", "New", "Member");
        logout(newToken);
        final Group group = findNationalGroup(token);

        final OwnerRequest request = new OwnerRequest();
        request.setGroup(group);
        request.setUser(user);
        final Response response = administration.changeGroupOwner(token, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(false));
        assertThat(response.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(response.getMessage(), is("Cannot reassign National Secretary to a person who is not a member from Denmark."));
    }

    /**
     * The Member Group is where we control access to the system. Deleting Users
     * from a Member Group is thus not allowed, it is the equivalent of deleting
     * the User, which is handled via the Administration#controlUserAccount
     * request.
     */
    @Test
    public void testRemovingUserFromMemberGroup() {
        final User user = createAndActiveUser(token, "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f19c949c939483c3b1989094828594df9f9e">[email protected]</a>", "New", "Member2");
        final FetchGroupRequest fetchGroupRequest = new FetchGroupRequest(GroupType.MEMBER);
        fetchGroupRequest.setUsersToFetch(FetchGroupRequest.UserFetchType.ACTIVE);
        final FetchGroupResponse fetchGroupResponse = administration.fetchGroup(token, fetchGroupRequest);
        UserGroup userGroup = null;
        for (final UserGroup member : fetchGroupResponse.getMembers()) {
            if (member.getUser().getUserId().equals(user.getUserId())) {
                userGroup = member;
            }
        }

        final UserGroupAssignmentRequest request = new UserGroupAssignmentRequest();
        request.setAction(Action.DELETE);
        request.setUserGroup(userGroup);
        final UserGroupResponse response = administration.processUserGroupAssignment(token, request);
        assertThat(response.isOk(), is(false));
        assertThat(response.getError(), is(IWSErrors.WARNING));
        assertThat(response.getMessage(), is("It is not permitted to remove a User from a Member Group with this request. Please delete the user instead."));
    }
}
