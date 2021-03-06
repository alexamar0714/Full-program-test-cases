//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

  /**

   */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class Factor extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String description;
    private Term type;
    private Set<AbstractFactorValue> factorValues = new HashSet<AbstractFactorValue>();
    private Experiment experiment;

    /**
     * Gets the name.
     *
     * @return the name
     */
    @NotNull
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_name")
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     *
     * @param nameVal the name
     */
    public void setName(final String nameVal) {
        this.name = nameVal;
    }

    /**
     * @return the description
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "factor_type_fk")
    @NotNull
    public Term getType() {
        return this.type;
    }

    /**
     * Sets the type.
     *
     * @param typeVal the type
     */
    public void setType(final Term typeVal) {
        this.type = typeVal;
    }

    /**
     * Gets the factorValues.
     *
     * @return the factorValues
     */
    @OneToMany(mappedBy = "factor", fetch = FetchType.LAZY)
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    public Set<AbstractFactorValue> getFactorValues() {
        return this.factorValues;
    }

    /**
     * Sets the factorValues.
     *
     * @param factorValuesVal the factorValues
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setFactorValues(final Set<AbstractFactorValue> factorValuesVal) {
        this.factorValues = factorValuesVal;
    }

    /**
     * @return the experiment
     */
    @ManyToOne
    @JoinColumn(name = "experiment", insertable = false, updatable = false)
    @ForeignKey(name = "factor_experiment_fk")
    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * @param experiment
     *            the experiment to set
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }
}
