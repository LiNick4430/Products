package com.github.lianick.config;

import java.util.Set;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.responses.ApiResponses;

@OpenAPIDefinition(
	info = @Info(
		title = "Daycare System API",
		version = "v1",
		description = "公托系統後端API文件"
	)
)
@SecurityScheme(
	name = "bearerAuth",
	type = SecuritySchemeType.HTTP,
	scheme = "bearer",
	bearerFormat = "JWT",
	in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
	
	private static final Set<String> ALLOWED_RESPONSES = Set.of("200", "401");

    @Bean
    OperationCustomizer removeDefaultResponsesCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            responses.keySet().removeIf(
                status -> !ALLOWED_RESPONSES.contains(status)
            );
            return operation;
        };
    }
}
