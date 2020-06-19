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
import net.iaeste.iws.api.enums.GroupType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@NamedQueries({
        @NamedQuery(name= "grouptype.findByType",
                query = "select gt from GroupTypeEntity gt " +
                        "where gt.grouptype = :type"),
        @NamedQuery(name = "grouptype.findByName",
                query = "select gt from GroupTypeEntity gt " +
                        "where lower(gt.grouptype) = lower(:name)")
})
@Entity
@Table(name = "grouptypes")
public final class GroupTypeEntity implements IWSEntity {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id = null;

    @Column(name = "grouptype", unique = true, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private GroupType grouptype = null;

    @Column(name = "who_may_join", length = 10, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private GroupType.WhoMayJoin whoMayJoin = null;

    @Column(name = "description", length = 2048, nullable = false, updatable = false)
    private String description = null;

    @Column(name = "private_list", nullable = false, updatable = false)
    private Boolean privateList = null;

    @Column(name = "public_list", nullable = false, updatable = false)
    private Boolean publicList = null;

    @Column(name = "folder_type", length = 10, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private GroupType.FolderType folderType = null;

    /**
     * This field is just a placeholder, to allow that we can use the normal IWS
     * Persistence Logic to perform database operations on this Entity.
     */
    @Transient
    private Date created = null;

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

    public void setGrouptype(final GroupType grouptype) {
        this.grouptype = grouptype;
    }

    public GroupType getGrouptype() {
        return grouptype;
    }

    public void setWhoMayJoin(final GroupType.WhoMayJoin whoMayJoin) {
        this.whoMayJoin = whoMayJoin;
    }

    public GroupType.WhoMayJoin getWhoMayJoin() {
        return whoMayJoin;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPrivateList(final Boolean privateList) {
        this.privateList = privateList;
    }

    public Boolean getPrivateList() {
        return privateList;
    }

    public void setPublicList(final Boolean publicList) {
        this.publicList = publicList;
    }

    public Boolean getPublicList() {
        return publicList;
    }

    public void setFolderType(final GroupType.FolderType folderType) {
        this.folderType = folderType;
    }

    public GroupType.FolderType getFolderType() {
        return folderType;
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
}
