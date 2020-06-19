/* Approvals.java
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
import be.ugent.degage.db.FilterField;
import be.ugent.degage.db.dao.*;
import be.ugent.degage.db.models.*;
import com.google.common.base.Strings;
import controllers.util.Pagination;
import controllers.util.UserpickerData;
import data.Referrer;
import db.CurrentUser;
import db.DataAccess;
import db.InjectContext;
import notifiers.Notifier;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.approvals.*;

import java.util.*;

/**
 */
public class Approvals extends Controller {


    private static Iterable<String> checkApprovalConditions(int userId, UserDAO udao, MembershipDAO mdao, FileDAO fdao) {
        // TODO: minimize data transfer by using a query with true/false results
        User user = udao.getUser(userId); // gets the full user instead of small cached one

        Collection<String> errors = new ArrayList<>();
        if (Strings.isNullOrEmpty(user.getAddressDomicile().getStreet())) {
            errors.add("Domicilieadres ontbreekt.");
        }
        if (Strings.isNullOrEmpty(user.getAddressResidence().getStreet())) {
            errors.add("Verblijfsadres ontbreekt.");
        }
        if (user.getIdentityId() == null) {
            errors.add("Identiteitsgegevens ontbreken.");
        } else if (!fdao.hasUserFile(userId, FileDAO.UserFileType.ID)) {
            errors.add("Scan identiteitskaart ontbreekt");
        }
        if (user.getLicense() == null) {
            errors.add("Rijbewijs ontbreekt.");
        } else if (!fdao.hasUserFile(userId, FileDAO.UserFileType.LICENSE)) {
            errors.add("Ingescand rijbewijs ontbreekt");
        }
        if (user.getCellPhone() == null && user.getPhone() == null) {
            errors.add("Telefoon/GSM ontbreekt.");
        }
        Membership membership = mdao.getMembership(userId);
        if (membership.getDeposit() == null) {
            errors.add("Waarborg nog niet betaald.");
        }
        if (membership.getContractDate() == null) {
            errors.add("Contract nog niet getekend");
        }
        if (membership.getFee() == null) {
            errors.add("Lidgeld nog niet betaald.");
        }
        return errors;
    }

    public static class RequestApprovalData {
        public String message;
        public boolean acceptsTerms;

        public List<ValidationError> validate() {
            if (!acceptsTerms) {
                return Collections.singletonList(
                        new ValidationError("acceptsTerms", "Gelieve de algemene voorwaarden te accepteren")
                );
            } else {
                return null;
            }
        }
    }

    /**
     * Page which allows the user to request to become a full member
     */
    @AllowRoles({})
    @InjectContext
    public static Result requestApproval() {
        if (CurrentUser.hasFullStatus()) {
            flash("warning", "Je bent reeds een volwaardig lid.");
            return redirect(routes.Application.index());
        } else {
            DataAccessContext context = DataAccess.getInjectedContext();
            if (context.getApprovalDAO().membershipRequested(CurrentUser.getId())) {
                flash("warning", "We nemen op dit moment je aanvraag om lid te worden in beschouwing.");
                return redirect(routes.Application.index());
            } else if (context.getInfoSessionDAO().getInfoSessionWherePresent(CurrentUser.getId()) == 0) {
                flash("danger", "Je bent nog niet naar een infosessie geweest en kan dus nog geen lid worden.");
                return redirect(routes.InfoSessions.showUpcomingSessions());
            } else {
                return ok(approvalrequest.render(
                        checkApprovalConditions(CurrentUser.getId(), context.getUserDAO(), context.getMembershipDAO(), context.getFileDAO()),
                        Form.form(RequestApprovalData.class))
                );
            }
        }
    }

