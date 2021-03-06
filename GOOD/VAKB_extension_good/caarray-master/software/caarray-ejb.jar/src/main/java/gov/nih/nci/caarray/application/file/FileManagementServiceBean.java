//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * EJB implementation of the entry point to the FileManagement subsystem. Delegates functionality to other components in
 * the subsystem.
 */
@Local(FileManagementService.class)
@Stateless
@Interceptors({ExceptionLoggingInterceptor.class, InjectionInterceptor.class })
@SuppressWarnings("PMD.TooManyMethods")
public class FileManagementServiceBean implements FileManagementService {

    private static final Logger LOG = Logger.getLogger(FileManagementServiceBean.class);
    private static final int SAVE_ARRAY_DESIGN_TIMEOUT = 1800;

    private FileManagementJobSubmitter jobSubmitter;
    private ProjectDao projectDao;
    private ArrayDao arrayDao;
    private FileDao fileDao;
    private SearchDao searchDao;
    private JobFactory jobFactory;
    private Provider<MageTabImporter> mageTabImporterProvider;

    private void checkForReparse(CaArrayFileSet fileSet) {
        for (final CaArrayFile caArrayFile : fileSet.getFiles()) {
            if (!caArrayFile.isUnparsedAndReimportable()) {
                throw new IllegalArgumentException("Illegal attempt to reparse file " + caArrayFile.getName());
            }
        }
    }

    private void checkForImport(CaArrayFileSet fileSet) {
        for (final CaArrayFile caArrayFile : fileSet.getFiles()) {
            if (!caArrayFile.getFileStatus().isImportable()) {
                throw new IllegalArgumentException("Illegal attempt to import file " + caArrayFile.getName()
                        + " with status " + caArrayFile.getFileStatus());
            }
        }
    }

    private void checkForValidation(CaArrayFileSet fileSet) {
        for (final CaArrayFile caArrayFile : fileSet.getFiles()) {
            if (!caArrayFile.getFileStatus().isValidatable()) {
                throw new IllegalArgumentException("Illegal attempt to validate file " + caArrayFile.getName()
                        + " with status " + caArrayFile.getFileStatus());
            }
        }
    }

    private void checkForFileType(CaArrayFile caArrayFile, FileType ft) {
        if (!ft.equals(caArrayFile.getFileType())) {
            throw new IllegalArgumentException("File " + caArrayFile.getName() + " must be an " + ft.getName()
                    + " file type.");
        }

    }

