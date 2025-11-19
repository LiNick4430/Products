package com.github.lianick.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
		
		// 檢查 格式 是否 PDF/PNG/JPG
		String contentType = file.getContentType();
		if (!List.of("image/jpeg", "image/png", "application/pdf").contains(contentType)) {
			throw new FileStorageException("檔案錯誤：上傳檔案格式錯誤");
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
		
		// 清理檔名: 提取最單純的檔名部分，移除任何惡意的路徑遍歷資訊 (../../) 
		// 範例 惡意檔名: ../../../../windows/system32/cmd.exe -> cmd.exe
		// 範例 正常檔名: 公告文件.pdf -> 公告文件.pdf
		String safeFileName = Paths.get(orignalFileName).getFileName().toString();
		
		// 組合最終檔名
		String storedFileName = UUID.randomUUID().toString() + "_" + safeFileName;
		
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
			throw new FileStorageException("檔案錯誤：儲存失敗，請檢查伺服器路徑權限。", e);
			// TODO 檔案錯誤：儲存失敗，請檢查伺服器路徑權限或者儲存空間。
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
		
		// 1. 路徑的 安全處理
		
		// 取得檔案儲存的根目錄，並標準化為絕對路徑 
		// 範例 file.upload-path = uploads/ 			-> /data/uploads/
		// 範例 file.upload-path = /data/uploads/ 	-> /data/uploads/
	    Path uploadRootPath = Paths.get(fileProperties.getUploadPath()).toAbsolutePath().normalize();
		
		// 將字串路徑轉換為 Path 物件
		Path targetLocation = Paths.get(pathString);
		
		// 標準化目標路徑
	    Path fullTargetPath = targetLocation.toAbsolutePath().normalize();
	    
	    // 檢查 fullTargetPath 是否以 uploadRootPath 開頭
	    if (!fullTargetPath.startsWith(uploadRootPath)) {
	        throw new FileStorageException("檔案錯誤：檔案不存在");
	        // TODO LOG 檔案錯誤：不允許存取指定儲存根目錄外的檔案。
	    }
		
	    // 2. 讀取檔案 並放入 Resource
		try {
			// 建立 UrlResource 實例：必須使用 Path.toUri() 確保路徑格式正確
	        // Path.toUri() 會將本地路徑轉換為 file:/// 格式，UrlResource 才能讀取
			Resource resource = new UrlResource(fullTargetPath.toUri());
			
			// 檢查 Resource 是否真的存在且可讀取
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
		
		// 路徑的 安全處理
		
		// 取得檔案儲存的根目錄，並標準化為絕對路徑 
		// 範例 file.upload-path = uploads/ 			-> /data/uploads/
		// 範例 file.upload-path = /data/uploads/ 	-> /data/uploads/
	    Path uploadRootPath = Paths.get(fileProperties.getUploadPath()).toAbsolutePath().normalize();
		
		// 將字串路徑轉換為 Path 物件
		Path targetLocation = Paths.get(pathString);
		
		// 標準化目標路徑
	    Path fullTargetPath = targetLocation.toAbsolutePath().normalize();
	    
	    // 檢查 fullTargetPath 是否以 uploadRootPath 開頭
	    if (!fullTargetPath.startsWith(uploadRootPath)) {
	        throw new FileStorageException("檔案錯誤：檔案不存在");
	        // TODO LOG 檔案錯誤：不允許存取指定儲存根目錄外的檔案。
	    }
		
		try {
			
			Boolean isDeleted = Files.deleteIfExists(fullTargetPath);
			
			if (!isDeleted) {
				//TODO  刪除不存在物件 的 LOG
			}
			
		} catch (IOException e) {
			throw new FileStorageException("檔案錯誤：刪除失敗", e);
		}
	}
	
}
