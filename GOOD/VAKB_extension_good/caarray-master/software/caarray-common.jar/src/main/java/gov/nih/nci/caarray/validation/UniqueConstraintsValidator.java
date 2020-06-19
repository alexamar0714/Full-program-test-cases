//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.validation;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.validator.PersistentClassConstraint;
import org.hibernate.validator.Validator;

/**
 * Validator implementation for UniqueConstraints. Delegates to UniqueConstraintValidator.
 * This would not be needed in Hib Validator 3.3 as it transparently supports a wrapper annotation whose
 * only purpose is to allow multiple instances of an annotation to be applied, but for the moment we are tied
 * to the Hib Annotations / Validator 3.2 which does not support this, so we need to do this explicitly.
 * 
 * As a side effect, it is impossible to distinguish which of the constraints actually failed.
 * 
 * @author <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5d393632363229322b1d683c302e323128293432332e733e3230">[email protected]</a>
 */
public class UniqueConstraintsValidator implements Validator<UniqueConstraints>, PersistentClassConstraint {
    private UniqueConstraint[] constraints;
    
    /**
     * {@inheritDoc}
     */
    public void initialize(UniqueConstraints ucs) {
        constraints = ucs.constraints();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValid(Object o) {
        UniqueConstraintValidator ucv = new UniqueConstraintValidator();
        for (UniqueConstraint uc : constraints) {
            ucv.initialize(uc);
            if (!ucv.isValid(o)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(PersistentClass pc) {
        UniqueConstraintValidator ucv = new UniqueConstraintValidator();
        for (UniqueConstraint uc : constraints) {
            ucv.initialize(uc);
            ucv.apply(pc);
        }
    }    
}

