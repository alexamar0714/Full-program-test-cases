/* Calendars.java
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

import be.ugent.degage.db.Filter;
import be.ugent.degage.db.FilterField;
import be.ugent.degage.db.dao.CarDAO;
import be.ugent.degage.db.dao.UserDAO;
import be.ugent.degage.db.dao.ReservationDAO;
import be.ugent.degage.db.models.User;
import be.ugent.degage.db.models.CarHeaderLong;
import be.ugent.degage.db.models.ReservationHeader;
import be.ugent.degage.db.models.Page;
import controllers.util.OverviewLine;
import controllers.util.Pagination;
import db.CurrentUser;
import db.DataAccess;
import db.InjectContext;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.calendars.*;
import views.html.snippets.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Controller responsible to display overviews of availability of cars preliminary to reservations.
 */
public class Calendars extends Controller {

    /**
     * Show an initial page with a reservation search form. This page uses Ajax to show all
     * cars available for reservation in a certain period. The view is a list, not a calendar.
     */
    @AllowRoles
    @InjectContext
    public static Result index() {
        return ok(start.render());
    }

    /**
     * Ajax call as a result of the 'search' button in  start.
     */
    @AllowRoles
    @InjectContext
    public static Result listAvailableCarsPage(int page, int pageSize, int ascInt, String orderBy, String searchString) {

        FilterField field = FilterField.stringToField(orderBy, FilterField.NAME);

        boolean asc = Pagination.parseBoolean(ascInt);

        Filter filter = Pagination.parseFilter(searchString);

        String validationError = null;
        String fromString = filter.getValue(FilterField.FROM);
        String untilString = filter.getValue(FilterField.UNTIL);

        //System.err.printf("FromString = [%s], untilstring = [%s]\n", fromString, untilString);
        try {
            if (fromString.isEmpty() || untilString.isEmpty()) {
                validationError = "Gelieve beide tijdstippen in te vullen";
            }  else {
                LocalDateTime from = Utils.toLocalDateTime(fromString);
                LocalDateTime until = Utils.toLocalDateTime(untilString);
                if (!until.isAfter(from)) {
                    validationError = "Het einde van de periode moet na het begin van de periode liggen";
                }
            }
        } catch (IllegalArgumentException ex) {
            validationError = "Gelieve geldige datums in te geven";
        }
        if (validationError != null) {
            return ok (errortablerow.render(validationError));
        }

        CarDAO dao = DataAccess.getInjectedContext().getCarDAO();
        Page<CarHeaderLong> listOfCars = dao.listActiveCars(field, asc, page, pageSize, filter, CurrentUser.getId());
        return ok(availablecarspage.render(listOfCars, fromString, untilString));
    }

    /**
     * Shows a list of cars with restricted information in order to make a reservation
     */
    @AllowRoles
    @InjectContext
    public static Result showCarsForReservation() {
        UserDAO userDAO = DataAccess.getInjectedContext().getUserDAO();
        User user = userDAO.getUser(CurrentUser.getId());
        return ok(carsforreservation.render(
                DataAccess.getInjectedContext().getCarDAO().listAllActiveCars(CurrentUser.getId()), user)
        );
    }

    public static class DateData {
        @Constraints.Required
        public String date;

    }

    public static class CarDateData extends DateData {
        public int carId;

        @Constraints.Required
        public String carIdAsString;

        public String period; // week or month
    }


    /**
     * Shows a reservation search form containing a car and a date. Results in a calendar overview
     * for the given car, starting at the given date.
     */
    @AllowRoles
    @InjectContext
    public static Result startCar () {
        CarDateData data = new CarDateData();
        data.date = Utils.toDateString(LocalDate.now());
        return ok(startcar.render(Form.form(CarDateData.class).fill(data)));
    }

    /**
     * Shows a reservation search form containing a car and a date. Results in a calendar overview
     * for the given car, starting at the given date.
     */
    @AllowRoles
    @InjectContext
    public static Result availabilityCar (int carId, String dateString) {
        LocalDate date = dateString == null ? LocalDate.now() : Utils.toLocalDate(dateString);
        CarDateData data = new CarDateData();
        data.carId = carId;
        // data.carIdAsString = carName;
        data.date = Utils.toDateString(date);
        return ok(overviewForCarGrid.render(new OverviewForCar(carId, "", data.date, getOverviewLines(data)), 10, null, null));
        // return overviewPanel("titelke", "fa-calendar", getOverviewLines(data), 10);
    }

    /**
     * Show an overview of reservations for a specific car during a certain week.
     */
    @AllowRoles
    @InjectContext
    public static Result overviewCarPost () {
        Form<CarDateData> form = Form.form(CarDateData.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(startcar.render(form));
        } else {
            return overviewPanel(form);
        }
    }

    // /**
    //  * Show an overview raster per day of reservations for a specific car during a certain week.
    //  */
    // @AllowRoles
    // @InjectContext
    // public static Result overviewPanelCarPost () {
    //     Form<CarDateData> form = Form.form(CarDateData.class).bindFromRequest();
    //     if (form.hasErrors()) {
    //         return badRequest(startcar.render(form));
    //     } else {
    //         return overviewPanel(form);
    //     }
    // }

