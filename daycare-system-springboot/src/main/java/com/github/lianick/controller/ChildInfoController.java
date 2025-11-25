package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.child.ChildCreateDTO;
import com.github.lianick.model.dto.child.ChildDTO;
import com.github.lianick.model.dto.child.ChildDeleteDTO;
import com.github.lianick.model.dto.child.ChildUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.ChildInfoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * ChildInfoController
 * Request Mapping: "/child"
 * GET	"/find/all", "/find/all/"		尋找 民眾底下 全部幼兒資料		"/child/find/all/"			AUTHENTICATED
 * POST	"/find", "/find/"				尋找 民眾底下 特定幼兒資料		"/child/find/"				AUTHENTICATED
 * POST	"/information", "/information/"	設定 民眾底下 新幼兒資料		"/child/information/"		AUTHENTICATED
 * POST	"/update", "/update/"			更新 民眾底下 特定幼兒資料		"/child/update/"			AUTHENTICATED
 * DELETE	"/delete", "/delete/"		刪除 民眾底下 特定幼兒資料		"/child/delete/"			AUTHENTICATED
 * */

@RestController
@RequestMapping("/child")
public class ChildInfoController {
	
	@Autowired
	private ChildInfoService childInfoService;
	
	@GetMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<ChildDTO>> findAllChild() {
		List<ChildDTO> childDTOs = childInfoService.findAllChildByUserPublic();
		return ApiResponse.success("尋找 全部嬰兒資料 成功", childDTOs);
	}
	
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<ChildDTO> findById(@RequestBody ChildDTO childDTO) {
		childDTO = childInfoService.findChildByUserPublic(childDTO);
		return ApiResponse.success("尋找 特定幼兒資料 成功", childDTO);
	}
	
	@PostMapping(value = {"/information", "/information/"})
	public ApiResponse<ChildCreateDTO> setNewChild(@RequestBody ChildCreateDTO childCreateDTO) {
		childCreateDTO = childInfoService.createChildInfo(childCreateDTO);
		return ApiResponse.success("設定 新幼兒資料 成功", childCreateDTO);
	}
	
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<ChildUpdateDTO> updateChild(@RequestBody ChildUpdateDTO childUpdateDTO) {
		childUpdateDTO = childInfoService.updateChildInfo(childUpdateDTO);
		return ApiResponse.success("更新 幼兒資料 成功", childUpdateDTO);
	}
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> daleteChild(@RequestBody ChildDeleteDTO childDeleteDTO) {
		childInfoService.deleteChildInfo(childDeleteDTO);
		return ApiResponse.success("刪除 幼兒資料 成功", null);
	}
	
}
