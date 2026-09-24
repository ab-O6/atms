package com.atms.support;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared PostgreSQL + pgvector container for integration tests (see test-strategy.md).
 */
public final class PostgresTestcontainerExtension implements BeforeAllCallback {

    private static final DockerImageName PGVECTOR_IMAGE =
            DockerImageName.parse("pgvector/pgvector:pg16").asCompatibleSubstituteFor("postgres");

    @SuppressWarnings("resource")
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(PGVECTOR_IMAGE)
            .withDatabaseName("atms")
            .withUsername("atms")
            .withPassword("atms");

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }
    }

    public static String jdbcUrl() {
        return POSTGRES.getJdbcUrl();
    }
}
