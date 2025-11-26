package com.github.lianick.model.dto.child;

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
	
	// 2. 更新 所需要的資料
	private String newName;
}
