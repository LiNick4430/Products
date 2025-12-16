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
import com.github.lianick.model.dto.userAdmin.UserAdminCreateDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminDTO;
import com.github.lianick.model.dto.userAdmin.UserAdminUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.UserAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(
		name = "UserAdmin",
		description = "員工/主管帳號相關API"
		)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin/user")
public class UserAdminController {

	@Autowired
	private UserAdminService userAdminService;
	
	@Operation(
			summary = "獲取自身資料",
			description = """
					透過JWT, 獲取員工/主管自身資料。
					- 權限限制：ROLE_MANAGER, ROLE_STAFF
					"""
			)
	@GetMapping(value = {"/me/", "/me"})
	public ApiResponse<UserAdminDTO> findMe() {
		UserAdminDTO userAdminDTO = userAdminService.findUserAdmin();
		return ApiResponse.success("尋找 自己 成功", userAdminDTO);
	}
	
	@Operation(
			summary = "獲取員工資料",
			description = """
					主管 尋找 員工的資料。
					- 權限限制：ROLE_MANAGER
					"""
			)
	@GetMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<UserAdminDTO>> findAllUserAdmin() {
		List<UserAdminDTO> userAdminDTOs = userAdminService.findAllUserAdmin();
		return ApiResponse.success("尋找 全部員工帳號 成功", userAdminDTOs);
	}
	
	@Operation(
			summary = "獲取特定員工資料",
			description = """
					主管 尋找 特定員工的資料。
					- 權限限制：ROLE_MANAGER
					"""
			)
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<UserAdminDTO> findUserAdmin(@RequestBody UserAdminDTO userAdminDTO) {
		userAdminDTO = userAdminService.findByUsername(userAdminDTO);
		return ApiResponse.success("尋找 特定員工帳號 成功", userAdminDTO);
	}
	
	@Operation(
			summary = "建立新的員工資料",
			description = """
					主管 建立 新的員工資料, 建立成功後 員工需進行 Email 驗證。
					- 權限限制：ROLE_MANAGER
					"""
			)
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<UserAdminDTO> createUserAdmin(@RequestBody UserAdminCreateDTO userAdminCreateDTO) {
		UserAdminDTO userAdminDTO = userAdminService.createUserAdmin(userAdminCreateDTO);
		return ApiResponse.success("設定 員工基本資料 成功, 請通知員工 進入信箱 啟動帳號", userAdminDTO);
	}
	
	@Operation(
			summary = "更新 員工資料",
			description = """
					主管 更新 員工資料, 但是不能更新主管自己。
					- 可以更新 角色權限(不能比主管自身還高)
					- 可以更新 員工名稱
					- 可以更新 職位稱呼
					- 可以更新 所屬機構
					- 權限限制：ROLE_MANAGER
					"""
			)
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<UserAdminDTO> updateUserAdmin(@RequestBody UserAdminUpdateDTO userAdminUpdateDTO) {
		UserAdminDTO userAdminDTO = userAdminService.updateUserAdmin(userAdminUpdateDTO);
		return ApiResponse.success("更新 員工基本資料 成功", userAdminDTO);
	}
	
	@Operation(
			summary = "刪除 員工帳號",
			description = """
					主管 刪除 員工帳號, 需要輸入 員工的 帳號, 無法 在此介面刪除自己。
					- 權限限制：ROLE_MANAGER
					"""
			)
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteUserAdmin(@RequestBody UserDeleteDTO userDeleteDTO) {
		userAdminService.deleteUserAdmin(userDeleteDTO);
		return ApiResponse.success("刪除 特定員工帳號 成功", null);
	}
}
