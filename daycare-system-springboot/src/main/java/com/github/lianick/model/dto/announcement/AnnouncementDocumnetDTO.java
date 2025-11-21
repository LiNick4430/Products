package com.github.lianick.model.dto.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementDocumnetDTO {

	private Long id;						// 公告 ID
	
	private Long documentId;				// 附件 ID
}
