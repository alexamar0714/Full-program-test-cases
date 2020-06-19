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

import net.iaeste.iws.api.dtos.Address;
import net.iaeste.iws.api.dtos.Country;
import net.iaeste.iws.api.dtos.Group;
import net.iaeste.iws.api.dtos.User;
import net.iaeste.iws.api.dtos.exchange.Employer;
import net.iaeste.iws.api.dtos.exchange.Offer;
import net.iaeste.iws.api.dtos.exchange.Student;
import net.iaeste.iws.api.dtos.exchange.StudentApplication;
import net.iaeste.iws.api.enums.exchange.FieldOfStudy;
import net.iaeste.iws.api.enums.exchange.Specialization;
import net.iaeste.iws.api.enums.exchange.StudyLevel;
import net.iaeste.iws.api.util.Date;
import net.iaeste.iws.api.util.DateTime;
import net.iaeste.iws.persistence.views.EmbeddedAddress;
import net.iaeste.iws.persistence.views.EmbeddedApplication;
import net.iaeste.iws.persistence.views.EmbeddedCountry;
import net.iaeste.iws.persistence.views.EmbeddedEmployer;
import net.iaeste.iws.persistence.views.EmbeddedGroup;
import net.iaeste.iws.persistence.views.EmbeddedHomeAddress;
import net.iaeste.iws.persistence.views.EmbeddedOffer;
import net.iaeste.iws.persistence.views.EmbeddedStudent;
import net.iaeste.iws.persistence.views.EmbeddedTermAddress;
import net.iaeste.iws.persistence.views.EmbeddedUser;

