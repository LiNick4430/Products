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
import com.github.lianick.model.dto.clazz.ClassLinkCaseDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.ClassService;

/**
 * ClassController
 * Request Mapping: "/class"
 * POST	"/find", "/find/"				尋找 機構旗下班級		"/class/find/"				AUTHENTICATED
 * POST	"/create", "/create/"			建立 機構 新班級		"/class/create/"			AUTHENTICATED
 * POST	"/link/case", "/link/case/"		和 案件 建立關連		"/class/link/case/"			AUTHENTICATED
 * DELETE	"/delete", "/delete/"		刪除 	班級			"/class/delete/"			AUTHENTICATED
 * */

@RestController
@RequestMapping("/class")
public class ClassController {

	@Autowired
	private ClassService classService;
	
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<List<ClassDTO>> findAllClass(@RequestBody ClassFindDTO classFindDTO) {
		List<ClassDTO> classDTOs = classService.findAllClassByOrganization(classFindDTO);
		return ApiResponse.success("尋找 機構旗下班級 成功", classDTOs);
	}
	
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
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteClass(@RequestBody ClassDeleteDTO classDeleteDTO) {
		classService.deleteClass(classDeleteDTO);
		return ApiResponse.success("刪除 班級", null);
	} 
}
