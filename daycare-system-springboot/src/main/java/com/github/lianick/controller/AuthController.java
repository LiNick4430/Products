package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.AuthResponseDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.AuthService;
import com.github.lianick.service.RefreshTokenService;
import com.github.lianick.service.UserPublicService;
import com.github.lianick.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * AuthContorller
 * Request Mapping: "/auth"
 * POST	"/login", "/login/"									登入					"/auth/login/"					PUBLIC
 * GET  "/logout", "/logout/"								登出					"/auth/logout/"					AUTHENTICATED
 * POST	"/check/password", "/check/password/"				修改前		確認密碼	"/auth/check/password/"			AUTHENTICATED
 * POST	"/public/check/password", "/public/check/password/"	民眾資料修改前	確認密碼	"/auth/public/check/password/"	AUTHENTICATED
 * POST	"/access/token/refresh", "/access/token/refresh/"	access token 刷新		"/auth/access/token/refresh/"	PUBLIC(當前端收到 "JWT_EXPIRED" "Access Token is expired" 時使用)
 * */

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserPublicService userPublicService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private AuthService authService;
	
	@PostMapping(value = {"/login", "/login/"})
	public ApiResponse<AuthResponseDTO> login (@RequestBody UserLoginDTO userLoginDTO) {		
		AuthResponseDTO authResponseDTO = authService.login(userLoginDTO);	
		// return new ApiResponse<>(HttpStatus.OK.value(), "登陸成功", authResponseDTO);
		return ApiResponse.success("登陸成功", authResponseDTO);
	}
	
	// 在 JWT 架構中，登出行為由前端負責銷毀 Token。
	@GetMapping(value = {"/logout", "/logout/"})
	public ApiResponse<Void> logout () {
		authService.logout();		// 刪除 RefreshToken
		// return new ApiResponse<>(HttpStatus.OK.value(), "登出成功，請清除客戶端 Token", null);	// 只是返回成功訊息，告訴前端可以安全地刪除其儲存的 Token 了
		return ApiResponse.success("登出成功，請清除客戶端 Token", null);
	}
	
	@PostMapping(value = {"/check/password", "/check/password/"})
	public ApiResponse<UserUpdateDTO> updateCheckPassword (@RequestBody UserUpdateDTO userUpdateDTO) {
		userUpdateDTO = userService.updateUserCheckPassword(userUpdateDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userUpdateDTO);
		return ApiResponse.success("密碼確認成功, 進入修改資料網頁", userUpdateDTO);
	}
	
	@PostMapping(value = {"/public/check/password", "/public/check/password/"})
	public ApiResponse<UserPublicUpdateDTO> updatePublicCheckPassword (@RequestBody UserPublicUpdateDTO userPublicUpdateDTO) {
		userPublicUpdateDTO = userPublicService.updateUserPublicCheckPassword(userPublicUpdateDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userPublicUpdateDTO);
		return ApiResponse.success("密碼確認成功, 進入修改資料網頁", userPublicUpdateDTO);
	}
	
	@PostMapping(value = {"/access/token/refresh", "/access/token/refresh/"})
	public ApiResponse<AuthResponseDTO> updateAccessToken(@RequestBody AuthResponseDTO authResponseDTO) {
		authResponseDTO = refreshTokenService.updateRefreshToken(authResponseDTO.getRefreshToken());
		// return new ApiResponse<AuthResponseDTO>(HttpStatus.OK.value(), "Refresh token 更新成功", authResponseDTO);
		return ApiResponse.success("Refresh token 更新成功", authResponseDTO);
	}
}
