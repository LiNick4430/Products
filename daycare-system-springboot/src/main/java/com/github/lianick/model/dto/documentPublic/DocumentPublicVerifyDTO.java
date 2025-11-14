package com.github.lianick.model.dto.documentPublic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPublicVerifyDTO {

	private Long id;		// 附件 ID
	
	private Long userId;	// 員工 帳號 ID
	
	private Boolean isVerified;				// 附件 是否 聽過認證
}
