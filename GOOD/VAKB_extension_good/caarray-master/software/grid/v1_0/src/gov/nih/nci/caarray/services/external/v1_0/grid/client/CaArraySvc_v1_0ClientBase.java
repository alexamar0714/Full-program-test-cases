//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.client;

import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import java.util.Calendar;
import java.util.List;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import org.globus.gsi.GlobusCredential;

import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.NotificationConsumerManager;
import org.globus.wsrf.container.ContainerException;

import org.oasis.wsrf.lifetime.ImmediateResourceTermination;
import org.oasis.wsrf.lifetime.WSResourceLifetimeServiceAddressingLocator;

import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.CaArraySvc_v1_0PortType;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.service.CaArraySvc_v1_0ServiceAddressingLocator;
import gov.nih.nci.caarray.services.external.v1_0.grid.common.CaArraySvc_v1_0I;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;


/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.5
 */
public abstract class CaArraySvc_v1_0ClientBase extends ServiceSecurityClient {	
	protected CaArraySvc_v1_0PortType portType;
	protected Object portTypeMutex;
    protected NotificationConsumerManager consumer = null;
    protected EndpointReferenceType consumerEPR = null;

	public CaArraySvc_v1_0ClientBase(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	   	initialize();
	}
	
	public CaArraySvc_v1_0ClientBase(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
		initialize();
	}
	
	protected void initialize() throws RemoteException {
	    this.portTypeMutex = new Object();
		this.portType = createPortType();
	}

	protected CaArraySvc_v1_0PortType createPortType() throws RemoteException {

		CaArraySvc_v1_0ServiceAddressingLocator locator = new CaArraySvc_v1_0ServiceAddressingLocator();
		// attempt to load our context sensitive wsdd file
		InputStream resourceAsStream = getClass().getResourceAsStream("client-config.wsdd");
		if (resourceAsStream != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(resourceAsStream);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}
		CaArraySvc_v1_0PortType port = null;
		try {
			port = locator.getCaArraySvc_v1_0PortTypePort(getEndpointReference());
		} catch (Exception e) {
			throw new RemoteException("Unable to locate portType:" + e.getMessage(), e);
		}

		return port;
	}
	
	public void setConnectionTimeout(int timeout) {
    	((Stub) this.portType).setTimeout(timeout);
    }
    
    public int getConnectionTimeout() {
    	return ((Stub) this.portType).getTimeout();
    }
	
    

}
