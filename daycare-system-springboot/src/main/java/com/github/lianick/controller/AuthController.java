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

import org.springframework.web.bind.annotation.GetMapping;

/**
 * AuthContorller
 * Request Mapping: "/auth"
 * POST	"/login", "/login/"									登入					"/auth/login/"					PUBLIC
 * GET  "/logout", "/logout/"								登出					"/auth/logout/"					AUTHENTICATED
 * POST	"/check/password", "/check/password/"				修改前		確認密碼	"/auth/check/password/"			AUTHENTICATED
 * POST	"/public/check/password", "/public/check/password/"	民眾資料修改前	確認密碼	"/auth/public/check/password/"	AUTHENTICATED
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
	
	@PostMapping(value = {"/login", "/login/"})
	public ApiResponse<UserLoginDTO> login (@RequestBody UserLoginDTO userLoginDTO) {
		// 1. 執行登入驗證
		userLoginDTO = userService.loginUser(userLoginDTO);
		
		// 2. 生成 JWT Token
		String token = jwtUtil.generateToken(
				userLoginDTO.getUsername(), 
				userLoginDTO.getId(), 
				userLoginDTO.getRoleNumber(),
				userLoginDTO.getRoleName()
				);
		
		// 3. 將生成的 Token 設置到 DTO
		userLoginDTO.setToken(token);
		
		// 4. 回傳包含 Token 的 DTO
		return new ApiResponse<>(HttpStatus.OK.value(), "登陸成功", userLoginDTO);
	}
	
	@GetMapping(value = {"/logout", "/logout/"})
	public ApiResponse<Void> logout () {
		// 在 JWT 架構中，登出行為由前端負責銷毀 Token。
	    // 對於大多數應用來說，只需讓前端銷毀 Token 即可。
	    
	    // 如果您想強制清空當前的 Spring Security Context (雖然 Token 仍然有效，但可以強制當前請求失效):
	    // SecurityContextHolder.clearContext();
	    
	    // 只是返回成功訊息，告訴前端可以安全地刪除其儲存的 Token 了
		return new ApiResponse<>(HttpStatus.OK.value(), "登出成功，請清除客戶端 Token", null);
	}
	
	@PostMapping(value = {"/check/password", "/check/password/"})
	public ApiResponse<UserUpdateDTO> updateCheckPassword (@RequestBody UserUpdateDTO userUpdateDTO) {
		userUpdateDTO = userService.updateUserCheckPassword(userUpdateDTO);
		return new ApiResponse<>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userUpdateDTO);
	}
	
	@PostMapping(value = {"/public/check/password", "/public/check/password/"})
	public ApiResponse<UserPublicUpdateDTO> updatePublicCheckPassword (@RequestBody UserPublicUpdateDTO userPublicUpdateDTO) {
		userPublicUpdateDTO = userPublicService.updateUserPublicCheckPassword(userPublicUpdateDTO);
		return new ApiResponse<>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userPublicUpdateDTO);
	}
}
