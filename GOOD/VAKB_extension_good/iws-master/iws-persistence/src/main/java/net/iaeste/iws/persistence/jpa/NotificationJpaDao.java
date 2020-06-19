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

import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.exceptions.IWSException;
import net.iaeste.iws.common.configuration.Settings;
import net.iaeste.iws.common.notification.NotificationType;
import net.iaeste.iws.persistence.NotificationDao;
import net.iaeste.iws.persistence.entities.notifications.NotificationConsumerEntity;
import net.iaeste.iws.persistence.entities.notifications.NotificationJobEntity;
import net.iaeste.iws.persistence.entities.UserEntity;
import net.iaeste.iws.persistence.entities.UserNotificationEntity;
import net.iaeste.iws.persistence.views.NotificationJobTasksView;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author  Pavel Fiala / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class NotificationJpaDao extends BasicJpaDao implements NotificationDao {

    /**
     * Default Constructor.
     *
     * @param entityManager  Entity Manager instance to use
     * @param settings       IWS System Settings
     */
    public NotificationJpaDao(final EntityManager entityManager, final Settings settings) {
        super(entityManager, settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserNotificationEntity findUserNotificationSetting(final UserEntity user, final NotificationType type) {
        final Query query = entityManager.createNamedQuery("notifications.findSettingByUserAndType");
        query.setParameter("id", user.getId());
        query.setParameter("type", type);

        final List<UserNotificationEntity> result = query.getResultList();
        UserNotificationEntity entity = null;

        if (result.size() == 1) {
            entity = result.get(0);
        } else if (result.size() > 1) {
            throw new IWSException(IWSErrors.AUTHORIZATION_ERROR, "No user notification (" + type + ") for the user with id: '" + user.getId() + "' was found.");
        }

        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NotificationConsumerEntity> findActiveNotificationConsumers() {
        final Query query = entityManager.createNamedQuery("notifications.findConsumersByActive");
        query.setParameter("active", Boolean.TRUE);

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NotificationJobEntity> findUnprocessedNotificationJobs(final Date date) {
        final Query query = entityManager.createNamedQuery("notifications.findJobsByNotifiedAndDate");
        query.setParameter("notified", Boolean.FALSE);
        query.setParameter("date", date);

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationConsumerEntity findNotificationConsumerById(final Long id) {
        final Query query = entityManager.createNamedQuery("notifications.findConsumersById");
        query.setParameter("id", id);

        return (NotificationConsumerEntity) query.getSingleResult();
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public List<NotificationJobTasksView> findUnprocessedNotificationJobTaskByConsumerId(final Long consumerId, final Integer attemptsLimit) {
        final Query query = entityManager.createNamedQuery("view.NotificationJobTasksByConsumerId");
        query.setParameter("consumerId", consumerId);
        query.setParameter("attempts", attemptsLimit);

        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateNotificationJobTask(final Long id, final boolean processed, final Integer attempts) {
        final Query query = entityManager.createNamedQuery("notifications.updateJobTaskProcessedAndAttempts");
        query.setParameter("processed", processed);
        query.setParameter("attempts", attempts);
        query.setParameter("id", id);

        query.executeUpdate();
    }
}
