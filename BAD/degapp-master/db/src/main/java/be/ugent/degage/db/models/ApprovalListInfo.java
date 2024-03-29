/* ApprovalListInfo.java
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

package be.ugent.degage.db.models;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Information about approvals and related users for use in lists
 */
public class ApprovalListInfo {

    private int id;
    private UserHeaderShort user;
    private MembershipStatus status;
    private UserHeaderShort admin;
    private LocalDate dateJoined;
    private LocalDate contractDate;
    private Instant submitted;
    private Integer deposit;
    private Integer fee;

    public ApprovalListInfo(int id, UserHeaderShort user, MembershipStatus status, UserHeaderShort admin,
                            LocalDate dateJoined, LocalDate contractDate, Instant submitted, Integer deposit, Integer fee) {
        this.id = id;
        this.user = user;
        this.status = status;
        this.admin = admin;
        this.dateJoined = dateJoined;
        this.contractDate = contractDate;
        this.submitted = submitted;
        this.deposit = deposit;
        this.fee = fee;
    }

    public int getId() {
        return id;
    }

    public UserHeaderShort getUser() {
        return user;
    }

    public boolean isAdminAssigned () {
        return admin != null;
    }

    public UserHeaderShort getAdmin() {
        return admin;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public boolean isContractSigned() {
        return contractDate != null;
    }

    public LocalDate getContractDate() {
        return contractDate;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public Instant getSubmitted() {
        return submitted;
    }

    public Integer getDeposit() {
        return deposit;
    }

    public Integer getFee() {
        return fee;
    }
}
