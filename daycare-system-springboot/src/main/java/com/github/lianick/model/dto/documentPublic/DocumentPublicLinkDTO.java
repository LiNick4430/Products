package com.github.lianick.model.dto.documentPublic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPublicLinkDTO {

	private Long id;		// 附件ID
	
	private Long userId;	// 帳號 ID
	private Long caseId;	// 案件 ID
	
	private Boolean isVerified;				// 附件 是否 聽過認證
}
