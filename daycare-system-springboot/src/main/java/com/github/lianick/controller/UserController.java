package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * UserController
 * Request Mapping: "/user"
 * POST		"/register", "/register/"				註冊	帳號			"/user/register/"			PUBLIC
 * POST		"/reset/password", "/reset/password/"	重設	密碼			"/user/reset/password/"		PUBLIC
 * POST		"/update", "/update/"					更新	帳號			"/user/update/"				AUTHENTICATED
 * DELETE	"/delete", "/delete/"					刪除	帳號			"/user/delete/"				AUTHENTICATED
 * GET		"/me", "/me/"							從JWT獲得基本資料	"/user/me/"					AUTHENTICATED
 * */

@Tag(
		name = "User",
		description = "使用者帳號相關API"
		)
@RestController
@RequestMapping("/user")
public class UserController{

	@Autowired
	private UserService userService;

	@Operation(
			summary = "使用者註冊",
			description = "建立新使用者帳號, 註冊後成功需進行 Email 驗證"
			)
	@PostMapping(value = {"/register", "/register/"})
	public ApiResponse<UserRegisterDTO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
		userRegisterDTO = userService.registerUser(userRegisterDTO);
		// return new ApiResponse<UserRegisterDTO>(HttpStatus.OK.value(), "帳號建立成功, 請驗證信箱", userRegisterDTO);
		return ApiResponse.success("帳號建立成功, 請驗證信箱", userRegisterDTO);
	}

	@Operation(
			summary = "忘記密碼 / 重設密碼",
			description = "使用者透過 Email 驗證碼或 Token 重設密碼"
			)
	@PostMapping(value = {"/reset/password", "/reset/password/"})
	public ApiResponse<UserForgetPasswordDTO> resetPassword(@RequestBody UserForgetPasswordDTO userForgetPasswordDTO) {
		userForgetPasswordDTO = userService.forgetPasswordUpdatePassword(userForgetPasswordDTO);
		// return new ApiResponse<UserForgetPasswordDTO>(HttpStatus.OK.value(), "密碼更新完成, 請使用新密碼登入", userForgetPasswordDTO);
		return ApiResponse.success("密碼更新完成, 請使用新密碼登入", userForgetPasswordDTO);
	}

	@Operation(
			summary = "更新使用者資料",
			description = """
					需要 JWT。
					- 可更新 密碼
					- 可更新 電話號碼
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<UserUpdateDTO> update(@RequestBody UserUpdateDTO userUpdateDTO) {
		userUpdateDTO = userService.updateUser(userUpdateDTO);
		// return new ApiResponse<UserUpdateDTO>(HttpStatus.OK.value(), "資料更新完成 請重新登入", userUpdateDTO);
		return ApiResponse.success("資料更新完成 請重新登入", userUpdateDTO);
	}

	@Operation(
			summary = "刪除使用者帳號",
			description = """
					需要 JWT。
					- 使用者需再次輸入密碼確認身份
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> delete(@RequestBody UserDeleteDTO userDeleteDTO) {
		userService.deleteUser(userDeleteDTO);
		// return new ApiResponse<Void>(HttpStatus.OK.value(), "帳號刪除成功", null);
		return ApiResponse.success("帳號刪除成功", null);
	}

	@Operation(
			summary = "取得目前登入使用者資訊",
			description = """
					需要 JWT。
					- 從 Access Token 解析使用者基本資料
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping(value = {"/me", "/me/"})
	public ApiResponse<UserMeDTO> getUserDetails() {
		UserMeDTO userMeDTO = userService.getUserDetails();
		// return new ApiResponse<>(HttpStatus.OK.value(), "獲取登入資料 成功", userMeDTO);
		return ApiResponse.success("獲取登入資料 成功", userMeDTO);
	}

}
