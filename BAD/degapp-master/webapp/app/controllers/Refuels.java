/* Refuels.java
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

package controllers;

import be.ugent.degage.db.DataAccessContext;
import be.ugent.degage.db.Filter;
import be.ugent.degage.db.FilterField;
import be.ugent.degage.db.dao.FileDAO;
import be.ugent.degage.db.dao.ReservationDAO;
import be.ugent.degage.db.models.*;
import controllers.util.FileHelper;
import controllers.util.Pagination;
import db.CurrentUser;
import db.DataAccess;
import db.InjectContext;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.refuels.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Controller that displays information about refuels
 */
public class Refuels extends RefuelCommon {

    /**
     * Dispatches to the correct refuels page
     */
    @AllowRoles
    @InjectContext
    public static Result showRefuels() {
        if (CurrentUser.hasRole(UserRole.CAR_ADMIN)) {
            return ok(refuelsAdmin.render());
        } else if (CurrentUser.hasRole(UserRole.CAR_OWNER)) {
            return ok(refuelsOwner.render());
        } else {
            return ok(refuels.render());
        }
    }

    @AllowRoles
    @InjectContext
    public static Result showUserRefuelsPage(int page, int pageSize, int ascInt, String orderBy, String searchString) {
        //FilterField field = FilterField.stringToField(orderBy);
        //boolean asc = Pagination.parseBoolean(ascInt);
        Filter filter = Pagination.parseFilter(searchString);
        filter.putValue(FilterField.REFUEL_USER_ID, CurrentUser.getId());
        FilterField field = FilterField.stringToField(orderBy, FilterField.FROM);
        return ok(refuelspage.render(
                DataAccess.getInjectedContext().getRefuelDAO().getRefuels(field, (ascInt == 1), page, pageSize, filter)
                ));
    }

    @AllowRoles({UserRole.CAR_OWNER, UserRole.RESERVATION_ADMIN})
    @InjectContext
    public static Result showOwnerRefuelsPage(int page, int pageSize, int ascInt, String orderBy, String searchString) {
        //FilterField field = FilterField.stringToField(orderBy);
        //boolean asc = Pagination.parseBoolean(ascInt);
        Filter filter = Pagination.parseFilter(searchString);
        filter.putValue(FilterField.REFUEL_OWNER_ID, CurrentUser.getId());
        FilterField field = FilterField.stringToField(orderBy, FilterField.FROM);
        return ok(refuelspage.render(
                DataAccess.getInjectedContext().getRefuelDAO().getRefuels(field, (ascInt == 1), page, pageSize, filter)
                ));
        // return ok(refuelList(page, pageSize, filter));
    }

    @AllowRoles({UserRole.CAR_ADMIN})
    @InjectContext
    public static Result showAllRefuelsPage(int page, int pageSize, int ascInt, String orderBy, String searchString) {
        //FilterField field = FilterField.stringToField(orderBy);
        //boolean asc = Pagination.parseBoolean(ascInt);
        Filter filter = Pagination.parseFilter(searchString);
        FilterField field = FilterField.stringToField(orderBy, FilterField.FROM);
        return ok(refuelList(field, ascInt, page, pageSize, filter));
    }

    // should be used with an injected context only
    private static Html refuelList(FilterField field, int ascInt, int page, int pageSize, Filter filter) {
        return refuelspage.render(
                DataAccess.getInjectedContext().getRefuelDAO().getRefuels(field, ascInt == 1 ? true : false, page, pageSize, filter)
        );
    }

    /**
     * Displays the refueling ticket
     */
    @AllowRoles
    @InjectContext
    public static Result getProof(int refuelId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        RefuelExtended refuel = context.getRefuelDAO().getRefuelExtended(refuelId);
        if (isDriverOrOwnerOrAdmin(refuel)) {
            return FileHelper.getFileStreamResult(context.getFileDAO(), refuel.getProofId());
        } else {
            return badRequest(); // hacker
        }
    }

    /**
     *
     */
    // should only be used with injected context - used in refuel menu
    public static int numberOfRefuelRequests() {
        if (CurrentUser.hasRole(UserRole.CAR_OWNER)) {
            return DataAccess.getInjectedContext().getRefuelDAO().numberOfRefuelRequests(CurrentUser.getId());
        } else {
            return 0;
        }
    }

