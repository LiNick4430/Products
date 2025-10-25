package com.github.lianick.model.dto.userPublic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChildCreateDTO {

	private Long id;						// 兒童ID
	
	// 基本資料
	private String name;
	private String nationalIdNo;
	private String birthdate;
	private String gender;
	
}
