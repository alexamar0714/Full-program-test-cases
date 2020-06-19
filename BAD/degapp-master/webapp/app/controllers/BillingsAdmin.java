/* BillingsAdmin.java
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
import be.ugent.degage.db.dao.BillingAdmDAO;
import be.ugent.degage.db.dao.BillingDAO;
import be.ugent.degage.db.dao.CheckDAO;
import be.ugent.degage.db.models.Billing;
import be.ugent.degage.db.models.BillingStatus;
import be.ugent.degage.db.models.KmPrice;
import be.ugent.degage.db.models.UserRole;
import db.DataAccess;
import db.InjectContext;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.billingadm.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Actions related to billing (management).
 */
public class BillingsAdmin extends Controller {

    public static class CarData {
        public int carId;

        public String carIdAsString;
    }


    /**
     * Show anomalies for the given billing and for all cars
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result showAnomalies(int billingId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        CheckDAO dao = context.getCheckDAO();
        return ok(anomalies.render(
                Form.form(CarData.class),
                context.getBillingDAO().getBilling(billingId),
                dao.getTripAnomalies(billingId, 0),
                dao.getRefuelAnomalies(billingId, 0)
        ));
    }

    /**
     * Show anomalies for the given billing and restricted to a selected car
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result doShowAnomalies(int billingId) {
        Form<CarData> form = Form.form(CarData.class).bindFromRequest();
        DataAccessContext context = DataAccess.getInjectedContext();
        if (form.hasErrors()) {
            return showAnomalies(billingId);
        } else {
            CheckDAO dao = context.getCheckDAO();
            return ok(anomalies.render(
                    form,
                    context.getBillingDAO().getBilling(billingId),
                    dao.getTripAnomalies(billingId, form.get().carId),
                    dao.getRefuelAnomalies(billingId, form.get().carId)
            ));
        }
    }

    private static boolean allArchived(Iterable<Billing> list) {
        for (Billing billing : list) {
            if (billing.getStatus() != BillingStatus.ARCHIVED) {
                return false;
            }
        }
        return true;
    }

    public static class Data {
        @Constraints.Required
        public String description;

        @Constraints.Required
        public String prefix;

        @Constraints.Required
        public LocalDate from;

        @Constraints.Required
        public LocalDate until;

        public List<ValidationError> validate() {
            // TODO: avoid copy from e.g. ReservationData
            if (from.isBefore(until)) {
                return null;
            } else {
                return Collections.singletonList(
                        new ValidationError("until", "Het einde van de periode moet na het begin van de periode liggen")
                );
            }
        }
    }

    /**
     * Produce a list of all billings for administration purposes.
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result listAll() {
        Iterable<Billing> list = DataAccess.getInjectedContext().getBillingDAO().listAllBillings();
        Form<Data> form = null;
        if (allArchived(list)) {
            form = Form.form(Data.class);
        }
        return ok(listAll.render(list, form));
    }

    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result archive(int billingId) {
        DataAccess.getInjectedContext().getBillingAdmDAO().archive(billingId);
        return redirect(routes.BillingsAdmin.listAll());
    }

    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result doCreate() {
        Form<Data> form = Form.form(Data.class).bindFromRequest();
        if (form.hasErrors()) {
            Iterable<Billing> list = DataAccess.getInjectedContext().getBillingDAO().listAllBillings();
            return badRequest(listAll.render(list, form));
        } else {
            Data data = form.get();
            DataAccess.getInjectedContext().getBillingAdmDAO().createBilling(
                    data.description,
                    data.prefix,
                    data.from,
                    data.until.plusDays(1)
            );
            return redirect(routes.BillingsAdmin.listAll());
        }
    }

    public static class CarsBilledData {
        public Map<Integer, Boolean> included;

        public CarsBilledData() {
            included = new HashMap<>();
        }

        public CarsBilledData(Iterable<BillingAdmDAO.CarInfo> list) {
            this();
            for (BillingAdmDAO.CarInfo info : list) {
                if (!info.nodata && !info.incomplete && info.included) {
                    included.put(info.carId, true);
                }
            }
        }

    }

    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result selectCars(int billingId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        List<BillingAdmDAO.CarInfo> infoList = context.getBillingAdmDAO().listCarBillingInfo(billingId);
        Form<CarsBilledData> form = Form.form(CarsBilledData.class).fill(new CarsBilledData(infoList));
        return ok(selectcars.render(
                form,
                context.getBillingDAO().getBilling(billingId),
                Utils.splitList(infoList, 3)
        ));
    }

    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result doSelectCars(int billingId) {
        Form<CarsBilledData> form = Form.form(CarsBilledData.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(); // this should not happen
        } else {
            BillingAdmDAO dao = DataAccess.getInjectedContext().getBillingAdmDAO();
            dao.updateCarsBilled(
                    billingId,
                    form.get().included.keySet()
            );
            dao.updateToPreparing(billingId);
            return redirect(routes.BillingsAdmin.listAll());
        }
    }

    /**
     * Show the page which can launch a simulation
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result simulation(int billingId) {
        Billing billing = DataAccess.getInjectedContext().getBillingDAO().getBilling(billingId);
        if (billing.getStatus() == BillingStatus.PREPARING || billing.getStatus() == BillingStatus.SIMULATION) {
            return ok(showSimulation.render(billing));
        } else {
            return badRequest(); // hacking?
        }
    }

    /**
     * Start simulation
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result doSimulation(int billingId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        Billing billing = context.getBillingDAO().getBilling(billingId);
        if (billing.getStatus() == BillingStatus.PREPARING || billing.getStatus() == BillingStatus.SIMULATION) {
            context.getBillingAdmDAO().computeSimulation(billingId);
            return redirect(routes.BillingsAdmin.listAll());
        } else {
            return badRequest(); // hacking?
        }
    }

    /**
     * Show the page which finalizes the user invoices
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result userInvoices(int billingId) {
        Billing billing = DataAccess.getInjectedContext().getBillingDAO().getBilling(billingId);
        if (billing.getStatus() == BillingStatus.SIMULATION) {
            return ok(showUserInvoices.render(billing));
        } else {
            return badRequest(); // hacking?
        }
    }

    /**
     * Start simulation
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result doUserInvoices(int billingId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        Billing billing = context.getBillingDAO().getBilling(billingId);
        if (billing.getStatus() == BillingStatus.SIMULATION) {
            context.getBillingAdmDAO().computeUserInvoices(billingId);
            return redirect(routes.BillingsAdmin.listAll());
        } else {
            return badRequest(); // hacking?
        }
    }

    /**
     * Show the page which finalizes the car invoices
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result carInvoices(int billingId) {
        Billing billing = DataAccess.getInjectedContext().getBillingDAO().getBilling(billingId);
        if (billing.getStatus() == BillingStatus.USERS_DONE) {
            return ok(showCarInvoices.render(billing));
        } else {
            return badRequest(); // hacking?
        }
    }

    /**
     * Start simulation
     */
    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result doCarInvoices(int billingId) {

        DataAccessContext context = DataAccess.getInjectedContext();
        Billing billing = context.getBillingDAO().getBilling(billingId);
        if (billing.getStatus() == BillingStatus.USERS_DONE) {
            context.getBillingAdmDAO().computeCarInvoices(billingId);
            return redirect(routes.BillingsAdmin.listAll());
        } else {
            return badRequest(); // hacking?
        }
    }

