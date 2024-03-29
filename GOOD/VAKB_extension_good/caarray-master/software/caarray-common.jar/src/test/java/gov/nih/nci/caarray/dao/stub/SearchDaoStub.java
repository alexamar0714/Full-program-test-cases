//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 *
 */
public class SearchDaoStub extends AbstractDaoStub implements SearchDao {

    int callsToFiltercollection = 0;
    int callsToPageAndFilterCollection = 0;

    /**
     * {@inheritDoc}
     */
    public <T extends AbstractCaArrayObject> List<T> query(T entityToMatch) {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
    public List<AbstractCaArrayObject> query(final CQLQuery cqlQuery) {
        return new ArrayList<AbstractCaArrayObject>();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId, LockMode lockMode) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> T retrieveUnsecured(Class<T> entityClass, Long entityId) {
        return null;
    }

    public <T extends PersistentObject> List<T> retrieveByIds(Class<T> entityClass, List<? extends Serializable> ids) {
        return new ArrayList<T>();
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(PersistentObject o) {
        //nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findValuesWithSamePrefix(Class<?> entityClass, String fieldName, String prefix) {
        return new ArrayList<String>();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> filterCollection(Collection<T> collection, String property, String value) {
        this.callsToFiltercollection++;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> pageCollection(Collection<T> collection,
            PageSortParams<T> pageSortParams) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> pageAndFilterCollection(Collection<T> collection, String property,
            List<? extends Serializable> values, PageSortParams<T> pageSortParams) {
        this.callsToPageAndFilterCollection++;
        return new ArrayList<T>(collection);
    }

    /**
     * {@inheritDoc}
     */
    public int collectionSize(Collection<? extends PersistentObject> collection) {
        return 0;
    }

    /**
     * @return the callsToFiltercollection
     */
    public int getCallsToFiltercollection() {
        return this.callsToFiltercollection;
    }

    public void reset() {
        this.callsToFiltercollection = 0;
        this.callsToPageAndFilterCollection = 0;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, Order... orders) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends PersistentObject> List<T> retrieveAll(Class<T> entityClass, int maxResults, int firstResult,
            Order... orders) {
        return Collections.emptyList();
    }

    public <T extends AbstractCaArrayEntity> T getEntityByLsid(Class<T> entityClass, LSID lsid) {
        return null;
    }

    /**
     * @return the callsToPageAndFilterCollection
     */
    public int getCallsToPageAndFilterCollection() {
        return callsToPageAndFilterCollection;
    }

    /**
     * @param callsToPageAndFilterCollection the callsToPageAndFilterCollection to set
     */
    public void setCallsToPageAndFilterCollection(int callsToPageAndFilterCollection) {
        this.callsToPageAndFilterCollection = callsToPageAndFilterCollection;
    }
}
