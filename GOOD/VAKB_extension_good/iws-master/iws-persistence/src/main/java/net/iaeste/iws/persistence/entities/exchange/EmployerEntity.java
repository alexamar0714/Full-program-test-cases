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
package net.iaeste.iws.persistence.entities.exchange;

import net.iaeste.iws.api.constants.IWSConstants;
import net.iaeste.iws.api.enums.MonitoringLevel;
import net.iaeste.iws.persistence.Externable;
import net.iaeste.iws.persistence.entities.AbstractUpdateable;
import net.iaeste.iws.persistence.entities.AddressEntity;
import net.iaeste.iws.persistence.entities.GroupEntity;
import net.iaeste.iws.persistence.monitoring.Monitored;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Employer Entity, contains the common fields for the Offers for a given
 * Employer.
 *
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@NamedQueries({
        @NamedQuery(name = "employer.findAllForGroup",
                query = "select e from EmployerEntity e " +
                        "where e.group.id = :gid"),
        @NamedQuery(name = "employer.findByExternalId",
                query = "select e from EmployerEntity e " +
                        "where e.externalId = :eid" +
                        "  and e.group.parentId = :pid"),
        @NamedQuery(name = "employer.findEmployerByValues",
                query = "select e from EmployerEntity e " +
                        "where e.group.id = :gid" +
                        "  and lower(trim(both from e.name)) = lower(trim(both from :name))" +
                        "  and lower(trim(both from e.department)) = lower(trim(both from :department))" +
                        "  and lower(trim(both from e.workingPlace)) = lower(trim(both from :workingPlace))")
})
@Entity
@Table(name = "employers")
@Monitored(name = "Employer", level = MonitoringLevel.DETAILED)
public final class EmployerEntity extends AbstractUpdateable<EmployerEntity> implements Externable<EmployerEntity> {

    /** {@link IWSConstants#SERIAL_VERSION_UID}. */
    private static final long serialVersionUID = IWSConstants.SERIAL_VERSION_UID;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id = null;

    /**
     * The content of this Entity is exposed externally, however to avoid that
     * someone tries to spoof the system by second guessing our Sequence values,
     * An External Id is used, the External Id is a Unique UUID value, which in
     * all external references is referred to as the "Id". Although this can be
     * classified as StO (Security through Obscurity), there is no need to
     * expose more information than necessary.
     */
    @Column(name = "external_id", length = 36, unique = true, nullable = false, updatable = false)
    private String externalId = null;

    @ManyToOne(targetEntity = GroupEntity.class)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private GroupEntity group = null;

    @Monitored(name="Employer name", level = MonitoringLevel.DETAILED)
    @Column(name = "name", nullable = false)
    private String name = null;

    @Monitored(name="Employer department", level = MonitoringLevel.DETAILED)
    @Column(name = "department", nullable = false)
    private String department = "";

    @Monitored(name="Employer business", level = MonitoringLevel.DETAILED)
    @Column(name = "business")
    private String business = null;

    @Monitored(name="Employer working place", level = MonitoringLevel.DETAILED)
    @Column(name = "working_place", nullable = false)
    private String workingPlace = "";

    @ManyToOne(targetEntity = AddressEntity.class)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private AddressEntity address = null;

    @Monitored(name="Employer number of employees", level = MonitoringLevel.DETAILED)
    @Column(name = "number_of_employees", length = 25)
    private String numberOfEmployees = null;

    @Monitored(name="Employer website", level = MonitoringLevel.DETAILED)
    @Column(name = "website")
    private String website = null;

    @Monitored(name="Employer canteen", level = MonitoringLevel.DETAILED)
    @Column(name = "canteen")
    private Boolean canteen = Boolean.FALSE;

    @Monitored(name="Employer nearest airport", level = MonitoringLevel.DETAILED)
    @Column(name = "nearest_airport")
    private String nearestAirport = null;

    @Monitored(name="Employer nearest public transport", level = MonitoringLevel.DETAILED)
    @Column(name = "nearest_public_transport")
    private String nearestPublicTransport = null;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return externalId;
    }

    public void setGroup(final GroupEntity group) {
        this.group = group;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDepartment(final String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setBusiness(final String business) {
        this.business = business;
    }

    public String getBusiness() {
        return business;
    }

    public void setWorkingPlace(final String workingPlace) {
        this.workingPlace = workingPlace;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public void setAddress(final AddressEntity address) {
        this.address = address;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setNumberOfEmployees(final String numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public String getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setWebsite(final String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setCanteen(final Boolean canteen) {
        this.canteen = canteen;
    }

    public Boolean getCanteen() {
        return canteen;
    }

    public void setNearestAirport(final String nearestAirport) {
        this.nearestAirport = nearestAirport;
    }

    public String getNearestAirport() {
        return nearestAirport;
    }

    public void setNearestPublicTransport(final String nearestPublicTransport) {
        this.nearestPublicTransport = nearestPublicTransport;
    }

    public String getNearestPublicTransport() {
        return nearestPublicTransport;
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
    // Standard Entity Methods
    // =========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean diff(final EmployerEntity obj) {
        int changes = 0;

        changes += different(name, obj.name);
        changes += different(department, obj.department);
        changes += different(business, obj.business);
        changes += different(workingPlace, obj.workingPlace);
        changes += different(numberOfEmployees, obj.numberOfEmployees);
        changes += different(website, obj.website);
        changes += different(canteen, obj.canteen);
        changes += different(nearestAirport, obj.nearestAirport);
        changes += different(nearestPublicTransport, obj.nearestPublicTransport);

        return changes == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(final EmployerEntity obj) {
        if (canMerge(obj)) {
            name = which(name, obj.name);
            department = which(department, obj.department);
            business = which(business, obj.business);
            workingPlace = which(workingPlace, obj.workingPlace);
            numberOfEmployees = which(numberOfEmployees, obj.numberOfEmployees);
            website = which(website, obj.website);
            canteen = which(canteen, obj.canteen);
            nearestAirport = which(nearestAirport, obj.nearestAirport);
            nearestPublicTransport = which(nearestPublicTransport, obj.nearestPublicTransport);
        }
    }
}
