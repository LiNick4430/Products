package com.github.lianick.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lianick.model.dto.UserRegistrationDTO;
import com.github.lianick.model.dto.UserResponseDTO;
import com.github.lianick.model.dto.UserUpdateDTO;

import com.github.lianick.model.eneity.User;

@Configuration
public class ModelMapperConfig {

	@Bean
	ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		
		// Entity -> DTO
		// User -> UserRegistrationDTO
		modelMapper.typeMap(User.class, UserRegistrationDTO.class).addMappings(mapper -> {
			mapper.map(User::getUserId, UserRegistrationDTO::setId);
			mapper.map(User::getUserName, UserRegistrationDTO::setName);
			mapper.map(User::getNationalIdNo, UserRegistrationDTO::setIdNumber);
			mapper.map(User::getBirthdate, UserRegistrationDTO::setBirthday);
		});
		
		// User -> UserResponseDTO
		modelMapper.typeMap(User.class, UserResponseDTO.class).addMappings(mapper -> {
			mapper.map(User::getUserId, UserResponseDTO::setId);
			mapper.map(User::getUserName, UserResponseDTO::setName);
		});
		
		// User -> UserUpdateDTO
		modelMapper.typeMap(User.class, UserUpdateDTO.class).addMappings(mapper -> {
			mapper.map(User::getUserId, UserUpdateDTO::setId);
			mapper.map(User::getUserName, UserUpdateDTO::setName);
			mapper.map(User::getNationalIdNo, UserUpdateDTO::setIdNumber);
			mapper.map(User::getBirthdate, UserUpdateDTO::setBirthday);
		});
		
		// ------------------------------------------------------------------------------------
		// DTO -> Entity 
		// UserRegistrationDTO -> User
		modelMapper.typeMap(UserRegistrationDTO.class, User.class).addMappings(mapper -> {
			mapper.map(UserRegistrationDTO::getId, User::setUserId);
			mapper.map(UserRegistrationDTO::getName, User::setUserName);
			mapper.map(UserRegistrationDTO::getIdNumber, User::setNationalIdNo);
			mapper.map(UserRegistrationDTO::getBirthday, User::setBirthdate);
		});
		
		// UserResponseDTO -> User
		modelMapper.typeMap(UserResponseDTO.class, User.class).addMappings(mapper -> {
			mapper.map(UserResponseDTO::getId, User::setUserId);
			mapper.map(UserResponseDTO::getName, User::setUserName);
		});
		
		// UserUpdateDTO -> User
		modelMapper.typeMap(UserUpdateDTO.class, User.class).addMappings(mapper -> {
			mapper.map(UserUpdateDTO::getId, User::setUserId);
			mapper.map(UserUpdateDTO::getName, User::setUserName);
			mapper.map(UserUpdateDTO::getIdNumber, User::setNationalIdNo);
			mapper.map(UserUpdateDTO::getBirthday, User::setBirthdate);
		});
		
		return modelMapper;
	}
}
