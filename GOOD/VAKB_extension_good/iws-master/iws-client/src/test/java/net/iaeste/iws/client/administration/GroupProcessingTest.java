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

import static net.iaeste.iws.common.utils.StringUtils.toLower;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.Group;
import net.iaeste.iws.api.enums.GroupType;
import net.iaeste.iws.api.enums.MonitoringLevel;
import net.iaeste.iws.api.requests.FetchGroupRequest;
import net.iaeste.iws.api.requests.GroupRequest;
import net.iaeste.iws.api.responses.FetchGroupResponse;
import net.iaeste.iws.api.responses.GroupResponse;
import net.iaeste.iws.api.responses.Response;
import org.junit.Test;

/**
 * This test will attempt to verify the creation and updating of our normally
 * allowed Groups, WorkGroups & Local Committees. Other types of Groups are
 * controlled via other mechanisms.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class GroupProcessingTest extends AbstractAdministration {

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp() {
        token = login("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f296979c9f938099b29b9397818697dc9699">[email protected]</a>", "denmark");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown() {
        logout(token);
    }

    // =========================================================================
    // Positive Tests, when creating a subgroup
    // =========================================================================

    @Test
    public void testMonitoringChanges() {
        final Group group = findNationalGroup(token);
        assertThat(group.getMonitoringLevel(), is(not(MonitoringLevel.DETAILED)));

        token.setGroupId(group.getGroupId());
        group.setMonitoringLevel(MonitoringLevel.DETAILED);
        final GroupRequest request1 = new GroupRequest(group);
        final GroupResponse response1 = administration.processGroup(token, request1);
        assertThat(response1.isOk(), is(true));

        final FetchGroupRequest request2 = new FetchGroupRequest(group.getGroupId());
        final FetchGroupResponse response2 = administration.fetchGroup(token, request2);
        assertThat(response2.isOk(), is(true));
        assertThat(response2.getGroup(), is(not(nullValue())));
        assertThat(response2.getGroup().getMonitoringLevel(), is(MonitoringLevel.DETAILED));
    }

    @Test
    public void testCreatingLocalAsSubGroupToMembers() {
        final String groupName = "My Local Committee";
        final String groupDescription = "The Group Description";
        final String fullName = "Denmark." + groupName;
        final String listName = toLower(fullName.replace(' ', '_'));
        final String publicListName = toLower(fullName.replace(' ', '_') + '@' + IWSConstants.PUBLIC_EMAIL_ADDRESS);
        final String privateListName = toLower(fullName.replace(' ', '_') + '@' + IWSConstants.PRIVATE_EMAIL_ADDRESS);

        final GroupResponse result = createGroup(token, GroupType.MEMBER, GroupType.LOCAL, groupName);
        assertThat(result.isOk(), is(true));
        assertThat(result.getGroup(), is(not(nullValue())));
        assertThat(result.getGroup().getListName(), is(listName));
        assertThat(result.getGroup().getFullName(), is(fullName));

        // Okay, created Group - let's try to modify it
        final Group group = result.getGroup();
        // Changes to GroupType should be ignored
        group.setGroupType(GroupType.ADMINISTRATION);
        group.setDescription(groupDescription);

        final GroupRequest request = new GroupRequest(group);
        final GroupResponse response = administration.processGroup(token, request);

        // Now, check that the changes are in
        assertThat(response.isOk(), is(true));
        assertThat(response.getGroup(), is(not(nullValue())));
        assertThat(response.getGroup().getGroupId(), is(group.getGroupId()));
        assertThat(response.getGroup().getDescription(), is(groupDescription));
        assertThat(response.getGroup().getGroupType(), is(GroupType.LOCAL));
        assertThat(response.getGroup().getGroupName(), is(groupName));
        assertThat(response.getGroup().getFullName(), is(fullName));
        assertThat(response.getGroup().hasPrivateList(), is(GroupType.LOCAL.getMayHavePrivateMailinglist()));
        assertThat(response.getGroup().hasPublicList(), is(GroupType.LOCAL.getMayHavePublicMailinglist()));
        assertThat(response.getGroup().getPublicList(), is(publicListName));
        assertThat(response.getGroup().getPrivateList(), is(privateListName));
    }

    @Test
    public void testCreatingWorkGroupAsSubGroupToMembers() {
        final String groupName = "My Work Group";
        final String groupDescription = "My Description";
        final String fullName = "Denmark." + groupName;
        final String listName = toLower(fullName.replace(' ', '_'));
        final String publicListName = toLower(fullName.replace(' ', '_') + '@' + IWSConstants.PUBLIC_EMAIL_ADDRESS);
        final String privateListName = toLower(fullName.replace(' ', '_') + '@' + IWSConstants.PRIVATE_EMAIL_ADDRESS);

        final GroupResponse result = createGroup(token, GroupType.MEMBER, GroupType.WORKGROUP, groupName);
        assertThat(result.isOk(), is(true));
        assertThat(result.getGroup(), is(not(nullValue())));
        assertThat(result.getGroup().getFullName(), is(fullName));
        assertThat(result.getGroup().getListName(), is(listName));

        // Okay, created Group - let's try to modify it
        final Group group = result.getGroup();
        // Changes to GroupType should be ignored
        group.setGroupType(GroupType.ADMINISTRATION);
        group.setDescription(groupDescription);

        final GroupRequest request = new GroupRequest(group);
        final GroupResponse response = administration.processGroup(token, request);

        // Now, check that the changes are in
        assertThat(response.isOk(), is(true));
        assertThat(response.getGroup(), is(not(nullValue())));
        assertThat(response.getGroup().getGroupId(), is(group.getGroupId()));
        assertThat(response.getGroup().getDescription(), is(groupDescription));
        assertThat(response.getGroup().getGroupType(), is(GroupType.WORKGROUP));
        assertThat(response.getGroup().getGroupName(), equalToIgnoringCase(groupName));
        assertThat(response.getGroup().getFullName(), is(fullName));
        assertThat(response.getGroup().getListName(), is(listName));
        assertThat(response.getGroup().getPublicList(), is(publicListName));
        assertThat(response.getGroup().getPrivateList(), is(privateListName));
    }

    @Test
    public void testCreatingWorkGroupAsSubGroupToNational() {
        final String groupName = "My Work Group";
        final String groupDescription = "My Description";

        final GroupResponse result = createGroup(token, GroupType.NATIONAL, GroupType.WORKGROUP, groupName);
        assertThat(result.isOk(), is(true));
        assertThat(result.getGroup(), is(not(nullValue())));

        // Okay, created Group - let's try to modify it
        final Group group = result.getGroup();
        // Changes to GroupType should be ignored
        group.setGroupType(GroupType.ADMINISTRATION);
        group.setDescription(groupDescription);

        final GroupRequest request = new GroupRequest(group);
        final GroupResponse response = administration.processGroup(token, request);

        // Now, check that the changes are in
        assertThat(response.isOk(), is(true));
        assertThat(response.getGroup(), is(not(nullValue())));
        assertThat(response.getGroup().getGroupId(), is(group.getGroupId()));
        assertThat(response.getGroup().getDescription(), is(groupDescription));
        assertThat(response.getGroup().getGroupType(), is(GroupType.WORKGROUP));
        assertThat(response.getGroup().getGroupName(), is(groupName));
        assertThat(result.getGroup().getFullName(), not(nullValue()));
        assertThat(result.getGroup().getListName(), not(nullValue()));
        assertThat(result.getGroup().getListName(), not(startsWith("null")));
    }

    @Test
    public void testCreatingWorkgroupAsSubGroupToLocal() {
        final String localName = "Some Local Committee";
        final String workgroupName = "My Local Work Group";
        final String workgroupDescription = "The Group Description";
        final String workgroupFullName = "Denmark." + localName + '.' + workgroupName;
        final String workgroupPublicListName = toLower(workgroupFullName.replace(' ', '_') + '@' + IWSConstants.PUBLIC_EMAIL_ADDRESS);
        final String workgroupPrivateListName = toLower(workgroupFullName.replace(' ', '_') + '@' + IWSConstants.PRIVATE_EMAIL_ADDRESS);

        final GroupResponse result = createGroup(token, GroupType.MEMBER, GroupType.LOCAL, localName);
        assertThat(result.isOk(), is(true));

        final Group group = new Group();
        group.setGroupName(workgroupName);
        group.setGroupType(GroupType.WORKGROUP);
        group.setDescription(workgroupDescription);
        token.setGroupId(result.getGroup().getGroupId());

        final GroupRequest request = new GroupRequest(group);
        final GroupResponse response = administration.processGroup(token, request);

        assertThat(response.isOk(), is(true));
        assertThat(response.getGroup(), is(not(nullValue())));
        assertThat(response.getGroup().getGroupId(), not(nullValue()));
        assertThat(response.getGroup().getDescription(), is(workgroupDescription));
        assertThat(response.getGroup().getGroupType(), is(GroupType.WORKGROUP));
        assertThat(response.getGroup().getGroupName(), is(workgroupName));
        assertThat(response.getGroup().getPrivateList(), is(workgroupPrivateListName));
        assertThat(response.getGroup().getPublicList(), is(workgroupPublicListName));
        assertThat(response.getGroup().getFullName(), is(workgroupFullName));
    }

    // =========================================================================
    // Negative tests, when creating new subgroups
    // =========================================================================

    @Test
    public void testUpdatingRestrictedGroup() {
        final String newName = "New Name";
        final Group group = findNationalGroup(token);
        assertThat(group.getMonitoringLevel(), is(not(MonitoringLevel.MARKED)));
        group.setGroupName(newName);
        group.setMonitoringLevel(MonitoringLevel.MARKED);
        token.setGroupId(group.getGroupId());
        final GroupRequest request = new GroupRequest(group);

        final GroupResponse response = administration.processGroup(token, request);
        assertThat(response.isOk(), is(true));
        final Group updatedGroup = response.getGroup();
        assertThat(updatedGroup.getGroupName(), is(not(newName)));
        assertThat(updatedGroup.getMonitoringLevel(), is(MonitoringLevel.MARKED));
    }

    @Test
    public void testUpdatingInvalidGroup() {
        final Group group = new Group();

        // Invalid Id
        group.setGroupId("c7b32f81-4f83-48e8-9ffb-9e73255f5e5e");
        group.setGroupName("New Name");
        group.setGroupType(GroupType.LOCAL);
        final GroupRequest request = new GroupRequest(group);

        token.setGroupId(findMemberGroup(token).getGroupId());
        final GroupResponse response = administration.processGroup(token, request);
        assertThat(response, is(not(nullValue())));
        assertThat(response.isOk(), is(false));
        assertThat(response.getError(), is(IWSErrors.AUTHORIZATION_ERROR));
        assertThat(response.getMessage().contains("is not permitted to perform the action 'Process Group'."), is(true));
    }

    @Test
    public void testCreatingAdministrationAsSubGroupToMembers() {
        final Response result = createGroup(token, GroupType.MEMBER, GroupType.ADMINISTRATION, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.ADMINISTRATION.getDescription() + "'."));
    }

    @Test
    public void testCreatingPrivateAsSubGroupToMembers() {
        final Response result = createGroup(token, GroupType.MEMBER, GroupType.PRIVATE, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.PRIVATE.getDescription() + "'."));
    }

    @Test
    public void testCreatingMemberAsSubGroupToMembers() {
        final Response result = createGroup(token, GroupType.MEMBER, GroupType.MEMBER, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.MEMBER.getDescription() + "'."));
    }

    @Test
    public void testCreatingInternationalAsSubGroupToMembers() {
        final Response result = createGroup(token, GroupType.MEMBER, GroupType.INTERNATIONAL, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.INTERNATIONAL.getDescription() + "'."));
    }

    @Test
    public void testCreatingNationalAsSubGroupToMembers() {
        final Response result = createGroup(token, GroupType.MEMBER, GroupType.NATIONAL, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.NATIONAL.getDescription() + "'."));
    }

    @Test
    public void testCreatingStudentsAsSubGroupToMembers() {
        final Response result = createGroup(token, GroupType.MEMBER, GroupType.STUDENT, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.STUDENT.getDescription() + "'."));
    }

    @Test
    public void testCreatingAdministrationAsSubGroupToNational() {
        final Response result = createGroup(token, GroupType.NATIONAL, GroupType.ADMINISTRATION, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.ADMINISTRATION.getDescription() + "'."));
    }

    @Test
    public void testCreatingPrivateAsSubGroupToNational() {
        final Response result = createGroup(token, GroupType.NATIONAL, GroupType.PRIVATE, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.PRIVATE.getDescription() + "'."));
    }

    @Test
    public void testCreatingMemberAsSubGroupToNational() {
        final Response result = createGroup(token, GroupType.NATIONAL, GroupType.MEMBER, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.MEMBER.getDescription() + "'."));
    }

    @Test
    public void testCreatingInternationalAsSubGroupToNational() {
        final Response result = createGroup(token, GroupType.NATIONAL, GroupType.INTERNATIONAL, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.INTERNATIONAL.getDescription() + "'."));
    }

    @Test
    public void testCreatingNationalAsSubGroupToNational() {
        final Response result = createGroup(token, GroupType.NATIONAL, GroupType.NATIONAL, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.NATIONAL.getDescription() + "'."));
    }

    @Test
    public void testCreatingLocalAsSubGroupToNational() {
        final Response result = createGroup(token, GroupType.NATIONAL, GroupType.LOCAL, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.LOCAL.getDescription() + "'."));
    }

    @Test
    public void testCreatingStudentsAsSubGroupToNational() {
        final Response result = createGroup(token, GroupType.NATIONAL, GroupType.STUDENT, "My Group");
        assertThat(result.isOk(), is(false));
        assertThat(result.getError(), is(IWSErrors.NOT_PERMITTED));
        assertThat(result.getMessage(), is("Not allowed to create a sub-group of type '" + GroupType.STUDENT.getDescription() + "'."));
    }

    @Test
    public void testCreatingDuplicateGroup() {
        final String duplicateName = "Duplicate Name";
        final GroupResponse result = createGroup(token, GroupType.MEMBER, GroupType.WORKGROUP, duplicateName);
        assertThat(result.isOk(), is(true));

        final GroupResponse failed = createGroup(token, GroupType.MEMBER, GroupType.WORKGROUP, duplicateName);
        assertThat(failed.isOk(), is(false));
    }
}
