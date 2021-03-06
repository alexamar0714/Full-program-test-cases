//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignDeleteException;
import gov.nih.nci.caarray.application.fileaccess.FileAccessUtils;
import gov.nih.nci.caarray.application.vocabulary.VocabularyUtils;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.UnsupportedAffymetrixCdfFiles;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.InvalidNumberOfArgsException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.web.fileupload.MonitoredMultiPartRequest;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.google.inject.Injector;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * @author Winston Cheng
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" })
public class ArrayDesignAction extends ActionSupport implements Preparable {
    private static final String UPLOAD_FIELD_NAME = "upload";

    private static final long serialVersionUID = 1L;

    private ArrayDesign arrayDesign;
    private List<File> uploads;
    private List<String> uploadFileName;
    private String uploadContentType;
    private List<String> fileFormatTypes;
    private List<ArrayDesign> arrayDesigns;
    private List<Organization> providers;
    private Set<Term> featureTypes;
    private List<Organism> organisms;
    private Set<AssayType> assayTypes;
    private boolean editMode;
    private boolean locked;
    private boolean createMode;
    private FileTypeRegistry fileTypeRegistry;
    private FileAccessUtils fileAccessUtils;

    /**
     * Construts a new action.
     */
    public ArrayDesignAction() {
        this(InjectorFactory.getInjector());
    }

    /**
     * Constructs a new action.
     *
     * @param injector for injection
     */
    public ArrayDesignAction(Injector injector) {
        this.fileTypeRegistry = injector.getInstance(FileTypeRegistry.class);
        this.fileAccessUtils = injector.getInstance(FileAccessUtils.class);
    }

    /**
     * @return the set of array design file types to display in UI.
     */
    public Set<FileType> getArrayDesignTypes() {
        return this.fileTypeRegistry.getArrayDesignTypes();
    }

    /**
     * @return the array design
     */
    public ArrayDesign getArrayDesign() {
        return this.arrayDesign;
    }

    /**
     * @param arrayDesign the array design to set
     */
    public void setArrayDesign(ArrayDesign arrayDesign) {
        this.arrayDesign = arrayDesign;
    }

    /**
     * uploaded file.
     *
     * @return uploads uploaded files
     */
    public List<File> getUpload() {
        return this.uploads;
    }

    /**
     * sets file uploads.
     *
     * @param inUploads List
     */
    public void setUpload(List<File> inUploads) {
        this.uploads = inUploads;
    }

    /**
     * @return the uploadFileName
     */
    public List<String> getUploadFileName() {
        return this.uploadFileName;
    }

    /**
     * @param uploadFileNames List
     */
    public void setUploadFileName(List<String> uploadFileNames) {
        this.uploadFileName = uploadFileNames;
    }

    /**
     * @return the uploadContentType
     */
    public String getUploadContentType() {
        return this.uploadContentType;
    }

    /**
     * @param uploadContentType the uploadContentType to set
     */
    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    /**
     * @return the uploadFormatType
     */
    public List<String> getFileFormatType() {
        return this.fileFormatTypes;
    }

    /**
     * @param uploadFormatTypes the uploadFormatType to set
     */
    public void setFileFormatType(List<String> uploadFormatTypes) {
        this.fileFormatTypes = uploadFormatTypes;
    }

    /**
     * @return the arrayDesigns
     */
    public List<ArrayDesign> getArrayDesigns() {
        return this.arrayDesigns;
    }

    /**
     * @return the manufacturers
     */
    public List<Organization> getProviders() {
        return this.providers;
    }

    /**
     * @return the featureTypes
     */
    public Set<Term> getFeatureTypes() {
        return this.featureTypes;
    }

    /**
     * @return the organisms
     */
    public List<Organism> getOrganisms() {
        return this.organisms;
    }

    /**
     * @return the createMode
     */
    public boolean isCreateMode() {
        return this.createMode;
    }

