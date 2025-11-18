package com.github.lianick.model.dto.documentPublic;

import java.time.LocalDateTime;
import java.util.List;

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
	private String type;	// 附件類型	(戶籍證明/ 證件照片)
	
	private Long userId;	// 對應的 民眾 ID
	
	private Boolean isVerified;				// 附件 是否 聽過認證
	private LocalDateTime verificationDate;	// 附件 認證時間
	
	private List<String> caseNumbers;	// 使用 此附件 的 案件
}
