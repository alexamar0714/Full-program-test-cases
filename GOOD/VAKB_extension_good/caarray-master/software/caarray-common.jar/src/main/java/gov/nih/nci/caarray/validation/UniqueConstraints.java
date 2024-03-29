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
 * Allows specifying multiple uniqueness constraints. Due to the limitations of hibernate validator in the 3.2.0
 * version, the validation message must be specified as part of this annotation and will be shown regardless of which
 * specific uniqueness constraint fails. The messages set on the individual <code>UniqueConstraint</code> annotations
 * will be ignored
 *
 * @author <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4226292d292d362d340277232f312d2e37362b2d2c316c212d2f">[email protected]</a>
 */
@ValidatorClass(UniqueConstraintsValidator.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UniqueConstraints {
    /**
     * The set of uniqueness constraints.
     */
    UniqueConstraint[] constraints();

    /** The message to display if validation fails. */
    String message() default "{validator.uniqueConstraint}";
}

