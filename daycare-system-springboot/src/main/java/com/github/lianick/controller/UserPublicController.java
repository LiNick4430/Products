package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(
		name = "UserPublic",
		description = "民眾帳號相關API"
		)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/public/user")
public class UserPublicController {
	
	@Autowired
	private UserPublicService userPublicService;
	
	@Operation(
			summary = "獲取自身資料",
			description = """
					透過JWT, 獲取民眾自身資料。
					- 權限限制：ROLE_PUBLIC
					"""
			)
	@GetMapping(value = {"/me/", "/me"})
	public ApiResponse<UserPublicDTO> me() {
		UserPublicDTO userPublicDTO = userPublicService.findUserPublicDTO();
		return ApiResponse.success("找尋自己 成功", userPublicDTO);
	}
	
	@Operation(
			summary = "獲取民眾資料",
			description = """
					員工/長官 尋找 民眾的資料。
					- 權限限制：ROLE_MANAGER, ROLE_STAFF
					"""
			)
	@GetMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<UserPublicDTO>> findAll() {
		List<UserPublicDTO> userPublicDTOs = userPublicService.findAllUserPublic();
		// return new ApiResponse<>(HttpStatus.OK.value(), "搜尋 全部民眾資料成功", userPublicDTOs);
		return ApiResponse.success("搜尋 全部民眾資料成功", userPublicDTOs);
	}
	
	@Operation(
			summary = "獲取特定民眾資料",
			description = """
					員工/長官 尋找 特定民眾的資料
					- 權限限制：ROLE_MANAGER, ROLE_STAFF
					"""
			)
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<UserPublicDTO> find(@RequestBody UserPublicDTO userPublicDTO) {
		userPublicDTO = userPublicService.findByUsername(userPublicDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "搜尋 民眾資料成功", userPublicDTO);
		return ApiResponse.success("搜尋 民眾資料成功", userPublicDTO);
	}
	
	@Operation(
			summary = "填寫自身資料",
			description = """
					第一次登陸時, 民眾需要填寫自身基本資料
					- 權限限制：ROLE_PUBLIC
					"""
			)
	@PostMapping(value = {"/information", "/information/"})
	public ApiResponse<UserPublicDTO> information(@RequestBody UserPublicCreateDTO userPublicCreateDTO) {
		UserPublicDTO userPublicDTO = userPublicService.createUserPublic(userPublicCreateDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "民眾資料 建立成功", userPublicCreateDTO);
		return ApiResponse.success("民眾資料 建立成功", userPublicDTO);
	}

	@Operation(
			summary = "更新自身資料",
			description = """
					民眾 修改自身基本資料
					- 可更新 姓名
					- 可更新 地址
					- 權限限制：ROLE_PUBLIC
					"""
			)
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<UserPublicDTO> update(@RequestBody UserPublicUpdateDTO userPublicUpdateDTO) {
		UserPublicDTO userPublicDTO = userPublicService.updateUserPublic(userPublicUpdateDTO);
		// return new ApiResponse<>(HttpStatus.OK.value(), "民眾資料 更新成功", userPublicUpdateDTO);
		return ApiResponse.success("民眾資料 更新成功", userPublicDTO);
	}
	
	@Operation(
			summary = "刪除使用者帳號",
			description = """
					需要 JWT。
					- 使用者需再次輸入密碼確認身份
					"""
			)
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> delete(@RequestBody UserDeleteDTO userDeleteDTO) {
		userPublicService.deleteUserPublic(userDeleteDTO);
		// return new ApiResponse<Void>(HttpStatus.OK.value(), "民眾帳號 刪除成功", null);
		return ApiResponse.success("民眾帳號 刪除成功", null);
	}
}
