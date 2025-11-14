package com.github.lianick.model.dto.documentPublic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPublicFindDTO {
	private Long userId;		// 使用者ID
	private Long caseId;		// 案件ID
}
