//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author dkokotov
 * 
 */
public class FileType extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;    

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
