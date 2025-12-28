package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.regulation.RegulationCreateDTO;
import com.github.lianick.model.dto.regulation.RegulationDTO;
import com.github.lianick.model.dto.regulation.RegulationDeleteDTO;
import com.github.lianick.model.dto.regulation.RegulationFindDTO;
import com.github.lianick.model.dto.regulation.RegulationUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.RegulationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * RegulationController
 * Request Mapping: "/regulation"
 * POST	"/find/all", "/find/all/"			搜尋 全部規範			"/regulation/find/all/"				PUBLIC
 * POST "/find", "/find/"					搜尋 特定規範			"/regulation/find/"					PUBLIC
 * POST	"/create", "/create/"				建立 規範				"/regulation/create/"				AUTHENTICATED
 * POST	"/update", "/update/"				更新 規範				"/regulation/update/"				AUTHENTICATED
 * DELETE	"/delete", "/delete/"			刪除 規範				"/regulation/delete/"				AUTHENTICATED
 * */

@Tag(
		name = "Regulation",
		description = "規範相關的API"
		)
@RestController
@RequestMapping("/regulation")
public class RegulationController {

	@Autowired
	private RegulationService regulationService;
	
	@Operation(
			summary = "搜尋全部規範",
			description = """
					搜尋全部規範
					- 依照 特定機構ID 搜尋
					"""
			)
	@PostMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<RegulationDTO>> findAll(@RequestBody RegulationFindDTO regulationFindDTO) {
		List<RegulationDTO> regulationDTOs = regulationService.findAll(regulationFindDTO);
		return ApiResponse.success("搜尋 全部規範 成功", regulationDTOs);
	}
	
	@Operation(
			summary = "搜尋特定規範",
			description = """
					搜尋特定規範
					- 依照 特定機構ID 與 特定規範ID 搜尋
					"""
			)
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<RegulationDTO> findByRegulationId(@RequestBody RegulationFindDTO regulationFindDTO) {
		RegulationDTO regulationDTO = regulationService.findByRegulationId(regulationFindDTO);
		return ApiResponse.success("搜尋 特定規範 成功", regulationDTO);
	}
	
	@Operation(
			summary = "建立規範",
			description = """
					主管 建立規範
					- 主管 可以建立 全部的機構 的規範
					- 權限限制：ROLE_ADMIN
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<RegulationDTO> createRegulation(@RequestBody RegulationCreateDTO regulationCreateDTO) {
		RegulationDTO regulationDTO = regulationService.createRegulation(regulationCreateDTO);
		return ApiResponse.success("建立 規範 成功", regulationDTO);
	}
	
	@Operation(
			summary = "更新規範",
			description = """
					主管/員工 更新規範
					- 主管 可以更新 全部的機構 的規範
					- 員工 可以更新 自己的機構 的規範
					- 權限限制：ROLE_ADMIN
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<RegulationDTO> updateRegulation(@RequestBody RegulationUpdateDTO regulationUpdateDTO) {
		RegulationDTO regulationDTO = regulationService.updateRegulation(regulationUpdateDTO);
		return ApiResponse.success("更新 規範 成功", regulationDTO);
	}
	
	@Operation(
			summary = "刪除規範",
			description = """
					主管 刪除規範
					- 主管 可以刪除 全部的機構 的規範
					- 權限限制：ROLE_ADMIN
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteRegulation(@RequestBody RegulationDeleteDTO regulationDeleteDTO) {
		regulationService.deleteRegulation(regulationDeleteDTO);
		return ApiResponse.success("刪除 規範 成功", null);
	}
}
