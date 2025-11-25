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
import com.github.lianick.model.dto.announcement.AnnouncementCreateDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDeleteDTO;
import com.github.lianick.model.dto.announcement.AnnouncementDocumnetDTO;
import com.github.lianick.model.dto.announcement.AnnouncementFindDTO;
import com.github.lianick.model.dto.announcement.AnnouncementPublishDTO;
import com.github.lianick.model.dto.announcement.AnnouncementUpdateDTO;
import com.github.lianick.response.ApiResponse;
import com.github.lianick.response.DownloadResponse;
import com.github.lianick.service.AnnouncementService;
import com.github.lianick.util.JsonUtil;

/**AnnouncementController
 * Request Mapping: "/announcement"
 * GET		"/find/all", "/find/all/"				搜尋 全部公告			"/announcement/find/all/"			PUBLIC
 * POST 	"/find", "/find/"						搜尋 特定公告			"/announcement/find/"				PUBLIC
 * POST 	"/download/doc", "/download/doc/"		下載 公告附件			"/announcement/download/doc/"		PUBLIC
 * POST		"/create", "/create/"					建立 公告				"/announcement/create/"				AUTHENTICATED
 * POST		"/update", "/update/"					更新 公告				"/announcement/update/"				AUTHENTICATED
 * POST		"/upload/doc", "/upload/doc/"			上傳 公告附件			"/announcement/upload/doc/"			AUTHENTICATED
 * DELETE	"/delete/doc", "/delete/doc/"			刪除 公告附件			"/announcement/delete/doc/"			AUTHENTICATED
 * DELETE	"/delete", "/delete/"					刪除 公告				"/announcement/delete/"				AUTHENTICATED
 * GET		"/find/not/publish", "/find/not/publish/"	搜尋 尚未發布公告		"/announcement/find/not/publish/"	AUTHENTICATED
 * POST		"/publish", "/publish/"					發布 公告				"/announcement/publish/"			AUTHENTICATED
 * 
 * */

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

	@Autowired
	private AnnouncementService announcementService;
	
	@Autowired
	private JsonUtil jsonUtil;
	
	@GetMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<AnnouncementDTO>> findAll() {
		List<AnnouncementDTO> announcementDTOs = announcementService.findAllAnnouncement();
		return ApiResponse.success("搜尋 全部公告 成功", announcementDTOs);
	}
	
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<AnnouncementDTO> findById(@RequestBody AnnouncementFindDTO announcementFindDTO) {
		AnnouncementDTO announcementDTO = announcementService.findAnnouncementById(announcementFindDTO);
		return ApiResponse.success("搜尋 特定公告 成功", announcementDTO);
	}
	
	@PostMapping(value = {"/download/doc", "/download/doc/"})
	public ResponseEntity<Resource> download(@RequestBody AnnouncementDocumnetDTO announcementDocumnetDTO) {
		DownloadDTO downloadDTO = announcementService.downloadDoc(announcementDocumnetDTO);
		return DownloadResponse.create(downloadDTO);
	}
	
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<AnnouncementDTO> create(@RequestBody AnnouncementCreateDTO announcementCreateDTO) {
		AnnouncementDTO announcementDTO = announcementService.createAnnouncement(announcementCreateDTO);
		return ApiResponse.success("建立公告 成功", announcementDTO);
	}
	
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<AnnouncementDTO> update(@RequestBody AnnouncementUpdateDTO announcementUpdateDTO) {
		AnnouncementDTO announcementDTO = announcementService.updateAnnouncement(announcementUpdateDTO);
		return ApiResponse.success("更新公告 成功", announcementDTO);
	}
	
	@PostMapping(value = {"/upload/doc", "/upload/doc/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<AnnouncementDTO> upload(
			@RequestPart("file") MultipartFile file,
			@RequestPart("data") String json ) {
		
		AnnouncementDocumnetDTO announcementDocumnetDTO = jsonUtil.jsonStringToDTO(json, AnnouncementDocumnetDTO.class);
		
		AnnouncementDTO announcementDTO = announcementService.uploadAnnouncementDocument(announcementDocumnetDTO, file);
		
		return ApiResponse.success("上傳 公告附件 成功", announcementDTO);
	}
	
	@DeleteMapping(value = {"/delete/doc", "/delete/doc/"})
	public ApiResponse<Void> deleteDocument(@RequestBody AnnouncementDocumnetDTO announcementDocumnetDTO) {
		announcementService.deleteAnnouncementDocument(announcementDocumnetDTO);
		return ApiResponse.success("刪除 公告附件 成功", null);
	}
	
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> delete(@RequestBody AnnouncementDeleteDTO announcementDeleteDTO) {
		announcementService.deleteAnnouncement(announcementDeleteDTO);
		return ApiResponse.success("刪除 公告 成功", null);
	}
	
	@GetMapping(value = {"/find/not/publish", "/find/not/publish/"})
	public ApiResponse<List<AnnouncementDTO>> findAllNotPublish() {
		List<AnnouncementDTO> announcementDTOs = announcementService.findAllAnnouncementNotPublish();
		return ApiResponse.success("搜尋 尚未發布公告 成功", announcementDTOs);
	}
	
	@PostMapping(value = {"/publish", "/publish/"})
	public ApiResponse<AnnouncementDTO> publish(@RequestBody AnnouncementPublishDTO announcementPublishDTO) {
		AnnouncementDTO announcementDTO = announcementService.publishAnnouncement(announcementPublishDTO);
		return ApiResponse.success("發布 公告 成功", announcementDTO);
	}
}
