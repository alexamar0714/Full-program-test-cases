package eu.drus.jpa.unit.mongodb.ext;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.net.HostAndPort;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

import eu.drus.jpa.unit.spi.PersistenceUnitDescriptor;

public class HibernateOgmConfiguration extends AbstractConfiguration {

    private static final String HIBERNATE_OGM_MONGODB_READ_PREFERENCE = "hibernate.ogm.mongodb.read_preference";
    private static final String HIBERNATE_OGM_MONGODB_WRITE_CONCERN = "hibernate.ogm.mongodb.write_concern";
    private static final String HIBERNATE_OGM_MONGODB_OPTIONS_PREFIX = "hibernate.ogm.mongodb.driver";
    private static final String HIBERNATE_OGM_MONGODB_AUTHENTICATION_DATABASE = "hibernate.ogm.mongodb.authentication_database";
    private static final String HIBERNATE_OGM_DATASTORE_PASSWORD = "hibernate.ogm.datastore.password";
    private static final String HIBERNATE_OGM_DATASTORE_USERNAME = "hibernate.ogm.datastore.username";
    private static final String HIBERNATE_OGM_DATASTORE_HOST = "hibernate.ogm.datastore.host";
    private static final String HIBERNATE_OGM_DATASTORE_DATABASE = "hibernate.ogm.datastore.database";
    private static final String HIBERNATE_OGM_DATASTORE_PROVIDER = "hibernate.ogm.datastore.provider";

    public static class ConfigurationFactoryImpl implements ConfigurationFactory {

        @Override
        public boolean isSupported(final PersistenceUnitDescriptor descriptor) {
            final Map<String, Object> dbConfig = descriptor.getProperties();

            return dbConfig.containsKey(HIBERNATE_OGM_DATASTORE_PROVIDER)
                    && ((String) dbConfig.get(HIBERNATE_OGM_DATASTORE_PROVIDER)).contains("mongo");
        }

        @Override
        public Configuration createConfiguration(final PersistenceUnitDescriptor descriptor) {
            return new HibernateOgmConfiguration(descriptor);
        }
    }

    private HibernateOgmConfiguration(final PersistenceUnitDescriptor descriptor) {
        final Map<String, Object> properties = descriptor.getProperties();

        configureServerAddresses(properties);
        configureDatabaseName(properties);
        configureCredentials(properties);
        configureClientOptions(properties);
    }

    private void configureClientOptions(final Map<String, Object> properties) {
        final MongoClientOptions.Builder builder = MongoClientOptions.builder();
        setOptions(builder, (final String key) -> (String) properties.get(HIBERNATE_OGM_MONGODB_OPTIONS_PREFIX + "." + key));

        final String writeConcern = (String) properties.get(HIBERNATE_OGM_MONGODB_WRITE_CONCERN);
        final String readPreference = (String) properties.get(HIBERNATE_OGM_MONGODB_READ_PREFERENCE);

        if (writeConcern != null) {
            builder.writeConcern(WriteConcern.valueOf(writeConcern));
        }
        if (readPreference != null) {
            builder.readPreference(ReadPreference.valueOf(readPreference));
        }
        mongoClientOptions = builder.build();
    }

    private void configureCredentials(final Map<String, Object> properties) {
        final String userName = (String) properties.get(HIBERNATE_OGM_DATASTORE_USERNAME);
        final String password = (String) properties.get(HIBERNATE_OGM_DATASTORE_PASSWORD);
        final String authDatabaseName = (String) properties.get(HIBERNATE_OGM_MONGODB_AUTHENTICATION_DATABASE);
        if (userName != null) {
            checkArgument(password != null, HIBERNATE_OGM_DATASTORE_PASSWORD + " was not configured, but required");
            mongoCredentialList = Collections.singletonList(MongoCredential.createPlainCredential(userName,
                    authDatabaseName != null ? authDatabaseName : "admin", password.toCharArray()));
        } else {
            mongoCredentialList = Collections.emptyList();
        }
    }

    private void configureDatabaseName(final Map<String, Object> properties) {
        databaseName = (String) properties.get(HIBERNATE_OGM_DATASTORE_DATABASE);
        checkArgument(databaseName != null, HIBERNATE_OGM_DATASTORE_DATABASE + " was not configured, but required");
    }

    private void configureServerAddresses(final Map<String, Object> properties) {
        final List<HostAndPort> hostsAndPorts = parse((String) properties.get(HIBERNATE_OGM_DATASTORE_HOST));
        serverAddresses = hostsAndPorts.stream()
                .map(h -> new ServerAddress(h.getHost(), h.hasPort() ? h.getPort() : ServerAddress.defaultPort()))
                .collect(Collectors.toList());
        if (serverAddresses.isEmpty()) {
            serverAddresses.add(new ServerAddress());
        }
    }

    private List<HostAndPort> parse(final String hostString) {
        final List<HostAndPort> hostAndPorts = new ArrayList<>();
        if (hostString == null || hostString.trim().isEmpty()) {
            return hostAndPorts;
        }

        for (final String rawSplit : hostString.split(",")) {
            hostAndPorts.add(HostAndPort.fromString(rawSplit.trim()));
        }
        return hostAndPorts;
    }
}
