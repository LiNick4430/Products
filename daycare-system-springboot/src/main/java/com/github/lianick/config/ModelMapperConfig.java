package com.github.lianick.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.lianick.converter.RoleNumberToRoleConveter;
import com.github.lianick.converter.UsersToUsernameConveter;
import com.github.lianick.model.dto.AuthResponseDTO;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserMeDTO;
import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
import com.github.lianick.model.dto.userPublic.UserPublicDTO;
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;
import com.github.lianick.model.dto.userPublic.ChildCreateDTO;
import com.github.lianick.model.dto.userPublic.ChildDTO;
import com.github.lianick.model.dto.userPublic.ChildUpdateDTO;
import com.github.lianick.model.dto.userPublic.UserPublicCreateDTO;
import com.github.lianick.model.eneity.ChildInfo;
import com.github.lianick.model.eneity.UserPublic;
import com.github.lianick.model.eneity.Users;

@Configuration
public class ModelMapperConfig {

	@Autowired
    private RoleNumberToRoleConveter roleTypeToRoleConveter;
	
	@Autowired
	private UsersToUsernameConveter usersToUsernameConveter;

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		
		// Entity -> DTO
		// User 相關
		modelMapper.typeMap(Users.class, UserRegisterDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserRegisterDTO::setId);
			mapper.map(Users::getAccount, UserRegisterDTO::setUsername);
		});
		modelMapper.typeMap(Users.class, UserVerifyDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserVerifyDTO::setId);
			mapper.map(Users::getAccount, UserVerifyDTO::setUsername);
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
		modelMapper.typeMap(Users.class, UserMeDTO.class).addMappings(mapper -> {
			mapper.map(Users::getUserId, UserMeDTO::setId);
			mapper.map(Users::getAccount, UserMeDTO::setUsername);
		});
		
		// UserPublic 相關
		modelMapper.typeMap(UserPublic.class, UserPublicDTO.class).addMappings(mapper -> {
			mapper.map(UserPublic::getPublicId, UserPublicDTO::setId);
			mapper.using(usersToUsernameConveter)
					.map(UserPublic::getUsers, UserPublicDTO::setUsername);
		});
		modelMapper.typeMap(UserPublic.class, UserPublicCreateDTO.class).addMappings(mapper -> {
			mapper.map(UserPublic::getPublicId, UserPublicCreateDTO::setId);
			mapper.using(usersToUsernameConveter)
					.map(UserPublic::getUsers, UserPublicCreateDTO::setUsername);
		});
		modelMapper.typeMap(UserPublic.class, UserPublicUpdateDTO.class).addMappings(mapper -> {
			mapper.map(UserPublic::getPublicId, UserPublicUpdateDTO::setId);
			mapper.using(usersToUsernameConveter)
					.map(UserPublic::getUsers, UserPublicUpdateDTO::setUsername);
		});
		
		// ChildInfo 相關
		modelMapper.typeMap(ChildInfo.class, ChildDTO.class).addMappings(mapper -> {
			mapper.map(ChildInfo::getChildId, ChildDTO::setId);
		});
		modelMapper.typeMap(ChildInfo.class, ChildCreateDTO.class).addMappings(mapper -> {
			mapper.map(ChildInfo::getChildId, ChildCreateDTO::setId);
		});
		modelMapper.typeMap(ChildInfo.class, ChildUpdateDTO.class).addMappings(mapper -> {
			mapper.map(ChildInfo::getChildId, ChildUpdateDTO::setId);
		});
		
		// ------------------------------------------------------------------------------------
		// DTO -> Entity		
		// User 相關
		modelMapper.typeMap(UserRegisterDTO.class, Users.class).addMappings(mapper -> {
			mapper.map(UserRegisterDTO::getUsername, Users::setAccount);
			// 使用自定義 Converter 進行映射
			mapper.using(roleTypeToRoleConveter)
					.map(UserRegisterDTO::getRoleNumber, Users::setRole);
		});
		
		return modelMapper;
	}
}