    public static class PriceDataElement {
        public int kilometer;

        public int eurocents;
    }

    public static class PriceData {

        public static final int MAX_SIZE = 6;

        public PriceDataElement[] elements;

        public PriceData() {
            elements = new PriceDataElement[MAX_SIZE];
            for (int i = 0; i < MAX_SIZE; i++) {
                elements[i] = new PriceDataElement();
            }
        }

        public List<ValidationError> validate () {
            List<ValidationError> list = new ArrayList<>();
            //
            if (elements[0].kilometer != 1) {
                list.add(new ValidationError("elements[0].kilometer", "Eerste kilometergrens moet 1 zijn"));
            }
            //
            for (int i=0; i < MAX_SIZE; i++) {
                 if (elements[i].kilometer == 0 && elements[i].eurocents != 0) {
                    list.add(new ValidationError("elements["+i+"].kilometer", "Beide velden invullen"));
                } else if (elements[i].kilometer != 0 && elements[i].eurocents == 0) {
                    list.add(new ValidationError("elements["+i+"].eurocents", "Beide velden invullen"));
                }
            }
            if (!list.isEmpty()) {
                return list;
            }
            //
            int size = 1;
            while (size < MAX_SIZE && elements[size].kilometer != 0) {
                size ++;
            }
            for (int i = size; i < MAX_SIZE; i++) {
                if (elements[i].kilometer != 0) {
                    list.add(new ValidationError("elements["+i+"].kilometer", "Ingevulde velden moeten aaneensluiten"));
                }
            }
            if (!list.isEmpty()) {
                return list;
            }

            for (int i = 1; i < size; i++) {
                if (elements[i-1].kilometer >= elements[i].kilometer) {
                    list.add(new ValidationError("elements["+i+"].kilometer", "Kilometergrenzen moeten in stijgende volgorde staan"));
                }
            }
            for (int i = 0; i < size; i++) {
                if (elements[i].eurocents <= 0) {
                    list.add(new ValidationError("elements["+i+"].eurocents", "Bedrag moet positief zijn"));
                }
            }

            return list.isEmpty() ? null : list;
        }

