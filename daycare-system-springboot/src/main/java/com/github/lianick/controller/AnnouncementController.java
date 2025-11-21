package com.github.lianick.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.GetExchange;

import com.github.lianick.model.dto.announcement.AnnouncementCreateDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDocumnetDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.service.AnnouncementService;
import com.github.lianick.util.JsonUtil;

@RestController
@RequestMapping("/announcement")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AnnouncementController {

	@Autowired
	private AnnouncementService announcementService;
	
	@Autowired
	private JsonUtil jsonUtil;
	
	@GetExchange("/test3")
	public ApiResponse<List<AnnouncementDTO>> findAllNotPublish() {
		List<AnnouncementDTO> announcementDTOs = announcementService.findAllAnnouncementNotPublish();
		return ApiResponse.success("找尋 公告 成功", announcementDTOs);
	}
	
	@PostMapping("/test")
	public ApiResponse<AnnouncementDTO> create(@RequestBody AnnouncementCreateDTO announcementCreateDTO) {
		AnnouncementDTO announcementDTO = announcementService.createAnnouncement(announcementCreateDTO);
		return ApiResponse.success("公告建立成功", announcementDTO);
	}
	
	@PostMapping(value = {"/test2"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<AnnouncementDTO> upload(
			@RequestPart("file") MultipartFile file,
			@RequestPart("data") String json ) {
		
		AnnouncementDocumnetDTO announcementDocumnetDTO = jsonUtil.jsonStringToDTO(json, AnnouncementDocumnetDTO.class);
		
		AnnouncementDTO announcementDTO = announcementService.uploadAnnouncementDocument(announcementDocumnetDTO, file);
		
		return ApiResponse.success("公告附件上傳成功", announcementDTO);
	}
}
