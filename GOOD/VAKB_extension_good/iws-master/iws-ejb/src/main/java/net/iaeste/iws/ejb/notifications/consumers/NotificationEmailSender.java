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
package net.iaeste.iws.ejb.notifications.consumers;

import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.enums.NotificationFrequency;
import net.iaeste.iws.api.exceptions.IWSException;
import net.iaeste.iws.common.configuration.Settings;
import net.iaeste.iws.common.notification.NotificationField;
import net.iaeste.iws.common.notification.NotificationType;
import net.iaeste.iws.common.utils.Observable;
import net.iaeste.iws.common.utils.Observer;
import net.iaeste.iws.ejb.emails.EmailMessage;
import net.iaeste.iws.ejb.emails.MessageField;
import net.iaeste.iws.ejb.notifications.MessageGenerator;
import net.iaeste.iws.persistence.AccessDao;
import net.iaeste.iws.persistence.NotificationDao;
import net.iaeste.iws.persistence.entities.UserEntity;
import net.iaeste.iws.persistence.entities.UserNotificationEntity;
import net.iaeste.iws.persistence.jpa.AccessJpaDao;
import net.iaeste.iws.persistence.jpa.NotificationJpaDao;
import net.iaeste.iws.persistence.views.NotificationJobTasksView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Class requires an EJB framework to properly work. For this reason, large
 * parts of the code is commented out.
 *
 * @author  Pavel Fiala / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public class NotificationEmailSender implements Observer {

    private Long id = null;
    private static final Integer ATTEMPTS_LIMIT = 3;
    private boolean initialized = false;

    private MessageGenerator messageGenerator = null;
    private NotificationDao dao = null;
    private AccessDao accessDao = null;

    private static final Logger LOG = LoggerFactory.getLogger(NotificationEmailSender.class);

    private static final String QUEUE_NAME = "jms/queue/iwsEmailQueue";

    private static final String QUEUE_FACTORY_NAME = "jms/factory/iwsQueueConnectionFactory";

    private QueueConnection queueConnection = null;
    private QueueSender sender = null;
    private QueueSession session = null;

    @Override
    public final void init(final EntityManager entityManager, final Settings settings) {
        dao = new NotificationJpaDao(entityManager, settings);
        accessDao = new AccessJpaDao(entityManager, settings);
        messageGenerator = new MessageGenerator(settings);

        initializeQueue();

        initialized = true;
    }

    private void initializeQueue() {
        Context context = null;

        try {
            context = new InitialContext();
            final QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(QUEUE_FACTORY_NAME);
            queueConnection = factory.createQueueConnection();
            queueConnection.start();

            final Queue queue = (Queue) context.lookup(QUEUE_NAME);

            session = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            sender = session.createSender(queue);
            sender.setDeliveryMode(DeliveryMode.PERSISTENT);
        } catch (NamingException | JMSException e) {
            throw new IWSException(IWSErrors.ERROR, "Queue sender (NotificationEmailSender) initialization failed.", e);
        } finally {
            close(context);
        }
    }

    private static void close(final Context context) {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * Method for unsubscribing from queue and closing connection.
     */
    public final void stop() {
        try {
            sender.close();
            session.close();
            queueConnection.stop();
        } catch (JMSException e) {
            throw new IWSException(IWSErrors.ERROR, "Queue recipient stopping failed.", e);
        }
    }

    @Override
    public final Long getId() {
        return id;
    }

    @Override
    public final void setId(final Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void update(final Observable subject) {
        if (initialized) {
            try {
                processMessages();
            } catch (RuntimeException e) {
                // Catching and logging all Runtime Exceptions, to prevent
                // stopping notification processing
                LOG.error("System error occurred: {}.", e.getMessage(), e);
            }
        } else {
            LOG.warn("Update called for uninitialized observer.");
        }
    }

    private void processMessages() {
        //TODO this DB request doesn't work just after the task is persisted, I (Pavel) have no idea why. once it's solved, some TODOs in NotificationManager(Bean) could fixed
        final List<NotificationJobTasksView> jobTasks = dao.findUnprocessedNotificationJobTaskByConsumerId(id, ATTEMPTS_LIMIT);
        for (final NotificationJobTasksView jobTask : jobTasks) {
            LOG.info("Processing email notification job task {}.", jobTask.getId());
            processTask(jobTask);
        }
    }

    private void processTask(final NotificationJobTasksView task) {
        if ((task != null) && (task.getObject() != null)) {
            LOG.info("Processing email notification job task {}.", task.getId());
            try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(task.getObject());
                 final ObjectInputStream objectStream = new ObjectInputStream(inputStream)) {
                final Map<NotificationField, String> fields = (Map<NotificationField, String>) objectStream.readObject();
                NotificationProcessTaskStatus processedStatus = NotificationProcessTaskStatus.ERROR;
                //TODO task is not processed, so value false is hardcoded for now, should be changed or deleted once problems are solved
                dao.updateNotificationJobTask(task.getId(), false, task.getAttempts() + 1);
                if (fields != null) {
                    processedStatus = processTask(fields, task.getNotificationType());
                }
                final boolean processed = processedStatus != NotificationProcessTaskStatus.ERROR;
                LOG.info("Notification job task {} attempt number is going to be updated to {}, processed set to {}.", task.getId(), task.getAttempts() + 1, processed);
                dao.updateNotificationJobTask(task.getId(), processed, task.getAttempts() + 1);
                LOG.info("Notification job task {} was updated", task.getId());
            } catch (IOException | ClassNotFoundException e) {
                final boolean processed = false;
                LOG.info("Notification job task {} failed, task is going to be updated to {}, processed set to " + processed, task.getId(), task.getAttempts() + 1);
                dao.updateNotificationJobTask(task.getId(), processed, task.getAttempts() + 1);
                LOG.info("Notification job task {} was updated.", task.getId());
                LOG.error(e.getMessage(), e);
            } catch (IWSException e) {
                //prevent throwing IWSException out, it stops the timer to run this processing
                final boolean processed = false;
                dao.updateNotificationJobTask(task.getId(), processed, task.getAttempts() + 1);
                LOG.error("Error during notification processing.", e);
            }
        } else {
            if (task != null) {
                LOG.error("Processing of the {} which contains no Object, cannot be completed.", task);
            } else {
                LOG.error("Processing of a NULL task will not work.");
            }
        }
    }

    private NotificationProcessTaskStatus processTask(final Map<NotificationField, String> fields, final NotificationType type) {
        //TODO marking task as processed depending on the successful sending to all recepients might be a problem. if it
        //     fails for one user, even those already sent users will be included during next processing. Just failed user
        //     NotificationType and message could be saved for further processing/investigation

        NotificationProcessTaskStatus ret = NotificationProcessTaskStatus.ERROR;
        final List<UserEntity> recipients = getRecipients(fields, type);
        if (recipients.isEmpty()) {
            LOG.info("Notification job task for {} has no recipient", type);
            ret = NotificationProcessTaskStatus.NOT_FOR_ME;
        } else {
            for (final UserEntity recipient : recipients) {
                ret = prepareNotification(fields, type, recipient);
            }
        }

        return ret;
    }

    private NotificationProcessTaskStatus prepareNotification(final Map<NotificationField, String> fields, final NotificationType type, final UserEntity recipient) {
        LOG.info("Notification job task for {} has recipient {}", type, recipient.getId());
        NotificationProcessTaskStatus status = NotificationProcessTaskStatus.ERROR;

        try {
            final UserNotificationEntity userSetting = dao.findUserNotificationSetting(recipient, type);
            //Processing of other notification than 'IMMEDIATELY' ones will be triggered by a timer and all required information
            //should be get from DB directly according to the NotificationType
            if ((userSetting != null) && (userSetting.getFrequency() == NotificationFrequency.IMMEDIATELY)) {
                LOG.info("User notification setting for {} was found", type);
                status = sendNotication(fields, type, recipient);
            } else if (userSetting == null) {
                LOG.warn("User {} has no setting for notification type '{}'", recipient.getId(), type);
            }
        } catch (IWSException e) {
            LOG.debug(e.getMessage(), e);
            LOG.warn("User {} has not proper notification setting for notification type {}", recipient.getId(), type);
        }

        return status;
    }

    private NotificationProcessTaskStatus sendNotication(final Map<NotificationField, String> fields, final NotificationType type, final UserEntity recipient) {
        NotificationProcessTaskStatus status = NotificationProcessTaskStatus.ERROR;

        try {
            final ObjectMessage msg = session.createObjectMessage();
            final EmailMessage emsg = new EmailMessage();
            emsg.setTo(getTargetEmailAddress(recipient, type));
            final Map<MessageField, String> messageData = messageGenerator.generate(fields, type);
            LOG.info("Email message for for {} was generated", type);
            emsg.setSubject(messageData.get(MessageField.SUBJECT));
            emsg.setMessage(messageData.get(MessageField.MESSAGE));
            msg.setObject(emsg);

            sender.send(msg);
            LOG.info("Email message for for {} was sent to message queue", type);
            status = NotificationProcessTaskStatus.OK;
        } catch (IWSException e) {
            LOG.error("Notification message generating failed", e);
        } catch (JMSException e) {
            //do something, log or exception?
            LOG.error("Error during sending notification message to JMS queue", e);
        }

        return status;
    }

    /**
     * Retrieve recipients.
     *
     * @param fields Fields
     * @param type   Type
     * @return List of Users
     */
    private List<UserEntity> getRecipients(final Map<NotificationField, String> fields, final NotificationType type) {
        final List<UserEntity> result = new ArrayList<>();
        final UserEntity user;

        switch (type) {
            case ACTIVATE_NEW_USER:
                user = accessDao.findUserByUsername(fields.get(NotificationField.EMAIL));
                if (user != null) {
                    result.add(user);
                }
                break;
            case NEW_GROUP_OWNER:
            case RESET_PASSWORD:
            case RESET_SESSION:
            case UPDATE_USERNAME:
                user = accessDao.findActiveUserByUsername(fields.get(NotificationField.EMAIL));
                if (user != null) {
                    result.add(user);
                }
                break;
            default:
        }

        return result;
    }

    private static String getTargetEmailAddress(final UserEntity recipient, final NotificationType type) {
        final String result;

        switch (type) {
            case ACTIVATE_NEW_USER:
            case NEW_GROUP_OWNER:
            case RESET_PASSWORD:
            case RESET_SESSION:
                result = recipient.getUsername();
                break;
            case UPDATE_USERNAME:
                result = recipient.getData();
                break;
            default:
                result = "";
        }

        return result;
    }
}
