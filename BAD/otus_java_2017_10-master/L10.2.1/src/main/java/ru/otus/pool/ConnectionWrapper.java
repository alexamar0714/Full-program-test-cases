package ru.otus.pool;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 */
public class ConnectionWrapper implements Connection {

    private final Connection connection;

    public ConnectionWrapper(Connection connection) {
        this.connection = connection;
    }


    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void commit() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void rollback() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public String getCatalog() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new NotImplementedException();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new NotImplementedException();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public String getSchema() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new NotImplementedException();
    }
}
