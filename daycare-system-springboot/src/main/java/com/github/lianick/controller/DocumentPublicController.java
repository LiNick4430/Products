package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.dto.documentPublic.DocumentPublicCreateDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDeleteDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicFindDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicLinkDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicVerifyDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.DocumentPublicService;

/**
 * DocumentPublicController	
 * Request Mapping: "/document"
 * GET 		"/public/find", "/public/find/"				尋找 民眾底下 所有的附件	"/document/public/find/" 		AUTHENTICATED
 * POST 	"/case/find", "/case/find/"					尋找 案件底下 所有的附件	"/document/case/find/" 			AUTHENTICATED
 * POST		"/public/create", "/public/create/"			民眾建立 新的附件			"/document/public/create/" 		AUTHENTICATED
 * POST		"/case/create", "/case/create/"				民眾為了 案件建立 新的附件	"/document/case/create/" 		AUTHENTICATED
 * POST		"/public/link/case", "/public/link/case/"	民眾為了 案件關連 舊有的附件	"/document/public/link/case/" 	AUTHENTICATED
 * POST		"/admin/verify", "/admin/verify/"			員工 審核 附件			"/document/admin/verify/" 		AUTHENTICATED
 * DELETE 	"/public/delete", "/public/delete/"			民眾刪除 附件			"/document/public/delete/" 		AUTHENTICATED
 * */

@RestController
@RequestMapping("/document")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DocumentPublicController {
	
	@Autowired
	private DocumentPublicService documentPublicService;

	@GetMapping(value = {"/public/find", "/public/find/"})
	public ApiResponse<List<DocumentPublicDTO>> findAllDocByPublic() {
		List<DocumentPublicDTO> documentPublicDTOs = documentPublicService.findAllDocByPublic();
		return ApiResponse.success("尋找 民眾 旗下附件 成功", documentPublicDTOs);
	}
	
	@PostMapping(value = {"/case/find", "/case/find/"})
	public ApiResponse<List<DocumentPublicDTO>> findAllDocByCase(@RequestBody DocumentPublicFindDTO documentPublicFindDTO) {
		List<DocumentPublicDTO> documentPublicDTOs = documentPublicService.findAllDocByCase(documentPublicFindDTO);
		return ApiResponse.success("尋找 案件 旗下附件 成功", documentPublicDTOs);
	}
	
	@PostMapping(value = {"/public/create", "/public/create/"}, consumes = "multipart/form-data")
	public ApiResponse<DocumentPublicDTO> createDocumentByPublic(
			@RequestPart("file") MultipartFile file,
			@RequestPart("documentPublicCreateDTO") DocumentPublicCreateDTO documentPublicCreateDTO
			) {
		DocumentPublicDTO documentPublicDTO = documentPublicService.createDocumentByPublic(documentPublicCreateDTO, file);
		return ApiResponse.success("民眾附件 上傳成功", documentPublicDTO);
	}
	
	@PostMapping(value = {"/case/create", "/case/create/"}, consumes = "multipart/form-data")
	public ApiResponse<DocumentPublicDTO> createDocumentByCase(
			@RequestPart("file") MultipartFile file,
			@RequestPart("documentPublicCreateDTO") DocumentPublicCreateDTO documentPublicCreateDTO
			) {
		DocumentPublicDTO documentPublicDTO = documentPublicService.createDocumentByCase(documentPublicCreateDTO, file);
		return ApiResponse.success("案件用附件 上傳成功", documentPublicDTO);
	}
	
	@PostMapping(value = {"/public/link/case", "/public/link/case/"})
	public ApiResponse<DocumentPublicDTO> documentLinkCase(@RequestBody DocumentPublicLinkDTO documentPublicLinkDTO) {
		DocumentPublicDTO documentPublicDTO = documentPublicService.documentLinkCase(documentPublicLinkDTO);
		return ApiResponse.success("案件 連接附件 成功", documentPublicDTO);
	}
	
	@PostMapping(value = {"/admin/verify", "/admin/verify/"})
	public ApiResponse<DocumentPublicDTO> verifyDocument(@RequestBody DocumentPublicVerifyDTO documentPublicVerifyDTO) {
		DocumentPublicDTO documentPublicDTO = documentPublicService.verifyDocument(documentPublicVerifyDTO);
		return ApiResponse.success("附件審核 成功", documentPublicDTO);
	}
	
	@DeleteMapping(value = {"/public/delete", "/public/delete/"})
	public ApiResponse<Void> deleteDocument(@RequestBody DocumentPublicDeleteDTO documentPublicDeleteDTO) {
		documentPublicService.deleteDocument(documentPublicDeleteDTO);
		return ApiResponse.success("刪除附件 成功", null);
	}
	
}
