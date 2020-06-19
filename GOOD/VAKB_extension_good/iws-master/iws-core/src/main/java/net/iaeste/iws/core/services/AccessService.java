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
package net.iaeste.iws.core.services;

import static net.iaeste.iws.common.utils.LogUtil.formatLogMessage;
import static net.iaeste.iws.common.utils.StringUtils.toLower;
import static net.iaeste.iws.core.transformers.AdministrationTransformer.transform;

import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.AuthenticationToken;
import net.iaeste.iws.api.dtos.Authorization;
import net.iaeste.iws.api.dtos.Country;
import net.iaeste.iws.api.dtos.Group;
import net.iaeste.iws.api.dtos.Password;
import net.iaeste.iws.api.dtos.Role;
import net.iaeste.iws.api.dtos.User;
import net.iaeste.iws.api.dtos.UserGroup;
import net.iaeste.iws.api.enums.Permission;
import net.iaeste.iws.api.enums.UserStatus;
import net.iaeste.iws.api.exceptions.IWSException;
import net.iaeste.iws.api.requests.AuthenticationRequest;
import net.iaeste.iws.api.requests.SessionDataRequest;
import net.iaeste.iws.api.responses.AuthenticationResponse;
import net.iaeste.iws.api.responses.FetchPermissionResponse;
import net.iaeste.iws.api.responses.SessionDataResponse;
import net.iaeste.iws.api.util.DateTime;
import net.iaeste.iws.common.configuration.Settings;
import net.iaeste.iws.common.notification.NotificationType;
import net.iaeste.iws.core.exceptions.SessionException;
import net.iaeste.iws.core.monitors.ActiveSessions;
import net.iaeste.iws.core.monitors.LoginRetries;
import net.iaeste.iws.core.notifications.Notifications;
import net.iaeste.iws.persistence.AccessDao;
import net.iaeste.iws.persistence.Authentication;
import net.iaeste.iws.persistence.entities.SessionEntity;
import net.iaeste.iws.persistence.entities.UserEntity;
import net.iaeste.iws.persistence.views.UserPermissionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class AccessService extends CommonService<AccessDao> {

    private static final Logger LOG = LoggerFactory.getLogger(AccessService.class);
    private final Notifications notifications;
    private final ActiveSessions activeSessions;
    private final LoginRetries loginRetries;

    /**
     * Default Constructor. This Service only requires an AccessDao instance,
     * which is used for all database operations.
     *
     * @param settings      IWS Settings
     * @param dao           AccessDAO instance
     * @param notifications Notification Object
     */
    public AccessService(final Settings settings, final AccessDao dao, final Notifications notifications) {
        super(settings, dao);

        this.notifications = notifications;
        activeSessions = ActiveSessions.getInstance(settings);
        loginRetries = LoginRetries.getInstance(settings);
    }

    /**
     * Generates a new Session for the User, using the provided (verified)
     * credentials. Returns a new Token, if no session exists. If an active
     * session exists, then the method will thrown an SessionExists Exception.
     *
     * @param request Request Object with User Credentials
     * @return New AuthenticationToken
     * @throws SessionException if an Active Session already exists
     */
    public AuthenticationResponse generateSession(final AuthenticationRequest request) {
        removeDeprecatedSessions();
        loginRetries.registerUser(toLower(request.getUsername()));
        final UserEntity user = findUserFromCredentials(request);
        final SessionEntity activeSession = dao.findActiveSession(user);

        if ((activeSession == null) && !activeSessions.hasMaximumRegisteredSessions()) {
            // Before continuing with authenticating the User, we're checking
            // the EULA status, to ensure that the User has accepted the latest
            verifyLicenseAgreement(request, user);

            final SessionEntity session = generateAndPersistSessionKey(user);
            activeSessions.registerToken(session.getSessionKey(), session.getCreated());
            loginRetries.removeAuthenticatedUser(request.getUsername());

            final AuthenticationToken token = new AuthenticationToken(session.getSessionKey());
            LOG.info(formatLogMessage(token, "Created a new Session for the user " + user));
            return new AuthenticationResponse(token);
        } else {
            final String msg = "An Active Session for user %s %s already exists.";
            throw new SessionException(format(msg, user.getFirstname(), user.getLastname()));
        }
    }

    /**
     * Checks if the User have accepted the latest EULA version. If not, then an
     * Exception is thrown with this error.
     *
     * @param request User Authentication Request with optional EULA version
     * @param user    User Entity to check and possibly update
     * @throws IWSException if the accepted or given EULA version is incorrect
     */
    private void verifyLicenseAgreement(final AuthenticationRequest request, final UserEntity user) {
        final String version = settings.getCurrentEULAVersion();
        final String accepted = user.getEulaVersion();

        // First check, if the current EULA is different from what the User has
        // accepted, then we have to check if the User have accepted the new
        // License or not. It should be noted, that the User has already been
        // added to the LoginRetries queue, so attempting to guess a new version
        // will result in the account being prevented access for a period.
        if (!version.equals(accepted)) {
            // Okay, versions differ. So - let's see if the User have set a new
            // License. By default, the License in the Request is undefined, in
            // which case we will throw an Exception. If it is set, then we will
            // update it in the Database.
            final String updated = request.getEulaVersion();

            if (version.equals(updated)) {
                // User have updated it, so we're storing the changes.
                user.setEulaVersion(version);
                dao.persist(user);
            } else {
                throw new IWSException(IWSErrors.DEPRECATED_EULA, "The User must accept the latest EULA before being able to log in.");
            }
        }
    }

    /**
     * Handles the first part of resetting the current session for a user. It
     * simply adds a Temporary Code to the User, and ships it as an immediate
     * notification to the user. The user can then use this Code to invoke the
     * second part of the resetting mechanism, which handles the actual
     * resetting.<br />
     *   The method requires that we can uniquely identify the user, thus the
     * same information as for generating a new Session is required.<br />
     *   If no active sessions exists, then an Exception is thrown.
     *
     * @param request Authentication Request Object, with user credentials
     * @throws SessionException if no active session exists
     */
    public void requestResettingSession(final AuthenticationRequest request) {
        removeDeprecatedSessions();
        final UserEntity user = findUserFromCredentials(request);
        final SessionEntity activeSession = dao.findActiveSession(user);

        if (activeSession != null) {
            user.setCode(hashcodeGenerator.generateHash(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
            dao.persist(user);
            final Authentication authentication = new Authentication(user, UUID.randomUUID().toString());
            notifications.notify(authentication, user, NotificationType.RESET_SESSION);
        } else {
            throw new SessionException("No Session exists for this user.");
        }
    }

    /**
     * Reads the current user & session data from the system. If unable to find
     * a user for the given reset String, or if no active Session Objects
     * exists, then an exception is thrown, otherwise the current Sessions are
     * deprecated and a new Session is created.
     *
     * @param resetSessionString The Token for resetting the users Session
     * @return New AuthenticationToken
     */
    public AuthenticationToken resetSession(final String resetSessionString) {
        removeDeprecatedSessions();
        final UserEntity user = dao.findActiveUserByCode(resetSessionString);
        final SessionEntity deadSession = dao.findActiveSession(user);

        if (deadSession != null) {
            dao.deprecateSession(deadSession);
            activeSessions.removeToken(deadSession.getSessionKey());
            final SessionEntity session = generateAndPersistSessionKey(user);
            activeSessions.registerToken(session.getSessionKey(), session.getCreated());

            return new AuthenticationToken(session.getSessionKey());
        } else {
            throw new SessionException("No Session exists to reset.");
        }
    }

    /**
     * Clients may store some temporary data together with the Session. This
     * data is set in the DTO Object in the API module, and there converted to
     * a Byte Array. The reason for converting the Byte Array already in the
     * API module, is to avoid needing knowledge about the Object later.<br />
     * The data is simply added (updated) to the currently active session,
     * and saved. The data is then added to the response from a readSessionData
     * request.
     *
     * @param token   User Token
     * @param request SessionData Request
     */
    public <T extends Serializable> void saveSessionData(final AuthenticationToken token, final SessionDataRequest<T> request) {
        final SessionEntity entity = dao.findActiveSession(token);
        entity.setSessionData(request.getSessionData());
        entity.setModified(new Date());
        dao.persist(entity);
    }

    /**
     * Retrieves the data that a client stored temporarily with the Session.
     * The data is read out as a serializable Object, presuming that the
     * invoking method is using the correct Object type for reading as was used
     * for writing.
     *
     * @param token User Token
     * @return SessionData Response
     */
    public <T extends Serializable> SessionDataResponse<T> fetchSessionData(final AuthenticationToken token) {
        final SessionEntity entity = dao.findActiveSession(token);
        final byte[] data = entity.getSessionData();
        final DateTime created = new DateTime(entity.getCreated());
        final DateTime modified = new DateTime(entity.getModified());

        final SessionDataResponse<T> response = new SessionDataResponse<>();
        response.setSessionData(data);
        response.setModified(modified);
        response.setCreated(created);

        return response;
    }

    /**
     * Deprecates a Session, meaning that the current session associated with
     * the given token, is set to deprecated (invalid), so it can no longer be
     * used.
     *
     * @param token Token containing the session to deprecate
     */
    public void deprecateSession(final AuthenticationToken token) {
        removeDeprecatedSessions();
        final SessionEntity session = dao.findActiveSession(token);
        dao.deprecateSession(session);
        activeSessions.removeToken(token.getToken());
        LOG.info(formatLogMessage(token, "Deprecated session for user: " + session.getUser()));
    }

    /**
     * Finds the user account for the user who forgot the password in the
     * database, based on the provided username. If the username does not exist
     * in the database, then the request is ignored and no error message is
     * returned. This is to ensure that a caller cannot use this functionality
     * to learn about possible accounts in the system.
     *
     * @param username Username to the users account
     */
    public void forgotPassword(final String username) {
        final UserEntity user = dao.findActiveUserByUsername(username);

        if (user != null) {
            user.setCode(hashcodeGenerator.generateHash(UUID.randomUUID().toString(), user.getSalt()));
            dao.persist(user);
            notifications.notify(user);
        } else {
            throw new IWSException(IWSErrors.AUTHENTICATION_ERROR, "No account for this user was found.");
        }
    }

    /**
     * Resets the user session, by finding the Account via the token. Only
     * accounts that are currently Active can have their passwords reset.
     *
     * @param password           New Password for the user
     */
    public void resetPassword(final Password password) {
        final UserEntity user = dao.findActiveUserByCode(password.getIdentification());

        if (user != null) {
            final String pwd = toLower(password.getNewPassword());
            final String salt = UUID.randomUUID().toString();

            user.setPassword(hashcodeGenerator.generateHash(pwd, salt));
            user.setSalt(salt);
            user.setCode(null);
            user.setModified(new Date());

            dao.persist(user);
        } else {
            throw new IWSException(IWSErrors.AUTHENTICATION_ERROR, "No account for this user was found.");
        }
    }

    /**
     * Updates a users Password (and Salt). The method checks the users current
     * Password against the one provided, and only if there is a match, will
     * the system update the new Password. If the old Password is invalid, then
     * the check should catch it.<br />
     *   If the old Password is invalid, then an Invalid
     *
     * @param authentication Authentication Object, with User & optional Group
     * @param password       New Password for the user
     */
    public void updatePassword(final Authentication authentication, final Password password) {
        final String newPassword = toLower(password.getNewPassword());
        final String salt = UUID.randomUUID().toString();
        final UserEntity user = authentication.getUser();

        if (isOldPasswordValid(user, password)) {
            user.setPassword(hashcodeGenerator.generateHash(newPassword, salt));
            user.setSalt(salt);
            user.setModified(new Date());

            dao.persist(user);
        } else {
            throw new IWSException(IWSErrors.CANNOT_UPDATE_PASSWORD, "The system cannot update Password.");
        }
    }

    /**
     * Fetches a list of Permissions for a User. If the ExternalGroupId is not
     * set, then the result will be all the Groups and their respective
     * Permissions. If the ExternalGroupId is set, then the result will be for
     * the specific Group, unless the user is either not a member of the given
     * group, or the group is invalid, in which case, an Exception is thrown.
     *
     * @param authentication  Authentication Object, with User & optional Group
     * @param externalGroupId If only the permissions for the given Groups
     *                        should be fetched
     * @return List of Authorization Objects
     */
    public FetchPermissionResponse findPermissions(final Authentication authentication, final String externalGroupId) {
        // List will always contain at least 1 entry, otherwise an exception is thrown
        final List<UserPermissionView> found = dao.findPermissions(authentication, externalGroupId);
        final Map<String, Set<Permission>> permissions = new HashMap<>(16);
        final Map<String, UserGroup> userGroups = new HashMap<>(16);
        final User user = transform(authentication.getUser());

        for (final UserPermissionView view : found) {
            final UserGroup userGroup = readUserGroup(view);
            userGroup.setUser(user);
            if (!userGroups.containsKey(userGroup.getUserGroupId())) {
                userGroups.put(userGroup.getUserGroupId(), userGroup);
                permissions.put(userGroup.getUserGroupId(), EnumSet.noneOf(Permission.class));
            }
            permissions.get(userGroup.getUserGroupId()).add(view.getPermission());
        }

        final List<Authorization> list = convertPermissionMap(userGroups, permissions);
        final String userId = found.get(0).getExternalUserId();
        return new FetchPermissionResponse(userId, list);
    }

    // =========================================================================
    // Internal helper methods
    // =========================================================================

    /**
     * Removes all deprecated Sessions from the in-memory listing.
     */
    private void removeDeprecatedSessions() {
        for (final String token : activeSessions.findAndRemoveExpiredTokens()) {
            final SessionEntity session = dao.findActiveSession(token);
            final UserEntity user = session.getUser();
            dao.deprecateSession(session);
            LOG.info("Deprecated inactive session for user {}", user);
        }
    }

    private SessionEntity generateAndPersistSessionKey(final UserEntity user) {
        final String sessionKey = hashcodeGenerator.generateRandomHash();

        // Generate the new Session, and persist it
        final SessionEntity entity = new SessionEntity();
        entity.setUser(user);
        entity.setSessionKey(sessionKey);
        dao.persist(entity);

        // Now, let's return the newly generated SessionKey
        return entity;
    }

    /**
     * Finding a user based on the credentials requires that we first lookup the
     * user, based on the username. The method then checks if the password
     * matches the one stored for the user.<br />
     *   The method makes no distinction between the existing of an Entity, or
     * of the password was incorrect, this is to avoid that someone attempts to
     * run a check against the system for known user accounts, which can then be
     * further tested.<br />
     *   If the user was found (Entity exists matching the credentials), then
     * this Entity is returned, otherwise a {@code IWSException} is thrown.
     *
     * @param request Authentication Request Object with username and password
     * @return Found UserEntity
     * @throws IWSException if no user account exists or matches the credentials
     */
    private UserEntity findUserFromCredentials(final AuthenticationRequest request) {
        // First, find an Entity exists for the given (lowerCased) username
        final String username = toLower(request.getUsername());
        final UserEntity user = dao.findActiveUserByUsername(username);

        if (user != null) {
            if (user.getStatus() == UserStatus.ACTIVE) {
                // Okay, an Active  UserEntity Object was found, now to match if
                // the Password is correct, we first have to read it out of the
                // Request Object, lowercase and generate a salted
                // cryptographic hash value for it, which we can then match
                // directly with the stored value from the UserEntity
                final String password = toLower(request.getPassword());
                final String hashcode = hashcodeGenerator.generateHash(password, user.getSalt());
                if (!hashcode.equals(user.getPassword())) {
                    // Password mismatch, throw generic error
                    throw new IWSException(IWSErrors.AUTHENTICATION_ERROR, "No account for the user '" + username + "' was found.");
                }

                // All good, return found UserEntity
                return user;
            } else {
                // No active account exist! Error condition
                throw new IWSException(IWSErrors.NO_USER_ACCOUNT_FOUND, "The account is not active, and can therefore not be used.");
            }
        } else {
            // No account for this User, throw generic error
            throw new IWSException(IWSErrors.AUTHENTICATION_ERROR, "No account for the user '" + username + "' was found.");
        }
    }

    private boolean isOldPasswordValid(final UserEntity user, final Password password) {
        final String oldPassword = password.getIdentification();
        final boolean result;

        if (oldPassword != null) {
            final String pwd = hashcodeGenerator.generateHash(toLower(oldPassword), user.getSalt());
            result = user.getPassword().equals(pwd);
        } else {
            result = false;
        }

        return result;
    }

    private static UserGroup readUserGroup(final UserPermissionView view) {
        final UserGroup userGroup = new UserGroup();

        userGroup.setUserGroupId(view.getExternalUserGroupId());
        userGroup.setGroup(readGroup(view));
        userGroup.setRole(readRole(view));
        userGroup.setTitle(view.getTitle());

        return userGroup;
    }

    private static Group readGroup(final UserPermissionView view) {
        final Group group = new Group();

        group.setGroupId(view.getExternalGroupId());
        group.setGroupType(view.getGroupType());
        group.setGroupName(view.getGroupname());
        group.setFullName(view.getGroupFullname());
        group.setCountry(readCountry(view));
        group.setDescription(view.getGroupDescription());

        return group;
    }

    private static Role readRole(final UserPermissionView view) {
        final Role role = new Role();

        role.setRoleId(view.getExternalRoleId());
        role.setRoleName(view.getRole());

        return role;
    }

    private static Country readCountry(final UserPermissionView view) {
        Country country = null;

        if (view .getCountryCode() != null) {
            country = new Country();
            country.setCountryCode(view.getCountryCode());
        }

        return country;
    }

    private static List<Authorization> convertPermissionMap(final Map<String, UserGroup> userGroupMap, final Map<String, Set<Permission>> permissionMap) {
        final List<Authorization> result = new ArrayList<>(userGroupMap.size());

        for (final Map.Entry<String, Set<Permission>> permissionSet : permissionMap.entrySet()) {
            final String key = permissionSet.getKey();
            result.add(readFromSet(userGroupMap.get(key), permissionSet));
        }

        return result;
    }

    private static Authorization readFromSet(final UserGroup userGroup, final Map.Entry<String, Set<Permission>> set) {
        userGroup.getRole().setPermissions(set.getValue());

        return new Authorization(userGroup);
    }
}
