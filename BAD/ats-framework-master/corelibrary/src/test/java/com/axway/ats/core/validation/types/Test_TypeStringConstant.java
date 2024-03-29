/*
 * Copyright 2017 Axway Software
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.axway.ats.core.validation.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.axway.ats.core.BaseTest;
import com.axway.ats.core.validation.ValidationType;
import com.axway.ats.core.validation.Validator;
import com.axway.ats.core.validation.types.TypeStringConstant;

/**
 * This Unit test is used to verify the proper work of the
 * {@link TypeStringConstant} class
 */
public class Test_TypeStringConstant extends BaseTest {

    private final Validator             validator                  = new Validator();

    /** Valid test data */
    private static final String         VALID_CONSTANT             = "dos";
    private static final String         VALID_CONSTANT_IGNORE_CASE = "dOs";
    private static final String[]       CONSTANTS_LIST             = { "uno", "dos", "tres", "quattro" };

    /** Invalid test data */
    private static final String         INVALID_CONSTANT           = "peperonni";
    @SuppressWarnings( "boxing")
    private static final Integer[]      INVALID_CONSTANT_LIST      = { 1, 2, 3, 4, 5 };

    private static final ValidationType VALIDATION_TYPE            = ValidationType.STRING_CONSTANT;

    /**
     * Set up the test cases
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        // empty
    }

    /**
     * Test this certain type's validation
     * 
     * @throws Exception if any unexpected error occurs
     */
    @Test
    public void testValidation() throws Exception {

        assertTrue(this.validator.validate(VALIDATION_TYPE, VALID_CONSTANT, CONSTANTS_LIST));
        assertTrue(this.validator.validate(ValidationType.STRING_CONSTANT_IGNORE_CASE,
                                           VALID_CONSTANT_IGNORE_CASE,
                                           CONSTANTS_LIST));

        assertFalse(this.validator.validate(VALIDATION_TYPE, INVALID_CONSTANT, CONSTANTS_LIST));
        assertFalse(this.validator.validate(VALIDATION_TYPE, VALID_CONSTANT, INVALID_CONSTANT_LIST));
        assertFalse(this.validator.validate(VALIDATION_TYPE, VALID_CONSTANT));
    }
}
