package com.github.lianick.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.lianick.converter.RoleNumberToRoleConveter;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
import com.github.lianick.model.eneity.Users;

@Configuration
public class ModelMapperConfig {

	@Autowired
    private  RoleNumberToRoleConveter roleTypeToRoleConveter;

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		
		// Entity -> DTO
		modelMapper.typeMap(Users.class, UserRegisterDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserRegisterDTO::setId);
		});
		modelMapper.typeMap(Users.class, UserVerifyDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserVerifyDTO::setId);
		});
		modelMapper.typeMap(Users.class, UserLoginDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserLoginDTO::setId);
			mapper.map(Users::getAccount, UserLoginDTO::setUsername);
		});
		modelMapper.typeMap(Users.class, UserForgetPasswordDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserForgetPasswordDTO::setId);
			mapper.map(Users::getAccount, UserForgetPasswordDTO::setUsername);
		});
		modelMapper.typeMap(Users.class, UserUpdateDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserUpdateDTO::setId);
			mapper.map(Users::getAccount, UserUpdateDTO::setUsername);
		});
		
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
