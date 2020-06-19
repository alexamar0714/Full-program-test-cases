/* FileDAO.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright Ⓒ 2014-2015 Universiteit Gent
 * 
 * This file is part of the Degage Web Application
 * 
 * Corresponding author (see also AUTHORS.txt)
 * 
 * Kris Coolsaet
 * Department of Applied Mathematics, Computer Science and Statistics
 * Ghent University 
 * Krijgslaan 281-S9
 * B-9000 GENT Belgium
 * 
 * The Degage Web Application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Degage Web Application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with the Degage Web Application (file LICENSE.txt in the
 * distribution).  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ugent.degage.db.dao;

import be.ugent.degage.db.DataAccessException;
import be.ugent.degage.db.models.File;

/**
 * DAO for handling file(path)s
 */
public interface FileDAO {

    // GENERAL FILES

    /**
     * Return the file with given id, or null if it does not exist
     */
    public File getFile(int id) throws DataAccessException;

    /**
     * Create a file
     */
    public File createFile(String path, String fileName, String contentType) throws DataAccessException;

    /**
     * Delete a file
     */
    public void deleteFile(int fileId) throws DataAccessException;

    // USER FILES

    public enum UserFileType {
        ID, LICENSE // TODO: add DAMAGE?
    }

    /**
     * Delete an image file for an identity card or a drivers license
     */
    public void deleteUserFile(int userId, int fileId, UserFileType uft) throws DataAccessException;


    /**
     * Return all the image files for an identity card or a drivers license
     */
    public Iterable<File> getUserFiles(int userId, UserFileType uft) throws DataAccessException;

    /**
     * Return a single user image file, or null if it does not exist
     */
    public File  getUserFile(int userId, int fileId, UserFileType uft) throws DataAccessException;

    /**
     * Add an image file for an identity card  or a drivers license
     */
    public void addUserFile(int userId, int fileId, UserFileType uft) throws DataAccessException;

    /**
     * Did the given user upload at least one file of the given type? (Used to check completeness of profile.)
     */
    public boolean hasUserFile (int userId, UserFileType uft) throws DataAccessException;

    // DAMAGE FILES

    /**
     * Return the image files for a damage case
     */
    public Iterable<File> getDamageFiles(int damageId) throws DataAccessException;

    /**
     * Add an image file to a damage case
     */
    public void addDamageFile(int damageId, int fileId) throws DataAccessException;

}
