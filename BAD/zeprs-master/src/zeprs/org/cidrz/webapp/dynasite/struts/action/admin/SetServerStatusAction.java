/*
 *    Copyright 2003, 2004, 2005, 2006 Research Triangle Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package org.cidrz.webapp.dynasite.struts.action.admin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.cidrz.webapp.dynasite.struts.action.generic.BaseAction;
import org.cidrz.webapp.dynasite.valueobject.DynaSiteObjects;

/**
 * Imports patient records from an rss feed.
 *
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b6d5ddd3dadad3cff6c4c2df98d9c4d1">[email protected]</a>">Chris Kelley</a>
 *         Date: Mar 8, 2006
 *         Time: 1:08:00 PM
 */
public class SetServerStatusAction extends BaseAction {
	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getFactory().getInstance(SetServerStatusAction.class);

	protected ActionForward doExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map statusMap = DynaSiteObjects.getStatusMap();
        String statusString = null;
        String messageString = null;
		if (request.getParameter("status") != null) {
			statusString = request.getParameter("status");
		}
		if (request.getParameter("message") != null) {
			messageString = request.getParameter("message");
		} else {
			messageString ="true";
		}
		if (messageString.equals("remove")) {
			statusMap.remove(statusString);
		} else {
			statusMap.put(statusString, messageString);
		}
		String message = statusString + " is set to " + messageString + ".";
		request.setAttribute("message", message);
		return mapping.findForward("success");
	}
}
