package com.github.lianick.model.dto.cases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseWithdrawnAdminDTO {

	private Long id;					// 案件 ID
	
	private Boolean isPassed;			// 這個是否通過
}