    private void addFilesToInputSet(CaArrayFileSet addTo, CaArrayFileSet addFrom, FileType ft) {
        for (final CaArrayFile caArrayFile : addFrom.getFiles()) {
            if (ft.equals(caArrayFile.getFileType())) {
                addTo.add(caArrayFile);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions) {
        LogUtil.logSubsystemEntry(LOG, fileSet);
        checkForImport(fileSet);
        clearValidationMessages(fileSet);
        sendSplitJobMessage(targetProject, fileSet, dataImportOptions);
        LogUtil.logSubsystemExit(LOG);
    }

    private void clearValidationMessages(CaArrayFileSet fileSet) {
        for (final CaArrayFile caArrayFile : fileSet.getFiles()) {
            caArrayFile.setValidationResult(null);
            this.projectDao.save(caArrayFile);
        }
    }

    private void
            sendSplitJobMessage(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions) {
        final ProjectFilesSplitJob job =
                this.jobFactory.createProjectFilesSplitJob(CaArrayUsernameHolder.getUser(), targetProject, fileSet,
                        dataImportOptions, jobSubmitter);
        this.jobSubmitter.submitJob(job);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateFiles(Project project, CaArrayFileSet fileSet) {
        checkForValidation(fileSet);
        clearValidationMessages(fileSet);
        sendValidationJobMessage(project, fileSet);
    }

    private void sendValidationJobMessage(Project project, CaArrayFileSet fileSet) {
        final ProjectFilesValidationJob job =
                this.jobFactory.createProjectFilesValidationJob(CaArrayUsernameHolder.getUser(), project, fileSet);
        this.jobSubmitter.submitJob(job);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionTimeout(SAVE_ARRAY_DESIGN_TIMEOUT)
    public void saveArrayDesign(ArrayDesign arrayDesign, CaArrayFileSet designFiles) throws InvalidDataFileException,
            IllegalAccessException {
        final boolean newArrayDesign = arrayDesign.getId() == null;
        final CaArrayFileSet oldFiles = arrayDesign.getDesignFileSet();
        designFiles.updateStatus(FileStatus.VALIDATING);

        final ArrayDesignService ads = ServiceLocatorFactory.getArrayDesignService();
        arrayDesign.setDesignFileSet(designFiles);
        arrayDesign = ads.saveArrayDesign(arrayDesign);
        ads.importDesign(arrayDesign);

        if (FileStatus.VALIDATION_ERRORS.equals(designFiles.getStatus())) {
            if (newArrayDesign) {
                this.arrayDao.remove(arrayDesign);
                arrayDesign.getDesignFiles().clear();
            } else {
                arrayDesign.setDesignFileSet(oldFiles);
                this.arrayDao.save(arrayDesign);
            }
            checkDesignFiles(designFiles);
        } else if (oldFiles.getFiles().size() > 0) {
            for (final CaArrayFile file : oldFiles.getFiles()) {
                this.arrayDao.remove(file);
            }
        }
    }

    private void checkDesignFiles(CaArrayFileSet designFiles) throws InvalidDataFileException {
        for (final CaArrayFile designFile : designFiles.getFiles()) {
            if (designFile.getValidationResult() != null && !designFile.getValidationResult().getMessages().isEmpty()) {
                throw new InvalidDataFileException(designFile.getValidationResult());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importArrayDesignDetails(ArrayDesign arrayDesign) {
        arrayDesign.getDesignFileSet().updateStatus(FileStatus.IN_QUEUE);
        this.projectDao.save(arrayDesign.getDesignFiles());
        final AbstractFileManagementJob job =
                this.jobFactory.createArrayDesignFileImportJob(CaArrayUsernameHolder.getUser(), arrayDesign);
        this.jobSubmitter.submitJob(job);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void reimportAndParseArrayDesign(Long arrayDesignId) 
    throws InvalidDataFileException, IllegalAccessException {
        ArrayDesign arrayDesign = this.searchDao.retrieve(ArrayDesign.class, arrayDesignId);
        if (!arrayDesign.isUnparsedAndReimportable()) {
            throw new IllegalAccessException("This array design is not eligible for reimport");
        }

        final ArrayDesignService ads = ServiceLocatorFactory.getArrayDesignService();
        arrayDesign.getDesignFileSet().updateStatus(FileStatus.VALIDATING);
        try {
            arrayDesign = ads.saveArrayDesign(arrayDesign);
            ads.importDesign(arrayDesign);
            checkDesignFiles(arrayDesign.getDesignFileSet());
        } catch (final InvalidDataFileException e) {
            arrayDesign.getDesignFileSet().updateStatus(FileStatus.IMPORT_FAILED);
            throw e;
        } catch (final IllegalAccessException e) {
            arrayDesign.getDesignFileSet().updateStatus(FileStatus.IMPORT_FAILED);
            throw e;
        } catch (final Exception e) {
            arrayDesign.getDesignFileSet().updateStatus(FileStatus.IMPORT_FAILED);
            throw new UnhandledException(e);
        }

        importArrayDesignDetails(arrayDesign);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reimportAndParseProjectFiles(Project targetProject, CaArrayFileSet fileSet) {
        LogUtil.logSubsystemEntry(LOG, fileSet);
        checkForReparse(fileSet);
        clearValidationMessages(fileSet);
        fileSet.updateStatus(FileStatus.IN_QUEUE);

        final ProjectFilesReparseJob job =
                this.jobFactory.createProjectFilesReparseJob(CaArrayUsernameHolder.getUser(), targetProject, fileSet);
        this.jobSubmitter.submitJob(job);

        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSupplementalFiles(Project targetProject, CaArrayFileSet fileSet) {
        if (targetProject == null) {
            throw new IllegalArgumentException("targetProject was null");
        }
        for (final CaArrayFile caArrayFile : fileSet.getFiles()) {
            caArrayFile.setFileStatus(FileStatus.SUPPLEMENTAL);
            caArrayFile.setProject(targetProject);
            this.fileDao.save(caArrayFile);
            targetProject.getFiles().add(caArrayFile);
        }
        targetProject.getExperiment().setLastDataModificationDate(new Date());
        this.projectDao.save(targetProject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findIdfRefFileNames(CaArrayFile idfFile, Project project) {
        final List<String> filenames = new ArrayList<String>();
        checkForFileType(idfFile, FileTypeRegistry.MAGE_TAB_IDF);
        final CaArrayFileSet inputFiles = new CaArrayFileSet(project);
        inputFiles.add(idfFile);
        addFilesToInputSet(inputFiles, project.getFileSet(), FileTypeRegistry.MAGE_TAB_SDRF);
        final MageTabImporter mti = this.mageTabImporterProvider.get();
        final MageTabDocumentSet mTabSet = mti.selectRefFiles(project, inputFiles);
        // we only care about the sdrf docs connected to the idf
        for (final SdrfDocument sdrfDoc : mTabSet.getIdfDocuments().iterator().next().getSdrfDocuments()) {
            filenames.addAll(getRefFileNames(sdrfDoc));
        }

        return filenames;
    }

    private List<String> getRefFileNames(SdrfDocument sdrfDoc) {
        final List<String> filenames = new ArrayList<String>();
        filenames.add(sdrfDoc.getFile().getName());
        filenames.addAll(sdrfDoc.getReferencedDataMatrixFileNames());
        filenames.addAll(sdrfDoc.getReferencedDerivedFileNames());
        filenames.addAll(sdrfDoc.getReferencedRawFileNames());

        return filenames;
    }

    /**
     * @param jobSubmitter the jobSubmitter to set
     */
    @Inject
    public void setJobSubmitter(FileManagementJobSubmitter jobSubmitter) {
        this.jobSubmitter = jobSubmitter;
    }

    /**
     * @param projectDao the projectDao to set
     */
    @Inject
    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    /**
     * @param arrayDao the arrayDao to set
     */
    @Inject
    public void setArrayDao(ArrayDao arrayDao) {
        this.arrayDao = arrayDao;
    }

    /**
     * @param fileDao the fileDao to set
     */
    @Inject
    public void setFileDao(FileDao fileDao) {
        this.fileDao = fileDao;
    }

    /**
     * @param searchDao the searchDao to set
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * @param jobFactory the jobFactory to set
     */
    @Inject
    public void setJobFactory(JobFactory jobFactory) {
        this.jobFactory = jobFactory;
    }

    /**
     * @param mageTabImporterProvider the mageTabImporterProvider to set
     */
    @Inject
    public void setMageTabImporterProvider(Provider<MageTabImporter> mageTabImporterProvider) {
        this.mageTabImporterProvider = mageTabImporterProvider;
    }
}
