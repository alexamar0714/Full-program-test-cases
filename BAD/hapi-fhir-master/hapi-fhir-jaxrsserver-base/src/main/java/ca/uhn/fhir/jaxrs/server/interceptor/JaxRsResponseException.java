package ca.uhn.fhir.jaxrs.server.interceptor;

/*
 * #%L
 * HAPI FHIR JAX-RS Server
 * %%
 * Copyright (C) 2014 - 2018 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.ejb.ApplicationException;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

/**
 * A JEE wrapper exception that will not force a rollback.
 * 
 * @author Peter Van Houte | <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1464716071663a62757a7c7b61607154757372753a777b79">[email protected]</a> | Agfa Healthcare
 */
@ApplicationException(rollback=false)
public class JaxRsResponseException extends BaseServerResponseException {

	private static final long serialVersionUID = 1L;

	/**
	 * Utility constructor
	 * 
	 * @param base the base exception
	 */
	public JaxRsResponseException(BaseServerResponseException base) {
		super(base.getStatusCode(), base.getMessage(), base.getCause(), base.getOperationOutcome());
	}

}