    /**
     * Same as overviewCarPost, but with car id and car name already filled in and day of today
     */
    @AllowRoles
    @InjectContext
    public static Result indexWithCar(String carName, int carId) {
        CarDateData data = new CarDateData();
        data.carId = carId;
        data.carIdAsString = carName;
        data.date = Utils.toDateString(LocalDate.now());
        return overviewCar(Form.form(CarDateData.class).fill(data));
    }

    /**
     * Show a calendar overview of reservations during a certain day. Do not show cars
     * that are not available
     */
    @AllowRoles
    @InjectContext
    public static Result overview (String dateString) {
        LocalDate date = dateString == null ? LocalDate.now() : Utils.toLocalDate(dateString);
        LocalDateTime from = date.atTime(OverviewLine.START_HOUR, 0);
        LocalDateTime until = date.plusDays(OverviewLine.END_HOUR/24).atTime(OverviewLine.END_HOUR % 24, 0);

        ReservationDAO dao = DataAccess.getInjectedContext().getReservationDAO();
        Iterable<ReservationDAO.CRInfo> list = dao.listCRInfo(from, until, CurrentUser.getId());
        Collection<OverviewLine> lines = new ArrayList<>();
        for (ReservationDAO.CRInfo crInfo : list) {
            OverviewLine line = new OverviewLine();
            line.populate(crInfo, date);
            if (line.hasFree()) {
                 lines.add(line);
            }
        }

        DateData data = new DateData();
        data.date = Utils.toDateString(date); // cannot use dateString!, might be empty
        return ok(overview.render(
                Form.form(DateData.class).fill(data),
                lines, date,
                date.minusDays(1L), date.plusDays(1L)
        ));

    }

    /**
     * Same as {@link #overview} but gets the date from a POST instead of a GET.
     */
    @AllowRoles
    @InjectContext
    public static Result overviewPost () {
        // TODO: use method GET in the related form and remove this method
        DateData data = Form.form(DateData.class).bindFromRequest().get();
        return overview(data.date);
    }

    private static Result overviewCar(Form<CarDateData> form) {
        return ok(overviewcar.render(form, new OverviewForCar(form)));
    }

    private static Result overviewForCarPanel(Form<CarDateData> form) {
        return ok(overviewForCarPanel.render("Beschikbare perioden", "fa-calendar", new OverviewForCar(form), 10, null, null));
    }

    private static Result overviewPanel(Form<CarDateData> form) {
        // return ok(overviewpanel.render("Beschikbare perioden", "fa-calendar", (new OverviewForCar(form)).lines, 10, null, null));
        return ok(overviewcar.render(form, new OverviewForCar(form)));
    }

    public static class OverviewForCar {
        public int id;
        public String name;
        public String date;
        public String nextWeek;
        public String previousWeek;
        public Iterable<OverviewLine> lines;

        public OverviewForCar(int id, String name, Iterable<OverviewLine> lines) {
            this.id = id;
            this.name = name;
            this.lines = lines;
        }

        public OverviewForCar(int id, String name, String date, Iterable<OverviewLine> lines) {
            this.id = id;
            this.name = name;
            this.lines = lines;
            this.date = date;
            LocalDate localDate = Utils.toLocalDate(date);
            nextWeek = Utils.toDateString(localDate.plusDays(7));
            previousWeek = Utils.toDateString(localDate.plusDays(-7));
        }

        public OverviewForCar(Form<CarDateData> form) {
            this.id = form.get().carId;
            this.name = form.get().carIdAsString;
            this.lines = getOverviewLines(form.get());
            this.date = form.get().date;
            LocalDate localDate = Utils.toLocalDate(date);
            nextWeek = Utils.toDateString(localDate.plusDays(7));
            previousWeek = Utils.toDateString(localDate.plusDays(-7));
        }
    }

    public static OverviewForCar getOverviewForCar(CarDateData data) {
        return new OverviewForCar(data.carId, data.carIdAsString, data.date, getOverviewLines(data));
    }

    public static Collection<OverviewLine> getOverviewLines(CarDateData data) {
        LocalDateTime from = Utils.toLocalDate(data.date).atStartOfDay();
        LocalDateTime until;
        if ("month".equals(data.period)) {
            until = from.plusMonths(1);
        } else { // week
            until = from.plusDays(7);
        }
        Iterable<ReservationHeader> reservations =
                DataAccess.getInjectedContext().getReservationDAO().
                        listReservationsForCarInPeriod(data.carId, from, until);
        Collection<OverviewLine> lines = new ArrayList<>();
        LocalDate fromDate = from.toLocalDate();
        LocalDate untilDate = until.toLocalDate();
        for (LocalDate date = fromDate; date.isBefore(untilDate); date = date.plusDays(1)) {
            OverviewLine line = new OverviewLine();
            line.populate(reservations, data.carId, date);
            lines.add(line);
        }
        return lines;
    }

    @AllowRoles
    @InjectContext
    public static Result carPreferences () {
        return ok();
    }

    @AllowRoles
    @InjectContext
    public static Result doCarPreferences () {
        return redirect(routes.Calendars.showCarsForReservation());
    }


}

