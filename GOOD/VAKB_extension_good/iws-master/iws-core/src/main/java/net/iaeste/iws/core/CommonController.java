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
package net.iaeste.iws.core;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.AuthenticationToken;
import net.iaeste.iws.api.enums.Permission;
import net.iaeste.iws.common.exceptions.VerificationException;
import net.iaeste.iws.api.util.Verifiable;
import net.iaeste.iws.common.utils.HashcodeGenerator;
import net.iaeste.iws.core.exceptions.SessionException;
import net.iaeste.iws.core.monitors.ActiveSessions;
import net.iaeste.iws.core.services.ServiceFactory;
import net.iaeste.iws.persistence.AccessDao;
import net.iaeste.iws.persistence.Authentication;
import net.iaeste.iws.persistence.entities.GroupEntity;
import net.iaeste.iws.persistence.entities.UserEntity;
import net.iaeste.iws.persistence.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Common Controller, handles the default checks.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
class CommonController {

    private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);

    private static final String NULL_REQUEST = " Object may not be null.";
    protected final ServiceFactory factory;
    private final AccessDao dao;

    CommonController(final ServiceFactory factory) {
        this.factory = factory;
        this.dao = factory.getAccessDao();
    }

    /**
     * Checks the given Token to see if the user is known (authenticated) and
     * allowed (authorized) to invoke the desired request. Please note, that
     * although a user is allowed to perform a request, it doesn't mean that
     * the user is allowed to process the desired data. This check must be made
     * additionally.<br />
     *   If the user is allowed to make the request, then the method will create
     * a new {@code Authentication} Object, that will be returned, this Object
     * will contain the User & Group Entities for the action.
     *
     * @param token      Authentication Token with GroupId
     * @param permission Permission to be checked
     * @return Authentication Object
     * @throws VerificationException if neither authenticated nor authorized
     */
    final Authentication verifyAccess(final AuthenticationToken token, final Permission permission) {
        verify(token, "Invalid Authentication Token provided.");

        return verifyAccess(token, permission, token.getGroupId());
    }

    /**
     * Checks the given Token to see if the user is known (authenticated) and
     * allowed (authorized) to invoke the desired request. Please note, that
     * although a user is allowed to perform a request, it doesn't mean that
     * the user is allowed to process the desired data. This check must be made
     * additionally.<br />
     *   If the user is allowed to make the request, then the method will create
     * a new {@code Authentication} Object, that will be returned, this Object
     * will contain the User & Group Entities for the action.
     *
     * @param token           Authentication Token
     * @param permission      Permission to be checked
     * @param externalGroupId Public id of the Group to use
     * @return Authentication Object
     * @throws VerificationException if neither authenticated nor authorized
     */
    private Authentication verifyAccess(final AuthenticationToken token, final Permission permission, final String externalGroupId) {
        final ActiveSessions sessions = factory.getActiveSessions();

        if (sessions.isActive(token.getToken())) {
            try {
                // Authentication Check; Expect Exception if unable to find a User
                final UserEntity user = dao.findActiveSession(token).getUser();

                // Okay, Session is authentic (otherwise an exception would've
                // been thrown), so let's register the last usage. The attempt
                // at using the system is independent of the permission check
                sessions.updateToken(token.getToken());

                // Authorization Check; Expect Exception if unable to match a Group
                final GroupEntity group = dao.findGroupByPermission(user, externalGroupId, permission);

                // So far so good, return the information
                return new Authentication(token, user, group, token.getTraceId());
            } catch (PersistenceException e) {
                throw new VerificationException(e);
            }
        } else {
            throw new SessionException(IWSErrors.SESSION_EXPIRED, "The token has expired.");
        }
    }

    /**
     * For those requests, which does not require a Permission, we still need to
     * verify the provided Token, fetch an {@code Authentication} Object, which
     * we can use internally.
     *
     * @param token Authentication Token
     * @return Authentication Object
     * @throws VerificationException if neither authenticated nor authorized
     */
    final Authentication verifyPrivateAccess(final AuthenticationToken token) {
        verify(token, "Invalid Authentication Token provided.");
        final ActiveSessions sessions = factory.getActiveSessions();

        if (sessions.isActive(token.getToken())) {
            try {
                // Authentication Check; Expect Exception if unable to find a User
                final UserEntity user = dao.findActiveSession(token).getUser();

                // For most requests, we are not using the Group, so for our Private
                // usage, we're ignoring this. If data has to be added, and thus
                // requires the Private Group, then this must be fetched. The
                // decision to do it so, was made to avoid loosing too much
                // performance on operations that are rarely required
                return new Authentication(token, user);
            } catch (PersistenceException e) {
                throw new VerificationException(e);
            }
        } else {
            throw new SessionException(IWSErrors.SESSION_EXPIRED, "The token has expired.");
        }
    }

    /**
     * Internal method to test verifiable objects. If the object is undefined,
     * i.e. null a VerificationException with the provided message is thrown,
     * otherwise the verify method is called on the verifiable object.
     *
     * @param verifiable Object to verify
     * @param message    The Error message to display
     * @throws VerificationException if the verification failed
     * @see Verifiable#verify()
     */
    final void verify(final Verifiable verifiable, final String... message) {
        if (LOG.isTraceEnabled() && (verifiable != null)) {
            LOG.trace("Verifying Object {}", verifiable.getClass().getName());
        }

        if (verifiable == null) {
            final String text = prepareErrorText("Cannot process a Null Request Object.", message);

            throw new VerificationException(text + NULL_REQUEST);
        }

        final Map<String, String> validationResult = verifiable.validate();

        if (!validationResult.isEmpty()) {
            throw new VerificationException("Validation failed: " + validationResult);
        }
    }
    /**
     * Internal method to verify the validity of a given e-mail address. If the
     * address does not match what the IWS allows, then a
     * {@code VerificationException} is thrown.
     *
     * @param value Value to verify if is a proper e-mail address
     * @throws VerificationException if not a valid e-mail address
     */
    final void verifyEmail(final String value) {
        if ((value != null) && !IWSConstants.EMAIL_PATTERN.matcher(value).matches()) {
            throw new VerificationException(prepareErrorText("Invalid e-mail address provided."));
        }
    }

    /**
     * Internal method to verify if the given code being used matches the size
     * of the one used within the IWS. If the code doesn't match, then a
     * {@code VerificationException} is thrown.
     *
     * @param code    Code to verify if matches an IWS HashCode
     * @param message Optional error messsage
     * @throws VerificationException if not a valid IWS HashCode
     */
    final void verifyCode(final String code, final String... message) {
        if ((code == null) || (code.length() != HashcodeGenerator.HASHCODE_LENGTH)) {
            throw new VerificationException(prepareErrorText("Invalid Code Object", message));
        }
    }

    private static String prepareErrorText(final String defaultMessage, final String... message) {
        final String text;

        if ((message != null) && (message.length == 1)) {
            text = message[0];
        } else {
            text = defaultMessage;
        }

        return text;
    }
}
