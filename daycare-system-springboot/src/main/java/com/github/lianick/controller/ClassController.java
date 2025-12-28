package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.clazz.ClassCreateDTO;
import com.github.lianick.model.dto.clazz.ClassDTO;
import com.github.lianick.model.dto.clazz.ClassDeleteDTO;
import com.github.lianick.model.dto.clazz.ClassFindDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.ClassService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ClassController
 * Request Mapping: "/class"
 * POST	"/find", "/find/"				尋找 機構旗下班級		"/class/find/"				AUTHENTICATED
 * POST	"/create", "/create/"			建立 機構 新班級		"/class/create/"			AUTHENTICATED
 * POST	"/link/case", "/link/case/"		和 案件 建立關連		"/class/link/case/"			AUTHENTICATED
 * DELETE	"/delete", "/delete/"		刪除 	班級			"/class/delete/"			AUTHENTICATED
 * */

@Tag(
		name = "Class",
		description = "班級相關的API"
		)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/class")
public class ClassController {

	@Autowired
	private ClassService classService;
	
	@Operation(
			summary = "搜尋機構底下的全部班級",
			description = """
					搜尋機構底下的全部班級, 依照角色改變搜尋方式
					- ROLE_PUBLIC 使用 organizationName 搜尋班級(可以部份符合)
					- ROLE_STAFF 使用 organizationId 搜尋班級(限定自己的機構)
					- ROLE_MANAGER 使用 organizationId 搜尋班級(可以全部機構)
					"""
			)
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<List<ClassDTO>> findAllClass(@RequestBody ClassFindDTO classFindDTO) {
		List<ClassDTO> classDTOs = classService.findAllClassByOrganization(classFindDTO);
		return ApiResponse.success("尋找 機構旗下班級 成功", classDTOs);
	}
	
	@Operation(
			summary = "建立機構的班級",
			description = """
					建立機構的班級, 依照角色改變搜尋方式
					- ROLE_STAFF 建立 自身機構底下的班級
					- ROLE_MANAGER 建立 特定機構底下的班級
					"""
			)
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<ClassDTO> createClass(@RequestBody ClassCreateDTO classCreateDTO) {
		ClassDTO classDTO = classService.createClass(classCreateDTO);
		return ApiResponse.success("建立 機構 新班級 成功", classDTO);
	}
	
	/*
	@PostMapping(value = {"/link/case", "/link/case/"})
	public ApiResponse<ClassDTO> classLinkCase(@RequestBody ClassLinkCaseDTO classLinkCaseDTO) {
		ClassDTO classDTO = classService.classLinkCase(classLinkCaseDTO);
		return ApiResponse.success("和 案件 建立關連 成功", classDTO);
	}
	*/
	
	@Operation(
			summary = "刪除機構的班級",
			description = """
					刪除機構的班級, 依照角色改變搜尋方式
					- ROLE_STAFF 刪除 自身機構底下的班級
					- ROLE_MANAGER 刪除 特定機構底下的班級
					"""
			)
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteClass(@RequestBody ClassDeleteDTO classDeleteDTO) {
		classService.deleteClass(classDeleteDTO);
		return ApiResponse.success("刪除 班級", null);
	} 
}
