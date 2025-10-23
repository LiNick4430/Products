package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.user.UserForgetPasswordDTO;
import com.github.lianick.model.dto.user.UserVerifyDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserService;

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

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class EmailController {

	@Autowired
	private UserService userService;
	
	@PostMapping(value = {"/send/password", "/send/password/"})
	public ApiResponse<UserForgetPasswordDTO> sendPassword(@RequestBody UserForgetPasswordDTO userForgetPasswordDTO) {
		userForgetPasswordDTO = userService.forgetPasswordSendEmail(userForgetPasswordDTO);
		return new ApiResponse<UserForgetPasswordDTO>(HttpStatus.OK.value(), "驗證信寄出成功", userForgetPasswordDTO);
	}
	
	@GetMapping("/verify")
	public ApiResponse<UserVerifyDTO> verify(@RequestParam String token) {
		UserVerifyDTO userVerifyDTO = userService.veriftyUser(token);
		return new ApiResponse<UserVerifyDTO>(HttpStatus.OK.value(), "帳號啟用成功", userVerifyDTO);
	}
	
	@GetMapping("/reset/password")
	public ApiResponse<UserForgetPasswordDTO> resetPassword(@RequestParam String token) {
		UserForgetPasswordDTO userForgetPasswordDTO = userService.forgetPasswordVerifty(token);
		return new ApiResponse<UserForgetPasswordDTO>(HttpStatus.OK.value(), "驗證成功, 進入修改密碼網頁", userForgetPasswordDTO);
	}
	
}
