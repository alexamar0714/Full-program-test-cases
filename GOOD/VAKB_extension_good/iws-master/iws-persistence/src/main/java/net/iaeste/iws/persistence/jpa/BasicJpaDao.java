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
package net.iaeste.iws.persistence.jpa;

import static net.iaeste.iws.common.utils.StringUtils.toUpper;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.Field;
import net.iaeste.iws.api.enums.GroupType;
import net.iaeste.iws.api.enums.MonitoringLevel;
import net.iaeste.iws.api.exceptions.IWSException;
import net.iaeste.iws.api.util.Page;
import net.iaeste.iws.api.util.Serializer;
import net.iaeste.iws.common.configuration.Settings;
import net.iaeste.iws.persistence.Authentication;
import net.iaeste.iws.persistence.BasicDao;
import net.iaeste.iws.persistence.Externable;
import net.iaeste.iws.persistence.entities.AddressEntity;
import net.iaeste.iws.persistence.entities.CountryEntity;
import net.iaeste.iws.persistence.entities.EntityConstants;
import net.iaeste.iws.persistence.entities.FileEntity;
import net.iaeste.iws.persistence.entities.FiledataEntity;
import net.iaeste.iws.persistence.entities.GroupEntity;
import net.iaeste.iws.persistence.entities.GroupTypeEntity;
import net.iaeste.iws.persistence.entities.IWSEntity;
import net.iaeste.iws.persistence.entities.MonitoringEntity;
import net.iaeste.iws.persistence.entities.PermissionRoleEntity;
import net.iaeste.iws.persistence.entities.Updateable;
import net.iaeste.iws.persistence.entities.UserEntity;
import net.iaeste.iws.persistence.entities.UserGroupEntity;
import net.iaeste.iws.persistence.exceptions.IdentificationException;
import net.iaeste.iws.persistence.monitoring.MonitoringProcessor;
import net.iaeste.iws.persistence.views.IWSView;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public class BasicJpaDao implements BasicDao {

    private static final String PARAMETER_TABLE = "table";
    private static final String PARAMETER_RECORD = "record";
    private static final String PARAMETER_GROUP = "group";
    private static final String PARAMETER_CODE = "code";
    private static final String PARAMETER_UID = "uid";
    private static final String PARAMETER_GID = "gid";
    private static final String PARAMETER_FID = "fid";
    private static final String PARAMETER_CID = "cid";

    protected EntityManager entityManager;
    protected Settings settings;
    private final MonitoringProcessor monitoringProcessor;

    /**
     * Default Constructor.
     *
     * @param entityManager  Entity Manager instance to use
     * @param settings       IWS System Settings
     */
    public BasicJpaDao(final EntityManager entityManager, final Settings settings) {
        if ((entityManager == null) || (settings == null)) {
            throw new IWSException(IWSErrors.FATAL, "Cannot instantiate the DAO without a valid Entity Manager instance or settings.");
        }

        this.entityManager = entityManager;
        this.settings = settings;
        this.monitoringProcessor = new MonitoringProcessor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void persist(final IWSEntity entityToPersist) {
        ensureUpdateableHasExternalId(entityToPersist);
        entityManager.persist(entityToPersist);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void persist(final Authentication authentication, final IWSEntity entityToPersist) {
        ensureUpdateableHasExternalId(entityToPersist);

        if ((entityToPersist instanceof Updateable<?>) && (entityToPersist.getId() != null)) {
            ((Updateable<?>) entityToPersist).setModified(new Date());
        }

        // We have to start by persisting the entityToPersist, to have an Id
        entityManager.persist(entityToPersist);

        final MonitoringLevel level = findMonitoringLevel(entityToPersist, authentication.getGroup());
        if (level != MonitoringLevel.NONE) {
            final List<Field> changes = monitoringProcessor.findChanges(level, entityToPersist);
            final String className = monitoringProcessor.findClassMonitoringName(entityToPersist);
            persistMonitoredData(authentication, className, entityToPersist.getId(), changes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T extends Updateable<T>> void persist(final Authentication authentication, final T entityToPersist, final T changesToBeMerged) {
        final MonitoringLevel level = findMonitoringLevel(entityToPersist, authentication.getGroup());
        if (level != MonitoringLevel.NONE) {
            final List<Field> changes = monitoringProcessor.findChanges(level, entityToPersist, changesToBeMerged);
            final String className = monitoringProcessor.findClassMonitoringName(entityToPersist);
            persistMonitoredData(authentication, className, entityToPersist.getId(), changes);
        }

        entityToPersist.merge(changesToBeMerged);
        entityToPersist.setModified(new Date());
        entityManager.persist(entityToPersist);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void delete(final IWSEntity entity) {
        // First, let's drop all Objects matching the entityToPersist. Since
        // the record Id in the history table cannot be set up as a foreign
        // key, we must do this manually.
        final String tableName = monitoringProcessor.findClassMonitoringName(entity);
        final Query query = entityManager.createNamedQuery("monitoring.deleteChanges");
        query.setParameter(PARAMETER_TABLE, tableName);
        query.setParameter(PARAMETER_RECORD, entity.getId());
        query.executeUpdate();

        entityManager.remove(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T extends IWSView> List<T> fetchList(final Query query, final Page page) {
        // The Pagination starts with page 1, so we have to subtract one here,
        // to ensure that we read out the correct data from the database.
        if (settings.isPaginationEnabled()) {
            query.setFirstResult((page.pageNumber() - 1) * page.pageSize());
            query.setMaxResults(page.pageSize());
        }

        return query.getResultList();
    }

    private void persistMonitoredData(final Authentication authentication, final String className, final Long recordId, final List<Field> fields) {
        final MonitoringEntity monitoringEntity = new MonitoringEntity();
        final ArrayList<Field> list = new ArrayList<>(0);
        if (fields != null) {
            list.addAll(fields);
        }
        final byte[] data = Serializer.serialize(list);

        monitoringEntity.setUser(authentication.getUser());
        monitoringEntity.setGroup(authentication.getGroup());
        monitoringEntity.setTableName(className);
        monitoringEntity.setRecordId(recordId);
        monitoringEntity.setFields(data);

        entityManager.persist(monitoringEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserGroupEntity> findGroupMembers(final GroupEntity group) {
        return entityManager.createQuery("select u from UserGroupEntity u where u.group = :group")
                            .setParameter(PARAMETER_GROUP, group)
                            .getResultList();
    }

    // =========================================================================
    // Following lookup methods are added here, since they're used often
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<PermissionRoleEntity> findRoles(final GroupEntity group) {
        final Long cid = (group.getCountry() != null) ? group.getCountry().getId() : 0;
        return entityManager.createNamedQuery("permissionRole.findByRoleToGroup")
                            .setParameter(PARAMETER_CID, cid)
                            .setParameter(PARAMETER_GID, group.getId())
                            .getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CountryEntity findCountry(final String countryCode) {
        final Query query = entityManager.createNamedQuery("country.findByCountryCode");
        query.setParameter(PARAMETER_CODE, toUpper(countryCode));

        return findUniqueResult(query, "country");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<CountryEntity> findAllCountries() {
        return entityManager.createNamedQuery("country.findAll")
                            .getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final AddressEntity findAddress(final Long id) {
        final Query query = entityManager.createNamedQuery("address.findById");
        query.setParameter("id", id);

        return findUniqueResult(query, "address");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Long findNumberOfAliasesForName(final String name) {
        final Query query = entityManager.createNamedQuery("user.findNumberOfSimilarAliases");
        query.setParameter("startOfAlias", name.toLowerCase(IWSConstants.DEFAULT_LOCALE) + '%');

        return (Long) query.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final GroupTypeEntity findGroupType(final GroupType groupType) {
        final Query query = entityManager.createNamedQuery("grouptype.findByName");
        // Query runs a String lower check on the value
        query.setParameter("name", groupType.name());

        return findSingleResult(query, "GroupType");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final FileEntity findFileByUserAndExternalId(final UserEntity user, final String externalId) {
        final Query query = entityManager.createNamedQuery("file.findByUserAndExternalId");
        query.setParameter(PARAMETER_UID, user.getId());
        query.setParameter("efid", externalId);

        return findUniqueResult(query, "File");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final FileEntity findAttachedFileByUserAndExternalId(final GroupEntity group, final String externalId) {
        final Query query = entityManager.createNamedQuery("file.findApplicationBySendingGroupAndExternalFileId")
                                         .setParameter(PARAMETER_TABLE, EntityConstants.STUDENT_APPLICATIONS_ATTACHMENT)
                                         .setParameter(PARAMETER_GID, group.getId())
                                         .setParameter(PARAMETER_FID, externalId);

        return findUniqueResult(query, "File");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteFileData(final FileEntity file) {
        return entityManager.createNamedQuery("filedata.deleteByFile")
                            .setParameter(PARAMETER_FID, file.getId())
                            .executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int deleteAttachmentRecord(final FileEntity file) {
        return entityManager.createNamedQuery("attachments.deleteByFile")
                            .setParameter(PARAMETER_FID, file.getId())
                            .executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final GroupEntity findMemberGroup(final UserEntity user) {
        final Query query = entityManager.createNamedQuery("group.findGroupByUserAndType");
        query.setParameter(PARAMETER_UID, user.getId());
        query.setParameter("type", GroupType.MEMBER);

        return findUniqueResult(query, "User");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final FileEntity findFileByUserGroupAndExternalId(final UserEntity user, final GroupEntity group, final String externalId) {
        final Query query = entityManager.createNamedQuery("file.findByUserGroupAndExternalId")
                                         .setParameter(PARAMETER_UID, user.getId())
                                         .setParameter(PARAMETER_GID, group.getId())
                                         .setParameter("efid", externalId);

        return findUniqueResult(query, "File");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final FiledataEntity findAttachedFile(final String fileId, final String groupId) {
        final Query query = entityManager.createNamedQuery("filedata.findApplicationByReceivingGroupAndExternalFileId")
                                         .setParameter(PARAMETER_TABLE, EntityConstants.STUDENT_APPLICATIONS_ATTACHMENT)
                                         .setParameter(PARAMETER_GID, groupId)
                                         .setParameter(PARAMETER_FID, fileId);

        return findUniqueResult(query, "File");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final FiledataEntity findFileData(final Long fileId) {
        final Query query = entityManager.createNamedQuery("filedata.findByFileId")
                                         .setParameter(PARAMETER_FID, fileId);

        return findSingleResult(query, "File");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserGroupEntity> findAllUserGroups(final UserEntity user) {
        return entityManager.createNamedQuery("userGroup.findAllUserGroups")
                            .setParameter(PARAMETER_UID, user.getId())
                            .getResultList();
    }

    // =========================================================================
    // Internal Methods
    // =========================================================================

    /**
     * The JPA Documentation is vague when it comes to Empty lists used in the
     * SQL 'in' expression. If the database doesn't support the empty list, then
     * we may see an 'unexpected end of subtree' Exception if an empty list is
     * passed.<br />
     *   For more details, please see the Hibernate bug report
     * <a href="https://hibernate.atlassian.net/browse/HHH-8091">8091</a>, which
     * also explains this in details. Note at the time of writing this comment,
     * there is no solution pending. Hence we're having this method which will
     * provide a work-around.<br />
     *   Basically, what the method does, is that it takes a list and expands
     * it, if it is empty, with the given value.
     *
     * @param collection Collection of values to expand if empty
     * @param emptyValue new value to be added if the list is empty
     * @return Collection with at least 1 element
     */
    static <T> Collection<T> expandEmptyCollection(final Collection<T> collection, final T emptyValue) {
        final Collection<T> expanded;

        if (collection != null) {
            expanded = collection;

            if (expanded.isEmpty()) {
                expanded.add(emptyValue);
            }
        } else {
            // To prevent NullPointer Exception, we're simply creating a new
            // Collection and add the Empty value to it.
            expanded = new ArrayList<>();
            expanded.add(emptyValue);
        }

        return expanded;
    }

    /**
     * Monitoring of data is made based on the common monitoring level of both
     * the Entity & Group. If both have detailed level - then all information is
     * monitored. Otherwise if either is having either marked or detailed, then
     * the level is marked and if one or both doesn't support monitoring, then
     * nothing is monitored.<br />
     *   By default, all entities support detailed monitoring but the groups
     * doesn't support monitoring. Monitoring must be explicitly added by the
     * group moderators.
     *
     * @param entity Entity to check for monitoring
     * @param group  Group to check for monitoring
     * @return Combined result for the monitoring
     */
    private MonitoringLevel findMonitoringLevel(final IWSEntity entity, final GroupEntity group) {
        final MonitoringLevel entityLevel = monitoringProcessor.findClassMonitoringLevel(entity);
        final MonitoringLevel groupLevel;
        if ((group != null) && (group.getMonitoringLevel() != null)) {
            groupLevel = group.getMonitoringLevel();
        } else {
            groupLevel = MonitoringLevel.NONE;
        }

        final MonitoringLevel level;
        if ((entityLevel == MonitoringLevel.NONE) || (groupLevel == MonitoringLevel.NONE)) {
            level = MonitoringLevel.NONE;
        } else if ((entityLevel == MonitoringLevel.MARKED) || (groupLevel == MonitoringLevel.MARKED)) {
            level = MonitoringLevel.MARKED;
        } else {
            level = MonitoringLevel.DETAILED;
        }

        return level;
    }

    /**
     * First, we check if the Entity is updateable, i.e. one that is exposed
     * externally, if so. Then we must ensure that the externalId is set.
     * For some Entities, they are marked Updateable, but do not have a
     * "real" external Id, rather they're using something else. Those must
     * be defined by the invoking code, otherwise we'll get into trouble.
     *
     * @param entity Entity to check
     */
    private static void ensureUpdateableHasExternalId(final IWSEntity entity) {
        if ((entity instanceof Externable) && (((Externable<?>) entity).getExternalId() == null)) {
            ((Externable<?>) entity).setExternalId(UUID.randomUUID().toString());
            // Just to make sure that the modification date is always set
            ((Updateable<?>) entity).setModified(new Date());
        }
    }

    /**
     * Resolves the given Query, and will throw an Identification Exception, if
     * a unique result was not found.
     *
     * @param query      Query to resolve
     * @param entityName Name of the entity expected, used if exception is thrown
     * @return Unique Entity
     */
    protected <T extends IWSEntity> T findUniqueResult(final Query query, final String entityName) {
        final List<T> found = query.getResultList();

        if (found.isEmpty()) {
            throw new IdentificationException("No " + entityName + " was found.");
        }

        if (found.size() > 1) {
            throw new IdentificationException("Multiple " + entityName + "s were found.");
        }

        return found.get(0);
    }

    /**
     * Attempts to find a single result from the list. If the list is empty,
     * then a null is returned, if there is more than one record, then an
     * Exception is thrown - otherwise if only a single result was found, this
     * will be returned.
     *
     * @param query      Query to resolve
     * @param entityName Name of the entity expected, used if exception is thrown
     * @return Single Entity
     */
    static <T extends IWSEntity> T findSingleResult(final Query query, final String entityName) {
        final List<T> found = query.getResultList();
        T result = null;

        if (found != null) {
            if (found.size() == 1) {
                result = found.get(0);
            } else if (found.size() > 1) {
                throw new IdentificationException("Multiple " + entityName + "s were found.");
            }
        }

        return result;
    }

    static String generateTimestamp() {
        // Format is: Year + Month + Date + Hour24 + Minute + Second + Millis
        // Example: 20140503193432987 -> May 3rd, 2014 at 19:34:43.987
        final String timestampFormat = "yyyyMMddHHmmssSSS";
        final DateFormat formatter = new SimpleDateFormat(timestampFormat, IWSConstants.DEFAULT_LOCALE);

        return formatter.format(new Date());
    }
}
