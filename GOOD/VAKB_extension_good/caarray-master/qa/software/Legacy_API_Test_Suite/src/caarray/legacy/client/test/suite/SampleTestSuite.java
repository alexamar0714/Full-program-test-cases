//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.legacy.client.test.suite;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.sample.Sample;

import java.io.File;
import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.TestResult;
import caarray.legacy.client.test.search.ExampleSearch;
import caarray.legacy.client.test.search.SampleSearch;

/**
 * @author vaughng
 * Aug 12, 2009
 */
public class SampleTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "Sample.csv";

    private static final String NAME = "Name";
    private static final String EXPECTED_EXTERNAL_ID = "Expected External ID";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
    API, NAME, EXPECTED_RESULTS, MIN_RESULTS, EXPECTED_EXTERNAL_ID};

    /**
     * @param apiFacade
     */
    public SampleTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#evaluateResults(java.util.List, caarray.legacy.client.test.search.ExampleSearch, caarray.legacy.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayObject> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        SampleSearch sampleSearch = (SampleSearch)search;
        List<Sample> sampleResults = (List<Sample>)resultsList;
        int namedResults = 0;
        for (Sample sample : sampleResults)
        {
            if (sample.getName() != null)
                namedResults++;
        }
        if (sampleSearch.getExpectedResults() != null)
        {
            
            if (namedResults != sampleSearch.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected: "
                        + sampleSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (sampleSearch.getMinResults() != null)
        {
            
            if (namedResults < sampleSearch.getMinResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected minimum: "
                        + sampleSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        
        if (sampleSearch.getExpectedExternalId() != null)
        {
            String id = sampleSearch.getExpectedExternalId();
            boolean foundId = false;
                for (Sample sample : sampleResults)
                {
                    if (sample != null && sample.getExternalId() != null && sample.getExternalId().equals(id))
                    {
                        foundId = true;
                        break;
                    }
                }
                if (!foundId)
                {
                    testResult.setPassed(false);
                    String detail = "Didn't find expected external ID: " + id;
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected external ID: " + id;
                    testResult.addDetail(detail);
                }
            
        }
        
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#getExampleSearch()
     */
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new SampleSearch();
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.legacy.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        // N/A

    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.SearchByExampleTestSuite#populateSearch(java.lang.String[], caarray.legacy.client.test.search.ExampleSearch)
     */
    @Override
    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        SampleSearch search = (SampleSearch)exampleSearch;
        Sample example = new Sample();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        
        search.setSample(example);
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer
                    .parseInt(input[headerIndexMap.get(MIN_RESULTS)].trim()));
        
        if (headerIndexMap.get(EXPECTED_EXTERNAL_ID) < input.length
                && !input[headerIndexMap.get(EXPECTED_EXTERNAL_ID)].equals(""))
        {
            search.setExpectedExternalId((input[headerIndexMap.get(EXPECTED_EXTERNAL_ID)].trim()));
        }
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
        return "Sample";
    }

}
