/* AnnouncementDAO.java
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

import be.ugent.degage.db.models.Announcement;

/**
 * Processes announcements
 */
public interface AnnouncementDAO {

    /**
     * Get the description field of the announcement with the given key
     */
    public String getAnnouncementDescription(String key);

    /**
     * Get the announcement for the given key. Does not contain
     * the markdown.
     */
    public Announcement getAnnouncement (String key);

    /**
     * Get the announcement for the given key, markdown included.
     */
    public Announcement getAnnouncementFull (String key);

    /**
     * Update HTML and Markdown for the given key
     */
    public void updateAnnouncement (String key, String html, String markdown);

    /**
     * Get the list of all announcements. Does not contain the markdown.
     */
    public Iterable<Announcement> listAnnouncements ();

}
