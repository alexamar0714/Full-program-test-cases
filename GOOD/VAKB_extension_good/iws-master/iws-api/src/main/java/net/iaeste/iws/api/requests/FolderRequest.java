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
package net.iaeste.iws.api.requests;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.dtos.Folder;
import net.iaeste.iws.api.enums.Action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "folderRequest", propOrder = { "parentId", "folder" })
public final class FolderRequest extends Actions {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    /**
     * The Id of the Parent Folder, if nothing given - the Group's root folder
     * is used. This value is used when creating new Folders and when moving
     * Folders.
     */
    @XmlElement(required = true, nillable = true)  private String parentId = null;

    /** The Folder Object to process. */
    @XmlElement(required = true, nillable = true)  private Folder folder = null;

    // =========================================================================
    // Object Constructors
    // =========================================================================

    /**
     * Default Constructor.
     */
    public FolderRequest() {
        super(EnumSet.of(Action.PROCESS, Action.MOVE, Action.DELETE), Action.PROCESS);
    }

    // =========================================================================
    // Standard Setters & Getters
    // =========================================================================

    /**
     * Sets the File to be processed. The method will thrown an
     * {@code IllegalArgumentException} if the Object is not verifiable.
     *
     * @param folder Folder Object to be processed
     * @throws IllegalArgumentException if the File Object is not verifiable
     */
    public void setFolder(final Folder folder) {
        ensureNotNullAndVerifiable("folder", folder);
        this.folder = new Folder(folder);
    }

    public Folder getFolder() {
        return new Folder(folder);
    }

    /**
     * Sets the Id of the parent Folder.
     *
     * @param parentId Parent Folder Id
     * @throws IllegalArgumentException if not a valid Id
     */
    public void setParentId(final String parentId) {
        ensureValidId("parentId", parentId);
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    // =========================================================================
    // Standard Request Methods
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> validate() {
        final Map<String, String> validation = new HashMap<>(1);

        isNotNull(validation, "folder", folder);
        isNotNull(validation, FIELD_ACTION, action);

        return validation;
    }
}
