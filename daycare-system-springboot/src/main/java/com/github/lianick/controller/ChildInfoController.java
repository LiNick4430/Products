package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.child.ChildCreateDTO;
import com.github.lianick.model.dto.child.ChildDTO;
import com.github.lianick.model.dto.child.ChildDeleteDTO;
import com.github.lianick.model.dto.child.ChildFindDTO;
import com.github.lianick.model.dto.child.ChildUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.ChildInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * ChildInfoController
 * Request Mapping: "/child"
 * POST	"/find/all", "/find/all/"		尋找 民眾底下 全部幼兒資料		"/child/find/all/"			AUTHENTICATED
 * POST	"/find", "/find/"				尋找 民眾底下 特定幼兒資料		"/child/find/"				AUTHENTICATED
 * POST	"/information", "/information/"	設定 民眾底下 新幼兒資料		"/child/information/"		AUTHENTICATED
 * POST	"/update", "/update/"			更新 民眾底下 特定幼兒資料		"/child/update/"			AUTHENTICATED
 * DELETE	"/delete", "/delete/"		刪除 民眾底下 特定幼兒資料		"/child/delete/"			AUTHENTICATED
 * */

@Tag(
		name = "Child",
		description = "幼兒相關API"
		)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/child")
public class ChildInfoController {
	
	@Autowired
	private ChildInfoService childInfoService;
	
	@Operation(
			summary = "尋找幼兒資料",
			description = """
					尋找 幼兒們的資料
					- 民眾 搜尋 自身旗下的 幼兒資料
					- 員工/主管 則根據 publicId 尋找該 民眾 旗下的 幼兒資料
					"""
			)
	@PostMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<ChildDTO>> findAllChild(@RequestBody ChildFindDTO childFindDTO) {
		List<ChildDTO> childDTOs = childInfoService.findAllChildByUserPublic(childFindDTO);
		return ApiResponse.success("尋找 全部嬰兒資料 成功", childDTOs);
	}
	
	@Operation(
			summary = "尋找幼兒資料",
			description = """
					民眾 搜尋 自身旗下的 特定幼兒資料
					- id = 幼兒ID
					- 權限限制：ROLE_PUBLIC
					"""
			)
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<ChildDTO> findById(@RequestBody ChildFindDTO childFindDTO) {
		ChildDTO childDTO = childInfoService.findChildByUserPublic(childFindDTO);
		return ApiResponse.success("尋找 特定幼兒資料 成功", childDTO);
	}
	
	@Operation(
			summary = "建立幼兒資料",
			description = """
					民眾 建立 新的 幼兒資料
					- 權限限制：ROLE_PUBLIC
					"""
			)
	@PostMapping(value = {"/information", "/information/"})
	public ApiResponse<ChildDTO> setNewChild(@RequestBody ChildCreateDTO childCreateDTO) {
		ChildDTO childDTO = childInfoService.createChildInfo(childCreateDTO);
		return ApiResponse.success("設定 新幼兒資料 成功", childDTO);
	}
	
	@Operation(
			summary = "更新幼兒資料",
			description = """
					民眾 更新 幼兒資料
					- 可以更新 新的姓名
					- 權限限制：ROLE_PUBLIC
					"""
			)
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<ChildDTO> updateChild(@RequestBody ChildUpdateDTO childUpdateDTO) {
		ChildDTO childDTO = childInfoService.updateChildInfo(childUpdateDTO);
		return ApiResponse.success("更新 幼兒資料 成功", childDTO);
	}
	
	@Operation(
			summary = "刪除幼兒資料",
			description = """
					民眾 刪除 幼兒資料
					- 案件中 有幼兒資料 則 無法刪除
					- 權限限制：ROLE_PUBLIC
					"""
			)
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> daleteChild(@RequestBody ChildDeleteDTO childDeleteDTO) {
		childInfoService.deleteChildInfo(childDeleteDTO);
		return ApiResponse.success("刪除 幼兒資料 成功", null);
	}
	
}
