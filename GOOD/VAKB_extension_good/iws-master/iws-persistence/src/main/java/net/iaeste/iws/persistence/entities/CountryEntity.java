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
package net.iaeste.iws.persistence.entities;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.enums.Currency;
import net.iaeste.iws.api.enums.Membership;
import net.iaeste.iws.api.enums.MonitoringLevel;
import net.iaeste.iws.persistence.Externable;
import net.iaeste.iws.persistence.monitoring.Monitored;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entity for the Countries table. Contains all known countries, including their
 * relationship with IAESTE.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@NamedQueries({
        @NamedQuery(name = "country.findAll",
                query = "select c from CountryEntity c"),
        @NamedQuery(name = "country.findByName",
                query = "select c from CountryEntity c " +
                        "where lower(c.countryName) = lower(:name)"),
        @NamedQuery(name = "country.findByCountryCode",
                query = "select c from CountryEntity c " +
                        "where c.countryCode = :code"),
        @NamedQuery(name = "country.findByCountryCodes",
                query = "select c from CountryEntity c " +
                        "where c.countryCode in :codes")
})
@Entity
@Table(name = "countries")
@Monitored(name = "Country", level = MonitoringLevel.DETAILED)
public final class CountryEntity extends AbstractUpdateable<CountryEntity> implements Externable<CountryEntity> {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id = null;

    @Column(name = "country_code", length = 2, unique = true, nullable = false, updatable = false)
    private String countryCode = null;

    @Monitored(name="Country name", level = MonitoringLevel.DETAILED)
    @Column(name = "country_name", length = 100, unique = true, nullable = false)
    private String countryName = null;

    @Monitored(name="Country name full", level = MonitoringLevel.DETAILED)
    @Column(name = "country_name_full", length = 100)
    private String countryNameFull = null;

    @Monitored(name="Country name native", level = MonitoringLevel.DETAILED)
    @Column(name = "country_name_native", length = 100)
    private String countryNameNative = null;

    @Monitored(name="Country nationality", level = MonitoringLevel.DETAILED)
    @Column(name = "nationality", length = 100)
    private String nationality = null;

    @Monitored(name="Country citizens", level = MonitoringLevel.DETAILED)
    @Column(name = "citizens")
    private String citizens = null;

    @Monitored(name="Country phone code", level = MonitoringLevel.DETAILED)
    @Column(name = "phonecode")
    private String phonecode = null;

    @Monitored(name="Country currency", level = MonitoringLevel.DETAILED)
    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency = null;

    @Monitored(name="Country languages", level = MonitoringLevel.DETAILED)
    @Column(name = "languages")
    private String languages = null;

    @Monitored(name="Country membership", level = MonitoringLevel.DETAILED)
    @Enumerated(EnumType.STRING)
    @Column(name = "membership")
    private Membership membership = Membership.LISTED;

    @Monitored(name="Country member since", level = MonitoringLevel.DETAILED)
    @Column(name = "member_since")
    private Integer memberSince = null;

    /**
     * Last time the Entity was modified.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified", nullable = false)
    private Date modified = new Date();

    /**
     * Timestamp when the Entity was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    private Date created = new Date();

    // =========================================================================
    // Entity Setters & Getters
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExternalId(final String externalId) {
        this.countryCode = externalId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryNameFull(final String countryNameFull) {
        this.countryNameFull = countryNameFull;
    }

    public String getCountryNameFull() {
        return countryNameFull;
    }

    public void setCountryNameNative(final String countryNameNative) {
        this.countryNameNative = countryNameNative;
    }

    public String getCountryNameNative() {
        return countryNameNative;
    }

    public void setNationality(final String nationality) {
        this.nationality = nationality;
    }

    public String getNationality() {
        return nationality;
    }

    public void setCitizens(final String citizens) {
        this.citizens = citizens;
    }

    public String getCitizens() {
        return citizens;
    }

    public void setPhonecode(final String phonecode) {
        this.phonecode = phonecode;
    }

    public String getPhonecode() {
        return phonecode;
    }

    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setLanguages(final String languages) {
        this.languages = languages;
    }

    public String getLanguages() {
        return languages;
    }

    public void setMembership(final Membership membership) {
        this.membership = membership;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMemberSince(final Integer memberSince) {
        this.memberSince = memberSince;
    }

    public Integer getMemberSince() {
        return memberSince;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModified(final Date modified) {
        this.modified = modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getModified() {
        return modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getCreated() {
        return created;
    }

    // =========================================================================
    // Entity Standard Methods
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean diff(final CountryEntity obj) {
        // Until properly implemented, better return true to avoid that we're
        // missing updates!
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(final CountryEntity obj) {
        if (canMerge(obj)) {
            countryName = which(countryName, obj.countryName);
            countryNameFull = which(countryNameFull, obj.countryNameFull);
            countryNameNative = which(countryNameNative, obj.countryNameNative);
            nationality = which(nationality, obj.nationality);
            citizens = which(citizens, obj.citizens);
            phonecode = which(phonecode, obj.phonecode);
            currency = which(currency, obj.currency);
            languages = which(languages, obj.languages);
            membership = which(membership, obj.membership);
            memberSince = which(memberSince, obj.memberSince);
        }
    }
}
