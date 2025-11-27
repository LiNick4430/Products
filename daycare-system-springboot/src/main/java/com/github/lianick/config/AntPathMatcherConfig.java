package com.github.lianick.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
public class AntPathMatcherConfig {

	@Bean
	AntPathMatcher antPathMatcher() {
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		
		return antPathMatcher;
	}
}
