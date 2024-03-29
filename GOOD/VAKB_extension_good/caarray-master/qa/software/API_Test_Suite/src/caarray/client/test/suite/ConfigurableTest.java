//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.suite;

import caarray.client.test.TestResult;

/**
 * @author vaughng
 * Jun 29, 2009
 */
public interface ConfigurableTest
{

    public TestResult runTest();
    public String getApi();
    public float getTestCase();
}
