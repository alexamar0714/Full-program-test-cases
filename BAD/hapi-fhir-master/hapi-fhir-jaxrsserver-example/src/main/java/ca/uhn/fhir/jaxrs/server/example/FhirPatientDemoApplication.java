package ca.uhn.fhir.jaxrs.server.example;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Fhir Patient Demo Application
 * 
 * @author Peter Van Houte | <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2d5d4859485f035b4c4345425859486d4c4a4b4c034e4240">[email protected]</a> | Agfa Healthcare
 */
@ApplicationPath(value=FhirPatientDemoApplication.PATH)
public class FhirPatientDemoApplication extends Application {
    /** The demo application path */
    public final static String PATH = "/jaxrs-demo";
}
