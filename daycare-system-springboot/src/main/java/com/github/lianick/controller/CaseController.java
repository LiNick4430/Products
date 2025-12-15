package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lianick.model.dto.cases.CaseCreateDTO;
import com.github.lianick.model.dto.cases.CaseDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.CaseService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * CaseController
 * Request Mapping: "/case"
 * POST	"/create", "/create/"					民眾建立新的案件			"/case/create/"						AUTHENTICATED
 * */

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/case")
public class CaseController {

	@Autowired
	private CaseService caseService;
	
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<CaseDTO> createNewCase(@RequestBody CaseCreateDTO caseCreateDTO) {
		CaseDTO caseDTO = caseService.createNewCase(caseCreateDTO);
		return ApiResponse.success("民眾 建立案件 成功", caseDTO);
	}
	
}