    /**
     * Show all refuels connected with a given trip.
     */
    @AllowRoles
    @InjectContext
    public static Result showRefuelsForTrip(int reservationId, boolean ownerFlow) {
        DataAccessContext context = DataAccess.getInjectedContext();
        TripAndCar trip = context.getTripDAO().getTripAndCar(reservationId, false);
        if (isAuthorized(trip, ownerFlow)) {
            return ok(refuelsForTrip(
                    trip,
                    Form.form(RefuelData.class).fill(RefuelData.EMPTY),
                    context.getRefuelDAO().getRefuelsForCarRide(reservationId),
                    ownerFlow));
        } else {
            return badRequest(); // hacker?
        }
    }

    @AllowRoles
    @InjectContext
    public static Result showDetails(int refuelId) {
        RefuelExtended refuel = DataAccess.getInjectedContext().getRefuelDAO().getRefuelExtended(refuelId);
        if (isDriverOrOwnerOrAdmin(refuel)) {
            return ok(details.render(refuel, isOwnerOrAdmin(refuel)));
        } else {
            return badRequest(); // not authorized
        }
    }

    public static class RefuelWithImageType {
        public Refuel refuel;
        public boolean fileIsImage;

        public RefuelWithImageType(Refuel refuel, boolean fileIsImage) {
            this.refuel = refuel;
            this.fileIsImage = fileIsImage;
        }
    }

    private static Iterable<RefuelWithImageType> addImageTypes(FileDAO fdao, Iterable<Refuel> list) {
        Collection<RefuelWithImageType> result = new ArrayList<>();
        for (Refuel refuel : list) {
            result.add(new RefuelWithImageType(
                    refuel,
                    fdao.getFile(refuel.getProofId()).isImage()
            ));
        }
        return result;
    }

    /**
     * Produces the correct html file for the given 'flow'
     */
    static Html refuelsForTrip(TripAndCar trip, Form<RefuelData> form, Iterable<Refuel> refuels, boolean ownerFlow) {
        // must be used in injected context
        if (ownerFlow) {
            DataAccessContext context = DataAccess.getInjectedContext();
            ReservationDAO dao = context.getReservationDAO();
            return refuelsForTripOwner.render(
                    form,
                    addImageTypes(context.getFileDAO(), refuels),
                    trip,
                    dao.getNextTripId(trip.getId()),
                    dao.getPreviousTripId(trip.getId())
            );
        } else {
            return refuelsForTripDriver.render(form, refuels, trip);
        }
    }

    /**
     * Shows the start page where a date can be chosen to start listing
     * the refuel information
     */
    @AllowRoles({UserRole.CAR_OWNER, UserRole.RESERVATION_ADMIN})
    @InjectContext
    public static Result startOverviewForCar(int carId) {
        if (isOwnerOrAdmin(DataAccess.getInjectedContext().getCarDAO().getCarHeaderShort(carId))) {
            return ok(startoverview.render(
                    Form.form(Calendars.DateData.class), carId
            ));
        } else {
            return badRequest(); // hacker?
        }
    }

    /**
     * Process the information from {@link #startOverviewForCar} and dispatch to the correct trip page
     */
    @AllowRoles({UserRole.CAR_OWNER, UserRole.RESERVATION_ADMIN})
    @InjectContext
    public static Result doStartOverviewForCar(int carId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        if (!isOwnerOrAdmin(context.getCarDAO().getCarHeaderShort(carId))) {
            return badRequest();
        }

        Form<Calendars.DateData> form = Form.form(Calendars.DateData.class).bindFromRequest();

        if (!form.hasErrors()) {
            int reservationId = context.getReservationDAO().getFirstTripAfterDate(carId, Utils.toLocalDate(form.get().date));
            if (reservationId > 0) {
                return redirect(routes.Refuels.showRefuelsForTrip(reservationId, true));
            } else {
                form.reject("date", "Geen ritten gevonden vanaf deze datum");
            }
        }
        return badRequest(startoverview.render(form, carId));

    }


}
