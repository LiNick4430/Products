package com.github.lianick.model.dto.documentPublic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPublicDeleteDTO {

	private Long id;		// 附件ID
	
	private Long userId;		// 帳號 ID
	private String username;	// 帳號
	
}
