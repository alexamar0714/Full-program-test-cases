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

import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.BasicRowProcessor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ckelley
 * Date: Jun 5, 2005
 * Time: 10:47:10 PM
 */
public class ZEPRSRowProcessor  implements RowProcessor {

        /**
     * The default BeanProcessor instance to use if not supplied in the
     * constructor.
     */
    private static final BeanProcessor defaultConvert = new BeanProcessor();

    /**
     * The Singleton instance of this class.
     */
    private static final ZEPRSRowProcessor instance = new ZEPRSRowProcessor();

    /**
     * Returns the Singleton instance of this class.
     *
     * @return The single instance of this class.
     * @deprecated Create instances with the constructors instead.  This will
     * be removed after DbUtils 1.1.
     */
    public static ZEPRSRowProcessor instance() {
        return instance;
    }

    /**
     * Use this to process beans.
     */
    private BeanProcessor convert = null;

    /**
     * ZEPRSRowProcessor constructor.  Bean processing defaults to a
     * BeanProcessor instance.
     */
    public ZEPRSRowProcessor() {
        this(defaultConvert);
    }

    /**
     * ZEPRSRowProcessor constructor.
     * @param convert The BeanProcessor to use when converting columns to
     * bean properties.
     * @since DbUtils 1.1
     */
    public ZEPRSRowProcessor(BeanProcessor convert) {
        super();
        this.convert = convert;
    }

    /**
     * Convert a <code>ResultSet</code> row into an <code>Object[]</code>.
     * This implementation copies column values into the array in the same
     * order they're returned from the <code>ResultSet</code>.  Array elements
     * will be set to <code>null</code> if the column was SQL NULL.
     *
     * @see org.apache.commons.dbutils.RowProcessor#toArray(java.sql.ResultSet)
     */
    public Object[] toArray(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        Object[] result = new Object[cols];

        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }

        return result;
    }

    /**
     * Convert a <code>ResultSet</code> row into a JavaBean.  This
     * implementation delegates to a BeanProcessor instance.
     * @see org.apache.commons.dbutils.BeanProcessor#toBean(java.sql.ResultSet, java.lang.Class)
     * @see org.apache.commons.dbutils.RowProcessor#toBean(java.sql.ResultSet, java.lang.Class)
     */
    public Object toBean(ResultSet rs, Class type) throws SQLException {
        return this.convert.toBean(rs, type);
    }

    /**
     * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans.
     * This implementation delegates to a BeanProcessor instance.
     * @see org.apache.commons.dbutils.BeanProcessor#toBeanList(java.sql.ResultSet, java.lang.Class)
     * @see org.apache.commons.dbutils.RowProcessor#toBeanList(java.sql.ResultSet, java.lang.Class)
     */
    public List toBeanList(ResultSet rs, Class type) throws SQLException {
        return this.convert.toBeanList(rs, type);
    }

    /**
     * Convert a <code>ResultSet</code> row into a <code>Map</code>.  This
     * implementation returns a <code>Map</code> with case insensitive column
     * names as keys.  Calls to <code>map.get("COL")</code> and
     * <code>map.get("col")</code> return the same value.
     * @see org.apache.commons.dbutils.RowProcessor#toMap(java.sql.ResultSet)
     */
    public Map toMap(ResultSet rs) throws SQLException {
        Map result = new CaseInsensitiveHashMap();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        for (int i = 1; i <= cols; i++) {
            result.put(rsmd.getColumnName(i), rs.getObject(i));
        }

        return result;
    }

    /**
     * A Map that converts all keys to lowercase Strings for case insensitive
     * lookups.  This is needed for the toMap() implementation because
     * databases don't consistenly handle the casing of column names.
     */
    private static class CaseInsensitiveHashMap extends HashMap {

        /**
         * @see java.util.Map#containsKey(java.lang.Object)
         */
        public boolean containsKey(Object key) {
            return super.containsKey(key.toString().toLowerCase());
        }

        /**
         * @see java.util.Map#get(java.lang.Object)
         */
        public Object get(Object key) {
            return super.get(key.toString().toLowerCase());
        }

        /**
         * @see java.util.Map#put(java.lang.Object, java.lang.Object)
         */
        public Object put(Object key, Object value) {
            return super.put(key.toString().toLowerCase(), value);
        }

        /**
         * @see java.util.Map#putAll(java.util.Map)
         */
        public void putAll(Map m) {
            Iterator iter = m.keySet().iterator();
            while (iter.hasNext()) {
                Object key = iter.next();
                Object value = m.get(key);
                this.put(key, value);
            }
        }

        /**
         * @see java.util.Map#remove(java.lang.Object)
         */
        public Object remove(Object key) {
            return super.remove(key.toString().toLowerCase());
        }
    }

}
