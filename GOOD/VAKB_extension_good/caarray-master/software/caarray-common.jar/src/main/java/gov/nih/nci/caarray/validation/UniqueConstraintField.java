//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.validator.ValidatorClass;

/**
 * Describes a single field that is part of a potentially-multi-field unique constraint.
 * @author <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c4a0afabafabb0abb284f1a5a9b7aba8b1b0adabaab7eaa7aba9">[email protected]</a>
 */
@ValidatorClass(UniqueConstraintValidator.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME) 
@Documented
public @interface UniqueConstraintField {
    /**
     * The field name. This should be the Java field name (ie property name), not
     * SQL column name. Only fields of simple, composite, or single-valued association types are supported.
     */
    String name();
    
    /**
     * Defines whether two null values for this field are considered (and thus could cause a uniqueness violation),
     * or whether two null values for this field are never equal. The latter behavior is the default and most closely
     * matches the SQL standard.
     */
    boolean nullsEqual() default false;
}

