/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.postgresql.config;

import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.fineract.cn.postgresql.domain.FlywayFactoryBean;
import org.apache.fineract.cn.postgresql.util.JdbcUrlBuilder;
import org.apache.fineract.cn.lang.ApplicationName;
import org.apache.fineract.cn.lang.config.EnableApplicationName;
import org.apache.fineract.cn.postgresql.util.PostgreSQLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
@Configuration
@ConditionalOnProperty(prefix = "postgresql", name = "enabled", matchIfMissing = true)
@EnableTransactionManagement
@EnableApplicationName
public class PostgreSQLJavaConfiguration {

  private final Environment env;

  @Autowired
  public PostgreSQLJavaConfiguration(final Environment env) {
    super();
    this.env = env;
  }

  @Bean(name = PostgreSQLConstants.LOGGER_NAME)
  public Logger logger() {
    return LoggerFactory.getLogger(PostgreSQLConstants.LOGGER_NAME);
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
    final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setPersistenceUnitName("metaPU");
    em.setDataSource(dataSource);
    em.setPackagesToScan("org.apache.fineract.cn.**.repository");

    final JpaVendorAdapter vendorAdapter = new OpenJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(additionalProperties());

    return em;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);
    return transactionManager;
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  @Bean
  public FlywayFactoryBean flywayFactoryBean(final ApplicationName applicationName) {
    return new FlywayFactoryBean(applicationName);
  }

  @Bean
  public MetaDataSourceWrapper metaDataSourceWrapper() {

    final BoneCPDataSource boneCPDataSource = new BoneCPDataSource();
    boneCPDataSource.setDriverClass(
            this.env.getProperty(PostgreSQLConstants.POSTGRESQL_DRIVER_CLASS_PROP, PostgreSQLConstants.POSTGRESQL_DRIVER_CLASS_DEFAULT));
    boneCPDataSource.setJdbcUrl(JdbcUrlBuilder
            .create(JdbcUrlBuilder.DatabaseType.POSTGRESQL)
            .host(this.env.getProperty(PostgreSQLConstants.POSTGRESQL_HOST_PROP, PostgreSQLConstants.POSTGRESQL_HOST_DEFAULT))
            .port(this.env.getProperty(PostgreSQLConstants.POSTGRESQL_PORT_PROP, PostgreSQLConstants.POSTGRESQL_PORT_DEFAULT))
            .instanceName(this.env.getProperty(PostgreSQLConstants.POSTGRESQL_DATABASE_NAME_PROP, PostgreSQLConstants.POSTGRESQL_DATABASE_NAME_DEFAULT))
            .build());
    boneCPDataSource.setUsername(
            this.env.getProperty(PostgreSQLConstants.POSTGRESQL_USER_PROP, PostgreSQLConstants.POSTGRESQL_USER_DEFAULT));
    boneCPDataSource.setIdleConnectionTestPeriodInMinutes(
            Long.valueOf(this.env.getProperty(PostgreSQLConstants.BONECP_IDLE_CONNECTION_TEST_PROP, PostgreSQLConstants.BONECP_IDLE_CONNECTION_TEST_DEFAULT)));
    boneCPDataSource.setIdleMaxAgeInMinutes(
            Long.valueOf(this.env.getProperty(PostgreSQLConstants.BONECP_IDLE_MAX_AGE_PROP, PostgreSQLConstants.BONECP_IDLE_MAX_AGE_DEFAULT)));
    boneCPDataSource.setMaxConnectionsPerPartition(
            Integer.valueOf(this.env.getProperty(PostgreSQLConstants.BONECP_MAX_CONNECTION_PARTITION_PROP, PostgreSQLConstants.BONECP_MAX_CONNECTION_PARTITION_DEFAULT)));
    boneCPDataSource.setMinConnectionsPerPartition(
            Integer.valueOf(this.env.getProperty(PostgreSQLConstants.BONECP_MIN_CONNECTION_PARTITION_PROP, PostgreSQLConstants.BONECP_MIN_CONNECTION_PARTITION_DEFAULT)));
    boneCPDataSource.setPartitionCount(
            Integer.valueOf(this.env.getProperty(PostgreSQLConstants.BONECP_PARTITION_COUNT_PROP, PostgreSQLConstants.BONECP_PARTITION_COUNT_DEFAULT)));
    boneCPDataSource.setAcquireIncrement(
            Integer.valueOf(this.env.getProperty(PostgreSQLConstants.BONECP_ACQUIRE_INCREMENT_PROP, PostgreSQLConstants.BONECP_ACQUIRE_INCREMENT_DEFAULT)));
    boneCPDataSource.setStatementsCacheSize(
            Integer.valueOf(this.env.getProperty(PostgreSQLConstants.BONECP_STATEMENT_CACHE_PROP, PostgreSQLConstants.BONECP_STATEMENT_CACHE_DEFAULT)));

    final Properties driverProperties = new Properties();
    driverProperties.setProperty("useServerPrepStmts", "false");
    boneCPDataSource.setDriverProperties(driverProperties);
    return new MetaDataSourceWrapper(boneCPDataSource);
  }

  private Properties additionalProperties() {
    final Properties properties = new Properties();
    properties.setProperty("openjpa.jdbc.DBDictionary", "org.apache.openjpa.jdbc.sql.PostgresDictionary");
    return properties;
  }
}
