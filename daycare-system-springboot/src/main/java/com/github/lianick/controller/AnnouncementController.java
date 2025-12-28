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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**AnnouncementController
 * Request Mapping: "/announcement"
 * GET		"/find/all", "/find/all/"				搜尋 全部公告			"/announcement/find/all/"			PUBLIC
 * GET		"/find/all/no/expiry", "/find/all/no/expiry/"	搜尋 全部公告(未過期包含未發布)	"/announcement/find/all/no/expiry/"	AUTHENTICATED
 * POST 	"/find", "/find/"						搜尋 特定公告			"/announcement/find/"				PUBLIC
 * POST 	"/find/no/expiry", "/find/no/expiry/"	搜尋 特定公告(未過期包含未發布)	"/announcement/find/no/expiry/"	AUTHENTICATED
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

@Tag(
		name = "Announcement",
		description = "公告相關的API"
		)
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

	@Autowired
	private AnnouncementService announcementService;
	
	@Autowired
	private JsonUtil jsonUtil;
	
	@Operation(
			summary = "搜尋全部公告",
			description = "搜尋 全部 公告(已發布)"
			)
	@GetMapping(value = {"/find/all", "/find/all/"})
	public ApiResponse<List<AnnouncementDTO>> findAll() {
		List<AnnouncementDTO> announcementDTOs = announcementService.findAllActiveAnnouncement();
		return ApiResponse.success("搜尋 全部公告 成功", announcementDTOs);
	}
	
	@Operation(
			summary = "搜尋全部公告",
			description = """
					搜尋 全部 公告(包含 已發布, 未發布)
					- 主管 搜尋 全部的機構 的 公告
					- 員工 搜尋 自己的機構 的 公告
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping(value = {"/find/all/no/expiry", "/find/all/no/expiry/"})
	public ApiResponse<List<AnnouncementDTO>> findAllNoExpiry() {
		List<AnnouncementDTO> announcementDTOs = announcementService.findAllNoExpiryAnnouncement();
		return ApiResponse.success("搜尋 全部公告 成功", announcementDTOs);
	}
	
	@Operation(
			summary = "搜尋特定公告",
			description = "依照公告ID 搜尋 特定公告(已發布)"
			)
	@PostMapping(value = {"/find", "/find/"})
	public ApiResponse<AnnouncementDTO> findById(@RequestBody AnnouncementFindDTO announcementFindDTO) {
		AnnouncementDTO announcementDTO = announcementService.findActiveAnnouncementById(announcementFindDTO);
		return ApiResponse.success("搜尋 特定公告 成功", announcementDTO);
	}
	
	@Operation(
			summary = "搜尋特定公告",
			description = """
					依照公告ID 搜尋 特定公告(包含 已發布, 未發布)
					- 主管 搜尋 全部的機構 的 公告
					- 員工 搜尋 自己的機構 的 公告
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/find/no/expiry", "/find/no/expiry/"})
	public ApiResponse<AnnouncementDTO> findNoExpiryById(@RequestBody AnnouncementFindDTO announcementFindDTO) {
		AnnouncementDTO announcementDTO = announcementService.findNoExpiryAnnouncementById(announcementFindDTO);
		return ApiResponse.success("搜尋 特定公告 成功", announcementDTO);
	}
	
	@Operation(
			summary = "下載特定公告附件",
			description = """
					下載特定的公告附件, 依照 公告ID 附件ID
					"""
			)
	@PostMapping(value = {"/download/doc", "/download/doc/"})
	public ResponseEntity<Resource> download(@RequestBody AnnouncementDocumnetDTO announcementDocumnetDTO) {
		DownloadDTO downloadDTO = announcementService.downloadDoc(announcementDocumnetDTO);
		return DownloadResponse.create(downloadDTO);
	}
	
	@Operation(
			summary = "建立公告",
			description = """
					建立新的公告
					- 主管 建立 全部的機構 的 公告
					- 員工 建立 自己的機構 的 公告
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/create", "/create/"})
	public ApiResponse<AnnouncementDTO> create(@RequestBody AnnouncementCreateDTO announcementCreateDTO) {
		AnnouncementDTO announcementDTO = announcementService.createAnnouncement(announcementCreateDTO);
		return ApiResponse.success("建立公告 成功", announcementDTO);
	}
	
	@Operation(
			summary = "更新公告",
			description = """
					更新公告
					- 主管 更新 全部的機構 的 公告
					- 員工 更新 自己的機構 的 公告
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/update", "/update/"})
	public ApiResponse<AnnouncementDTO> update(@RequestBody AnnouncementUpdateDTO announcementUpdateDTO) {
		AnnouncementDTO announcementDTO = announcementService.updateAnnouncement(announcementUpdateDTO);
		return ApiResponse.success("更新公告 成功", announcementDTO);
	}
	
	@Operation(
			summary = "上傳特定公告附件",
			description = """
					上傳特定公告 的附件
					- 主管 上傳 全部的機構 的 公告附件
					- 員工 上傳 自己的機構 的 公告附件
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/upload/doc", "/upload/doc/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<AnnouncementDTO> upload(
			@RequestPart("file") MultipartFile file,
			@RequestPart("data") String json ) {
		
		AnnouncementDocumnetDTO announcementDocumnetDTO = jsonUtil.jsonStringToDTO(json, AnnouncementDocumnetDTO.class);
		
		AnnouncementDTO announcementDTO = announcementService.uploadAnnouncementDocument(announcementDocumnetDTO, file);
		
		return ApiResponse.success("上傳 公告附件 成功", announcementDTO);
	}
	
	@Operation(
			summary = "刪除特定公告附件",
			description = """
					刪除特定公告 的附件
					- 主管 刪除 全部的機構 的 公告附件
					- 員工 刪除 自己的機構 的 公告附件
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping(value = {"/delete/doc", "/delete/doc/"})
	public ApiResponse<Void> deleteDocument(@RequestBody AnnouncementDocumnetDTO announcementDocumnetDTO) {
		announcementService.deleteAnnouncementDocument(announcementDocumnetDTO);
		return ApiResponse.success("刪除 公告附件 成功", null);
	}
	
	@Operation(
			summary = "刪除特定公告",
			description = """
					刪除特定公告 
					- 已發布 且 尚未過期的 無法刪除
					- 主管 刪除 全部的機構 的 公告
					- 員工 刪除 自己的機構 的 公告
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping(value = {"/delete", "/delete/"})
	public ApiResponse<Void> delete(@RequestBody AnnouncementDeleteDTO announcementDeleteDTO) {
		announcementService.deleteAnnouncement(announcementDeleteDTO);
		return ApiResponse.success("刪除 公告 成功", null);
	}
	
	@Operation(
			summary = "搜尋全部公告",
			description = """
					搜尋 全部 公告(未發布)
					- 主管 搜尋 全部的機構 的 公告
					- 員工 搜尋 自己的機構 的 公告
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping(value = {"/find/not/publish", "/find/not/publish/"})
	public ApiResponse<List<AnnouncementDTO>> findAllNotPublish() {
		List<AnnouncementDTO> announcementDTOs = announcementService.findAllAnnouncementNotPublish();
		return ApiResponse.success("搜尋 尚未發布公告 成功", announcementDTOs);
	}
	
	@Operation(
			summary = "發布公告",
			description = """
					將 未發布的公告 發布出去
					- daysUntilExpiry 幾天後過期
					- 主管 搜尋 全部的機構 的 公告
					- 員工 搜尋 自己的機構 的 公告
					- 權限限制：ROLE_ADMIN, ROLE_STAFF 
					"""
			)
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping(value = {"/publish", "/publish/"})
	public ApiResponse<AnnouncementDTO> publish(@RequestBody AnnouncementPublishDTO announcementPublishDTO) {
		AnnouncementDTO announcementDTO = announcementService.publishAnnouncement(announcementPublishDTO);
		return ApiResponse.success("發布 公告 成功", announcementDTO);
	}
}
