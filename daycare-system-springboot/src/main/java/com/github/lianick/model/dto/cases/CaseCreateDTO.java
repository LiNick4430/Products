package com.github.lianick.model.dto.cases;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaseCreateDTO {
	
	private String applicationMethod;		// 申請方式
	private Long childId;					// 申請幼兒ID
	
	private Long organizationIdFirst;		// 第一志願ID
	private Long organizationIdSecond;		// 第二志願ID
	
	private List<Long> priorityIds;			// 對應的 優先條件們
}
