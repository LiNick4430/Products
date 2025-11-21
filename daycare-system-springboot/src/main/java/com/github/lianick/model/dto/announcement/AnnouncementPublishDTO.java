package com.github.lianick.model.dto.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementPublishDTO {
	private Long id;						// 公告 ID
	private Integer daysUntilExpiry;		// 幾天後 過期
}
