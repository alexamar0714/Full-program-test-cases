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
 * Specifies a uniqueness constraint on a set of fields that results in the creation of a DDL unique constraint, as 
 * well as enforcement via hibernate validator.
 * 
 * @author <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e1858a8e8a8e958e97a1d4808c928e8d9495888e8f92cf828e8c">[email protected]</a>
 */
@ValidatorClass(UniqueConstraintValidator.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UniqueConstraint {
    /**
     * The list of fields that participate in the constraint. Each field is described by a UniqueConstraintField
     * annotation.
     */
    UniqueConstraintField[] fields() default { };

    /** The message to display if validation fails. */
    String message() default "{validator.uniqueConstraint}";
    
    /**
     * Defines whether a unique constraint will be applied to the table.
     */
    boolean generateDDLConstraint() default true;
}
