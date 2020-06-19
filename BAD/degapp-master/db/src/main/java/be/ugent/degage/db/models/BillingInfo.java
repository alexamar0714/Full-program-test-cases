/* BillingInfo.java
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

import java.time.LocalDate;

/**
 * Contains information about which kind of billings are available for a given user, including
 * billings for the cars of which his is owner
 */
public class BillingInfo {

    private int id;
    private String description;
    private LocalDate start;
    private LocalDate limit;
    private BillingStatus status;

    private int carId; // 0 for a users billing, otherwise car id

    private String carName; // null for users billing

    public BillingInfo(int id, String description, LocalDate start, LocalDate limit, BillingStatus status, int carId, String carName) {
        this.id = id;
        this.description = description;
        this.start = start;
        this.limit = limit;
        this.status = status;
        this.carId = carId;
        this.carName = carName;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getLimit() {
        return limit;
    }

    public BillingStatus getStatus() {
        return status;
    }

    public int getCarId() {
        return carId;
    }

    public String getCarName() {
        return carName;
    }
}
