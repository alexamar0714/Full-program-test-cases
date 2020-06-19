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
package net.iaeste.iws.api.dtos;

import static net.iaeste.iws.api.util.Immutable.immutableSet;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.enums.Permission;
import net.iaeste.iws.api.util.Verifications;
import net.iaeste.iws.api.util.StandardMethods;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Roles are simple collections of Permissions, with a name to associate them
 * with. The list of Permissions is all those Permissions that the Role may
 * undertake, this is held together with the Permissions or Actions that a Group
 * may perform, and the intersect of these two sets is then the actual set
 * of Permissions that a user with this Role, may undertake in the Context of
 * the Group.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "role", propOrder = { "roleId", "roleName", "permissions" })
public final class Role extends Verifications {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    @XmlElement(required = true, nillable = true)  @StandardMethods(StandardMethods.For.ALL)      private String roleId = null;
    @XmlElement(required = true)                   @StandardMethods(StandardMethods.For.ALL)      private String roleName = null;
    // For the HashCode & Equals, the name and Id should be enough as they
    // combined should be unique. The Permissions are only used by toString
    @XmlElement(required = true) @StandardMethods(StandardMethods.For.TOSTRING) private Set<Permission> permissions = EnumSet.noneOf(Permission.class);

    // =========================================================================
    // Object Constructors
    // =========================================================================

    /**
     * Empty Constructor, to use if the setters are invoked. This is required
     * for WebServices to work properly.
     */
    public Role() {
        // Required for WebServices to work. Comment added to please Sonar.
    }

    /**
     * Default Constructor. Note, that the list of Permissions is all the
     * permissions that this role has, not necessarily the ones that apply to a
     * specific group.
     *
     * @param roleId      Id of the Role
     * @param roleName    Name of the Role
     * @param permissions Associated Permissions for this Role
     */
    public Role(final String roleId, final String roleName, final Set<Permission> permissions) {
        setRoleId(roleId);
        setRoleName(roleName);
        setPermissions(permissions);
    }

    /**
     * Copy Constructor.
     *
     * @param role Role Object to copy
     */
    public Role(final Role role) {
        if (role != null) {
            roleId = role.roleId;
            roleName = role.roleName;
            permissions = role.permissions;
        }
    }

    // =========================================================================
    // Standard Setters & Getters
    // =========================================================================

    /**
     * <p>Sets the Role Id, which is the internally generated key for this
     * Object. Note, that the presence of the value will determine if the IWS
     * should process this record as if it exist or not. If the Id is set, but
     * no record exists, then the system will reply with an error. Likewise, if
     * no Id is provided, but the record exists, the system will reply with an
     * error.</p>
     *
     * <p>The value must be a valid Id, otherwise the method will throw an
     * {@code IllegalArgumentException}.</p>
     *
     * @param roleId Role Id
     * @throws IllegalArgumentException if the Id is set but invalid
     * @see Verifications#UUID_FORMAT
     */
    public void setRoleId(final String roleId) {
        ensureValidId("roleId", roleId);
        this.roleId = roleId;
    }

    public String getRoleId() {
        return roleId;
    }

    /**
     * Sets the name of the Role. The name must be defined but not exceed the
     * maximum allowed length of 50 characters. If the name is either null,
     * empty or too long, then the method will thrown an
     * {@code IllegalArgumentException}.
     *
     * @param roleName Name of this Role
     * @throws IllegalArgumentException if the name is either null, empty or too long
     */
    public void setRoleName(final String roleName) {
        ensureNotNullOrEmptyOrTooLong("roleName", roleName, 50);
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    /**
     * <p>The Role contains a set of Permissions. This set may not be null. It
     * is allowed to contain any internally defined Permission. Please note,
     * that a Role is general, and although some permissions may be added, the
     * functionality is linked together with the Groups, so the system will
     * determine if a User is allowed to perform the action based on the
     * combined information of both Permission and Group, not just the
     * one.</p>
     *
     * <p>The IWS uses the mathematical "Set" to verify if a Permission is
     * allowed for a User in the given context or not. If both the Role and the
     * Group have the Permission assigned, then the user may perform the action,
     * otherwise not.</p>
     *
     * <p>If the permission is null, then the method will thrown an
     * {@code IllegalArgumentException}.</p>
     *
     * @param permissions Set of Permissions for this Role
     * @throws IllegalArgumentException if the value is null
     */
    public void setPermissions(final Set<Permission> permissions) {
        ensureNotNull("permissions", permissions);
        this.permissions.addAll(permissions);
    }

    public Set<Permission> getPermissions() {
        return immutableSet(permissions);
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

        isNotNull(validation, "roleName", roleName);

        return validation;
    }
}
