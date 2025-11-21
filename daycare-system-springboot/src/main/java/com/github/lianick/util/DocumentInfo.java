package com.github.lianick.util;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 負責處理 DocumentUtil 的 基本資料
 * */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentInfo {
	private Resource resource;	// 附件資源
	private String contentType;	// 附件格式
	private Long contentLength;	// 附件長度
}
