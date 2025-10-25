package com.github.lianick.model.dto.userPublic;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChildUpdateDTO {

	private Long id;						// 兒童ID
	
	// 基本資料
	private String name;
	private String nationalIdNo;
	private LocalDate birthdate;
	private String gender;
	
	// 2. 更新 所需要的資料
	private String newName;
}