    /**
     * Handles a request for full membership
     */
    @AllowRoles({})
    @InjectContext
    public static Result requestApprovalPost() {
        Form<RequestApprovalData> form = Form.form(RequestApprovalData.class).bindFromRequest();
        DataAccessContext context = DataAccess.getInjectedContext();
        if (form.hasErrors()) {
            return badRequest(approvalrequest.render(
                    checkApprovalConditions(CurrentUser.getId(), context.getUserDAO(), context.getMembershipDAO(), context.getFileDAO()),
                    form
                    )
            );
        } else {
            int isp = context.getInfoSessionDAO().getInfoSessionWherePresent(CurrentUser.getId());
            if (isp == 0) {
                flash("danger", "Je bent nog niet naar een infosessie geweest en kan dus nog geen lid worden.");
                return redirect(routes.InfoSessions.showUpcomingSessions());
            } else {
                context.getApprovalDAO().createApproval(CurrentUser.getId(), isp, form.get().message);
                flash("success", "Bedankt voor de interesse. We nemen je aanvraag tot lidmaatschap in beraad.");
                return redirect(routes.Application.index());
            }
        }
    }

    // must be used with injected context - used in contracts menu
    public static int getNrOfPendingApprovals() {
        return DataAccess.getInjectedContext().getApprovalDAO().getNrOfPendingApprovals();
    }

    public static Referrer REF_APPROVALS =
            Referrer.register ("Goedkeuring gebruikers", routes.Approvals.pendingApprovalList(0), "APPS");

