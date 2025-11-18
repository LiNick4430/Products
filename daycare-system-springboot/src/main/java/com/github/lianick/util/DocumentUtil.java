package com.github.lianick.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.github.lianick.config.FileProperties;
import com.github.lianick.exception.FileStorageException;
import com.github.lianick.model.dto.DocumentDTO;

/**
 * 負責處 理附件相關 事宜
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
		} catch (IOException e) {
			throw new FileStorageException("檔案錯誤：儲存失敗，請檢查伺服器路徑權限或磁碟空間。", e);
		}
		
		DocumentDTO document = new DocumentDTO();
		document.setOrignalFileName(orignalFileName);
		document.setTargetLocation(targetLocation.toString());
		
		return document;
	}
}
