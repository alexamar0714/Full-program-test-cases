/* ReservationStatus.java
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

/**
 * Note: by convention the value returned by {@link #toString} is used to display a description of the
 * enum value in the web interface.
 */
public enum ReservationStatus {
    REQUEST("Wachten op goedkeuring"),
    ACCEPTED("Aanvraag goedgekeurd"),
    REFUSED("Aanvraag geweigerd"),
    CANCELLED("Aanvraag geannuleerd"),
    REQUEST_DETAILS("Wachten op ritdetails"),
    DETAILS_PROVIDED("Ritdetails ingegeven"),
    FINISHED("Rit afgewerkt"),
    CANCELLED_LATE("Rit ging niet door"),
    DETAILS_REJECTED("Ritdetails niet correct"),
    FROZEN ("Rit volledig afgewerkt");

    // Enum definition
    private String description;

    private ReservationStatus(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
