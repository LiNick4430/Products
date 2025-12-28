package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.AuthResponseDTO;
import com.github.lianick.model.dto.user.UserLoginDTO;
import com.github.lianick.model.dto.user.UserUpdateDTO;
import com.github.lianick.model.dto.userPublic.UserPublicDTO;
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.AuthService;
import com.github.lianick.service.RefreshTokenService;
import com.github.lianick.service.UserPublicService;
import com.github.lianick.service.UserService;
import com.github.lianick.swagger.annotation.LoginApiDoc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(
		name = "Auth",
		description = "驗證相關的API"
		)
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
	@LoginApiDoc
	public ApiResponse<AuthResponseDTO> login (@RequestBody UserLoginDTO userLoginDTO) {		
		AuthResponseDTO authResponseDTO = authService.login(userLoginDTO);	
		// return new ApiResponse<>(HttpStatus.OK.value(), "登陸成功", authResponseDTO);
		return ApiResponse.success("登入成功", authResponseDTO);
	}
	
	// 在 JWT 架構中，登出行為由前端負責銷毀 Token。
	@Operation(
			summary = "使用者登出",
			description = """
					進行登出程序, 作廢 刷新TOKEN, 並提醒前端 作廢 JWT
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping(value = {"/logout", "/logout/"})
	public ApiResponse<Void> logout () {
		authService.logout();		// 刪除 RefreshToken
		// return new ApiResponse<>(HttpStatus.OK.value(), "登出成功，請清除客戶端 Token", null);	// 只是返回成功訊息，告訴前端可以安全地刪除其儲存的 Token 了
		return ApiResponse.success("登出成功，請清除客戶端 Token", null);
	}
	
	@Operation(
			summary = "使用者更新前 密碼驗證",
			description = """
					使用者更新資料前, 進行的密碼驗證
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/check/password", "/check/password/"})
	public ApiResponse<UserUpdateDTO> updateCheckPassword (@RequestBody UserUpdateDTO userUpdateDTO) {
		userUpdateDTO = userService.updateUserCheckPassword(userUpdateDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userUpdateDTO);
		return ApiResponse.success("密碼確認成功, 進入修改資料網頁", userUpdateDTO);
	}
	
	@Operation(
			summary = "民眾更新前 密碼驗證",
			description = """
					民眾更新資料前, 進行的密碼驗證
					- 權限限制：ROLE_PUBLIC
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/public/check/password", "/public/check/password/"})
	public ApiResponse<UserPublicDTO> updatePublicCheckPassword (@RequestBody UserPublicUpdateDTO userPublicUpdateDTO) {
		UserPublicDTO userPublicDTO = userPublicService.updateUserPublicCheckPassword(userPublicUpdateDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "密碼確認成功, 進入修改資料網頁", userPublicUpdateDTO);
		return ApiResponse.success("密碼確認成功, 進入修改資料網頁", userPublicDTO);
	}
	
	@Operation(
			summary = "JWT 到期時 自動進行刷新程序",
			description = """
					JWT 過期的時候 會返回 ErrorCode: "JWT_EXPIRED"
					前端 將 收到這個錯誤代碼後 自動使用這個方法(回傳 更新TOKEN)
					刷新同時 會作廢 過去的 TOKEN們 並回傳 新的
					"""
			)
	@PostMapping(value = {"/access/token/refresh", "/access/token/refresh/"})
	public ApiResponse<AuthResponseDTO> updateAccessToken(@RequestBody AuthResponseDTO authResponseDTO) {
		authResponseDTO = refreshTokenService.updateRefreshToken(authResponseDTO.getRefreshToken());
		// return new ApiResponse<AuthResponseDTO>(HttpStatus.OK.value(), "Refresh token 更新成功", authResponseDTO);
		return ApiResponse.success("Refresh token 更新成功", authResponseDTO);
	}
}
