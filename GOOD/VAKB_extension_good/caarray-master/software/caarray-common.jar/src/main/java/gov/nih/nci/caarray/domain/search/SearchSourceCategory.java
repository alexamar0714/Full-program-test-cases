//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.search;

import org.apache.commons.lang.ArrayUtils;


/**
 * @author mshestopalov
 *
 */

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum SearchSourceCategory implements BiomaterialSearchCategory {

    /**
     * Disease State.
     */
    SAMPLE_DISEASE_STATE ("search.category.diseaseState",
            new String[]{"this.diseaseState sds"},
            new String[]{"sds.value"}),

    /**
     * Tissue Site.
     */
    SAMPLE_TISSUE_SITE ("search.category.tissueSite",
            new String[]{"this.tissueSite sts"},
            new String[]{"sts.value"}),

    /**
     * Organism.
     */
     SAMPLE_ORGANISM ("search.category.organism",
             new String[]{"this.organism so", "this.experiment e", "e.organism eo"},
             ArrayUtils.EMPTY_STRING_ARRAY) {

             /**
              * {@inheritDoc}
              */
             public String getWhereClause() {
                 return "(so IS NOT NULL AND"
                    + " (so.commonName like :keyword OR so.scientificName like :keyword)) OR"
                    + " (eo IS NOT NULL AND"
                    + " (eo.commonName like :keyword OR eo.scientificName like :keyword))";

             }
     },

    /**
     * Experiment title.
     */
    SAMPLE_EXPERIMENT_TITLE ("search.category.experimentTitle",
            new String[]{"this.experiment e"},
            "e.title"),

    /**
     * Material Type.
     */
    SAMPLE_MATERIAL_TYPE ("search.category.materialType",
            new String[]{"this.materialType sms"},
            new String[]{"sms.value"}),

    /**
     * Cell Type.
     */
    SAMPLE_CELL_TYPE ("search.category.cellType",
             new String[]{"this.cellType scs"},
             new String[]{"scs.value"}),

     /**
      * Source Provider.
      */
    SAMPLE_PROVIDER ("search.category.sourceProvider",
            new String[]{"this.providers sps"},
            new String[]{"sps.name"});


    private final String resourceKey;
    private final String[] joins;
    private final String[] searchFields;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    SearchSourceCategory(String resourceKey, String[] joins, String... searchFields) {
        this.resourceKey = resourceKey;
        this.joins = joins;
        this.searchFields = searchFields;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public String[] getJoins() {
        return this.joins;
    }

    /**
     * {@inheritDoc}
     */
    public String getWhereClause() {
        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (String field : this.searchFields) {
            if (j++ > 0) {
                sb.append(" OR ");
            }
            sb.append(field).append(" LIKE :keyword");
        }
        return sb.toString();
    }



}
