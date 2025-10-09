package com.github.lianick.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.lianick.converter.RoleNumberToRoleConveter;
import com.github.lianick.model.dto.UserLoginDTO;
import com.github.lianick.model.dto.UserRegisterDTO;
import com.github.lianick.model.eneity.Users;

@Configuration
public class UserMapperConfig {

	@Autowired
    private  RoleNumberToRoleConveter roleTypeToRoleConveter;


	@Bean
	ModelMapper userModelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		
		// Entity -> DTO
		
		
		// ------------------------------------------------------------------------------------
		// DTO -> Entity
		
		
		modelMapper.typeMap(UserRegisterDTO.class, Users.class).addMappings(mapper -> {
			mapper.map(UserRegisterDTO::getUsername, Users::setAccount);
			// 使用自定義 Converter 進行映射
			mapper.using(roleTypeToRoleConveter)
					.map(UserRegisterDTO::getRoleNumber, Users::setRole);
		});
		
		return modelMapper;
	}
}
