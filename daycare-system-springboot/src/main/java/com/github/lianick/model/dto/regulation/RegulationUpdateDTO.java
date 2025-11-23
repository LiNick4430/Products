package com.github.lianick.model.dto.regulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegulationUpdateDTO {

	private Long id;			// 規範 ID
	
	private String newContent;		// 規範 內容
}
