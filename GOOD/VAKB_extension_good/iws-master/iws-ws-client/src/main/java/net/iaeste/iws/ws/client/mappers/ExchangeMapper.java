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
package net.iaeste.iws.ws.client.mappers;

import net.iaeste.iws.api.dtos.GroupList;
import net.iaeste.iws.api.dtos.exchange.CSVProcessingErrors;
import net.iaeste.iws.api.dtos.exchange.Employer;
import net.iaeste.iws.api.dtos.exchange.Offer;
import net.iaeste.iws.api.dtos.exchange.OfferStatistics;
import net.iaeste.iws.api.dtos.exchange.PublishingGroup;
import net.iaeste.iws.api.enums.FetchType;
import net.iaeste.iws.api.enums.exchange.EmployerFetchType;
import net.iaeste.iws.api.enums.exchange.ExchangeType;
import net.iaeste.iws.api.enums.exchange.LanguageOperator;
import net.iaeste.iws.api.enums.exchange.OfferState;
import net.iaeste.iws.api.enums.exchange.OfferType;
import net.iaeste.iws.api.enums.exchange.PaymentFrequency;
import net.iaeste.iws.api.enums.exchange.TypeOfWork;
import net.iaeste.iws.api.requests.exchange.EmployerRequest;
import net.iaeste.iws.api.requests.exchange.FetchEmployerRequest;
import net.iaeste.iws.api.requests.exchange.FetchOffersRequest;
import net.iaeste.iws.api.requests.exchange.FetchPublishGroupsRequest;
import net.iaeste.iws.api.requests.exchange.FetchPublishedGroupsRequest;
import net.iaeste.iws.api.requests.exchange.HideForeignOffersRequest;
import net.iaeste.iws.api.requests.exchange.OfferCSVUploadRequest;
import net.iaeste.iws.api.requests.exchange.OfferRequest;
import net.iaeste.iws.api.requests.exchange.OfferStatisticsRequest;
import net.iaeste.iws.api.requests.exchange.PublishingGroupRequest;
import net.iaeste.iws.api.requests.exchange.PublishOfferRequest;
import net.iaeste.iws.api.requests.exchange.RejectOfferRequest;
import net.iaeste.iws.api.responses.exchange.EmployerResponse;
import net.iaeste.iws.api.responses.exchange.FetchEmployerResponse;
import net.iaeste.iws.api.responses.exchange.FetchGroupsForSharingResponse;
import net.iaeste.iws.api.responses.exchange.FetchOffersResponse;
import net.iaeste.iws.api.responses.exchange.FetchPublishedGroupsResponse;
import net.iaeste.iws.api.responses.exchange.FetchPublishingGroupResponse;
import net.iaeste.iws.api.responses.exchange.OfferCSVDownloadResponse;
import net.iaeste.iws.api.responses.exchange.OfferCSVUploadResponse;
import net.iaeste.iws.api.responses.exchange.OfferResponse;
import net.iaeste.iws.api.responses.exchange.OfferStatisticsResponse;
import net.iaeste.iws.api.responses.exchange.PublishOfferResponse;
import net.iaeste.iws.ws.CsvProcessingErrors;
import net.iaeste.iws.ws.FieldDelimiter;
import net.iaeste.iws.ws.Group;
import net.iaeste.iws.ws.ProcessingResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.1
 */
public final class ExchangeMapper extends CommonMapper {

    /** Private Constructor, this is a Utility Class. */
    private ExchangeMapper() {
    }

    /**
     * Mapping of the API OfferStatistics Request Object to the WebService SOAP
     * Object.
     *
     * @param api API OfferStatistics Request Object
     * @return WS OfferStatistics Request Object
     */
    public static net.iaeste.iws.ws.OfferStatisticsRequest map(final OfferStatisticsRequest api) {
        net.iaeste.iws.ws.OfferStatisticsRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.OfferStatisticsRequest();

            ws.setExchangeYear(api.getExchangeYear());
        }

