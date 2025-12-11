package com.github.lianick.model.dto.child;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChildFindDTO {

	private Long id;						// 兒童ID
	
	private Long publicId;					// 民眾ID
	
}
