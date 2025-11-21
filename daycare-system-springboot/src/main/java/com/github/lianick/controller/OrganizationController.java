package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.dto.organization.OrganizationCreateDTO;
import com.github.lianick.model.dto.organization.OrganizationDTO;
import com.github.lianick.model.dto.organization.OrganizationDeleteDTO;
import com.github.lianick.model.dto.organization.OrganizationDocumentDTO;
import com.github.lianick.model.dto.organization.OrganizationFindDTO;
import com.github.lianick.model.dto.organization.OrganizationUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.OrganizationService;
import com.github.lianick.util.JsonUtil;

/**
 * OrganizationController
 * Request Mapping: "/organization"
 * POST	"/find", "/find/"				尋找 特定機構資料			"/organization/find/"			PUBLIC
 * POST	"/create", "/create/"			主管 建立 新的機構		"/organization/create/"			AUTHENTICATED
 * POST	"/upload/doc", "/upload/doc/"	主管/員工 上傳 機構附件 	"/organization/upload/doc/"		AUTHENTICATED
 * POST	"/update", "/update/"			主管/員工 更新 機構資料	"/organization/update/"			AUTHENTICATED
 * DELETE	"/delete/doc", "/delete/doc/"	主管 刪除 機構附件 	"/organization/delete/doc/"		AUTHENTICATED
 * DELETE	"/delete", "/delete/"		主管 刪除 機構資料		"/organization/delete/"			AUTHENTICATED

 * */

@RestController
@RequestMapping("/organization")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class OrganizationController {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired 
	private JsonUtil jsonUtil;

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
	
	@PostMapping(value = {"/upload/doc", "/upload/doc/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<OrganizationDTO> uploadOrganizationDocument(
			@RequestPart("file") MultipartFile file,
			@RequestPart("data") String json ) {
		// 1. 把 Json 轉成 DTO 物件
		OrganizationDocumentDTO organizationDocumentDTO = jsonUtil.jsonStringToDTO(json, OrganizationDocumentDTO.class);
		
		// 2. Service 方法
		OrganizationDTO organizationDTO = organizationService.uploadOrganization(organizationDocumentDTO, file);
		return ApiResponse.success("上傳附件 成功", organizationDTO);
	}
	
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<OrganizationDTO> updateOrganization(@RequestBody OrganizationUpdateDTO organizationUpdateDTO) {
		OrganizationDTO organizationDTO = organizationService.updateOrganization(organizationUpdateDTO);
		return ApiResponse.success("更新 機構資料 成功", organizationDTO);
	}
	
	@DeleteMapping(value = {"/delete/doc", "/delete/doc/"})
	public ApiResponse<Void> deleteOrganizationDocument(@RequestBody OrganizationDocumentDTO organizationDocumentDTO) {
		organizationService.deleteOrganizationDocument(organizationDocumentDTO);
		return ApiResponse.success("刪除 機構附件 成功", null);
	}
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteOrganization(@RequestBody OrganizationDeleteDTO organizationDeleteDTO) {
		organizationService.deleteOrganization(organizationDeleteDTO);
		return ApiResponse.success("刪除 機構資料 成功", null);
	}
}
