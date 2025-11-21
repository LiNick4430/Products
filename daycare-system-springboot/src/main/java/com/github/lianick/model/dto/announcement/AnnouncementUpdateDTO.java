package com.github.lianick.model.dto.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementUpdateDTO {
	private Long id;						// 公告 ID
	
	private String newTitle;				// 公告 新標題
	private String newContent;				// 公告 新內容
	
	private Long organizationId;			// 機構 ID
}
