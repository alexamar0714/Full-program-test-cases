package ca.uhn.fhir.jaxrs.server.example;

import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ca.uhn.fhir.jaxrs.server.AbstractJaxRsConformanceProvider;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.server.IResourceProvider;

/**
 * Conformance Rest Service
 * 
 * @author Peter Van Houte | <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3343564756411d45525d5b5c46475673525455521d505c5e">[email protected]</a> | Agfa Healthcare
 */
@Path("")
@Stateless
@Produces({ MediaType.APPLICATION_JSON, Constants.CT_FHIR_JSON, Constants.CT_FHIR_XML })
public class JaxRsConformanceProvider extends AbstractJaxRsConformanceProvider {
	private static final String SERVER_VERSION = "1.0.0";
	private static final String SERVER_DESCRIPTION = "Jax-Rs Test Example Description";
	private static final String SERVER_NAME = "Jax-Rs Test Example";
	
    @Inject
    private JaxRsPatientRestProvider patientProvider;

	/**
	 * Standard Constructor
	 */
	public JaxRsConformanceProvider() {
		super(SERVER_DESCRIPTION, SERVER_NAME, SERVER_VERSION);
	}

	@Override
	protected ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider> getProviders() {
		ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider> map = new ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider>();
		map.put(JaxRsConformanceProvider.class, this);
		map.put(JaxRsPatientRestProvider.class, patientProvider);
		return map;
	}
}
