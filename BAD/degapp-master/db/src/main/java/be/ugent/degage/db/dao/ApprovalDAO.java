/* ApprovalDAO.java
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
import be.ugent.degage.db.FilterField;
import be.ugent.degage.db.models.Approval;
import be.ugent.degage.db.models.ApprovalListInfo;
import be.ugent.degage.db.models.MembershipStatus;
import be.ugent.degage.db.models.Page;

/**
 * Data access object for approvals
 */
public interface ApprovalDAO {

    /**
     * Was membership requested by this user?
     */
    public boolean membershipRequested (int userId) throws DataAccessException;

    public Page<ApprovalListInfo> getApprovals(FilterField orderBy, boolean asc, int page, int pageSize, MembershipStatus status) throws DataAccessException;

    /**
     * The number of pending approvals.
     */
    public int getNrOfPendingApprovals() throws DataAccessException;

    public void setApprovalAdmin(int approvalId, int adminId) throws DataAccessException;
    public Approval getApproval(int approvalId) throws DataAccessException;

    /**
     * Create approval with submit time set to now. Indicates that the user wants to become a member. Changes the
     * state of the user to FULL_VALIDATING.
     */
    public void createApproval(int userId, int sessionId, String userMessage) throws DataAccessException;

    /**
     * Change the status of the approval (and store admin message). Also stores review date.
     */
    public void setApprovalStatus (int approvalId, MembershipStatus status, String message) throws DataAccessException;
}
