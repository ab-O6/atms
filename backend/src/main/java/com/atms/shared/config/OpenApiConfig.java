package com.atms.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Loads the canonical OpenAPI document bundled from {@code specs/.../contracts/openapi.yaml}.
 */
@Configuration
public class OpenApiConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenApiConfig.class);
    private static final String OPENAPI_CLASSPATH = "openapi/openapi.yaml";

    @Bean
    public OpenAPI atmsOpenApi() throws IOException {
        ClassPathResource resource = new ClassPathResource(OPENAPI_CLASSPATH);
        if (!resource.exists()) {
            log.warn("Missing {}; springdoc will use runtime-generated spec only", OPENAPI_CLASSPATH);
            return new OpenAPI();
        }
        String yaml = resource.getContentAsString(StandardCharsets.UTF_8);
        SwaggerParseResult parsed = new OpenAPIV3Parser().readContents(yaml, null, null);
        if (parsed.getOpenAPI() == null) {
            log.warn("Failed to parse {}: {}", OPENAPI_CLASSPATH, parsed.getMessages());
            return new OpenAPI();
        }
        return parsed.getOpenAPI();
    }
}
