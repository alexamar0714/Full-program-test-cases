/*
 *    Copyright 2003, 2004, 2005, 2006 Research Triangle Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 */

package org.cidrz.webapp.dynasite.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.cidrz.project.zeprs.valueobject.EncounterData;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Arrays;
import java.util.Map;

/**
 * @author <a href="mailto:<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="54373f313838312d1426203d7a3b2633">[email protected]</a>">Chris Kelley</a>
 *         Date: Jul 19, 2005
 *         Time: 5:50:10 PM
 */
public class ZEPRSQueryRunner {

    /**
     * The DataSource to retrieve connections from.
     */
    protected DataSource ds = null;

    /**
     * Constructor for QueryRunner.
     */
    public ZEPRSQueryRunner() {
        super();
    }

    /**
     * Constructor for QueryRunner.  Methods that do not take a
     * <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds The <code>DataSource</code> to retrieve connections from.
     */
    public ZEPRSQueryRunner(DataSource ds) {
        super();
        setDataSource(ds);
    }

    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     *
     * @param conn   The Connection to use to run the query.  The caller is
     *               responsible for closing this Connection.
     * @param sql    The SQL to execute.
     * @param params An array of query replacement parameters.  Each row in
     *               this array is one set of batch replacement values.
     * @return The number of rows updated per statement.
     * @throws java.sql.SQLException
     * @since DbUtils 1.1
     */
    public int[] batch(Connection conn, String sql, Object[][] params)
            throws SQLException {

        PreparedStatement stmt = null;
        int[] rows = null;
        try {
            stmt = this.prepareStatement(conn, sql);

            for (int i = 0; i < params.length; i++) {
                this.fillStatement(stmt, params[i]);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();

        } catch (SQLException e) {
            this.rethrow(e, sql, params);
        } finally {
            close(stmt);
        }

        return rows;
    }

    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.  The
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the update will not be saved.
     *
     * @param sql    The SQL to execute.
     * @param params An array of query replacement parameters.  Each row in
     *               this array is one set of batch replacement values.
     * @return The number of rows updated per statement.
     * @throws SQLException
     * @since DbUtils 1.1
     */
    public int[] batch(String sql, Object[][] params) throws SQLException {
        Connection conn = this.prepareConnection();

        try {
            return this.batch(conn, sql, params);
        } finally {
            close(conn);
        }
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with
     * the given objects.
     *
     * @param stmt
     * @param params Query replacement parameters; <code>null</code> is a valid
     *               value to pass in.
     * @throws SQLException
     */
    protected void fillStatement(PreparedStatement stmt, Object[] params)
            throws SQLException {

        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                if (params[i].equals("true")) {
                    stmt.setBoolean(i + 1, true);
                } else if (params[i].equals("false")) {
                    stmt.setBoolean(i + 1, true);
                } else
                    stmt.setObject(i + 1, params[i]);
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type.  Oddly, NULL and
                // OTHER don't work with Oracle's drivers.
                stmt.setNull(i + 1, Types.VARCHAR);
            }
        }
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with
     * the given objects.
     *
     * @param stmt
     * @param params Query replacement parameters; <code>null</code> is a valid
     *               value to pass in.
     * @throws SQLException
     */
    protected void fillStatement(PreparedStatement stmt, Object[] params, EncounterData object)
            throws SQLException {

        if (params == null) {
            return;
        }

        Map props = null;
        try {
            props = BeanUtils.describe(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        /*PropertyDescriptor prop = props[columnToProperty[i]];
            Class propType = prop.getPropertyType();*/

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                if (params[i].equals("true")) {
                    stmt.setBoolean(i + 1, true);
                } else if (params[i].equals("false")) {
                    stmt.setBoolean(i + 1, true);
                } else
                    stmt.setObject(i + 1, params[i]);
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type.  Oddly, NULL and
                // OTHER don't work with Oracle's drivers.
                stmt.setNull(i + 1, Types.VARCHAR);
            }
        }
    }

    protected Object processColumn(ResultSet rs, int index, Class propType)
            throws SQLException {

        if (propType.equals(String.class)) {
            return rs.getString(index);

        } else if (
                propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
            Object value = null;
            value = rs.getObject(index);
            if (value != null) {
                value = new Integer(rs.getInt(index));
            }
            return value;

        } else if (
                propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
            return new Boolean(rs.getBoolean(index));

        } else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
            return new Long(rs.getLong(index));

        } else if (
                propType.equals(Double.TYPE) || propType.equals(Double.class)) {
            Object value = null;
            value = rs.getObject(index);
            if (value != null) {
                value = new Double(rs.getDouble(index));
            }
            return value;

        } else if (
                propType.equals(Float.TYPE) || propType.equals(Float.class)) {
            Object value = null;
            value = rs.getObject(index);
            if (value != null) {
                value = new Float(rs.getFloat(index));
            }
            return value;

        } else if (
                propType.equals(Short.TYPE) || propType.equals(Short.class)) {
            return new Short(rs.getShort(index));

        } else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
            Object value = null;
            value = rs.getObject(index);
            if (value != null) {
                value = new Byte(rs.getByte(index));
            }
            return value;

        } else if (propType.equals(Timestamp.class)) {
            return rs.getTimestamp(index);

        } else {
            return rs.getObject(index);
        }

    }

    /**
     * Returns the <code>DataSource</code> this runner is using.
     * <code>QueryRunner</code> methods always call this method to get the
     * <code>DataSource</code> so subclasses can provide specialized
     * behavior.
     */
    public DataSource getDataSource() {
        return this.ds;
    }

    /**
     * Factory method that creates and initializes a
     * <code>PreparedStatement</code> object for the given SQL.
     * <code>QueryRunner</code> methods always call this method to prepare
     * statements for them.  Subclasses can override this method to provide
     * special PreparedStatement configuration if needed.  This implementation
     * simply calls <code>conn.prepareStatement(sql)</code>.
     *
     * @param conn The <code>Connection</code> used to create the
     *             <code>PreparedStatement</code>
     * @param sql  The SQL statement to prepare.
     * @return An initialized <code>PreparedStatement</code>.
     * @throws SQLException
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql)
            throws SQLException {

        return conn.prepareStatement(sql);
    }

    /**
     * Factory method that creates and initializes a
     * <code>Connection</code> object.  <code>QueryRunner</code> methods
     * always call this method to retrieve connections from its DataSource.
     * Subclasses can override this method to provide
     * special <code>Connection</code> configuration if needed.  This
     * implementation simply calls <code>ds.getConnection()</code>.
     *
     * @return An initialized <code>Connection</code>.
     * @throws SQLException
     * @since DbUtils 1.1
     */
    protected Connection prepareConnection() throws SQLException {
        return this.getDataSource().getConnection();
    }

    /**
     * Execute an SQL SELECT query with a single replacement parameter. The
     * caller is responsible for closing the connection.
     *
     * @param conn  The connection to execute the query in.
     * @param sql   The query to execute.
     * @param param The replacement parameter.
     * @param rsh   The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException
     */
    public Object query(Connection conn, String sql, Object param,
                        ResultSetHandler rsh) throws SQLException {

        return this.query(conn, sql, new Object[]{param}, rsh);
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     *
     * @param conn   The connection to execute the query in.
     * @param sql    The query to execute.
     * @param params The replacement parameters.
     * @param rsh    The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException
     */
    public Object query(Connection conn, String sql, Object[] params,
                        ResultSetHandler rsh) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object result = null;

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            rs = this.wrap(stmt.executeQuery());
            result = rsh.handle(rs);

        } catch (SQLException e) {
            this.rethrow(e, sql, params);

        } finally {
            try {
                close(rs);
            } finally {
                close(stmt);
            }
        }

        return result;
    }

    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for closing the connection.
     *
     * @param conn The connection to execute the query in.
     * @param sql  The query to execute.
     * @param rsh  The handler that converts the results into an object.
     * @return The object returned by the handler.
     * @throws SQLException
     */
    public Object query(Connection conn, String sql, ResultSetHandler rsh)
            throws SQLException {

        return this.query(conn, sql, (Object[]) null, rsh);
    }

    /**
     * Executes the given SELECT SQL with a single replacement parameter.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     *
     * @param sql   The SQL statement to execute.
     * @param param The replacement parameter.
     * @param rsh   The handler used to create the result object from
     *              the <code>ResultSet</code>.
     * @return An object generated by the handler.
     * @throws SQLException
     */
    public Object query(String sql, Object param, ResultSetHandler rsh)
            throws SQLException {

        return this.query(sql, new Object[]{param}, rsh);
    }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     *
     * @param sql    The SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters with
     *               this array.
     * @param rsh    The handler used to create the result object from
     *               the <code>ResultSet</code>.
     * @return An object generated by the handler.
     * @throws SQLException
     */
    public Object query(String sql, Object[] params, ResultSetHandler rsh)
            throws SQLException {

        Connection conn = this.prepareConnection();

        try {
            return this.query(conn, sql, params, rsh);
        } finally {
            close(conn);
        }
    }

    /**
     * Executes the given SELECT SQL without any replacement parameters.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     *
     * @param sql The SQL statement to execute.
     * @param rsh The handler used to create the result object from
     *            the <code>ResultSet</code>.
     * @return An object generated by the handler.
     * @throws SQLException
     */
    public Object query(String sql, ResultSetHandler rsh) throws SQLException {
        return this.query(sql, (Object[]) null, rsh);
    }

    /**
     * Throws a new exception with a more informative error message.
     *
     * @param cause  The original exception that will be chained to the new
     *               exception when it's rethrown.
     * @param sql    The query that was executing when the exception happened.
     * @param params The query replacement parameters; <code>null</code> is a
     *               valid value to pass in.
     * @throws SQLException
     */
    protected void rethrow(SQLException cause, String sql, Object[] params)
            throws SQLException {

        StringBuffer msg = new StringBuffer(cause.getMessage());

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.asList(params));
        }

        SQLException e = new SQLException(msg.toString(), cause.getSQLState(),
                cause.getErrorCode());
        e.setNextException(cause);

        throw e;
    }

    /**
     * Sets the <code>DataSource</code> this runner will use to get
     * database connections from.  This should be called after creating a
     * runner with the default constructor if you intend to use the
     * execute methods without passing in a <code>Connection</code>.
     *
     * @param dataSource The DataSource to use.
     */
    public void setDataSource(DataSource dataSource) {
        this.ds = dataSource;
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
     * parameters.
     *
     * @param conn The connection to use to run the query.
     * @param sql  The SQL to execute.
     * @return The number of rows updated.
     * @throws SQLException
     */
    public int update(Connection conn, String sql) throws SQLException {
        return this.update(conn, sql, (Object[]) null);
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query with a single replacement
     * parameter.
     *
     * @param conn  The connection to use to run the query.
     * @param sql   The SQL to execute.
     * @param param The replacement parameter.
     * @return The number of rows updated.
     * @throws SQLException
     */
    public int update(Connection conn, String sql, Object param)
            throws SQLException {

        return this.update(conn, sql, new Object[]{param});
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query.
     *
     * @param conn   The connection to use to run the query.
     * @param sql    The SQL to execute.
     * @param params The query replacement parameters.
     * @return The number of rows updated.
     * @throws SQLException
     */
    public int update(Connection conn, String sql, Object[] params)
            throws SQLException {

        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            rows = stmt.executeUpdate();

        } catch (SQLException e) {
            this.rethrow(e, sql, params);

        } finally {
            close(stmt);
        }

        return rows;
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query.
     * resolves datatypes
     *
     * @param conn   The connection to use to run the query.
     * @param sql    The SQL to execute.
     * @param params The query replacement parameters.
     * @return The number of rows updated.
     * @throws SQLException
     */
    public int update(Connection conn, String sql, Object[] params, EncounterData object)
            throws SQLException {

        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params, object);
            rows = stmt.executeUpdate();

        } catch (SQLException e) {
            this.rethrow(e, sql, params);

        } finally {
            close(stmt);
        }

        return rows;
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement without
     * any replacement parameters. The <code>Connection</code> is retrieved
     * from the <code>DataSource</code> set in the constructor.  This
     * <code>Connection</code> must be in auto-commit mode or the update will
     * not be saved.
     *
     * @param sql The SQL statement to execute.
     * @return The number of rows updated.
     * @throws SQLException
     */
    public int update(String sql) throws SQLException {
        return this.update(sql, (Object[]) null);
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with
     * a single replacement parameter.  The <code>Connection</code> is
     * retrieved from the <code>DataSource</code> set in the constructor.
     * This <code>Connection</code> must be in auto-commit mode or the
     * update will not be saved.
     *
     * @param sql   The SQL statement to execute.
     * @param param The replacement parameter.
     * @return The number of rows updated.
     * @throws SQLException
     */
    public int update(String sql, Object param) throws SQLException {
        return this.update(sql, new Object[]{param});
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement.  The
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the update will not be saved.
     *
     * @param sql    The SQL statement to execute.
     * @param params Initializes the PreparedStatement's IN (i.e. '?')
     *               parameters.
     * @return The number of rows updated.
     * @throws SQLException
     */
    public int update(String sql, Object[] params) throws SQLException {
        Connection conn = this.prepareConnection();

        try {
            return this.update(conn, sql, params);
        } finally {
            close(conn);
        }
    }

    /**
     * Wrap the <code>ResultSet</code> in a decorator before processing it.
     * This implementation returns the <code>ResultSet</code> it is given
     * without any decoration.
     * <p/>
     * <p/>
     * Often, the implementation of this method can be done in an anonymous
     * inner class like this:
     * </p>
     * <pre>
     * QueryRunner run = new QueryRunner() {
     *     protected ResultSet wrap(ResultSet rs) {
     *         return StringTrimmedResultSet.wrap(rs);
     *     }
     * };
     * </pre>
     *
     * @param rs The <code>ResultSet</code> to decorate; never
     *           <code>null</code>.
     * @return The <code>ResultSet</code> wrapped in some decorator.
     */
    protected ResultSet wrap(ResultSet rs) {
        return rs;
    }

    /**
     * Close a <code>Connection</code>.  This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     *
     * @throws SQLException
     * @since DbUtils 1.1
     */
    protected void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    /**
     * Close a <code>Statement</code>.  This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     *
     * @throws SQLException
     * @since DbUtils 1.1
     */
    protected void close(Statement stmt) throws SQLException {
        DbUtils.close(stmt);
    }

    /**
     * Close a <code>ResultSet</code>.  This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     *
     * @throws SQLException
     * @since DbUtils 1.1
     */
    protected void close(ResultSet rs) throws SQLException {
        DbUtils.close(rs);
    }
}
