/* WFCancel.java
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
import be.ugent.degage.db.dao.ReservationDAO;
import be.ugent.degage.db.models.*;
import com.google.common.base.Strings;
import controllers.util.WorkflowAction;
import controllers.util.WorkflowRole;
import db.CurrentUser;
import db.DataAccess;
import db.InjectContext;
import notifiers.Notifier;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Result;
import views.html.workflow.cancelaccepted;
import views.html.workflow.latecancel;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Controller that groups all cancelation actions in the reservation workflow
 */
public class WFCancel extends WFCommon {
    /**
     * Try to cancel a reservation.
     */
    @AllowRoles
    @InjectContext
    public static Result cancelReservation(int reservationId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        ReservationDAO dao = context.getReservationDAO();
        ReservationHeader reservation = dao.getReservationHeader(reservationId);

        if (WorkflowAction.CANCEL.isForbiddenForCurrentUser(reservation)) {
            flash("danger", "U kan deze reservatie niet (meer) annuleren");
            return redirectToDetails(reservationId);
        }

        // one special case: already accepted and in the past (and not owner or admin)
        if (reservation.getStatus() == ReservationStatus.ACCEPTED
                && reservation.getFrom().isAfter(LocalDateTime.now())
                && !WorkflowRole.OWNER.isCurrentRoleFor(reservation)
                && !WorkflowRole.ADMIN.isCurrentRoleFor(reservation)
                ) {
            flash("danger",
                    "Deze reservatie was reeds goedgekeurd! " +
                            "Je moet daarom verplicht een reden opgeven voor de annulatie");
            return redirect(routes.WFCancel.cancelAccepted(reservationId));
        } else {
            dao.updateReservationStatus(reservationId, ReservationStatus.CANCELLED);
            return redirectToDetails(reservationId);
        }
    }

    /**
     * Show a form for cancelling a reservation which was already accepted
     */
    @AllowRoles
    @InjectContext
    public static Result cancelAccepted(int reservationId) {
        TripAndCar trip = DataAccess.getInjectedContext().getTripDAO().getTripAndCar(reservationId, false);
        return ok(cancelaccepted.render(Form.form(CancelData.class), trip));
    }

    /**
     * Process the results of {@link #cancelAccepted}
     */
    @AllowRoles
    @InjectContext
    public static Result doCancelAccepted(int reservationId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        TripAndCar trip = context.getTripDAO().getTripAndCar(reservationId, false);

        if (CurrentUser.isNot(trip.getDriverId()) || trip.getStatus() != ReservationStatus.ACCEPTED) {
            return badRequest(); // should not happen
        }

        Form<CancelData> form = Form.form(CancelData.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(cancelaccepted.render(form, trip));
        } else {
            String comments = form.get().remarks;
            context.getReservationDAO().updateReservationStatus(reservationId, ReservationStatus.CANCELLED, comments);
            trip.setMessage(comments);
            Notifier.sendReservationCancelled(
                    context.getUserDAO().getUserHeader(trip.getOwnerId()),
                    trip
            );
            return redirectToDetails(reservationId);
        }
    }

    /**
     * Show a screen given the option between cancelling a reservation or marking it
     * explicitely as not taken place
     */
    @AllowRoles({UserRole.CAR_OWNER, UserRole.RESERVATION_ADMIN})
    @InjectContext
    public static Result cancelLate(int reservationId) {
        TripAndCar trip = DataAccess.getInjectedContext().getTripDAO().getTripAndCar(reservationId, false);
        if (WorkflowAction.CANCEL_LATE.isForbiddenForCurrentUser(trip)) {
            flash("warning", "De rit kan niet (meer) worden geannuleerd");
            return redirectToDetails(reservationId);
        } else {
            return ok(latecancel.render(Form.form(CancelData.class), trip));
        }
    }


    /**
     * Processes the results of {@link #cancelLate(int)}
     */
    @AllowRoles({UserRole.CAR_OWNER, UserRole.RESERVATION_ADMIN})
    @InjectContext
    public static Result doCancelLate(int reservationId, boolean soft) {

        DataAccessContext context = DataAccess.getInjectedContext();
        TripAndCar trip = context.getTripDAO().getTripAndCar(reservationId, false);
        if (WorkflowAction.CANCEL_LATE.isForbiddenForCurrentUser(trip)) {
            return badRequest();
        }
        ReservationDAO rdao = context.getReservationDAO();
        if (soft) {
            rdao.updateReservationStatus(reservationId, ReservationStatus.CANCELLED);
            return redirectToDetails(reservationId);
            // same as a normal cancel
        } else {
            Form<CancelData> form = Form.form(CancelData.class).bindFromRequest();
            if (form.hasErrors()) {
                return badRequest(latecancel.render(form, trip));
            } else {
                String comments = form.get().remarks;
                rdao.updateReservationStatus(reservationId, ReservationStatus.CANCELLED_LATE, comments);
                trip.setMessage(comments);
                Notifier.sendLateCancel(context.getUserDAO().getUserHeader(trip.getDriverId()), trip);
                return redirectToDetails(reservationId);
            }
        }
    }

    public static class CancelData {
        public String remarks;

        public List<ValidationError> validate() {
            if (Strings.isNullOrEmpty(remarks)) { // TODO: isNullOrBlank
                return Collections.singletonList(
                        new ValidationError("remarks", "Je moet een reden opgeven.")
                );
            } else {
                return null;
            }
        }
    }
}
