/*
 *    Copyright 2003, 2004, 2005, 2006 Research Triangle Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package org.cidrz.webapp.dynasite.valueobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Logs updates to this application - this includes sql updates
 *
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="197a727c75757c60596b6d7037766b7e">[email protected]</a>">Chris Kelley</a>
 *         Date: Dec 28, 2005
 *         Time: 12:50:46 PM
 */

/**
 * @hibernate.class table="appupdate"
 * mutable="true"
 */
public class ApplicationUpdate {
    private Long id;
    private Long updateid;
    private Timestamp dateposted;
    private Timestamp dateinstalled;
    private String type;
    private String job;
    private String message;
    private Boolean logJob;
    private ArrayList values;	// for sql parameters.
    private Boolean intendedSite;
    private Boolean supportedVersion;
    private String siteAbbrev;
    
    public ApplicationUpdate(Long id, Long updateid, Timestamp dateposted, Timestamp dateinstalled, String type,
			String job, String message, Boolean logJob, ArrayList values, Boolean intendedSite, Boolean supportedVersion, String siteAbbrev) {
		super();
		this.id = id;
		this.updateid = updateid;
		this.dateposted = dateposted;
		this.dateinstalled = dateinstalled;
		this.type = type;
		this.job = job;
		this.message = message;
		this.logJob = logJob;
		this.values = values;
		this.intendedSite = intendedSite;
		this.supportedVersion = supportedVersion;
		this.siteAbbrev = siteAbbrev;
	}
    
	public ApplicationUpdate() {
		super();
	}

    /**
     * @return
     * @hibernate.id unsaved-value="0"
     * generator-class="identity"
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

     /**
     * @return id from admin db record
     * @hibernate.property column="updateid"
     */
    public Long getUpdateid() {
        return updateid;
    }

    public void setUpdateid(Long updateid) {
        this.updateid = updateid;
    }

    /**
     * @return date this item was created on the master or dev system
     * @hibernate.property column="dateposted"
     * not-null="false"
     * unique="false"
     */
    public Timestamp getDateposted() {
        return dateposted;
    }

    public void setDateposted(Timestamp dateposted) {
        this.dateposted = dateposted;
    }

    /**
     * @return date this was installed on this system
     * @hibernate.property column="dateinstalled"
     * not-null="false"
     * unique="false"
     */
    public Timestamp getDateinstalled() {
        return dateinstalled;
    }

    public void setDateinstalled(Timestamp dateinstalled) {
        this.dateinstalled = dateinstalled;
    }

    /**
     * @return type of object to be replicated.  Types: S (SQL), W (war)
     * @hibernate.property column="type"
     * not-null="false"
     * unique="false"
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return job details - sql statement(s) or other
     * @hibernate.property column="job"
     * not-null="false"
     * unique="false"
     */

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    /**
     * @return message if any
     * @hibernate.property column="message"
     * not-null="false"
     * unique="false"
     */

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Used in sql form to indicate if job needs to be saved to the database. Notmally appupdates are automatically
     * logged; however the system can have jobs manually added.
     *
     * @return logJob
     */
    public Boolean getLogJob() {
        return logJob;
    }

    public void setLogJob(Boolean logJob) {
        this.logJob = logJob;
    }
    
    /**
	 * @return the values
	 */
	public ArrayList getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(ArrayList values) {
		this.values = values;
	}
	
	public Boolean getIntendedSite() {
		return intendedSite;
	}

	public void setIntendedSite(Boolean intendedSite) {
		this.intendedSite = intendedSite;
	}

	public Boolean getSupportedVersion() {
		return supportedVersion;
	}

	public void setSupportedVersion(Boolean supportedVersion) {
		this.supportedVersion = supportedVersion;
	}

	public String getSiteAbbrev() {
		return siteAbbrev;
	}

	public void setSiteAbbrev(String siteAbbrev) {
		this.siteAbbrev = siteAbbrev;
	}

}
