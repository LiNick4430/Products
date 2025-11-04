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
import com.github.lianick.model.dto.userAdmin.UserAdminCreateDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserAdminService;

/**
 * UserAdminController
 * Request Mapping: "/admin/user"
 * GET	"/me/", "/me"					主管/員工 取得 自己的資料	"/admin/user/me/"			AUTHENTICATED
 * GET	"/find/all", "/find/all/"		主管 尋找 全部員工帳號		"/admin/user/find/all/"		AUTHENTICATED
 * POST	"/find", "/find/"				主管 尋找 特定員工帳號		"/admin/user/find/"			AUTHENTICATED
 * POST	"/create", "/create/"			主管 設定 員工基本資料		"/admin/user/create/"		AUTHENTICATED
 * POST	"/update", "/update/"			主管 更新 員工基本資料		"/admin/user/update/"		AUTHENTICATED
 * DELETE"/delete", "/delete/"			主管 刪除 特定員工帳號		"/admin/user/delete/"		AUTHENTICATED
 * */

@RestController
@RequestMapping("/admin/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserAdminController {

	@Autowired
	private UserAdminService userAdminService;
	
	@GetMapping(value = {"/me/", "/me"})
	public ApiResponse<UserAdminDTO> findMe() {
		UserAdminDTO userAdminDTO = userAdminService.findUserAdmin();
		return ApiResponse.success("尋找 自己 成功", userAdminDTO);
	}
	
	@GetMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<UserAdminDTO>> findAllUserAdmin() {
		List<UserAdminDTO> userAdminDTOs = userAdminService.findAllUserAdmin();
		return ApiResponse.success("尋找 全部員工帳號 成功", userAdminDTOs);
	}
	
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<UserAdminDTO> findUserAdmin(@RequestBody UserAdminDTO userAdminDTO) {
		userAdminDTO = userAdminService.findByUsername(userAdminDTO);
		return ApiResponse.success("尋找 特定員工帳號 成功", userAdminDTO);
	}
	
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<UserAdminDTO> createUserAdmin(@RequestBody UserAdminCreateDTO userAdminCreateDTO) {
		UserAdminDTO userAdminDTO = userAdminService.createUserAdmin(userAdminCreateDTO);
		return ApiResponse.success("設定 員工基本資料 成功, 請通知員工 進入信箱 啟動帳號", userAdminDTO);
	}
	
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<UserAdminDTO> updateUserAdmin(@RequestBody UserAdminUpdateDTO userAdminUpdateDTO) {
		UserAdminDTO userAdminDTO = userAdminService.updateUserAdmin(userAdminUpdateDTO);
		return ApiResponse.success("更新 員工基本資料 成功", userAdminDTO);
	}
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteUserAdmin(@RequestBody UserDeleteDTO userDeleteDTO) {
		userAdminService.deleteUserAdmin(userDeleteDTO);
		return ApiResponse.success("刪除 特定員工帳號 成功", null);
	}
}
