package com.atms.support;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared PostgreSQL + pgvector for integration tests (see test-strategy.md).
 *
 * <p>Testcontainers talks to Docker over {@code unix://} or {@code tcp://} only — not {@code ssh://}
 * from {@code docker context}. For remote Docker over SSH, tunnel the daemon socket (see
 * {@code scripts/backend-integration-test.sh}) or set {@code ATMS_IT_JDBC_URL} to an existing
 * PostgreSQL instance (e.g. compose on the remote host).
 */
public final class PostgresTestcontainerExtension implements BeforeAllCallback {

    public static final String JDBC_URL_ENV = "ATMS_IT_JDBC_URL";
    public static final String JDBC_USER_ENV = "ATMS_IT_DB_USER";
    public static final String JDBC_PASSWORD_ENV = "ATMS_IT_DB_PASSWORD";

    private static final DockerImageName PGVECTOR_IMAGE =
            DockerImageName.parse("pgvector/pgvector:pg16").asCompatibleSubstituteFor("postgres");

    @SuppressWarnings("resource")
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(PGVECTOR_IMAGE)
            .withDatabaseName("atms")
            .withUsername("atms")
            .withPassword("atms");

    public static boolean usesExternalDatabase() {
        String url = System.getenv(JDBC_URL_ENV);
        return url != null && !url.isBlank();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        if (usesExternalDatabase()) {
            return;
        }
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }
    }

    public static String jdbcUrl() {
        if (usesExternalDatabase()) {
            return System.getenv(JDBC_URL_ENV);
        }
        return POSTGRES.getJdbcUrl();
    }

    public static String jdbcUser() {
        if (usesExternalDatabase()) {
            String user = System.getenv(JDBC_USER_ENV);
            return user != null && !user.isBlank() ? user : "atms";
        }
        return POSTGRES.getUsername();
    }

    public static String jdbcPassword() {
        if (usesExternalDatabase()) {
            String password = System.getenv(JDBC_PASSWORD_ENV);
            return password != null ? password : "atms";
        }
        return POSTGRES.getPassword();
    }
}
