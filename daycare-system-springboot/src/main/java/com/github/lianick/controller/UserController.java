package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;

import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserService;

/**
 * UserController
 * Request Mapping: "/user"
 * POST		"/register/"			註冊		"/user/register/"
 * POST		"/reset/password/"		修改密碼	"/user/reset/password/"
 * POST		"/update/"				更新帳號	"/user/update/"
 * DELETE	"/delete/"				刪除帳號	"/user/delete/"
 * */

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController{
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/register/")
	public ApiResponse<UserRegisterDTO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
		userRegisterDTO = userService.registerUser(userRegisterDTO);
		return new ApiResponse<UserRegisterDTO>(HttpStatus.OK.value(), "帳號建立成功, 請驗證信箱", userRegisterDTO);
	}
	
	@PostMapping("/reset/password/")
	public ApiResponse<UserForgetPasswordDTO> resetPassword(@RequestBody UserForgetPasswordDTO userForgetPasswordDTO) {
		userForgetPasswordDTO = userService.forgetPasswordUpdatePassword(userForgetPasswordDTO);
		return new ApiResponse<UserForgetPasswordDTO>(HttpStatus.OK.value(), "密碼更新完成, 請使用新密碼登入", userForgetPasswordDTO);
	}
	
	@PostMapping("/update/")
	public ApiResponse<UserUpdateDTO> update(@RequestBody UserUpdateDTO userUpdateDTO) {
		userUpdateDTO = userService.updateUser(userUpdateDTO);
		return new ApiResponse<UserUpdateDTO>(HttpStatus.OK.value(), "資料更新完成 請重新登入", userUpdateDTO);
	}
	
	@DeleteMapping("/delete/")
	public ApiResponse<Void> delete(@RequestBody UserDeleteDTO userDeleteDTO) {
		userService.deleteUser(userDeleteDTO);
		return new ApiResponse<Void>(HttpStatus.OK.value(), "帳號刪除成功", null);
	}
	
}