    @AllowRoles({UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result pendingApprovalList(int tab) {
        return ok(approvaltabs.render(tab));
    }

    /**
     * Shows a list of all approvals in the database.
     */
    @AllowRoles({UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result pendingApprovalListPaged(int page, int pageSize, int ascInt, String orderBy, String searchString) {
        // searchString not used
        DataAccessContext context = DataAccess.getInjectedContext();
        ApprovalDAO dao = context.getApprovalDAO();
        FilterField field = FilterField.stringToField(orderBy, FilterField.USER_NAME);
        boolean asc = Pagination.parseBoolean(ascInt);
        searchString = searchString.substring(searchString.lastIndexOf('=') + 1);
        MembershipStatus membershipStatus = MembershipStatus.valueOf(searchString);
        Page<ApprovalListInfo> approvals = dao.getApprovals(field, asc, page, pageSize, membershipStatus);
        return ok(approvalpage.render(approvals));
    }

    /**
     * Show the page that assigns a contract administrator for the given approval record.
     */
    @AllowRoles({UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result approvalAdmin(int approvalId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        Approval ap = context.getApprovalDAO().getApproval(approvalId);
        EnrollementStatus status = EnrollementStatus.ABSENT;
        if (ap.getSession() != null) {
            status = context.getInfoSessionDAO().getUserEnrollmentStatus(ap.getSession().getId(), ap.getUserId());
        }
        UserpickerData data = new UserpickerData();
        UserDAO userDAO = context.getUserDAO();
        data.populate(userDAO.getUserHeader(ap.getAdminId())); // TODO: only id and fullname
        return ok(setcontractadmin.render(ap, userDAO.getUserHeader(ap.getUserId()), status,
                Form.form(UserpickerData.class).fill(data)));
    }

    /**
     * Assigns a contract administrator for the given approval record.
     */
    @AllowRoles({UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result approvalAdminPost(int id) {
        DataAccessContext context = DataAccess.getInjectedContext();
        ApprovalDAO adao = context.getApprovalDAO();
        Approval app = adao.getApproval(id);
        Form<UserpickerData> form = Form.form(UserpickerData.class).bindFromRequest();
        UserDAO udao = context.getUserDAO();
        UserHeader user = udao.getUserHeader(app.getUserId());
        if (form.hasErrors()) {
            // TODO: code in common with approvalAdmin
            EnrollementStatus status = EnrollementStatus.ABSENT;
            if (app.getSession() != null) {
                InfoSessionDAO idao = context.getInfoSessionDAO();
                status = idao.getUserEnrollmentStatus(app.getSession().getId(), app.getUserId());
            }
            return ok(setcontractadmin.render(app, user, status, form));
        } else {
            int userId = form.get().userId;

            UserHeader contractManager = udao.getUserHeader(userId);

            if (CurrentUser.hasRole(UserRole.INFOSESSION_ADMIN)) {
                adao.setApprovalAdmin(id, userId);
                Notifier.sendContractManagerAssignedMail(user, contractManager);
                flash("success", "De aanvraag werd successvol toegewezen aan " + contractManager);
                return redirect(routes.Approvals.pendingApprovalList(1));
            } else {
                flash("danger", contractManager + " heeft geen infosessie beheerdersrechten.");
                return redirect(routes.Approvals.approvalAdmin(id));
            }
        }
    }

    public static class ApprovalAdminData {
        public String message;
        public String action;
    }

    // used in injected context
    private static Result approvalForm(Approval ap, DataAccessContext context, Form<ApprovalAdminData> form, boolean bad) {
        EnrollementStatus status = EnrollementStatus.ABSENT;
        int userId = ap.getUserId();
        if (ap.getSession() != null) {
            InfoSessionDAO idao = context.getInfoSessionDAO();
            status = idao.getUserEnrollmentStatus(ap.getSession().getId(), userId);
        }
        UserHeader user = context.getUserDAO().getUserHeader(userId);
        Iterable<String> reasons = checkApprovalConditions(userId, context.getUserDAO(), context.getMembershipDAO(), context.getFileDAO());
        if (!bad) {
            return ok(approvaladmin.render(ap, user, status, reasons, form));
        } else {
            return badRequest(approvaladmin.render(ap, user, status, reasons, form));
        }
    }

    /**
     * Show the page that allows approval or rejection of membership
     */
    @AllowRoles({UserRole.CONTRACT_ADMIN, UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result approvalApproveReject(int approvalId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        ApprovalDAO dao = context.getApprovalDAO();
        Approval ap = dao.getApproval(approvalId);
        if (ap.getAdminId() == 0) {
            flash("danger", "Gelieve eerst een contractverantwoordelijke op te geven.");
            return redirect(routes.Approvals.approvalAdmin(approvalId));
        } else if (CurrentUser.hasRole(UserRole.PROFILE_ADMIN) || CurrentUser.is(ap.getAdminId())) {
            ApprovalAdminData data = new ApprovalAdminData();
            data.message = ap.getAdminMessage();
            return approvalForm(ap, context, Form.form(ApprovalAdminData.class).fill(data), false);
        } else {
            return badRequest(); // hacker?
        }
    }

    /**
     * Process approval or rejection of membership
     */
    @AllowRoles({UserRole.CONTRACT_ADMIN, UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result approvalApproveRejectPost(int approvalId) {
        Form<ApprovalAdminData> form = Form.form(ApprovalAdminData.class).bindFromRequest();
        DataAccessContext context = DataAccess.getInjectedContext();
        ApprovalDAO dao = context.getApprovalDAO();
        Approval ap = dao.getApproval(approvalId);

        if (form.hasErrors()) {
            return approvalForm(ap, context, form, true);
        }

        if (!CurrentUser.hasRole(UserRole.PROFILE_ADMIN) && CurrentUser.isNot(ap.getAdminId())) {
            badRequest(); // hacker?
        }

        ApprovalAdminData data = form.get();
        UserDAO udao = context.getUserDAO();
        if ("reject".equals(data.action)) {
            //TODO Warning, if status was not pending, possibly have to remove user roles? But should not be called in that situation
            dao.setApprovalStatus(approvalId, MembershipStatus.DENIED, data.message);
            Notifier.sendMembershipRejected(udao.getUserHeader(ap.getUserId()), data.message);
            flash("success", "De aanvraag werd afgekeurd.");
            return redirect(routes.Approvals.pendingApprovalList(2));
        } else {
            boolean owner = false;
            if ("owner".equals(data.action)) {
                owner = true;
            } else if (!"accept".equals(data.action)) {
                return badRequest();
            }
            dao.setApprovalStatus(approvalId, MembershipStatus.ACCEPTED, data.message);
            int userId = ap.getUserId();
            udao.makeUserFull(userId);

            // Add the new user roles
            UserRoleDAO roleDao = context.getUserRoleDAO();
            if (owner) {
                roleDao.addUserRole(userId, UserRole.CAR_OWNER);
            }
            roleDao.addUserRole(userId, UserRole.CAR_USER); // always
            Notifier.sendMembershipApproved(udao.getUserHeader(userId), data.message);
            flash("success", "De gebruiker is volwaardig lid geworden.");

            return redirect(routes.Approvals.pendingApprovalList(1));
        }
    }
}
