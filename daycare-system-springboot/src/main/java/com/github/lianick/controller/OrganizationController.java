package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.dto.DownloadDTO;
import com.github.lianick.model.dto.organization.OrganizationCreateDTO;
import com.github.lianick.model.dto.organization.OrganizationDTO;
import com.github.lianick.model.dto.organization.OrganizationDeleteDTO;
import com.github.lianick.model.dto.organization.OrganizationDocumentDTO;
import com.github.lianick.model.dto.organization.OrganizationFindDTO;
import com.github.lianick.model.dto.organization.OrganizationUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.response.DownloadResponse;
import com.github.lianick.service.OrganizationService;
import com.github.lianick.util.JsonUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * OrganizationController
 * Request Mapping: "/organization"
 * GET	"/find/all", "/find/all/"			搜尋 全部 機構 資料		"/organization/find/all/"		PUBLIC
 * POST	"/find", "/find/"					關鍵字 搜尋 機構 資料		"/organization/find/"			PUBLIC
 * POST	"/download/doc/", "/download/doc/"	下載 特定機構附件			"/organization/download/doc/"	AUTHENTICATED
 * POST	"/create", "/create/"				主管 建立 新的機構		"/organization/create/"			AUTHENTICATED
 * POST	"/upload/doc", "/upload/doc/"		主管/員工 上傳 機構附件 	"/organization/upload/doc/"		AUTHENTICATED
 * POST	"/update", "/update/"				主管/員工 更新 機構資料	"/organization/update/"			AUTHENTICATED
 * DELETE	"/delete/doc", "/delete/doc/"	主管 刪除 機構附件 		"/organization/delete/doc/"		AUTHENTICATED
 * DELETE	"/delete", "/delete/"			主管 刪除 機構資料		"/organization/delete/"			AUTHENTICATED
 * */

@Tag(
		name = "Organization",
		description = "機構相關的API"
		)
@RestController
@RequestMapping("/organization")
public class OrganizationController {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired 
	private JsonUtil jsonUtil;

	@Operation(
			summary = "搜尋全部機構",
			description = "搜尋全部機構"
			)
	@GetMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<OrganizationDTO>> findAllOrganization() {
		List<OrganizationDTO> organizationDTOs = organizationService.findAllOrganization();
		return ApiResponse.success("搜尋 全部 機構 資料 成功", organizationDTOs);
	}
	
	@Operation(
			summary = "搜尋特定機構",
			description = """
					使用關鍵字搜尋特定的機構
					- name -> 機構名字(可以部份符合)
					- address -> 機構住址(可以部份符合)
					"""
			)
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<List<OrganizationDTO>> findOrganization(@RequestBody OrganizationFindDTO organizationFindDTO) {
		List<OrganizationDTO> organizationDTOs = organizationService.findOrganization(organizationFindDTO);
		return ApiResponse.success("關鍵字 搜尋 機構 資料 成功", organizationDTOs);
	}
	
	@Operation(
			summary = "下載機構附件",
			description = """
					下載特定的機構的特定附件
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/download/doc/", "/download/doc"})
	public ResponseEntity<Resource> download(@RequestBody OrganizationDocumentDTO organizationDocumentDTO) {
		// 1. 獲取所有下載所需資訊
		DownloadDTO downloadDTO = organizationService.downloadDocument(organizationDocumentDTO);
		
		// 2. 構建 DownloadResponse
		return DownloadResponse.create(downloadDTO);
	}
	
	@Operation(
			summary = "建立新的機構",
			description = """
					主管 建立新的機構
					- 權限限制：ROLE_ADMIN
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<OrganizationDTO> createOrganization(@RequestBody OrganizationCreateDTO organizationCreateDTO) {
		OrganizationDTO organizationDTO = organizationService.createOrganization(organizationCreateDTO);
		return ApiResponse.success("建立 新的機構 成功", organizationDTO);
	}
	
	@Operation(
			summary = "上傳機構附件",
			description = """
					主管/員工 上傳機構附件
					- 主管 可以上傳 全部的機構
					- 員工 可以上傳 自己的機構
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
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
	
	@Operation(
			summary = "更新機構訊息",
			description = """
					主管/員工 更新機構訊息
					- 主管 可以更新 全部的機構
					- 員工 可以更新 自己的機構
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<OrganizationDTO> updateOrganization(@RequestBody OrganizationUpdateDTO organizationUpdateDTO) {
		OrganizationDTO organizationDTO = organizationService.updateOrganization(organizationUpdateDTO);
		return ApiResponse.success("更新 機構資料 成功", organizationDTO);
	}
	
	@Operation(
			summary = "刪除機構附件",
			description = """
					主管 刪除機構附件
					- 權限限制：ROLE_ADMIN
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping(value = {"/delete/doc", "/delete/doc/"})
	public ApiResponse<Void> deleteOrganizationDocument(@RequestBody OrganizationDocumentDTO organizationDocumentDTO) {
		organizationService.deleteOrganizationDocument(organizationDocumentDTO);
		return ApiResponse.success("刪除 機構附件 成功", null);
	}
	
	@Operation(
			summary = "刪除機構",
			description = """
					主管 刪除機構
					- 機構中還有關聯員工 將無法刪除
					- 權限限制：ROLE_ADMIN
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> deleteOrganization(@RequestBody OrganizationDeleteDTO organizationDeleteDTO) {
		organizationService.deleteOrganization(organizationDeleteDTO);
		return ApiResponse.success("刪除 機構資料 成功", null);
	}
}
