package com.github.lianick.model.dto.cases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaseErrorDTO {

	private Long id;
	private String errorMessage;
}
