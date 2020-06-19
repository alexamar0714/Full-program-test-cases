/* Users.java
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
import be.ugent.degage.db.dao.UserDAO;
import be.ugent.degage.db.models.Page;
import be.ugent.degage.db.models.User;
import be.ugent.degage.db.models.UserHeader;
import be.ugent.degage.db.models.UserRole;
import controllers.util.Pagination;
import data.Referrer;
import db.CurrentUser;
import db.DataAccess;
import db.InjectContext;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.users.users;
import views.html.users.userspage;
import views.html.users.usersWithTabs;

import java.util.Set;

/**
 * Created by HannesM on 27/03/14.
 */
public class Users extends Controller {

    public static Referrer REF_USERS =
            Referrer.register ("Gebruikers", routes.Users.showUsersWithTabs(0), "USRS");


    /**
     * @return The users index-page with all users
     */
    @AllowRoles({UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result showUsers() {
        return ok(users.render());
    }

    /**
     * @return The users index-page with all users
     */
    @AllowRoles({UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result showUsersWithTabs(int tab) {
        return ok(usersWithTabs.render(tab));
    }

    /**
     * @param page         The page in the userlists
     * @param ascInt       An integer representing ascending (1) or descending (0)
     * @param orderBy      A field representing the field to order on
     * @param searchString A string witth form field1:value1,field2:value2 representing the fields to filter on
     * @return A partial page with a table of users of the corresponding page
     */
    @AllowRoles({UserRole.CAR_USER, UserRole.PROFILE_ADMIN})
    @InjectContext
    public static Result showUsersPage(int page, int pageSize, int ascInt, String orderBy, String searchString) {
        // TODO: orderBy not as String-argument?
        FilterField field = FilterField.stringToField(orderBy, FilterField.USER_NAME);

        boolean asc = Pagination.parseBoolean(ascInt);
        Filter filter = Pagination.parseFilter(searchString);
        UserDAO dao = DataAccess.getInjectedContext().getUserDAO();

        Page<User> listOfUsers = dao.getUserList(field, asc, page, pageSize, filter);
        return ok(userspage.render(listOfUsers, filter.getValue(FilterField.USER_STATUS)));
    }

    @AllowRoles({UserRole.SUPER_USER})
    @InjectContext
    public static Result impersonate(int userId) {
        DataAccessContext context = DataAccess.getInjectedContext();
        UserHeader user = context.getUserDAO().getUserHeader(userId);
        if (user != null) {
            Set<UserRole> userRoles = context.getUserRoleDAO().getUserRoles(userId);
            if (Play.isProd() && userRoles.contains(UserRole.SUPER_USER)) {
                flash("danger", "Je kan geen superuser impersoneren.");
                return redirect(routes.Users.showUsers());
            } else {
                CurrentUser.set(user, userRoles);
                return redirect(routes.Application.index());
            }
        } else {
            flash("danger", "Deze gebruikersID bestaat niet.");
            return redirect(routes.Users.showUsers());
        }
    }
}
