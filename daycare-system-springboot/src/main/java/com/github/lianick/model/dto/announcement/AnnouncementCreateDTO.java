package com.github.lianick.model.dto.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementCreateDTO {
	private String title;					// 公告 標題
	private String content;					// 公告 內容
	
	private Long organizationId;			// 機構 ID
	
}
