package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
 * CitizenProfileController
 * Request Mapping: "/public"
 * GET		"/find/all/"		尋找	全部民眾帳號	"/public/find/all/"			AUTHENTICATED
 * POST		"/find/"			尋找	特定民眾帳號	"/public/find/"				AUTHENTICATED
 * POST		"/information/"		設定 民眾基本資料		"/public/information/"		AUTHENTICATED
 * POST		"/update/"			更新 民眾基本資料		"/public/update/"			AUTHENTICATED
 * DELETE	"/delete/"			刪除	民眾帳號		"/public/delete/"			AUTHENTICATED
 * 
 * */

@RestController
@RequestMapping("/public")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CitizenProfileController {
	
	@Autowired
	private UserPublicService userPublicService;
	
	@GetMapping("/find/all/")
	public ApiResponse<List<UserPublicDTO>> findAll() {
		List<UserPublicDTO> userPublicDTOs = userPublicService.findAllUserPublic();
		return new ApiResponse<>(HttpStatus.OK.value(), "搜尋 全部民眾資料成功", userPublicDTOs);
	}
	
	@PostMapping("/find/")
	public ApiResponse<UserPublicDTO> find(@RequestBody UserPublicDTO userPublicDTO) {
		userPublicDTO = userPublicService.findByUsername(userPublicDTO);
		return new ApiResponse<>(HttpStatus.OK.value(), "搜尋 民眾資料成功", userPublicDTO);
	}
	
	@PostMapping("/information/")
	public ApiResponse<UserPublicCreateDTO> information(@RequestBody UserPublicCreateDTO userPublicCreateDTO) {
		userPublicCreateDTO = userPublicService.createUserPublic(userPublicCreateDTO);
		return new ApiResponse<>(HttpStatus.OK.value(), "民眾資料 建立成功", userPublicCreateDTO);
	}

	@PostMapping("/update/")
	public ApiResponse<UserPublicUpdateDTO> update(@RequestBody UserPublicUpdateDTO userPublicUpdateDTO) {
		userPublicUpdateDTO = userPublicService.updateUserPublic(userPublicUpdateDTO);
		return new ApiResponse<>(HttpStatus.OK.value(), "民眾資料 更新成功", userPublicUpdateDTO);
	}
	
	@DeleteMapping("/delete/")
	public ApiResponse<Void> delete(@RequestBody UserDeleteDTO userDeleteDTO) {
		userPublicService.deleteUserPublic(userDeleteDTO);
		return new ApiResponse<Void>(HttpStatus.OK.value(), "民眾帳號 刪除成功", null);
	}
}
