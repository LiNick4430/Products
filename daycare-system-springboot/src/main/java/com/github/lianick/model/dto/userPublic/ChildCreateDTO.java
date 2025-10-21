package com.github.lianick.model.dto.userPublic;

import java.time.LocalDate;

public class ChildCreateDTO {

	private Long id;						// 兒童ID
	private String username;				// 帳號名
	
	// 基本資料
	private String name;
	private String nationalIdNo;
	private LocalDate birthdate;
	private String gender;
	
}
