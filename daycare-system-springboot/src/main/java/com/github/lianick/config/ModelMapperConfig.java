package com.github.lianick.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		
		// Entity -> DTO
		
		// ------------------------------------------------------------------------------------
		// DTO -> Entity 
		
		
		return modelMapper;
	}
}
