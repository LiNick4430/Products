package com.github.lianick.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.github.lianick.model.dto.UserDeleteDTO;
import com.github.lianick.model.dto.UserForgetPasswordDTO;
import com.github.lianick.model.dto.UserLoginDTO;
import com.github.lianick.model.dto.UserRegisterDTO;
import com.github.lianick.model.eneity.Users;
import com.github.lianick.repository.UsersRepository;
import com.github.lianick.service.UserSrevice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor	// Lombok 會自動生成包含所有 final 欄位的建構式
public class UserSreviceImpl implements UserSrevice{

	@Autowired
	private UsersRepository usersRepository;
	
	// 注入時不需要 @Autowired，因為 Lombok 會處理它
	@Qualifier("userModelMapper")
	private final ModelMapper userMapper;
	
	public Users convertToUser(UserRegisterDTO userRegisterDTO) {
		return userMapper.map(userRegisterDTO, Users.class);
	}
	
	@Override
	public UserRegisterDTO registerUser(UserRegisterDTO userRegisterDTO) {
		Users user = convertToUser(userRegisterDTO);
		usersRepository.save(user);
		return userRegisterDTO;
	}

	@Override
	public void veriftyUser(String token) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserLoginDTO loginUser(UserLoginDTO userLoginDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserForgetPasswordDTO forgetPassword(UserForgetPasswordDTO userForgetPasswordDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(UserDeleteDTO userDeleteDTO) {
		// TODO Auto-generated method stub
		
	}
}
