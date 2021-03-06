//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;

/**
 * Base class for project tab actions that implement list-type tabs, ie Factors, Sources, etc,
 * that display a list of entities associated with project and allow you to add, edit, remove, copy them.
 *
 * @author Dan Kokotov
 */
public abstract class AbstractProjectListTabAction extends ProjectTabAction {
    private static final long serialVersionUID = 1L;

    /** page size for item lists. */
    public static final int PAGE_SIZE = 15;

    private final String resourceKey;
    private final SortablePaginatedList<? extends PersistentObject, ?> pagedItems;

    /**
     * @param resourceKey the resource to display
     * @param pagedItems the paged list to use for this tab's item list
     */
    public AbstractProjectListTabAction(String resourceKey,
            SortablePaginatedList<? extends PersistentObject, ?> pagedItems) {
        super();
        this.resourceKey = resourceKey;
        this.pagedItems = pagedItems;
    }

    /**
     * loads the tab with a list of the items.
     * {@inheritDoc}
     */
    @Override
    @SkipValidation
    public String load() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        updatePagedList();
        return "list";
    }

    /**
     * Helper method that should be called to refresh the list of items according
     * to current paging parameters.
     */
    @SuppressWarnings("unchecked")
    protected void updatePagedList() {
        GenericDataService gds = ServiceLocatorFactory.getGenericDataService();
        this.pagedItems.setList(gds.pageCollection(getCollection(), this.pagedItems.getPageSortParams()));
        this.pagedItems.setFullListSize(gds.collectionSize(getCollection()));
    }

    /**
     * loads the tab with editing a single item.
     * @return input
     */
    @SkipValidation
    public String edit() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        setEditMode(true);
        return INPUT;
    }

    /**
     * loads the tab with viewing a single item.
     * @return input
     */
    @SkipValidation
    public String view() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        setEditMode(false);
        return INPUT;
    }

    /**
     * Saves the item.
     *
     * @return the string indicating the result to use.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String save() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }

        if (!checkBioMaterialName()) {
            return edit();
        }

        PersistentObject item = getItem();
        if (item.getId() == null) {
            getCollection().add(item);
            if (item instanceof AbstractBioMaterial) {
                AbstractBioMaterial abm = (AbstractBioMaterial) getItem();
                abm.setExperiment(getProject().getExperiment());
            }
            ActionHelper.saveMessage(getText("experiment.items.created", new String[] {getItemName()}));
        } else {
            ActionHelper.saveMessage(getText("experiment.items.updated", new String[] {getItemName()}));
        }
        String result = super.save();
        updatePagedList();
        return result;
    }

    private boolean checkBioMaterialName() {
        // validate biomaterial name
        if (getItem() instanceof AbstractExperimentDesignNode) {
            AbstractExperimentDesignNode bio = (AbstractExperimentDesignNode) getItem();
            for (Object obj : getCollection()) {
                    AbstractExperimentDesignNode abm = (AbstractExperimentDesignNode) obj;
                    if (bio.getName().equals(abm.getName())
                            && bio.getId() != abm.getId()) {
                        String errorField = "current" + getBioMaterialType(bio) + ".name";
                        List<Object> strList = new ArrayList<Object>();
                        strList.add(getBioMaterialType(bio));
                        strList.add(bio.getName());
                        addFieldError(errorField, getText("experiment.annotations.bioname.duplicate", strList));
                        return false;
                    }
            }
        }

        return true;
    }

    private String getBioMaterialType(AbstractExperimentDesignNode bio) {
        String className = bio.getClass().getName()
            .substring(bio.getClass().getName().lastIndexOf(".") + 1);
        return getText("experiment." + className.toLowerCase(Locale.getDefault()));
    }

    /**
     * Gets the label for the item to be used in success messages.
     * @return
     */
    private String getItemName() {
        return getText("experiment." + this.resourceKey);
    }

    /**
     * remove the item with the given id from the set of items.
     *
     * @return the string indicating which result to forward to.
     */
    @SkipValidation
    public String delete() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        getCollection().remove(getItem());
        ActionHelper.saveMessage(getText("experiment.items.deleted", new String[] {getItemName()}));
        super.save();
        updatePagedList();
        return "list";
    }

    /**
     * Copy the item to a new object with a name obtained by adding a counter to the old name,
     * and return to the list screen.
     *
     * @return the string indicating the result to forward to.
     */
    @SkipValidation
    public String copy() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        try {
            doCopyItem();
            ActionHelper.saveMessage(getText("experiment.items.copied", new String[] {getItemName()}));
            ActionHelper.saveMessage(getText("project.saved"));
        } catch (ProposalWorkflowException e) {
            handleWorkflowError();
        } catch (InconsistentProjectStateException e) {
            handleInconsistentStateError(e);
        }
        updatePagedList();
        return "list";
    }

    /**
     * return the project's collection of items to which new items should be added or from which items
     * should be removed.
     * DEVELOPER NOTE: this is intentionally ungenericized as there is no way to make this work with genericized
     * Collections due to limitations of generic type bounds in Java
     * @return project's collection of objects.
     */
    @SuppressWarnings("unchecked")
    protected abstract Collection getCollection();

    /**
     * @return  the List item currently being edited, for save/copy/remove methods
     */
    protected abstract PersistentObject getItem();

    /**
     * Subclasses should make the actual call to the appropriate service method to copy the item.
     * @throws ProposalWorkflowException when the experiment cannot be saved due to workflow restrictions
     * @throws InconsistentProjectStateException when the experiment cannot be saved due to inconsistent state
     */
    protected abstract void doCopyItem() throws ProposalWorkflowException, InconsistentProjectStateException;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (this.hasErrors()) {
            setEditMode(true);
        }
    }

    /**
     * @return the pagedItems
     */
    public SortablePaginatedList<?, ?> getPagedItems() {
        return pagedItems;
    }
}
