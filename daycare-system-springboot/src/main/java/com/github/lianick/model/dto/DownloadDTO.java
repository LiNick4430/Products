package com.github.lianick.model.dto;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用於 下載回傳 的 DTO
 * */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DownloadDTO {

	private Resource resource;		// 下載 的 資源
	private String name;			// 下載 的 名稱
	private String contentType;		// 下載 的 格式
	private Long contentLength;		// 下載 的 長度
	
}
