/* checkkm.js
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
 * distribution).  If not, see http://www.gnu.org/licenses/.
 */

/**
 * Checks, and asks to acknowledge the start and end kilometers in newtrip and edittrip.
 * Needs @trips.checkModal
 */

$( "#detailsModal" ).on ('show.bs.modal',
    function() {
        var startVal = $( "#startKm" ).val();
        var startValNumeric = parseInt(startVal, 10);
        var endVal = $( "#endKm" ).val();
        var endValNumeric = parseInt(endVal, 10);
        if (!isNaN(startValNumeric) && !isNaN(endValNumeric) &&
            startValNumeric > 0 && endValNumeric >= startValNumeric) {
           newVal = endValNumeric - startValNumeric;
           $( "#submit" ).show();
           $( "#warning" ).show();
           $( "#error" ).hide();
        } else {
           newVal = "???";
           $( "#submit" ).hide();
           $( "#warning" ).hide();
           $( "#error" ).show();
        }
        $( "#total" ).text( newVal );
        $( "#copyStart" ).text(startVal);
        $( "#copyEnd" ).text(endVal);
    }
);
