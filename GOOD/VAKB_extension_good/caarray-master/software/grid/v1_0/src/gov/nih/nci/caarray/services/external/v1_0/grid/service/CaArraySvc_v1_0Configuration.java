//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.service;

import gov.nih.nci.cagrid.introduce.servicetools.ServiceConfiguration;

import org.globus.wsrf.config.ContainerConfig;
import java.io.File;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 * 
 * This class holds all service properties which were defined for the service to have
 * access to.
 * 
 * @created by Introduce Toolkit version 1.5
 * 
 */
public class CaArraySvc_v1_0Configuration implements ServiceConfiguration {

	public static CaArraySvc_v1_0Configuration  configuration = null;
    public String etcDirectoryPath;
    	
	public static CaArraySvc_v1_0Configuration getConfiguration() throws Exception {
		if (CaArraySvc_v1_0Configuration.configuration != null) {
			return CaArraySvc_v1_0Configuration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			CaArraySvc_v1_0Configuration.configuration = (CaArraySvc_v1_0Configuration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return CaArraySvc_v1_0Configuration.configuration;
	}
	

	
	private String caGridWsEnumeration_iterImplType;
	
	
    public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}


	
	public String getCaGridWsEnumeration_iterImplType() {
		return caGridWsEnumeration_iterImplType;
	}
	
	
	public void setCaGridWsEnumeration_iterImplType(String caGridWsEnumeration_iterImplType) {
		this.caGridWsEnumeration_iterImplType = caGridWsEnumeration_iterImplType;
	}

	
}
