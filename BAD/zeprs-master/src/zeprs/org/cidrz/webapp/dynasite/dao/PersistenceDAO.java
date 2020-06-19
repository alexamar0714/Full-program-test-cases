/*
 *    Copyright 2003, 2004, 2005, 2006 Research Triangle Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package org.cidrz.webapp.dynasite.dao;

import org.cidrz.project.zeprs.valueobject.BaseRecord;
import org.cidrz.project.zeprs.valueobject.BaseRecord;

import java.sql.Connection;

/**
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="dcbfb7b9b0b0b9a59caea8b5f2b3aebb">[email protected]</a>">Chris Kelley</a>
 *         Date: Jul 6, 2005
 *         Time: 5:11:01 PM
 */
public interface PersistenceDAO {

    public BaseRecord getOne(Connection conn, Long patientId, Long pregnancyId) throws Exception;
}
