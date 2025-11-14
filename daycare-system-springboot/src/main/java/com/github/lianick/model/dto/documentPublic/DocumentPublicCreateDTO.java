package com.github.lianick.model.dto.documentPublic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPublicCreateDTO {

	private Long id;		// 附件ID
	
	private String name;	// 附件檔名
	private String path;	// 附件路徑
	private String type;	// 附件類型	(戶籍證明/ 證件照片)
	
	private Long userId;	// 帳號 ID
	private Long caseId;	// 案件 ID
}