/**
 * Transforms all Embedded View, which is used by the read-only queries. The
 * embedded classes are written, so they only contain the primary information,
 * i.e. no Id's (except our externalId) or foreign keys.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
public final class EmbeddedConverter {

    /**
     * Private Constructor, this is a utility class.
     */
    private EmbeddedConverter() {
    }

    public static User convert(final EmbeddedUser embedded) {
        final User result = new User();

        result.setUserId(embedded.getExternalId());
        result.setUsername(embedded.getUsername());
        result.setFirstname(embedded.getFirstname());
        result.setLastname(embedded.getLastname());
        result.setStatus(embedded.getStatus());

        return result;
    }

    public static Address convert(final EmbeddedAddress embedded) {
        final Address result = new Address();

        result.setStreet1(embedded.getStreet1());
        result.setStreet2(embedded.getStreet2());
        result.setPobox(embedded.getPobox());
        result.setPostalCode(embedded.getPostalCode());
        result.setCity(embedded.getCity());
        result.setState(embedded.getState());

        return result;
    }

    public static Address convert(final EmbeddedHomeAddress embedded) {
        final Country country = new Country();
        final Address result = new Address();

        if (embedded.getCountryId() != null) {
            country.setCountryCode(embedded.getCountryCode());
            country.setCountryName(embedded.getCountryName());
            country.setNationality(embedded.getNationality());
            country.setPhonecode(embedded.getPhonecode());
            country.setCurrency(embedded.getCurrency());
            country.setMembership(embedded.getMembership());
            country.setMemberSince(embedded.getMemberSince());
        }

        result.setStreet1(embedded.getStreet1());
        result.setStreet2(embedded.getStreet2());
        result.setPobox(embedded.getPobox());
        result.setPostalCode(embedded.getPostalCode());
        result.setCity(embedded.getCity());
        result.setState(embedded.getState());
        result.setCountry(country);

        return result;
    }

    public static Address convert(final EmbeddedTermAddress embedded) {
        final Country country = new Country();
        final Address result = new Address();

        if (embedded.getCountryId() != null) {
            country.setCountryCode(embedded.getCountryCode());
            country.setCountryName(embedded.getCountryName());
            country.setNationality(embedded.getNationality());
            country.setPhonecode(embedded.getPhonecode());
            country.setCurrency(embedded.getCurrency());
            country.setMembership(embedded.getMembership());
            country.setMemberSince(embedded.getMemberSince());
        }

        result.setStreet1(embedded.getStreet1());
        result.setStreet2(embedded.getStreet2());
        result.setPobox(embedded.getPobox());
        result.setPostalCode(embedded.getPostalCode());
        result.setCity(embedded.getCity());
        result.setState(embedded.getState());
        result.setCountry(country);

        return result;
    }

    public static Country convert(final EmbeddedCountry embedded) {
        Country result = null;

        if (embedded != null) {
            result = new Country();
            result.setCountryCode(embedded.getCountryCode());
            result.setCountryName(embedded.getCountryName());
            result.setNationality(embedded.getNationality());
            result.setPhonecode(embedded.getPhonecode());
            result.setCurrency(embedded.getCurrency());
            result.setMembership(embedded.getMembership());
            result.setMemberSince(embedded.getMemberSince());
        }

        return result;
    }

    public static Group convert(final EmbeddedGroup embedded) {
        final Group group = new Group();

        group.setGroupId(embedded.getExternalId());
        group.setGroupName(embedded.getGroupName());
        group.setPrivateList(embedded.getPrivateList());
        group.setPublicList(embedded.getPublicList());
        group.setGroupType(embedded.getGroupType());
        group.setPrivateListReplyTo(embedded.getPrivateReplyTo());
        group.setPublicListReplyTo(embedded.getPublicReplyTo());

        return group;
    }

    public static Employer convert(final EmbeddedEmployer embedded) {
        final Employer employer = new Employer();

        employer.setEmployerId(embedded.getExternalId());
        employer.setName(embedded.getName());
        employer.setDepartment(embedded.getDepartment());
        employer.setBusiness(embedded.getBusiness());
        employer.setEmployeesCount(embedded.getNumberOfEmployees());
        employer.setWebsite(embedded.getWebsite());
        employer.setWorkingPlace(embedded.getWorkingPlace());
        employer.setCanteen(embedded.getCanteen());
        employer.setNearestAirport(embedded.getNearestAirport());
        employer.setNearestPublicTransport(embedded.getNearestPublicTransport());

        return employer;
    }

    public static Offer convert(final EmbeddedOffer embedded) {
        final Offer result = new Offer();

        result.setOfferId(embedded.getExternalId());
        result.setRefNo(embedded.getRefNo());
        result.setOfferType(embedded.getOfferType());
        result.setExchangeType(embedded.getExchangeType());
        result.setOldRefNo(embedded.getOldRefNo());
        result.setWorkDescription(embedded.getWorkDescription());
        result.setTypeOfWork(embedded.getTypeOfWork());
        result.setWeeklyHours(embedded.getWeeklyHours());
        result.setDailyHours(embedded.getDailyHours());
        result.setWeeklyWorkDays(embedded.getWeeklyWorkDays());
        result.setStudyLevels(CollectionTransformer.explodeEnumSet(StudyLevel.class, embedded.getStudyLevels()));
        result.setFieldOfStudies(CollectionTransformer.explodeEnumSet(FieldOfStudy.class, embedded.getFieldOfStudies()));
        result.setSpecializations(CollectionTransformer.explodeStringSet(embedded.getSpecializations()));
        result.setPreviousTrainingRequired(embedded.getPrevTrainingRequired());
        result.setOtherRequirements(embedded.getOtherRequirements());
        result.setLanguage1(embedded.getLanguage1());
        result.setLanguage1Level(embedded.getLanguage1Level());
        result.setLanguage1Operator(embedded.getLanguage1Operator());
        result.setLanguage2(embedded.getLanguage2());
        result.setLanguage2Level(embedded.getLanguage2Level());
        result.setLanguage2Operator(embedded.getLanguage2Operator());
        result.setLanguage3(embedded.getLanguage3());
        result.setLanguage3Level(embedded.getLanguage3Level());
        result.setMinimumWeeks(embedded.getMinimumWeeks());
        result.setMaximumWeeks(embedded.getMaximumWeeks());
        result.setPeriod1(CommonTransformer.transform(embedded.getFromDate(), embedded.getToDate()));
        result.setPeriod2(CommonTransformer.transform(embedded.getFromDate2(), embedded.getToDate2()));
        result.setUnavailable(CommonTransformer.transform(embedded.getUnavailableFrom(), embedded.getUnavailableTo()));
        result.setPayment(embedded.getPayment());
        result.setPaymentFrequency(embedded.getPaymentFrequency());
        result.setCurrency(embedded.getCurrency());
        result.setDeduction(embedded.getDeduction());
        result.setLivingCost(embedded.getLivingCost());
        result.setLivingCostFrequency(embedded.getLivingCostFrequency());
        result.setLodgingBy(embedded.getLodgingBy());
        result.setLodgingCost(embedded.getLodgingCost());
        result.setLodgingCostFrequency(embedded.getLodgingCostFrequency());
        result.setNominationDeadline(CommonTransformer.convert(embedded.getNominationDeadline()));
        result.setNumberOfHardCopies(embedded.getNumberOfHardCopies());
        result.setAdditionalInformation(embedded.getAdditionalInformation());
        result.setPrivateComment(embedded.getPrivateComment());
        result.setStatus(embedded.getStatus());
        result.setModified(new DateTime(embedded.getModified()));
        result.setCreated(new DateTime(embedded.getCreated()));

        return result;
    }

    public static Student convert(final EmbeddedStudent embedded) {
        final Student result = new Student();

        result.setStudyLevel(embedded.getStudyLevel());
        result.setFieldOfStudies(CollectionTransformer.explodeEnumSet(FieldOfStudy.class, embedded.getFieldOfStudies()));
        result.setSpecializations(CollectionTransformer.explodeEnumSet(Specialization.class, embedded.getSpecializations()));
        result.setAvailable(CommonTransformer.transform(embedded.getAvailableFrom(), embedded.getAvailableTo()));
        result.setLanguage1(embedded.getLanguage1());
        result.setLanguage1Level(embedded.getLanguage1Level());
        result.setLanguage2(embedded.getLanguage2());
        result.setLanguage2Level(embedded.getLanguage2Level());
        result.setLanguage3(embedded.getLanguage3());
        result.setLanguage3Level(embedded.getLanguage3Level());

        return result;
    }

    public static StudentApplication convert(final EmbeddedApplication embedded) {
        final StudentApplication result = new StudentApplication();

        result.setApplicationId(embedded.getExternalId());
        result.setStatus(embedded.getStatus());
        result.setEmail(embedded.getEmail());
        result.setPhoneNumber(embedded.getPhoneNumber());
        result.setDateOfBirth((embedded.getDateOfBirth() != null) ? new Date(embedded.getDateOfBirth()) : null);
        result.setUniversity(embedded.getUniversity());
        result.setPlaceOfBirth(embedded.getPlaceOfBirth());
        result.setGender(embedded.getGender());
        result.setCompletedYearsOfStudy(embedded.getCompletedYearsOfStudy());
        result.setTotalYearsOfStudy(embedded.getTotalYearsOfStudy());
        result.setIsLodgingByIaeste(embedded.isLodgingByIaeste());
        result.setLanguage1(embedded.getLanguage1());
        result.setLanguage1Level(embedded.getLanguage1Level());
        result.setLanguage2(embedded.getLanguage2());
        result.setLanguage2Level(embedded.getLanguage2Level());
        result.setLanguage3(embedded.getLanguage3());
        result.setLanguage3Level(embedded.getLanguage3Level());
        result.setAvailable(CommonTransformer.transform(embedded.getInternshipStart(), embedded.getInternshipEnd()));
        result.setFieldOfStudies(CollectionTransformer.explodeEnumSet(FieldOfStudy.class, embedded.getFieldOfStudies()));
        result.setSpecializations(CollectionTransformer.explodeStringList(embedded.getSpecializations()));
        result.setPassportNumber(embedded.getPassportNumber());
        result.setPassportPlaceOfIssue(embedded.getPassportPlaceOfIssue());
        result.setPassportValidUntil(embedded.getPassportValidUntil());
        result.setRejectByEmployerReason(embedded.getRejectByEmployerReason());
        result.setRejectDescription(embedded.getRejectDescription());
        result.setRejectInternalComment(embedded.getRejectInternalComment());
        result.setNominatedAt(new DateTime(embedded.getNominatedAt()));
        result.setCreated(new DateTime(embedded.getCreated()));
        result.setModified(new DateTime(embedded.getModified()));

        return result;
    }
}
