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
public class ChildDTO {

	private Long id;						// 兒童ID
	
	// 基本資料
	private String name;
	private String nationalIdNo;
	private LocalDate birthdate;
	private String gender;
	
}
