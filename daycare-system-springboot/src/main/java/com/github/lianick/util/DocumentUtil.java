package com.github.lianick.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.config.FileProperties;
import com.github.lianick.exception.FileStorageException;
import com.github.lianick.model.dto.DocumentDTO;

/**
 * 負責處理附件相關 事宜
 * */

@Component
public class DocumentUtil {
	
	@Autowired
	private FileProperties fileProperties;

	/**
	 * 附件上傳
	 * */
	public DocumentDTO upload(Long id, MultipartFile file, Boolean isAdmin) {
		
		// 檢查檔案是否存在
		if (file.isEmpty()) {
			throw new FileStorageException("檔案錯誤：上傳檔案不存在");
		}
		
		String idString = String.valueOf(id);
		String folderString = isAdmin ? "admin" : "public";
		
		// 檔案儲存
		// 組合路徑 (基本 + 帳號ID)
		Path uploadPath = Paths.get(
				fileProperties.getUploadPath(),
				folderString,
				idString
				);
		Path targetLocation = null;
		String absolutePathToStore = null;
		
		// 建立 唯一的 檔案名稱
		String orignalFileName = file.getOriginalFilename();
		String storedFileName = UUID.randomUUID().toString() + "_" + orignalFileName;
		
		// I/O 區塊
		try {
			// 確認 目標路徑存在(如果不存在，則會自動創建)
			Files.createDirectories(uploadPath);
			
			// 組合最終目標
			targetLocation = uploadPath.resolve(storedFileName);
			
			// 儲存檔案
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			
			// 將相對路徑轉換為完整的絕對路徑並標準化 (normalize)
			absolutePathToStore = targetLocation.toAbsolutePath().normalize().toString();
			
		} catch (IOException e) {
			throw new FileStorageException("檔案錯誤：儲存失敗，請檢查伺服器路徑權限或磁碟空間。", e);
		}
		
		DocumentDTO document = new DocumentDTO();
		document.setOrignalFileName(orignalFileName);
		document.setTargetLocation(absolutePathToStore);
		
		return document;
	}
	
	/**
	 * 附件下載
	 * */
	public Resource download(String pathString) {
		// 1. 將字串路徑轉換為 Path 物件
		Path targetLocation = Paths.get(pathString);
		
		try {
			// 2. 建立 UrlResource 實例：必須使用 Path.toUri() 確保路徑格式正確
	        // Path.toUri() 會將本地路徑轉換為 file:/// 格式，UrlResource 才能讀取
			Resource resource = new UrlResource(targetLocation.toUri());
			
			// 3. 檢查 Resource 是否真的存在且可讀取
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				// 檔案不存在或無法讀取時，拋出業務異常
	            throw new FileStorageException("檔案錯誤：找不到檔案或無法讀取。");
			}
			
		} catch (IOException e) {	// 捕獲所有與路徑格式和檔案讀取相關的錯誤
			String errorMessage;

			// 使用 instanceof 來區分錯誤類型，提供更精確的錯誤訊息
			if (e instanceof MalformedURLException) {
				// 路徑格式無效
				errorMessage = "檔案錯誤：儲存路徑格式無效。";
			} else {
				// 其他 I/O 錯誤，例如權限不足
				errorMessage = "檔案錯誤：讀取檔案失敗。";
			}
			throw new FileStorageException(errorMessage, e);
		} 
	}
	
	/**
	 * 附件刪除
	 * */
	public void delete(String pathString) {
		
		Path targetLocation = Paths.get(pathString);
		
		try {
			
			Boolean isDeleted = Files.deleteIfExists(targetLocation);
			
			if (!isDeleted) {
				//TODO  刪除不存在物件 的 LOG
			}
			
		} catch (IOException e) {
			throw new FileStorageException("檔案錯誤：刪除失敗", e);
		}
	}
	
}
