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
package net.iaeste.iws.api.requests.exchange;

import static net.iaeste.iws.api.util.Immutable.immutableList;
import static net.iaeste.iws.api.util.Immutable.immutableSet;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.enums.FetchType;
import net.iaeste.iws.api.enums.exchange.OfferState;
import net.iaeste.iws.api.util.Paginatable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fetchOffersRequest", propOrder = { "fetchType", "identifiers", "exchangeYear", "states", "retrieveCurrentAndNextExchangeYear" })
public final class FetchOffersRequest extends Paginatable {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    /** Allowed States for an Offer to be retrieved. */
    private static final Set<OfferState> ALLOWED = EnumSet.of(
            OfferState.NEW,          OfferState.OPEN,        OfferState.SHARED,
            OfferState.APPLICATIONS, OfferState.NOMINATIONS, OfferState.CLOSED,
            OfferState.COMPLETED,    OfferState.AT_EMPLOYER, OfferState.ACCEPTED,
            OfferState.EXPIRED,      OfferState.REJECTED);

    @XmlElement(required = true) private FetchType fetchType = null;
    @XmlElement(required = true) private final List<String> identifiers = new ArrayList<>(0);
    @XmlElement(required = true) private Integer exchangeYear = calculateExchangeYear();
    @XmlElement(required = true) private final Set<OfferState> states = EnumSet.copyOf(ALLOWED);
    @XmlElement(required = true) private boolean retrieveCurrentAndNextExchangeYear = false;

    // =========================================================================
    // Object Constructors
    // =========================================================================

    /**
     * <p>Empty Constructor, to use if the setters are invoked. This is required
     * for WebServices to work properly.</p>
     */
    public FetchOffersRequest() {
        // Required for WebServices to work. Comment added to please Sonar.
    }

    /**
     * <p>Default Constructor.</p>
     *
     * @param fetchType The Fetch Type
     */
    public FetchOffersRequest(final FetchType fetchType) {
        setFetchType(fetchType);
    }

    // =========================================================================
    // Standard Setters & Getters
    // =========================================================================

    /**
     * <p>Sets the mandatory FetchType for the CSV Downloading of Offers, the
     * type can be either Domestic (a Committee's own Offers) or Shared (Offers from
     * other Committee's). However, the value cannot be null.</p>
     *
     * <p>The method will thrown an {@code IllegalArgumentException} if the given
     * value is null.</p>
     *
     * @param fetchType Type of Offers to be fetched
     * @throws IllegalArgumentException if the parameter is null
     */
    public void setFetchType(final FetchType fetchType) {
        ensureNotNull("fetchType", fetchType);
        this.fetchType = fetchType;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    /**
     * <p>Sets a list of Identifiers, meaning either the Id of the Offers or
     * their Reference Number, which both can be used to uniquely identify an
     * Offer.</p>
     *
     * <p>The Identifiers must either belong to the Country (if the FetchType is
     * domestic) or the Identifiers must belong to Offers shared (if the
     * FetchType is shared). If the list of Identifiers is empty, then all
     * Offers matching the FetchType and Exchange Year will be retrieved.</p>
     *
     * <p>The method will thrown an {@code IllegalArgumentException} if the given
     * value is null.</p>
     *
     * @param identifiers List of OfferId's or Reference Numbers to be fetched, may be empty
     * @throws IllegalArgumentException if the parameter is null
     */
    public void setIdentifiers(final List<String> identifiers) {
        ensureNotNullAndValidIdentifiers("identifiers", identifiers);
        this.identifiers.addAll(identifiers);
    }

    public List<String> getIdentifiers() {
        return immutableList(identifiers);
    }

    public void setExchangeYear(final Integer exchangeYear) {
        ensureNotNullAndWithinLimits("exchangeYear", exchangeYear, IWSConstants.FOUNDING_YEAR, calculateExchangeYear());
        this.exchangeYear = exchangeYear;
    }

    public Integer getExchangeYear() {
        return exchangeYear;
    }

    /**
     * <p>Sets the different States, that an Offer can have. By default all
     * States are allowed.</p>
     *
     * @param states The States to use in the filter
     */
    public void setStates(final Set<OfferState> states) {
        ensureNotNullAndContains("states", states, ALLOWED);
        this.states.retainAll(states);
    }

    public Set<OfferState> getStates() {
        return immutableSet(states);
    }

    /**
     * <p>Sets the flag to determine if Offers from both the current Exchange
     * Year and the new Exchange Year shall be fetched. This option will allow
     * Users to see Offers from both years following September 1st, when the new
     * Exchange Year starts. By default, this is set to false, so only the
     * Exchange year counts.</p>
     *
     * <p>This value is mandatory. If set to null, then a
     * {$code IllegalArgumentException} is thrown.</p>
     *
     * @param retrieveCurrentAndNextExchangeYear True if both shall be retrieved
     * @throws IllegalArgumentException if the value is undefined, i.e. null
     */
    public void setRetrieveCurrentAndNextExchangeYear(final boolean retrieveCurrentAndNextExchangeYear) {
        ensureNotNull("retrieveCurrentAndNextExchangeYear", retrieveCurrentAndNextExchangeYear);
        this.retrieveCurrentAndNextExchangeYear = retrieveCurrentAndNextExchangeYear;
    }

    public boolean getRetrieveCurrentAndNextExchangeYear() {
        return retrieveCurrentAndNextExchangeYear;
    }

    // =========================================================================
    // Standard Request Methods
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> validate() {
        final Map<String, String> validation = new HashMap<>(0);

        isNotNull(validation, "fetchType", fetchType);
        isNotNull(validation, "identifiers", identifiers);
        isNotNull(validation, "exchangeYear", exchangeYear);
        isNotNull(validation, "states", states);
        isNotNull(validation, "retrieveCurrentAndNextExchangeYear", retrieveCurrentAndNextExchangeYear);

        return validation;
    }
}