        public void fill (Iterable<KmPrice> list) {
            int c = 0;
            for (KmPrice kmPrice : list) {
                elements[c].kilometer = kmPrice.getFromKm();
                elements[c].eurocents = kmPrice.getEurocents();
                c++;
            }
        }

        public Iterable<KmPrice> export () {
            Collection<KmPrice> list = new ArrayList<>();
            int last = 0;
            int i = 0;
            while (i < MAX_SIZE && elements[i].kilometer != 0) {
                list.add(new KmPrice(elements[i].kilometer, elements[i].eurocents, elements[i].eurocents - last));
                last = elements[i].eurocents;
                i ++;
            }
            return list;
        }

    }

    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result showPrices(int billingId) {
        BillingDAO dao = DataAccess.getInjectedContext().getBillingDAO();
        Billing billing = dao.getBilling(billingId);
        if (billing.getStatus() == BillingStatus.CREATED || billing.getStatus() == BillingStatus.PREPARING || billing.getStatus() == BillingStatus.SIMULATION) {
            PriceData data = new PriceData();
            data.fill(dao.listKmPrices(billingId));
            Form<PriceData> form = Form.form(PriceData.class).fill(data);
            return ok(editPrices.render(billing, form));
        } else {  // TODO: show prices only
            return badRequest(); // hacking?
        }
    }

    @InjectContext
    @AllowRoles(UserRole.SUPER_USER)
    public static Result doEditPrices(int billingId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        BillingDAO dao = context.getBillingDAO();
        Billing billing = dao.getBilling(billingId);
        if (billing.getStatus() == BillingStatus.CREATED || billing.getStatus() == BillingStatus.PREPARING || billing.getStatus() == BillingStatus.SIMULATION) {
            Form<PriceData> form = Form.form(PriceData.class).bindFromRequest();
            if (form.hasErrors()) {
                return badRequest(editPrices.render(billing, form));
            } else {
                context.getBillingAdmDAO().updatePricing(billingId, form.get().export());
                return redirect(routes.BillingsAdmin.listAll());
            }
        }
        else {  // TODO: show prices only
            return badRequest(); // hacking?
        }
    }

}
