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
package net.iaeste.iws.client.spring;

import net.iaeste.iws.common.configuration.InternalConstants;
import net.iaeste.iws.common.configuration.Settings;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author  Kim Jensen / last $Author:$
 * @version $Revision:$ / $Date:$
 * @since   IWS 1.0
 */
@Configuration
@ComponentScan("net.iaeste.iws.client.spring")
@EnableTransactionManagement
public class Beans {

    // Internal Lock, to synchronize access
    private static final Object LOCK = new Object();

    // Following is used to configure the Settings
    private static final Integer MAX_ACTIVE_TOKENS = InternalConstants.MAX_ACTIVE_TOKENS;
    private static final Long MAX_IDLE_TIME_FOR_SESSIONS = InternalConstants.MAX_SESSION_IDLE_PERIOD;
    private static final Integer MAX_LOGIN_RETRIES = InternalConstants.MAX_LOGIN_RETRIES;
    private static final long LOGIN_BLOCKED_TIME = InternalConstants.LOGIN_BLOCKING_PERIOD;

    private static final boolean USE_INMEMORY_DATABASE = true;

    // Internal Settings, which we can reuse to control the behaviour of IWS
    // from the tests.
    private static Settings settings = null;

    @Bean
    protected DataSource dataSource() {
        return USE_INMEMORY_DATABASE ? hsqldbDataSource() : postgresDataSource();
    }

    private static DataSource postgresDataSource() {
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("iws");
        dataSource.setUser("iws_user");
        dataSource.setPassword("iws");

        return dataSource;
    }

    private static DataSource hsqldbDataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL)
                .addScript("net/iaeste/iws/persistence/hsqldb/10-base-tables.sql")
                .addScript("net/iaeste/iws/persistence/15-base-views.sql")
                .addScript("net/iaeste/iws/persistence/19-base-data.sql")
                .addScript("net/iaeste/iws/persistence/hsqldb/30-exchange-tables.sql")
                .addScript("net/iaeste/iws/persistence/35-exchange-views.sql")
                .addScript("net/iaeste/iws/persistence/90-initial-test-data.sql")
                .build();
    }

    @Bean(name = "entityManagerFactoryBean")
    protected LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        factoryBean.setPackagesToScan("net.iaeste.iws.persistence");
        factoryBean.setDataSource(dataSource());
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaProperties(jpaProperties());

        return factoryBean;
    }

    @Bean
    protected Properties jpaProperties() {
        final Properties properties = new Properties();

        // For testing the result, it is helpful to be able to see the queries
        // executed against the database, preferably formatted as well :-)
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "true");

        return properties;
    }

    /**
     * Prepares a Transaction Manager.
     *
     * @return New Transaction Manager instance
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());

        return transactionManager;
    }

    /**
     * Prepares a set of IWS Settings, with default settings for testing.
     *
     * @return IWS Settings for testing
     */
    public static Settings settings() {
        synchronized (LOCK) {
            if (settings == null) {
                settings = new Settings();

                settings.setMaxActiveTokens(MAX_ACTIVE_TOKENS);
                settings.setMaxIdleTimeForSessions(MAX_IDLE_TIME_FOR_SESSIONS);
                settings.setMaxLoginRetries(MAX_LOGIN_RETRIES);
                settings.setLoginBlockedTime(LOGIN_BLOCKED_TIME);
            }

            return settings;
        }
    }
}
