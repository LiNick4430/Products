package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.user.UserDeleteDTO;
import com.github.lianick.model.dto.userPublic.UserPublicCreateDTO;
import com.github.lianick.model.dto.userPublic.UserPublicDTO;
import com.github.lianick.model.dto.userPublic.UserPublicUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserPublicService;

/**
 * UserPublicController
 * Request Mapping: "/public/user"
 * GET		"/me/", "/me"					民眾 取得 自己的資料	"/public/user/me/"				AUTHENTICATED
 * GET		"/find/all", "/find/all/"		尋找	全部民眾帳號	"/public/user/find/all/"		AUTHENTICATED
 * POST		"/find", "/find/"				尋找	特定民眾帳號	"/public/user/find/"			AUTHENTICATED
 * POST		"/information", "/information/"	設定 民眾基本資料		"/public/user/information/"		AUTHENTICATED
 * POST		"/update", "/update/"			更新 民眾基本資料		"/public/user/update/"			AUTHENTICATED
 * DELETE	"/delete", "/delete/"			刪除	帳號			"/public/user/delete/"			AUTHENTICATED
 * */

@RestController
@RequestMapping("/public/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserPublicController {
	
	@Autowired
	private UserPublicService userPublicService;
	
	@GetMapping(value = {"/me/", "/me"})
	public ApiResponse<UserPublicDTO> me() {
		UserPublicDTO userPublicDTO = userPublicService.findUserPublicDTO();
		return ApiResponse.success("找尋自己 成功", userPublicDTO);
	}
	
	@GetMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<UserPublicDTO>> findAll() {
		List<UserPublicDTO> userPublicDTOs = userPublicService.findAllUserPublic();
		// return new ApiResponse<>(HttpStatus.OK.value(), "搜尋 全部民眾資料成功", userPublicDTOs);
		return ApiResponse.success("搜尋 全部民眾資料成功", userPublicDTOs);
	}
	
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<UserPublicDTO> find(@RequestBody UserPublicDTO userPublicDTO) {
		userPublicDTO = userPublicService.findByUsername(userPublicDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "搜尋 民眾資料成功", userPublicDTO);
		return ApiResponse.success("搜尋 民眾資料成功", userPublicDTO);
	}
	
	@PostMapping(value = {"/information", "/information/"})
	public ApiResponse<UserPublicCreateDTO> information(@RequestBody UserPublicCreateDTO userPublicCreateDTO) {
		userPublicCreateDTO = userPublicService.createUserPublic(userPublicCreateDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "民眾資料 建立成功", userPublicCreateDTO);
		return ApiResponse.success("民眾資料 建立成功", userPublicCreateDTO);
	}

	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<UserPublicUpdateDTO> update(@RequestBody UserPublicUpdateDTO userPublicUpdateDTO) {
		userPublicUpdateDTO = userPublicService.updateUserPublic(userPublicUpdateDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "民眾資料 更新成功", userPublicUpdateDTO);
		return ApiResponse.success("民眾資料 更新成功", userPublicUpdateDTO);
	}
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> delete(@RequestBody UserDeleteDTO userDeleteDTO) {
		userPublicService.deleteUserPublic(userDeleteDTO);
		// return new ApiResponse<Void>(HttpStatus.OK.value(), "民眾帳號 刪除成功", null);
		return ApiResponse.success("民眾帳號 刪除成功", null);
	}
}
