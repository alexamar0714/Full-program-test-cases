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

import java.io.File;

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
public class Test_TypeFile extends BaseTest {

    private final Validator             validator              = new Validator();

    /** Valid test data */
    private static final String         EXISTING_FILE_NAME     = "src/test/resources/validation/file.txt";         // TODO use classpath
    private static final File           EXISTING_FILE          = new File(EXISTING_FILE_NAME);

    /** Invalid test data */
    private static final String         NON_EXISTING_FILE_NAME = "src/test/resources/validation/file.tx_NOT_FOUND";
    private static final File           NON_EXISTING_FILE      = new File(NON_EXISTING_FILE_NAME);

    private static final ValidationType VALIDATION_TYPE        = ValidationType.FILE_EXISTING;

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

        assertTrue(this.validator.validate(VALIDATION_TYPE, EXISTING_FILE));

        assertFalse(this.validator.validate(VALIDATION_TYPE, NON_EXISTING_FILE));
        assertFalse(this.validator.validate(VALIDATION_TYPE, new Object()));
        assertFalse(this.validator.validate(VALIDATION_TYPE, null));
    }
}
