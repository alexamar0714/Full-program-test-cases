/*
 *    Copyright 2003, 2004, 2005, 2006 Research Triangle Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package org.cidrz.test;

import java.security.Principal;

/**
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="224149474e4e475b6250564b0c4d5045">[email protected]</a>">Chris Kelley</a>
 *         Date: Jul 14, 2005
 *         Time: 9:15:41 AM
 */
public class TestPrincipal implements Principal {
    public String name;

    public TestPrincipal() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}