package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * EmailController
 * Request Mapping: "/email"
 * POST	"/send/password", "/send/password/"	密碼驗證信		"/email/send/password/"						PUBLIC
 * GET 	"/verify"							帳號驗證		"/email/verify?token=" + token碼				PUBLIC	(信件內附超連結使用)
 * GET 	"/reset/password"					忘記密碼驗證	"/email/reset/password?token=" + token碼		PUBLIC	(信件內附超連結使用)
 * */

@Tag(
		name = "Email",
		description = "發送信箱/信箱驗證的API"
		)
@RestController
@RequestMapping("/email")
public class EmailController {

	@Autowired
	private UserService userService;
	
	@Operation(
			summary = "忘記密碼 發送驗證信",
			description = "忘記密碼前, 需進行 Email 驗證"
			)
	@PostMapping(value = {"/send/password", "/send/password/"})
	public ApiResponse<UserForgetPasswordDTO> sendPassword(@RequestBody UserForgetPasswordDTO userForgetPasswordDTO) {
		userForgetPasswordDTO = userService.forgetPasswordSendEmail(userForgetPasswordDTO);
		// return new ApiResponse<UserForgetPasswordDTO>(HttpStatus.OK.value(), "驗證信寄出成功", userForgetPasswordDTO);
		return ApiResponse.success("驗證信寄出成功", userForgetPasswordDTO);
	}
	
	@Operation(
			summary = "帳號啟用",
			description = """
					從信箱驗證信, 啟用新帳號
					以下情形 都會發送驗證信
					- 1. 新建立帳號時
					- 2. 主管 建立新的 員工帳號時
					- 3. 進行登錄 未啟用的帳號時
					"""
			)
	@GetMapping("/verify")
	public ApiResponse<UserVerifyDTO> verify(@RequestParam String token) {
		UserVerifyDTO userVerifyDTO = userService.verifyUser(token);
		// return new ApiResponse<UserVerifyDTO>(HttpStatus.OK.value(), "帳號啟用成功", userVerifyDTO);
		return ApiResponse.success("帳號啟用成功", userVerifyDTO);
	}
	
	@Operation(
			summary = "忘記密碼 驗證碼 驗證",
			description = "Email 驗證 時的連結 驗證碼驗證"
			)
	@GetMapping("/reset/password")
	public ApiResponse<UserForgetPasswordDTO> resetPassword(@RequestParam String token) {
		UserForgetPasswordDTO userForgetPasswordDTO = userService.forgetPasswordVerifty(token);
		// return new ApiResponse<UserForgetPasswordDTO>(HttpStatus.OK.value(), "驗證成功, 進入修改密碼網頁", userForgetPasswordDTO);
		return ApiResponse.success("驗證成功, 進入修改密碼網頁", userForgetPasswordDTO);
	}
	
}
