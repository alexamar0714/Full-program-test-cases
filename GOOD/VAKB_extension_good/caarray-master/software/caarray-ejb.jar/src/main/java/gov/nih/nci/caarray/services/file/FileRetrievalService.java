//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.file;

import gov.nih.nci.caarray.domain.file.CaArrayFile;

/**
 * Allows remote API access to download files stored in caArray.
 */
public interface FileRetrievalService {

    /**
     * The JNDI name to look up the remote <code>FileRetrievalService</code> service.
     */
    String JNDI_NAME = "caarray/FileRetrievalServiceBean/remote";

    /**
     * Returns the requested file's contents.
     *
     * @param file the caArray file to retrieve
     * @return a stream to read the file from
     */
    byte[] readFile(CaArrayFile file);
}
