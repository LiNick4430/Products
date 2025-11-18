package com.github.lianick.model.dto.announcement;

import java.time.LocalDate;
import java.util.List;

import com.github.lianick.model.dto.documentAdmin.DocumentAnnouncementDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementDTO {

	private Long id;						// 公告 ID
	
	private String title;					// 公告 標題
	private String content;					// 公告 內容
	private LocalDate publishDate;			// 公告 實際發布時間
	private Boolean isPublished = false;	// 預設為未發布 (草稿)
	private LocalDate expiryDate;			// 公告 到期日期 (可為 NULL)
	
	private Long organizationId;			// 機構 ID
	private String organizationName;		// 機構 名稱
	
	private List<DocumentAnnouncementDTO> documentAnnouncementDTOs;	// 對應的公告文件
}
