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
package net.iaeste.iws.core.transformers;

import static net.iaeste.iws.common.utils.StringUtils.capitalizeFully;
import static net.iaeste.iws.core.transformers.EmbeddedConverter.convert;

import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.dtos.Address;
import net.iaeste.iws.api.dtos.Country;
import net.iaeste.iws.api.dtos.File;
import net.iaeste.iws.api.dtos.Group;
import net.iaeste.iws.api.dtos.User;
import net.iaeste.iws.api.dtos.exchange.Employer;
import net.iaeste.iws.api.dtos.exchange.Offer;
import net.iaeste.iws.api.dtos.exchange.Student;
import net.iaeste.iws.api.dtos.exchange.StudentApplication;
import net.iaeste.iws.api.enums.Descriptable;
import net.iaeste.iws.api.enums.exchange.FieldOfStudy;
import net.iaeste.iws.api.enums.exchange.OfferFields;
import net.iaeste.iws.api.enums.exchange.OfferState;
import net.iaeste.iws.api.enums.exchange.StudyLevel;
import net.iaeste.iws.api.enums.exchange.TypeOfWork;
import net.iaeste.iws.api.exceptions.IWSException;
import net.iaeste.iws.api.util.Date;
import net.iaeste.iws.api.util.DateTime;
import net.iaeste.iws.common.utils.StringUtils;
import net.iaeste.iws.persistence.views.ApplicationView;
import net.iaeste.iws.persistence.views.AttachedFileView;
import net.iaeste.iws.persistence.views.EmbeddedAddress;
import net.iaeste.iws.persistence.views.EmbeddedCountry;
import net.iaeste.iws.persistence.views.EmbeddedEmployer;
import net.iaeste.iws.persistence.views.EmbeddedOffer;
import net.iaeste.iws.persistence.views.EmployerView;
import net.iaeste.iws.persistence.views.IWSView;
import net.iaeste.iws.persistence.views.OfferSharedToGroupView;
import net.iaeste.iws.persistence.views.OfferView;
import net.iaeste.iws.persistence.views.SharedOfferView;
import net.iaeste.iws.persistence.views.StudentView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class ViewTransformer {

    private static final Pattern PATTERN_UNWANTED_CHARACTERS = Pattern.compile("[_\"\\t\\r\\n\\u00a0]");

    /**
     * Private Constructor, this is a utility class.
     */
    private ViewTransformer() {
    }

    /**
     * Transforms the {@link EmployerView} to an {@link Employer} DTO Object. As
     * the DTO contains several sub-objects, which again may contain certain
     * other objects, the transformer is building the entire Object Structure.
     *
     * @param view EmployerView to transform
     * @return Employer DTO Object to display externally
     */
    public static Employer transform(final EmployerView view) {
        final Country country = convert(view.getCountry());

        final Group group = convert(view.getGroup());
        group.setCountry(country);

        final Address address = convert(view.getAddress());
        address.setCountry(country);

        final Employer employer = convert(view.getEmployer());
        employer.setAddress(address);
        employer.setGroup(group);

        return employer;
    }

    /**
     * Transforms the {@link OfferView} to an {@link Offer} DTO Object. As the
     * DTO contains several sub-objects, which again may contain certain other
     * objects, the transformer is building the entire Object Structure.
     *
     * @param view OfferView to transform
     * @return Offer DTO Object to display externally
     */
    public static Offer transform(final OfferView view) {
        final Country country = convert(view.getCountry());

        final Group group = convert(view.getGroup());
        group.setCountry(country);

        final Address address = convert(view.getAddress());
        address.setCountry(country);

        final Employer employer = convert(view.getEmployer());
        employer.setAddress(address);
        employer.setGroup(group);

        final Offer offer = convert(view.getOffer());
        offer.setNsFirstname(view.getNsFirstname());
        offer.setNsLastname(view.getNsLastname());
        offer.setEmployer(employer);

        return offer;
    }

    /**
     * Transforms the {@link OfferView} to an {@link Offer} DTO Object. As the
     * DTO contains several sub-objects, which again may contain certain other
     * objects, the transformer is building the entire Object Structure.
     *
     * @param view OfferView to transform
     * @return Offer DTO Object to display externally
     */
    public static Offer transform(final SharedOfferView view) {
        final Country country = convert(view.getCountry());

        final Group group = convert(view.getGroup());
        group.setCountry(country);

        final Address address = convert(view.getAddress());
        address.setCountry(country);

        final Employer employer = convert(view.getEmployer());
        employer.setAddress(address);
        employer.setGroup(group);

        final Offer offer = convert(view.getOffer());
        offer.setNsFirstname(view.getNsFirstname());
        offer.setNsLastname(view.getNsLastname());
        offer.setEmployer(employer);

        // Shared offers are per definition never from yourself, so this value
        // is *always* set to null, to avoid exposure
        offer.setPrivateComment(null);

        // Showing the OfferGroup status rather than the real Offer Status
        offer.setStatus(view.getOfferGroup().getStatus());

        offer.setHidden(view.getOfferGroup().getHidden());
        offer.setShared(new DateTime(view.getOfferGroup().getCreated()));

        return offer;
    }

    /**
     * Transforms the {@link StudentView} to an {@link Student} DTO Object. As
     * the DTO contains several sub-objects, which again may contain certain
     * other objects, the transformer is building the entire Object Structure.
     *
     * @param view StudentView to transform
     * @return Student DTO Object to display externally
     */
    public static Student transform(final StudentView view) {
        final Student student = convert(view.getStudent());

        final User user = convert(view.getUser());
        student.setUser(user);

        return student;
    }

    /**
     * Transforms the {@link ApplicationView}
     * to an {@link StudentApplication} DTO Object.
     * As the DTO contains several sub-objects, which again may contain certain
     * other objects, the transformer is building the entire Object Structure.
     *
     * @param view ApplicationView to transform
     * @return StudentApplication DTO Object to display externally
     */
    public static StudentApplication transform(final ApplicationView view) {
        final StudentApplication application = convert(view.getApplication());
        application.setOfferId(view.getOfferExternalId());
        application.setOfferState(view.getOfferState());

        final Address homeAddress = convert(view.getHomeAddress());
        application.setHomeAddress(homeAddress);

        final Address termsAddress = convert(view.getTermsAddress());
        application.setAddressDuringTerms(termsAddress);

        final Country nationality = convert(view.getNationality());
        if (nationality != null) {
            application.setNationality(nationality);
        }

        final Student student = convert(view.getStudent());
        final User user = convert(view.getUser());
        student.setUser(user);
        application.setStudent(student);

        return application;
    }

    /**
     * Transforms a SharedOfferGroup View to a Group DTO.
     *
     * @param view View to transform
     * @return Transformed DTO
     */
    public static Group transform(final OfferSharedToGroupView view) {
        final Country country = convert(view.getCountry());
        final Group group = convert(view.getGroup());
        group.setCountry(country);

        return group;
    }

    /**
     * Transforms the File from the Attached File View to a File DTO Object.
     * Note, that the File Data is omitted, since it is stored in the file
     * system, and should only be fetched if explicitly requested.
     *
     * @param view AttachedFileView to transform
     * @return File DTO
     */
    public static File transform(final AttachedFileView view) {
        final File file = new File();

        file.setFileId(view.getFile().getExternalId());
        file.setGroup(convert(view.getGroup()));
        file.setUser(convert(view.getUser()));
        file.setFilename(view.getFile().getFileName());
        file.setFilesize(view.getFile().getSize());
        file.setMimetype(view.getFile().getMimeType());
        file.setDescription(view.getFile().getDescription());
        file.setKeywords(view.getFile().getKeywords());
        file.setChecksum(view.getFile().getChecksum());
        file.setModified(CommonTransformer.convert(view.getFile().getModified()));
        file.setCreated(CommonTransformer.convert(view.getFile().getCreated()));

        return file;
    }

    /**
     * Interim Stage Transformer. We need to somehow make the two different
     * Objects more like, so we can use the same routine for transforming
     * it, since at least 90% of the content is identical. What is annoying
     *is the control of the ordering, as we could then just convert the
     * sub-Objects without any problems. But as the are mixed for various
     * historical reasons - we're currently sticking to it, until a better
     * and more permanent solution is found.
     *
     * @param view View to transform
     * @param type Offer type
     * @param <V> Actual View
     * @return Transformed list of Objects
     */
    public static <V extends IWSView> List<Object> transformOfferToObjectList(final V view, final OfferFields.Type type) {
        final IWSView.Transfomer transformer = view.getTransformer();
        final List<Object> result;

        if (transformer == IWSView.Transfomer.OFFER) {
            final OfferView offer = (OfferView) view;
            result = transformOfferToList(offer, type);
        } else if (transformer == IWSView.Transfomer.SHAREDOFFER) {
            final SharedOfferView offer = (SharedOfferView) view;
            result = transformToStringList(offer, type);
        } else {
            throw new IWSException(IWSErrors.FATAL, "Provided Object is not a supported Offer View.");
        }

        return result;
    }

    private static List<Object> transformOfferToList(final OfferView view, final OfferFields.Type type) {
        final EmbeddedOffer offer = view.getOffer();
        final EmbeddedEmployer employer = view.getEmployer();
        final String refNo = (offer.getOldRefNo() != null) ? offer.getOldRefNo() : offer.getRefNo();
        final List<Object> result = new ArrayList<>();

        addObjectIfRequired(OfferFields.REF_NO, type, result, refNo);
        addObjectIfRequired(OfferFields.OFFER_TYPE, type, result, offer.getOfferType());
        addObjectIfRequired(OfferFields.EXCHANGE_TYPE, type, result, offer.getExchangeType());
        addDateIfRequired(OfferFields.DEADLINE, type, result, offer.getNominationDeadline());
        addObjectIfRequired(OfferFields.COMMENT, type, result, offer.getPrivateComment());

        addObjectIfRequired(OfferFields.EMPLOYER, type, result, employer.getName());
        addObjectIfRequired(OfferFields.DEPARTMENT, type, result, employer.getDepartment());
        addAddressIfRequired(OfferFields.STREET1, type, result, view.getAddress());
        addCountryIfRequired(OfferFields.COUNTRY, type, result, view.getCountry());

        commonOfferInformation(type, result, offer, employer);

        addObjectIfRequired(OfferFields.SHARED, type, result, null);
        addDateIfRequired(OfferFields.LAST_MODIFIED, type, result, offer.getModified());
        addObjectIfRequired(OfferFields.NS_FIRST_NAME, type, result, view.getNsFirstname());
        addObjectIfRequired(OfferFields.NS_LAST_NAME, type, result, view.getNsLastname());

        return result;
    }

    /**
     * Transforms the {@link SharedOfferView} to an {@link List <Object>} for CSVPrinter.
     *
     * @param view SharedOfferView to transform
     * @return List of view objects to be exported to CSV
     */
    private static List<Object> transformToStringList(final SharedOfferView view, final OfferFields.Type type) {
        final EmbeddedOffer offer = view.getOffer();
        final EmbeddedEmployer employer = view.getEmployer();
        final String refNo = (offer.getOldRefNo() != null) ? offer.getOldRefNo() : offer.getRefNo();
        final List<Object> result = new ArrayList<>();

        addObjectIfRequired(OfferFields.REF_NO, type, result, refNo);
        addObjectIfRequired(OfferFields.DEADLINE, type, result, offer.getNominationDeadline());
        result.add(null);

        addObjectIfRequired(OfferFields.EMPLOYER, type, result, employer.getName());
        addObjectIfRequired(OfferFields.DEPARTMENT, type, result, employer.getDepartment());
        addAddressIfRequired(OfferFields.STREET1, type, result, view.getAddress());
        addCountryIfRequired(OfferFields.COUNTRY, type, result, view.getCountry());

        commonOfferInformation(type, result, offer, employer);

        addDateIfRequired(OfferFields.SHARED, type, result, view.getOfferGroup().getCreated());
        addDateIfRequired(OfferFields.LAST_MODIFIED, type, result, offer.getModified());
        addObjectIfRequired(OfferFields.NS_FIRST_NAME, type, result, view.getNsFirstname());
        addObjectIfRequired(OfferFields.NS_LAST_NAME, type, result, view.getNsLastname());

        return result;
    }

    private static void commonOfferInformation(final OfferFields.Type type, final List<Object> result, final EmbeddedOffer offer, final EmbeddedEmployer employer) {
        addObjectIfRequired(OfferFields.WEBSITE, type, result, employer.getWebsite());
        addObjectIfRequired(OfferFields.WORKPLACE, type, result, employer.getWorkingPlace());
        addObjectIfRequired(OfferFields.BUSINESS, type, result, employer.getBusiness());
        // Unsupported, yet required field
        addObjectIfRequired(OfferFields.RESPONSIBLE, type, result, "");
        addObjectIfRequired(OfferFields.AIRPORT, type, result, employer.getNearestAirport());
        addObjectIfRequired(OfferFields.TRANSPORT, type, result, employer.getNearestPublicTransport());
        addObjectIfRequired(OfferFields.EMPLOYEES, type, result, employer.getNumberOfEmployees());
        addObjectIfRequired(OfferFields.HOURS_WEEKLY, type, result, offer.getWeeklyHours());
        addObjectIfRequired(OfferFields.HOURS_DAILY, type, result, offer.getDailyHours());
        addBooleanIfRequired(OfferFields.CANTEEN, type, result, employer.getCanteen());

        addFacultyIfRequired(OfferFields.FACULTY, type, result, offer.getFieldOfStudies());
        addSpecializationIfRequired(OfferFields.SPECIALIZATION, type, result, offer.getSpecializations());
        addBooleanIfRequired(OfferFields.TRAINING_REQUIRED, type, result, offer.getPrevTrainingRequired());
        addTextIfRequired(OfferFields.OTHER_REQUIREMENTS, type, result, offer.getOtherRequirements());
        addTextIfRequired(OfferFields.WORK_KIND, type, result, offer.getWorkDescription());
        addObjectIfRequired(OfferFields.WEEKS_MIN, type, result, offer.getMinimumWeeks());
        addObjectIfRequired(OfferFields.WEEKS_MAX, type, result, offer.getMaximumWeeks());
        addDateIfRequired(OfferFields.FROM, type, result, offer.getFromDate());
        addDateIfRequired(OfferFields.TO, type, result, offer.getToDate());
        addStudyCompletedIfRequired(OfferFields.STUDY_COMPLETED, type, result, offer.getStudyLevels());
        addWorkTypeIfRequired(OfferFields.WORK_TYPE, type , result, offer.getTypeOfWork());
        addLanguageIfRequired(type, result, offer);
        addFinansIfRequired(type, result, offer);
        addObjectIfRequired(OfferFields.NO_HARD_COPIES, type, result, offer.getNumberOfHardCopies());
        addStatusIfRequired(OfferFields.STATUS, type, result, offer.getStatus());
        addDateIfRequired(OfferFields.PERIOD_2_FROM, type, result, offer.getFromDate2());
        addDateIfRequired(OfferFields.PERIOD_2_TO, type, result, offer.getToDate2());
        addDateIfRequired(OfferFields.HOLIDAYS_FROM, type, result, offer.getUnavailableFrom());
        addDateIfRequired(OfferFields.HOLIDAYS_TO, type, result, offer.getUnavailableTo());
        addTextIfRequired(OfferFields.ADDITIONAL_INFO, type, result, offer.getAdditionalInformation());
    }

    private static void addStudyCompletedIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final String value) {
        if (field.useField(type)) {
            final Set<StudyLevel> set = CollectionTransformer.explodeEnumSet(StudyLevel.class, value);

            addBooleanIfRequired(OfferFields.STUDY_COMPLETED_BEGINNING, type, result, set.contains(StudyLevel.B));
            addBooleanIfRequired(OfferFields.STUDY_COMPLETED_MIDDLE, type, result, set.contains(StudyLevel.M));
            addBooleanIfRequired(OfferFields.STUDY_COMPLETED_END, type, result, set.contains(StudyLevel.E));
        }
    }

    private static void addWorkTypeIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final TypeOfWork value) {
        if (field.useField(type)) {
            addBooleanIfRequired(OfferFields.WORK_TYPE_P, type, result, value == TypeOfWork.O);
            addBooleanIfRequired(OfferFields.WORK_TYPE_R, type, result, value == TypeOfWork.R);
            addBooleanIfRequired(OfferFields.WORK_TYPE_W, type, result, value == TypeOfWork.F);
            addBooleanIfRequired(OfferFields.WORK_TYPE_N, type, result, false);
        }
    }

    private static void addLanguageIfRequired(final OfferFields.Type type, final List<Object> result, final EmbeddedOffer offer) {
        addDescriptableIfRequired(OfferFields.LANGUAGE_1, type, result, offer.getLanguage1());
        addDescriptableIfRequired(OfferFields.LANGUAGE_1_LEVEL, type, result, offer.getLanguage1Level());

        // If the second language is not set, then let's just show empty fields,
        // it looks so stupid with filled in values for the operator or level
        // when the language is missing.
        if (offer.getLanguage2() != null) {
            addDescriptableIfRequired(OfferFields.LANGUAGE_1_OR, type, result, offer.getLanguage1Operator());
            addDescriptableIfRequired(OfferFields.LANGUAGE_2, type, result, offer.getLanguage2());
            addDescriptableIfRequired(OfferFields.LANGUAGE_2_LEVEL, type, result, offer.getLanguage2Level());
        } else {
            addDescriptableIfRequired(OfferFields.LANGUAGE_1_OR, type, result, null);
            addDescriptableIfRequired(OfferFields.LANGUAGE_2, type, result, null);
            addDescriptableIfRequired(OfferFields.LANGUAGE_2_LEVEL, type, result, null);
        }

        // If the third language is not set, then let's just show empty fields,
        // it looks so stupid with filled in values for the operator or level
        // when the language is missing.
        if (offer.getLanguage3() != null) {
            addDescriptableIfRequired(OfferFields.LANGUAGE_2_OR, type, result, offer.getLanguage2Operator());
            addDescriptableIfRequired(OfferFields.LANGUAGE_3, type, result, offer.getLanguage3());
            addDescriptableIfRequired(OfferFields.LANGUAGE_3_LEVEL, type, result, offer.getLanguage3Level());
        } else {
            addDescriptableIfRequired(OfferFields.LANGUAGE_2_OR, type, result, null);
            addDescriptableIfRequired(OfferFields.LANGUAGE_3, type, result, null);
            addDescriptableIfRequired(OfferFields.LANGUAGE_3_LEVEL, type, result, null);
        }
    }

    private static void addFinansIfRequired(final OfferFields.Type type, final List<Object> result, final EmbeddedOffer offer) {
        addObjectIfRequired(OfferFields.CURRENCY, type, result, offer.getCurrency());
        addObjectIfRequired(OfferFields.PAYMENT, type, result, offer.getPayment());
        addDescriptableIfRequired(OfferFields.PAYMENT_FREQUENCY, type, result, offer.getPaymentFrequency());
        addObjectIfRequired(OfferFields.DEDUCTION, type, result, offer.getDeduction());
        addObjectIfRequired(OfferFields.LODGING, type, result, offer.getLodgingBy());
        addObjectIfRequired(OfferFields.LODGING_COST, type, result, offer.getLodgingCost());
        addDescriptableIfRequired(OfferFields.LODGING_COST_FREQUENCY, type, result, offer.getLodgingCostFrequency());
        addObjectIfRequired(OfferFields.LIVING_COST, type, result, offer.getLivingCost());
        addDescriptableIfRequired(OfferFields.LIVING_COST_FREQUENCY, type, result, offer.getLivingCostFrequency());
    }

    private static void addObjectIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final Object obj) {
        if (field.useField(type)) {
            result.add(obj);
        }
    }

    private static void addTextIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final String value) {
        if (field.useField(type)) {
            result.add(StringUtils.removeLineBreaks(value));
        }
    }

    private static void addAddressIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final EmbeddedAddress address) {
        if (field.useField(type)) {
            result.add(address.getStreet1());
            result.add(address.getStreet2());
            result.add(address.getPobox());
            result.add(address.getPostalCode());
            result.add(address.getCity());
            result.add(address.getState());
        }
    }

    private static void addBooleanIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final Boolean value) {
        if (field.useField(type)) {
            result.add(CommonTransformer.convertToYesNo(value));
        }
    }

    private static void addDateIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final java.util.Date value) {
        if (field.useField(type)) {
            result.add((value != null) ? new Date(value).toString() : "");
        }
    }

    private static void addDescriptableIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final Descriptable<?> obj) {
        if (field.useField(type)) {
            result.add((obj != null) ? obj.getDescription() : null);
        }
    }

    private static void addFacultyIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final String value) {
        if (field.useField(type)) {
            final StringBuilder builder = new StringBuilder(16);
            boolean firstRow = true;

            // Before we begin, we'll just clean up the value we've received
            // to ensure that our logic is working.
            final String parsedValue = PATTERN_UNWANTED_CHARACTERS.matcher(value).replaceAll(" ").trim();

            for (final FieldOfStudy faculty : CollectionTransformer.explodeEnumSet(FieldOfStudy.class, parsedValue)) {
                if (firstRow) {
                    firstRow = false;
                } else {
                    builder.append(", ");
                }
                builder.append(faculty.getDescription());
            }

            result.add(builder.toString());
        }
    }

    private static void addSpecializationIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final String value) {
        if (field.useField(type)) {
            final StringBuilder builder = new StringBuilder(16);

            // Before we begin, we'll just clean up the value we've received
            // to ensure that our logic is working.
            final String parsedValue = PATTERN_UNWANTED_CHARACTERS.matcher(value).replaceAll(" ").trim();
            addSpecializationFields(builder, parsedValue);
            result.add(builder.toString());
        }
    }

    private static void addSpecializationFields(final StringBuilder builder, final String parsedValue) {
        boolean firstRow = true;

        for (final String specialization : CollectionTransformer.explodeStringSet(parsedValue)) {
            if ((specialization != null) && !specialization.isEmpty()) {
                if (firstRow) {
                    firstRow = false;
                } else {
                    builder.append(", ");
                }

                // Now we have to add the data. Specializations are free
                // format so they can contain many things. But generally,
                // we're assuming that they don't contain any comma's.
                //   The first letter is Capitalized, and the rest is lower,
                // and any white space (including newlines) builder.append(is removed.
                builder.append(capitalizeFully(specialization.trim()));
            }
        }
    }

    private static void addCountryIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final EmbeddedCountry country) {
        if (field.useField(type)) {
            result.add(country.getCountryName());
        }
    }

    private static void addStatusIfRequired(final OfferFields field, final OfferFields.Type type, final List<Object> result, final OfferState value) {
        if (field.useField(type)) {
            result.add((value != null) ? value.getDescription() : null);
        }
    }
}
