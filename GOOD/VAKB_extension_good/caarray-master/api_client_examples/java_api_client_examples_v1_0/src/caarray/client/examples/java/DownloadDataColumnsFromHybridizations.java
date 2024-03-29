//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.AbstractDataColumn;
import gov.nih.nci.caarray.external.v1_0.data.BooleanColumn;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.DesignElement;
import gov.nih.nci.caarray.external.v1_0.data.DoubleColumn;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.FloatColumn;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.IntegerColumn;
import gov.nih.nci.caarray.external.v1_0.data.LongColumn;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.data.ShortColumn;
import gov.nih.nci.caarray.external.v1_0.data.StringColumn;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.InconsistentDataSetsException;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A client downloading data columns from hybridizations using the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadDataColumnsFromHybridizations {
    private static SearchService searchService = null;
    private static DataService dataService = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String QUANTITATION_TYPES_CSV_STRING = BaseProperties.AFFYMETRIX_CHP_QUANTITATION_TYPES;

    public static void main(String[] args) {
        DownloadDataColumnsFromHybridizations downloader = new DownloadDataColumnsFromHybridizations();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            dataService = server.getDataService();
            searchServiceHelper = new JavaSearchApiUtils(searchService);
            System.out.println("Downloading data columns from hybridizations in " + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading data columns from hybridizations.");
            t.printStackTrace();
        }
    }

    private void download() throws InvalidInputException, InconsistentDataSetsException {
        DataSetRequest dataSetRequest = new DataSetRequest();
        // Select an experiment of interest.
        CaArrayEntityReference experimentRef = selectExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find experiment with the requested title.");
            return;
        }

        // Select hybridizations in the experiment.
        Set<CaArrayEntityReference> hybridizationRefs = selectHybridizations(experimentRef);
        if (hybridizationRefs == null) {
            System.err.println("Could not find any hybridizations with CHP data in the selected experiment.");
            return;
        }
        dataSetRequest.setHybridizations(hybridizationRefs);

        // Select the quantitation types (columns) of interest.
        QuantitationTypeSearchCriteria qtCrit = new QuantitationTypeSearchCriteria();
        qtCrit.setHybridization(hybridizationRefs.iterator().next());
        qtCrit.getFileCategories().add(FileCategory.DERIVED_DATA);
        List<QuantitationType> qtypes = searchService.searchForQuantitationTypes(qtCrit);
        Set<CaArrayEntityReference> quantitationTypeRefs = new HashSet<CaArrayEntityReference>();
        for (QuantitationType qt : qtypes) {
            quantitationTypeRefs.add(qt.getReference());
        }
        System.out.println("Retrieved quant types: " + qtypes);
        if (quantitationTypeRefs.isEmpty()) {
            System.err.println("Could not find one or more of the requested quantitation types: "
                    + QUANTITATION_TYPES_CSV_STRING);
            return;
        }
        dataSetRequest.setQuantitationTypes(quantitationTypeRefs);

        // Retrieve the parsed data set.
        DataSet dataSet = dataService.getDataSet(dataSetRequest);
        if (dataSet == null) {
            System.err.println("Retrieved null data set.");
            return;
        }
        printDataSet(dataSet);
    }

    /**
     * Search for experiments and select one.
     */
    private CaArrayEntityReference selectExperiment() throws InvalidInputException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        // ... OR Search for experiment with the given public identifier.
        // ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        // experimentSearchCriteria.setPublicIdentifier(EXPERIMENT_PUBLIC_IDENTIFIER);

        List<Experiment> experiments = (searchService.searchForExperiments(experimentSearchCriteria, null)).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This will always be true for a search by public identifier, but may not be true for a search by title.
        Experiment experiment = experiments.iterator().next();
        return experiment.getReference();
    }

    /**
     * Select all hybridizations in the given experiment that have CHP data.
     */
    private Set<CaArrayEntityReference> selectHybridizations(CaArrayEntityReference experimentRef)
            throws InvalidInputException {
        HybridizationSearchCriteria searchCriteria = new HybridizationSearchCriteria();
        searchCriteria.setExperiment(experimentRef);
        List<Hybridization> hybridizations = (searchServiceHelper.hybridizationsByCriteria(searchCriteria)).list();
        if (hybridizations == null || hybridizations.size() <= 0) {
            return null;
        }

        // Get references to the hybridizations.
        Set<CaArrayEntityReference> hybridizationRefs = new HashSet<CaArrayEntityReference>();
        for (Hybridization hybridization : hybridizations) {
            hybridizationRefs.add(hybridization.getReference());
        }

        // Check if the hybridizations have CHP files associated with them.
        if (haveChpFiles(experimentRef, hybridizationRefs)) {
            return hybridizationRefs;
        } else {
            return null;
        }
    }

    private boolean haveChpFiles(CaArrayEntityReference experimentRef, Set<CaArrayEntityReference> hybridizationRefs) throws
            InvalidInputException {
        FileSearchCriteria searchCriteria = new FileSearchCriteria();
        searchCriteria.setExperiment(experimentRef);
        CaArrayEntityReference chpFileTypeRef = getChpFileType();
        searchCriteria.setExperimentGraphNodes(hybridizationRefs);
        searchCriteria.getTypes().add(chpFileTypeRef);
        List<File> dataFiles = (searchService.searchForFiles(searchCriteria, null)).getResults();
        if (dataFiles == null || dataFiles.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private CaArrayEntityReference getChpFileType() throws InvalidInputException {
        ExampleSearchCriteria<FileType> criteria = new ExampleSearchCriteria<FileType>();
        FileType exampleFileType = new FileType();
        exampleFileType.setName("AFFYMETRIX_CHP");
        criteria.setExample(exampleFileType);
        SearchResult<FileType> results = searchService.searchByExample(criteria, null);
        List<FileType> fileTypes = results.getResults();
        FileType chpFileType = fileTypes.iterator().next();
        return chpFileType.getReference();
    }

    private void printDataSet(DataSet dataSet) {
        // Ordered list of row headers (probe sets)
        List<DesignElement> probeSets = dataSet.getDesignElements();
        printProbeSets(probeSets);
        // Ordered list of column headers (quantitation types like Signal, Log Ratio etc.)
        List<QuantitationType> quantitationTypes = dataSet.getQuantitationTypes();
        // Data for the first hybridization (the only hybridization, in our case)
        HybridizationData data = dataSet.getDatas().get(0);
        // Ordered list of columns with values (columns are in the same order as column headers/quantitation types)
        List<AbstractDataColumn> dataColumns = data.getDataColumns();
        Iterator columnIterator = dataColumns.iterator();
        for (QuantitationType quantitationType : quantitationTypes) {
            System.out.println("Column = " + quantitationType.getName() + "; Data type = "
                    + quantitationType.getDataType());
            AbstractDataColumn dataColumn = (AbstractDataColumn) columnIterator.next();
            // Ordered list of values in the column (values are in the same order as row headers/probe sets)
            printColumnValues(quantitationType, dataColumn);
        }
    }

    private void printProbeSets(List<DesignElement> probeSets) {
        System.out.print("Probe Sets: ");
        for (DesignElement probeSet : probeSets) {
            System.out.print(probeSet.getName() + " ");
        }
        System.out.println();
    }

    private void printColumnValues(QuantitationType quantitationType, AbstractDataColumn dataColumn) {
        // Extract individual values in the column according to its type.
        DataType columnDataType = quantitationType.getDataType();
        switch (columnDataType) {
        case BOOLEAN:
            boolean[] booleanValues = ((BooleanColumn) dataColumn).getValues();
            System.out.println("Retrieved " + booleanValues.length + " boolean values.");
            break;
        case INTEGER:
            int[] intValues = ((IntegerColumn) dataColumn).getValues();
            System.out.println("Retrieved " + intValues.length + " int values.");
            break;
        case DOUBLE:
            double[] doubleValues = ((DoubleColumn) dataColumn).getValues();
            System.out.println("Retrieved " + doubleValues.length + " double values.");
            break;
        case FLOAT:
            float[] floatValues = ((FloatColumn) dataColumn).getValues();
            System.out.println("Retrieved " + floatValues.length + " float values.");
            break;
        case SHORT:
            short[] shortValues = ((ShortColumn) dataColumn).getValues();
            System.out.println("Retrieved " + shortValues.length + " short values.");
            break;
        case LONG:
            long[] longValues = ((LongColumn) dataColumn).getValues();
            System.out.println("Retrieved " + longValues.length + " long values.");
            break;
        case STRING:
            String[] stringValues = ((StringColumn) dataColumn).getValues();
            System.out.println("Retrieved " + stringValues.length + " String values.");
            break;
        default:
            // Should never get here.
        }
    }
}
