//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.common;

import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.services.grid.util.EnumFieldHandler;

/**
 * @author dkokotov
 */
public class DataTypeFieldHandler extends EnumFieldHandler<DataType>{
    public DataTypeFieldHandler() {
        super(DataType.class);
    }
}
