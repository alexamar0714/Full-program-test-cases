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
package net.iaeste.iws.api.dtos.exchange;

import static net.iaeste.iws.api.util.Immutable.immutableList;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.dtos.Group;
import net.iaeste.iws.api.util.Verifications;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>A Publishing Group, is a helping tool for the Exchange process, so it is
 * possible to save a selection of countries to make an exchange with. Examples
 * of usage, EU, Latin America, French Speaking countries, etc.</p>
 *
 * <p>The Group consists of a name and the selection of countries. And it is
 * important to note, that a Publishing Group is only available for the country
 * of origin, meaning that no other Committees than the owning Committee may
 * see this Publishing Group.</p>
 *
 * @author  Sondre Naustdal / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publishingGroup", propOrder = { "publishingGroupId", "name", "groups" })
public final class PublishingGroup extends Verifications {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    private static final int MAX_NAME_LENGTH = 50;

    /**
     * The Id of the Publishing Group. If null, then a new will be created.
     */
    @XmlElement(required = true, nillable = true)
    private String publishingGroupId = null;

    /**
     * The name of the Publishing Group, the name must be non-null and unique
     * for the Committee, and will not be shared or shown to other Committees.
     */
    @XmlElement(required = true)
    private String name = null;

    /** The List of Committees, to make up this Publishing Group. */
    @XmlElement(required = true)
    private List<Group> groups = new ArrayList<>(0);

    // =========================================================================
    // Object Constructors
    // =========================================================================

    /**
     * Empty Constructor, to use if the setters are invoked. This is required
     * for WebServices to work properly.
     */
    public PublishingGroup() {
        // Required for WebServices to work. Comment added to please Sonar.
    }

    /**
     * Default Constructor, for creating a new Publishing Group.
     *
     * @param name   Name of the Group
     * @param groups List of Countries to be part of this Publishing Group
     */
    public PublishingGroup(final String name, final List<Group> groups) {
        setName(name);
        setGroups(groups);
    }

    /**
     * Default Constructor, for maintaining a Publishing Group.
     *
     * @param publishingGroupId The Id of the existing Publishing Group
     * @param name              Name of the Group
     * @param groups            List of Countries to be part of this Publishing Group
     */
    public PublishingGroup(final String publishingGroupId, final String name, final List<Group> groups) {
        setPublishingGroupId(publishingGroupId);
        setName(name);
        setGroups(groups);
    }

    /**
     * Copy Constructor.
     *
     * @param publishingGroup PublishingGroup Object to copy
     */
    public PublishingGroup(final PublishingGroup publishingGroup) {
        if (publishingGroup != null) {
            publishingGroupId = publishingGroup.publishingGroupId;
            name = publishingGroup.name;
            groups = publishingGroup.groups;
        }
    }

    // =========================================================================
    // Standard Setters & Getters
    // =========================================================================

    /**
     * <p>Sets the Id of the PublishingGroup. The value is generated by the IWS,
     * if set, then the system will assume that an record with this Id already
     * exists and attempt to process it, causing an error if it doesn't. If not
     * set, then the IWS will assume that this is a new record, and will
     * likewise throw an error of a matching record exists.</p>
     *
     * <p>The method will throw an {@code IllegalArgumentException} if the given
     * value is not a valid Id.</p>
     *
     * @param publishingGroupId PublishingGroup Id
     * @throws IllegalArgumentException if the given value is invalid
     */
    public void setPublishingGroupId(final String publishingGroupId) {
        ensureValidId("publishingGroupId", publishingGroupId);
        this.publishingGroupId = publishingGroupId;
    }

    public String getPublishingGroupId() {
        return publishingGroupId;
    }

    /**
     * Sets the name of the {@code PublishingGroup} to the given. If the name is
     * null, not set (empty) or too long (a maximum of 50 characters is
     * allowed), then the method will thrown an
     * {@code IllegalArgumentException}.
     *
     * @param name PublishingGroup Name
     */
    public void setName(final String name) {
        ensureNotNullOrEmptyOrTooLong("name", name, MAX_NAME_LENGTH);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the Groups for this {@code PublishingGroup} Object. The Groups must
     * be a valid list, i.e. not null. If the list is invalid, then the method
     * will thrown an {@code IllegalArgumentException}.
     *
     * @param groups PublishingGroup Group List
     */
    public void setGroups(final List<Group> groups) {
        ensureNotNull("groups", groups);
        this.groups.addAll(groups);
    }

    public List<Group> getGroups() {
        return immutableList(groups);
    }

    // =========================================================================
    // DTO required methods
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> validate() {
        final Map<String, String> validation = new HashMap<>(0);

        // As all fields are validated, if set - we just need to check that the
        // required fields are not null
        isNotNull(validation, "name", name);
        isNotNull(validation, "groups", groups);

        return validation;
    }
}
