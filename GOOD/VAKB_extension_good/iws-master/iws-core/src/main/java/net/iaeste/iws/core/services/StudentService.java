/*
 * Licensed to IAESTE A.s.b.l. (IAESTE) under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership. The Authors
 * (See the AUTHORS file distributed with this work) licenses this file to
 * You under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a
 * copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.iaeste.iws.core.services;

import static net.iaeste.iws.core.transformers.ExchangeTransformer.transform;

import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.File;
import net.iaeste.iws.api.dtos.exchange.Student;
import net.iaeste.iws.api.dtos.exchange.StudentApplication;
import net.iaeste.iws.api.enums.exchange.ApplicationStatus;
import net.iaeste.iws.api.enums.exchange.OfferState;
import net.iaeste.iws.api.exceptions.IWSException;
import net.iaeste.iws.api.requests.student.FetchStudentApplicationsRequest;
import net.iaeste.iws.api.requests.student.FetchStudentsRequest;
import net.iaeste.iws.api.requests.student.StudentApplicationsRequest;
import net.iaeste.iws.api.requests.student.StudentApplicationRequest;
import net.iaeste.iws.api.requests.student.StudentRequest;
import net.iaeste.iws.api.responses.student.FetchStudentApplicationsResponse;
import net.iaeste.iws.api.responses.student.FetchStudentsResponse;
import net.iaeste.iws.api.responses.student.StudentApplicationResponse;
import net.iaeste.iws.api.responses.student.StudentResponse;
import net.iaeste.iws.api.util.DateTime;
import net.iaeste.iws.common.configuration.Settings;
import net.iaeste.iws.common.exceptions.NotImplementedException;
import net.iaeste.iws.common.exceptions.VerificationException;
import net.iaeste.iws.core.transformers.StorageTransformer;
import net.iaeste.iws.core.transformers.ViewTransformer;
import net.iaeste.iws.persistence.AccessDao;
import net.iaeste.iws.persistence.Authentication;
import net.iaeste.iws.persistence.ExchangeDao;
import net.iaeste.iws.persistence.StudentDao;
import net.iaeste.iws.persistence.ViewsDao;
import net.iaeste.iws.persistence.entities.AttachmentEntity;
import net.iaeste.iws.persistence.entities.CountryEntity;
import net.iaeste.iws.persistence.entities.FileEntity;
import net.iaeste.iws.persistence.entities.GroupEntity;
import net.iaeste.iws.persistence.entities.UserEntity;
import net.iaeste.iws.persistence.entities.exchange.ApplicationEntity;
import net.iaeste.iws.persistence.entities.exchange.OfferEntity;
import net.iaeste.iws.persistence.entities.exchange.OfferGroupEntity;
import net.iaeste.iws.persistence.entities.exchange.StudentEntity;
import net.iaeste.iws.persistence.views.ApplicationView;
import net.iaeste.iws.persistence.views.StudentView;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class StudentService extends CommonService<StudentDao> {

    private static final String STUDENT_APPLICATION_TABLE = "student_applications";

    private final AccessDao accessDao;
    private final ExchangeDao exchangeDao;
    private final ViewsDao viewsDao;

    public StudentService(final Settings settings, final AccessDao accessDao, final ExchangeDao exchangeDao, final StudentDao studentDao, final ViewsDao viewsDao) {
        super(settings, studentDao);
        this.accessDao = accessDao;
        this.exchangeDao = exchangeDao;
        this.viewsDao = viewsDao;
    }

    public StudentResponse processStudent(final Authentication authentication, final StudentRequest request) {
        final StudentEntity studentEntity = processStudent(authentication, request.getStudent());
        return new StudentResponse(transform(studentEntity));
    }

    private StudentEntity processStudent(final Authentication authentication, final Student student) {
        final GroupEntity memberGroup = accessDao.findMemberGroup(authentication.getUser());
        final UserEntity user = accessDao.findUserByExternalId(student.getStudentId());
        final StudentEntity newEntity = transform(student);
        newEntity.setUser(user);
        final StudentEntity existingEntity = dao.findStudentByExternal(memberGroup.getId(), student.getStudentId());
        if (existingEntity != null) {
            dao.persist(authentication, existingEntity, newEntity);
        } else {
            throw new VerificationException("The student with id '" + student.getStudentId() + "' was not found.");
        }

        return existingEntity;
    }

    public FetchStudentsResponse fetchStudents(final Authentication authentication, final FetchStudentsRequest request) {
        final List<StudentView> found = viewsDao.findStudentsForMemberGroup(authentication.getGroup().getParentId(), request.getPage());

        final List<Student> result = new ArrayList<>(found.size());
        for (final StudentView view : found) {
            result.add(ViewTransformer.transform(view));
        }

        return new FetchStudentsResponse(result);
    }

    public StudentApplicationResponse processStudentApplication(final Authentication authentication, final StudentApplicationsRequest request) {
        final ApplicationEntity entity = processStudentApplication(authentication, request.getStudentApplication());
        final List<AttachmentEntity> attachments = processAttachments(authentication, entity, request.getStudentApplication().getAttachments());

        final StudentApplication application = transform(entity, attachments);
        return new StudentApplicationResponse(application);
    }

    private ApplicationEntity processStudentApplication(final Authentication authentication, final StudentApplication application) {
        final GroupEntity nationalGroup = accessDao.findNationalGroup(authentication.getUser());
        final String externalId = application.getApplicationId();
        final ApplicationEntity applicationEntity = dao.findApplicationByExternalId(externalId);
        final ApplicationEntity processed;

        if ((applicationEntity == null) || applicationEntity.getOfferGroup().getGroup().getId().equals(nationalGroup.getId())) {
            //application owner
            processed = processStudentApplicationByApplicationOwner(authentication, application, applicationEntity);
        } else {
            final OfferGroupEntity sharedOfferGroup = applicationEntity.getOfferGroup();
            final OfferEntity offer = sharedOfferGroup.getOffer();
            if (offer.getEmployer().getGroup().getId().equals(nationalGroup.getId())) {
                //offer owner
                processed = processStudentApplicationByOfferOwner(authentication, application, applicationEntity);
            } else {
                throw new IWSException(IWSErrors.PROCESSING_FAILURE, "Cannot process student application");
            }
        }

        return processed;
    }

    private ApplicationEntity processStudentApplicationByApplicationOwner(final Authentication authentication, final StudentApplication application, final ApplicationEntity existingApplication) {
        final GroupEntity nationalGroup = accessDao.findNationalGroup(authentication.getUser());
        final OfferGroupEntity sharedOfferGroup;
        if (existingApplication == null) {
            sharedOfferGroup = exchangeDao.findInfoForSharedOffer(authentication.getGroup(), application.getOfferId());
        } else {
            sharedOfferGroup = existingApplication.getOfferGroup();
        }

        if ((sharedOfferGroup == null) || !sharedOfferGroup.getGroup().getId().equals(nationalGroup.getId())) {
            final String offerId = application.getOfferId();
            throw new VerificationException("The offer with Id '" + offerId + "' is not shared to the group '" + authentication.getGroup().getGroupName() + "'.");
        }

        if (EnumSet.of(OfferState.CLOSED, OfferState.COMPLETED, OfferState.NEW).contains(sharedOfferGroup.getOffer().getStatus())) {
            throw new VerificationException("It is not possible to create/update application for the offer with status '" + sharedOfferGroup.getOffer().getStatus() + "'.");
        }

        final GroupEntity memberGroup = accessDao.findMemberGroup(authentication.getUser());
        final StudentEntity student = dao.findStudentByExternal(memberGroup.getId(), application.getStudent().getUser().getUserId());
        CountryEntity nationality = null;
        if (application.getNationality() != null) {
            nationality = dao.findCountry(application.getNationality().getCountryCode());
        }

        final ApplicationEntity processed;
        if (existingApplication == null) {
            processed = createNewApplication(authentication, application, sharedOfferGroup, student, nationality);
        } else {
            processed = updateExistingApplication(authentication, application, existingApplication, nationality);
        }

        return processed;
    }

    private ApplicationEntity createNewApplication(final Authentication authentication, final StudentApplication application, final OfferGroupEntity sharedOfferGroup, final StudentEntity student, final CountryEntity nationality) {
        final ApplicationEntity processed = transform(application);
        processed.setOfferGroup(sharedOfferGroup);
        processAddress(authentication, processed.getHomeAddress());
        processAddress(authentication, processed.getAddressDuringTerms());
        processed.setNationality(nationality);
        processed.setStudent(student);
        dao.persist(authentication, processed);

        boolean updateOfferGroup = false;
        if (sharedOfferGroup.getStatus() == OfferState.SHARED) {
            sharedOfferGroup.setStatus(OfferState.APPLICATIONS);
            updateOfferGroup = true;
        }
        if (!sharedOfferGroup.getHasApplication()) {
            sharedOfferGroup.setHasApplication(true);
            updateOfferGroup = true;
        }
        if (updateOfferGroup) {
            dao.persist(sharedOfferGroup);
        }

        return processed;
    }

    private ApplicationEntity updateExistingApplication(final Authentication authentication, final StudentApplication application, final ApplicationEntity applicationEntity, final CountryEntity nationality) {
        final ApplicationEntity updated = transform(application);
        updated.setNationality(nationality);

        //using OfferGroup from found entity since this field can't be updated
        updated.setOfferGroup(applicationEntity.getOfferGroup());
        //do not allow to change status
        updated.setStatus(applicationEntity.getStatus());

        processAddress(authentication, applicationEntity.getHomeAddress(), application.getHomeAddress());
        processAddress(authentication, applicationEntity.getAddressDuringTerms(), application.getAddressDuringTerms());
        dao.persist(authentication, applicationEntity, updated);

        return applicationEntity;
    }

    private ApplicationEntity processStudentApplicationByOfferOwner(final Authentication authentication, final StudentApplication application, final ApplicationEntity applicationEntity) {
        final GroupEntity nationalGroup = accessDao.findNationalGroup(authentication.getUser());
        final OfferGroupEntity sharedOfferGroup = applicationEntity.getOfferGroup();
        final OfferEntity offer = sharedOfferGroup.getOffer();

        if (!offer.getEmployer().getGroup().getId().equals(nationalGroup.getId())) {
            throw new VerificationException("The group with '" + authentication.getGroup().getGroupName() + "' does not own the offer with id '" + offer.getExternalId() + "'.");
        }

        //Offer owner can change only acceptance
        final StudentApplication updatedApplication = transform(applicationEntity);
        updatedApplication.setAcceptance(application.getAcceptance());

        final ApplicationEntity newEntity = transform(updatedApplication);
        dao.persist(authentication, applicationEntity, newEntity);
        return applicationEntity;
    }

    private List<AttachmentEntity> processAttachments(final Authentication authentication, final ApplicationEntity applicationEntity, final List<File> files) {
        final List<AttachmentEntity> attachments = new ArrayList<>(files.size());

        for (final File file : files) {
            final FileEntity fileEntity = processFile(authentication, file);
            final AttachmentEntity attachmentEntity = processAttachment(authentication, applicationEntity, fileEntity);
            attachments.add(attachmentEntity);
        }

        return attachments;
    }

    private AttachmentEntity processAttachment(final Authentication authentication, final ApplicationEntity applicationEntity, final FileEntity fileEntity) {
        AttachmentEntity attachmentEntity = dao.findAttachment(STUDENT_APPLICATION_TABLE, applicationEntity.getId(), fileEntity.getId());

        if (attachmentEntity == null) {
            attachmentEntity = new AttachmentEntity();
            attachmentEntity.setTable(STUDENT_APPLICATION_TABLE);
            attachmentEntity.setRecord(applicationEntity.getId());
            attachmentEntity.setFile(fileEntity);

            dao.persist(authentication, attachmentEntity);
        }

        return attachmentEntity;
    }

    /**
     * Retrieves the Student Applications matching the criteria's from the
     * Request Object.
     *
     * @param authentication User Authentication information
     * @param request        Request Object
     * @return Response Object with the found Student Applications
     */
    public FetchStudentApplicationsResponse fetchStudentApplications(final Authentication authentication, final FetchStudentApplicationsRequest request) {
        final String offerExternalId = request.getOfferId();
        final OfferEntity ownedOffer = exchangeDao.findOfferByOwnerAndExternalId(authentication, offerExternalId);

        final List<ApplicationView> found;
        if ((ownedOffer != null) && ownedOffer.getEmployer().getGroup().equals(authentication.getGroup())) {
            found = dao.findForeignApplicationsForOffer(offerExternalId, authentication.getGroup().getId());
        } else {
            found = dao.findDomesticApplicationsForOffer(offerExternalId, authentication.getGroup().getId());
        }

        final List<StudentApplication> applications = new ArrayList<>(found.size());
        for (final ApplicationView entity : found) {
            final StudentApplication application = ViewTransformer.transform(entity);
            final List<File> attachments = findAndTransformAttachments(entity);
            application.setAttachments(attachments);

            applications.add(application);
        }

        return new FetchStudentApplicationsResponse(applications);
    }

    private List<File> findAndTransformAttachments(final ApplicationView view) {
        final List<AttachmentEntity> attachments = dao.findAttachments(STUDENT_APPLICATION_TABLE, view.getId());
        final List<File> files = new ArrayList<>(attachments.size());

        for (final AttachmentEntity entity : attachments) {
            final File file = StorageTransformer.transform(entity.getFile());
            files.add(file);
        }

        return files;
    }

    /**
     * Process the status of an Application.
     *
     * @param authentication Authentication Information for the requesting User
     * @param request Request Object
     * @return Response Object with the processed Application
     */
    public StudentApplicationResponse processApplicationStatus(final Authentication authentication, final StudentApplicationRequest request) {
        final ApplicationEntity found = dao.findApplicationByExternalId(request.getApplicationId());

        if (found == null) {
            throw new VerificationException("The application with id '" + request.getApplicationId() + "' was not found.");
        }

        final GroupEntity nationalGroup = accessDao.findNationalGroup(authentication.getUser());

        if (nationalGroup == null) {
            throw new IWSException(IWSErrors.OBJECT_IDENTIFICATION_ERROR, "No National Group was found for the User.");
        }

        final OfferEntity offer = found.getOfferGroup().getOffer();

        if (found.getOfferGroup().getGroup().getId().equals(nationalGroup.getId())) {
            //application owner
             processApplicationStatusByApplicationOwner(authentication, request, found);
        } else if (offer.getEmployer().getGroup().getId().equals(nationalGroup.getId())) {
            //offer owner
            processApplicationStatusByOfferOwner(authentication, request, found);
        } else {
            throw new IWSException(IWSErrors.PROCESSING_FAILURE, "Cannot process student application status.");
        }

        return new StudentApplicationResponse(transform(found));
    }

    private void processApplicationStatusByOfferOwner(final Authentication authentication, final StudentApplicationRequest request, final ApplicationEntity applicationEntity) {
        final OfferGroupEntity sharedOfferGroup = applicationEntity.getOfferGroup();

        final StudentApplication studentApplication = transform(applicationEntity);

        verifyOfferAcceptNewApplicationStatus(sharedOfferGroup.getStatus(), request.getStatus());
        verifyApplicationStatusTransition(studentApplication.getStatus(), request.getStatus());

        switch (request.getStatus()) {
            case REJECTED:
                rejectApplication(authentication, request, applicationEntity);
                break;
            case FORWARDED_TO_EMPLOYER:
                forwardToEmployer(authentication, studentApplication, applicationEntity);
                break;
            case ACCEPTED:
                acceptApplication(authentication, studentApplication, applicationEntity);
                break;
            default:
                throw new NotImplementedException("Action '" + request.getStatus() + "' pending implementation.");
        }
    }

    private void processApplicationStatusByApplicationOwner(final Authentication authentication, final StudentApplicationRequest request, final ApplicationEntity applicationEntity) {
        final OfferGroupEntity sharedOfferGroup = applicationEntity.getOfferGroup();

        final StudentApplication studentApplication = transform(applicationEntity);

        verifyOfferAcceptNewApplicationStatus(sharedOfferGroup.getStatus(), request.getStatus());
        verifyApplicationStatusTransition(studentApplication.getStatus(), request.getStatus());

        switch (request.getStatus()) {
            case NOMINATED:
                nominateApplication(authentication, studentApplication, applicationEntity);
                break;
            case CANCELLED:
                cancelApplication(authentication, studentApplication, applicationEntity);
                break;
            case APPLIED:
                applyApplication(authentication, studentApplication, applicationEntity);
                break;
            case REJECTED_BY_SENDING_COUNTRY:
                rejectApplicationByApplicationOwner(authentication, request, applicationEntity);
                break;
            default:
                throw new NotImplementedException("Action '" + request.getStatus() + "' pending implementation.");
        }
    }

    private void applyApplication(final Authentication authentication, final StudentApplication application, final ApplicationEntity applicationEntity) {
        application.setStatus(ApplicationStatus.APPLIED);
        final ApplicationEntity updated = transform(application);
        updated.setOfferGroup(applicationEntity.getOfferGroup());
        updated.setNationality(applicationEntity.getNationality());
        dao.persist(authentication, applicationEntity, updated);
    }

    private void forwardToEmployer(final Authentication authentication, final StudentApplication application, final ApplicationEntity applicationEntity) {
        application.setStatus(ApplicationStatus.FORWARDED_TO_EMPLOYER);
        final ApplicationEntity updated = transform(application);
        updated.setOfferGroup(applicationEntity.getOfferGroup());
        updated.setNationality(applicationEntity.getNationality());
        dao.persist(authentication, applicationEntity, updated);

        //update status for OfferGroup
        updateOfferGroupStatus(applicationEntity.getOfferGroup(), OfferState.AT_EMPLOYER);
        //update status for Offer
        updateOfferStatus(applicationEntity.getOfferGroup().getOffer(), OfferState.AT_EMPLOYER);
    }

    private void acceptApplication(final Authentication authentication, final StudentApplication application, final ApplicationEntity applicationEntity) {
        application.setStatus(ApplicationStatus.ACCEPTED);
        final ApplicationEntity updated = transform(application);
        updated.setOfferGroup(applicationEntity.getOfferGroup());
        updated.setNationality(applicationEntity.getNationality());
        dao.persist(authentication, applicationEntity, updated);

        //update status for OfferGroup
        updateOfferGroupStatus(applicationEntity.getOfferGroup(), OfferState.ACCEPTED);
        //update status for Offer
        updateOfferStatus(applicationEntity.getOfferGroup().getOffer(), OfferState.ACCEPTED);
    }

    private void nominateApplication(final Authentication authentication, final StudentApplication application, final ApplicationEntity storedApplication) {
        application.setNominatedAt(new DateTime());
        application.setStatus(ApplicationStatus.NOMINATED);
        final ApplicationEntity updated = transform(application);
        //using OfferGroup from found entity since this field can't be updated
        updated.setOfferGroup(storedApplication.getOfferGroup());
        updated.setNationality(storedApplication.getNationality());
        dao.persist(authentication, storedApplication, updated);

        //update status for OfferGroup
        //it can be done either when there are applications (APPLICATIONS) or the nominated application was rejected or cancelled previously
        if (EnumSet.of(OfferState.APPLICATIONS, OfferState.SHARED).contains(storedApplication.getOfferGroup().getStatus())) {
            updateOfferGroupStatus(storedApplication.getOfferGroup(), OfferState.NOMINATIONS);
        }
        //update status for Offer
        if (storedApplication.getOfferGroup().getOffer().getStatus() == OfferState.SHARED) {
            updateOfferStatus(storedApplication.getOfferGroup().getOffer(), OfferState.NOMINATIONS);
        }
    }

    private void rejectApplication(final Authentication authentication, final StudentApplicationRequest request, final ApplicationEntity storedApplication) {
        final StudentApplication application = transform(storedApplication);

        application.setStatus(ApplicationStatus.REJECTED);
        application.setRejectByEmployerReason(request.getRejectByEmployerReason());
        application.setRejectDescription(request.getRejectDescription());
        application.setRejectInternalComment(request.getRejectInternalComment());
        final ApplicationEntity updated = transform(application);
        //using OfferGroup from stored entity since this field can't be updated
        updated.setOfferGroup(storedApplication.getOfferGroup());
        updated.setNationality(storedApplication.getNationality());
        dao.persist(authentication, storedApplication, updated);

        final OfferState newOfferGroupState = doUpdateOfferGroupStatus(storedApplication.getOfferGroup().getId(), storedApplication.getOfferGroup().getStatus());
        if (newOfferGroupState != null) {
            updateOfferGroupStatus(storedApplication.getOfferGroup(), newOfferGroupState);
        }

        if (doUpdateOfferStatusToShared(storedApplication.getOfferGroup().getOffer().getId())) {
            updateOfferStatus(storedApplication.getOfferGroup().getOffer(), OfferState.SHARED);
        }
    }

    private void rejectApplicationByApplicationOwner(final Authentication authentication, final StudentApplicationRequest request, final ApplicationEntity storedApplication) {
        //Application owner is allowed to reject only application in state Applied so we don't need to care if Offer status should be changed

        final StudentApplication application = transform(storedApplication);

        application.setStatus(ApplicationStatus.REJECTED_BY_SENDING_COUNTRY);
        application.setRejectDescription(request.getRejectDescription());
        application.setRejectInternalComment(request.getRejectInternalComment());
        final ApplicationEntity updated = transform(application);
        //using OfferGroup from stored entity since this field can't be updated
        updated.setOfferGroup(storedApplication.getOfferGroup());
        updated.setNationality(storedApplication.getNationality());
        dao.persist(authentication, storedApplication, updated);

        final OfferState newOfferGroupState = doUpdateOfferGroupStatus(storedApplication.getOfferGroup().getId(), storedApplication.getOfferGroup().getStatus());
        if (newOfferGroupState != null) {
            updateOfferGroupStatus(storedApplication.getOfferGroup(), newOfferGroupState);
        }
    }

    private void cancelApplication(final Authentication authentication, final StudentApplication application, final ApplicationEntity storedApplication) {
        application.setStatus(ApplicationStatus.CANCELLED);
        final ApplicationEntity updated = transform(application);
        //using OfferGroup from stored entity since this field can't be updated
        updated.setOfferGroup(storedApplication.getOfferGroup());
        updated.setNationality(storedApplication.getNationality());
        dao.persist(authentication, storedApplication, updated);

        final OfferState newOfferGroupState = doUpdateOfferGroupStatus(storedApplication.getOfferGroup().getId(), storedApplication.getOfferGroup().getStatus());
        if (newOfferGroupState != null) {
            updateOfferGroupStatus(storedApplication.getOfferGroup(), newOfferGroupState);
        }

        if (doUpdateOfferStatusToShared(storedApplication.getOfferGroup().getOffer().getId())) {
            updateOfferStatus(storedApplication.getOfferGroup().getOffer(), OfferState.SHARED);
        }
    }

    private boolean doUpdateOfferStatusToShared(final Long offerId) {
        return !dao.otherOfferGroupWithCertainStatus(offerId, EnumSet.of(OfferState.NOMINATIONS,
                                                                         OfferState.AT_EMPLOYER,
                                                                         OfferState.ACCEPTED));
    }

    private OfferState doUpdateOfferGroupStatus(final Long offerGroupId, final OfferState offerGroupState) {
        OfferState newStatus = null;

        if (offerGroupState != OfferState.CLOSED) {
            if (!dao.otherDomesticApplicationsWithCertainStatus(offerGroupId, EnumSet.of(ApplicationStatus.NOMINATED,
                                                                                         ApplicationStatus.FORWARDED_TO_EMPLOYER,
                                                                                         ApplicationStatus.ACCEPTED,
                                                                                         ApplicationStatus.APPLIED))) {
                newStatus = OfferState.SHARED;
            } else if (dao.otherDomesticApplicationsWithCertainStatus(offerGroupId, EnumSet.of(ApplicationStatus.APPLIED))) {
                newStatus = OfferState.APPLICATIONS;
            }
        }

        return newStatus;
    }

    private void updateOfferGroupStatus(final OfferGroupEntity offerGroup, final OfferState state) {
        offerGroup.setStatus(state);
        dao.persist(offerGroup);
    }

    private void updateOfferStatus(final OfferEntity offer, final OfferState state) {
        offer.setStatus(state);
        dao.persist(offer);
    }

    private static void verifyOfferAcceptNewApplicationStatus(final OfferState offerState, final ApplicationStatus applicationStatus) {
        final boolean allowChanges;

        switch (offerState) {
            case COMPLETED:
                allowChanges = checkStateNewStateForCompleted(applicationStatus);
                break;
            case CLOSED:
                allowChanges = applicationStatus == ApplicationStatus.REJECTED_BY_SENDING_COUNTRY;
                break;
            default:
                allowChanges = true;
        }

        if (!allowChanges) {
            throw new VerificationException("Offer with status '" + offerState + "' does not accept new application status '" + applicationStatus + '\'');
        }
    }

    private static boolean checkStateNewStateForCompleted(final ApplicationStatus applicationStatus) {
        final boolean result;

        switch (applicationStatus) {
            case REJECTED:
            case CANCELLED:
                result = true;
                break;
            default:
                result = false;
        }

        return result;
    }

    private static void verifyApplicationStatusTransition(final ApplicationStatus oldStatus, final ApplicationStatus newStatus) {
        final boolean allowChanges;

        switch (oldStatus) {
            case ACCEPTED:
                allowChanges = newStatus == ApplicationStatus.CANCELLED;
                break;
            case APPLIED:
                allowChanges = checkStateNewStateForApplied(newStatus);
                break;
            case FORWARDED_TO_EMPLOYER:
                allowChanges = checkStateNewStateForForwardedToEmployer(newStatus);
                break;
            case REJECTED:
                allowChanges = newStatus == ApplicationStatus.NOMINATED;
                break;
            case CANCELLED:
                allowChanges = checkStateNewStateForCancelled(newStatus);
                break;
            case NOMINATED:
                allowChanges = checkStateNewStateForNominated(newStatus);
                break;
            default:
                throw new VerificationException("Unsupported Status '" + oldStatus + "'.");
        }

        if (!allowChanges) {
            throw new VerificationException("Unsupported transition from '" + oldStatus + "' to " + newStatus);
        }
    }

    private static boolean checkStateNewStateForApplied(final ApplicationStatus newStatus) {
        final boolean result;

        switch (newStatus) {
            case CANCELLED:
            case NOMINATED:
            case REJECTED_BY_SENDING_COUNTRY:
                result = true;
                break;
            default:
                result = false;
        }

        return result;
    }

    private static boolean checkStateNewStateForForwardedToEmployer(final ApplicationStatus newStatus) {
        final boolean result;

        switch (newStatus) {
            case ACCEPTED:
            case CANCELLED:
            case REJECTED:
                result = true;
                break;
            default:
                result = false;
        }

        return result;
    }

    private static boolean checkStateNewStateForCancelled(final ApplicationStatus newStatus) {
        final boolean result;

        switch (newStatus) {
            case APPLIED:
            case NOMINATED:
                result = true;
                break;
            default:
                result = false;
        }

        return result;
    }

    private static boolean checkStateNewStateForNominated(final ApplicationStatus newStatus) {
        final boolean result;

        switch (newStatus) {
            case CANCELLED:
            case FORWARDED_TO_EMPLOYER:
            case REJECTED:
                result = true;
                break;
            default:
                result = false;
        }

        return result;
    }
}
