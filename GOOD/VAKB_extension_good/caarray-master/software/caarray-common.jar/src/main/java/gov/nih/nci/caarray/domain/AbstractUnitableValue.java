//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import javax.persistence.Transient;

/**
 * Base class for UnitableValue implementations.
 * 
 * @author dkokotov
 */
public abstract class AbstractUnitableValue extends AbstractCaArrayEntity implements UnitableValue {
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getDisplayValue() {
        String val = getDisplayValueWithoutUnit();
        if (val == null) {
            return null;
        }
        if (getUnit() == null) {
            return val;
        }
        return val + " " + getUnit().getValue();
    }
}