    /**
     * @param createMode the createMode to set
     */
    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }

    /**
     * @param editMode the editMode to set
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    /**
     * @return the editMode
     */
    public boolean isEditMode() {
        return this.editMode;
    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * @return the assayTypes
     */
    public Set<AssayType> getAssayTypes() {
        return this.assayTypes;
    }

    /**
     * @param assayTypes the assayTypes to set
     */
    public void setAssayTypes(Set<AssayType> assayTypes) {
        this.assayTypes = assayTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        this.organisms = ServiceLocatorFactory.getVocabularyService().getOrganisms();
        this.providers = ServiceLocatorFactory.getArrayDesignService().getAllProviders();
        this.featureTypes = VocabularyUtils.getTermsFromCategory(ExperimentOntologyCategory.TECHNOLOGY_TYPE);
        if (this.arrayDesign != null && this.arrayDesign.getId() != null) {
            final ArrayDesign retrieved =
                ServiceLocatorFactory.getArrayDesignService().getArrayDesign(this.arrayDesign.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(getArrayDesign(), SecurityUtils.PERMISSIONS_PRIVILEGE,
                        CaArrayUsernameHolder.getUser());
            } else {
                this.arrayDesign = retrieved;
            }
            this.locked = ServiceLocatorFactory.getArrayDesignService().isArrayDesignLocked(this.arrayDesign.getId());
        }
    }

    /**
     * Retrieves the list of all array designs.
     *
     * @return list
     */
    @SkipValidation
    public String list() {
        this.arrayDesigns = ServiceLocatorFactory.getArrayDesignService().getArrayDesigns();
        return "list";
    }

    /**
     * Edit view of an array design.
     *
     * @return input
     */
    @SkipValidation
    public String edit() {
        this.createMode = false;
        this.editMode = true;
        return Action.INPUT;
    }

    /**
     * Edit view of an import file.
     *
     * @return input
     */
    @SkipValidation
    public String editFile() {
        this.createMode = false;
        this.editMode = true;
        return "metaValid";
    }

    /**
     * Read-only view of an array design.
     *
     * @return input
     */
    @SkipValidation
    public String view() {
        this.createMode = false;
        this.editMode = false;
        return Action.INPUT;
    }

    /**
     * Creation of a new array design.
     *
     * @return input
     */
    @SkipValidation
    public String create() {
        this.createMode = true;
        this.editMode = true;
        return Action.INPUT;
    }

    /**
     * Save a new or existing array design.
     *
     * @return success
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Validations(
        fieldExpressions = {@FieldExpressionValidator(fieldName = "arrayDesign.assayTypes", message = "",
            key = "errors.required", expression = "!arrayDesign.assayTypes.isEmpty") },
        requiredStrings = {@RequiredStringValidator(fieldName = "arrayDesign.version", key = "errors.required",
            message = "") },
        requiredFields = {
                @RequiredFieldValidator(fieldName = "arrayDesign.provider", key = "errors.required", message = ""),
                @RequiredFieldValidator(fieldName = "arrayDesign.technologyType", key = "errors.required",
                        message = ""),
                @RequiredFieldValidator(fieldName = "arrayDesign.organism", key = "errors.required", message = "") })
    /**
     * Save the array design metadata.
     */
    public
            String saveMeta() {
        if (!this.createMode && this.editMode) {

            if (ServiceLocatorFactory.getArrayDesignService().isDuplicate(this.arrayDesign)) {
                ActionHelper.saveMessage(getText("arraydesign.duplicate", new String[] {getArrayDesign().getName() }));
                return Action.INPUT;
            }

            saveImportFile();
            final List<Object> args = new ArrayList<Object>();
            args.add(getArrayDesign().getName());
            args.add(getArrayDesign().getProvider().getName());
            ActionHelper.saveMessage(getText("arraydesign.saved", args));
            return Action.SUCCESS;
        }
        this.editMode = true;
        return "metaValid";
    }

    /**
     * download data files for an array design.
     *
     * @return download
     * @throws IOException on file error
     */
    @SkipValidation
    public String download() throws IOException {
        if (getArrayDesign() == null || getArrayDesign().getDesignFiles().isEmpty()) {
            ActionHelper.saveMessage(getText("arrayDesign.noDataToDownload"));
            return "list";
        }

        DownloadHelper.downloadFiles(getArrayDesign().getDesignFiles(), determineDownloadFileName());
        return null;
    }

    /**
     * Returns the filename for a zip of files for the given project, assuming that the download will not be grouped.
     *
     * @return the filename
     */
    private String determineDownloadFileName() {
        final StringBuilder name = new StringBuilder("caArray_").append(this.arrayDesign.getName()).append("_file");
        return name.toString();
    }

    /**
     * Save/Update file associated with array design.
     *
     * @return input
     */
    @SkipValidation
    @SuppressWarnings("deprecation")
    public String save() {
        String returnVal = null;
        Long id = null;
        // upload file is required for new array designs
        if (this.arrayDesign.getId() == null && (this.uploadFileName == null || this.uploadFileName.isEmpty())) {
            addFieldError(UPLOAD_FIELD_NAME, getText("fileRequired"));
        } else {
            id = this.arrayDesign.getId();
            if (id == null) {
                // Checks if any uploaded files are zip files. If they are, the files are unzipped,
                // and the appropriate properties are updated so that the unzipped files are
                // part of the uploads list.
                fileAccessUtils.unzipFiles(this.uploads, this.uploadFileName);
                this.arrayDesign.setName(FilenameUtils.getBaseName(this.uploadFileName.get(0)));
            }

            saveImportFile();
        }

        if (this.hasErrors()) {
            // addArrayDesign overwrites the id, so reset if there is an error.
            if (id != null) {
                this.arrayDesign.setId(id);
            }
            returnVal = "metaValid";
        } else {
            returnVal = "importComplete";
        }

        MonitoredMultiPartRequest.releaseProgressMonitor(ServletActionContext.getRequest());

        return returnVal;
    }

    /**
     * Delete an array design.
     *
     * @return input
     */
    @SkipValidation
    public String delete() {
        try {
            ServiceLocatorFactory.getArrayDesignService().deleteArrayDesign(getArrayDesign());
            ActionHelper
            .saveMessage(getText("arrayDesign.deletionSuccess", new String[] {getArrayDesign().getName() }));
        } catch (final ArrayDesignDeleteException e) {
            ActionHelper.saveMessage(e.getMessage());
        }
        this.arrayDesigns = ServiceLocatorFactory.getArrayDesignService().getArrayDesigns();
        return SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (this.hasErrors()) {
            this.editMode = true;
        }
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private void saveImportFile() {
        try {
            // figure out if we are editing or creating.
            if (this.arrayDesign.getId() != null && (this.uploadFileName == null || this.uploadFileName.isEmpty())) {
                ServiceLocatorFactory.getArrayDesignService().saveArrayDesign(this.arrayDesign);
            } else {
                handleFiles();
            }
        } catch (final RuntimeException re) {
            if (re.getCause() instanceof InvalidNumberOfArgsException) {
                final InvalidNumberOfArgsException inv = (InvalidNumberOfArgsException) re.getCause();
                addFieldError(
                        UPLOAD_FIELD_NAME,
                        getText("arrayDesign.error." + inv.getMessage(),
                                inv.getArguments().toArray(new String[inv.getArguments().size()])));
            } else {
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.importingFile"));
            }
            this.arrayDesign.getDesignFiles().clear();
        } catch (final InvalidDataFileException e) {
            LOG.debug("Swallowed exception saving array design file", e);
            final FileValidationResult result = e.getFileValidationResult();
            for (final ValidationMessage message : result.getMessages()) {
                addFieldError(UPLOAD_FIELD_NAME, message.getMessage());
            }
            this.arrayDesign.getDesignFiles().clear();
        } catch (final IllegalAccessException iae) {
            LOG.debug("Swallowed exception saving array design file", iae);
            this.arrayDesign = ServiceLocatorFactory.getArrayDesignService().getArrayDesign(this.arrayDesign.getId());
            addActionError(iae.getMessage());
        } catch (final Exception e) {
            LOG.debug("Swallowed exception saving array design file", e);
            if (this.arrayDesign.getId() != null) {
                this.arrayDesign =
                    ServiceLocatorFactory.getArrayDesignService().getArrayDesign(this.arrayDesign.getId());
            }
            addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.importingFile"));
        } finally {
            // delete any files created as part of the unzipping process.
            // if the file uploaded was not a zip it will also be deleted
            if (this.uploads != null) {
                for (final File f : this.uploads) {
                    f.delete();
                }
            }
        }
    }

    /**
     * Action is used for an ajax call by the list generator.
     *
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String generateAssayList() {
        setAssayTypes(new TreeSet<AssayType>(ServiceLocatorFactory.getProjectManagementService().getAssayTypes()));

        return "generateAssayList";
    }

    private void handleFiles() throws InvalidDataFileException, IllegalAccessException {
        // check file names against list of unsupported files
        for (final String fileName : this.uploadFileName) {
            if (UnsupportedAffymetrixCdfFiles.isUnsupported(fileName)) {
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.unsupportedFile"));
                return;
            }
        }

        // check user assigned file types and derived ext against known array designs
        int fileCount = 0;
        final Map<String, File> arrayDesignFiles = new HashMap<String, File>();
        final Map<String, FileType> arrayDesignFileTypes = new HashMap<String, FileType>();
        for (final File file : this.uploads) {
            final FileType userType = checkFileType(fileCount);
            final FileType derivedType = checkFileExt(this.uploadFileName.get(fileCount));
            if (userType != null || derivedType != null) {

                arrayDesignFiles.put(this.uploadFileName.get(fileCount), file);

                if (userType != null) {
                    arrayDesignFileTypes.put(this.uploadFileName.get(fileCount), userType);
                } else {
                    arrayDesignFileTypes.put(this.uploadFileName.get(fileCount), derivedType);
                }

            }
            fileCount++;
        }

        doImport(arrayDesignFiles, arrayDesignFileTypes);
    }

    private FileType checkFileType(int index) {
        if (this.fileFormatTypes != null && !this.fileFormatTypes.isEmpty()) {
            final String typeStr = this.fileFormatTypes.get(index);
            final FileType type = this.fileTypeRegistry.getTypeByName(typeStr);
            if (type != null && type.isArrayDesign()) {
                return type;
            }
        }

        return null;
    }

    private FileType checkFileExt(String filename) {
        final FileType ft = this.fileTypeRegistry.getTypeFromExtension(filename);

        if (ft != null && ft.isArrayDesign()) {
            return ft;
        }

        return null;
    }

    private void doImport(Map<String, File> arrayDesignFiles, Map<String, FileType> arrayDesignFileTypes)
    throws InvalidDataFileException, IllegalAccessException {

        if (!arrayDesignFiles.isEmpty()) {
            final CaArrayFileSet designFiles = new CaArrayFileSet();
            for (final String fileName : arrayDesignFiles.keySet()) {
                final CaArrayFile designFile =
                    ServiceLocatorFactory.getFileAccessService().add(arrayDesignFiles.get(fileName), fileName);

                designFile.setFileType(arrayDesignFileTypes.get(fileName));
                designFiles.add(designFile);

            }

            ServiceLocatorFactory.getFileManagementService().saveArrayDesign(this.arrayDesign, designFiles);
            ServiceLocatorFactory.getFileManagementService().importArrayDesignDetails(this.arrayDesign);

            // add error message for the user if there was an attempt to import non-array design files.
            if (this.uploads.size() > designFiles.getFiles().size()) {
                ActionHelper.saveMessage(getText("arrayDesign.warning.multiFile"));
            }

        } else {
            if (this.uploads.size() > 1) {
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.multipleCannotDetermineType"));
            } else {
                addFieldError(UPLOAD_FIELD_NAME, getText("arrayDesign.error.cannotDetermineType"));
            }

        }
    }

    /**
     * Handles reimporting an array design that was previously imported-not-parsed but now can be parsed.
     *
     * @return "list"
     */
    public String reimport() {
        if (getArrayDesign() == null || getArrayDesign().getId() == null) {
            ActionHelper.saveMessage(getText("arrayDesign.noDesignSelected"));
            return SUCCESS;
        }

        try {
            ServiceLocatorFactory.getFileManagementService().reimportAndParseArrayDesign(getArrayDesign().getId());
            ActionHelper.saveMessage(getText("arrayDesign.importing"));
        } catch (final IllegalAccessException e) {
            ActionHelper.saveMessage(getText("arrayDesign.cannotReimport"));
        } catch (final InvalidDataFileException e) {
            ActionHelper.saveMessage(getText("arrayDesign.invalid"));
            for (final ValidationMessage vm : e.getFileValidationResult().getMessages()) {
                ActionHelper.saveMessage(vm.getMessage());
            }
        } catch (final UnhandledException e) {
            ActionHelper.saveMessage(getText("arrayDesign.error.importingFile"));
            ActionHelper.saveMessage(e.getCause().getMessage());
        }

        return SUCCESS;
    }
}
