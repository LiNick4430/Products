package com.github.lianick.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.EnumDTO;
import com.github.lianick.model.enums.ApplicationMethod;
import com.github.lianick.model.enums.CaseOrganizationStatus;
import com.github.lianick.model.enums.CaseStatus;
import com.github.lianick.model.enums.LotteryQueueStatus;
import com.github.lianick.model.enums.LotteryResultStatus;
import com.github.lianick.model.enums.PreferenceOrder;
import com.github.lianick.model.enums.RegulationType;
import com.github.lianick.model.enums.WithdrawalRequestStatus;
import com.github.lianick.model.enums.document.DocumentType;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.EnumService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * 給前端工程師 用的 ENUM 街口
 * 查詢 該 ENUM 的 code + description
 * EnumController
 * Request Mapping: "/enum"
 * GET 	"/document/type" 				DocumentType
 * GET 	"/application/method" 			ApplicationMethod
 * GET 	"/regulation/type" 				RegulationType
 * GET 	"/preference/order" 			PreferenceOrder
 * GET 	"/case/status" 					CaseStatus
 * GET 	"/case/organization/status" 	CaseOrganizationStatus
 * GET 	"/lottery/queue/status" 		LotteryQueueStatus
 * GET 	"/lottery/result/status" 		LotteryResultStatus
 * GET 	"/withdrawal/request/status" 	WithdrawalRequestStatus
 * */

@Tag(
		name = "Enum",
		description = """
				尋找特定 Enum 的API
				- 前台 顯示 需要
				- DTO 回傳 需要
				"""
		)
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
	
	@GetMapping("/lottery/queue/status")
	public ApiResponse<List<EnumDTO>> getLotteryQueueStatus() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(LotteryQueueStatus.class);
		return ApiResponse.success("LotteryQueueStatus OK", enumDTOs);
	}
	
	@GetMapping("/lottery/result/status")
	public ApiResponse<List<EnumDTO>> getLotteryResultStatus() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(LotteryResultStatus.class);
		return ApiResponse.success("LotteryResultStatus OK", enumDTOs);
	}
	
	@GetMapping("/withdrawal/request/status")
	public ApiResponse<List<EnumDTO>> getWithdrawalRequestStatus() {
		List<EnumDTO> enumDTOs = enumService.convertToDTOList(WithdrawalRequestStatus.class);
		return ApiResponse.success("WithdrawalRequestStatus OK", enumDTOs);
	}
}
