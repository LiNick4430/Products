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
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserPublicService;
import com.github.lianick.service.UserService;
import com.github.lianick.util.JwtUtil;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AuthContorller
 * Request Mapping: "/auth"
 * POST	"/login/"					登入				"/auth/login/"
 * GET  "/logout/"					登出				"/auth/logout/"
 * POST	"/check/password/"			修改前      確認密碼	"/auth/check/password/"
 * POST	"/public/check/password/"	民眾資料修改前 確認密碼	"/auth/public/check/password/"
 * */

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserPublicService userPublicService;
	
	@Autowired
	private JwtUtil jwtUtil;	// 注入 JWT 工具
	
	@PostMapping("/login/")
	public ApiResponse<UserLoginDTO> login (@RequestBody UserLoginDTO userLoginDTO) {
		// 1. 執行登入驗證
		userLoginDTO = userService.loginUser(userLoginDTO);
		
		// 2. 生成 JWT Token
		String token = jwtUtil.generateToken(
				userLoginDTO.getUsername(), 
				userLoginDTO.getId(), 
				userLoginDTO.getRoleNumber());
		
		// 3. 將生成的 Token 設置到 DTO
		userLoginDTO.setToken(token);
		
		// 4. 回傳包含 Token 的 DTO
		return new ApiResponse<>(HttpStatus.OK.value(), "登陸成功", userLoginDTO);
	}
	
	@GetMapping("/logout/")
	public ApiResponse<Void> logout (HttpSession httpSession) {
		httpSession.invalidate();
		return new ApiResponse<>(HttpStatus.OK.value(), "登出成功", null);
	}
	
	@PostMapping("/check/password/")
	public ApiResponse<UserUpdateDTO> updateCheckPassword (@RequestBody UserUpdateDTO userUpdateDTO) {
		userUpdateDTO = userService.updateUserCheckPassword(userUpdateDTO);
		return new ApiResponse<>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userUpdateDTO);
	}
	
	@PostMapping("/public/check/password/")
	public ApiResponse<UserPublicUpdateDTO> updatePublicCheckPassword (@RequestBody UserPublicUpdateDTO userPublicUpdateDTO) {
		userPublicUpdateDTO = userPublicService.updateUserPublicCheckPassword(userPublicUpdateDTO);
		return new ApiResponse<>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userPublicUpdateDTO);
	}
}
