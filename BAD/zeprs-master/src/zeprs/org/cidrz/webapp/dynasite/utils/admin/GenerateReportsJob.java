/*
 *    Copyright 2003, 2004, 2005, 2006 Research Triangle Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package org.cidrz.webapp.dynasite.utils.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cidrz.project.zeprs.report.DbOutput;
import org.cidrz.webapp.dynasite.utils.DatabaseUtils;
import org.cidrz.webapp.dynasite.utils.XmlUtils;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.servlet.ServletException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a8cbc3cdc4c4cdd1e8dadcc186c7dacf">[email protected]</a>">Chris Kelley</a>
 *         Date: Nov 28, 2005
 *         Time: 12:23:41 PM
 */
public class GenerateReportsJob implements Job {

    static Log logger = LogFactory.getLog(GenerateReportsJob.class);

    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        Connection conn = null;
        try {
            conn = DatabaseUtils.getZEPRSConnection(null);

            // Every job has it's own job detail
            JobDetail jobDetail = context.getJobDetail();

            // The name is defined in the job definition
            String jobName = jobDetail.getName();

            // Log the time the job started
            logger.info(jobName + " fired at " + new Date());
            logger.debug("generateReportXML");
            //XmlUtils.generateReportXML(conn);
            logger.debug("generatePatientList");
            //DbOutput.generatePatientList(conn);
            logger.debug("generateEncounterDataList");
            //DbOutput.generateEncounterDataList(conn);

            // Log the time the job ended
            logger.info(jobName + " completed at " + new Date());
        } catch (ServletException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
