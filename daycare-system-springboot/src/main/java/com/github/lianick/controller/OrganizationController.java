package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.organization.OrganizationCreateDTO;
import com.github.lianick.model.dto.organization.OrganizationDTO;
import com.github.lianick.model.dto.organization.OrganizationDeleteDTO;
import com.github.lianick.model.dto.organization.OrganizationFindDTO;
import com.github.lianick.model.dto.organization.OrganizationUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.OrganizationService;

/**
 * OrganizationController
 * Request Mapping: "/organization"
 * POST	"/find", "/find/"				尋找 特定機構資料			"/organization/find/"			PUBLIC
 * POST	"/create", "/create/"			主管 建立 新的機構		"/organization/create/"			AUTHENTICATED
 * POST	"/update", "/update/"			主管/員工 更新 機構資料	"/organization/update/"			AUTHENTICATED
 * DELETE	"/delete", "/delete/"		主管 刪除 機構資料		"/organization/delete/"			AUTHENTICATED

 * */

@RestController
@RequestMapping("/organization")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class OrganizationController {

	@Autowired
	private OrganizationService organizationService;
	
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<List<OrganizationDTO>> findOrganization(@RequestBody OrganizationFindDTO organizationFindDTO) {
		List<OrganizationDTO> organizationDTOs = organizationService.findOrganization(organizationFindDTO);
		return ApiResponse.success("尋找 特定機構資料 成功", organizationDTOs);
	}
	
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<OrganizationDTO> createOrganization(@RequestBody OrganizationCreateDTO organizationCreateDTO) {
		OrganizationDTO organizationDTO = organizationService.createOrganization(organizationCreateDTO);
		return ApiResponse.success("建立 新的機構 成功", organizationDTO);
	}
	
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<OrganizationDTO> updateOrganization(@RequestBody OrganizationUpdateDTO organizationUpdateDTO) {
		OrganizationDTO organizationDTO = organizationService.updateOrganization(organizationUpdateDTO);
		return ApiResponse.success("更新 機構資料 成功", organizationDTO);
	}
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteOrganization(@RequestBody OrganizationDeleteDTO organizationDeleteDTO) {
		organizationService.deleteOrganization(organizationDeleteDTO);
		return ApiResponse.success("刪除 機構資料 成功", null);
	}
}
