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
import net.iaeste.iws.api.enums.MonitoringLevel;
import net.iaeste.iws.api.enums.Privacy;
import net.iaeste.iws.persistence.Externable;
import net.iaeste.iws.persistence.monitoring.Monitored;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@NamedQueries({
        @NamedQuery(name = "file.findByUserAndExternalId",
                query = "select f from FileEntity f " +
                        "where f.user.id = :uid" +
                        "  and f.externalId = :efid"),
        @NamedQuery(name = "file.findByUserGroupAndExternalId",
                query = "select f " +
                        "from" +
                        "  FileEntity f," +
                        "  UserGroupEntity u2g " +
                        "where f.group.id = u2g.group.id" +
                        "  and u2g.user.id = :uid" +
                        "  and u2g.group.id = :gid" +
                        "  and f.externalId = :efid")
})
@Entity
@Table(name = "files")
@Monitored(name = "File", level = MonitoringLevel.DETAILED)
public final class FileEntity extends AbstractUpdateable<FileEntity> implements Externable<FileEntity> {

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
     * All files may be shared, but the Privacy determines who may view/process
     * them. Files marked Public is for everyone to see, files marked Private or
     * Protected is for the Group only.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", length = 10, nullable = false)
    private Privacy privacy = Privacy.PROTECTED;

    /**
     * Some Files are of a general Purpose nature, and thus cannot have a Group
     * associated, which will otherwise limit the viewing.
     */
    @ManyToOne(targetEntity = GroupEntity.class)
    @JoinColumn(name = "group_id", referencedColumnName = "id", updatable = false)
    private GroupEntity group = null;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    private UserEntity user = null;

    @ManyToOne(targetEntity = FolderEntity.class)
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    private FolderEntity folder = null;

    @Monitored(name="File Name", level = MonitoringLevel.DETAILED)
    @Column(name = "filename", length = 100, nullable = false)
    private String filename = null;

    @Monitored(name="File size", level = MonitoringLevel.DETAILED)
    @Column(name = "filesize")
    private Integer filesize = null;

    @Monitored(name="MIME Type", level = MonitoringLevel.DETAILED)
    @Column(name = "mimetype", length = 50)
    private String mimetype = null;

    @Monitored(name="Description", level = MonitoringLevel.DETAILED)
    @Column(name = "description", length = 250)
    private String description = null;

    @Monitored(name="Keywords", level = MonitoringLevel.DETAILED)
    @Column(name = "keywords", length = 250)
    private String keywords = null;

    @Monitored(name="Checksum", level = MonitoringLevel.DETAILED)
    @Column(name = "checksum", length = 128)
    private Long checksum = null;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified", nullable = false)
    private Date modified = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
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

    public void setPrivacy(final Privacy privacy) {
        this.privacy = privacy;
    }

    public Privacy getPrivacy() {
        return privacy;
    }

    public void setGroup(final GroupEntity group) {
        this.group = group;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setUser(final UserEntity user) {
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setFolder(final FolderEntity folder) {
        this.folder = folder;
    }

    public FolderEntity getFolder() {
        return folder;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilesize(final Integer filesize) {
        this.filesize = filesize;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setMimetype(final String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setChecksum(final Long checksum) {
        this.checksum = checksum;
    }

    public Long getChecksum() {
        return checksum;
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
    // Other Methods required for this Entity
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean diff(final FileEntity obj) {
        // Until properly implemented, better return true to avoid that we're
        // missing updates!
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(final FileEntity obj) {
        if (canMerge(obj)) {
            // Note; Id & ExternalId are *not* allowed to be updated!
            privacy = which(privacy, obj.privacy);
            filename = which(filename, obj.filename);
            // The filedata is stored in the filesystem, so the name stored here
            // is simply just the reference. Hence, we do not allow it to be
            // updated
            checksum = which(checksum, obj.checksum);
            filesize = which(filesize, obj.filesize);
            mimetype = which(mimetype, obj.mimetype);
            description = which(description, obj.description);
            keywords = which(keywords, obj.keywords);
            checksum = which(checksum, obj.checksum);
        }
    }
}
