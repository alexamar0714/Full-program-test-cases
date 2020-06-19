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

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.constants.IWSErrors;
import net.iaeste.iws.api.enums.exchange.OfferFields;
import net.iaeste.iws.api.enums.exchange.OfferState;
import net.iaeste.iws.api.exceptions.IWSException;
import net.iaeste.iws.api.requests.exchange.FetchOffersRequest;
import net.iaeste.iws.api.responses.exchange.OfferCSVDownloadResponse;
import net.iaeste.iws.api.util.Page;
import net.iaeste.iws.common.configuration.Settings;
import net.iaeste.iws.core.exceptions.PermissionException;
import net.iaeste.iws.core.transformers.ViewTransformer;
import net.iaeste.iws.persistence.Authentication;
import net.iaeste.iws.persistence.ExchangeDao;
import net.iaeste.iws.persistence.ViewsDao;
import net.iaeste.iws.persistence.views.IWSView;
import net.iaeste.iws.persistence.views.OfferView;
import net.iaeste.iws.persistence.views.SharedOfferView;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.1
 */
public final class ExchangeCSVFetchService extends CommonCSVService<ExchangeDao> {

    private final ViewsDao viewsDao;

    /**
     * Default Constructor.
     *
     * @param settings IWS Settings
     * @param dao      Exchange DAO to use for the processing
     * @param viewsDao Views DAO to read data via Database Views
     */
    public ExchangeCSVFetchService(final Settings settings, final ExchangeDao dao, final ViewsDao viewsDao) {
        super(settings, dao);

        this.viewsDao = viewsDao;
    }

    /**
     * Primary implementation of the Offer Download, which will result in a
     * list of either Domestic or Shared offers, depending on the request
     * details.
     *
     * @param authentication User Authentication Information
     * @param request        Request Object
     * @return Response Object
     */
    public OfferCSVDownloadResponse downloadOffers(final Authentication authentication, final FetchOffersRequest request) {
        final OfferCSVDownloadResponse response = new OfferCSVDownloadResponse();
        switch (request.getFetchType()) {
            case DOMESTIC:
                response.setCsv(findDomesticOffers(authentication, request));
                break;
            case SHARED:
                response.setCsv(findSharedOffers(authentication, request));
                break;
            default:
                throw new PermissionException("The search type is not permitted.");
        }

        return response;
    }

    private String findDomesticOffers(final Authentication authentication, final FetchOffersRequest request) {
        final List<String> offerIds = request.getIdentifiers();
        final Page page = request.getPage();
        final Integer exchangeYear = request.getExchangeYear();

        final List<OfferView> found;
        if (offerIds.isEmpty()) {
            //paging could make a problem here if it returns only some offers
            final Set<OfferState> states = EnumSet.allOf(OfferState.class);
            states.remove(OfferState.DELETED);
            found = viewsDao.findDomesticOffers(authentication, exchangeYear, states, false, page);
        } else {
            found = viewsDao.findDomesticOffersByOfferIds(authentication, exchangeYear, offerIds);
        }

        return convertOffersToCsv(found, OfferFields.Type.DOMESTIC);
    }

    private String findSharedOffers(final Authentication authentication, final FetchOffersRequest request) {
        final List<String> offerIds = request.getIdentifiers();
        final Page page = request.getPage();
        final Integer exchangeYear = request.getExchangeYear();
        final Set<OfferState> states = EnumSet.allOf(OfferState.class);
        states.remove(OfferState.DELETED);

        final List<SharedOfferView> found;
        if (offerIds.isEmpty()) {
            //paging could make a problem here if it returns only some offers
            found = viewsDao.findSharedOffers(authentication, exchangeYear, states, false, page);
        } else {
            found = viewsDao.findSharedOffersByOfferIds(authentication, exchangeYear, offerIds);
        }

        return convertOffersToCsv(found, OfferFields.Type.FOREIGN);
    }

    private static <V extends IWSView> String convertOffersToCsv(final List<V> offers, final OfferFields.Type type) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             OutputStreamWriter streamWriter = new OutputStreamWriter(stream, IWSConstants.DEFAULT_ENCODING);
             BufferedWriter writer = new BufferedWriter(streamWriter);
             CSVPrinter printer = getDefaultCsvPrinter(writer)) {
            printer.printRecord(createFirstRow(type));

            for (final V offer : offers) {
                printer.printRecord(ViewTransformer.transformOfferToObjectList(offer, type));
            }

            // Should suffice to flush the Writer
            writer.flush();
            return new String(stream.toByteArray(), IWSConstants.DEFAULT_ENCODING);
        } catch (IOException e) {
            throw new IWSException(IWSErrors.PROCESSING_FAILURE, "Serialization to CSV failed", e);
        }
    }

    private static CSVPrinter getDefaultCsvPrinter(final Appendable output) {
        try {
            return CSVFormat.RFC4180
                            .withDelimiter(DELIMITER.getDescription())
                            .withNullString("")
                            .print(output);
        } catch (IOException e) {
            throw new IWSException(IWSErrors.PROCESSING_FAILURE, "Creating CSVPrinter failed", e);
        }
    }
}
