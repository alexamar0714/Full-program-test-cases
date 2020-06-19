/*
 *    Copyright 2003, 2004, 2005, 2006 Research Triangle Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package org.cidrz.webapp.dynasite.security;


public abstract class AuthorizationManager {
    //todo: make this a singleton???
    public static final String SESSION_KEY_AUTH_PERMS = "auth_perms";
    public static final String SESSION_KEY_AUTH_GROUPS = "auth_groups";
    public static final String AUTH_GROUP_SEPARATOR = ":";

    public static final String ROLE_ALTER_OPERATING_SYSTEM_NETWORK_SECURITY_PROGRAMS = "ALTER_OPERATING_SYSTEM_NETWORK_SECURITY_PROGRAMS";
    public static final String ROLE_CREATE_MEDICAL_STAFF_IDS_AND_PASSWORDS_FOR_MEDICAL_STAFF = "CREATE_MEDICAL_STAFF_IDS_AND_PASSWORDS_FOR_MEDICAL_STAFF";
    public static final String ROLE_CREATE_VIEW_MODIFY_INDIVIDUAL_PATIENT_RECORDS = "CREATE_VIEW_MODIFY_INDIVIDUAL_PATIENT_RECORDS";
    public static final String ROLE_VIEW_INDIVIDUAL_PATIENT_RECORDS = "VIEW_INDIVIDUAL_PATIENT_RECORDS";
    public static final String ROLE_VIEW_SELECTED_REPORTS_AND_VIEW_STATISTICAL_SUMMARIES = "VIEW_SELECTED_REPORTS_AND_VIEW_STATISTICAL_SUMMARIES";
    public static final String ROLE_IDENTIFY_INDIVIDUAL_PATIENTS_OF_REPORTABLE_DISEASES = "IDENTIFY_INDIVIDUAL_PATIENTS_OF_REPORTABLE_DISEASES";
    public static final String ROLE_ENTER_ALL_LAB_RESULTS_ONLY_NO_VIEWING_OF_INDIVIDUAL_PATIENT_DATA = "ENTER_ALL_LAB_RESULTS_ONLY_NO_VIEWING_OF_INDIVIDUAL_PATIENT_DATA";
    public static final String ROLE_ENTER_SUBSET_OF_LAB_RESULTS_ONLY_NO_VIEWING_OF_INDIVIDUAL_PATIENT_DATA = "ENTER_SUBSET_OF_LAB_RESULTS_ONLY_NO_VIEWING_OF_INDIVIDUAL_PATIENT_DATA";
    public static final String ROLE_CHANGE_DATABASE_TABLES_AND_CONTENTS_OF_ENUMERATED_LISTS = "CHANGE_DATABASE_TABLES_AND_CONTENTS_OF_ENUMERATED_LISTS";
    public static final String ROLE_ALTER_PROGRAMS_AND_SCREEN_APPEARANCE = "ALTER_PROGRAMS_AND_SCREEN_APPEARANCE";
    public static final String ROLE_CREATE_NEW_PATIENTS_AND_SEARCH = "CREATE_NEW_PATIENTS_AND_SEARCH";
    public static final String ROLE_DELETE_ARCHIVE_PATIENT_RECORDS = "DELETE_ARCHIVE_PATIENT_RECORDS";
}