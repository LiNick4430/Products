package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AuthContorller
 * Request Mapping: "/auth"
 * POST	"/login/"			登入			"/auth/login/"
 * GET  "/logout/"			登出			"/auth/logout/"
 * POST	"/check/password/"	修改前確認密碼	"/auth/check/password/"
 * */

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthContorller {

	@Autowired
	private UserService userService;
	
	@PostMapping("/login/")
	public ApiResponse<UserLoginDTO> login (@RequestBody UserLoginDTO userLoginDTO, HttpSession httpSession) {
		userLoginDTO = userService.loginUser(userLoginDTO);
		httpSession.setAttribute("userLoginDTO", userLoginDTO);
		return new ApiResponse<UserLoginDTO>(HttpStatus.OK.value(), "登陸成功", userLoginDTO);
	}
	
	@GetMapping("/logout/")
	public ApiResponse<Void> getMethodName(HttpSession httpSession) {
		httpSession.invalidate();
		return new ApiResponse<Void>(HttpStatus.OK.value(), "登陸成功", null);
	}
	
	@PostMapping("/check/password/")
	public ApiResponse<UserUpdateDTO> login (@RequestBody UserUpdateDTO userUpdateDTO) {
		userUpdateDTO = userService.updateUserCheckPassword(userUpdateDTO);
		return new ApiResponse<UserUpdateDTO>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userUpdateDTO);
	}
	
}
