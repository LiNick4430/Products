package com.github.lianick.model.dto.documentPublic;

import java.time.LocalDateTime;

import com.github.lianick.model.dto.CaseDTO;
import com.github.lianick.model.dto.userPublic.UserPublicDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentPublicDTO {

	private Long id;		// 附件ID
	
	private String name;	// 附件檔名
	private String path;	// 附件路徑
	private String type;	// 附件類型	(戶籍證明/ 證件照片)
	
	private UserPublicDTO userPublicDTO;	// 對應的 民眾
	private CaseDTO caseDTO;				// 被使用的 案件 們
	
	private Boolean isVerified;				// 附件 是否 聽過認證
	private LocalDateTime verificationDate;	// 附件 認證時間
}
