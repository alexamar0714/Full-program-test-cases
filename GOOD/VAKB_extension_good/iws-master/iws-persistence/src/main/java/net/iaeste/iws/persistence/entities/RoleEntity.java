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
package net.iaeste.iws.persistence.entities;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.persistence.Externable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Roles are "Hats", that a user can wear in a specific context. By default, a
 * few roles exists, which can be used for general access. However, it is also
 * possible to create customized roles, which can be used to create a more
 * fine-grained access control.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 * @noinspection AssignmentToDateFieldFromParameter
 */
@NamedQueries({
        @NamedQuery(name = "role.findById",
                query = "select r from RoleEntity r " +
                        "where r.id = :id"),
        @NamedQuery(name = "role.findByUserAndGroup",
                query = "select r from UserGroupEntity ug, RoleEntity r " +
                        "where ug.role.id = r.id" +
                        "  and ug.user.externalId = :euid" +
                        "  and ug.group.id = :gid"),
        @NamedQuery(name = "role.findByExternalIdAndGroup",
                query = "select r from RoleEntity r " +
                        "where r.externalId = :reid" +
                        "  and r.country = null" +
                        "  and r.group = null" +
                        "  or (r.country.id = :cid" +
                        "    or r.group.id = :gid)"),
        @NamedQuery(name = "role.findByExternalId",
                    query = "select r from RoleEntity r " +
                        "where r.externalId = :erid")
})
@Entity
@Table(name = "roles")
public final class RoleEntity extends AbstractUpdateable<RoleEntity> implements Externable<RoleEntity> {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id = null;

    /**
     * The content of this Entity is exposed externally, however to avoid that
     * someone tries to spoof the system by second guessing our Sequence values,
     * An External Id is used, the External Id is a Unique UUID value, which in
     * all external references is referred to as the "Id". Although this can be
     * classified as StO (Security through Obscurity), there is no need to
     * expose more information than necessary.
     */
    @Column(name = "external_id", length = 36, unique = true, nullable = false, updatable = false)
    private String externalId = null;

    /**
     * The name of the Role. The default Roles have the names "Owner",
     * "Moderator", "Member" and "Guest". It is possible to also create custom
     * roles, to fit more specific needs. the name of a Role should optimally
     * be unique, but as some roles are relevant only for certain countries, the
     * potential name overlap should not be an issue, hence there is no database
     * uniqueness check on the name, instead this is made in the Business Logic.
     */
    @Column(name = "role", length = 50, nullable = false)
    private String role = null;

    /**
     * Customized Role are either assigned to a Country or to a Group. Meaning
     * that the members of either the assigned Country or Group can use this
     * Role to their liking.<br />
     *   Customized Roles cannot be deleted - the reason for this is that the
     * system have no way of determining which Role to assign to the affected
     * users otherwise.
     */
    @ManyToOne(targetEntity = CountryEntity.class)
    @JoinColumn(name = "country_id", referencedColumnName = "id", updatable = false)
    private CountryEntity country = null;

    /**
     * Customized Role are either assigned to a Country or to a Group. Meaning
     * that the members of either the assigned Country or Group can use this
     * Role to their liking.<br />
     *   Customized Roles cannot be deleted - the reason for this is that the
     * system have no way of determining which Role to assign to the affected
     * users otherwise.
     */
    @ManyToOne(targetEntity = GroupEntity.class)
    @JoinColumn(name = "group_id", referencedColumnName = "id", updatable = false)
    private GroupEntity group = null;

    /**
     * Longer description of the Role.
     */
    @Column(name = "description", length = 2048, nullable = false)
    private String description = null;

    /**
     * Last time the Entity was modified.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified", nullable = false)
    private Date modified = new Date();

    /**
     * Timestamp when the Entity was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    private Date created = new Date();

    // =========================================================================
    // Entity Setters & Getters
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return externalId;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setCountry(final CountryEntity country) {
        this.country = country;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setGroup(final GroupEntity group) {
        this.group = group;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModified(final Date modified) {
        this.modified = modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getModified() {
        return modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getCreated() {
        return created;
    }

    // =========================================================================
    // Entity Standard Methods
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean diff(final RoleEntity obj) {
        // Until properly implemented, better return true to avoid that we're
        // missing updates!
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(final RoleEntity obj) {
        if (canMerge(obj)) {
            role = which(role, obj.role);
            description = which(description, obj.description);
        }
    }
}
