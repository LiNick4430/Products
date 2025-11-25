package com.github.lianick.model.dto.documentPublic;

import com.github.lianick.model.enums.document.DocumentType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPublicCreateDTO {

	private DocumentType docType;	// 附件類型	(戶籍證明/ 證件照片)
	
	private Long caseId;	// 案件 ID
}
