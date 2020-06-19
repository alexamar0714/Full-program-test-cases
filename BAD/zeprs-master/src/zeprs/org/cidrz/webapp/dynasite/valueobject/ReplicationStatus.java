/*
 *    Copyright 2003, 2004, 2005, 2006 Research Triangle Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package org.cidrz.webapp.dynasite.valueobject;

import java.sql.Date;

/**
 * log of replication to main server
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="0b68606e67676e724b797f622564796c">[email protected]</a>">Chris Kelley</a>
 *         Date: Dec 28, 2005
 *         Time: 12:48:13 PM
 */
public class ReplicationStatus {
    private Date dateReplicated;
    private Boolean success;

    public Date getDateReplicated() {
        return dateReplicated;
    }

    public void setDateReplicated(Date dateReplicated) {
        this.dateReplicated = dateReplicated;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
