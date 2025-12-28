package com.github.lianick.swagger.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lianick.swagger.customizer.ApiResponseCustomizer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

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
	
	@Bean
    OperationCustomizer apiResponseCustomizer() {
        return new ApiResponseCustomizer();
    }
	
}
