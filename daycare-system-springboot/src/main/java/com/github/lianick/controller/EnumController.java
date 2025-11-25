package com.github.lianick.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.EnumDTO;
import com.github.lianick.model.enums.ApplicationMethod;
import com.github.lianick.model.enums.CaseOrganizationStatus;
import com.github.lianick.model.enums.CaseStatus;
import com.github.lianick.model.enums.PreferenceOrder;
import com.github.lianick.model.enums.RegulationType;
import com.github.lianick.model.enums.document.DocumentType;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.EnumService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * 給前端工程師 用的 ENUM 街口
 * 查詢 該 ENUM 的 code + description
 * EnumController
 * Request Mapping: "/enum"
 * */

@RestController
@RequestMapping("/enum")
public class EnumController {

	@Autowired
	private EnumService enumService;
	
	@GetMapping("/document/type")
	public ApiResponse<List<EnumDTO>> getDocumentType() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(DocumentType.class);
		return ApiResponse.success("DocumentType OK", enumDTOs);
	}
	
	@GetMapping("/application/method")
	public ApiResponse<List<EnumDTO>> getApplicationMethod() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(ApplicationMethod.class);
		return ApiResponse.success("ApplicationMethod OK", enumDTOs);
	}
	
	@GetMapping("/regulation/type")
	public ApiResponse<List<EnumDTO>> getRegulationType() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(RegulationType.class);
		return ApiResponse.success("RegulationType OK", enumDTOs);
	}
	
	@GetMapping("/preference/order")
	public ApiResponse<List<EnumDTO>> getPreferenceOrder() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(PreferenceOrder.class);
		return ApiResponse.success("PreferenceOrder OK", enumDTOs);
	}
	
	@GetMapping("/case/status")
	public ApiResponse<List<EnumDTO>> getCaseStatus() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(CaseStatus.class);
		return ApiResponse.success("CaseStatus OK", enumDTOs);
	}
	
	@GetMapping("/case/organization/status")
	public ApiResponse<List<EnumDTO>> getCaseOrganizationStatus() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(CaseOrganizationStatus.class);
		return ApiResponse.success("CaseOrganizationStatus OK", enumDTOs);
	}
}
