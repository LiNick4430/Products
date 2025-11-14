package com.github.lianick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.model.dto.documentPublic.DocumentPublicCreateDTO;
import com.github.lianick.model.dto.documentPublic.DocumentPublicDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.DocumentPublicService;

@RestController
@RequestMapping("/document")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DocumentPublicController {
	
	@Autowired
	private DocumentPublicService documentPublicService;

	// 方法範例
	@PostMapping(value = {"/upload/public", "/upload/public/"}, consumes = "multipart/form-data")
	public ApiResponse<DocumentPublicDTO> uploadDocumentToPublic (
	    @RequestPart("file") MultipartFile file,
	    @RequestPart("data") DocumentPublicCreateDTO data // 接收額外的 JSON 數據 (如 docType, publicId)
	) {
	    if (file.isEmpty()) {
	        throw new RuntimeException("上傳檔案不得為空");
	    }
	    
	    // 這裡調用 Service，並將 MultipartFile 傳入
	    DocumentPublicDTO docDTO = documentPublicService.createDocumentByPublic(data, file);
	    return ApiResponse.success("附件上傳至個人文件庫 成功", docDTO);
	}
	
}
