package ca.uhn.fhir.jaxrs.server.example;

import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsConformanceProvider;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.server.IResourceProvider;

/**
 * Conformance Rest Service
 * 
 * @author Peter Van Houte | <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="87f7e2f3e2f5a9f1e6e9efe8f2f3e2c7e6e0e1e6a9e4e8ea">[email protected]</a> | Agfa Healthcare
 */
@Path("")
@Stateless
@Produces({ MediaType.APPLICATION_JSON, Constants.CT_FHIR_JSON, Constants.CT_FHIR_XML })
public class JaxRsConformanceProviderDstu3 extends AbstractJaxRsConformanceProvider {
	private static final String SERVER_VERSION = "1.0.0";
	private static final String SERVER_DESCRIPTION = "Jax-Rs Test Example Description";
	private static final String SERVER_NAME = "Jax-Rs Test Example";
	
    @Inject
    private JaxRsPatientRestProvider patientProvider;

	/**
	 * Standard Constructor
	 */
	public JaxRsConformanceProviderDstu3() {
		super(FhirContext.forDstu3(), SERVER_DESCRIPTION, SERVER_NAME, SERVER_VERSION);
	}

	@Override
	protected ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider> getProviders() {
		ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider> map = new ConcurrentHashMap<Class<? extends IResourceProvider>, IResourceProvider>();
		map.put(JaxRsConformanceProviderDstu3.class, this);
		map.put(JaxRsPatientRestProvider.class, patientProvider);
		return map;
	}
}