        return ws;
    }

    /**
     * Mapping of the WebService OfferStatistics Response Object to the API
     * SOAP Object.
     *
     * @param ws WebService OfferStatistics Response Object
     * @return API OfferStatistics Response Object
     */
    public static OfferStatisticsResponse map(final net.iaeste.iws.ws.OfferStatisticsResponse ws) {
        OfferStatisticsResponse api = null;

        if (ws != null) {
            api = new OfferStatisticsResponse(map(ws.getError()), ws.getMessage());

            api.setDomesticStatistics(map(ws.getDomesticStatistics()));
            api.setForeignStatistics(map(ws.getForeignStatistics()));
        }

        return api;
    }

    private static OfferStatistics map(final net.iaeste.iws.ws.OfferStatistics ws) {
        OfferStatistics api = null;

        if (ws != null) {
            api = new OfferStatistics();

            api.setStatistics(mapWSStatisticsMap(ws.getStatistics()));
            api.setExchangeYear(ws.getExchangeYear());
        }

        return api;
    }

    /**
     * Mapping of the API Employer Request Object to the WebService SOAP Object.
     *
     * @param api API Employer Request Object
     * @return WS Employer Request Object
     */
    public static net.iaeste.iws.ws.EmployerRequest map(final EmployerRequest api) {
        net.iaeste.iws.ws.EmployerRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.EmployerRequest();
            ws.setEmployer(map(api.getEmployer()));
            ws.setEmployerId(api.getEmployerId());
            ws.setAction(map(api.getAction()));
        }

        return ws;
    }

    /**
     * Mapping of the WebService Employer Response Object to the API SOAP
     * Object.
     *
     * @param ws WebService Employer Response Object
     * @return API Employer Response Object
     */
    public static EmployerResponse map(final net.iaeste.iws.ws.EmployerResponse ws) {
        EmployerResponse api = null;

        if (ws != null) {
            api = new EmployerResponse(map(ws.getError()), ws.getMessage());

            api.setEmployer(map(ws.getEmployer()));
        }

        return api;
    }

    /**
     * Mapping of the API FetchEmployer Request Object to the WebService SOAP
     * Object.
     *
     * @param api API FetchEmployer Request Object
     * @return WS FetchEmployer Request Object
     */
    public static net.iaeste.iws.ws.FetchEmployerRequest map(final FetchEmployerRequest api) {
        net.iaeste.iws.ws.FetchEmployerRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.FetchEmployerRequest();

            ws.setPage(map(api.getPage()));
            ws.setType(map(api.getFetchType()));
            ws.setField(api.getFetchField());
            ws.setFetchOfferReferenceNumbers(api.getFetchOfferReferenceNumbers());
        }

        return ws;
    }

    /**
     * Mapping of the WebService FetchEmployer Response Object to the API SOAP
     * Object.
     *
     * @param ws WebService FetchEmployer Response Object
     * @return API FetchEmployer Response Object
     */
    public static FetchEmployerResponse map(final net.iaeste.iws.ws.FetchEmployerResponse ws) {
        FetchEmployerResponse api = null;

        if (ws != null) {
            api = new FetchEmployerResponse(map(ws.getError()), ws.getMessage());

            final List<Employer> employers = new ArrayList<>(ws.getEmployers().size());
            for (final net.iaeste.iws.ws.Employer employer : ws.getEmployers()) {
                employers.add(map(employer));
            }
            api.setEmployers(employers);
            api.setOfferRefNos(mapStringCollection(ws.getOfferRefNos()));
        }

        return api;
    }

    /**
     * Mapping of the API FetchOffers Request Object to the WebService SOAP
     * Object.
     *
     * @param api API FetchOffers Request Object
     * @return WS Fetch Offers Request Object
     */
    public static net.iaeste.iws.ws.FetchOffersRequest map(final FetchOffersRequest api) {
        net.iaeste.iws.ws.FetchOffersRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.FetchOffersRequest();

            ws.setPage(map(api.getPage()));
            ws.setFetchType(map(api.getFetchType()));
            ws.getIdentifiers().addAll(mapStringCollection(api.getIdentifiers()));
            ws.setExchangeYear(api.getExchangeYear());
            ws.getStates().addAll(mapApiStateCollection(api.getStates()));
            ws.setRetrieveCurrentAndNextExchangeYear(api.getRetrieveCurrentAndNextExchangeYear());
        }

        return ws;
    }

    /**
     * Mapping of the WebService FetchOffers Response Object to the API SOAP
     * Object.
     *
     * @param ws WebService FetchOffers Response Object
     * @return API FetchOffers Response Object
     */
    public static FetchOffersResponse map(final net.iaeste.iws.ws.FetchOffersResponse ws) {
        FetchOffersResponse api = null;

        if (ws != null) {
            api = new FetchOffersResponse(map(ws.getError()), ws.getMessage());

            final List<Offer> offers = new ArrayList<>(ws.getOffers().size());
            for (final net.iaeste.iws.ws.Offer offer : ws.getOffers()) {
                offers.add(map(offer));
            }
            api.setOffers(offers);
        }

        return api;
    }

    /**
     * Mapping of the API Offer Request Object to the WebService SOAP Object.
     *
     * @param api API Offer Request Object
     * @return WS Offer Request Object
     */
    public static net.iaeste.iws.ws.OfferRequest map(final OfferRequest api) {
        net.iaeste.iws.ws.OfferRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.OfferRequest();

            ws.setOffer(map(api.getOffer()));
            ws.setOfferId(api.getOfferId());
            ws.setAction(map(api.getAction()));
        }

        return ws;
    }

    /**
     * Mapping of the WebService Offer Response Object to the API SOAP Object.
     *
     * @param ws WebService Offer Response Object
     * @return API Offer Response Object
     */
    public static OfferResponse map(final net.iaeste.iws.ws.OfferResponse ws) {
        OfferResponse api = null;

        if (ws != null) {
            api = new OfferResponse(map(ws.getError()), ws.getMessage());

            api.setOffer(map(ws.getOffer()));
        }

        return api;
    }

    /**
     * Mapping of the API OfferCSVUpload Request Object to the WebService SOAP
     * Object.
     *
     * @param api API OfferCSVUpload Request Object
     * @return WS OfferCSVUpload Request Object
     */
    public static net.iaeste.iws.ws.OfferCSVUploadRequest map(final OfferCSVUploadRequest api) {
        net.iaeste.iws.ws.OfferCSVUploadRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.OfferCSVUploadRequest();

            ws.setCsv(api.getCsv());
            ws.setDelimiter(map(api.getDelimiter()));
        }

        return ws;
    }

    /**
     * Mapping of the WebService OfferCSVUpload Response Object to the API
     * SOAP Object.
     *
     * @param ws WebService OfferCSVUpload Response Object
     * @return API OfferCSVUpload Response Object
     */
    public static OfferCSVUploadResponse map(final net.iaeste.iws.ws.OfferCSVUploadResponse ws) {
        OfferCSVUploadResponse api = null;

        if (ws != null) {
            api = new OfferCSVUploadResponse(map(ws.getError()), ws.getMessage());

            api.setProcessingResult(map(ws.getProcessingResult()));
            api.setErrors(map(ws.getErrors()));
        }

        return api;
    }

    /**
     * Mapping of the WebService PublishOffer Response Object to the API SOAP
     * Object.
     *
     * @param ws WebService PublishOffer Response Object
     * @return API PublishOffer Response Object
     */
    public static PublishOfferResponse map(final net.iaeste.iws.ws.PublishOfferResponse ws) {
        PublishOfferResponse api = null;

        if (ws != null) {
            api = new PublishOfferResponse(map(ws.getError()), ws.getMessage());
        }

        return api;
    }

    /**
     * Mapping an IWS API ProcessPublishingGroup Request to the WebService SOAP
     * Object.
     *
     * @param api API ProcessPublishingGroup Request
     * @return WS ProcessPublishingGroup Request
     */
    public static net.iaeste.iws.ws.PublishingGroupRequest map(final PublishingGroupRequest api) {
        net.iaeste.iws.ws.PublishingGroupRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.PublishingGroupRequest();

            ws.setPublishingGroup(map(api.getPublishingGroup()));
            ws.setPublishingGroupId(api.getPublishingGroupId());
            ws.setAction(map(api.getAction()));
        }

        return ws;
    }

    /**
     * Mapping of the WebService FetchGroupsForSharing Response Object to the
     * API SOAP Object.
     *
     * @param ws WebService FetchGroupsForSharing Response Object
     * @return API FetchGroupsForSharing Response Object
     */
    public static FetchGroupsForSharingResponse map(final net.iaeste.iws.ws.FetchGroupsForSharingResponse ws) {
        FetchGroupsForSharingResponse api = null;

        if (ws != null) {
            api = new FetchGroupsForSharingResponse(map(ws.getError()), ws.getMessage());

            api.setGroups(mapWSGroupCollection(ws.getGroups()));
        }

        return api;
    }

    /**
     * Mapping of the WebService OfferCSVDownload Response Object to the API
     * SOAP Object.
     *
     * @param ws WebService OfferCSVDownload Response Object
     * @return API OfferCSVDownload Response Object
     */
    public static OfferCSVDownloadResponse map(final net.iaeste.iws.ws.OfferCSVDownloadResponse ws) {
        OfferCSVDownloadResponse api = null;

        if (ws != null) {
            api = new OfferCSVDownloadResponse(map(ws.getError()), ws.getMessage());

            api.setCsv(ws.getCsv());
        }

        return api;
    }

    /**
     * Mapping an IWS API FetchPublishGroups Request to the WebService SOAP
     * Object.
     *
     * @param api API FetchPublishGroups Request
     * @return WS FetchPublishGroups Request
     */
    public static net.iaeste.iws.ws.FetchPublishGroupsRequest map(final FetchPublishGroupsRequest api) {
        net.iaeste.iws.ws.FetchPublishGroupsRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.FetchPublishGroupsRequest();
            ws.setPage(map(api.getPage()));
        }

        return ws;
    }

    /**
     * Mapping of the WebService FetchPublishingGroup Response Object to the API
     * SOAP Object.
     *
     * @param ws WebService FetchPublishingGroup Response Object
     * @return API FetchPublishingGroup Response Object
     */
    public static FetchPublishingGroupResponse map(final net.iaeste.iws.ws.FetchPublishingGroupResponse ws) {
        FetchPublishingGroupResponse api = null;

        if (ws != null) {
            api = new FetchPublishingGroupResponse(map(ws.getError()), ws.getMessage());

            api.setPublishingGroups(mapWSPublishingGroupCollection(ws.getPublishingGroups()));
        }

        return api;
    }

    /**
     * Mapping an IWS API FetchPublishedGroups Request to the WebService SOAP
     * Object.
     *
     * @param api API FetchPublishedGroups Request
     * @return WS FetchPublishedGroups Request
     */
    public static net.iaeste.iws.ws.FetchPublishedGroupsRequest map(final FetchPublishedGroupsRequest api) {
        net.iaeste.iws.ws.FetchPublishedGroupsRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.FetchPublishedGroupsRequest();

            ws.setPage(map(api.getPage()));
            ws.getIdentifiers().addAll(mapStringCollection(api.getIdentifiers()));
            ws.setExchangeYear(api.getExchangeYear());
        }

        return ws;
    }

    /**
     * Mapping of the WebService FetchPublishedGroups Response Object to the
     * API SOAP Object.
     *
     * @param ws WebService FetchPublishedGroups Response Object
     * @return API FetchPublishedGroups Response Object
     */
    public static FetchPublishedGroupsResponse map(final net.iaeste.iws.ws.FetchPublishedGroupsResponse ws) {
        FetchPublishedGroupsResponse api = null;

        if (ws != null) {
            api = new FetchPublishedGroupsResponse(map(ws.getError()), ws.getMessage());

            api.setOffersGroups(mapOfferGroupList(ws.getOffersGroups()));
        }

        return api;
    }

    private static Map<String, GroupList> mapOfferGroupList(final net.iaeste.iws.ws.FetchPublishedGroupsResponse.OffersGroups ws) {
        Map<String, GroupList> api = null;

        if (ws != null) {
            api = new HashMap<>();
            for (final net.iaeste.iws.ws.FetchPublishedGroupsResponse.OffersGroups.Entry entry : ws.getEntry()) {
                api.put(entry.getKey(), map(entry.getValue()));
            }
        }

        return api;
    }

    /**
     * Mapping an IWS API PublishOffer Request to the WebService SOAP Object.
     *
     * @param api API PublishOffer Request
     * @return WS PublishOffer Request
     */
    public static net.iaeste.iws.ws.PublishOfferRequest map(final PublishOfferRequest api) {
        net.iaeste.iws.ws.PublishOfferRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.PublishOfferRequest();

            ws.setAction(map(api.getAction()));
            ws.setOfferId(api.getOfferId());
            ws.setNominationDeadline(map(api.getNominationDeadline()));
            ws.getGroupIds().addAll(mapStringCollection(api.getGroupIds()));
        }

        return ws;
    }

    /**
     * Mapping an IWS API HideForeignOffers Request to the WebService SOAP
     * Object.
     *
     * @param api API HideForeignOffers Request
     * @return WS HideForeignOffers Request
     */
    public static net.iaeste.iws.ws.HideForeignOffersRequest map(final HideForeignOffersRequest api) {
        net.iaeste.iws.ws.HideForeignOffersRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.HideForeignOffersRequest();

            ws.setAction(map(api.getAction()));
            ws.getOffers().addAll(mapStringCollection(api.getOffers()));

        }

        return ws;
    }

    /**
     * Mapping an IWS API RejectOffer Request to the WebService SOAP Object.
     *
     * @param api API RejectOffer Request
     * @return WS RejectOffer Request
     */
    public static net.iaeste.iws.ws.RejectOfferRequest map(final RejectOfferRequest api) {
        net.iaeste.iws.ws.RejectOfferRequest ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.RejectOfferRequest();

            ws.setOfferId(api.getOfferId());
        }

        return ws;
    }

    // =========================================================================
    // Internal mapping of required Collections, DTOs & Enums
    // =========================================================================

    private static List<PublishingGroup> mapWSPublishingGroupCollection(final Collection<net.iaeste.iws.ws.PublishingGroup> ws) {
        final List<PublishingGroup> api;

        if (ws != null) {
            api = new ArrayList<>(ws.size());

            for (final net.iaeste.iws.ws.PublishingGroup group : ws) {
                api.add(map(group));
            }
        } else {
            api = new ArrayList<>(0);
        }

        return api;
    }

    private static List<net.iaeste.iws.ws.OfferState> mapApiStateCollection(final Collection<OfferState> api) {
        final List<net.iaeste.iws.ws.OfferState> ws;

        if (api != null) {
            ws = new ArrayList<>(api.size());

            for (final OfferState state : api) {
                ws.add(map(state));
            }
        } else {
            ws = new ArrayList<>(0);
        }

        return ws;
    }

    private static Map<String, OfferCSVUploadResponse.ProcessingResult> map(final net.iaeste.iws.ws.OfferCSVUploadResponse.ProcessingResult ws) {
        Map<String, OfferCSVUploadResponse.ProcessingResult> api = null;

        if (ws != null) {
            api = new HashMap<>();
            for (final net.iaeste.iws.ws.OfferCSVUploadResponse.ProcessingResult.Entry entry : ws.getEntry()) {
                api.put(entry.getKey(), map(entry.getValue()));
            }
        }

        return api;
    }

    private static Map<String, CSVProcessingErrors> map(final net.iaeste.iws.ws.OfferCSVUploadResponse.Errors ws) {
        Map<String, CSVProcessingErrors> api = null;

        if (ws != null) {
            api = new HashMap<>();

            for (final net.iaeste.iws.ws.OfferCSVUploadResponse.Errors.Entry entry : ws.getEntry()) {
                api.put(entry.getKey(), map(entry.getValue()));
            }
        }

        return api;
    }

    private static CSVProcessingErrors map(final CsvProcessingErrors ws) {
        CSVProcessingErrors api = null;

        if ((ws != null) && (ws.getCsvErrors() != null)) {
            api = new CSVProcessingErrors();

            for (final CsvProcessingErrors.CsvErrors.Entry entry : ws.getCsvErrors().getEntry()) {
                api.put(entry.getKey(), entry.getValue());
            }
        }

        return api;
    }

    static Employer map(final net.iaeste.iws.ws.Employer ws) {
        Employer api = null;

        if (ws != null) {
            api = new Employer();

            api.setEmployerId(ws.getEmployerId());
            api.setGroup(map(ws.getGroup()));
            api.setName(ws.getName());
            api.setDepartment(ws.getDepartment());
            api.setBusiness(ws.getBusiness());
            api.setAddress(map(ws.getAddress()));
            api.setEmployeesCount(ws.getEmployeesCount());
            api.setWebsite(ws.getWebsite());
            api.setWorkingPlace(ws.getWorkingPlace());
            api.setCanteen(ws.isCanteen());
            api.setNearestAirport(ws.getNearestAirport());
            api.setNearestPublicTransport(ws.getNearestPublicTransport());
            api.setOfferReferenceNumbers(mapStringCollection(ws.getOfferReferenceNumbers()));
            api.setModified(map(ws.getModified()));
            api.setCreated(map(ws.getCreated()));
        }

        return api;
    }

    static net.iaeste.iws.ws.Employer map(final Employer api) {
        net.iaeste.iws.ws.Employer ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.Employer();

            ws.setEmployerId(api.getEmployerId());
            ws.setGroup(map(api.getGroup()));
            ws.setName(api.getName());
            ws.setDepartment(api.getDepartment());
            ws.setBusiness(api.getBusiness());
            ws.setAddress(map(api.getAddress()));
            ws.setEmployeesCount(api.getEmployeesCount());
            ws.setWebsite(api.getWebsite());
            ws.setWorkingPlace(api.getWorkingPlace());
            ws.setCanteen(api.getCanteen());
            ws.setNearestAirport(api.getNearestAirport());
            ws.setNearestPublicTransport(api.getNearestPublicTransport());
            ws.setModified(map(api.getModified()));
            ws.setCreated(map(api.getCreated()));
        }

        return ws;
    }

    static net.iaeste.iws.ws.Offer map(final Offer api) {
        net.iaeste.iws.ws.Offer ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.Offer();

            ws.setOfferId(api.getOfferId());
            ws.setRefNo(api.getRefNo());
            ws.setOfferType(map(api.getOfferType()));
            ws.setExchangeType(map(api.getExchangeType()));
            ws.setOldRefNo(api.getOldRefNo());
            ws.setEmployer(map(api.getEmployer()));
            ws.setWorkDescription(api.getWorkDescription());
            ws.setTypeOfWork(map(api.getTypeOfWork()));
            ws.setWeeklyHours(api.getWeeklyHours());
            ws.setDailyHours(api.getDailyHours());
            ws.setWeeklyWorkDays(api.getWeeklyWorkDays());
            ws.getStudyLevels().addAll(mapStudyLevelCollection(api.getStudyLevels()));
            ws.getFieldOfStudies().addAll(mapAPIFieldOfStudyCollection(api.getFieldOfStudies()));
            ws.getSpecializations().addAll(mapStringCollection(api.getSpecializations()));
            ws.setPreviousTrainingRequired(api.getPreviousTrainingRequired());
            ws.setOtherRequirements(api.getOtherRequirements());
            ws.setMinimumWeeks(api.getMinimumWeeks());
            ws.setMaximumWeeks(api.getMaximumWeeks());
            ws.setPeriod1(map(api.getPeriod1()));
            ws.setPeriod2(map(api.getPeriod2()));
            ws.setUnavailable(map(api.getUnavailable()));
            ws.setLanguage1(map(api.getLanguage1()));
            ws.setLanguage1Level(map(api.getLanguage1Level()));
            ws.setLanguage1Operator(map(api.getLanguage1Operator()));
            ws.setLanguage2(map(api.getLanguage2()));
            ws.setLanguage2Level(map(api.getLanguage2Level()));
            ws.setLanguage2Operator(map(api.getLanguage2Operator()));
            ws.setLanguage3(map(api.getLanguage3()));
            ws.setLanguage3Level(map(api.getLanguage3Level()));
            ws.setPayment(api.getPayment());
            ws.setPaymentFrequency(map(api.getPaymentFrequency()));
            ws.setCurrency(map(api.getCurrency()));
            ws.setDeduction(api.getDeduction());
            ws.setLivingCost(api.getLivingCost());
            ws.setLivingCostFrequency(map(api.getLivingCostFrequency()));
            ws.setLodgingBy(api.getLodgingBy());
            ws.setLodgingCost(api.getLodgingCost());
            ws.setLodgingCostFrequency(map(api.getLodgingCostFrequency()));
            ws.setNominationDeadline(map(api.getNominationDeadline()));
            ws.setNumberOfHardCopies(api.getNumberOfHardCopies());
            ws.setAdditionalInformation(api.getAdditionalInformation());
            ws.setPrivateComment(api.getPrivateComment());
            ws.setStatus(map(api.getStatus()));
            ws.setModified(map(api.getModified()));
            ws.setCreated(map(api.getCreated()));
            ws.setNsFirstname(api.getNsFirstname());
            ws.setNsLastname(api.getNsLastname());
            ws.setShared(map(api.getShared()));
            ws.setHidden(api.isHidden());
        }

        return ws;
    }

    static Offer map(final net.iaeste.iws.ws.Offer ws) {
        Offer api = null;

        if (ws != null) {
            api = new Offer();

            api.setOfferId(ws.getOfferId());
            api.setRefNo(ws.getRefNo());
            api.setOfferType(map(ws.getOfferType()));
            api.setExchangeType(map(ws.getExchangeType()));
            api.setOldRefNo(ws.getOldRefNo());
            api.setEmployer(map(ws.getEmployer()));
            api.setWorkDescription(ws.getWorkDescription());
            api.setTypeOfWork(map(ws.getTypeOfWork()));
            api.setWeeklyHours(ws.getWeeklyHours());
            api.setDailyHours(ws.getDailyHours());
            api.setWeeklyWorkDays(ws.getWeeklyWorkDays());
            api.setStudyLevels(mapStudyLevelCollectionToSet(ws.getStudyLevels()));
            api.setFieldOfStudies(mapFieldOfStudyCollection(ws.getFieldOfStudies()));
            api.setSpecializations(new HashSet<>(mapStringCollection(ws.getSpecializations())));
            api.setPreviousTrainingRequired(ws.isPreviousTrainingRequired());
            api.setOtherRequirements(ws.getOtherRequirements());
            api.setMinimumWeeks(ws.getMinimumWeeks());
            api.setMaximumWeeks(ws.getMaximumWeeks());
            api.setPeriod1(map(ws.getPeriod1()));
            api.setPeriod2(map(ws.getPeriod2()));
            api.setUnavailable(map(ws.getUnavailable()));
            api.setLanguage1(map(ws.getLanguage1()));
            api.setLanguage1Level(map(ws.getLanguage1Level()));
            api.setLanguage1Operator(map(ws.getLanguage1Operator()));
            api.setLanguage2(map(ws.getLanguage2()));
            api.setLanguage2Level(map(ws.getLanguage2Level()));
            api.setLanguage2Operator(map(ws.getLanguage2Operator()));
            api.setLanguage3(map(ws.getLanguage3()));
            api.setLanguage3Level(map(ws.getLanguage3Level()));
            api.setPayment(ws.getPayment());
            api.setPaymentFrequency(map(ws.getPaymentFrequency()));
            api.setCurrency(map(ws.getCurrency()));
            api.setDeduction(ws.getDeduction());
            api.setLivingCost(ws.getLivingCost());
            api.setLivingCostFrequency(map(ws.getLivingCostFrequency()));
            api.setLodgingBy(ws.getLodgingBy());
            api.setLodgingCost(ws.getLodgingCost());
            api.setLodgingCostFrequency(map(ws.getLodgingCostFrequency()));
            api.setNominationDeadline(map(ws.getNominationDeadline()));
            api.setNumberOfHardCopies(ws.getNumberOfHardCopies());
            api.setAdditionalInformation(ws.getAdditionalInformation());
            api.setPrivateComment(ws.getPrivateComment());
            api.setStatus(map(ws.getStatus()));
            api.setModified(map(ws.getModified()));
            api.setCreated(map(ws.getCreated()));
            api.setNsFirstname(ws.getNsFirstname());
            api.setNsLastname(ws.getNsLastname());
            api.setShared(map(ws.getShared()));
            api.setHidden(ws.isHidden());
        }

        return api;
    }

    private static GroupList map(final net.iaeste.iws.ws.GroupList ws) {
        GroupList api = null;

        if (ws != null) {
            api = new GroupList();

            for (final Group group : ws.getGroups()) {
                api.add(map(group));
            }
        }

        return api;
    }

    private static PublishingGroup map(final net.iaeste.iws.ws.PublishingGroup ws) {
        PublishingGroup api = null;

        if (ws != null) {
            api = new PublishingGroup();

            api.setPublishingGroupId(ws.getPublishingGroupId());
            api.setName(ws.getName());
            api.setGroups(mapWSGroupCollection(ws.getGroups()));
        }

        return api;
    }

    private static net.iaeste.iws.ws.PublishingGroup map(final PublishingGroup api) {
        net.iaeste.iws.ws.PublishingGroup ws = null;

        if (api != null) {
            ws = new net.iaeste.iws.ws.PublishingGroup();

            ws.setPublishingGroupId(api.getPublishingGroupId());
            ws.setName(api.getName());
            ws.getGroups().addAll(mapAPIGroupCollection(api.getGroups()));
        }

        return ws;
    }

    private static Map<OfferState, Integer> mapWSStatisticsMap(final net.iaeste.iws.ws.OfferStatistics.Statistics ws) {
        final Map<OfferState, Integer> api = new EnumMap<>(OfferState.class);

        if (ws != null) {
            for (final net.iaeste.iws.ws.OfferStatistics.Statistics.Entry entry : ws.getEntry()) {
                api.put(map(entry.getKey()), entry.getValue());
            }
        }

        return api;
    }

    private static FieldDelimiter map(final OfferCSVUploadRequest.FieldDelimiter api) {
        return (api != null) ? FieldDelimiter.valueOf(api.name()) : null;
    }

    private static OfferCSVUploadResponse.ProcessingResult map(final ProcessingResult ws) {
        return (ws != null) ? OfferCSVUploadResponse.ProcessingResult.valueOf(ws.value()) : null;
    }

    private static net.iaeste.iws.ws.EmployerFetchType map(final EmployerFetchType api) {
        return (api != null) ? net.iaeste.iws.ws.EmployerFetchType.valueOf(api.name()) : null;
    }

    private static net.iaeste.iws.ws.FetchType map(final FetchType api) {
        return (api != null) ? net.iaeste.iws.ws.FetchType.valueOf(api.name()) : null;
    }

    private static LanguageOperator map(final net.iaeste.iws.ws.LanguageOperator ws) {
        return (ws != null) ? LanguageOperator.valueOf(ws.value()) : null;
    }

    private static net.iaeste.iws.ws.LanguageOperator map(final LanguageOperator api) {
        return (api != null) ? net.iaeste.iws.ws.LanguageOperator.valueOf(api.name()) : null;
    }

    private static OfferType map(final net.iaeste.iws.ws.OfferType ws) {
        return (ws != null) ? OfferType.valueOf(ws.value()) : null;
    }

    private static net.iaeste.iws.ws.OfferType map(final OfferType api) {
        return (api != null) ? net.iaeste.iws.ws.OfferType.valueOf(api.name()) : null;
    }

    private static ExchangeType map(final net.iaeste.iws.ws.ExchangeType ws) {
        return (ws != null) ? ExchangeType.valueOf(ws.value()) : null;
    }

    private static net.iaeste.iws.ws.ExchangeType map(final ExchangeType api) {
        return (api != null) ? net.iaeste.iws.ws.ExchangeType.valueOf(api.name()) : null;
    }

    private static TypeOfWork map(final net.iaeste.iws.ws.TypeOfWork ws) {
        return (ws != null) ? TypeOfWork.valueOf(ws.value()) : null;
    }

    private static net.iaeste.iws.ws.TypeOfWork map(final TypeOfWork api) {
        return (api != null) ? net.iaeste.iws.ws.TypeOfWork.valueOf(api.name()) : null;
    }

    private static PaymentFrequency map(final net.iaeste.iws.ws.PaymentFrequency ws) {
        return (ws != null) ? PaymentFrequency.valueOf(ws.value()) : null;
    }

    private static net.iaeste.iws.ws.PaymentFrequency map(final PaymentFrequency api) {
        return (api != null) ? net.iaeste.iws.ws.PaymentFrequency.valueOf(api.name()) : null;
    }
}
