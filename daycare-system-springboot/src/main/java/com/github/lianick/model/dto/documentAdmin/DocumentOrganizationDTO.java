package com.github.lianick.model.dto.documentAdmin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentOrganizationDTO {
	
private Long id;		// 附件ID
	
	private String name;	// 附件檔名
	private String type;	// 附件類型	(公告用 / 機構用)
	
	private Long organizationId;	// 使用在 機構
}
