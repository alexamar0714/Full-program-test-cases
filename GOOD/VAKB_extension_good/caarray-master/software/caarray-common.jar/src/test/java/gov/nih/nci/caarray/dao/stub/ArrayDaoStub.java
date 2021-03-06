//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

public class ArrayDaoStub extends AbstractDaoStub implements ArrayDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(long id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ArrayDesign> getArrayDesigns(Organization provider, Set<AssayType> assayTypes, boolean importedOnly) {
        return new ArrayList<ArrayDesign>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractArrayData getArrayData(Long fileId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
        return null;
    }

    @Override
    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return null;
    }

    @Override
    public boolean isArrayDesignLocked(Long id) {
        return false;
    }

    @Override
    public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return null;
    }

    public void createDesignElementListEntry(DesignElementList designElementList, int elementIndex, Long logicalProbeId) {
        // empty method
    }

    public Long getLogicalProbeId(ArrayDesign design, String name) {
        return null;
    }

    @Override
    public void createDesignElementListEntries(DesignElementList designElementList, int startIndex,
            List<Long> logicalProbeIds) {
        // empty method
    }

    @Override
    public Map<String, Long> getLogicalProbeNamesToIds(ArrayDesign design, List<String> names) {
        return null;
    }

    @Override
    public void deleteArrayDesignDetails(ArrayDesign design) {
        // empty method
    }

    @Override
    public List<Long> getLogicalProbeIds(ArrayDesign design, PageSortParams<LogicalProbe> params) {
        return null;
    }

    @Override
    public void createFeatures(int rows, int cols, ArrayDesignDetails designDetails) {
        // empty method
    }

    @Override
    public Long getFirstFeatureId(ArrayDesignDetails designDetails) {
        return NumberUtils.LONG_ONE;
    }

    @Override
    public List<ArrayDesign> getArrayDesigns(ArrayDesignDetails arrayDesignDetails) {
        return new ArrayList<ArrayDesign>();
    }

    @Override
    public List<QuantitationType> searchForQuantitationTypes(PageSortParams<QuantitationType> params,
            QuantitationTypeSearchCriteria criteria) {
        return new ArrayList<QuantitationType>();
    }

    @Override
    public List<PhysicalProbe> getPhysicalProbeByNames(ArrayDesign design, List<String> names) {
        final List<PhysicalProbe> l = new ArrayList<PhysicalProbe>(names.size());
        for (final PhysicalProbe pp : design.getDesignDetails().getProbes()) {
            if (names.contains(pp.getName())) {
                l.add(pp);
            }
        }
        return l;
    }
    
    @Override
    public Set<String> getPhysicalProbeNames(ArrayDesign design) {
        Set<String> probeNames = new HashSet<String>();
        for (PhysicalProbe pp : design.getDesignDetails().getProbes()) {
            probeNames.add(pp.getName());
        }
        return probeNames;
    }

    @Override
    public List<ArrayDesign> getArrayDesignsWithReImportable() {
        return new ArrayList<ArrayDesign>();
    }

    @Override
    public List<URI> getAllParsedDataHandles() {
        return Collections.emptyList();
    }
}
