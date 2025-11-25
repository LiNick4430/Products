package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping("/regulation")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RegulationController {

	@Autowired
	private RegulationService regulationService;
	
	@PostMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<RegulationDTO>> findAll(@RequestBody RegulationFindDTO regulationFindDTO) {
		List<RegulationDTO> regulationDTOs = regulationService.findAll(regulationFindDTO);
		return ApiResponse.success("搜尋 全部規範 成功", regulationDTOs);
	}
	
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<RegulationDTO> findByRegulationId(@RequestBody RegulationFindDTO regulationFindDTO) {
		RegulationDTO regulationDTO = regulationService.findByRegulationId(regulationFindDTO);
		return ApiResponse.success("搜尋 特定規範 成功", regulationDTO);
	}
	
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<RegulationDTO> createRegulation(@RequestBody RegulationCreateDTO regulationCreateDTO) {
		RegulationDTO regulationDTO = regulationService.createRegulation(regulationCreateDTO);
		return ApiResponse.success("建立 規範 成功", regulationDTO);
	}
	
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<RegulationDTO> updateRegulation(@RequestBody RegulationUpdateDTO regulationUpdateDTO) {
		RegulationDTO regulationDTO = regulationService.updateRegulation(regulationUpdateDTO);
		return ApiResponse.success("更新 規範 成功", regulationDTO);
	}
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteRegulation(@RequestBody RegulationDeleteDTO regulationDeleteDTO) {
		regulationService.deleteRegulation(regulationDeleteDTO);
		return ApiResponse.success("刪除 規範 成功", null);
	}
}
