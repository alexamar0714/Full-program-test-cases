//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.SourceSortCriterion;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.IOException;
import java.util.Collection;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Action implementing the sources tab.
 * @author Dan Kokotov
 */
@Validations(
    requiredFields = @RequiredFieldValidator(fieldName = "currentSource.tissueSite",
            key = "struts.validator.requiredString", message = "")
)
public class ProjectSourcesAction extends AbstractProjectProtocolAnnotationListTabAction {
    private static final long serialVersionUID = 1L;

    private Source currentSource = new Source();

    /**
     * Default constructor.
     */
    public ProjectSourcesAction() {
        super("source", new SortablePaginatedList<Source, SourceSortCriterion>(PAGE_SIZE,
                SourceSortCriterion.NAME.name(), SourceSortCriterion.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        super.prepare();
        this.currentSource = retrieveByIdOrExternalId(Source.class, this.currentSource);
    }

    /**
     * {@inheritDoc}
     * @throws ProposalWorkflowException
     */
    @Override
    protected void doCopyItem() throws ProposalWorkflowException, InconsistentProjectStateException {
        ServiceLocatorFactory.getProjectManagementService().copySource(getProject(), this.currentSource.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Source> getCollection() {
        return getProject().getExperiment().getSources();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractCaArrayEntity getItem() {
        return getCurrentSource();
    }

    /**
     * @return the currentSource
     */
    @CustomValidator(type = "hibernate",
                     parameters = @ValidationParameter(name = "resourceKeyBase", value = "experiment.sources"))
    public Source getCurrentSource() {
        return this.currentSource;
    }

    /**
     * @param currentSource the currentSource to set
     */
    public void setCurrentSource(Source currentSource) {
        this.currentSource = currentSource;
    }

    /**
     * download the data for this source.
     * @return download
     * @throws IOException on file error
     */
    @SkipValidation
    public String download() throws IOException {
        Collection<CaArrayFile> files = getCurrentSource().getAllDataFiles();
        if (files.isEmpty()) {
            ActionHelper.saveMessage(getText("experiment.sources.noDataToDownload"));
            return "noSourceData";
        }
        String baseName = ProjectFilesAction.determineDownloadFileName(getProject()).toString();
        DownloadHelper.downloadFiles(files, baseName);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SkipValidation
    @Override
    public String delete() {
        if (!getCurrentSource().getSamples().isEmpty()) {
            ActionHelper.saveMessage(getText("experiment.annotations.cantdelete",
                    new String[] {getText("experiment.source"),
                                  getText("experiment.sample") }));
            updatePagedList();
            return "list";
        }
        return super.delete();
    }

    /**
     * {@inheritDoc}
     */
    @SkipValidation
    @Override
    public String view() {
        downloadFiles();
        return super.view();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SkipValidation
    public String edit() {
        downloadFiles();
        return super.edit();
    }


    /**
     * {@inheritDoc}
     */
    protected Collection<CaArrayFile> getAllDataFiles() {
        return getCurrentSource().getAllDataFiles();
    }

    /**
     * {@inheritDoc}
     */
    public String getDownloadFileListAction() {
        return getDownloadFileListActionUrl(BioMaterialTypes.SOURCES);
    }

    /**
     * {@inheritDoc}
     */
    public String getDownloadFilesTableListSortUrlAction() {
        return getDownloadFileTableListSortActionUrl(BioMaterialTypes.SOURCES);
    }
}
