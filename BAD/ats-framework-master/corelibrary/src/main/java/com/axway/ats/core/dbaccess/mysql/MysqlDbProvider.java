/*
 * Copyright 2017 Axway Software
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.axway.ats.core.dbaccess.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.axway.ats.core.dbaccess.AbstractDbProvider;
import com.axway.ats.core.dbaccess.ConnectionPool;
import com.axway.ats.core.dbaccess.DbProvider;
import com.axway.ats.core.dbaccess.DbRecordValue;
import com.axway.ats.core.dbaccess.DbRecordValuesList;
import com.axway.ats.core.dbaccess.exceptions.DbException;

/**
 * MySQL implementation of the DB provider
 */
public class MysqlDbProvider extends AbstractDbProvider {

    private static final Logger log                    = Logger.getLogger(MysqlDbProvider.class);

    public final static String  FUNC_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP()";
    public final static String  FUNC_NOW               = "NOW()";
    public final static String  FUNC_LOCALTIME         = "LOCALTIME()";
    public final static String  FUNC_LOCALTIMESTAMP    = "LOCALTIMESTAMP()";
    public final static String  FUNC_UNIX_TIMESTAMP    = "UNIX_TIMESTAMP()";

    /**
     * Constructor to create authenticated connection to a database.
     * Takes DbConnection object
     * 
     * @param dbconn db-connection object
     */
    public MysqlDbProvider( DbConnMySQL dbConnection ) {

        super(dbConnection);

        this.reservedWords = new String[]{ FUNC_CURRENT_TIMESTAMP,
                                           FUNC_NOW,
                                           FUNC_LOCALTIME,
                                           FUNC_LOCALTIMESTAMP,
                                           FUNC_UNIX_TIMESTAMP };
    }

    /**
     * Returns the {@link Connection} associated with this {@link DbProvider}
     * 
     * @return the {@link Connection} associated with this {@link DbProvider}
     * @throws DbException
     */
    public Connection getConnection() throws DbException {

        return ConnectionPool.getConnection(dbConnection);
    }

    @Override
    protected String getResultAsEscapedString(
                                               ResultSet resultSet,
                                               int index,
                                               String columnTypeName ) throws SQLException, IOException {

        String value;
        Object valueAsObject = resultSet.getObject(index);
        if (valueAsObject == null) {
            return null;
        }
        if (valueAsObject != null && valueAsObject.getClass().isArray()) {
            // we have an array of primitive data type
            // LONGBLOB types are returned as byte array and '1?' should be transformed to 0x313F
            if (! (valueAsObject instanceof byte[])) {
                // FIXME other array types might be needed to be tracked in a different way 
                log.warn("Array type that needs attention");
            }

            StringBuilder hexString = new StringBuilder();
            hexString.append("0x");
            // read the binary data from the stream and convert it to hex
            InputStream blobInputStream = resultSet.getBinaryStream(index);
            hexString = addBinDataAsHexAndCloseStream(hexString, blobInputStream);
            value = hexString.toString();

        } else if (valueAsObject instanceof Blob) {
            log.info("Blob detected. Will try to dump as hex");
            // we have a blob
            Blob blobValue = (Blob) valueAsObject;
            InputStream blobInputStream = blobValue.getBinaryStream();
            StringBuilder hexString = new StringBuilder();
            hexString.append("0x");
            hexString = addBinDataAsHexAndCloseStream(hexString, blobInputStream);
            value = hexString.toString();
        } else {
            // treat as a string
            value = resultSet.getString(index);
            logDebugInfoForDBValue(value, index, resultSet);
        }

        return value;
    }

    @Override
    protected Map<String, String> extractTableIndexes( String tableName, DatabaseMetaData databaseMetaData,
                                                       String catalog ) throws DbException {

        String sql = "SELECT TABLE_NAME,INDEX_NAME,NON_UNIQUE,SEQ_IN_INDEX,COLUMN_NAME,COLLATION,SUB_PART,PACKED,NULLABLE,INDEX_TYPE "
                     + "FROM INFORMATION_SCHEMA.STATISTICS " + "WHERE TABLE_NAME='" + tableName + "'";

        String indexName = null;
        Map<String, String> indexes = new HashMap<>();
        for (DbRecordValuesList valueList : select(sql)) {
            StringBuilder info = new StringBuilder();
            boolean firstTime = true;
            for (DbRecordValue dbValue : valueList) {
                String value = dbValue.getValueAsString();
                String name = dbValue.getDbColumn().getColumnName();
                if ("INDEX_NAME".equalsIgnoreCase(name)) {
                    indexName = value;
                } else {
                    if (firstTime) {
                        firstTime = false;
                        info.append(name + "=" + value);
                    } else {
                        info.append(", " + name + "=" + value);
                    }
                }
            }

            if (indexName == null) {
                indexName = "NULL_NAME_FOUND_FOR_INDEX_OF_TABLE_" + tableName;
                log.warn("IndexName column not found in query polling for index properties:\nQuery: " + sql
                         + "\nQuery result: " + valueList.toString()
                         + "\nWe will use the following as an index name: " + indexName);
            }

            indexes.put(indexName, info.toString());
        }

        return indexes;
    }
}
