package com.github.lianick.model.dto.cases;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseCompleteDTO {

	private List<Long> ids;					// 案件 ID們
}
