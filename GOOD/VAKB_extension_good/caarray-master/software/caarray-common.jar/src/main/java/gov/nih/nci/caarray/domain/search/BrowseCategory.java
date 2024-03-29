//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.ResourceBasedEnum;

/**
 * @author Winston Cheng
 *
 */
public enum BrowseCategory implements ResourceBasedEnum {
    /**
     * Experiments.
     */
    EXPERIMENTS("browse.category.experiments", null, "p"),
    /**
     * Organisms.
     */
    ORGANISMS("browse.category.organisms", null, "p.experiment.organism.scientificName"),
    /**
     * Array providers.
     */
    ARRAY_PROVIDERS("browse.category.arrayProviders", null, "p.experiment.manufacturer"),
    /**
     * Array designs.
     */
    ARRAY_DESIGNS("browse.category.arrayDesigns", "p.experiment.arrayDesigns a", "a");

    private final String resourceKey;
    private String join;
    private String field;

    private BrowseCategory(String resourceKey, String join, String field) {
        this.resourceKey = resourceKey;
        this.field = field;
        this.join = join;
    }

    /**
     * @return the resource key that should be used to retrieve a label
     * for this BrowseCategory in the UI
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * @return the join table
     */
    public String getJoin() {
        return join;
    }

    /**
     * @return the field that represents this category
     */
    public String getField() {
        return field;
    }
}
