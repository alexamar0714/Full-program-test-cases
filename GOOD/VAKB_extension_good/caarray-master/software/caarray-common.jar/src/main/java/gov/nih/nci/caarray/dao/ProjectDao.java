//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.project</code> package.
 *
 * @author Rashmi Srinivasa
 */
public interface ProjectDao extends CaArrayDao {
    /**
     * Gets a subset of the projects belonging to the current user. Projects directly
     * related to the current user are returned. A project is directly related to a user if the user is either the data
     * owner or in a collaboration group which has been granted access to the project. The subset to retrieve depends on
     * the page and sort specifications in pageSortParams
     *
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     *
     * @param pageSortParams specifies the sorting to apply and which page of the full result set to return
     *
     * @return public or non-public projects directly related to the current user, as described above
     */
    List<Project> getProjectsForCurrentUser(PageSortParams<Project> pageSortParams);

    /**
     * Gets all projects belonging to the given user.
     * @param user owner.
     * @return all projects owned by the given user.
     */
    List<Project> getProjectsForOwner(User user);


    /**
     * Gets the count of projects belonging to the current user. The count of all projects
     * directly related to the current user are returned. A project is directly related to a user if the user is
     * either the data owner or in a collaboration group which has been granted access to the project.
     *
     *
     * @return the count of public or non-public projects directly related to the current user, as described above
     */
    int getProjectCountForCurrentUser();

    /**
     * Performs a query for experiments by text matching for the given keyword.
     *
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     *
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching experiments
     */
    List<Project> searchByCategory(PageSortParams<Project> params, String keyword, SearchCategory... categories);
    
    /**
     * Performs a query for experiments by the given criteria.
     *
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @return a list of matching experiments
     */
    List<Experiment> searchByCriteria(PageSortParams<Experiment> params, ExperimentSearchCriteria criteria);

    /**
     * Gets the count of search results matching the given keyword.
     * @param keyword keyword to search for
     * @param categories categories to search
     * @return number of results
     */
    int searchCount(String keyword, SearchCategory... categories);

    /**
     * Retrieve a project based on its public identifier.
     * @param publicId the public identifier of the requested project
     * @return the Project with given public id, or null if no such Project
     */
    Project getProjectByPublicId(String publicId);

    /**
     * Get tissue sites for the experiment and category.
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getTissueSitesForExperiment(Experiment experiment);

    /**
     * Get material types for the experiment and category.
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getMaterialTypesForExperiment(Experiment experiment);

    /**
     * Returns the list of all Assay Types.
     * @return the List&lt;AssayType&gt; of assay types
     */
    List<AssayType> getAssayTypes();

    /**
     * Get cell types for the experiment and category.
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getCellTypesForExperiment(Experiment experiment);

    /**
     * Get disease states for the experiment and category.
     * @param experiment the experiment
     * @return the list of terms
     */
    List<Term> getDiseaseStatesForExperiment(Experiment experiment);

    /**
     * Get all characteristics for the experiment.
     * @param experiment the experiment
     * @return the list of characteristics
     */
    List<AbstractCharacteristic> getArbitraryCharacteristicsForExperimentSamples(Experiment experiment);

    /**
     * Get all characteristics categories for the experiment.
     * @param experiment the experiment
     * @return the list of characteristics
     */
    List<Category> getArbitraryCharacteristicsCategoriesForExperimentSamples(Experiment experiment);

    /**
     * Return the full list of AbstractBiomaterials for a project with given id,  bypassing instance security filters.
     * @param projectId id of project
     * @return all biomaterials for the project with given id
     */
    Set<AbstractBioMaterial> getUnfilteredBiomaterialsForProject(Long projectId);

    /**
     * Get a Source from an Experiment, based on its name.
     * @param experiment experiment to look in
     * @param sourceName name of Source to retrieve
     * @return matching Source or null if not found
     */
    Source getSourceForExperiment(Experiment experiment, String sourceName);

    /**
     * Get a Sample from an Experiment, based on its name.
     * @param experiment experiment to look in
     * @param sampleName name of Sample to retrieve
     * @return matching Sample or null if not found
     */
    Sample getSampleForExperiment(Experiment experiment, String sampleName);

    /**
     * Get an Extract from an Experiment, based on its name.
     * @param experiment experiment to look in
     * @param extractName name of Extract to retrieve
     * @return matching Extract or null if not found
     */
    Extract getExtractForExperiment(Experiment experiment, String extractName);

    /**
     * Get a LabeledExtract from an Experiment, based on its name.
     * @param experiment experiment to look in
     * @param labeledExtractName name of LabeledExtract to retrieve
     * @return matching LabeledExtract or null if not found
     */
    LabeledExtract getLabeledExtractForExperiment(Experiment experiment, String labeledExtractName);

    /**
     * Get experiments that have files that were uploaded but not parsed by earlier caArray versions,
     * but can now be parsed.
     * @return projects with unparsed - now parsable data file.
     * @since 2.4.0
     */
    List<Project> getProjectsWithReImportable();
}
