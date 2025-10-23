package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.user.UserRegisterDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserMeDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserService;

/**
 * UserController
 * Request Mapping: "/user"
 * POST		"/register", "/register/"				註冊	帳號			"/user/register/"			PUBLIC
 * POST		"/reset/password", "/reset/password/"	重設	密碼			"/user/reset/password/"		PUBLIC
 * POST		"/update", "/update/"					更新	帳號			"/user/update/"				AUTHENTICATED
 * DELETE	"/delete", "/delete/"					刪除	帳號			"/user/delete/"				AUTHENTICATED
 * GET		"/me", "/me/"							從JWT獲得基本資料	"/user/me/"					AUTHENTICATED
 * */

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController{
	
	@Autowired
	private UserService userService;
	
	@PostMapping(value = {"/register", "/register/"})
	public ApiResponse<UserRegisterDTO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
		userRegisterDTO = userService.registerUser(userRegisterDTO);
		return new ApiResponse<UserRegisterDTO>(HttpStatus.OK.value(), "帳號建立成功, 請驗證信箱", userRegisterDTO);
	}
	
	@PostMapping(value = {"/reset/password", "/reset/password/"})
	public ApiResponse<UserForgetPasswordDTO> resetPassword(@RequestBody UserForgetPasswordDTO userForgetPasswordDTO) {
		userForgetPasswordDTO = userService.forgetPasswordUpdatePassword(userForgetPasswordDTO);
		return new ApiResponse<UserForgetPasswordDTO>(HttpStatus.OK.value(), "密碼更新完成, 請使用新密碼登入", userForgetPasswordDTO);
	}
	
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<UserUpdateDTO> update(@RequestBody UserUpdateDTO userUpdateDTO) {
		userUpdateDTO = userService.updateUser(userUpdateDTO);
		return new ApiResponse<UserUpdateDTO>(HttpStatus.OK.value(), "資料更新完成 請重新登入", userUpdateDTO);
	}
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> delete(@RequestBody UserDeleteDTO userDeleteDTO) {
		userService.deleteUser(userDeleteDTO);
		return new ApiResponse<Void>(HttpStatus.OK.value(), "帳號刪除成功", null);
	}
	
	@GetMapping(value = {"/me", "/me/"})
	public ApiResponse<UserMeDTO> getUserDetails() {
		UserMeDTO userMeDTO = userService.getUserDetails();
		return new ApiResponse<>(HttpStatus.OK.value(), "獲取登入資料 成功", userMeDTO);
	}
	
}
