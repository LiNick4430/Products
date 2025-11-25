package com.github.lianick.model.dto.cases;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaseDTO {

	private Long id;					// 案件 ID
	
	private String caseNumber;				// 案件編號
	private LocalDateTime applicationDate;	// 申請時間
	private String applicationMethod;		// 申請方式
	private String childName;				// 申請幼兒姓名
	
	private String organizationNameFirst;		// 第一志願
	private String organizationNameFirstStatus;	// 第一志願狀態
	
	private String organizationNameSecond;		// 第二志願
	private String organizationNameSecondStatus;// 第二志願狀態
	
	private String status;				// 申請狀態
	
	private String className;			// 申請成功 的 班級名稱
}
