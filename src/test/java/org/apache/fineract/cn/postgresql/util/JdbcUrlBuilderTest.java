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
package org.apache.fineract.cn.postgresql.util;

import org.junit.Assert;
import org.junit.Test;

/*
 * PostgreSQL URL Formats
 * https://jdbc.postgresql.org/documentation/head/connect.html
 */

public class JdbcUrlBuilderTest {

  public JdbcUrlBuilderTest() {
    super();
  }

  @Test
  public void shouldCreatePostgresqlUrl() {
    final String expectedJdbcUrl = "jdbc:postgresql://localhost:5432/comp_test";

    final String postgresqlJdbcUrl = JdbcUrlBuilder
        .create(JdbcUrlBuilder.DatabaseType.POSTGRESQL)
        .host("localhost")
        .port("5432")
        .instanceName("comp_test")
        .build();

    Assert.assertEquals(expectedJdbcUrl, postgresqlJdbcUrl);
  }

  @Test
  public void shouldCreateMysqlUrlNoInstance() {
    final String expectedJdbcUrl = "jdbc:postgresql://localhost:5432";

    final String postgresqlJdbcUrl = JdbcUrlBuilder
            .create(JdbcUrlBuilder.DatabaseType.POSTGRESQL)
            .host("localhost")
            .port("5432").build();

    Assert.assertEquals(expectedJdbcUrl, postgresqlJdbcUrl);
  }

  @Test
  public void shouldCreateMysqlReplicationUrl() {
    final String expectedJdbcUrl = "jdbc:postgresql:replication://localhost:5432,anotherhost:5432/comp_test";

    final String mariaDbJdbcUrl = JdbcUrlBuilder
            .create(JdbcUrlBuilder.DatabaseType.POSTGRESQL)
            .host("localhost, anotherhost")
            .port("5432")
            .instanceName("comp_test")
            .build();

    Assert.assertEquals(expectedJdbcUrl, mariaDbJdbcUrl);
  }
}