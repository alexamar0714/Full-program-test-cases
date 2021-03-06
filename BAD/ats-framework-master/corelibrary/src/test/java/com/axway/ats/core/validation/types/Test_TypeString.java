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
import com.axway.ats.core.validation.types.TypeObject;

/**
 * This Unit test is used to verify the proper work of the
 * {@link TypeObject} class
 */
public class Test_TypeString extends BaseTest {

    private final Validator             validator        = new Validator();

    /** Invalid test data */
    private static final String         EMPTY_STRING     = "";
    private static final Integer        NOT_A_STRING     = new Integer(1);

    /** Valid test data */
    private static final Object         NON_EMPTY_STRING = "This string is not empty";

    private static final ValidationType VALIDATION_TYPE  = ValidationType.STRING_NOT_EMPTY;

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

        assertTrue(this.validator.validate(VALIDATION_TYPE, NON_EMPTY_STRING));

        assertFalse(this.validator.validate(VALIDATION_TYPE, EMPTY_STRING));
        assertFalse(this.validator.validate(VALIDATION_TYPE, NOT_A_STRING));
        assertFalse(this.validator.validate(VALIDATION_TYPE, null));
    }
}
