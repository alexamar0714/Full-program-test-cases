/* JDBCJobDAO.java
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Copyright Ⓒ 2014-2015 Universiteit Gent
 * 
 * This file is part of the Degage Web Application
 * 
 * Corresponding author (see also AUTHORS.txt)
 * 
 * Kris Coolsaet
 * Department of Applied Mathematics, Computer Science and Statistics
 * Ghent University 
 * Krijgslaan 281-S9
 * B-9000 GENT Belgium
 * 
 * The Degage Web Application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Degage Web Application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with the Degage Web Application (file LICENSE.txt in the
 * distribution).  If not, see <http://www.gnu.org/licenses/>.
 */

package be.ugent.degage.db.jdbc;

import be.ugent.degage.db.DataAccessException;
import be.ugent.degage.db.dao.JobDAO;
import be.ugent.degage.db.models.Job;
import be.ugent.degage.db.models.JobType;

import java.sql.*;
import java.time.Instant;

/**
 * JDBC implementation of {@link JobDAO}
 */
class JDBCJobDAO extends AbstractDAO implements JobDAO {


    public JDBCJobDAO(JDBCDataAccessContext context) {
        super(context);
    }

    @Override
    public void ping() throws DataAccessException {
        try (Statement stat = createStatement()) {
            stat.execute("DO 1");
        } catch (SQLException ex) {
            throw new DataAccessException("Could not ping the database", ex);
        }
    }

    @Override
    public Iterable<Job> listScheduledForNow() throws DataAccessException {
        try (PreparedStatement ps = prepareStatement(
                "SELECT job_id, job_type, job_ref_id, job_time, job_finished " +
                        "FROM jobs WHERE NOT job_finished AND job_time <= now()"
        )) {
            return toList(ps, rs -> new Job(
                    rs.getLong("job_id"),
                    Enum.valueOf(JobType.class, rs.getString("job_type")),
                    (Integer) rs.getObject("job_ref_id")
            ));
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to fetch unfinished jobs.", ex);
        }
    }

    /**
     * Create a new job for the scheduler to be executed at the requested time.
     */
    @Override
    public void createJob(JobType type, int refId, Instant when) throws DataAccessException {
        try (PreparedStatement ps = prepareStatement(
                "INSERT INTO jobs(job_type, job_ref_id, job_time, job_finished) " +
                        "VALUES(?,?,?,false)"
        )) {
            ps.setString(1, type.name());
            ps.setInt(2, refId);
            ps.setTimestamp(3, Timestamp.from(when));
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create job.", ex);
        }
    }

    @Override
    public void updateJobTime(JobType type, int refId, Instant when) throws DataAccessException {
        try (PreparedStatement ps = prepareStatement(
                "UPDATE jobs SET job_time = ? WHERE job_type = ? AND job_ref_id = ?"
        )) {
            ps.setTimestamp(1, Timestamp.from(when));
            ps.setString(2, type.name());
            ps.setInt(3, refId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    @Override
    public void deleteJob(JobType type, int refId) throws DataAccessException {
        try (PreparedStatement ps = prepareStatement(
                "DELETE FROM jobs WHERE job_type=? AND job_ref_id=?"
        )) {
            ps.setString(1, type.name());
            ps.setInt(2, refId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to delete job.", ex);
        }
    }

    @Override
    public void finishJob(long jobId) throws DataAccessException {
        try (PreparedStatement ps = prepareStatement(
                "UPDATE jobs SET job_finished=true WHERE job_id=?"
        )) {
            ps.setLong(1, jobId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update job status.", ex);
        }
    }
}
