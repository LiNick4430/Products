package com.github.lianick.response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.github.lianick.model.dto.DownloadDTO;

public class DownloadResponse {
	
	public static ResponseEntity<Resource> create(DownloadDTO downloadDTO) {
		
		// 檔案名稱：處理編碼以支援中文檔名
		String encodedFileName = null;
		try {
			encodedFileName = URLEncoder.encode(downloadDTO.getName(), StandardCharsets.UTF_8.toString()).replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			// 如果編碼失敗，回退到一個安全的、非中文的檔名
            encodedFileName = "downloaded_file";
		}
		
		// 構建 Content-Disposition 標頭 (指示瀏覽器強制下載)
        // 格式: attachment; filename="encoded-name.ext"
		String headerValue = "attachment; filename=\"" + encodedFileName + "\"";
		
		// 構建 ResponseEntity
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(downloadDTO.getContentType()))
				.contentLength(downloadDTO.getContentLength())
				.header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
				.body(downloadDTO.getResource());
		
	}
	
}
